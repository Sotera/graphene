package graphene.model.view.sample;

import java.util.ArrayList;
import java.util.List;


public class NodeData {
	private List<NodeAttribute> attributes = new ArrayList<NodeAttribute>();
	Long id;
	String name;

	/**
	 * @return the attributes
	 */
	public List<NodeAttribute> getAttributes() {
		return attributes;
	}

	/**
	 * @return the id
	 */
	public Long getId() {
		return id;
	}

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param attributes
	 *            the attributes to set
	 */
	public void setAttributes(final List<NodeAttribute> attributes) {
		this.attributes = attributes;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public void setId(final Long id) {
		this.id = id;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public void setName(final String name) {
		this.name = name;
	}
}
