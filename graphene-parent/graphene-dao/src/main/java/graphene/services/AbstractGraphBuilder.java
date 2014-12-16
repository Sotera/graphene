package graphene.services;

import graphene.business.commons.DocumentError;
import graphene.dao.DocumentGraphParser;
import graphene.model.idl.G_EdgeTypeAccess;
import graphene.model.idl.G_NodeTypeAccess;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.model.query.EntityQuery;
import graphene.util.G_CallBack;
import graphene.util.StringUtils;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Stack;

import mil.darpa.vande.generic.V_EdgeList;
import mil.darpa.vande.generic.V_GenericEdge;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GenericNode;
import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.generic.V_LegendItem;
import mil.darpa.vande.generic.V_NodeList;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.URLEncoder;
import org.slf4j.Logger;

public abstract class AbstractGraphBuilder<T, Q> implements G_CallBack<T, Q> {
	@Inject
	protected G_EdgeTypeAccess edgeTypeAccess;

	@Inject
	protected G_NodeTypeAccess nodeTypeAccess;

	@Inject
	protected URLEncoder encoder;
	public static final int MIN_NODE_SIZE = 16;
	public static final int MAX_NODE_SIZE = 0;

	protected String getCombinedSearchLink(String identifier) {
		String context = encoder.encode(identifier);
		return "<a href=\"graphene\\CombinedEntitySearchPage/" + context
				+ "\" class=\"btn btn-primary\" >" + identifier + "</a>";
	}

	/**
	 * @return the errors
	 */
	public final List<DocumentError> getErrors() {
		return errors;
	}

	/**
	 * @param errors
	 *            the errors to set
	 */
	public final void setErrors(List<DocumentError> errors) {
		this.errors = errors;
	}

	/**
	 * Returns true if this result id has previously been scanned.
	 * 
	 * @param reportId
	 * @return
	 */
	public boolean isPreviouslyScannedResult(String reportId) {
		return scannedResults.contains(reportId);
	}

	public void addScannedResult(String reportId) {
		scannedResults.add(reportId);
	}

	/**
	 * @return the scannedResults
	 */
	public final Set<String> getScannedResults() {
		return scannedResults;
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
	 * @param scannedResults
	 *            the scannedResults to set
	 */
	public final void setScannedResults(Set<String> scannedResults) {
		this.scannedResults = scannedResults;
	}

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
	protected Collection<V_GenericNode> unscannedNodeList = new HashSet<V_GenericNode>(
			3);
	protected Set<String> scannedQueries = new HashSet<String>();
	protected Set<String> scannedResults = new HashSet<String>();
	protected Stack<EntityQuery> queriesToRun = new Stack<EntityQuery>();
	protected Stack<EntityQuery> queriesToRunNextDegree = new Stack<EntityQuery>();
	protected V_NodeList nodeList = new V_NodeList();
	protected ArrayList<V_LegendItem> legendItems = new ArrayList<V_LegendItem>();
	@Inject
	private Logger logger;

	public AbstractGraphBuilder() {
		super();
	}

	public void addError(DocumentError e) {
		if (ValidationUtils.isValid(e)) {
			errors.add(e);
		}
	}

	public abstract void performPostProcess(V_GraphQuery graphQuery);

	/**
	 * @param supportedDatasets
	 *            the supportedDatasets to set
	 */
	void setSupportedDatasets(List<String> supportedDatasets) {
		this.supportedDatasets = supportedDatasets;
	}

	/**
	 * Doesn't matter what this does, as long as it is unique.
	 * 
	 * @param v
	 * @return
	 */
	protected String generateEdgeId(String... addendIds) {
		String key = null;
		// Allow for null values as part of the id.
		if (addendIds != null && addendIds.length > 0) {
			key = Arrays.toString(addendIds).toLowerCase();
		} else {
			logger.error("Unable to contruct an generateEdgeId for "
					+ Arrays.toString(addendIds).toLowerCase());
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
	protected String generateNodeId(String... addendIds) {
		String key = null;
		boolean foundValue = false;

		// Allow for null values as part of the id.
		if (addendIds != null && addendIds.length == 1
				&& ValidationUtils.isValid(addendIds[0])) {
			// removes all non alphanumeric, and converts to lowercase
			key = addendIds[0].replaceAll("[\\W]|_", "").toLowerCase();
			// replace leading zeros as part of the id.
			key = StringUtils.removeLeadingZeros(key);
		} else if (addendIds != null && addendIds.length > 0) {
			for (String a : addendIds) {
				// make sure something is non null.
				if (a != null && !a.isEmpty()) {
					foundValue = true;
					break;
				}
			}
			if (foundValue) {
				key = Arrays.toString(addendIds).toLowerCase();
			}
		} else {
			logger.error("Unable to contruct an generateNodeId for "
					+ Arrays.toString(addendIds).toLowerCase());
		}
		return key;
	}

	public List<String> getSupportedDatasets() {
		return supportedDatasets;
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
	protected int getLogSize(Long amount, int minSize, int maxSize) {
		int size = minSize;
		if (ValidationUtils.isValid(amount)) {
			long l = Math.round(0.5 * Math.log(amount));
			// if we are given a max size, cap the size at that value
			if (maxSize > 0) {
				size = (int) Math.min((l + minSize), maxSize);
			} else {
				size = (int) (l + minSize);
			}
		}
		return size;
	}

	public abstract V_GenericGraph makeGraphResponse(V_GraphQuery graphQuery)
			throws Exception;

	public void addReportDetails(V_GenericNode reportNode,
			Map<String, Object> properties) {
		try {
			reportNode.setSize(getLogSize(
					(Long) properties.get(DocumentGraphParser.TOTALAMOUNTNBR),
					MIN_NODE_SIZE, MAX_NODE_SIZE));

			reportNode
					.addData("Amount involved", (String) properties
							.get(DocumentGraphParser.TOTALAMOUNTSTR));

			List<String> datesOfEvents = (List<String>) properties
					.get(DocumentGraphParser.DATES_OF_EVENTS);
			if (ValidationUtils.isValid(datesOfEvents)) {
				for (String d : datesOfEvents) {
					reportNode.addData("Date of Event", d);
				}
			}

			List<String> datesFiled = (List<String>) properties
					.get(DocumentGraphParser.DATES_FILED);
			if (ValidationUtils.isValid(datesFiled)) {
				for (String d : datesOfEvents) {
					reportNode.addData("Date filed", d);
				}
			}
			List<String> datesReceived = (List<String>) properties
					.get(DocumentGraphParser.DATES_RECEIVED);
			if (ValidationUtils.isValid(datesReceived)) {
				for (String d : datesOfEvents) {
					reportNode.addData("Date received", d);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
	}

	public boolean createEdge(String fromId, String relationType, String toId,
			String relationValue) {
		String key = generateEdgeId(fromId, relationType, toId);
		V_GenericNode a = nodeList.getNode(fromId);
		V_GenericNode b = nodeList.getNode(toId);
		if (ValidationUtils.isValid(key, a, b) && !edgeMap.containsKey(key)) {

			V_GenericEdge v = new V_GenericEdge(a, b);
			v.setIdType(relationType);
			v.setLabel(null);
			v.setIdVal(relationType);
			v.addData(
					"Value",
					StringUtils.coalesc(" ", a.getLabel(), relationValue,
							b.getLabel()));
			edgeMap.put(key, v);
			return true;
		}
		return false;
	}

}