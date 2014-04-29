package graphene.model.graph.graphml;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * Used to insert the key definitions as properties in the top level of
 * <graphml>
 * 
 * @author pgofton
 * 
 */
public class GraphmlKey {

	@XmlAttribute
	String id;

	@XmlAttribute(name = "for")
	/* Note that "for" is a variable name in graphml */
	String fore;

	@XmlAttribute(name = "attr.name")
	String name;

	@XmlAttribute(name = "attr.type")
	String type;

	public GraphmlKey() {

	}

	/**
	 * 
	 * @param id
	 *            String: the name of the key
	 * @param fore
	 *            String: "node" or "edge"
	 * @param name
	 *            String: the name of the key (usually same as id)
	 * @param type
	 *            String: type of variable (usually "string")
	 */
	public GraphmlKey(String id, String fore, String name, String type) {
		this.id = id;
		this.fore = fore;
		this.name = name;
		this.type = type;
	}

}
