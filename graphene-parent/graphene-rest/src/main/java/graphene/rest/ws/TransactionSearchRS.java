package graphene.rest.ws;

import graphene.model.idl.G_Link;

import java.util.List;

import javax.ws.rs.DefaultValue;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;

@Path("/transaction")
public interface TransactionSearchRS {

	/**
	 * Note that accountNumbers and comments are plural, and accept comma
	 * separated values which will be tokenized and searched for.
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
	@Produces("application/json")
	@GET
	@Path("/any")
	public abstract List<G_Link> getTransactions(
			@QueryParam("accountNumbers") @DefaultValue(value = "") String account,
			@QueryParam("offset") @DefaultValue(value = "0") int offset,
			@QueryParam("limit") @DefaultValue(value = "1000") int limit,
			@QueryParam("minAmount") @DefaultValue(value = "0") String minAmount,
			@QueryParam("maxAmount") @DefaultValue(value = "0") String maxAmount,
			@QueryParam("fromdt") @DefaultValue(value = "0") String minSecs,
			@QueryParam("todt") @DefaultValue(value = "0") String maxSecs,
			@QueryParam("comments") @DefaultValue(value = "") String comments,
			@QueryParam("sortColumn") @DefaultValue(value = "date") String sortColumn,
			@QueryParam("intersection") @DefaultValue(value = "false") boolean intersection);

	/**
	 * In this version you explicitly define the senders and receivers
	 * @param senderAccounts
	 * @param receiverAccounts
	 * @param offset
	 * @param limit
	 * @param minAmount
	 * @param maxAmount
	 * @param minSecs
	 * @param maxSecs
	 * @param comments
	 * @param sortColumn
	 * @return
	 */
	@Produces("application/json")
	@GET
	@Path("/between")
	List<G_Link> getTransactions(
			@QueryParam("from") @DefaultValue(value = "") String senderAccounts,
			@QueryParam("to") @DefaultValue(value = "") String receiverAccounts,
			@QueryParam("limit") @DefaultValue(value = "0") int offset,
			@QueryParam("limit") @DefaultValue(value = "1000") int limit,
			@QueryParam("minAmount") @DefaultValue(value = "0") String minAmount,
			@QueryParam("minAmount") @DefaultValue(value = "0") String maxAmount,
			@QueryParam("fromdt") @DefaultValue(value = "0") String minSecs,
			@QueryParam("todt") @DefaultValue(value = "0") String maxSecs,
			@QueryParam("comments") @DefaultValue(value = "") String comments,
			@QueryParam("sortColumn") @DefaultValue(value = "date") String sortColumn,
			@QueryParam("intersection") @DefaultValue(value = "true") boolean intersection);

	// -----------------------------------------------------------------------

}