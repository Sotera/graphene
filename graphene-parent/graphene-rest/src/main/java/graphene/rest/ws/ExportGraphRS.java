package graphene.rest.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// History
// 09/02/13     A. Weller - Initial version
// 10/16/13     M. Martinet - Revised to use POST methods for export services,
//              removed the JSONArray painfull headaches, and added a getExportedGraph service
// 11/04/13 	D. Jue - Changed package to graphene.kiva.web.rest.  Making changes as necessary.
// 04/14/13 	D. Jue - Made generic and put into core rest module.
@Path("/graph")
public interface ExportGraphRS {

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/exportGraphAsXML")
	@Produces("text/plain")
	public abstract Response exportGraphAsXML(
			@QueryParam("fileName") @DefaultValue(value = "") String fileName,
			@QueryParam("fileExt") @DefaultValue(value = ".xml") String fileExt,
			@QueryParam("userName") @DefaultValue(value = "unknown") String userName,
			@QueryParam("timeStamp") @DefaultValue(value = "0") String timeStamp,
			/*
			 * this is the client timestamp in millisecs as a string
			 */

			String graphJSONdata);

	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/exportGraphAsJSON")
	@Produces("text/plain")
	public abstract Response exportGraphAsJSON(
			@QueryParam("fileName") @DefaultValue(value = "") String fileName,
			@QueryParam("fileExt") @DefaultValue(value = ".xml") String fileExt,
			@QueryParam("userName") @DefaultValue(value = "unknown") String userName,
			@QueryParam("timeStamp") @DefaultValue(value = "0") String timeStamp,
			/*
			 * this is the client timestamp in millisecs as a string
			 */
			String graphJSONdata);

	// This service is used to downloaded a file containing a previously
	// exported graph
	@GET
	@Path("/getExportedGraph")
	@Produces("application/x-unknown")
	public abstract Response getExportedGraph(
			@QueryParam("filePath") @DefaultValue(value = "") String filePath);
}