package graphene.rest.ws;

import graphene.model.view.events.SingleSidedEvents;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

/**
 * This is legacy.  We should not have to have a separate rest service for free text searches.
 * 
 * @author djue
 *
 */

@Path("/FreeText")
public interface LedgerFreeTextRS {

	// -----------------------------------------------------------------------
	@Produces("application/json")
	@GET
	@Path("/getTransactions")
	public abstract SingleSidedEvents getTransactions(
			@QueryParam("accountNumber") @DefaultValue(value = "") String account,
			@QueryParam("start") @DefaultValue(value = "0") int start,
			@QueryParam("limit") @DefaultValue(value = "1000") int limit,
			@QueryParam("minAmount") @DefaultValue(value = "0") String minAmount,
			@QueryParam("maxAmount") @DefaultValue(value = "0") String maxAmount,
			@QueryParam("fromdt") @DefaultValue(value = "0") String minSecs,
			@QueryParam("todt") @DefaultValue(value = "0") String maxSecs,
			@QueryParam("comments") @DefaultValue(value = "") String comments,
			@QueryParam("sortColumn") @DefaultValue(value = "trn_dt") String sortColumn);				
	// -----------------------------------------------------------------------

	
}