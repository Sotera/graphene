package graphene.rest.ws.impl;

import graphene.dao.DataSourceListDAO;
import graphene.model.datasourcedescriptors.DataSourceList;
import graphene.rest.ws.DataSourceServerRS;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

import org.apache.tapestry5.ioc.annotations.Inject;

@Path("/datasources")
public class DataSourceServerRSImpl implements DataSourceServerRS {

	@Inject
	private DataSourceListDAO dao; 
	// -----------------------------------------------------------------------
	@Produces("application/json")
	@GET
	@Path("/getAll")
	public DataSourceList getAll() {
		return dao.getList();
	}

}