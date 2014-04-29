package graphene.model.graph;

import java.util.HashMap;

public class InfoVisEdge {
	/**
	 * { "nodeTo" : "graphnode1", "nodeFrom" : "graphnode0", "data" : { "$color"
	 * : "#557EAA" } }
	 */
	String nodeTo, nodeFrom;

	public String getNodeTo() {
		return nodeTo;
	}

	public void setNodeTo(String nodeTo) {
		this.nodeTo = nodeTo;
	}

	public String getNodeFrom() {
		return nodeFrom;
	}

	public void setNodeFrom(String nodeFrom) {
		this.nodeFrom = nodeFrom;
	}

	public HashMap<String, String> getData() {
		return data;
	}

	public void setData(HashMap<String, String> data) {
		this.data = data;
	}

	HashMap<String, String> data;

	public void addData(String key, String value) {
		if (key != null && value != null && !key.isEmpty() && !value.isEmpty()) {
			if (data == null) {
				data = new HashMap<String,String>();
			}
			data.put(key, value);
		}
	}

	public InfoVisEdge(String t, String f, String... d) {
		nodeTo = t;
		nodeFrom = f;
		for (int i = 0; i < d.length; i += 2) {
			if (d.length > i + 1) {
				addData(d[i], d[i + 1]);
			}
		}
	}

	@Override
	public String toString() {
		return "InfoVisEdge [nodeTo=" + nodeTo + ", nodeFrom=" + nodeFrom
				+ ", data=" + data + "]";
	}

}
