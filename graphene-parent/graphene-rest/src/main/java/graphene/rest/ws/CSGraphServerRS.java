package graphene.rest.ws;


import graphene.model.graph.cytoscapejs.CSGraph;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import mil.darpa.vande.converters.cytoscapejs.V_CSGraph;


@Path("/csgraph/")
@Produces("application/json")
public interface CSGraphServerRS {

	@GET
	@Path("/{type}/{value}")
	@Produces("application/json")
	public abstract CSGraph getByIdentifier(
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
	@Produces("application/json")
	public abstract CSGraph getDirected(
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

	@GET
	@Path("/directed/{objectType}/{value}")
	@Produces("application/json")
	public abstract CSGraph getInteractions(
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

	@GET
	@Path("/interactiongraph/{objectType}")
	@Produces("application/json")
	public V_CSGraph getInteractionGraph(
			@PathParam("objectType") String objectType, // Dataset name etc
			@QueryParam("ids") String[] ids,
			@QueryParam("Type") String valueType, 
			@QueryParam("maxHops") String maxHops,
			@QueryParam("maxNodes") String maxNodes,
			@QueryParam("maxEdgesPerNode") String maxEdgesPerNode,
			@QueryParam("showIcons") boolean showIcons,	
			@QueryParam("fromdt") @DefaultValue(value = "0") String minSecs,
			@QueryParam("todt") @DefaultValue(value = "0") String maxSecs,
			@QueryParam("minLinksPairOverall") String minLinksPairOverall, // across all time periods
			@QueryParam("minValueAnyInteraction") String minValueAnyInteraction, // for any interaction
			@QueryParam("daily") @DefaultValue(value = "false") boolean daily,
			@QueryParam("monthly") @DefaultValue(value = "false") boolean monthly,
			@QueryParam("yearly") @DefaultValue(value = "false") boolean yearly,			
			@QueryParam("directed") @DefaultValue(value = "true") boolean directed			
		
			);
}