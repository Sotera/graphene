package graphene.services;

import graphene.business.commons.DocumentError;
import graphene.model.idl.G_EdgeTypeAccess;
import graphene.model.idl.G_NodeTypeAccess;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.model.query.EntityQuery;
import graphene.util.G_CallBack;
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

public abstract class AbstractGraphBuilder<T> implements G_CallBack<T> {
	@Inject
	protected G_EdgeTypeAccess edgeTypeAccess;

	@Inject
	protected G_NodeTypeAccess nodeTypeAccess;

	@Inject
	protected URLEncoder encoder;

	protected String getCombinedSearchLink(String identifier) {
		String context = encoder.encode(identifier);
		return "<a href=\"CombinedEntitySearchPage/" + context
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
			//removes all non alphanumeric, and converts to lowercase
			key = addendIds[0].replaceAll("[\\W]|_", "").toLowerCase();
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

	public abstract V_GenericGraph makeGraphResponse(V_GraphQuery graphQuery)
			throws Exception;

}