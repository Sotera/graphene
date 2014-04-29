package graphene.model.graph;

import graphene.model.graph.entity.IconMap;
import graphene.model.view.entities.IdType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * An extension of GRNode for directed graph use
 * 
 * @author ??
 * 
 */
public class DirectedNode extends GenericNode {

	// TODO: remove any that are dupes of GRNode

	static String origin_bg_color = "#ffff0000";
	static String leaf_bg_color = "#0000ffff";
	private boolean isPlaceholder = false;
	private boolean scanned = false; // true when we have searched on this value

	private String valueType = null;
	private IdType id_type = null;
	private boolean used = true;

	static Logger logger = LoggerFactory.getLogger(DirectedNode.class);

	public DirectedNode() {
		// Used for JAXB - has to have a no-arg constructor
		super();
	}

	public DirectedNode(String value, String id) {
		super();
		this.value = value;
		this.key = value;
		this.id = id;
	}

	/**
	 * Adds a key value pair to the dataSet list that appears in the keys
	 * section of graphml. If an entry with this attribute already exsists,
	 * append "_" to the attribute's name, except for labels, where we set the
	 * shortest version of the customer name.
	 * 
	 * @param attribute
	 *            String the name of the attribute
	 * @param value
	 *            String the value for the attribute
	 */
	public void addData(String attribute, String value) {
		// We can have two entries for an identifier name. But SnagL barfs
		// if it gets two entries for the same name. So we kludge the name
		// here

		try {
			if (attribute == null || attribute.length() == 0) {
				throw new Exception();
			}
		} catch (Exception e) {
			logger.error(
					"Add Data with empty attribute name " + e.getMessage(), e);
			return;
		}

		if (value == null || value.length() == 0) {
			logger.error("Add Data for name " + attribute + " with null value");
			new Exception().printStackTrace();
			return;
		}

		// Now add the value
		boolean need_add = false;
		for (GraphObjectData d : dataSet) {
			if (d.getKey().equals(attribute)) {
				if (d.getKeyVal().equals(value))
					return; // Duplicate attr + value - ignore it
				else {
					need_add = true; // Duplicate attr, new value
					break;
				}
			}
		}
		if (need_add) { // we have more than one value for the same attribute
			addData(attribute + "_", value); // recursive
			return;
		}

		// So the name doesn't exist in the data set

		dataSet.add(new GraphObjectData(attribute, value));
	}

	public void addData(String attribute, String value, String family) {

		addData(attribute, value);

	}

	public void setLabel(String label) {
		if (label == null || label.length() == 0) {
			logger.error("Setting invalid label into node");
			return;
		}
		removeData("label");
		removeData("node-prop-DisplayValue");

		addData("label", label);
		addData("node-prop-DisplayValue", label);
	}

	public String getLabel() {
		String label = null;

		for (GraphObjectData d : dataSet) {
			if (d.getKey().equals("label"))
				label = d.getKeyVal();
		}
		return label;
	}

	public void removeData(String type) {
		Object l = null;

		for (GraphObjectData d : dataSet) {
			if (d.getKey().equals(type)) {
				l = d;
				break;
			}
		}
		if (l != null)
			dataSet.remove(l);
	}

	public void setValueType(String type) {
		addData("IdentifierType", type);
		valueType = type;
		addData("node-prop-ImageSource", IconMap.getIcon(type));
		dataSource = 0; // todo - implement when we have more than one
	}


	@Override
	public void setValue(String value) {
		dataSet.add(new GraphObjectData("Identifier", value));
		this.value = value; // to make traversal easier
	}

	public String getValueType() {
		return valueType;
	}

	public void setIdTableSource(String source) {
		addData("IdentifierTableSource ", source);
	}

	public void setExpandable(boolean b) {
		/*
		 * if (!isOrigin) { removeData("node-prop-BackgroundColor");
		 * 
		 * if (b) { // logger.trace("Setting background color");
		 * addData("node-prop-BackgroundColor", "ffff0000"); } }
		 */
	}

	public void setColors(boolean GQT) {
		String color = null;

		if (isOrigin)
			color = origin_bg_color;
		else if (isLeaf)
			color = leaf_bg_color;
		else
			color = "FFFFFFFF";

		if (color != null)
			setBackgroundColor(color);

	}

	public void incLinks() {
		++nbrLinks;
	}

	public void decLinks() {
		--nbrLinks;
	}

	public IdType getId_type() {
		return id_type;
	}

	public boolean isPlaceholder() {
		return isPlaceholder;
	}

	public void setPlaceholder(boolean isPlaceholder) {
		this.isPlaceholder = isPlaceholder;
		String lab = getLabel();
		if (lab.charAt(0) != '[')
			setLabel("[" + lab + "]");
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public boolean isScanned() {
		return scanned;
	}

	public void setScanned(boolean scanned) {
		this.scanned = scanned;
	}

	public boolean isUsed() {
		return used;
	}

	public void setUsed(boolean used) {
		this.used = used;
	}

	@Override
	public String toString() {
		return "Node: " + id + " : " + getKey();
	}

}
