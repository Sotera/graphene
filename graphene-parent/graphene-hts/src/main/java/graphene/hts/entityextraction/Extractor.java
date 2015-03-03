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
	 * Allow large texts to be split into multiple pieces, to improve
	 * performance. For instance, split by newline, tabs and/or periods.
	 * 
	 * @param source
	 * @param divideRegex
	 * @return
	 */
	public abstract Collection<String> divideAndExtract(String body, String regex);

	/**
	 * 
	 * @param source
	 * @return a collection of strings which were found using source
	 */
	public abstract Collection<String> extract(String source);

	/**
	 * 
	 * @param source
	 * @return a collection of G_Entity which were found using source
	 */
	public abstract Collection<G_Entity> extractEntities(String source);

	/**
	 * 
	 * @return a list of entity tags
	 */
	public List<G_EntityTag> getEntityTags();

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
	 * @return a list of properties
	 */
	public List<G_Property> getProperties();

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

}