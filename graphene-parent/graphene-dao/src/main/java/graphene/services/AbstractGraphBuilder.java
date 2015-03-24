package graphene.services;

import graphene.dao.DocumentBuilder;
import graphene.dao.G_Parser;
import graphene.dao.HyperGraphBuilder;
import graphene.dao.StopWordService;
import graphene.dao.StyleService;
import graphene.model.idl.G_CallBack;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_Constraint;
import graphene.model.idl.G_DataAccess;
import graphene.model.idl.G_DocumentError;
import graphene.model.idl.G_EdgeTypeAccess;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_NodeTypeAccess;
import graphene.model.idl.G_PropertyKeyTypeAccess;
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
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.Stack;

import mil.darpa.vande.generic.V_GenericEdge;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GenericNode;
import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.generic.V_LegendItem;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.URLEncoder;
import org.slf4j.Logger;

public abstract class AbstractGraphBuilder implements G_CallBack, HyperGraphBuilder {
	@Inject
	protected G_EdgeTypeAccess edgeTypeAccess;

	// protected Collection<DocumentGraphParser> singletons;
	private static final boolean INHERIT_ATTRIBUTES = true;
	@Inject
	protected StopWordService stopwordService;
	@Inject
	protected StyleService style;

	protected ArrayList<String> skipInheritanceTypes;

	@Inject
	@Symbol(G_SymbolConstants.ENABLE_GRAPH_QUERY_PATH)
	private boolean enableGraphQueryPath;

	@Inject
	private DocumentBuilder db;

	@Inject
	protected G_NodeTypeAccess nodeTypeAccess;

	@Inject
	protected URLEncoder encoder;

	public static final int MIN_NODE_SIZE = 16;

	public static final int MAX_NODE_SIZE = 0;

	@Inject
	protected G_PropertyKeyTypeAccess propertyKeyTypeAccess;

	protected Map<String, V_GenericEdge> edgeList;

	/**
	 * This field is to inform other services about which data sources can be
	 * graphed using this builder. Each implementation should specify at least
	 * one datasource string that is supported by itself.
	 */

	// protected Map<String, V_GenericEdge> edgeMap = new HashMap<String,
	// V_GenericEdge>();

	protected List<G_DocumentError> errors = new ArrayList<G_DocumentError>();

	// TODO: Change this to a FIFO Queue and address any duplicate node issues
	protected Collection<V_GenericNode> unscannedNodeList = new HashSet<V_GenericNode>(3);

	protected Set<String> scannedQueries = new HashSet<String>();

	protected Set<String> scannedResults = new HashSet<String>();

	protected Stack<G_EntityQuery> queriesToRunNextDegree = new Stack<G_EntityQuery>();

	protected Map<String, V_GenericNode> nodeList = new HashMap<String, V_GenericNode>();

	protected Set<V_LegendItem> legendItems = new HashSet<V_LegendItem>();

	@Inject
	private Logger logger;

	protected LinkGenerator linkGenerator;

	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_MAX_SEARCH_RESULTS)
	protected Integer defaultMaxResults;

	public AbstractGraphBuilder() {
		super();
	}

	@Override
	public void addError(final G_DocumentError e) {
		if (ValidationUtils.isValid(e)) {
			errors.add(e);
		}
	}

	@Override
	public void addGraphQueryPath(final V_GenericNode reportNode, final G_EntityQuery q) {
		if (enableGraphQueryPath && ValidationUtils.isValid(reportNode, q)) {
			createEdge(q.getInitiatorId(), G_CanonicalRelationshipType.CONTAINED_IN.name(), reportNode.getId(),
					G_CanonicalRelationshipType.CONTAINED_IN.name());
		}
	}

	@Override
	public void addScannedResult(final String reportId) {
		scannedResults.add(reportId);
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
					// Have we done this EXACT query before? Note: a query id
					// may be different than a node id, depending on how the
					// query is constructed.
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

	// TODO: We want to remove query path edges if we have other edges going to
	// it.
	public boolean createEdge(final String fromId, final String relationType, final String toId,
			final String relationValue) {
		if (ValidationUtils.isValid(fromId, toId)) {
			final String key = generateEdgeId(fromId, relationType, toId);
			final V_GenericNode a = nodeList.get(fromId);
			final V_GenericNode b = nodeList.get(toId);
			if (ValidationUtils.isValid(key, a, b) && !edgeList.containsKey(key)) {

				final V_GenericEdge v = new V_GenericEdge(key, a, b);
				v.setIdType(relationType);
				v.setLabel(null);
				v.setIdVal(relationType);
				v.addData("Value", StringUtils.coalesc(" ", a.getLabel(), relationValue, b.getLabel()));
				edgeList.put(key, v);
				return true;
			}
		}
		return false;
	}

	@Override
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
				logger.error("ID contained a stopword, not creating this node.");
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

	/**
	 * Doesn't matter what this does, as long as it is unique.
	 * 
	 * @param v
	 * @return
	 */
	protected String generateEdgeId(final String... addendIds) {
		String key = null;
		// Allow for null values as part of the id.
		if ((addendIds != null) && (addendIds.length > 0)) {
			key = Arrays.toString(addendIds).toLowerCase();
		} else {
			logger.error("Unable to contruct an generateEdgeId for " + Arrays.toString(addendIds).toLowerCase());
		}
		return key;
	}

	/**
	 * This is a very important method. Changes to this method will affect which
	 * nodes get joined together, and what constitutes a unique id.
	 * 
	 * @param addendIds
	 * @return
	 */
	protected String generateNodeId(final String... addendIds) {
		String key = null;
		boolean foundValue = false;

		// Allow for null values as part of the id.
		if ((addendIds != null) && (addendIds.length == 1) && ValidationUtils.isValid(addendIds[0])) {
			// removes all non alphanumeric, and converts to lowercase
			key = addendIds[0].replaceAll("[\\W]|_", "").toLowerCase();
			// replace leading zeros as part of the id.
			key = StringUtils.removeLeadingZeros(key);
		} else if ((addendIds != null) && (addendIds.length > 0)) {
			for (final String a : addendIds) {
				// make sure something is non null.
				if ((a != null) && !a.isEmpty()) {
					foundValue = true;
					break;
				}
			}
			if (foundValue) {
				key = Arrays.toString(addendIds).toLowerCase();
			}
		} else {
			logger.error("Unable to contruct an generateNodeId for " + Arrays.toString(addendIds).toLowerCase());
		}
		return key;
	}

	protected String getCombinedSearchLink(final String nodeType, final String identifier) {
		if (linkGenerator != null) {
			// FIXME: Need to find a way to inject the page into the builder, so
			// we can call set()
			// logger.debug("Search page is defined when making a link for " +
			// identifier);
			final Link link = linkGenerator.set(null, null, null, identifier, defaultMaxResults);
			return "<a href=\"" + link.toRedirectURI() + "\" target=\"" + identifier + "\" class=\"btn btn-primary\" >"
					+ identifier + "</a>";
		} else {
			// logger.warn("No linkGenerator search page defined when making a link for "
			// + identifier);
			final String encodedIdentifier = encoder.encode(identifier);
			String matchType = "COMPARE_CONTAINS";
			if (nodeType.contains("ADDRESS")) {
				matchType = "COMPARE_EQUALS";
			}
			return "<a href=\"graphene\\CombinedEntitySearchPage/?term=" + encodedIdentifier + "&match=" + matchType
					+ "\" target=\"" + identifier + "\" class=\"btn btn-primary\" >" + identifier + "</a>";
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see graphene.services.HyperGraphBuilder#getDAO()
	 */
	@Override
	public abstract G_DataAccess getDAO();

	/**
	 * @return the errors
	 */
	@Override
	public final List<G_DocumentError> getErrors() {
		return errors;
	}

	/**
	 * @return the scannedQueries
	 */
	public final Set<String> getScannedQueries() {
		return scannedQueries;
	}

	/**
	 * @return the scannedResults
	 */
	public final Set<String> getScannedResults() {
		return scannedResults;
	}

	@Override
	public void inheritLabelIfNeeded(final V_GenericNode a, final V_GenericNode... nodes) {
		for (final V_GenericNode n : nodes) {
			if ((n != null) && ValidationUtils.isValid(n.getLabel())) {
				a.setLabel(n.getLabel());
				return;
			}
		}
	}

	/**
	 * Returns true if this result id has previously been scanned.
	 * 
	 * @param reportId
	 * @return
	 */
	@Override
	public boolean isPreviouslyScannedResult(final String reportId) {
		return scannedResults.contains(reportId);
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
					getDAO().performCallback(0, eq.getMaxResult(), this, eq);
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
	public boolean removeEdge(final String fromId, final String relationType, final String toId,
			final String relationValue) {
		if (ValidationUtils.isValid(fromId, relationType, toId)) {
			final String key = generateEdgeId(fromId, relationType, toId);
			final V_GenericEdge removedEdge = edgeList.remove(key);
			if (removedEdge != null) {
				return true;
			}
		}
		return false;
	}

	@Deprecated
	public void removeGraphQueryPath(final V_GenericNode reportNode, final G_EntityQuery q) {
		removeEdge(q.getInitiatorId(), G_CanonicalRelationshipType.CONTAINED_IN.name(), reportNode.getId(),
				G_CanonicalRelationshipType.CONTAINED_IN.name());
	}

	/**
	 * @param errors
	 *            the errors to set
	 */
	public final void setErrors(final List<G_DocumentError> errors) {
		this.errors = errors;
	}

	/**
	 * @param scannedQueries
	 *            the scannedQueries to set
	 */
	@Override
	public final void setScannedQueries(final Set<String> scannedQueries) {
		this.scannedQueries = scannedQueries;
	}

	/**
	 * @param scannedResults
	 *            the scannedResults to set
	 */
	@Override
	public final void setScannedResults(final Set<String> scannedResults) {
		this.scannedResults = scannedResults;
	}

}