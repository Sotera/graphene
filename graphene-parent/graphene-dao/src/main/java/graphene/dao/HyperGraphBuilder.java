package graphene.services;

import graphene.dao.GenericDAO;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GenericNode;
import mil.darpa.vande.generic.V_GraphQuery;

public interface HyperGraphBuilder<T> {

	public abstract void buildQueryForNextIteration(V_GenericNode... nodes);

	/**
	 * 
	 * @param minimumScoreRequired
	 * @param inheritedScore
	 * @param localPriority
	 * @param id
	 * @param idType
	 * @param nodeType
	 * @param attachTo
	 * @param relationType
	 * @param relationValue
	 * @param nodeCertainty
	 * @return
	 */
	public abstract V_GenericNode createOrUpdateNode(double minimumScoreRequired, double inheritedScore,
			double localPriority, String id, String idType, String nodeType, V_GenericNode attachTo,
			String relationType, String relationValue, double nodeCertainty);

	/**
	 * Create a node or update an existing one. Also, use the color based on the
	 * node type or data (depends on your implementation). It is preferred that
	 * the attachTo node act as the parent of the node you wish to add.
	 * 
	 * @param id
	 *            the unique id of this node.
	 * @param idType
	 *            This is used for the Identifier Type text box for the node in
	 *            the GraphUI
	 * @param nodeType
	 *            This is used for determining node color and search query
	 *            types, and more.
	 * @param attachTo
	 *            the id of a node this node should attach to with the following
	 *            relationship
	 * @param relationType
	 *            This is used for the Identifier Type text box for the edge in
	 *            the GraphUI
	 * @param relationValue
	 *            This is used for determining edge color and search query
	 *            types, and more.
	 * @return
	 */
	public abstract V_GenericNode createOrUpdateNode(String id, String idType, String nodeType, V_GenericNode attachTo,
			String relationType, String relationValue);

	/**
	 * Create a node or update an existing one. Also, force the color to the one
	 * provided.
	 * 
	 * @param id
	 * @param idType
	 * @param nodeType
	 * @param attachTo
	 * @param relationType
	 * @param relationValue
	 * @param forceColor
	 * @return
	 */
	// public abstract V_GenericNode createOrUpdateNode(String id, String
	// idType,
	// String nodeType, V_GenericNode attachTo, String relationType,
	// String relationValue, String forceColor);

	/**
	 * Determine whether or not you want to traverse deeper on this node.
	 * 
	 * @param n
	 * @return
	 */
	boolean determineTraversability(V_GenericNode n);

	/**
	 * This object will be supplied by the concrete implementation
	 */
	public abstract GenericDAO getDAO();

	// public abstract DocumentGraphParser getParserForObject(Object obj);

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
	public abstract V_GenericGraph makeGraphResponse(V_GraphQuery graphQuery) throws Exception;

	/**
	 * Individual implementations can override this method to perform
	 * modifications on the graph (or graph analysis) after the complete graph
	 * has been built.
	 * 
	 * @param graphQuery
	 */
	public abstract void performPostProcess(V_GraphQuery graphQuery);
}