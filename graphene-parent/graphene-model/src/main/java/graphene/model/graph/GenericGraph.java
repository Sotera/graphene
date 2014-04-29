package graphene.model.graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Used internally for graph generation, and converted into the appropriate
 * format when serving as a response
 * 
 * @author PWG
 * 
 */
public class GenericGraph {

	private List<GenericNode> nodes = null;
	private List<GenericEdge> edges = null;

	public GenericGraph(List<GenericNode> nodes, List<GenericEdge> edges) {
		this.nodes = nodes;
		this.edges = edges;
	}

	public GenericGraph() {
		nodes = new ArrayList<GenericNode>();
		edges = new ArrayList<GenericEdge>();
		// TODO Auto-generated constructor stub
	}

	public List<GenericNode> getNodes() {
		return nodes;
	}

	public void setNodes(List<GenericNode> nodes) {
		this.nodes = nodes;
	}

	public List<GenericEdge> getEdges() {
		return edges;
	}

	public void setEdges(List<GenericEdge> edges) {
		this.edges = edges;
	}
}
