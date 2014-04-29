package graphene.model.graph.cytoscapejs;

/**
 * Consists of a key value pair We add a list of CSAttrs to the data elements
 * when serving a Cytoscape graph. Note however that this is not part of
 * Cytoscape.js
 * 
 * @author PWG
 * 
 */
public class CSAttr {
	String key;
	String val;

	public CSAttr() {

	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getVal() {
		return val;
	}

	public void setVal(String val) {
		this.val = val;
	}

	public CSAttr(String key, String val) {
		this.key = key;
		this.val = val;
	}
}
