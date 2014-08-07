package graphene.services;

import graphene.dao.EventServer;
import graphene.dao.TransactionDAO;
import graphene.model.query.EventQuery;
import graphene.model.view.events.DirectedEventRow;
import graphene.model.view.events.DirectedEvents;
import graphene.model.view.events.EventStatistics;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.joda.time.DateTime;
import org.slf4j.Logger;

/**
 * To be genericised and moved to the core package. The goal is to pull out
 * logic that is being stored in the TransferServerRSImpl and consolidate it
 * here, so that the REST implementation can move to the graphene-rest module.
 * 
 * Second, we desire to eliminate unecessary business logic, and move custom
 * code down the stack, closer to classes that will already need to be custom
 * implemented (the DAOs)
 * 
 * Third, this class should start to have capabilities to act as a broker for
 * federated searches across several DAO implementations, and allow the
 * TransferServerRS REST resource to become the single endpoint for all similar
 * data, with the dataset name being an optional parameter.
 * 
 * TODO: Will need to figure out how to manually contribute or automatically
 * contribute DAOs that support the type of data we want. It's easy to inject a
 * known number of DAOs with markers, etc, but we want to add in an unknown
 * number of data providers.
 * 
 * @author djue
 * 
 */
public class EventServerImpl implements EventServer {

	@Inject
	private Logger logger;

	private TransactionDAO eventDAO;

	@Inject
	public EventServerImpl(@InjectService("Primary") TransactionDAO dao) {
		this.eventDAO = dao;
	}

	/**
	 * Removes duplicates
	 * 
	 * @param rows
	 * @return
	 */
	private List<DirectedEventRow> deDupeRows(List<DirectedEventRow> rows) {
		List<DirectedEventRow> newRows = new ArrayList<DirectedEventRow>();
		// Set<TransferRow> set = new HashSet<TransferRow>();
		// set.addAll(rows);
		// newRows.addAll(set);

		// MFM The input data is already sorted
		for (DirectedEventRow rec : rows) {
			if (!newRows.contains(rec)) {
				newRows.add(rec);
			}
		}
		return newRows;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * graphene.services.EventServer#getEvents(graphene.model.query.EventQuery)
	 */
	@Override
	public DirectedEvents getEvents(EventQuery q) {
		logger.debug("q=" + q.toString());
		DirectedEvents transactions = new DirectedEvents();
		List<DirectedEventRow> allRows = null;
		try {
			if (q.getIdList().size() > 1) {
				allRows = processIntersections(q);
			} else if (q.isSingleId()
					&& ValidationUtils.isValid(q.getComments())) {
				logger.debug("Processing single account");
				transactions = processSingleAccount(q);
				// updateBalances(allRows);
			}
			// Note that multi account means searching across multiple accounts
			// Showing the intersection of multiple accounts is
			else {
				logger.debug("Processing multiple accounts");
				transactions = processMultiAccount(q);
			}
			if (transactions.getRows() != null) {
				logger.debug("Returning " + transactions.getRows().size()
						+ " rows");
			}

			if (allRows != null) {
				logger.debug("Out of total count " + allRows.size());
			}
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return transactions;
	}

	/**
	 * Process query transfers among the selected accounts.
	 * 
	 * @param q
	 *            Query object
	 * @param transactions
	 *            SingleSidedEvents object
	 * @return List of TransferRows matching the query
	 * @throws Exception
	 */
	private List<DirectedEventRow> processIntersections(EventQuery q)
			throws Exception {

		List<DirectedEventRow> rows;

		logger.debug("Starting process transfer intersections with query "
				+ q.getIdList());

		// We now get all the transactions if it's a single account, so that we
		// can get
		// the statistics
		q.setIntersectionOnly(true);
		rows = eventDAO.getEvents(q);
		rows = deDupeRows(rows);
		// MFM The below BREAKs column sorting
		// Collections.sort(rows);

		return rows;
	}

	private DirectedEvents processMultiAccount(EventQuery q) throws Exception {
		List<DirectedEventRow> rows;
		// If we have a new query, perform it. Else we look at the previous full
		// results;

		rows = eventDAO.getEvents(q);
		rows = deDupeRows(rows);
		// MFM The below BREAKs column sorting
		// Collections.sort(rows);
		// return rows;
		DirectedEvents transactions = new DirectedEvents();
		transactions.setResultCount(rows.size());
		transactions.setRows(rows);
		return transactions;
	}

	/**
	 * Process query for a single account. Note that there may actually be more
	 * than one account in the result set, since we include the earlier and
	 * later accounts when the account transitioned. We simultaneously populate
	 * a monthly statistics object and persist it in the session for subsequent
	 * retrieval. Daily statistics are only generated on demand.
	 * 
	 * @param q
	 *            Query object
	 * @param transactions
	 *            SingleSidedEvents object
	 * @return List of LedgerPairRows matching the query
	 * @throws Exception
	 */
	private DirectedEvents processSingleAccount(EventQuery q) throws Exception {
		DirectedEvents transactions = new DirectedEvents();

		List<DirectedEventRow> rows = eventDAO.getEvents(q);
		double localUnitBalance = 0;
		double unitBalance = 0;

		logger.debug("Starting process single account with query "
				+ q.toString());

		// We now get all the transactions if it's a single account, so that we
		// can get
		// the statistics

		// List<EnronTransactionPair100> entries = transferDAO.findByQuery(q);
		// // ***
		// MFM
		// REVISIT
		// NAME:
		// transferDAOfullQ
		// TODO: Check if we need to dedupe here, before calculating statistics.
		// rows = new ArrayList<DirectedEventRow>();

		boolean multiUnit = false;
		String lastUnit = null;
		boolean transitionFound = false;

		for (DirectedEventRow e : rows) {
			if (lastUnit == null) {
				lastUnit = e.getUnit();
			} else if (!lastUnit.equals(e.getUnit())) {
				multiUnit = true;
			}

			DateTime dt = new DateTime(e.getDate());

			if (!transitionFound && dt.getYear() == 2010) {
				localUnitBalance = 0;
				unitBalance = 0;
				transitionFound = true;
			}

			if ((q.getMinSecs() != 0) && (dt.getMillis() < q.getMinSecs()))
				continue;
			if ((q.getMaxSecs() != 0) && (dt.getMillis() > q.getMaxSecs()))
				continue;
			// TODO: should we set balances for all accounts earlier on?
			// DirectedEventRow l = funnel.from(e);
			e.setLocalUnitBalance(localUnitBalance);
			e.setBalance(unitBalance);
			// rows.add(l);
		}

		EventStatistics stats = new EventStatistics();
		stats.setAccount(q.getSingleId());

		if (multiUnit) {
			stats.setUseUnit(false);
			transactions.setMultiUnit(true);
		}

		rows = deDupeRows(rows);
		if (transactions.isMultiUnit()) {
			for (DirectedEventRow r : rows)
				r.setBalance(0);
		}
		// MFM The below BREAKs column sorting
		// Collections.sort(rows);
		// updateBalances(allRows);

		// return rows;
		transactions.setResultCount(rows.size());
		transactions.setRows(rows);
		return transactions;
	}

	// XXX: GET RID OF THIS
	private List<DirectedEventRow> slice(List<DirectedEventRow> rows,
			long start, long limit) {
		logger.debug("Slicing from total: " + rows.size() + " start: " + start
				+ " limit: " + limit);
		List<DirectedEventRow> newRows = new ArrayList<DirectedEventRow>();
		if (start == 0 && limit == 0) {
			return rows;
		}
		if (start < 0) { // Seems to be an ExtJS bug
			logger.debug("Trying to slice from negative start point");
			return newRows;
		}
		int offset = 0;
		int count = 0;

		for (DirectedEventRow r : rows) {
			if (offset < start) {
				++offset;
				continue;
			}
			if (count >= limit)
				break;
			newRows.add(r);
			++count;
			++offset;
		}
		logger.debug("Returning " + newRows.size() + " rows from slice");
		return newRows;
		// return rows.subList(start, limit);

	}

}
