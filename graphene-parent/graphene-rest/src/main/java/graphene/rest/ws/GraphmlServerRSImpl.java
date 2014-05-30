package graphene.rest.ws;

import graphene.services.PropertyGraphBuilder;
import graphene.util.ExceptionUtil;
import graphene.util.FastNumberUtils;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import mil.darpa.vande.converters.graphml.GraphmlContainer;
import mil.darpa.vande.converters.graphml.GraphmlGraph;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.interactions.InteractionFinder;
import mil.darpa.vande.interactions.InteractionGraphBuilder;

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

	@InjectService("Property")
	private PropertyGraphBuilder pgb;

	@InjectService("Interaction")
	private InteractionFinder interactionFinder;

	@Inject
	private InteractionGraphBuilder interactionGraphBuilder;

	@Inject
	private Logger logger;

	public GraphmlServerRSImpl() {

	}

	@Override
	public GraphmlContainer getPropertyGraph(@PathParam("type") String type,
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
			// g = entityGraphBuilder.makeGraphResponse(q, propertyFinder);
			g = pgb.makeGraphResponse(q);
			m = new GraphmlGraph(g, true);
			c = new GraphmlContainer(m);
		} catch (Exception e) {
			logger.error(ExceptionUtil.getRootCauseMessage(e));
			e.printStackTrace();
		}

		return c;

	}

	@Override
	public GraphmlContainer getDirected(
			@PathParam("objectType") String objectType,
			@PathParam("value") String value,
			@QueryParam("Type") String valueType,
			@QueryParam("degree") String degree,
			@QueryParam("maxNodes") String maxNodes,
			@QueryParam("maxEdgesPerNode") String maxEdgesPerNode,
			@QueryParam("showIcons") boolean showIcons,
			@QueryParam("fromdt") @DefaultValue(value = "0") String minSecs,
			@QueryParam("todt") @DefaultValue(value = "0") String maxSecs,
			@QueryParam("minWeight") String minimumWeight) {
		logger.trace("-------");
		logger.trace("getGraph for type " + objectType);
		logger.trace("Value     " + value);
		logger.trace("Degrees   " + degree);
		logger.trace("Max Nodes " + maxNodes);
		logger.trace("Max Edges " + maxEdgesPerNode);
		logger.trace("min weight " + minimumWeight);

		// FIXME: Min weight is not working
		int maxdegree = FastNumberUtils.parseIntWithCheck(degree, 6);
		int maxnodes = FastNumberUtils.parseIntWithCheck(maxNodes, 1000);
		int maxedges = FastNumberUtils.parseIntWithCheck(maxEdgesPerNode, 50);
		int minWeight = FastNumberUtils.parseIntWithCheck(minimumWeight, 0);
		long startDate = FastNumberUtils.parseLongWithCheck(minSecs, 0);
		long endDate = FastNumberUtils.parseLongWithCheck(maxSecs, 0);

		String[] values;

		if (valueType.contains("list")) {
			values = value.split("_");
		} else
			values = new String[] { value };

		V_GraphQuery q = new V_GraphQuery();

		q.addSearchIds(values);

		q.setMaxEdgesPerNode(maxedges);
		q.setMaxHops(maxdegree);
		q.setMaxNodes(maxnodes);
		q.setStartTime(startDate);
		q.setEndTime(endDate);
		q.setType(valueType); // new, --djue
		q.setMinTransValue(minWeight); // new --djue

		V_GenericGraph g = null;
		GraphmlGraph m = null;
		try {
			g = interactionGraphBuilder.makeGraphResponse(q, interactionFinder);
			m = new GraphmlGraph(g, true);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		GraphmlContainer c = new GraphmlContainer(m);
		return c;

	}
	//
	// private String fixup(String id) {
	// id = id.replace("___", "/");
	// id = id.replace("ZZZZZ", "#");
	// return id;
	// }

}
