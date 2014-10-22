package graphene.hts.entityextraction;

import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;

import java.util.Collection;
import java.util.List;

public interface Extractor {

	public abstract Collection<G_Entity> extractEntities(String source);

	public abstract Collection<String> extract(String source);

	public abstract String getIdType();

	public abstract String getNodetype();

	public abstract String getRelationType();

	public abstract String getRelationValue();
	public List<G_EntityTag> getEntityTags();
	public List<G_Property> getProperties();
	
}