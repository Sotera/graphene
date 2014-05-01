package graphene.dao;

import mil.darpa.vande.legacy.graphserver.DirectableObjectLoader;
import mil.darpa.vande.legacy.graphserver.TransactionDistinctAccountPair;


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
