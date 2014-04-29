package graphene.model.graph.graphml;

import graphene.model.graph.GenericEdge;
import graphene.model.graph.GenericGraph;
import graphene.model.graph.GenericNode;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
/**
 * 
 * @author pwg
 *
 * Replace the usage of this class with the version in Vande.
 */
@Deprecated
public class GraphmlGraph {

	private List<GraphmlEdge> edges = new ArrayList<GraphmlEdge>();

	private List<GraphmlNode> nodes = new ArrayList<GraphmlNode>();

	private String nodeType = "Icon";
	
	/**
	 * 
	 * @param g GenericGraph
	 * @param GQT_Style true=use the colors from Graphics Query Tool
	 */
	public GraphmlGraph(GenericGraph g, boolean GQT_Style)
	{
		for (GenericEdge e:g.getEdges())
			edges.add(new GraphmlEdge(e));
		
		for (GenericNode n:g.getNodes()) {
			GraphmlNode node = new GraphmlNode(n);
			node.setColors(GQT_Style);
			nodes.add(new GraphmlNode(n));
		}
		
	}

	/**
	 * @return the edges
	 */
	@XmlElementWrapper(name = "edges")
	@XmlElement(name = "edge")
	public final List<GraphmlEdge> getEdges() {
		return edges;
	}

	/**
	 * @return the nodes
	 */
	@XmlElementWrapper(name = "nodes")
	@XmlElement(name = "node")
	public final List<GraphmlNode> getNodes() {
		return nodes;
	}

	public void addNode(GraphmlNode node) {
		nodes.add(node);
	}

	/**
	 * @return the nodeType
	 */
	@XmlAttribute(namespace = "http://graph.bericotechnologies.com/xmlns")
	public final String getNodeType() {
		return nodeType;
	}

	/**
	 * @param edges
	 *            the edges to set
	 */
	public final void setEdges(List<GraphmlEdge> edges) {
		this.edges = edges;
	}

	/**
	 * @param nodes
	 *            the nodes to set
	 */
	public final void setNodes(List<GraphmlNode> nodes) {
		this.nodes = nodes;
	}

	/**
	 * 
	 * @param type
	 *            String - either "Icon" or "Text"
	 */
	public void setNodeType(String type) {
		nodeType = type;
	}

	public String validate() {
		String err = "";
		Set<String> idList = new HashSet<String>();
		for (GraphmlNode g : nodes) {
			if (idList.contains(g.id)) {
				err = " Duplicate node number " + g.id;
				return err;
			}
			idList.add(g.id);
		}
/*
 * TODO		
		for (GraphmlEdge e : edges) {
			if (e.getSource().equals(e.getTarget())) // Note: do not use == or
														// != to
				// compare strings.
				err = "Node with source and target the same";
			if (!idList.contains(e.getSource()))
				err = "Source node for edge is missing";
			if (!idList.contains(e.getTarget()))
				err = "Target node for edge is missing";

		}
*/		
		return err;
	}

	public void addEdge(GraphmlEdge grEdge) {
		edges.add(grEdge);
	}
}
