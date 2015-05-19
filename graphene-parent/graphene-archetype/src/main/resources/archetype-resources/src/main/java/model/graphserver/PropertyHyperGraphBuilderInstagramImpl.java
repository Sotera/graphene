package ${package}.model.graphserver;

import graphene.business.commons.DocumentError;
import graphene.dao.CombinedDAO;
import graphene.dao.DocumentGraphParser;
import graphene.dao.GenericDAO;
import ${package}.dao.GraphTraversalRuleService;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_IdType;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.query.EntityQuery;
import graphene.services.PropertyHyperGraphBuilder;
import graphene.util.DataFormatConstants;
import graphene.util.StringUtils;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import mil.darpa.vande.generic.V_EdgeList;
import mil.darpa.vande.generic.V_GenericEdge;
import mil.darpa.vande.generic.V_GenericNode;
import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.generic.V_LegendItem;
import mil.darpa.vande.generic.V_NodeList;

import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.slf4j.Logger;

/**
 * This version uses Elastic Search to dynamically generate a graph, without
 * using an in-memory database.
 * 
 * @author djue
 * 
 */
public class PropertyHyperGraphBuilderInstagramImpl extends PropertyHyperGraphBuilder<Object> {

	public static final int MAX_RESULTS = 20;
	public static final double MIN_SCORE = 0.75d;
	private static final boolean CREATE_LINKS = true;
	private static final boolean MARK_START_NODE = true;
	private static final boolean TRIM_UNSHARED_NODES = false;
	protected HashMap<G_CanonicalPropertyType, String> colorMap = new HashMap<G_CanonicalPropertyType, String>();

	@Inject
	private CombinedDAO combinedDAO;

	private ArrayList<String> listOfTypesToAlwaysKeep;

	@Inject
	private Logger logger;

	private final Map<String, Integer> traversalDepthMap = new HashMap<String, Integer>();

	@Inject
	private GraphTraversalRuleService ruleService;

	/*
	 * Note: Must implement the LinkGenerator interface
	 */
	// @InjectPage
	// private CombinedEntitySearchPage searchPage;

	public PropertyHyperGraphBuilderInstagramImpl(final Collection<DocumentGraphParser> singletons) {
		super(singletons);
		// constant legend items regardless of what other node types are present
		// in graph
		// XXX: fix this, pull the highlight color and selected color from the
		// styleservice (inject it)
		legendItems.add(new V_LegendItem("#a90329", "Item you searched for."));
		legendItems.add(new V_LegendItem("darkblue", "Selected item(s)."));
		supportedDatasets.add("Instagram");
		setupTraversalDepths();
		setupTrimmingOptions();
		setupNodeInheritance();
		// linkGenerator = searchPage;

	}

	/**
	 * We can build either one query per attribute, or one query per set of
	 * related attributes, depending on the backend.
	 */
	@Override
	public void buildQueryForNextIteration(final V_GenericNode... nodes) {
		for (final V_GenericNode n : nodes) {
			if (determineTraversability(n)) {

				for (final G_EntityQuery eq : createQueriesFromNode(n)) {
					final String queryToString = eq.getPropertyMatchDescriptors().get(0).getValue();
					// Have we done this EXACT query before?
					if (!scannedQueries.contains(queryToString)) {

						/**
						 * TODO: Sort the queries by the score of the query that
						 * go to them.
						 * 
						 * For instance, if i search for x and get a result with
						 * a score of 0.8, then any queries I build from nodes
						 * in that graph get a priority of 0.8 in relation to
						 * other queries.
						 * 
						 * If the second result for x has a score of 0.25, then
						 * queries generated from those nodes get a 0.25
						 * priority.
						 * 
						 * 
						 * The list should stay sorted so that the highest score
						 * is picked for that iteration.
						 * 
						 * This affects how the graph is traversed.
						 * 
						 * We probably don't want to update the sorting after
						 * each query, but after a certain number of queries has
						 * been done on the current degree (possibly exhausting
						 * the list of queries)
						 * 
						 * Another tune is to cull (or not create queries) for
						 * priorities below a certain level.
						 * 
						 * We also need to store the score in the dotted link
						 * edges.
						 */

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

	@Override
	public boolean callBack(final Object p) {

		if (ValidationUtils.isValid(p)) {
			final DocumentGraphParser parser = getParserForObject(p);
			if (parser != null) {
				return parser.parse(p, null);
			} else {
				logger.warn("No parser was found for the supplied type, but carrying on.");
				return true;
			}
		} else {
			return false;
		}
	}

	@Override
	public boolean callBack(final Object p, final G_EntityQuery q) {
		if (ValidationUtils.isValid(p)) {
			final DocumentGraphParser parser = getParserForObject(p);
			if (parser != null) {
				return parser.parse(p, q);
			} else {
				logger.warn("No parser was found for the supplied type, but carrying on.");
				return true;
			}
		} else {
			return false;
		}
	}

	/**
	 * Creates one or more queries based on data within a specific node.
	 * 
	 * @param n
	 * @return
	 */
	private List<EntityQuery> createQueriesFromNode(final V_GenericNode n) {
		final List<EntityQuery> list = new ArrayList<EntityQuery>(2);
		final G_EntityQuery eq =  G_EntityQuery.newBuilder();
		final G_PropertyMatchDescriptor<String> tuple = new G_PropertyMatchDescriptor<>();
		tuple.setValue(n.getIdVal());
		tuple.setConstraint(ruleService.getRule(n.getIdType()));
		final G_IdType type = new G_IdType();
		type.setName(n.getNodeType());
		tuple.setNodeType(type);
		eq.addAttribute(tuple);
		eq.setMaxResult(MAX_RESULTS);
		eq.setMinimumScore(n.getMinScore());
		eq.setInitiatorId(n.getId());
		list.add(eq);
		// make second query here, stripping leading zeroes.
		if (n.getIdVal().startsWith("0")) {
			final G_EntityQuery eq2 =  G_EntityQuery.newBuilder();
			final G_PropertyMatchDescriptor<String> tuple2 = new G_PropertyMatchDescriptor<>();
			tuple2.setValue(StringUtils.removeLeadingZeros(n.getIdVal()));
			tuple2.setConstraint(ruleService.getRule(n.getIdType()));
			final G_IdType type2 = new G_IdType();
			type2.setName(n.getNodeType());
			tuple2.setNodeType(type2);
			eq2.addAttribute(tuple2);
			eq2.setMaxResult(MAX_RESULTS);
			eq2.setMinimumScore(n.getMinScore());
			eq2.setInitiatorId(n.getId());
			list.add(eq2);
		}
		// make a second query here, stripping leading ones for phone types.
		if (n.getIdType().equals(G_CanonicalPropertyType.PHONE.name())) {
			final G_EntityQuery eq2 =  G_EntityQuery.newBuilder();
			final G_PropertyMatchDescriptor<String> tuple2 = new G_PropertyMatchDescriptor<>();
			if (n.getIdVal().startsWith("1")) {
				// try without 1 code
				tuple2.setValue(n.getIdVal().replaceFirst("1", ""));
			} else {
				// try with 1 code
				tuple2.setValue("1" + n.getIdVal());
			}
			tuple2.setConstraint(ruleService.getRule(n.getIdType()));
			final G_IdType type2 = new G_IdType();
			type2.setName(n.getNodeType());
			tuple2.setNodeType(type2);
			eq2.addAttribute(tuple2);
			eq2.setMaxResult(MAX_RESULTS);
			eq2.setMinimumScore(n.getMinScore());
			eq2.setInitiatorId(n.getId());
			list.add(eq2);
		}
		return list;
	}

	@Override
	public boolean determineTraversability(final V_GenericNode n) {
		if (ValidationUtils.isValid(n)) {
			final Integer integer = traversalDepthMap.get(n.getNodeType());
			if ((integer == null) || (integer > 0)) {
				return true;
			} else {
				return false;
			}
		} else {
			logger.warn("An invalid node was provided, and determineTraversability will return false.");
			return false;
		}
	}

	@Override
	public GenericDAO<Object, G_EntityQuery> getDAO() {
		return combinedDAO;
	}

	@Override
	public DocumentGraphParser getParserForObject(final Object obj) {
		DocumentGraphParser dgp = null;
		if (obj == null) {
			logger.warn("Object was invalid");

		} else {
			for (final DocumentGraphParser s : singletons) {
				if (s.getSupportedObjects().contains(obj.getClass().getCanonicalName())) {
					//logger.debug("Found DocumentGraphParser which supports " + s.getSupportedObjects());
					dgp = s;
				}
			}
		}
		if (dgp == null) {
			logger.error("No handler for class " + obj);
		}
		return dgp;
	}

	@Override
	public void performPostProcess(final V_GraphQuery graphQuery) {
		logger.debug("Before post process, node list is size " + nodeList.size());
		logger.debug("Before post process, edge list is size " + edgeList.size());
		if (MARK_START_NODE) {
			for (final V_GenericNode n : nodeList.getNodes()) {
				for (final String queryId : graphQuery.getSearchIds()) {
					final String a = n.getLabel().toLowerCase().trim();
					final String c = n.getDataValue("text");
					final String b = queryId.toLowerCase().trim();
					if (org.apache.commons.lang3.StringUtils.containsIgnoreCase(a, b)
							|| org.apache.commons.lang3.StringUtils.containsIgnoreCase(b, a)) {
						n.setColor(style.getHighlightBackgroundColor());
					} else if ((c != null) && org.apache.commons.lang3.StringUtils.containsIgnoreCase(c, b)) {
						n.setColor(style.getHighlightBackgroundColor());
					}
					// n.addData("Label", a);

				}

			}
		}
		if (TRIM_UNSHARED_NODES) {

			final V_NodeList newNodeList = new V_NodeList();
			final Map<String, Integer> countMap = new HashMap<String, Integer>();
			final V_EdgeList newEdgeList = new V_EdgeList(null);

			/*
			 * First we iterate over all the edges. Each time we see a node as
			 * either a source or target, we increment it's count.
			 */
			for (final V_GenericEdge e : edgeList.getEdges()) {
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

			for (final V_GenericEdge e : edgeList.getEdges()) {
				boolean keepEdge = true;
				boolean keepTarget = true;
				boolean keepSource = true;
				final String s = e.getSourceId();
				final String t = e.getTargetId();
				if (countMap.get(s) == 1) {
					final V_GenericNode n = nodeList.getNode(s);
					if (n != null) {
						final String sType = nodeList.getNode(s).getIdType();
						// If the type is not something we always have to keep,
						// then mark the node and this edge to be pruned.
						if (!listOfTypesToAlwaysKeep.contains(sType)) {
							keepSource = false;
							keepEdge = false;
						}
					} else {
						logger.error("Node for source id " + s + " was null");
					}
				}
				if (countMap.get(t) == 1) {
					final V_GenericNode n = nodeList.getNode(t);
					if (n != null) {
						final String tType = nodeList.getNode(t).getIdType();
						if (!listOfTypesToAlwaysKeep.contains(tType)) {
							keepTarget = false;
							keepEdge = false;
						}
					} else {
						logger.error("Node for target id " + t + " was null");
					}
				}
				if (keepEdge == true) {
					if (e.getIdVal().equals(G_CanonicalRelationshipType.CONTAINED_IN.name())) {
						e.setLineStyle("dotted");
						e.setWeight(50);
					}
					newEdgeList.addEdge(e);
				}
				if (keepSource == true) {
					newNodeList.addNode(nodeList.getNode(s));
				}
				if (keepTarget == true) {
					newNodeList.addNode(nodeList.getNode(t));
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

	@Override
	public V_GenericNode createOrUpdateNode(final double minimumScoreRequired, final double inheritedScore,
			final double localPriority, final String originalId, final String idType, final String nodeType,
			final V_GenericNode attachTo, final String relationType, final String relationValue,
			final double nodeCertainty) {
		V_GenericNode a = null;

		if (ValidationUtils.isValid(originalId)) {
			if (!stopwordService.isValid(originalId)) {
				addError(new DocumentError("Bad Identifier", "The " + nodeType + " (" + originalId + ") contains a stopword", Severity.WARN));
			} else {
				final String id = generateNodeId(originalId);
				a = nodeList.getNode(id);
				final double calculatedPriority = inheritedScore * localPriority;
				if (a == null) {
					a = new V_GenericNode(id);
					a.setIdType(idType);
					// This is important because we use it to search on the next traversal.
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
				if (ValidationUtils.isValid(attachTo)) {
					final String key = generateEdgeId(attachTo.getId(), relationType, a.getId());
					if ((key != null) && !edgeMap.containsKey(key)) {
						final V_GenericEdge edge = new V_GenericEdge(a, attachTo);
						edge.setIdType(relationType);
						edge.setLabel(null);
						edge.setIdVal(relationType);
						if (nodeCertainty < 100.0) {
							edge.addData("Certainty", DataFormatConstants.formatPercent(nodeCertainty));
							edge.setLineStyle("dotted");
							// edge.setColor("#787878");
						}
						// if this is a LIKE edge
						if (relationType.equals(G_CanonicalRelationshipType.LIKES.name())) {
							edge.setColor("blue");
							edge.setLabel("+1");
						
						// if this is an OWNER_OF edge that is connected to a "MEDIA" node... 
						} else if (relationType.equals(G_CanonicalRelationshipType.OWNER_OF.name()) 
							&& (attachTo.getIdType().equals(G_CanonicalPropertyType.MEDIA.name()) 
								|| a.getIdType().equals(G_CanonicalPropertyType.MEDIA.name()))) {
							edge.setColor("green");
							edge.setCount(3);
						}
						edge.addData("Local_Priority", "" + localPriority);
						edge.addData("Min_Score_Required", "" + minimumScoreRequired);
						edge.addData("Parent_Score", "" + inheritedScore);
						edge.addData("Value", StringUtils.coalesc(" ", a.getLabel(), relationValue, attachTo.getLabel()));
						edgeMap.put(key, edge);
					}

					// if this flag is set, we'll add the attributes to the attached node.
					if (/*INHERIT_ATTRIBUTES*/ true) {
						attachTo.inheritPropertiesOfExcept(a, skipInheritanceTypes);
					}
				}
			}
		} else {
			logger.error("Invalid id for " + nodeType + " of node " + attachTo);
		}
		return a;
	}
	
	private void setupNodeInheritance() {
		// TODO Auto-generated method stub
		skipInheritanceTypes = new ArrayList<String>();
		skipInheritanceTypes.add("ENTITY");
	}

	private void setupTraversalDepths() {
		// TODO Auto-generated method stub
		traversalDepthMap.put(G_CanonicalPropertyType.TIME_DATE.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.GEO.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.ADDRESS_BLDG.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.ADDRESS_CITY.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.ADDRESS_POSTAL_CODE.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.OCCUPATION.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.CURRENCY.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.METRIC_CERTAINTY.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.ACCOUNT_TYPE.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.METRIC_IMPUTED.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.METRIC_IMPUTEDFROM.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.METRIC_PROVENANCE.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.METRIC_SCORE.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.FAMILYROLE.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.SEX.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.IMPORTANCE.name(), 0);
		traversalDepthMap.put(G_CanonicalPropertyType.TAXID.name(), 10);
		// This is special, we really need to create something called
		// "artificial entity" or "proxy entity" because it's value won't ever
		// be found elsewhere.
		traversalDepthMap.put(G_CanonicalPropertyType.ENTITY.name(), 0);
	}

	public void setupTrimmingOptions() {
		listOfTypesToAlwaysKeep = new ArrayList<String>();
		// listOfTypesToAlwaysKeep.add(G_CanonicalPropertyType.ACCOUNT.name());
		listOfTypesToAlwaysKeep.add(G_CanonicalPropertyType.CUSTOMER_NUMBER.name());
		listOfTypesToAlwaysKeep.add(G_CanonicalPropertyType.ENTITY.name());
		listOfTypesToAlwaysKeep.add(G_CanonicalPropertyType.REPORT_ID.name());
	}
}
