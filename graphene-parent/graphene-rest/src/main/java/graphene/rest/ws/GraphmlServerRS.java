package graphene.rest.ws;


import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import mil.darpa.vande.legacy.graphml.GraphmlContainer;


@Path("/graphml/")
@Produces("application/xml")
public interface GraphmlServerRS {

	@GET
	@Path("/{type}/{value}")
	@Produces("application/xml")
	public abstract GraphmlContainer getByIdentifier(
                        @PathParam("type") String type,
			@PathParam("value") String value,
			@QueryParam("degree") String degree,
			@QueryParam("maxNodes") String maxNodes,
			@QueryParam("maxEdgesPerNode") String maxEdgesPerNode,
			@QueryParam("bipartite") boolean bipartite,
			@QueryParam("showLeafNodes") boolean leafNodes,
			@QueryParam("showNameNodes") boolean showNameNodes,
			@QueryParam("showIcons") boolean showIcons
			);

	@GET
	@Path("/directed/{objectType}/{value}")
	@Produces("application/xml")
	public abstract GraphmlContainer getDirected(
			@PathParam("objectType") String objectType, // Dataset name etc
			@PathParam("value") String value,
			@QueryParam("Type") String valueType, 
			@QueryParam("degree") String degree,
			@QueryParam("maxNodes") String maxNodes,
			@QueryParam("maxEdgesPerNode") String maxEdgesPerNode,
			@QueryParam("showIcons") boolean showIcons,	
			@QueryParam("fromdt") @DefaultValue(value = "0") String minSecs,
			@QueryParam("todt") @DefaultValue(value = "0") String maxSecs,
			@QueryParam("minWeight") String minimumWeight			
			);


}