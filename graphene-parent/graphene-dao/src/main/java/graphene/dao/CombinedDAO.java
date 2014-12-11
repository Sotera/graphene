package graphene.dao;

import graphene.model.query.EntityQuery;
import graphene.model.view.GrapheneResults;

/**
 * For hitting a backend that will return either multiple datatypes or unknown
 * objects, which will be later decoded.
 * 
 * @author djue
 * 
 */
public interface CombinedDAO extends GenericDAO<Object, EntityQuery> {

	public abstract GrapheneResults<Object> findByQueryWithMeta(EntityQuery pq)
			throws Exception;
}