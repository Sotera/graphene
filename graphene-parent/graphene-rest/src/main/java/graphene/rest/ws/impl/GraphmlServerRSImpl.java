package graphene.rest.ws.impl;

import graphene.dao.FederatedEventGraphServer;
import graphene.rest.ws.GraphmlServerRS;
import graphene.services.AbstractGraphBuilder;
import graphene.services.EventGraphBuilder;
import graphene.services.HyperGraphBuilder;
import graphene.util.ExceptionUtil;
import graphene.util.FastNumberUtils;
import graphene.util.validator.ValidationUtils;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import mil.darpa.vande.converters.graphml.GraphmlContainer;
import mil.darpa.vande.converters.graphml.GraphmlGraph;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.interactions.TemporalGraphQuery;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.slf4j.Logger;

/**
 * /** The REST Service to return a graph in SnaglML format which is an
 * extension of graphml.
 * 
 * @author PWG for DARPA
 * 
 */
public class GraphmlServerRSImpl implements GraphmlServerRS {

//	@InjectService("Property")
//	private AbstractGraphBuilder propertyGraphBuilder;

	@InjectService("HyperProperty")
	private HyperGraphBuilder propertyGraphBuilder;
	
	@Inject
	private FederatedEventGraphServer feg;

	@Inject
	private Logger logger;

	public GraphmlServerRSImpl() {

	}

	@Override
	public GraphmlContainer getProperties(@PathParam("type") String type,
			@PathParam("value") String value,
			@QueryParam("degree") String degree,
			@QueryParam("maxNodes") String maxNodes,
			@QueryParam("maxEdgesPerNode") String maxEdgesPerNode,
			@QueryParam("bipartite") boolean bipartite,
			@QueryParam("showLeafNodes") boolean leafNodes,
			@QueryParam("showNameNodes") boolean showNameNodes,
			@QueryParam("showIcons") boolean showIcons) {
		logger.trace("-------");
		logger.trace("getGraph for type " + type);
		logger.trace("Value     " + value);
		logger.trace("Degrees   " + degree);
		logger.trace("LeafNodes " + leafNodes);
		logger.trace("Max Nodes " + maxNodes);
		logger.trace("Max Edges " + maxEdgesPerNode);
		logger.trace("LeafNodes " + leafNodes);
		logger.trace("Bipartite " + bipartite);
		logger.trace("showNameNodes " + showNameNodes);

		int maxdegree = FastNumberUtils.parseIntWithCheck(degree, 6);
		int maxnodes = FastNumberUtils.parseIntWithCheck(maxNodes, 1000);
		int maxedges = FastNumberUtils.parseIntWithCheck(maxEdgesPerNode, 50);

		V_GenericGraph g = null;
		GraphmlGraph m = null;
		GraphmlContainer c = null;
		try {
			V_GraphQuery q = new V_GraphQuery();
			q.addSearchIds(value);
			q.setDirected(false);
			q.setMaxNodes(maxnodes);
			q.setMaxEdgesPerNode(maxedges);
			q.setMaxHops(maxdegree);
			g = propertyGraphBuilder.makeGraphResponse(q);
			m = new GraphmlGraph(g, true);
			c = new GraphmlContainer(m);
		} catch (Exception e) {
			logger.error(ExceptionUtil.getRootCauseMessage(e));
			e.printStackTrace();
		}

		return c;

	}

	@Override
	public GraphmlContainer getEvents(String objectType, String[] value,
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

		GraphmlGraph m = null;
		GraphmlContainer c = null;
		if (ValidationUtils.isValid(value)) {
			try {
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
				g = gb.makeGraphResponse(q);
				m = new GraphmlGraph(g, true);
				c = new GraphmlContainer(m);
			} catch (Exception e) {
				logger.error(e.getMessage());
				e.printStackTrace();
			}

		} else {
			c = new GraphmlContainer();
			// c.setStrStatus("A query was sent without any ids");
			logger.error("A query was sent without any ids");
		}

		return c;

	}
}
