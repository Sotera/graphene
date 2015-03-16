package graphene.dao;

import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_TransactionResults;

public interface EventServer {

	public abstract G_TransactionResults getEvents(G_EntityQuery q);

}