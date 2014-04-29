package graphene.rest.ws.autobuild;


import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;

@Path("/ping")
public class PingResource
{

	@GET
	@Produces("text/html")
	public String ping()
	{
		return "<html><h1>PONG</h1></html>";
	}

}
