package graphene.dao;

import graphene.model.idl.G_Entity;
import graphene.model.query.EntityQuery;

import java.util.List;

public interface EntityService {
	public List<G_Entity> search(EntityQuery q);
}
