package graphene.rest.ws.impl;

import graphene.dao.EventServer;
import graphene.model.query.EventQuery;
import graphene.model.view.events.DirectedEventRow;
import graphene.model.view.events.DirectedEvents;
import graphene.model.view.events.EventStatistics;
import graphene.rest.ws.EventServerRS;
import graphene.util.FastNumberUtils;
import graphene.util.stats.TimeReporter;

import java.util.Arrays;
import java.util.List;

import javax.ws.rs.QueryParam;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

@Deprecated
public class EventServerRSImpl implements EventServerRS {

	@Inject
	private Logger logger;

	// XXX: This is not the way to do this.
	@Persist
	private List<DirectedEventRow> prevAllRows;
	// XXX: This is not the way to do this.
	@Persist
	private EventQuery prevQuery;
	// XXX: This is not the way to do this.
	@Persist
	private EventStatistics prevStatistics;

	@Inject
	private EventServer eventServer;

	@Override
	public DirectedEvents getEvents(final String[] account, final int start, final int limit, final String minAmount,
			final String maxAmount, final String minSecs, final String maxSecs, final String comments,
			final String sortColumn) {
		final TimeReporter t = new TimeReporter("getEvents", logger);
		if (start < 0) {
			logger.debug("Got Events request with invalid starting offset " + start);
			return new DirectedEvents(); // bug in extjs often asks for
											// negative start
		}

		final EventQuery q = new EventQuery();
		q.addIds(Arrays.asList(account));
		q.setFirstResult(start);
		q.setMaxResult(limit);
		q.setMinSecs(FastNumberUtils.parseLongWithCheck(minSecs, 0));
		q.setMaxSecs(FastNumberUtils.parseLongWithCheck(maxSecs, 0));
		q.setMinAmount(Double.parseDouble(minAmount.isEmpty() ? "0" : minAmount));
		q.setMaxAmount(Double.parseDouble(maxAmount.isEmpty() ? "0" : maxAmount));
		q.setComments(comments);
		q.setSortAndDirection(sortColumn);

		final DirectedEvents e = eventServer.getEvents(q);
		t.logAsCompleted();
		return e;
	}

	@Override
	public EventStatistics getPairDailyStatistics(@QueryParam("accountNumber") final String account,
			@QueryParam("year") final int year, @QueryParam("month") final int month)

	{
		if (!prevQuery.isSingleId()) {
			return new EventStatistics(); // to return an empty structure
		}
		if (!prevStatistics.account.equals(account)) {
			logger.debug("Account does not match the last one loaded");
			return new EventStatistics(); // to return an empty structure
		}

		final EventStatistics stats = new EventStatistics();

		return stats;
	}

	@Override
	public EventStatistics getPairMonthlyStatistics(@QueryParam("accountNumber") final String account) {
		if (!prevQuery.isSingleId()) {
			return new EventStatistics(); // to return an empty structure
		}
		return prevStatistics; // generated when previous query was executed
	}

}
