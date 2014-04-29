package graphene.model.query;

import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_Provenance;

import java.util.ArrayList;

/**
 * Used for interaction with OculusInfo's Apeture
 * 
 * @author djue
 * 
 */
public abstract class NamedEntity {
	protected String label = "default label";
	protected ArrayList<G_Property> properties = new ArrayList<G_Property>();
	
	protected G_Provenance provenance;
	protected Double score = 0.0d;
	protected ArrayList<G_EntityTag> tags = new ArrayList<G_EntityTag>();
	protected String uid;

	public void addProperty(G_Property p) {
		properties.add(p);
	}

	public void addTag(G_EntityTag e) {
		tags.add(e);
	}

	/**
	 * @return the label
	 */
	public String getLabel() {
		return label;
	}

	/**
	 * @return the properties
	 */
	public ArrayList<G_Property> getProperties() {
		return properties;
	}

	/**
	 * @return the provenance
	 */
	public G_Provenance getProvenance() {
		return provenance;
	}

	/**
	 * @return the score
	 */
	public Double getScore() {
		return score;
	}

	/**
	 * @return the tags
	 */
	public ArrayList<G_EntityTag> getTags() {
		return tags;
	}

	/**
	 * @return the uid
	 */
	public String getUid() {
		return uid;
	}

	/**
	 * @param label
	 *            the label to set
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @param properties
	 *            the properties to set
	 */
	public void setProperties(ArrayList<G_Property> properties) {
		this.properties = properties;
	}

	/**
	 * @param provenance
	 *            the provenance to set
	 */
	public void setProvenance(G_Provenance provenance) {
		this.provenance = provenance;
	}

	/**
	 * @param score
	 *            the score to set
	 */
	public void setScore(Double score) {
		this.score = score;
	}

	/**
	 * @param tags
	 *            the tags to set
	 */
	public void setTags(ArrayList<G_EntityTag> tags) {
		this.tags = tags;
	}

	/**
	 * @param uid
	 *            the uid to set
	 */
	public void setUid(String uid) {
		this.uid = uid;
	}

}
