package graphene.services;

import graphene.dao.EntityRefDAO;
import graphene.dao.EntityService;
import graphene.model.idl.G_Entity;
import graphene.model.query.EntityQuery;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

public class EntityServiceImpl implements EntityService{

	@Inject
	private EntityRefDAO dao;
	@Override
	public List<G_Entity> search(EntityQuery q) {
		// TODO Auto-generated method stub
		return null;
	}

}
