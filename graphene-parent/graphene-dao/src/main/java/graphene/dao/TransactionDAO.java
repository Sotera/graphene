package graphene.dao;

import graphene.model.query.EventQuery;
import graphene.util.CallBack;


/**
 * This is for ONE sided transactions.
 * @author djue
 *
 * @param <T>
 * @param <Q>
 */
public interface TransactionDAO<T, Q> extends GenericDAO<T,Q>{

	long countEdges(String id) throws Exception;

	boolean performThrottlingCallback(long offset, long maxResults,
			CallBack<T> cb, EventQuery q);
}
