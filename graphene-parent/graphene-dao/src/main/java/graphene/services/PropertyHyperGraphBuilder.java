package graphene.services;

import graphene.business.commons.DocumentError;
import graphene.dao.G_Parser;
import graphene.dao.GenericDAO;
import graphene.dao.StopWordService;
import graphene.dao.StyleService;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_IdType;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;
import graphene.model.idl.G_SymbolConstants;
import graphene.util.DataFormatConstants;
import graphene.util.StringUtils;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.PriorityQueue;

import mil.darpa.vande.generic.V_EdgeList;
import mil.darpa.vande.generic.V_GenericEdge;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GenericNode;
import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.generic.V_LegendItem;
import mil.darpa.vande.generic.V_NodeList;

import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
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

public abstract class PropertyHyperGraphBuilder<T> extends AbstractGraphBuilder<T, G_EntityQuery> implements
		HyperGraphBuilder<T> {

	// protected Collection<DocumentGraphParser> singletons;
	private static final boolean INHERIT_ATTRIBUTES = true;
	@Inject
	protected StopWordService stopwordService;
	@Inject
	protected StyleService style;
	@Inject
	private Logger logger;

	protected ArrayList<String> skipInheritanceTypes;

	@Inject
	@Symbol(G_SymbolConstants.ENABLE_GRAPH_QUERY_PATH)
	private boolean enableGraphQueryPath;

	/**
	 * Note that the concrete implmentation of this class will usually provide
	 * the DAOs needed through its constructor (which may take advantage of
	 * dependency injection)
	 */
	public PropertyHyperGraphBuilder() {
		super();
	}

	public PropertyHyperGraphBuilder(final Collection<G_Parser> singletons) {
		// this.singletons = singletons;
		// for (final DocumentGraphParser s : singletons) {
		// s.setPhgb(this);
		// }
	}

	public void addGraphQueryPath(final V_GenericNode reportNode, final G_EntityQuery q) {
		if (enableGraphQueryPath && ValidationUtils.isValid(reportNode, q)) {
			createEdge(q.getInitiatorId(), G_CanonicalRelationshipType.CONTAINED_IN.name(), reportNode.getId(),
					G_CanonicalRelationshipType.CONTAINED_IN.name());
		}
	}

	@Override
	public V_GenericNode createOrUpdateNode(final double minimumScoreRequired, final double inheritedScore,
			final double localPriority, final String originalId, final String idType, final String nodeType,
			final V_GenericNode attachTo, final String relationType, final String relationValue,
			final double nodeCertainty) {
		V_GenericNode a = null;

		if (ValidationUtils.isValid(originalId)) {
			if (!stopwordService.isValid(originalId)) {
				addError(new DocumentError("Bad Identifier", "The " + nodeType + " (" + originalId
						+ ") contains a stopword", Severity.WARN));
			} else {
				final String id = generateNodeId(originalId);
				a = nodeList.getNode(id);
				final double calculatedPriority = inheritedScore * localPriority;
				if (a == null) {
					a = new V_GenericNode(id);
					a.setIdType(idType);
					// This is important because we use it to search on the next
					// traversal.
					a.setIdVal(originalId);
					a.setNodeType(nodeType);
					a.setColor(style.getHexColorForNode(a.getNodeType()));
					a.setMinScore(minimumScoreRequired);
					a.setPriority(calculatedPriority);
					// Remove leading zeros from the label
					a.setLabel(StringUtils.removeLeadingZeros(originalId));
					// XXX: need a way of getting the link to the page with TYPE
					a.addData(nodeType, getCombinedSearchLink(nodeType, originalId));
					nodeList.addNode(a);
					legendItems.add(new V_LegendItem(a.getColor(), a.getNodeType()));
				}
				// now we have a valid node. Attach it to the other node
				// provided.
				if (ValidationUtils.isValid(a, attachTo)) {
					final String key = generateEdgeId(attachTo.getId(), relationType, a.getId());
					if ((key != null) && (edgeMap.get(key) == null)) {
						final V_GenericEdge edge = new V_GenericEdge(a, attachTo);
						edge.setIdType(relationType);
						edge.setLabel(null);
						edge.setIdVal(relationType);
						if (nodeCertainty < 100.0) {
							edge.addData("Certainty", DataFormatConstants.formatPercent(nodeCertainty));
							edge.setLineStyle("dotted");
							// edge.setColor("#787878");
						}
						edge.addData("Local_Priority", "" + localPriority);
						edge.addData("Min_Score_Required", "" + minimumScoreRequired);
						edge.addData("Parent_Score", "" + inheritedScore);
						edge.addData("Value",
								StringUtils.coalesc(" ", a.getLabel(), relationValue, attachTo.getLabel()));
						edgeMap.put(key, edge);
					}

					// if this flag is set, we'll add the attributes to the
					// attached
					// node.
					if (INHERIT_ATTRIBUTES) {
						// attachTo.addData(a.getNodeType(), a.getIdVal());
						attachTo.inheritPropertiesOfExcept(a, skipInheritanceTypes);
					}
				}
			}
		} else {
			logger.error("Invalid id for " + nodeType + " of node " + attachTo);
		}
		return a;
	}

	@Override
	public V_GenericNode createOrUpdateNode(final String originalId, final String idType, final String nodeType,
			final V_GenericNode attachTo, final String relationType, final String relationValue) {
		return createOrUpdateNode(originalId, idType, nodeType, attachTo, relationType, relationValue, 100.0d);
	}

	public V_GenericNode createOrUpdateNode(final String originalId, final String idType, final String nodeType,
			final V_GenericNode attachTo, final String relationType, final String relationValue, final double certainty) {
		return createOrUpdateNode(0.5d, 1.0d, 0.7d, originalId, idType, nodeType, attachTo, relationType,
				relationValue, 100.0d);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see graphene.services.HyperGraphBuilder#getDAO()
	 */
	@Override
	public abstract GenericDAO getDAO();

	public void inheritLabelIfNeeded(final V_GenericNode a, final V_GenericNode... nodes) {
		for (final V_GenericNode n : nodes) {
			if ((n != null) && ValidationUtils.isValid(n.getLabel())) {
				a.setLabel(n.getLabel());
				return;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * graphene.services.HyperGraphBuilder#makeGraphResponse(mil.darpa.vande
	 * .generic.V_GraphQuery)
	 */
	@Override
	public V_GenericGraph makeGraphResponse(final V_GraphQuery graphQuery) throws Exception {
		nodeList = new V_NodeList();
		edgeMap = new HashMap<String, V_GenericEdge>();
		edgeList = new V_EdgeList(graphQuery);
		scannedQueries = new HashSet<String>();
		queriesToRun = new PriorityQueue<G_EntityQuery>(10, new ScoreComparator());
		V_NodeList savNodeList = new V_NodeList();

		if (graphQuery.getMaxHops() <= 0) {
			return new V_GenericGraph();
		} else {
			logger.debug("Attempting a graph for query " + graphQuery.toString());
		}

		int intStatus = 0;
		String strStatus = "Graph Loaded";
		// Set<String> scannedStrings = new HashSet<String>();

		final G_EntityQuery.Builder queryBuilder = G_EntityQuery.newBuilder();
		// EntityQuery eq = G_EntityQuery.newBuilder();
		// prime the entity query. On first entry, we don't know what types the
		// ids are, so use ANY.
		final G_IdType nodeType = nodeTypeAccess.getCommonNodeType(G_CanonicalPropertyType.ANY);
		for (final String id : graphQuery.getSearchIds()) {
			queryBuilder.getAttributeList().add(new G_SearchTuple<String>(G_SearchType.COMPARE_EQUALS, nodeType, id));
		}
		queriesToRun.add(queryBuilder.build());

		Map<String, V_GenericEdge> saveEdgeMap = new HashMap<String, V_GenericEdge>();
		int currentDegree = 0;
		for (currentDegree = 0; (currentDegree < graphQuery.getMaxHops())
				&& (nodeList.getNodes().size() < graphQuery.getMaxNodes()); currentDegree++) {
			G_EntityQuery eq = null;
			logger.debug("$$$$There are " + queriesToRun.size() + " queries to run in the current degree.");
			while ((queriesToRun.size() > 0) && ((eq = queriesToRun.poll()) != null)
					&& (nodeList.getNodes().size() < graphQuery.getMaxNodes())) {

				if (ValidationUtils.isValid(eq.getAttributeList())
						&& ValidationUtils.isValid(eq.getAttributeList().get(0).getValue())) {

					savNodeList = nodeList.clone();
					logger.debug("Processing degree " + currentDegree);
					// if (SMART_SEARCH) {

					/**
					 * This will end up building nodes and edges, and creating
					 * new queries for the queue
					 */
					logger.debug("1111=====Running query " + eq.toString());
					getDAO().performCallback(0, 0, this, eq);
					logger.debug("3333====After running " + eq.toString() + ", there are "
							+ queriesToRunNextDegree.size() + " queries to run in the next degree.");
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
					} catch (final Exception e) {
						logger.error("makeGraphResponse " + e.getMessage());
					}
					// we're done scanning this id.
				}
			}// end while loop

			// very important!!
			// unscannedNodeList.clear();
			// ////////////////////////////////////////////////
			logger.debug("4444==== At the end of degree " + currentDegree + ", there are " + nodeList.size()
					+ " nodes and " + edgeMap.size() + " edges");

			saveEdgeMap = new HashMap<String, V_GenericEdge>(edgeMap);
			logger.debug("5555====There are " + queriesToRunNextDegree.size() + " queries to run in the next degree.");
			queriesToRun.addAll(queriesToRunNextDegree);
			queriesToRunNextDegree.clear();
		}

		// All hops have been done
		// Check to see if we have too many nodes.
		if (nodeList.getNodes().size() > graphQuery.getMaxNodes()) {
			nodeList = savNodeList;
			edgeMap = saveEdgeMap;
			intStatus = 1; // will trigger the message.
			strStatus = "Returning only " + currentDegree + " hops, as maximum nodes you requested would be exceeded";
		} else {
			intStatus = 1; // will trigger the message.
			strStatus = "Returning " + nodeList.getNodes().size() + " nodes and " + edgeMap.size() + " edges.";
		}

		// NOW finally add in all those unique edges.

		for (final V_GenericEdge e : edgeMap.values()) {
			edgeList.addEdge(e);
		}

		performPostProcess(graphQuery);
		final V_GenericGraph g = new V_GenericGraph(nodeList.getNodes(), edgeList.getEdges());
		g.setIntStatus(intStatus);
		g.setStrStatus(strStatus);
		logger.debug("Graph status: " + g.getStrStatus());
		for (final V_LegendItem li : legendItems) {
			g.addLegendItem(li);
		}

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
	public void performPostProcess(final V_GraphQuery graphQuery) {
		// default blank
	}

	public void removeGraphQueryPath(final V_GenericNode reportNode, final G_EntityQuery q) {
		removeEdge(q.getInitiatorId(), G_CanonicalRelationshipType.CONTAINED_IN.name(), reportNode.getId(),
				G_CanonicalRelationshipType.CONTAINED_IN.name());
	}
}