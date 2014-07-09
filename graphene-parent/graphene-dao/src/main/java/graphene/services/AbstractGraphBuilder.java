package graphene.services;

import graphene.util.G_CallBack;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import mil.darpa.vande.generic.V_EdgeList;
import mil.darpa.vande.generic.V_GenericEdge;
import mil.darpa.vande.generic.V_GenericNode;
import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.generic.V_NodeList;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public abstract class AbstractGraphBuilder<T> implements G_CallBack<T> {

	protected V_EdgeList edgeList;
	/**
	 * This field is to inform other services about which data sources can be
	 * graphed using this builder. Each implementation should specify at least
	 * one datasource string that is supported by itself.
	 */
	protected List<String> supportedDatasets = new ArrayList<String>(1);
	protected Map<String, V_GenericEdge> edgeMap;
	//TODO: Change this to a V_NodeList or a map so that we don't add the same node to scan within the same iteration
	protected ArrayList<V_GenericNode> unscannedNodeList = new ArrayList<V_GenericNode>(
			3);
	protected V_NodeList nodeList;
	@Inject
	private Logger logger;

	public AbstractGraphBuilder() {
		super();
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
			key = Arrays.toString(addendIds);
		} else {
			logger.error("Unable to contruct an generateEdgeId for "
					+ Arrays.toString(addendIds));
		}
		return key;
	}

	protected String generateNodeId(String... addendIds) {
		String key = null;
		boolean foundValue = false;
		// Allow for null values as part of the id.
		if (addendIds != null && addendIds.length > 0) {
			for (String a : addendIds) {
				// make sure something is non null.
				if (a != null && !a.isEmpty()) {
					foundValue = true;
					break;
				}
			}
			if (foundValue) {
				key = Arrays.toString(addendIds);
			}
		} else {
			logger.error("Unable to contruct an generateNodeId for "
					+ Arrays.toString(addendIds));
		}
		return key;
	}

	public List<String> getSupportedDatasets() {
		return supportedDatasets;
	}

}