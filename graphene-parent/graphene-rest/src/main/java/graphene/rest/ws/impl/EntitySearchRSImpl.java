package graphene.rest.ws.impl;

import graphene.dao.EntityService;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;
import graphene.model.query.EntityQuery;
import graphene.model.query.SearchTypeHelper;
import graphene.rest.ws.EntitySearchRS;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class EntitySearchRSImpl implements EntitySearchRS {
	private int defaultOffset = 0, defaultMaxResults = 30;
	@Inject
	private EntityService service;

	@Inject
	Logger logger;

	@Override
	public List<G_Entity> getEntity(String identifiers) {
		EntityQuery q = new EntityQuery();
		List<G_SearchTuple<String>> tupleList = SearchTypeHelper
				.processSearchList(identifiers, G_SearchType.COMPARE_CONTAINS);
		q.setAttributeList(tupleList);
		q.setFirstResult(defaultOffset);
		q.setMaxResult(defaultMaxResults);
		return service.search(q);
	}
}
