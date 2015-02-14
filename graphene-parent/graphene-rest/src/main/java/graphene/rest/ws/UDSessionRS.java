package graphene.rest.ws;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

// History
// 01/03/14     M. Martinet - Initial version - REST Services for User Defined Sessions

@Path("/UDsession")
public interface UDSessionRS {

        // getSession - Get session data for the specified session id
	@GET
	@Path("/getSession/{id}")
	@Produces("application/json")
	public abstract Response getSession(
                @PathParam("id") String sessionId
                );

        // getSessions - Get the list of All user-defined sessions
	@GET
	@Path("/getSessions")
	@Produces("application/json")
	public abstract Response getSessions();

        // getSessionsByDate - Get the list of All user-defined sessions for the date range specified by the from and to dates 
	@GET
	@Path("/getSessionsByDate/{fromdt}/{todt}")
	@Produces("application/json")
	public abstract Response getSessionsByDate(
                @PathParam("fromdt") @DefaultValue(value = "0") String fromdt,
                @PathParam("todt") @DefaultValue(value = "0") String todt
                );
        
        // getSessions - Get the list of user-defined sessions for the specified userId 
	@GET
	@Path("/getSessionsByuserId/{userId}")
	@Produces("application/json")
	public abstract Response getSessionsByuserId(
                @PathParam("userId") String userId
                );
        
        // getSessionsByName - Get the list of user-defined sessions for the specified session name 
	@GET
	@Path("/getSessionsByName/{name}")
	@Produces("application/json")
	public abstract Response getSessionsByName(
                @PathParam("name") String sessionName
                );
        
        // deleteSession - Delete the specified session for the specified user id 
	@DELETE
	@Path("/deleteSession/{id}")
	@Produces("application/json")
	public abstract Response deleteSession(
                @PathParam("id") String sessionId
                );
        
        // save - Save a user-defined session or graph layout
	@POST
	@Consumes(MediaType.APPLICATION_JSON)
	@Path("/save")
	@Produces("application/json")
	public abstract Response save(String sessionJSONdata);

	
}