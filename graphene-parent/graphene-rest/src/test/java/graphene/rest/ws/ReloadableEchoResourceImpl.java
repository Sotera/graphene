package graphene.rest.ws;

import javax.ws.rs.core.Response;

public class ReloadableEchoResourceImpl implements ReloadableEchoResource
{

	@Override
	public Response echo(String message)
	{
		return Response.status(200).entity(message).build();
	}

}