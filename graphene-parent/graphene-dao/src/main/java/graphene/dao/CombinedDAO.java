package graphene.dao;

import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_SearchResult;
import graphene.model.idl.G_SearchResults;

/**
 * For hitting a backend that will return either multiple datatypes or unknown
 * objects, which will be later decoded.
 * 
 * @author djue
 * 
 */
public interface CombinedDAO extends GenericDAO {

	public abstract G_SearchResult findById(G_EntityQuery pq);

	public abstract G_SearchResults findByQueryWithMeta(G_EntityQuery pq) throws Exception;
}