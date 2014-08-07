package graphene.rest.ws;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

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
	public abstract V_CSGraph getProperties(@PathParam("type") String type,
			@PathParam("value") String[] value,
			@QueryParam("degree") String degree,
			@QueryParam("maxNodes") String maxNodes,
			@QueryParam("maxEdgesPerNode") String maxEdgesPerNode,
			@QueryParam("bipartite") boolean bipartite,
			@QueryParam("showLeafNodes") boolean leafNodes,
			@QueryParam("showNameNodes") boolean showNameNodes,
			@QueryParam("showIcons") boolean showIcons);

	/**
	 * REST service to return a interaction-type graph.
	 * TODO: Rename this method
	 * @param objectType
	 * @param value
	 * @param valueType
	 * @param degree
	 * @param maxNodes
	 * @param maxEdgesPerNode
	 * @param showIcons
	 * @param minSecs
	 * @param maxSecs
	 * @param minimumWeight
	 * @return
	 */
	@Log
	@GET
	@Path("/directed/{objectType}/{value}")
	@Produces("application/json")
	public abstract V_CSGraph getEvents(
			@PathParam("objectType") String objectType, // Dataset name etc
			@PathParam("value") String[] value,
			@QueryParam("Type") String valueType,
			@QueryParam("degree") String degree,
			@QueryParam("maxNodes") String maxNodes,
			@QueryParam("maxEdgesPerNode") String maxEdgesPerNode,
			@QueryParam("showIcons") boolean showIcons,
			@QueryParam("fromdt") @DefaultValue(value = "0") String minSecs,
			@QueryParam("todt") @DefaultValue(value = "0") String maxSecs,
			@QueryParam("minWeight") String minimumWeight);

	/**
	 * 
	 * Version that uses the new Vande graph generator supporting temporal
	 * bucketing.
	 * 
	 * Not all the implementations use the temporal settings.
	 * 
	 * @param objectType
	 *            Dataset name etc
	 * @param ids
	 * @param valueType
	 * @param maxHops
	 * @param maxNodes
	 * @param maxEdgesPerNode
	 * @param showIcons
	 * @param minSecs
	 * @param maxSecs
	 * @param minLinksPairOverall
	 *            across all time periods
	 * @param minValueAnyInteraction
	 *            for any interaction
	 * @param daily
	 * @param monthly
	 * @param yearly
	 * @param directed
	 * @return
	 */
	@Log
	@GET
	@Path("/interactiongraph/{objectType}")
	@Produces("application/json")
	public V_CSGraph getTemporalEvents(
			@PathParam("objectType") String objectType,
			@QueryParam("ids") String[] ids,
			@QueryParam("Type") String valueType,
			@QueryParam("maxHops") String maxHops,
			@QueryParam("maxNodes") String maxNodes,
			@QueryParam("maxEdgesPerNode") String maxEdgesPerNode,
			@QueryParam("showIcons") boolean showIcons,
			@QueryParam("fromdt") @DefaultValue(value = "0") String minSecs,
			@QueryParam("todt") @DefaultValue(value = "0") String maxSecs,
			@QueryParam("minLinksPairOverall") String minLinksPairOverall,
			@QueryParam("minValueAnyInteraction") String minValueAnyInteraction,
			@QueryParam("daily") @DefaultValue(value = "false") boolean daily,
			@QueryParam("monthly") @DefaultValue(value = "false") boolean monthly,
			@QueryParam("yearly") @DefaultValue(value = "false") boolean yearly,
			@QueryParam("directed") @DefaultValue(value = "true") boolean directed

	);
}