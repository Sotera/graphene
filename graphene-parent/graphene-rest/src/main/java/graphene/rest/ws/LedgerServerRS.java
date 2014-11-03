package graphene.rest.ws;

import graphene.model.view.events.EventStatistics;
import graphene.model.view.events.SingleSidedEvents;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;

@Path("/")
public interface LedgerServerRS {

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
	@Produces("application/json")
	@GET
	@Path("/getMonthlyStatistics")
	public abstract EventStatistics getMonthlyStatistics(
			@QueryParam("accountNumber") @DefaultValue(value = "") String account);

	@Produces("application/json")
	@GET
	@Path("/getDailyStatistics")
	public abstract EventStatistics getDailyStatistics(
			@QueryParam("accountNumber") @DefaultValue(value = "") String account,
			@QueryParam("year") @DefaultValue(value = "0") int year,
			@QueryParam("month") @DefaultValue(value = "0") int month);
	
	@Produces("text/csv")
	@GET
	@Path("/exportCSV")
	public abstract Response exportCSVTransactions(
			@QueryParam("accountNumber") @DefaultValue(value = "") String account, 
			@QueryParam("start") @DefaultValue(value = "0") int start, 
			@QueryParam("limit") @DefaultValue(value = "") int limit,
			@QueryParam("minAmount") @DefaultValue(value = "0") String minAmount, 
			@QueryParam("maxAmount") @DefaultValue(value = "0") String maxAmount, 
			@QueryParam("fromdt") @DefaultValue(value = "0") String minSecs, 
			@QueryParam("todt") @DefaultValue(value = "0") String maxSecs,
			@QueryParam("comments") @DefaultValue(value = "") String comments, 
			@QueryParam("unit") @DefaultValue(value = "") String unit, 
			@QueryParam("sortColumn") @DefaultValue(value = "trn_dt") String sortColumn);
	
	@Produces("application/vnd.ms-excel")
	@GET
	@Path("/exportXLS")
	public abstract Response exportXLSTransactions(
			@QueryParam("accountNumber") @DefaultValue(value = "") String account, 
			@QueryParam("start") @DefaultValue(value = "0") int start, 
			@QueryParam("limit") @DefaultValue(value = "") int limit,
			@QueryParam("minAmount") @DefaultValue(value = "0") String minAmount, 
			@QueryParam("maxAmount") @DefaultValue(value = "0") String maxAmount, 
			@QueryParam("fromdt") @DefaultValue(value = "0") String minSecs, 
			@QueryParam("todt") @DefaultValue(value = "0") String maxSecs,
			@QueryParam("comments") @DefaultValue(value = "") String comments, 
			@QueryParam("unit") @DefaultValue(value = "") String unit, 
			@QueryParam("sortColumn") @DefaultValue(value = "trn_dt") String sortColumn);
}