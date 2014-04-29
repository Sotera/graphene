package graphene.rest.ws;

import graphene.model.idl.G_Entity;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/entity")
public interface EntitySearchRS {

	// -----------------------------------------------------------------------
	@Produces("application/json")
	@GET
	@Path("/any")
	public abstract List<G_Entity> getEntity(
			@QueryParam("identifier") @DefaultValue(value = "") String identifier);

}