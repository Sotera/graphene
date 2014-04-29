package graphene.model.graph.graphml;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "graphml")
public class GraphmlContainer {

	@XmlElement(name = "key")
	List<GraphmlKey> keys = new ArrayList<GraphmlKey>();

	@XmlElement(name = "graph")
	public GraphmlGraph graph;

	/*
	 * @XmlTransient public GraphmlGraph getGraph() { return graph; }
	 */
	public void setGraph(GraphmlGraph graph) {
		this.graph = graph;
	}

	public GraphmlContainer() {
		// Note: individual graph implementations should add their own initial
		// keys
		addNodeKey("label", "label");
		addNodeKey("node-prop-DisplayValue", "node-prop-DisplayValue");
		addNodeKey("node-prop-ImageSource", "node-prop-ImageSource");

	}

	public void addNodeKey(String name, String value) {
		keys.add(new GraphmlKey(name, "node", value, "string"));
	}
}
