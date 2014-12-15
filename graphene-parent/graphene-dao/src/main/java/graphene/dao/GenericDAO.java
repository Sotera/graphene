package graphene.dao;

import graphene.model.query.BasicQuery;
import graphene.util.G_CallBack;

import java.util.List;

/**
 * 
 * @author djue
 * 
 * @param <T>
 * @param <QUERYOBJECT>
 */
public interface GenericDAO<T, QUERYOBJECT extends BasicQuery> {

	/**
	 * This version assumes the offset and max results have been stored properly
	 * in the query object.
	 * 
	 * @param pq
	 * @return a list of T
	 */
	List<T> findByQuery(QUERYOBJECT pq) throws Exception;

	/**
	 * TODO: This could be replaced by putting offset and maxresults inside a
	 * QueryObject, with all other values null. (Similar to count(q) vs
	 * countAll())
	 * 
	 * @param offset
	 * @param maxResults
	 * @return a list of T
	 * @throws Exception
	 */
	List<T> getAll(final long offset, final long maxResults) throws Exception;

	long count(final QUERYOBJECT q) throws Exception;

	/**
	 * 
	 * @return true if this DAO is ready to be queried.
	 */
	boolean isReady();

	/**
	 * Allows end user implementations to record the readiness of the service,
	 * for example if a procedure fails we may set the service to not ready, or
	 * a larger service can cause this service to be available or unavailable
	 * depending on it's settings.
	 * 
	 * @param b
	 */
	void setReady(boolean b);

	/**
	 * This method is an expansion of the isReady system status. Individual
	 * implementations can declare what their readiness state is, and methods
	 * calling those implementations can decide whether the system is ready
	 * enough the be queried. This is good for multi layered DAOs, such as those
	 * backed by an InMemory portion. Depending on how much performance would be
	 * lost if the In Memory portion was not available, the implementation can
	 * adjust the strength of it's readiness accordingly.
	 * 
	 * @return a value between 0 (not ready) and 1 (fully operational)
	 */
	double getReadiness();

	/**
	 * Within this method, the callback cb will be executed, usually on each
	 * result the implementation finds. The implementation may choose to respect
	 * the QUERYOBJECT q. If maxResults is set to 0, that is usually understood
	 * to mean that there is no upper bound to the number of results (aka All
	 * results)
	 * 
	 * @param offset
	 * @param maxResults
	 * @param cb
	 * @param q
	 * @return true if the callback succeeded, or did not find any errors.
	 */
	boolean performCallback(long offset, long maxResults, G_CallBack<T,QUERYOBJECT> cb,
			QUERYOBJECT q);

}