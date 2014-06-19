package graphene.services;

import graphene.util.G_CallBack;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import mil.darpa.vande.generic.V_EdgeList;
import mil.darpa.vande.generic.V_GenericEdge;
import mil.darpa.vande.generic.V_GenericNode;
import mil.darpa.vande.generic.V_NodeList;

public abstract class AbstractGraphBuilder<T> implements G_CallBack<T> {

	protected V_EdgeList edgeList;
	/**
	 * This field is to inform other services about which data sources can be
	 * graphed using this builder. Each implementation should specify at least
	 * one datasource string that is supported by itself.  
	 */
	protected List<String> supportedDatasets = new ArrayList<String>(1);
	protected Map<String, V_GenericEdge> edgeMap;
	protected ArrayList<V_GenericNode> newNodeList = new ArrayList<V_GenericNode>(
			3);
	protected V_NodeList nodeList;

	public AbstractGraphBuilder() {
		super();
	}

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
		if (ValidationUtils.isValid(addendIds)) {
			key = addendIds.toString();
		}
		return key;
	}

	protected String generateNodeId(String... addendIds) {
		String key = null;
		if (ValidationUtils.isValid(addendIds)) {
			key = addendIds.toString();
		}
		return key;
	}

	public List<String> getSupportedDatasets() {
		return supportedDatasets;
	}

}