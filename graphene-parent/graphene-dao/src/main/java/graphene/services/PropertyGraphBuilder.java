package graphene.services;

import graphene.dao.EntityRefDAO;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_IdType;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;
import graphene.model.query.EntityQuery;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import mil.darpa.vande.generic.V_EdgeList;
import mil.darpa.vande.generic.V_GenericEdge;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GenericNode;
import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.generic.V_LegendItem;
import mil.darpa.vande.generic.V_NodeList;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

/**
 * This abstract class contains the common parts of a property graph builder.
 * The concrete versions will supply the correct DAO(s) and work with the
 * customer domain objects to create nodes and edges.
 * 
 * @author djue
 * 
 * @param <T>
 */
public abstract class PropertyGraphBuilder<T> extends AbstractGraphBuilder<T,V_GraphQuery> {

	@Inject
	private Logger logger;

	/**
	 * This object will be supplied by the concrete implementation
	 */
	protected EntityRefDAO dao;

	/**
	 * Note that the concrete implmentation of this class will usually provide
	 * the DAOs needed through its constructor (which may take advantage of
	 * dependency injection)
	 */
	public PropertyGraphBuilder() {
		super();
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
	@Override
	public V_GenericGraph makeGraphResponse(final V_GraphQuery graphQuery)
			throws Exception {
		if (graphQuery.getMaxHops() <= 0) {
			return new V_GenericGraph();
		} else {
			logger.debug("Attempting a graph for query "
					+ graphQuery.toString());
		}

		this.nodeList = new V_NodeList();

		this.edgeMap = new HashMap<String, V_GenericEdge>();

		int intStatus = 0;
		String strStatus = "Graph Loaded";
		Set<String> scannedActors = new HashSet<String>();

		// V_EdgeList savEdgeList = edgeList.clone();

		EntityQuery eq = new EntityQuery();
		// prime the entity query. On first entry, we don't know what types the
		// ids are, so use ANY.
		G_IdType nodeType = nodeTypeAccess
				.getCommonNodeType(G_CanonicalPropertyType.ANY);
		for (String id : graphQuery.getSearchIds()) {

			eq.addAttribute(new G_SearchTuple<String>(
					G_SearchType.COMPARE_EQUALS, nodeType, id));
		}
		V_NodeList savNodeList = new V_NodeList();
		Map<String, V_GenericEdge> saveEdgeMap = new HashMap<String, V_GenericEdge>();
		int currentDegree = 0;
		for (currentDegree = 0; currentDegree < graphQuery.getMaxHops()
				&& nodeList.getNodes().size() < graphQuery.getMaxNodes()
				&& eq.getAttributeList().size() > 0; currentDegree++) {

			savNodeList = nodeList.clone(); // concurrent modification error!!
			logger.debug("Processing degree " + currentDegree);

			if (eq.getAttributeList().size() > 0) {
				logger.debug("Found " + eq.getAttributeList().size()
						+ " unscanned nodes to query on");

				dao.performCallback(0, 0, this, eq);

				for (G_SearchTuple<String> tuple : eq.getAttributeList()) {
					scannedActors.add(tuple.getValue());
				}

			}

			eq = new EntityQuery();
			// Iterate over each node found by the previous query and scan them.
			for (V_GenericNode node : unscannedNodeList) {

				String valueToSearchOn = node.getIdVal();
				// start scanning this id.
				// logger.debug("::::Scanning valueToSearchOn " +
				// valueToSearchOn
				// + "\t\t " + node);

				long count = 0;
				try {
					count = dao.countEdges(valueToSearchOn);
					node.setNbrLinks((int) count);
					// Make sure there aren't too many edges.
					if (count > graphQuery.getMaxEdgesPerNode()) {
						// we will not search on it.
						node.setCluster(true);
					} else {
						// we will search on it.
						eq.addAttribute(
							new G_SearchTuple<String>(
								G_SearchType.COMPARE_EQUALS, 
								nodeTypeAccess.getNodeType(node.getNodeType()),
								valueToSearchOn
							)
						);
					}
				} catch (Exception e) {
					e.printStackTrace();
				}
				// we're done scanning this id.

			}
			// very important!!
			unscannedNodeList.clear();

			logger.debug("At the end of degree " + currentDegree
					+ ", there are" + nodeList.size() + " nodes and "
					+ edgeMap.size() + " edges");

			// savNodeList = nodeList;//.clone();
			saveEdgeMap = new HashMap<String, V_GenericEdge>(edgeMap);
		}

		// All hops have been done
		// Check to see if we have too many nodes.
		if (nodeList.getNodes().size() > graphQuery.getMaxNodes()) {
			nodeList = savNodeList;
			edgeMap = saveEdgeMap;
			intStatus = 1; // will trigger the message.
			strStatus = "Returning only " + currentDegree
					+ " hops, as maximum nodes you requested would be exceeded";
		}

		// NOW finally add in all those unique edges.
		this.edgeList = new V_EdgeList(graphQuery);
		for (V_GenericEdge e : edgeMap.values()) {
			edgeList.addEdge(e);
		}

		//nodeList.removeOrphans(edgeList);
		performPostProcess(graphQuery);
		V_GenericGraph g = new V_GenericGraph(nodeList.getNodes(), edgeList.getEdges());
		g.setIntStatus(intStatus);
		g.setStrStatus(strStatus);
		
		for (V_LegendItem li : legendItems) {
			g.addLegendItem(li);
		}
		
		return g;
	}

	/**
	 * Individual implementations can override this method to perform
	 * modifications on the graph (or graph analysis) after the complete graph
	 * has been built.
	 * 
	 * @param graphQuery
	 */
	public void performPostProcess(V_GraphQuery graphQuery) {

	}

}