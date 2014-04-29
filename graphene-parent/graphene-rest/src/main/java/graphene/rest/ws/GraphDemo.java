package graphene.rest.ws;

import graphene.model.view.sample.GraphDemoObject;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;


@Path("/node")
public interface GraphDemo {

	@GET
	@Path("all/")
	@Produces("application/json")
	public abstract GraphDemoObject getAllNodes();

	@GET
	@Path("show/")
	@Produces("application/json")
	public abstract GraphDemoObject getNode(@QueryParam("id") @DefaultValue(value = "1") String id);

	@GET
	@Path("testjson")
	@Produces("application/json")
	public abstract GraphDemoObject getJSON();

	// This can be used to test the integration with the browser
	@GET
	@Path("testxml")
	@Produces("application/xml")
	public abstract GraphDemoObject getHTML();

}