package graphene.rest.ws.impl;

import graphene.dao.FederatedEventGraphServer;
import graphene.dao.LoggingDAO;
import graphene.rest.ws.CSGraphServerRS;
import graphene.services.EventGraphBuilder;
import graphene.services.HyperGraphBuilder;
import graphene.util.ExceptionUtil;
import graphene.util.FastNumberUtils;
import graphene.util.StringUtils;
import graphene.util.validator.ValidationUtils;
import mil.darpa.vande.converters.cytoscapejs.V_CSGraph;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.interactions.TemporalGraphQuery;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.slf4j.Logger;

public class CSGraphServerRSImpl implements CSGraphServerRS {

	@Inject
	private FederatedEventGraphServer feg;

	@Inject
	private Logger logger;

	@InjectService("HyperProperty")
	private HyperGraphBuilder propertyGraphBuilder;

	public CSGraphServerRSImpl() {

	}
	@Inject
	protected LoggingDAO loggingDao;

	@Override
	public V_CSGraph getEvents(String objectType, String[] value,
			String valueType, String degree, String maxNodes,
			String maxEdgesPerNode, boolean showIcons, String minSecs,
			String maxSecs, String minimumWeight) {
		logger.debug("-------");
		logger.debug("get Interaction Graph for type " + objectType);
		logger.debug("Value     " + value);
		logger.debug("valueType     " + valueType);
		logger.debug("Degrees   " + degree);
		logger.debug("Max Nodes " + maxNodes);
		logger.debug("Max Edges per node" + maxEdgesPerNode);
		logger.debug("showIcons " + showIcons);
		logger.debug("minSecs " + minSecs);
		logger.debug("maxSecs " + maxSecs);
		logger.debug("minimumWeight " + minimumWeight);

		int maxdegree = FastNumberUtils.parseIntWithCheck(degree, 3);
		int maxnodes = FastNumberUtils.parseIntWithCheck(maxNodes, 1000);
		int maxedges = FastNumberUtils.parseIntWithCheck(maxEdgesPerNode, 1000);
		int minWeight = FastNumberUtils.parseIntWithCheck(minimumWeight, 0);
		long startDate = FastNumberUtils.parseLongWithCheck(minSecs, 0);
		long endDate = FastNumberUtils.parseLongWithCheck(maxSecs, 0);

		TemporalGraphQuery q = new TemporalGraphQuery();
		q.setStartTime(startDate);
		q.setEndTime(endDate);
		q.setType(valueType); // new, --djue
		q.setMinTransValue(minWeight); // new --djue
		q.setMaxNodes(maxnodes);
		q.setMaxEdgesPerNode(maxedges);
		q.setMaxHops(maxdegree);
		q.addSearchIds(value);
		
		V_CSGraph m = null;
		if (ValidationUtils.isValid(value)) {
			try {
				V_GenericGraph g = null;
				EventGraphBuilder gb = feg
						.getGraphBuilderForDataSource(objectType);
				if (gb != null) {
					logger.debug("Found Graph Builder for " + objectType + ": "
							+ gb.getClass().getName());
					loggingDao.recordQuery(q);
					g = gb.makeGraphResponse(q);
				} else {
					logger.error("Unable to handle graph request for type "
							+ objectType);
				}

				m = new V_CSGraph(g, true);
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		} else {
			m = new V_CSGraph();
			m.setStrStatus("A query was sent without any ids");
			logger.error("A query was sent without any ids");
		}
		return m;

	}

	@Override
	public V_CSGraph getProperties(String type, String[] value,
			String maxDegree, String maxNodes, String maxEdgesPerNode,
			boolean bipartite, boolean leafNodes, boolean showNameNodes,
			boolean showIcons) {
		logger.debug("-------");
		logger.debug("get property graph for type " + type);
		logger.debug("Value     " + StringUtils.toString(value));
		logger.debug("Degrees   " + maxDegree);
		logger.debug("LeafNodes " + leafNodes);
		logger.debug("Max Nodes " + maxNodes);
		logger.debug("Max Edges " + maxEdgesPerNode);
		logger.debug("LeafNodes " + leafNodes);
		logger.debug("Bipartite " + bipartite);
		logger.debug("showNameNodes " + showNameNodes);
		int maxDegreeInt = FastNumberUtils.parseIntWithCheck(maxDegree, 6);
		int maxNodesInt = FastNumberUtils.parseIntWithCheck(maxNodes, 1000);
		int maxEdgesPerNodeInt = FastNumberUtils.parseIntWithCheck(maxEdgesPerNode, 100);

		V_GenericGraph g = null;
		V_CSGraph m = null;
		try {
			V_GraphQuery q = new V_GraphQuery();
			q.addSearchIds(value);
			q.setDirected(false);
			q.setMaxNodes(maxNodesInt);
			q.setMaxEdgesPerNode(maxEdgesPerNodeInt);
			q.setMaxHops(maxDegreeInt);
			loggingDao.recordQuery(q);
			g = propertyGraphBuilder.makeGraphResponse(q);
			m = new V_CSGraph(g, true);
		} catch (Exception e) {
			logger.error(ExceptionUtil.getRootCauseMessage(e));
			e.printStackTrace();
		}

		return m;

	}

	@Override
	public V_CSGraph getTemporalEvents(String objectType,
			String[] ids, String valueType, String maxHops, String maxNodes,
			String maxEdgesPerNode, boolean showIcons, String minSecs,
			String maxSecs, String minLinksPairOverall,
			String minValueAnyInteraction, boolean daily, boolean monthly,
			boolean yearly, boolean directed)

	{
		logger.debug("-------");
		logger.debug("get Interaction Graph for type " + objectType);
		logger.debug("IDs     " + ids);
		logger.debug("Max Hops   " + maxHops);
		logger.debug("Max Nodes " + maxNodes);
		logger.debug("Max Edges " + maxEdgesPerNode);
		logger.debug("min links " + minLinksPairOverall);
		logger.debug("min value " + minValueAnyInteraction);
		logger.debug("daily " + daily);
		logger.debug("monthly " + monthly);
		logger.debug("yearly " + yearly);
		logger.debug("directed " + directed);

		TemporalGraphQuery q = new TemporalGraphQuery();

		q.setMaxHops(FastNumberUtils.parseIntWithCheck(maxHops, 3));
		q.setMaxNodes(FastNumberUtils.parseIntWithCheck(maxNodes, 500));
		q.setMaxEdgesPerNode(FastNumberUtils.parseIntWithCheck(
				maxEdgesPerNode, 50));
		q.setMinLinks(FastNumberUtils
				.parseIntWithCheck(minLinksPairOverall, 2));
		q.setMinTransValue(FastNumberUtils.parseIntWithCheck(
				minValueAnyInteraction, 0));
		q.setMinEdgeValue(FastNumberUtils.parseIntWithCheck(
				minValueAnyInteraction, 0)); // new, djue
		q.setByMonth(monthly);
		q.setByDay(daily);
		q.setByYear(yearly);
		q.setDirected(directed);
		q.setStartTime(FastNumberUtils.parseLongWithCheck(minSecs, 0));
		q.setEndTime(FastNumberUtils.parseLongWithCheck(maxSecs, 0));

		q.addSearchIds(ids);

		logger.debug(q.toString());

		// egb.setOriginalQuery(gq);

		V_CSGraph m = null;
		if (ValidationUtils.isValid(ids)) {
			try {
				// V_GenericGraph g = eventGraphBuilder.makeGraphResponse(gq);
				V_GenericGraph g = null;
				EventGraphBuilder gb = feg
						.getGraphBuilderForDataSource(objectType);
				if (gb != null) {
					logger.debug("Found Graph Builder for " + objectType + ": "
							+ gb.getClass().getName());
					g = gb.makeGraphResponse(q);
				} else {
					logger.error("Unable to handle graph request for type "
							+ objectType);
				}
				m = new V_CSGraph(g, true);
				logger.debug("Made graph with " + g.getNodes().size()
						+ " Nodes and " + g.getEdges().size() + " Edges");
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}
		} else {
			m = new V_CSGraph();
			m.setStrStatus("A query was sent without any ids");
			logger.error("A query was sent without any ids");
		}

		return m;

	}

}
