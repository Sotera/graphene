package graphene.model.graph.graphml;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class GraphResponse {

	public GraphmlContainer graphml = new GraphmlContainer();

	public GraphResponse() {

	}

	public GraphmlContainer getGraphml() {
		return graphml;
	}

	public GraphmlGraph getGraph() {
		return graphml.graph;
	}

}
