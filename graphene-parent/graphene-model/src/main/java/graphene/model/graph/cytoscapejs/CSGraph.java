package graphene.model.graph.cytoscapejs;

import graphene.model.graph.GenericEdge;
import graphene.model.graph.GenericGraph;
import graphene.model.graph.GenericNode;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "csgraph")
public class CSGraph {

	private List<CSEdge> edges = new ArrayList<CSEdge>();

	private List<CSNode> nodes = new ArrayList<CSNode>();

	public CSGraph() {

	}

	public CSGraph(GenericGraph g, boolean GQT_Style) {
		for (GenericEdge e : g.getEdges())
			edges.add(new CSEdge(e));

		for (GenericNode n : g.getNodes()) {
			nodes.add(new CSNode(n));
		}

	}

	/**
	 * @return the edges
	 */
	@XmlElement
	public List<CSEdge> getEdges() {
		return edges;
	}

	/**
	 * @return the nodes
	 */
	@XmlElement
	public final List<CSNode> getNodes() {
		return nodes;
	}

	public void addNode(CSNode node) {
		nodes.add(node);
	}

	/**
	 * @param edges
	 *            the edges to set
	 */
	public final void setEdges(List<CSEdge> edges) {
		this.edges = edges;
	}

	public void addEdge(CSEdge grEdge) {
		edges.add(grEdge);
	}
}
