package graphene.rest.ws.impl;

import graphene.dao.CombinedDAO;
import graphene.dao.DataSourceListDAO;
import graphene.model.view.entities.EntityLight;
import graphene.rest.ws.EntityServerRS;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

@Deprecated
public class EntityServerRSImpl implements EntityServerRS {

	@Inject
	private Logger logger;

	@Inject
	private DataSourceListDAO dataSourceListDAO;

	@Inject
	private CombinedDAO entitydao;

	@Override
	@GET
	@Path("/getEntityByID/{ID}")
	@Produces("application/json")
	/**
	 * Returns an entity given the ID. May not be needed, since
	 * all the search responses may have complete entities contained
	 * in them. 
	 */
	public EntityLight getEntityByID(@PathParam("ID") final String id) {
		logger.debug("Getting entity for id " + id);
		return entitydao.getById(id);
	}

}
