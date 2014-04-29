package graphene.model.view.sample;

import java.util.HashSet;
import java.util.Set;


public class NodeAttribute {

	// attributes = [{"name" => "No Relationships","name" =>
	// "No Relationships","values" => [{"id" => "#{params[:id]}","name" =>
	// "No Relationships "}]}] if attributes.empty?
	String id; // something like friends/enemies, etc
	String name; // something like Incoming:friends/Outgoing:friends
	Set<DisplayNode> values = new HashSet<DisplayNode>(); //a set of name/id pairs

	/**
	 * @return the values
	 */
	public final Set<DisplayNode> getValues() {
		return values;
	}

	/**
	 * @param values
	 *            the values to set
	 */
	public final void setValues(final Set<DisplayNode> values) {
		this.values = values;
	}

	/**
	 * @return the id
	 */
	public final String getId() {
		return id;
	}

	/**
	 * @param id
	 *            the id to set
	 */
	public final void setId(final String id) {
		this.id = id;
	}

	/**
	 * @return the name
	 */
	public final String getName() {
		return name;
	}

	/**
	 * @param name
	 *            the name to set
	 */
	public final void setName(final String name) {
		this.name = name;
	}

}
