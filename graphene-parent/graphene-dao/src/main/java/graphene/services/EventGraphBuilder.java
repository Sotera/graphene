package graphene.services;

import graphene.dao.TransactionDAO;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_EdgeTypeAccess;
import graphene.model.idl.G_IdType;
import graphene.model.idl.G_NodeTypeAccess;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;
import graphene.model.query.EventQuery;
import graphene.util.validator.ValidationUtils;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mil.darpa.vande.generic.V_EdgeList;
import mil.darpa.vande.generic.V_GenericEdge;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GenericNode;
import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.generic.V_NodeList;
import mil.darpa.vande.interactions.TemporalGraphQuery;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

/**
 * TODO: The parts between the eventgraphbuilder and propertygraphbuilder are
 * almost the same--the same methods are used on the DAO as well. Consider
 * combining the two.
 * 
 * This abstract class contains the common parts of an event graph builder. The
 * concrete versions will supply the correct DAO(s) and work with the customer
 * domain objects to create nodes and edges.
 * 
 * @author djue
 * 
 * @param <T>
 */
public abstract class EventGraphBuilder<T> extends AbstractGraphBuilder<T> {
	@Inject
	private Logger logger;

	/**
	 * This object will be supplied by the concrete implementation
	 */
	protected TransactionDAO dao;

	public EventGraphBuilder() {
		super();
	}

	@Override
	public void performPostProcess(V_GraphQuery graphQuery) {
		// TODO Auto-generated method stub

	}

	/**
	 * Unrolled version.
	 * 
	 * Start with a list of ids baked into a graph query. perform callbacks on
	 * those initial ids. (call those idList1) we now have an expanded nodeList.
	 * look through the nodelist to see which ones were in idList1. For those
	 * not in idList1, perform callbacks on those.
	 * 
	 * @param graphQuery
	 * @return
	 * @throws Exception
	 */
	
	public V_GenericGraph makeGraphResponse(final TemporalGraphQuery graphQuery)
			throws Exception {
		if (graphQuery.getMaxHops() <= 0) {
			return new V_GenericGraph();
		}

		this.nodeList = new V_NodeList();
		this.edgeList = new V_EdgeList(graphQuery);
		this.edgeMap = new HashMap<String, V_GenericEdge>();

		int intStatus = 0;
		String strStatus = "Graph Loaded";
		Set<String> scannedActors = new HashSet<String>();

		V_NodeList savNodeList = nodeList.clone();
		// V_EdgeList savEdgeList = edgeList.clone();
		Map<String, V_GenericEdge> saveEdgeMap = new HashMap<String, V_GenericEdge>(
				edgeMap);
		EventQuery eq = new EventQuery();
		// prime the entity query. On first entry, we don't know what types the
		// ids are, so use ANY.
		// for (String id : graphQuery.getSearchIds()) {
		eq.addIds(graphQuery.getSearchIds());
		// lets try using ids instead of attributes.
		// eq.addAttribute(
		// new G_SearchTuple<String>(G_SearchType.COMPARE_EQUALS,
		// G_CanonicalPropertyType.ANY, id));
		// }

		// aka traversals from legacy--djue
		int hop = 0;
		for (hop = 0; hop < graphQuery.getMaxHops()
				&& nodeList.getNodes().size() < graphQuery.getMaxNodes()
				&& eq.getIdList().size() > 0; hop++) {

			logger.debug("Processing hop " + hop);
			// If we have some ids to look for
			if (eq.getIdList().size() > 0) {
				logger.debug("Found " + eq.getIdList().size()
						+ " unscanned nodes to query on");

				dao.performCallback(0, 0, this, eq);

				for (String scannedId : eq.getIdList()) {
					scannedActors.add(scannedId);
				}

			}

			eq = new EventQuery();
			// Iterate over each node found by the previous query and scan them.
			for (V_GenericNode node : unscannedNodeList) {

				String valueToSearchOn = node.getIdVal();
				// if we haven't scanned
				if (ValidationUtils.isValid(valueToSearchOn)) {
					// start scanning this id.
					logger.debug("::::Scanning valueToSearchOn '"
							+ valueToSearchOn + "'\t\t " + node);
					// Make sure there aren't too many edges.
					long count = 0;
					try {
						count = dao.countEdges(valueToSearchOn);
						logger.debug("Found " + count + " results for value "
								+ valueToSearchOn);
						node.setNbrLinks((int) count);
						if (count > graphQuery.getMaxEdgesPerNode()) {
							logger.debug("There would be too many links created with this node (max "
									+ graphQuery.getMaxEdgesPerNode()
									+ "), setting clustered to true.");
							node.setCluster(true);
						} else {
							// we will search on it.
							eq.addAttribute(new G_SearchTuple<String>(
									G_SearchType.COMPARE_EQUALS, nodeTypeAccess
											.getNodeType(node.getNodeType()),
									valueToSearchOn));
						}
					} catch (Exception e) {
						e.printStackTrace();
					}
				} else {
					logger.warn("The value '" + valueToSearchOn
							+ "' was invalid and was not searched on");
				}
				// we're done scanning this id.

			}
			// very important!!
			unscannedNodeList.clear();

			logger.debug("At the end of onehop, " + nodeList.size()
					+ " nodes and " + edgeMap.size() + " edges");

			savNodeList = nodeList.clone();
			saveEdgeMap = new HashMap<String, V_GenericEdge>(edgeMap);
		}

		// All hops have been done
		// Check to see if we have too many nodes.
		if (nodeList.getNodes().size() > graphQuery.getMaxNodes()) {
			nodeList = savNodeList;
			edgeMap = saveEdgeMap;
			intStatus = 1; // will trigger the message.
			strStatus = "Returning only " + hop
					+ " hops, as maximum nodes you requested would be exceeded";
		}

		// NOW finally add in all those unique edges.
		for (V_GenericEdge e : edgeMap.values()) {
			edgeList.addEdge(e);
		}
		//nodeList.removeOrphans(edgeList);
		performPostProcess(graphQuery);
		V_GenericGraph g = new V_GenericGraph(nodeList.getNodes(),
				edgeList.getEdges());
		g.setIntStatus(intStatus);
		g.setStrStatus(strStatus);
		return g;
	}

}