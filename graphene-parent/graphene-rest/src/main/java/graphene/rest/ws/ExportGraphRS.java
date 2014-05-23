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

/**
 * History
 * 
 * 09/02/13 A. Weller - Initial version
 * 
 * 10/16/13 M. Martinet - Revised to use POST methods for export services,
 * removed the JSONArray painfull headaches, and added a getExportedGraph
 * service
 * 
 * 11/04/13 D. Jue - Changed package to graphene.kiva.web.rest. Making changes
 * as necessary.
 * 
 * 04/14/13 D. Jue - Made generic and put into core rest module.
 * 
 * @author djue
 * 
 */
@Path("/graph")
public interface ExportGraphRS {

	/**
	 * 
	 * @param fileName
	 * @param fileExt
	 * @param userName
	 * @param timeStamp
	 *            this is the client timestamp in millisecs as a string
	 * @param graphJSONdata
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/exportGraphAsXML")
	@Produces("text/plain")
	Response exportGraphAsXML(
			@QueryParam("fileName") @DefaultValue(value = "") String fileName,
			@QueryParam("fileExt") @DefaultValue(value = ".xml") String fileExt,
			@QueryParam("userName") @DefaultValue(value = "unknown") String userName,
			@QueryParam("timeStamp") @DefaultValue(value = "0") String timeStamp,

			String graphJSONdata);

	/**
	 * XXX: Fix this, we should never be writing files to the local file system
	 * like this --djue
	 * 
	 * NOTES: The graph export must be done in two stages and cannot be done
	 * using one simple Get request because the graph data sent from the client
	 * will typically be large ( > 4K).
	 * 
	 * 1. Graph data in JSON from the browser app is POSTed to this service
	 * (/exportGraphAsJSON) using an AJAX request. // The graph data is stored
	 * in a temporary file on the web server. The response contains the path to
	 * this file.
	 * 
	 * 2. A subsequent GET request (/getExportedGraph) is sent from the Browser
	 * app which prompts the user to // download the temporary file from the
	 * server and save it to their local filesystem. After the temporary file is
	 * downloaded it is deleted.
	 * 
	 * @param fileName
	 * @param fileExt
	 * @param userName
	 * @param timeStamp
	 *            this is the client timestamp in millisecs as a string
	 * @param graphJSONdata
	 * @return
	 */
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/exportGraphAsJSON")
	@Produces("text/plain")
	Response exportGraphAsJSON(
			@QueryParam("fileName") @DefaultValue(value = "") String fileName,
			@QueryParam("fileExt") @DefaultValue(value = ".xml") String fileExt,
			@QueryParam("userName") @DefaultValue(value = "unknown") String userName,
			@QueryParam("timeStamp") @DefaultValue(value = "0") String timeStamp,
			/*
			 * this is the client timestamp in millisecs as a string
			 */
			String graphJSONdata);

	/**
	 * This service is used to download a file containing a previously exported
	 * graph
	 * 
	 * @param filePath
	 * @return
	 */
	@GET
	@Path("/getExportedGraph")
	@Produces("application/x-unknown")
	Response getExportedGraph(
			@QueryParam("filePath") @DefaultValue(value = "") String filePath);
}