package graphene.services;

import graphene.dao.DocumentGraphParser;
import graphene.dao.GenericDAO;
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
import mil.darpa.vande.generic.V_NodeList;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.UsesConfiguration;
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
@UsesConfiguration(DocumentGraphParser.class)
public abstract class PropertyHyperGraphBuilder<T> extends
		AbstractGraphBuilder<T> {
	// public abstract V_GenericNode createOrUpdateNode(String id, String
	// idType,
	// String nodeType, V_GenericNode attachTo, String relationType,
	// String relationValue);

	private static final boolean SMART_SEARCH = true;
	protected Set<String> scannedQueries = new HashSet<String>();
	protected Set<String> scannedResults = new HashSet<String>();
	@Inject
	private Logger logger;

	/**
	 * This object will be supplied by the concrete implementation
	 */
	public abstract GenericDAO<T, EntityQuery> getDAO();

	/**
	 * Note that the concrete implmentation of this class will usually provide
	 * the DAOs needed through its constructor (which may take advantage of
	 * dependency injection)
	 */
	public PropertyHyperGraphBuilder() {
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
		Set<String> scannedStrings = new HashSet<String>();

		EntityQuery eq = new EntityQuery();
		// prime the entity query. On first entry, we don't know what types the
		// ids are, so use ANY.
		G_IdType nodeType = nodeTypeAccess
				.getCommonNodeType(G_CanonicalPropertyType.ANY);
		for (String id : graphQuery.getSearchIds()) {
			eq.addAttribute(new G_SearchTuple<String>(
					G_SearchType.COMPARE_EQUALS, nodeType, id));
		}
		queriesToRun.add(eq);
		V_NodeList savNodeList = new V_NodeList();
		Map<String, V_GenericEdge> saveEdgeMap = new HashMap<String, V_GenericEdge>();
		int currentDegree = 0;
		for (currentDegree = 0; currentDegree < graphQuery.getMaxHops()
				&& nodeList.getNodes().size() < graphQuery.getMaxNodes(); currentDegree++) {
			eq = null;
			while ((eq = queriesToRun.poll()) != null
					&& nodeList.getNodes().size() < graphQuery.getMaxNodes()) {
				if (eq != null && eq.getAttributeList() != null
						&& eq.getAttributeList().size() > 0
						&& !scannedQueries.contains(eq.toString())) {

					savNodeList = nodeList.clone();
					logger.debug("Processing degree " + currentDegree);
					if (SMART_SEARCH) {

						// record that we've already looked at these strings.
						scannedQueries.add(eq.toString());
						getDAO().performCallback(0, 0, this, eq);

						for (G_SearchTuple<String> a : eq.getAttributeList()) {
							scannedStrings.add(a.getValue());
						}
					} else {
						for (G_SearchTuple<String> a : eq.getAttributeList()) {
							EntityQuery subQuery = new EntityQuery();
							subQuery.setMaxResult(10l);
							subQuery.addAttribute(a);
							getDAO().performCallback(0, 0, this, subQuery);
							scannedStrings.add(a.getValue());
						}
					}

					try {

						// long count = dao.count(eq);
						// Make sure there aren't too many edges.
						// if (count > graphQuery.getMaxEdgesPerNode()) {
						// we will not search on it.
						// node.setCluster(true);
						// node.setLabel(node.getLabel() + " (" + count +
						// ")");
						// } else {
						// we will search on it the next go around.
						// eq.addAttribute(new G_SearchTuple<String>(
						// G_SearchType.COMPARE_EQUALS, nodeTypeAccess
						// .getNodeType(node.getNodeType()),
						// valueToSearchOn));
						// }
					} catch (Exception e) {
						e.printStackTrace();
					}
					// we're done scanning this id.
				}
			}
			// very important!!
			unscannedNodeList.clear();
			// ////////////////////////////////////////////////
			logger.debug("At the end of degree " + currentDegree
					+ ", there are " + nodeList.size() + " nodes and "
					+ edgeMap.size() + " edges");

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
		V_GenericGraph g = new V_GenericGraph(nodeList.getNodes(),
				edgeList.getEdges());
		g.setIntStatus(intStatus);
		g.setStrStatus(strStatus);

		nodeList.clear();
		scannedQueries.clear();
		scannedResults.clear();
		queriesToRun.clear();
		return g;
	}

	/**
	 * @return the scannedQueries
	 */
	public final Set<String> getScannedQueries() {
		return scannedQueries;
	}

	/**
	 * @param scannedQueries
	 *            the scannedQueries to set
	 */
	public final void setScannedQueries(Set<String> scannedQueries) {
		this.scannedQueries = scannedQueries;
	}

	/**
	 * @return the scannedResults
	 */
	public final Set<String> getScannedResults() {
		return scannedResults;
	}

	/**
	 * @param scannedResults
	 *            the scannedResults to set
	 */
	public final void setScannedResults(Set<String> scannedResults) {
		this.scannedResults = scannedResults;
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

	public abstract void buildQueryForNextIteration(V_GenericNode... nodes);

	public abstract V_GenericNode createOrUpdateNode(String id, String idType,
			String nodeType, V_GenericNode attachTo, String relationType,
			String relationValue);

	/**
	 * Returns true if this result id has previously been scanned.
	 * 
	 * @param reportId
	 * @return
	 */
	public boolean isPreviouslyScannedResult(String reportId) {
		// TODO Auto-generated method stub
		return false;
	}

	public void addScannedResult(String reportId) {
		// TODO Auto-generated method stub

	}

}