package graphene.services;

import graphene.business.commons.DocumentError;
import graphene.dao.G_Parser;
import graphene.model.idl.G_EdgeTypeAccess;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_NodeTypeAccess;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idlhelper.PropertyHelper;
import graphene.model.query.G_CallBack;
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

import mil.darpa.vande.generic.V_EdgeList;
import mil.darpa.vande.generic.V_GenericEdge;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GenericNode;
import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.generic.V_LegendItem;
import mil.darpa.vande.generic.V_NodeList;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.URLEncoder;
import org.slf4j.Logger;

public abstract class AbstractGraphBuilder<T, Q> implements G_CallBack {
	@Inject
	protected G_EdgeTypeAccess edgeTypeAccess;

	@Inject
	protected G_NodeTypeAccess nodeTypeAccess;

	@Inject
	protected URLEncoder encoder;
	public static final int MIN_NODE_SIZE = 16;
	public static final int MAX_NODE_SIZE = 0;

	@Inject
	protected G_PropertyKeyTypeAccess propertyKeyTypeAccess;

	protected V_EdgeList edgeList;

	/**
	 * This field is to inform other services about which data sources can be
	 * graphed using this builder. Each implementation should specify at least
	 * one datasource string that is supported by itself.
	 */
	protected List<String> supportedDatasets = new ArrayList<String>(1);

	protected Map<String, V_GenericEdge> edgeMap = new HashMap<String, V_GenericEdge>();

	protected List<DocumentError> errors = new ArrayList<DocumentError>();

	// TODO: Change this to a FIFO Queue and address any duplicate node issues
	protected Collection<V_GenericNode> unscannedNodeList = new HashSet<V_GenericNode>(3);

	protected Set<String> scannedQueries = new HashSet<String>();

	protected Set<String> scannedResults = new HashSet<String>();

	protected PriorityQueue<G_EntityQuery> queriesToRun = new PriorityQueue<G_EntityQuery>(10, new ScoreComparator());

	protected Stack<G_EntityQuery> queriesToRunNextDegree = new Stack<G_EntityQuery>();
	protected V_NodeList nodeList = new V_NodeList();
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

	public void addError(final DocumentError e) {
		if (ValidationUtils.isValid(e)) {
			errors.add(e);
		}
	}

	public void addReportDetails(final V_GenericNode reportNode, final List<G_Property> props,
			final String reportLinkTitle, final String url) {
		try {
			// for now, prevent the log-based increase on node dimensions
			/*
			 * reportNode.setSize(getLogSize(((Double)
			 * properties.get(DocumentGraphParser.TOTALAMOUNTNBR)).longValue(),
			 * MIN_NODE_SIZE, MAX_NODE_SIZE));
			 */
			reportNode.addData(reportLinkTitle, url);
			reportNode.setLabel((String) PropertyHelper.getPropertyByKey(props, G_Parser.REPORT_LABEL).getRange());
			reportNode
					.addData("Type", (String) PropertyHelper.getPropertyByKey(props, G_Parser.REPORT_TYPE).getRange());
			reportNode.addData("Amount involved",
					(String) PropertyHelper.getPropertyByKey(props, G_Parser.TOTALAMOUNTSTR).getRange());

			final Set<String> datesOfEvents = (Set<String>) PropertyHelper.getPropertyByKey(props,
					G_Parser.DATES_OF_EVENTS).getRange();
			if (ValidationUtils.isValid(datesOfEvents)) {
				for (final String d : datesOfEvents) {
					reportNode.addData("Date of Event", d);
				}
			}

			final Set<String> datesFiled = (Set<String>) PropertyHelper.getPropertyByKey(props, G_Parser.DATES_FILED)
					.getRange();
			if (ValidationUtils.isValid(datesFiled)) {
				for (final String d : datesFiled) {
					reportNode.addData("Date filed", d);
				}
			}
			final Set<String> datesReceived = (Set<String>) PropertyHelper.getPropertyByKey(props,
					G_Parser.DATES_RECEIVED).getRange();
			if (ValidationUtils.isValid(datesReceived)) {
				for (final String d : datesReceived) {
					reportNode.addData("Date received", d);
				}
			}
		} catch (final Exception e) {
			logger.error("addReportDetails " + e.getMessage());
		}
	}

	public void addScannedResult(final String reportId) {
		scannedResults.add(reportId);
	}

	// TODO: We want to remove query path edges if we have other edges going to
	// it.
	public boolean createEdge(final String fromId, final String relationType, final String toId,
			final String relationValue) {
		if (ValidationUtils.isValid(fromId, toId)) {
			final String key = generateEdgeId(fromId, relationType, toId);
			final V_GenericNode a = nodeList.getNode(fromId);
			final V_GenericNode b = nodeList.getNode(toId);
			if (ValidationUtils.isValid(key, a, b) && !edgeMap.containsKey(key)) {

				final V_GenericEdge v = new V_GenericEdge(a, b);
				v.setIdType(relationType);
				v.setLabel(null);
				v.setIdVal(relationType);
				v.addData("Value", StringUtils.coalesc(" ", a.getLabel(), relationValue, b.getLabel()));
				edgeMap.put(key, v);
				return true;
			}
		}
		return false;
	}

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
			logger.debug("Search page is defined when making a link for " + identifier);
			final Link link = linkGenerator.set(null, null, null, identifier, defaultMaxResults);
			return "<a href=\"" + link.toRedirectURI() + "\" target=\"" + identifier + "\" class=\"btn btn-primary\" >"
					+ identifier + "</a>";
		} else {
			logger.warn("No linkGenerator search page defined when making a link for " + identifier);
			final String encodedIdentifier = encoder.encode(identifier);
			String matchType = "COMPARE_CONTAINS";
			if (nodeType.contains("ADDRESS")) {
				matchType = "COMPARE_EQUALS";
			}
			return "<a href=\"graphene\\CombinedEntitySearchPage/?term=" + encodedIdentifier + "&match=" + matchType
					+ "\" target=\"" + identifier + "\" class=\"btn btn-primary\" >" + identifier + "</a>";
		}
	}

	/**
	 * @return the errors
	 */
	public final List<DocumentError> getErrors() {
		return errors;
	}

	/**
	 * Creates a log based integer for node size that is larger than minSize,
	 * and capped at maxSize (if maxSize is non-zero)
	 * 
	 * @param amount
	 * @param minSize
	 * @param maxSize
	 * @return an integer that can be used for sizing nodes on a display
	 */
	protected int getLogSize(final Long amount, final int minSize, final int maxSize) {
		int size = minSize;
		if (ValidationUtils.isValid(amount)) {
			final long additionalPixels = Math.round(Math.log(amount));
			// if we are given a max size, cap the size at that value
			if (maxSize > 0) {
				size = (int) Math.min((additionalPixels + minSize), maxSize);
			} else {
				size = (int) (additionalPixels + minSize);
			}
		}
		return size;
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

	public List<String> getSupportedDatasets() {
		return supportedDatasets;
	}

	/**
	 * Returns true if this result id has previously been scanned.
	 * 
	 * @param reportId
	 * @return
	 */
	public boolean isPreviouslyScannedResult(final String reportId) {
		return scannedResults.contains(reportId);
	}

	public abstract V_GenericGraph makeGraphResponse(V_GraphQuery graphQuery) throws Exception;

	public abstract void performPostProcess(V_GraphQuery graphQuery);

	public boolean removeEdge(final String fromId, final String relationType, final String toId,
			final String relationValue) {
		if (ValidationUtils.isValid(fromId, relationType, toId)) {
			final String key = generateEdgeId(fromId, relationType, toId);
			final V_GenericEdge removedEdge = edgeMap.remove(key);
			if (removedEdge != null) {
				return true;
			}
		}
		return false;
	}

	/**
	 * @param errors
	 *            the errors to set
	 */
	public final void setErrors(final List<DocumentError> errors) {
		this.errors = errors;
	}

	/**
	 * @param scannedQueries
	 *            the scannedQueries to set
	 */
	public final void setScannedQueries(final Set<String> scannedQueries) {
		this.scannedQueries = scannedQueries;
	}

	/**
	 * @param scannedResults
	 *            the scannedResults to set
	 */
	public final void setScannedResults(final Set<String> scannedResults) {
		this.scannedResults = scannedResults;
	}

	/**
	 * @param supportedDatasets
	 *            the supportedDatasets to set
	 */
	void setSupportedDatasets(final List<String> supportedDatasets) {
		this.supportedDatasets = supportedDatasets;
	}

}