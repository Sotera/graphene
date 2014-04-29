package graphene.model.idhelper;

import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_Provenance;
import graphene.model.idl.G_Uncertainty;

import java.util.List;

/**
 * Wraps our property class by giving us easy to use constructors, which Avro
 * won't generate.
 * 
 * @author djue
 * 
 */
public class PropertyHelper extends G_Property {

	public PropertyHelper(String key, String friendlyText, Object value,
			G_PropertyType type, G_Provenance provenance, G_Uncertainty uncertainty,
			List<G_PropertyTag> tags) {
		super();
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(provenance);
		setUncertainty(uncertainty);
		setTags(tags);
		setRange(new SingletonRangeHelper(value, type));
	}

	public PropertyHelper(String key, String friendlyText, Object value,
			G_PropertyTag tag) {
		setKey(key);
		setFriendlyText(friendlyText);
		setRange(new SingletonRangeHelper(value));
		getTags().add(tag);
	}

	public PropertyHelper(String key, String friendlyText, Object value,
			G_PropertyType type, G_PropertyTag... tags) {
		super();
		setKey(key);
		setFriendlyText(friendlyText);
		setRange(new SingletonRangeHelper(value, type));
		setProvenance(null);
		for (G_PropertyTag pt : tags) {
			getTags().add(pt);
		}
	}

}
