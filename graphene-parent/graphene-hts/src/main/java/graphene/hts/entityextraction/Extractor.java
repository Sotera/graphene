package graphene.hts.entityextraction;

import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;

import java.util.Collection;
import java.util.List;

/**
 * 
 * @author djue
 * 
 */
public interface Extractor {

	/**
	 * 
	 * @param source
	 * @return a collection of G_Entity which were found using source
	 */
	public abstract Collection<G_Entity> extractEntities(String source);

	/**
	 * 
	 * @param source
	 * @return a collection of strings which were found using source
	 */
	public abstract Collection<String> extract(String source);

	/**
	 * 
	 * @return the id type
	 */
	public abstract String getIdType();

	/**
	 * 
	 * @return the node type
	 */
	public abstract String getNodetype();

	/**
	 * 
	 * @return the relation type
	 */
	public abstract String getRelationType();

	/**
	 * 
	 * @return the relation value
	 */
	public abstract String getRelationValue();

	/**
	 * 
	 * @return a list of entity tags
	 */
	public List<G_EntityTag> getEntityTags();

	/**
	 * 
	 * @return a list of properties
	 */
	public List<G_Property> getProperties();

}