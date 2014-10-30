package graphene.dao;

import java.util.List;

import graphene.model.idl.G_Link;
import graphene.model.query.BasicQuery;
import graphene.model.query.DirectedEventQuery;

/**
 * Put anything here that you'd want this DAO to do differently than the generic
 * one (Such as methods returning view objects instead of model objects)
 * 
 * @author djue
 * 
 * @param <T>
 * @param <Q>
 */
public interface UnifiedCommunicationEventDAO<T, Q extends BasicQuery> extends
		GenericDAO<T, Q> {
	public List<G_Link> getLinks(DirectedEventQuery q);
}
