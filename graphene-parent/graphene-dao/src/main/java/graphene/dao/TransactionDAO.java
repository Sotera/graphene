package graphene.dao;

import graphene.model.query.BasicQuery;
import graphene.model.view.events.DirectedEventRow;

import java.util.List;

/**
 * This is for ONE OR TWO sided transactions.
 * 
 * @author djue
 * 
 * @param <T>
 * @param <Q>
 */
public interface TransactionDAO<T, Q extends BasicQuery> extends GenericDAO {

	long countEdges(String id) throws Exception;

	/**
	 * For finding by internal pair id
	 * 
	 * @param id
	 * @return
	 */
	DirectedEventRow findEventById(String id);

	List<DirectedEventRow> getEvents(Q q);

}
