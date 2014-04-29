package graphene.model.graph.cytoscapejs;

import graphene.model.graph.GenericNode;
import graphene.model.graph.GraphObjectData;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlAttribute;

public class CSNodeData {
	public String color; // text color

	@XmlAttribute(name = "background-color")
	public String backgroundColor;

	public String id;
	public String idType;
	public String idVal;
	public String label;
	public String name;
	public String type = "circle";
	public boolean visible = true;

	public List<CSAttr> attrs = new ArrayList<CSAttr>();

	public CSNodeData() {

	}

	public CSNodeData(GenericNode node) {
		// TODO: look into cytoscape colors
		// they define color for text and background-color for the object

		this.color = node.getBackgroundColor();
		this.setBackgroundColor(color); // TODO: check if it needs # in front of
										// it
		this.id = node.getId();
		this.label = node.getLabel();
		this.name = node.getLabel(); // TODO - check why this redundancy
		this.idVal = node.getValue();
		Set<GraphObjectData> s = node.getDataSet();
		for (GraphObjectData d : s) {
			if (d.getKey().equals("IdentifierType"))
				this.idType = d.getKeyVal();
			else if (d.getKey().equals("Identifier"))
				this.idVal = d.getKeyVal();
			else
				this.attrs.add(new CSAttr(d.getKey(), d.getKeyVal()));
		}
	}

	public void setBackgroundColor(String color) {
		this.backgroundColor = color;
	}

}