package graphene.dao;

import graphene.model.query.BasicQuery;

/**
 * For use with tables that have a from and a to id.
 * 
 * @author djue
 * 
 */
public interface UnifiedEventDAO<S, Q extends BasicQuery> extends GenericDAO<S, Q> {


}
