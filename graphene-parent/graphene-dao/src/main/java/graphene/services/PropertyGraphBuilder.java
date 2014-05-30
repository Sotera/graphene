package graphene.services;

import graphene.dao.EntityRefDAO;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_SearchType;
import graphene.model.query.EntityRefQuery;
import graphene.model.query.EntitySearchTuple;
import graphene.util.G_CallBack;

import java.util.ArrayList;
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

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public abstract class PropertyGraphBuilder<T> implements G_CallBack<T> {
	private V_EdgeList edgeList;
	protected Map<String, V_GenericEdge> edgeMap;
	@Inject
	private Logger logger;
	protected ArrayList<V_GenericNode> newNodeList = new ArrayList<V_GenericNode>(
			3);
	protected V_NodeList nodeList;
	protected EntityRefDAO propertyDAO;

	public PropertyGraphBuilder() {
		super();
	}

	/**
	 * Doesn't matter what this does, as long as it is unique.
	 * 
	 * @param v
	 * @return
	 */
	protected String generateEdgeId(V_GenericEdge v) {
		return "s:" + v.getSourceId() + "-->t:" + v.getTargetId();
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
	public V_GenericGraph makeGraphResponse(final V_GraphQuery graphQuery)
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
		EntityRefQuery eq = new EntityRefQuery();
		// prime the entity query. On first entry, we don't know what types the
		// ids are, so use ANY.
		for (String id : graphQuery.getSearchIds()) {
			eq.getAttributeList().add(
					new EntitySearchTuple<String>(G_SearchType.COMPARE_EQUALS,
							G_CanonicalPropertyType.ANY, id));
		}

		// aka traversals from legacy--djue
		int hop = 0;
		for (hop = 0; hop < graphQuery.getMaxHops()
				&& nodeList.getNodes().size() < graphQuery.getMaxNodes()
				&& eq.getAttributeList().size() > 0; hop++) {

			logger.debug("Processing hop " + hop);

			if (eq.getAttributeList().size() > 0) {
				logger.debug("Found " + eq.getAttributeList().size()
						+ " unscanned nodes to query on");

				propertyDAO.performCallback(0, 0, this, eq);

				for (EntitySearchTuple<String> tuple : eq.getAttributeList()) {
					scannedActors.add(tuple.getValue());
				}

			}

			eq = new EntityRefQuery();
			// Iterate over each node found by the previous query and scan them.
			for (V_GenericNode node : newNodeList) {

				G_CanonicalPropertyType nodeType = G_CanonicalPropertyType
						.fromValue(node.getFamily());
				String valueToSearchOn = node.getIdVal();
				// if we haven't scanned

				// start scanning this id.
				logger.debug("::::Scanning valueToSearchOn " + valueToSearchOn
						+ "\t\t " + node);
				// Make sure there aren't too many edges.
				long count = 0;
				try {
					count = propertyDAO.countEdges(valueToSearchOn);
					node.setNbrLinks((int) count);
					if (count > graphQuery.getMaxEdgesPerNode()) {
						// we will not search on it.
						node.setCluster(true);
					} else {
						// we will search on it.
						eq.getAttributeList().add(
								new EntitySearchTuple<String>(
										G_SearchType.COMPARE_EQUALS, nodeType,
										valueToSearchOn));
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// we're done scanning this id.

			}
			// very important!!
			newNodeList.clear();

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
		nodeList.removeOrphans(edgeList);
		V_GenericGraph g = new V_GenericGraph(nodeList.getNodes(),
				edgeList.getEdges());
		g.setIntStatus(intStatus);
		g.setStrStatus(strStatus);
		return g;
	}

}