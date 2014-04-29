package graphene.rest.ws;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

@Path("/echo")
public interface ReloadableEchoResource
{
	@GET
	@Path("/{message}")
	Response echo(@PathParam("message") String message);
}
