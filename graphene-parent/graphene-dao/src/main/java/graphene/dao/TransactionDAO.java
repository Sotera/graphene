package graphene.dao;

import graphene.model.query.EventQuery;
import graphene.util.G_CallBack;


/**
 * This is for ONE OR TWO sided transactions.
 * @author djue
 *
 * @param <T>
 * @param <Q>
 */
public interface TransactionDAO<T, Q> extends GenericDAO<T,Q>{

	long countEdges(String id) throws Exception;

	boolean performThrottlingCallback(long offset, long maxResults,
			G_CallBack<T> cb, EventQuery q);
}
