package graphene.dao;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.query.AdvancedSearch;
import graphene.model.query.EventQuery;
import graphene.model.view.entities.Entity;

import java.util.List;

public interface EntityDAO {
	public List<Entity> getEntitiesByProperty(G_CanonicalPropertyType property, String value);

	public List<Entity> getEntitiesByAdvancedSearch(AdvancedSearch srch);
	
	public Entity getById(String id);

	public void updateAllFields(Entity e);

	public long count(EventQuery q);

}