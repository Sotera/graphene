package graphene.rest.ws;

import graphene.model.datasourcedescriptors.DataSourceList;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/datasources")
public interface DataSourceServerRS {

	// -----------------------------------------------------------------------
	@Produces("application/json")
	@GET
	@Path("/getAll")
	public abstract DataSourceList getAll();

}