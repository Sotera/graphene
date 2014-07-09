package graphene.rest.ws;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import mil.darpa.vande.converters.graphml.GraphmlContainer;

import org.apache.tapestry5.annotations.Log;

@Path("/graphml/")
@Produces("application/xml")
public interface GraphmlServerRS {

	/**
	 * Seems to always be like this on EntityGraph.js:
	 * 
	 * graphml/ + intype + '/' + node.data().name;
	 * 
	 * or on TXN_Graph:
	 * 
	 * graphml/ + intype + '/' + node.name;
	 * 
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
	@Produces("application/xml")
	public abstract GraphmlContainer getProperties(
			@PathParam("type") String type, @PathParam("value") String value,
			@QueryParam("degree") String degree,
			@QueryParam("maxNodes") String maxNodes,
			@QueryParam("maxEdgesPerNode") String maxEdgesPerNode,
			@QueryParam("bipartite") boolean bipartite,
			@QueryParam("showLeafNodes") boolean leafNodes,
			@QueryParam("showNameNodes") boolean showNameNodes,
			@QueryParam("showIcons") boolean showIcons);

	/**
	 * Seems to always be graphml/directed/transfers/ or graphml/directed/COMMS/
	 * 
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
	@Produces("application/xml")
	public abstract GraphmlContainer getEvents(
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

}