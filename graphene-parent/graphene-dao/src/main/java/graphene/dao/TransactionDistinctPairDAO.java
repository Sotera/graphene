package graphene.dao;

import graphene.model.graphserver.DirectableObjectLoader;
import graphene.model.graphserver.TransactionDistinctAccountPair;


/**
 * Used to construct distinct sender/receiver pairs
 * for the transaction graph
 * @author PWG for DARPA
 *
 */
public interface TransactionDistinctPairDAO <T,Q>
extends GenericDAO<T, Q>, DirectableObjectLoader<TransactionDistinctAccountPair, Q>

{

}
