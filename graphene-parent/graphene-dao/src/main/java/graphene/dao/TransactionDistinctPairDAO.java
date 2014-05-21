package graphene.dao;

import mil.darpa.vande.interactions.InteractionFinder;
import mil.darpa.vande.property.PropertyFinder;

/**
 * Used to construct distinct sender/receiver pairs for the transaction graph
 * 
 * @author PWG for DARPA
 * 
 */
public interface TransactionDistinctPairDAO<T, Q> extends GenericDAO<T, Q>,
		InteractionFinder, PropertyFinder

{

}
