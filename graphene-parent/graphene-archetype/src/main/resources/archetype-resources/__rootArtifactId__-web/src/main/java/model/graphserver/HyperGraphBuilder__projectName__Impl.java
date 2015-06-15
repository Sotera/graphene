#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.graphserver;

import graphene.dao.DocumentBuilder;
import graphene.dao.G_Parser;
import graphene.dao.GraphTraversalRuleService;
import graphene.dao.es.JestModule;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_Constraint;
import graphene.model.idl.G_DataAccess;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SearchResult;
import graphene.model.idlhelper.ListRangeHelper;
import graphene.model.idlhelper.PropertyHelper;
import graphene.model.idlhelper.PropertyMatchDescriptorHelper;
import graphene.model.idlhelper.QueryHelper;
import graphene.model.idlhelper.SingletonRangeHelper;
import graphene.services.AbstractGraphBuilder;
import graphene.services.ScoreComparator;
import graphene.util.DataFormatConstants;
import graphene.util.StringUtils;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
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

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

/**
 * This version uses Elastic Search to dynamically generate a graph, without
 * using an in-memory database.
 * 
 * @author djue
 * 
 */
public class HyperGraphBuilder${projectName}Impl extends AbstractGraphBuilder {

	public static final int MAX_RESULTS = 20;
	public static final double MIN_SCORE = 0.75d;
	private static final boolean CREATE_LINKS = true;
	private static final boolean MARK_START_NODE = true;
	private static final boolean TRIM_UNSHARED_NODES = false;
	protected HashMap<G_CanonicalPropertyType, String> colorMap = new HashMap<G_CanonicalPropertyType, String>();

	@Inject
	@Symbol(JestModule.ES_SEARCH_INDEX)
	private String index;

	@Inject
	private G_DataAccess combinedDAO;

	private ArrayList<String> listOfTypesToAlwaysKeep;

	@Inject
	private Logger logger;

	private final Map<String, Integer> traversalDepthMap = new HashMap<String, Integer>();

	@Inject
	private GraphTraversalRuleService ruleService;

	@Inject
	private DocumentBuilder db;

	public HyperGraphBuilder${projectName}Impl() {

		// constant legend items regardless of what other node types are present
		// in graph
		// XXX: fix this, pull the highlight color and selected color from the
		// styleservice (inject it)
		legendItems.add(new V_LegendItem("${symbol_pound}a90329", "Item you searched for."));
		legendItems.add(new V_LegendItem("darkblue", "Selected item(s)."));

		setupTrimmingOptions();
		setupNodeInheritance();
	}

	@Override
	public V_GenericEdge createEdge(final V_GenericNode a, final String relationType, final String relationValue,
			final V_GenericNode attachTo, final double localPriority, final double inheritedScore,
			final double nodeCertainty, final double minimumScoreRequired) {
		V_GenericEdge edge = null;
		if (ValidationUtils.isValid(attachTo)) {
			final String key = generateEdgeId(attachTo.getId(), relationType, a.getId());
			if ((key != null) && !edgeList.containsKey(key)) {
				edge = new V_GenericEdge(key, a, attachTo);
				edge.setIdType(relationType);
				edge.setLabel(null);
				edge.setIdVal(relationType);
				if (nodeCertainty < 100.0) {
					edge.addData("Certainty", DataFormatConstants.formatPercent(nodeCertainty));
					edge.setLineStyle("dotted");
					// edge.setColor("${symbol_pound}787878");
				}
				// if this is a LIKE edge
				if (relationType.equals(G_CanonicalRelationshipType.LIKES.name())) {
					edge.setColor("blue");
					edge.setLabel("+1");

					// if this is an OWNER_OF edge that is connected to
					// a "MEDIA" node...
				} else if (relationType.equals(G_CanonicalRelationshipType.OWNER_OF.name())
						&& (attachTo.getIdType().equals(G_CanonicalPropertyType.MEDIA.name()) || a.getIdType().equals(
								G_CanonicalPropertyType.MEDIA.name()))) {
					edge.setColor("green");
					edge.setCount(3);
				}
				edge.addData("Local_Priority", "" + localPriority);
				edge.addData("Min_Score_Required", "" + minimumScoreRequired);
				edge.addData("Parent_Score", "" + inheritedScore);
				edge.addData("Value", StringUtils.coalesc(" ", a.getLabel(), relationValue, attachTo.getLabel()));
				edgeList.put(key, edge);
			}

			// if this flag is set, we'll add the attributes to the
			// attached node.
			if (inheritAttributes) {
				attachTo.inheritPropertiesOfExcept(a, skipInheritanceTypes);
			}
		}
		return edge;
	}

	/**
	 * Creates one or more queries based on data within a specific node.
	 * 
	 * @param n
	 * @return
	 */
	@Override
	public List<G_EntityQuery> createQueriesFromNode(final V_GenericNode n) {
		final List<G_EntityQuery> list = new ArrayList<G_EntityQuery>(2);

		final PropertyMatchDescriptorHelper pmdh = new PropertyMatchDescriptorHelper();
		pmdh.setKey("_all");
		pmdh.setSingletonRange(new SingletonRangeHelper(n.getIdVal(), G_PropertyType.STRING));
		pmdh.setConstraint(ruleService.getRule(n.getIdType()));

		final QueryHelper qh = new QueryHelper(pmdh);
		// if (isUserExists()) {
		// qh.setUserId(getUser().getId());
		// qh.setUsername(getUser().getUsername());
		// }
		qh.setMinimumScore(1.0d);
		qh.setMaxResult((long) MAX_RESULTS);
		qh.setMinimumScore(n.getMinScore());
		qh.setInitiatorId(n.getId());
		list.add(qh);
		return list;
	}

	@Override
	public boolean execute(final G_SearchResult t, final G_EntityQuery q) {
		if (ValidationUtils.isValid(t.getResult())) {
			final G_Entity entity = (G_Entity) t.getResult();
			final String type = (String) PropertyHelper.getSingletonValue(entity.getProperties().get(
					G_Parser.REPORT_TYPE));

			final G_Parser parser = db.getParserForObject(type);
			if (parser != null) {
				return parser.parse(t, q);
			} else {
				logger.warn("No parser was found for the supplied type, but carrying on.");
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public G_DataAccess getDAO() {
		return combinedDAO;
	}

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
				.setListRange(new ListRangeHelper(G_PropertyType.STRING, graphQuery.getSearchIds()))
				.setConstraint(G_Constraint.EQUALS).build();
		final QueryHelper qh = new QueryHelper(identifierList);
		qh.setTargetSchema(index);
		queriesToRun.add(qh);

		int currentDegree = 0;
		for (currentDegree = 0; (currentDegree < graphQuery.getMaxHops())
				&& (nodeList.size() < graphQuery.getMaxNodes()); currentDegree++) {
			G_EntityQuery eq = null;
			logger.debug("${symbol_dollar}${symbol_dollar}${symbol_dollar}${symbol_dollar}There are " + queriesToRun.size() + " queries to run in the current degree.");
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

	@Override
	public void performPostProcess(final V_GraphQuery graphQuery) {
		logger.debug("Before post process, node list is size " + nodeList.size());
		logger.debug("Before post process, edge list is size " + edgeList.size());

		V_GenericNode startNode = null;

		// mandatory now. you'll see why down below
		// if (MARK_START_NODE) {
		for (final V_GenericNode n : nodeList.values()) {
			for (final String queryId : graphQuery.getSearchIds()) {
				final String a = n.getLabel().toLowerCase().trim();
				final String c = n.getDataValue("text");
				final String b = queryId.toLowerCase().trim();
				if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(a, b)
						|| org.apache.commons.lang3.StringUtils.containsIgnoreCase(b, a)) {
					n.setColor(style.getHighlightBackgroundColor());
					if ((startNode == null) && (n.getNodeType() == "REPORT_ID")) {
						startNode = n;
					}
				} else if ((c != null) && org.apache.commons.lang3.StringUtils.containsIgnoreCase(c, b)) {
					n.setColor(style.getHighlightBackgroundColor());
					if ((startNode == null) && (n.getNodeType() == "REPORT_ID")) {
						startNode = n;
					}
				}
				// n.addData("Label", a);

			}

		}
		// }
		if (TRIM_UNSHARED_NODES) {

			final Map<String, V_GenericNode> newNodeList = new HashMap<String, V_GenericNode>();
			final Map<String, Integer> countMap = new HashMap<String, Integer>();
			final Map<String, V_GenericEdge> newEdgeList = new HashMap<String, V_GenericEdge>();

			/*
			 * First we iterate over all the edges. Each time we see a node as
			 * either a source or target, we increment it's count.
			 */
			for (final V_GenericEdge e : edgeList.values()) {
				final String s = e.getSourceId();
				final String t = e.getTargetId();
				final Integer sCount = countMap.get(s);
				if (sCount == null) {
					countMap.put(s, 1);
				} else {
					countMap.put(s, sCount + 1);
				}
				final Integer tCount = countMap.get(t);
				if (tCount == null) {
					countMap.put(t, 1);
				} else {
					countMap.put(t, tCount + 1);
				}
			}

			for (final V_GenericEdge e : edgeList.values()) {
				boolean keepEdge = true;
				boolean keepTarget = true;
				boolean keepSource = true;
				final String s = e.getSourceId();
				final String t = e.getTargetId();
				if (countMap.get(s) == 1) {
					final V_GenericNode n = nodeList.get(s);
					if (n != null) {
						// If the type is not something we always have to keep,
						// then mark the node and this edge to be pruned.
						if (!listOfTypesToAlwaysKeep.contains(n.getIdType())) {
							keepSource = false;
							keepEdge = false;
						}
					} else {
						logger.error("Node for source id " + s + " was null");
					}
				}
				if (countMap.get(t) == 1) {
					final V_GenericNode n = nodeList.get(t);
					if (n != null) {
						if (!listOfTypesToAlwaysKeep.contains(n.getIdType())) {
							keepTarget = false;
							keepEdge = false;
						}
					} else {
						logger.error("Node for target id " + t + " was null");
					}
				}
				if (keepEdge == true) {
					// if
					// (e.getIdVal().equals(G_CanonicalRelationshipType.CONTAINED_IN.name()))
					// {
					// e.setLineStyle("dotted");
					// e.setWeight(50);
					// }
					// newEdgeList.addEdge(e);
					if (!e.getIdVal().equals(G_CanonicalRelationshipType.CONTAINED_IN.name())) {
						newEdgeList.put(e.getId(), e);
					}
				}
				if (keepSource == true) {
					newNodeList.put(s, nodeList.get(s));
				}
				if (keepTarget == true) {
					newNodeList.put(t, nodeList.get(t));
				}
			}

			// TODO: remove legend items for node types that are no longer
			// present in graph

			nodeList = newNodeList;
			edgeList = newEdgeList;
			logger.debug("New node list is size " + nodeList.size());
			logger.debug("New edge list is size " + edgeList.size());
		}

	}

	@PostInjection
	public void setup() {

	}

	private void setupNodeInheritance() {
		skipInheritanceTypes = new ArrayList<String>();
		skipInheritanceTypes.add("ENTITY");
		// skipInheritanceTypes.add("IMAGE");
	}

	public void setupTrimmingOptions() {
		listOfTypesToAlwaysKeep = new ArrayList<String>();
		// listOfTypesToAlwaysKeep.add(G_CanonicalPropertyType.ACCOUNT.name());
		listOfTypesToAlwaysKeep.add(G_CanonicalPropertyType.CUSTOMER_NUMBER.name());
		listOfTypesToAlwaysKeep.add(G_CanonicalPropertyType.ENTITY.name());
		listOfTypesToAlwaysKeep.add(G_CanonicalPropertyType.REPORT_ID.name());
	}
}
