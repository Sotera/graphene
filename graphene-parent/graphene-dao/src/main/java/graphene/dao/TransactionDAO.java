package graphene.dao;

import graphene.model.query.BasicQuery;
import graphene.model.query.EventQuery;
import graphene.util.G_CallBack;

/**
 * This is for ONE OR TWO sided transactions.
 * 
 * @author djue
 * 
 * @param <T>
 * @param <Q>
 */
public interface TransactionDAO<T, Q extends BasicQuery> extends GenericDAO<T, Q> {

	long countEdges(String id) throws Exception;

	// FIXME: we already have a version of this in GenericDAO, why do we need
	// this? --djue
//	boolean performThrottlingCallback(long offset, long maxResults,
//			G_CallBack<T> cb, EventQuery q);
}
