package graphene.dao;

import graphene.model.idl.G_SearchResults;
import graphene.model.query.EntityQuery;
import graphene.model.view.GrapheneResults;

import java.util.List;

/**
 * For hitting a backend that will return either multiple datatypes or unknown
 * objects, which will be later decoded.
 * 
 * @author djue
 * 
 */
public interface CombinedDAO extends GenericDAO<Object, EntityQuery> {

	public abstract List<Object> findById(EntityQuery pq);

	@Deprecated
	public abstract GrapheneResults<Object> findByQueryWithMeta(EntityQuery pq) throws Exception;

	public abstract G_SearchResults findByQueryWithMeta2(EntityQuery pq) throws Exception;
}