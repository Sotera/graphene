package graphene.rest.ws;

import graphene.dao.EntityService;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;
import graphene.model.query.EntityQuery;
import graphene.model.query.SearchTypeHelper;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class EntitySearchRSImpl implements EntitySearchRS {
	private int defaultOffset = 0, defaultMaxResults = 30;
	@Inject
	private EntityService service;

	@Inject
	Logger logger;


	/**
	 * This private method is called from one or more public methods which
	 * construct the query object.
	 * 
	 * @param q
	 * @return
	 */
	private List<G_Entity> search(EntityQuery q) {
		List<G_Entity> retval = new ArrayList<G_Entity>();
		
		try {
			retval = service.search(q);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		logger.debug("Returning object to JSON");
		return retval;
	}

	@Override
	public List<G_Entity> getEntity(String identifiers) {
		EntityQuery q = new EntityQuery();
		List<G_SearchTuple<String>> tupleList = SearchTypeHelper
				.processSearchList(identifiers, G_SearchType.COMPARE_CONTAINS);
		q.setAttributeList(tupleList);
		q.setFirstResult(defaultOffset);
		q.setMaxResult(defaultMaxResults);
		return search(q);
	}
}
