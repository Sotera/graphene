package graphene.dao;

import graphene.model.query.AdvancedSearch;
import graphene.model.query.EventQuery;
import graphene.model.view.entities.Entity;

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
	public List<Entity> getEntitiesByAdvancedSearch(AdvancedSearch srch);

	/**
	 * 
	 * @param id
	 * @return
	 */
	public Entity getById(String id);

	/**
	 * XXX: This needs to be changed to work without side effects.
	 * 
	 * @param e
	 */
	public void updateAllFields(Entity e);

	/**
	 * 
	 * @param q
	 * @return
	 */
	public long count(EventQuery q);

}