package graphene.model.graph.graphml;

import graphene.model.graph.GenericEdge;
import graphene.model.graph.GraphObjectData;

import java.util.HashSet;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

public class GraphmlEdge {

	@XmlAttribute
	private String source = "src";
	@XmlAttribute
	private String target = "target";
	@XmlAttribute
	private int degree = 0;
	@XmlAttribute
	private String label;
	@XmlAttribute
	private boolean directed = false;
	@XmlAttribute
	private int weight = 1;

	private Set<GraphObjectData> dataSet = new HashSet<GraphObjectData>();

	public GraphmlEdge(GenericEdge e) {
		this.source = e.getSource();
		this.target = e.getTarget();
		this.degree = e.getDegree();
		this.label = e.getLabel();
		this.directed = e.isDirected();
		this.weight = e.getWeight();
		this.dataSet.addAll(e.getDataSet());
	}

	@XmlElement(name = "data")
	public final Set<GraphObjectData> getDataSet() {
		return dataSet;
	}

}
