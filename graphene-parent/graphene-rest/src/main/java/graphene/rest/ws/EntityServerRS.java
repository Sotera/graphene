package graphene.rest.ws;


import graphene.model.view.entities.EntityLight;
import graphene.model.view.entities.EntitySearchResults;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.tapestry5.annotations.Log;

@Path("/EntitySearch")
public interface EntityServerRS {
	@Log
	@GET
	@Path("/advancedSearch")
	@Produces("application/json")
	public EntitySearchResults advancedSearch(
			@QueryParam("jsonSearch") String jsonSearch);
	@Log
	@GET
	@Path("/getEntityByID/{ID}")
	@Produces("application/json")
	public EntityLight getEntityByID (@PathParam("ID") String id);
	
	
}
