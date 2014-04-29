package graphene.dao;

import graphene.util.CallBack;

import java.util.List;

public interface GenericDAO<T, QUERYOBJECT> {
	/**
	 * 
	 * @param offset
	 * @param maxResults
	 * @param q
	 * @return
	 */
	List<T> findByQuery(long offset, long maxResults, QUERYOBJECT q)
			throws Exception;

	/**
	 * TODO: This could be replaced by putting offset and maxresults inside a
	 * QueryObject, with all other values null. (Similar to count(q) vs
	 * countAll())
	 * 
	 * @param offset
	 * @param maxResults
	 * @return
	 * @throws Exception
	 */
	List<T> getAll(long offset, long maxResults) throws Exception;

	public abstract long count(QUERYOBJECT q) throws Exception;

	/**
	 * TODO: I see the need for having a separate version of this that can take
	 * a Tuple as T. This is so that during ingest and other large loads, we
	 * only work with the columns that are needed, which means less data over
	 * the wire and less heap space to take up.
	 * 
	 * @param offset
	 * @param maxResults
	 * @param cb
	 * @param q
	 * @return
	 */
	public abstract boolean performCallback(long offset, long maxResults,
			CallBack<T> cb, QUERYOBJECT q);

}