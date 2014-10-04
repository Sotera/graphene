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
import java.util.Stack;
import java.util.concurrent.ConcurrentLinkedQueue;

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
		AbstractGraphBuilder<T> implements HyperGraphBuilder<T> {

	private static final boolean SMART_SEARCH = true;

	@Inject
	private Logger logger;

	/*
	 * (non-Javadoc)
	 * 
	 * @see graphene.services.HyperGraphBuilder#getDAO()
	 */
	@Override
	public abstract GenericDAO<T, EntityQuery> getDAO();

	/**
	 * Note that the concrete implmentation of this class will usually provide
	 * the DAOs needed through its constructor (which may take advantage of
	 * dependency injection)
	 */
	public PropertyHyperGraphBuilder() {
		super();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * graphene.services.HyperGraphBuilder#makeGraphResponse(mil.darpa.vande
	 * .generic.V_GraphQuery)
	 */
	@Override
	public V_GenericGraph makeGraphResponse(final V_GraphQuery graphQuery)
			throws Exception {
		this.nodeList = new V_NodeList();
		this.edgeMap = new HashMap<String, V_GenericEdge>();
		this.edgeList = new V_EdgeList(graphQuery);
		this.scannedQueries = new HashSet<String>();
		// this.scannedResults = new HashSet<String>();
		this.queriesToRun = new Stack<EntityQuery>();
		V_NodeList savNodeList = new V_NodeList();

		if (graphQuery.getMaxHops() <= 0) {
			return new V_GenericGraph();
		} else {
			logger.debug("Attempting a graph for query "
					+ graphQuery.toString());
		}

		int intStatus = 0;
		String strStatus = "Graph Loaded";
		// Set<String> scannedStrings = new HashSet<String>();

		EntityQuery eq = new EntityQuery();
		// prime the entity query. On first entry, we don't know what types the
		// ids are, so use ANY.
		G_IdType nodeType = nodeTypeAccess
				.getCommonNodeType(G_CanonicalPropertyType.ANY);
		for (String id : graphQuery.getSearchIds()) {
			eq.addAttribute(new G_SearchTuple<String>(
					G_SearchType.COMPARE_EQUALS, nodeType, id));
		}
		eq.setCustomerQueryFlag(true);
		queriesToRun.add(eq);

		Map<String, V_GenericEdge> saveEdgeMap = new HashMap<String, V_GenericEdge>();
		int currentDegree = 0;
		for (currentDegree = 0; currentDegree < graphQuery.getMaxHops()
				&& nodeList.getNodes().size() < graphQuery.getMaxNodes(); currentDegree++) {
			eq = null;
			logger.debug("$$$$There are " + queriesToRun.size()
					+ " queries to run in the current degree.");
			while (queriesToRun.size() > 0 && (eq = queriesToRun.pop()) != null
					&& nodeList.getNodes().size() < graphQuery.getMaxNodes()) {

				if (eq.getAttributeList() != null
						&& eq.getAttributeList().size() > 0) {

					savNodeList = nodeList.clone();
					logger.debug("Processing degree " + currentDegree);
					// if (SMART_SEARCH) {

					/**
					 * This will end up building nodes and edges, and creating
					 * new queries for the queue
					 */
					logger.debug("1111=====Running query " + eq.toString());
					getDAO().performCallback(0, 0, this, eq);
					logger.debug("3333====After running " + eq.toString()
							+ ", there are " + queriesToRunNextDegree.size()
							+ " queries to run in the next degree.");
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
			}// end while loop

			// very important!!
			// unscannedNodeList.clear();
			// ////////////////////////////////////////////////
			logger.debug("4444==== At the end of degree " + currentDegree
					+ ", there are " + nodeList.size() + " nodes and "
					+ edgeMap.size() + " edges");

			saveEdgeMap = new HashMap<String, V_GenericEdge>(edgeMap);
			logger.debug("5555====There are " + queriesToRunNextDegree.size()
					+ " queries to run in the next degree. ref "
					+ queriesToRunNextDegree);
			queriesToRun.addAll(queriesToRunNextDegree);
			queriesToRunNextDegree.clear();
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

		for (V_GenericEdge e : edgeMap.values()) {
			edgeList.addEdge(e);
		}

		performPostProcess(graphQuery);
		V_GenericGraph g = new V_GenericGraph(nodeList.getNodes(),
				edgeList.getEdges());
		g.setIntStatus(intStatus);
		g.setStrStatus(strStatus);

		return g;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * graphene.services.HyperGraphBuilder#performPostProcess(mil.darpa.vande
	 * .generic.V_GraphQuery)
	 */
	@Override
	public void performPostProcess(V_GraphQuery graphQuery) {
		// default blank
	}

}