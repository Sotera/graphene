package graphene.dao;

import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntityQuery;

import java.util.List;

public interface EntityService {
	public List<G_Entity> search(G_EntityQuery q);
}
