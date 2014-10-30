package graphene.dao;

import graphene.model.idl.G_Entity;
import graphene.model.query.AdvancedSearch;
import graphene.model.query.EventQuery;
import graphene.model.view.entities.EntityLight;

import java.util.List;

/**
 * TODO: See if we still need to use this DAO the way we are using it, or if it
 * can be combined with the existing centralized EntityServer service.
 * 
 * @author pwg
 * 
 */
public interface EntityDAO {
	/**
	 * 
	 * @param srch
	 * @return
	 */
	public List<G_Entity> getEntitiesByAdvancedSearch(AdvancedSearch srch);

	/**
	 * 
	 * @param id
	 * @return
	 */
	public EntityLight getById(String id);

	/**
	 * XXX: This needs to be changed to work without side effects.
	 * 
	 * @param e
	 */
	// public void updateAllFields(G_Entity e);

	/**
	 * 
	 * @param q
	 * @return
	 */
	public long count(EventQuery q);

	public List<EntityLight> getLightEntitiesByAdvancedSearch(
			AdvancedSearch search);

}