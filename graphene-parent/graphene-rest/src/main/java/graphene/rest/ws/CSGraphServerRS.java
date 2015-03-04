package graphene.rest.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import mil.darpa.vande.converters.cytoscapejs.V_CSGraph;

import org.apache.tapestry5.annotations.Log;

@Path("/csgraph/")
@Produces("application/json")
public interface CSGraphServerRS {

	/**
	 * REST service to return a property-type graph.
	 * 
	 * @param type
	 * @param value
	 * @param degree
	 * @param maxNodes
	 * @param maxEdgesPerNode
	 * @param bipartite
	 * @param leafNodes
	 * @param showNameNodes
	 * @param showIcons
	 * @return
	 */
	@Log
	@GET
	@Path("/{type}/{value}")
	@Produces("application/json")
	public abstract V_CSGraph getProperties(@PathParam("type") String type, @PathParam("value") String[] value,
			@QueryParam("degree") String degree, @QueryParam("maxNodes") String maxNodes,
			@QueryParam("maxEdgesPerNode") String maxEdgesPerNode, @QueryParam("bipartite") boolean bipartite,
			@QueryParam("showLeafNodes") boolean leafNodes, @QueryParam("showNameNodes") boolean showNameNodes,
			@QueryParam("showIcons") boolean showIcons,
			@QueryParam("useSaved") @DefaultValue(value = "true") boolean useSaved);

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/save")
	@Produces("text/plain")
	Response saveGraph(@QueryParam("seed") @DefaultValue(value = "unknown") String graphSeed,
			@QueryParam("username") @DefaultValue(value = "unknown") String username,
			@QueryParam("timeStamp") @DefaultValue(value = "0") String timeStamp, String graph);
}