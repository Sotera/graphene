package graphene.dao;

import graphene.model.idl.G_EntityQuery;
import graphene.model.view.GrapheneResults;

import java.util.List;

/**
 * For hitting a backend that will return either multiple datatypes or unknown
 * objects, which will be later decoded.
 * 
 * @author djue
 * 
 */
public interface CombinedDAO extends GenericDAO<Object, G_EntityQuery> {

	public abstract List<Object> findById(G_EntityQuery pq);

	public abstract GrapheneResults<Object> findByQueryWithMeta(G_EntityQuery pq)
			throws Exception;
}