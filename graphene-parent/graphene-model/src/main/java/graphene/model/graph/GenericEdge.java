package graphene.model.graph;

import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author PWG
 * 
 */

public class GenericEdge {

	protected static final Logger logger = LoggerFactory
			.getLogger(GenericEdge.class);
	private GenericNode sourceNode;
	private GenericNode targetNode;
	private Set<GraphObjectData> dataSet = new HashSet<GraphObjectData>();

	private String source = "src";
	private String target = "target";
	private int degree = 0;
	private String label;
	private boolean directed = false;
	private int count = 1;
	private String amount = ""; // to be a serialized amount to hold aggregated
								// values
								// for the total interactions between the nodes

	private int weight = 1;

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public GenericEdge() {

	}

	public GenericEdge(GenericNode src_node, GenericNode target_node, int degree) {
		this.source = src_node.getId();
		this.target = target_node.getId();
		this.sourceNode = src_node;
		this.targetNode = target_node;
		this.degree = degree;
		this.count = degree;
	}

	public GenericEdge(GenericNode src_node, GenericNode target_node,
			int degree, boolean directed) {
		this.source = src_node.getId();
		this.target = target_node.getId();
		this.sourceNode = src_node;
		this.targetNode = target_node;
		this.degree = degree;
		this.directed = directed;
	}

	// MFM added
	public GenericEdge(GenericNode src_node, GenericNode target_node,
			int degree, int weight, boolean directed) {
		this.source = src_node.getId();
		this.target = target_node.getId();
		this.sourceNode = src_node;
		this.targetNode = target_node;
		this.degree = degree;
		this.weight = weight;
		this.directed = directed;
	}

	public GenericEdge(String src_node_id, String target_node_id, int degree) {
		this.source = src_node_id;
		this.target = target_node_id;
		this.degree = degree;
	}

	public void addData(String name, String value) {
		// We can have two entries for an identifier name. But SnagL barfs
		// if it gets two entries for the same name. So we kludge the name
		// here
		boolean found = false;
		StringBuffer buf = new StringBuffer();
		buf.append(name);
		do {
			found = false;
			for (GraphObjectData d : dataSet) {
				if (d.key.equals(name)) {
					found = true;
					buf.append("_");
					break;
				}
			}
		} while (found);

		dataSet.add(new GraphObjectData(buf.toString(), value));
	}

	/**
	 * @return the dataSet
	 */
	public final Set<GraphObjectData> getDataSet() {
		return dataSet;
	}

	/**
	 * @return the degree
	 */
	public final int getDegree() {
		return degree;
	}

	public String getLabel() {
		return label;
	}

	public final String getSource() {
		return source;
	}

	/**
	 * @return the sourceNode
	 */
	public final GenericNode getSourceNode() {
		return sourceNode;
	}

	public final String getTarget() {
		return target;
	}

	/**
	 * @return the targetNode
	 */
	public final GenericNode getTargetNode() {
		return targetNode;
	}

	/**
	 * Sometimes Gephi will not read in the edge weights if the column is named
	 * weight rather than Weight
	 * 
	 * @return
	 */

	public int getWeight() {
		return weight;
	}

	public int getCount() {
		return count;
	}

	private void removeData(String type) {
		Object l = null;

		for (GraphObjectData d : dataSet) {
			if (d.key.equals(type)) {
				l = d;
				break;
			}
		}
		if (l != null)
			dataSet.remove(l);
	}

	/**
	 * @param dataSet
	 *            the dataSet to set
	 */
	public final void setDataSet(Set<GraphObjectData> dataSet) {
		this.dataSet = dataSet;
	}

	public String getAmount() {
		return amount;
	}

	public void setAmount(String amount) {
		this.amount = amount;
	}

	/**
	 * @param degree
	 *            the degree to set
	 */
	public final void setDegree(int degree) {
		this.degree = degree;
	}

	public void setLabel(String label) {

		removeData("label");
		removeData("edge-prop-DisplayValue");

		logger.debug("Setting Edge label to " + label);
		addData("label", label);
		addData("edge-prop-DisplayValue", label);
		this.label = label;
	}

	/**
	 * @param source
	 *            the source to set
	 */
	public final void setSource(String source) {
		this.source = source;
	}

	/**
	 * @param sourceNode
	 *            the sourceNode to set
	 */

	public final void setSourceNode(GenericNode sourceNode) {
		this.sourceNode = sourceNode;
	}

	/**
	 * @param target
	 *            the target to set
	 */

	public final void setTarget(String target) {
		this.target = target;
	}

	/**
	 * @param targetNode
	 *            the targetNode to set
	 */
	public final void setTargetNode(GenericNode targetNode) {
		this.targetNode = targetNode;
	}

	/**
	 * Set the number of occurrences of this specific from-to pair
	 * 
	 * @param count
	 */
	public void setCount(int count) {
		this.count = count;
	}

	@Override
	public String toString() {
		return ("Edge from " + sourceNode + " to " + targetNode);
	}

	public boolean isDirected() {
		return directed;
	}

	public void setDirected(boolean directed) {
		this.directed = directed;
	}

	public String getDataValue(String key) {
		for (GraphObjectData o : dataSet) {
			if (o.key.equals(key))
				return o.keyVal;
		}
		return null;
	}

	/**
	 * Change the existing value of an attribute or add a new one. <BR>
	 * Different from addData which handles dupes by appending an underscore to
	 * the key
	 * 
	 * @param key
	 * @param value
	 */
	public void setDataValue(String key, String value) {
		if (value == null) {
			logger.error("Null value being set for key " + key);
		}

		for (GraphObjectData o : dataSet) {
			if (o.key.equals(key)) {
				dataSet.remove(o);
				break;
			}
		}

		addData(key, value);
	}

}
