package graphene.services;

import graphene.dao.DocumentBuilder;
import graphene.dao.G_Parser;
import graphene.dao.HyperGraphBuilder;
import graphene.dao.StopWordService;
import graphene.dao.StyleService;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_Constraint;
import graphene.model.idl.G_DataAccess;
import graphene.model.idl.G_DocumentError;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SearchResult;
import graphene.model.idl.G_SearchResults;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idlhelper.ListRangeHelper;
import graphene.model.idlhelper.PropertyHelper;
import graphene.model.idlhelper.QueryHelper;
import graphene.util.DataFormatConstants;
import graphene.util.StringUtils;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;

import mil.darpa.vande.generic.V_GenericEdge;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GenericNode;
import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.generic.V_LegendItem;

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

	@Inject
	private DocumentBuilder db;

	/**
	 * Note that the concrete implementation of this class will usually provide
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
	public V_GenericGraph buildFromSubGraphs(final V_GraphQuery graphQuery) {
		V_GenericGraph g = new V_GenericGraph();
		g.setNodes(new HashMap<String, V_GenericNode>());
		g.setEdges(new HashMap<String, V_GenericEdge>());
		scannedQueries = new HashSet<String>();

		final PriorityQueue<G_EntityQuery> queriesToRun = new PriorityQueue<G_EntityQuery>(10, new ScoreComparator());
		Map<String, V_GenericNode> nodesFromPreviousDegree = new HashMap<String, V_GenericNode>();
		Map<String, V_GenericEdge> edgesFromPreviousDegree = new HashMap<String, V_GenericEdge>();

		if (graphQuery.getMaxHops() <= 0) {
			return new V_GenericGraph();
		} else {
			logger.debug("Attempting a graph for query " + graphQuery.toString());
		}

		int intStatus = 0;
		String strStatus = "Graph Loaded";

		final G_PropertyMatchDescriptor identifierList = G_PropertyMatchDescriptor.newBuilder().setKey("identifiers")
				.setRange(new ListRangeHelper(G_PropertyType.STRING, graphQuery.getSearchIds()))
				.setConstraint(G_Constraint.REQUIRED_EQUALS).build();
		final QueryHelper qh = new QueryHelper(identifierList);
		queriesToRun.add(qh);

		int currentDegree = 0;
		for (currentDegree = 0; (currentDegree < graphQuery.getMaxHops())
				&& (g.getNodes().size() < graphQuery.getMaxNodes()); currentDegree++) {
			G_EntityQuery eq = null;
			logger.debug("$$$$There are " + queriesToRun.size() + " queries to run in the current degree.");
			while ((queriesToRun.size() > 0) && ((eq = queriesToRun.poll()) != null)
					&& (g.getNodes().size() < graphQuery.getMaxNodes())) {

				if (ValidationUtils.isValid(eq.getPropertyMatchDescriptors())) {
					nodesFromPreviousDegree = new HashMap<String, V_GenericNode>(g.getNodes());
					edgesFromPreviousDegree = new HashMap<String, V_GenericEdge>(edgeList);
					logger.debug("Processing degree " + currentDegree);

					/**
					 * This will end up building nodes and edges, and creating
					 * new queries for the queue
					 */
					logger.debug("1111=====Running query " + eq.toString());

					// getDAO().performCallback(0, 0, this, eq);
					G_SearchResults searchResults;
					try {
						searchResults = getDAO().findByQuery(eq);

						for (final G_SearchResult t : searchResults.getResults()) {
							V_GenericGraph subGraph = null;
							if (ValidationUtils.isValid(t.getResult())) {
								final G_Entity entity = (G_Entity) t.getResult();
								final String type = (String) PropertyHelper.getSingletonValue(entity.getProperties()
										.get(G_Parser.REPORT_TYPE));

								final G_Parser parser = db.getParserForObject(type);
								if (parser != null) {

									subGraph = parser.getSubGraph(t, eq);
									if (ValidationUtils.isValid(subGraph)) {
										logger.debug("Merging nodes");
										g.getEdges().putAll(subGraph.getEdges());
										g.getNodes().putAll(subGraph.getNodes());
										// FIXME: Switch legend items to a map
										// with
										// a priority index, then do putAll
										g.getLegend().addAll(subGraph.getLegend());
									} else {
										logger.debug("no subgraph returned due to duplicate id (likely) or an error.");
									}
								} else {
									logger.warn("No parser was found for the supplied type, but carrying on.");
								}
							} else {
								logger.warn("Invalid search result, but carrying on.");
							}
						}
					} catch (final Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					logger.debug("3333====After running " + eq.toString() + ", there are "
							+ queriesToRunNextDegree.size() + " queries to run in the next degree.");
				}
			}// end while loop

			// very important!!
			// unscannedNodeList.clear();
			// ////////////////////////////////////////////////
			logger.debug("4444==== At the end of degree " + currentDegree + ", there are " + g.getNodes().size()
					+ " nodes and " + edgeList.size() + " edges");

			logger.debug("5555====There are " + queriesToRunNextDegree.size() + " queries to run in the next degree.");
			queriesToRun.addAll(queriesToRunNextDegree);
			queriesToRunNextDegree.clear();
		}

		// All hops have been done
		// Check to see if we have too many nodes.
		if (g.getNodes().size() > graphQuery.getMaxNodes()) {
			g.setNodes(nodesFromPreviousDegree);
			g.setEdges(edgesFromPreviousDegree);
			intStatus = 1; // will trigger the message.
			strStatus = "Returning only " + currentDegree + " hops, as maximum nodes you requested would be exceeded";
		} else {
			intStatus = 1; // will trigger the message.
			strStatus = "Returning " + g.getNodes().size() + " nodes and " + edgeList.size() + " edges.";
		}

		// NOW finally add in all those unique edges.

		g = performPostProcess(graphQuery, g);

		// final V_GenericGraph g = new V_GenericGraph(g.getNodes(), edgeList);
		g.setIntStatus(intStatus);
		g.setStrStatus(strStatus);
		logger.debug("Graph status: " + g.getStrStatus());
		for (final V_LegendItem li : legendItems) {
			g.addLegendItem(li);
		}

		return g;
	}

	@Override
	public void buildQueryForNextIteration(final V_GenericNode... nodes) {
		for (final V_GenericNode n : nodes) {
			if (determineTraversability(n)) {
				for (final G_EntityQuery eq : createQueriesFromNode(n)) {
					final String queryToString = eq.toString();
					// Have we done this EXACT query before?
					// TODO: Use a bloom filter
					// XXX: make sure the .toString is unique since we now have
					// time and user, etc.
					if (!scannedQueries.contains(queryToString)) {
						scannedQueries.add(queryToString);
						logger.debug("Query eq is new: " + queryToString);
						queriesToRunNextDegree.add(eq);
					} else {
						logger.debug("Skipping query eq! " + queryToString);
					}
				}
			}
		}
		logger.debug("There are " + queriesToRunNextDegree.size() + " queries to run for the next degree. ref ");
	}

	public V_GenericNode createNodeInSubgraph(final double minimumScoreRequired, final double inheritedScore,
			final double localPriority, final String originalId, final String idType, final String nodeType,
			final V_GenericNode attachTo, final String relationType, final String relationValue,
			final double nodeCertainty, final V_GenericGraph subgraph) {
		V_GenericNode a = null;
		final Map<String, V_GenericNode> nodeList = subgraph.getNodes();
		final Map<String, V_GenericEdge> edgeList = subgraph.getEdges();
		if (ValidationUtils.isValid(originalId)) {
			if (!stopwordService.isValid(originalId)) {
				addError(new G_DocumentError("Bad Identifier", "The " + nodeType + " (" + originalId
						+ ") contains a stopword", Severity.WARN.toString()));
			} else {
				final String id = generateNodeId(originalId);
				a = subgraph.getNodes().get(id);
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
					nodeList.put(id, a);
					legendItems.add(new V_LegendItem(a.getColor(), a.getNodeType()));
				}
				// now we have a valid node. Attach it to the other node
				// provided.
				if (ValidationUtils.isValid(a, attachTo)) {
					final String key = generateEdgeId(attachTo.getId(), relationType, a.getId());
					if ((key != null) && (edgeList.get(key) == null)) {
						final V_GenericEdge edge = new V_GenericEdge(key, a, attachTo);
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
						edgeList.put(key, edge);
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
	public V_GenericNode createOrUpdateNode(final double minimumScoreRequired, final double inheritedScore,
			final double localPriority, final String originalId, final String idType, final String nodeType,
			final V_GenericNode attachTo, final String relationType, final String relationValue,
			final double nodeCertainty) {
		V_GenericNode a = null;

		if (ValidationUtils.isValid(originalId)) {
			if (!stopwordService.isValid(originalId)) {
				addError(new G_DocumentError("Bad Identifier", "The " + nodeType + " (" + originalId
						+ ") contains a stopword", Severity.WARN.toString()));
			} else {
				final String id = generateNodeId(originalId);
				a = nodeList.get(id);
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
					nodeList.put(id, a);
					legendItems.add(new V_LegendItem(a.getColor(), a.getNodeType()));
				}
				// now we have a valid node. Attach it to the other node
				// provided.
				if (ValidationUtils.isValid(a, attachTo)) {
					final String key = generateEdgeId(attachTo.getId(), relationType, a.getId());
					if ((key != null) && (edgeList.get(key) == null)) {
						final V_GenericEdge edge = new V_GenericEdge(key, a, attachTo);
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
						edgeList.put(key, edge);
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

	public abstract List<G_EntityQuery> createQueriesFromNode(V_GenericNode n);

	/*
	 * (non-Javadoc)
	 * 
	 * @see graphene.services.HyperGraphBuilder#getDAO()
	 */
	@Override
	public abstract G_DataAccess getDAO();

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
		nodeList = new HashMap<String, V_GenericNode>();
		// edgeMap = new HashMap<String, V_GenericEdge>();
		edgeList = new HashMap<String, V_GenericEdge>();
		scannedQueries = new HashSet<String>();

		final PriorityQueue<G_EntityQuery> queriesToRun = new PriorityQueue<G_EntityQuery>(10, new ScoreComparator());
		Map<String, V_GenericNode> nodesFromPreviousDegree = new HashMap<String, V_GenericNode>();
		Map<String, V_GenericEdge> edgesFromPreviousDegree = new HashMap<String, V_GenericEdge>();

		if (graphQuery.getMaxHops() <= 0) {
			return new V_GenericGraph();
		} else {
			logger.debug("Attempting a graph for query " + graphQuery.toString());
		}

		int intStatus = 0;
		String strStatus = "Graph Loaded";

		final G_PropertyMatchDescriptor identifierList = G_PropertyMatchDescriptor.newBuilder().setKey("_all")
				.setRange(new ListRangeHelper(G_PropertyType.STRING, graphQuery.getSearchIds()))
				.setConstraint(G_Constraint.REQUIRED_EQUALS).build();
		final QueryHelper qh = new QueryHelper(identifierList);
		queriesToRun.add(qh);

		int currentDegree = 0;
		for (currentDegree = 0; (currentDegree < graphQuery.getMaxHops())
				&& (nodeList.size() < graphQuery.getMaxNodes()); currentDegree++) {
			G_EntityQuery eq = null;
			logger.debug("$$$$There are " + queriesToRun.size() + " queries to run in the current degree.");
			while ((queriesToRun.size() > 0) && ((eq = queriesToRun.poll()) != null)
					&& (nodeList.size() < graphQuery.getMaxNodes())) {

				if (ValidationUtils.isValid(eq.getPropertyMatchDescriptors())) {
					nodesFromPreviousDegree = new HashMap<String, V_GenericNode>(nodeList);
					edgesFromPreviousDegree = new HashMap<String, V_GenericEdge>(edgeList);
					logger.debug("Processing degree " + currentDegree);

					/**
					 * This will end up building nodes and edges, and creating
					 * new queries for the queue
					 */
					logger.debug("1111=====Running query " + eq.toString());
					getDAO().performCallback(0, 0, this, eq);
					logger.debug("3333====After running " + eq.toString() + ", there are "
							+ queriesToRunNextDegree.size() + " queries to run in the next degree.");
				}
			}// end while loop

			// very important!!
			// unscannedNodeList.clear();
			// ////////////////////////////////////////////////
			logger.debug("4444==== At the end of degree " + currentDegree + ", there are " + nodeList.size()
					+ " nodes and " + edgeList.size() + " edges");

			logger.debug("5555====There are " + queriesToRunNextDegree.size() + " queries to run in the next degree.");
			queriesToRun.addAll(queriesToRunNextDegree);
			queriesToRunNextDegree.clear();
		}

		// All hops have been done
		// Check to see if we have too many nodes.
		if (nodeList.size() > graphQuery.getMaxNodes()) {
			nodeList = nodesFromPreviousDegree;
			edgeList = edgesFromPreviousDegree;
			intStatus = 1; // will trigger the message.
			strStatus = "Returning only " + currentDegree + " hops, as maximum nodes you requested would be exceeded";
		} else {
			intStatus = 1; // will trigger the message.
			strStatus = "Returning " + nodeList.size() + " nodes and " + edgeList.size() + " edges.";
		}

		// NOW finally add in all those unique edges.

		performPostProcess(graphQuery);
		final V_GenericGraph g = new V_GenericGraph(nodeList, edgeList);
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
	@Deprecated
	@Override
	public void performPostProcess(final V_GraphQuery graphQuery) {
		// default blank
	}

	@Override
	public V_GenericGraph performPostProcess(final V_GraphQuery graphQuery, final V_GenericGraph g) {
		// default blank
		return g;
	}

	@Deprecated
	public void removeGraphQueryPath(final V_GenericNode reportNode, final G_EntityQuery q) {
		removeEdge(q.getInitiatorId(), G_CanonicalRelationshipType.CONTAINED_IN.name(), reportNode.getId(),
				G_CanonicalRelationshipType.CONTAINED_IN.name());
	}
}