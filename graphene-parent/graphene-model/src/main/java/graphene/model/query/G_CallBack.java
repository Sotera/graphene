package graphene.model.query;

import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_SearchResult;

public interface G_CallBack {
	// TODO: Change method name to execute
	// public boolean callBack(G_SearchResult t);

	public boolean callBack(G_SearchResult t, G_EntityQuery q);

}
