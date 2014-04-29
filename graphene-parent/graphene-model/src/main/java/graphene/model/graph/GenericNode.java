package graphene.model.graph;

import java.util.SortedSet;
import java.util.TreeSet;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A base class that can be extended for other types of nodes in the view layer.
 * 
 * @author PWG
 * 
 */
public class GenericNode {

	protected String id; // allocated by nodefactory, used in edges to designate
							// from and to nodes
	protected int dataSource = 0; // TODO: set this value when we have more than
									// one

	protected char entityType;
	protected boolean traversed = false;
	protected boolean isOrigin = false; // true if it's the one searched for
	protected boolean isLeaf = true; // default, until children added
	protected int degree = 0;
	private boolean isPlaceholder = false;
	protected boolean isUsed = false;
	protected int nbrLinks = 0;
	protected String label;
	protected String key; // used to locate in nodeList
	protected String value;
	protected String icon = null;
	private String backgroundColor;

	protected SortedSet<GraphObjectData> dataSet = new TreeSet<GraphObjectData>();

	protected static final Logger logger = LoggerFactory
			.getLogger(GenericNode.class);

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
	public final void setId(String id) {
		this.id = id;
	}

	/**
	 * @return the dataSet
	 */

	public SortedSet<GraphObjectData> getDataSet() {
		return dataSet;
	}

	/**
	 * @param dataSet
	 *            the dataSet to set
	 */
	public final void setDataSet(SortedSet<GraphObjectData> dataSet) {
		this.dataSet = dataSet;
	}

	public char getEntityType() {
		return entityType;
	}

	public void setEntityType(char entityType) {
		this.entityType = entityType;
	}

	public boolean isTraversed() {
		return traversed;
	}

	public void setTraversed(boolean traversed) {
		this.traversed = traversed;
	}

	public boolean isOrigin() {
		return isOrigin;
	}

	public void setOrigin(boolean isOrigin) {
		this.isOrigin = isOrigin;
	}

	public boolean isLeaf() {
		return isLeaf;
	}

	public void setLeaf(boolean isLeaf) {
		this.isLeaf = isLeaf;
	}

	public int getDegree() {
		return degree;
	}

	public void setDegree(int degree) {
		this.degree = degree;
	}

	public boolean isPlaceholder() {
		return isPlaceholder;
	}

	public void setPlaceholder(boolean isPlaceholder) {
		this.isPlaceholder = isPlaceholder;
	}

	public boolean isUsed() {
		return isUsed;
	}

	public void setUsed(boolean isUsed) {
		this.isUsed = isUsed;
	}

	public int getNbrLinks() {
		return nbrLinks;
	}

	public void setNbrLinks(int nbrLinks) {
		this.nbrLinks = nbrLinks;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public void incLinks() {
		++nbrLinks;

	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
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

	public void setLabel(String label) {
		this.label = label;
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

	public void setBackgroundColor(String colorString) {
		this.backgroundColor = colorString;
	}

	public String getLabel() {
		return label;
	}

	public String getIcon() {
		return icon;
	}

	public void setIcon(String icon) {
		this.icon = icon;
	}

	public String getBackgroundColor() {
		return backgroundColor;
	}

	public int getDataSource() {
		return dataSource;
	}

	public void setDataSource(int source) {
		this.dataSource = source;
	}

	public String getDataValue(String key) {
		for (GraphObjectData o : dataSet) {
			if (o.key.equals(key))
				return o.keyVal;
		}
		return null;
	}

	/**
	 * Change the existing value of an attribute or add a new one. <BR>
	 * Different from addData which handles dupes by appending an underscore to
	 * the key
	 * 
	 * @param key
	 * @param value
	 */
	public void setDataValue(String key, String value) {
		if (value == null) {
			logger.error("Null value being set for key " + key);
		}
		for (GraphObjectData o : dataSet) {
			if (o.key.equals(key)) {
				dataSet.remove(o);
				break;
			}
		}

		addData(key, value);
	}

}
