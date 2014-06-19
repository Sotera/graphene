package graphene.rest.ws.impl;

import graphene.rest.ws.CSGraphServerRS;
import graphene.services.EventGraphBuilder;
import graphene.services.PropertyGraphBuilder;
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

	@InjectService("Property")
	private PropertyGraphBuilder propertyGraphBuilder;

	@InjectService("Event")
	private EventGraphBuilder eventGraphBuilder;

	@Inject
	private Logger logger;

	public CSGraphServerRSImpl() {

	}

	@Override
	public V_CSGraph getPropertyGraph(String type, String[] value,
			String degree, String maxNodes, String maxEdgesPerNode,
			boolean bipartite, boolean leafNodes, boolean showNameNodes,
			boolean showIcons) {
		logger.debug("-------");
		logger.debug("get property graph for type " + type);
		logger.debug("Value     " + StringUtils.toString(value));
		logger.debug("Degrees   " + degree);
		logger.debug("LeafNodes " + leafNodes);
		logger.debug("Max Nodes " + maxNodes);
		logger.debug("Max Edges " + maxEdgesPerNode);
		logger.debug("LeafNodes " + leafNodes);
		logger.debug("Bipartite " + bipartite);
		logger.debug("showNameNodes " + showNameNodes);
		int maxdegree = FastNumberUtils.parseIntWithCheck(degree, 6);
		int maxnodes = FastNumberUtils.parseIntWithCheck(maxNodes, 1000);
		int maxedges = FastNumberUtils.parseIntWithCheck(maxEdgesPerNode, 100);

		V_GenericGraph g = null;
		V_CSGraph m = null;
		try {
			V_GraphQuery q = new V_GraphQuery();
			q.addSearchIds(value);
			q.setDirected(false);
			q.setMaxNodes(maxnodes);
			q.setMaxEdgesPerNode(maxedges);
			q.setMaxHops(maxdegree);
			g = propertyGraphBuilder.makeGraphResponse(q);
			m = new V_CSGraph(g, true);
		} catch (Exception e) {
			logger.error(ExceptionUtil.getRootCauseMessage(e));
			e.printStackTrace();
		}

		return m;

	}

	@Override
	public V_CSGraph getTemporalInteractionGraph(String objectType,
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

		TemporalGraphQuery gq = new TemporalGraphQuery();

		gq.setMaxHops(FastNumberUtils.parseIntWithCheck(maxHops, 3));
		gq.setMaxNodes(FastNumberUtils.parseIntWithCheck(maxNodes, 500));
		gq.setMaxEdgesPerNode(FastNumberUtils.parseIntWithCheck(
				maxEdgesPerNode, 50));
		gq.setMinLinks(FastNumberUtils
				.parseIntWithCheck(minLinksPairOverall, 2));
		gq.setMinTransValue(FastNumberUtils.parseIntWithCheck(
				minValueAnyInteraction, 0));
		gq.setMinEdgeValue(FastNumberUtils.parseIntWithCheck(
				minValueAnyInteraction, 0)); // new, djue
		gq.setByMonth(monthly);
		gq.setByDay(daily);
		gq.setByYear(yearly);
		gq.setDirected(directed);
		gq.setStartTime(FastNumberUtils.parseLongWithCheck(minSecs, 0));
		gq.setEndTime(FastNumberUtils.parseLongWithCheck(maxSecs, 0));

		gq.addSearchIds(ids);

		logger.debug(gq.toString());

		// egb.setOriginalQuery(gq);

		V_CSGraph m = null;
		if (ValidationUtils.isValid(ids)) {
			try {
				V_GenericGraph g = eventGraphBuilder.makeGraphResponse(gq);
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

	@Override
	public V_CSGraph getInteractionGraph(String objectType, String[] value,
			String valueType, String degree, String maxNodes,
			String maxEdgesPerNode, boolean showIcons, String minSecs,
			String maxSecs, String minimumWeight) {
		logger.debug("-------");
		logger.debug("get Interaction Graph for type " + objectType);
		logger.debug("Value     " + value);
		logger.debug("Degrees   " + degree);
		logger.debug("Max Nodes " + maxNodes);
		logger.debug("Max Edges " + maxEdgesPerNode);
		logger.debug("min weight " + minimumWeight);

		// NB: min weight does not work. It is intended to say don't count edges
		// unless they occur X times (i.e. a called b + b called a > X)
		// However we are not iterating through the calls - we are using
		// SELECT DISTINCT for now.

		int maxdegree = FastNumberUtils.parseIntWithCheck(degree, 6);
		int maxnodes = FastNumberUtils.parseIntWithCheck(maxNodes, 1000);
		int maxedges = FastNumberUtils.parseIntWithCheck(maxEdgesPerNode, 50);
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
				V_GenericGraph g = eventGraphBuilder.makeGraphResponse(q);
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

}
