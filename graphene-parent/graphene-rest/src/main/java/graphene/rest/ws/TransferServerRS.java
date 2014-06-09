package graphene.rest.ws;

import graphene.model.view.events.EventStatistics;
import graphene.model.view.events.DirectedEvents;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

import org.apache.tapestry5.annotations.Log;
/**
 * NOTE:  This class will be superseded by EventSearchRS.
 * @author djue
 *
 */
@Path("/")
public interface TransferServerRS {

	/**
	 * The main resource path for this rest endpoint.
	 * 
	 * 
	 * @param account
	 * @param start
	 * @param limit
	 * @param minAmount
	 * @param maxAmount
	 * @param minSecs
	 * @param maxSecs
	 * @param comments
	 * @param sortColumn
	 * @return
	 */
	@Log
	@Produces("application/json")
	@GET
	@Path("/getEvents")
	public abstract DirectedEvents getEvents(
			@QueryParam("accountNumber") @DefaultValue(value = "") String[] account,
			@QueryParam("start") @DefaultValue(value = "0") int start,
			@QueryParam("limit") @DefaultValue(value = "1000") int limit,
			@QueryParam("minAmount") @DefaultValue(value = "0") String minAmount,
			@QueryParam("maxAmount") @DefaultValue(value = "0") String maxAmount,
			@QueryParam("fromdt") @DefaultValue(value = "0") String minSecs,
			@QueryParam("todt") @DefaultValue(value = "0") String maxSecs,
			@QueryParam("comments") @DefaultValue(value = "") String comments,
			@QueryParam("sortColumn") @DefaultValue(value = "trn_dt") String sortColumn);

	/**
	 * XXX: Seems like this is not being used.
	 * 
	 * @param account
	 * @return
	 */
	@Log
	@Produces("application/json")
	@GET
	@Path("/getPairMonthlyStatistics")
	public abstract EventStatistics getPairMonthlyStatistics(
			@QueryParam("accountNumber") @DefaultValue(value = "") String account);

	/**
	 * XXX: Seems like this is not being used.
	 * 
	 * @param account
	 * @param year
	 * @param month
	 * @return
	 */
	@Log
	@Produces("application/json")
	@GET
	@Path("/getPairDailyStatistics")
	public abstract EventStatistics getPairDailyStatistics(
			@QueryParam("accountNumber") @DefaultValue(value = "") String account,
			@QueryParam("year") @DefaultValue(value = "0") int year,
			@QueryParam("month") @DefaultValue(value = "0") int month);

}