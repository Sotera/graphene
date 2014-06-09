package graphene.dao;

import java.util.List;

import graphene.model.query.BasicQuery;
import graphene.model.view.events.DirectedEventRow;

/**
 * This is for ONE OR TWO sided transactions.
 * 
 * @author djue
 * 
 * @param <T>
 * @param <Q>
 */
public interface TransactionDAO<T, Q extends BasicQuery> extends
		GenericDAO<T, Q> {

	long countEdges(String id) throws Exception;

	List<DirectedEventRow> getEvents(Q q);

}
