package graphene.rest.ws.impl;

import graphene.dao.EventServer;
import graphene.export.DirectedEventsToCSV;
import graphene.export.DirectedEventsToXLS;
import graphene.export.ExportUtil;
import graphene.model.query.EventQuery;
import graphene.model.view.events.DirectedEventRow;
import graphene.model.view.events.DirectedEvents;
import graphene.model.view.events.EventStatistics;
import graphene.rest.ws.EventServerRS;
import graphene.util.FastNumberUtils;
import graphene.util.StringUtils;
import graphene.util.stats.TimeReporter;
import graphene.util.validator.ValidationUtils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

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
	public DirectedEvents getEvents(String[] account, int start, int limit,
			String minAmount, String maxAmount, String minSecs, String maxSecs,
			String comments, String sortColumn) {
		TimeReporter t = new TimeReporter("getEvents", logger);
		if (start < 0) {
			logger.debug("Got Events request with invalid starting offset "
					+ start);
			return new DirectedEvents(); // bug in extjs often asks for
											// negative start
		}

		EventQuery q = new EventQuery();
		q.addIds(Arrays.asList(account));
		q.setFirstResult(start);
		q.setMaxResult(limit);
		q.setMinSecs(FastNumberUtils.parseLongWithCheck(minSecs, 0));
		q.setMaxSecs(FastNumberUtils.parseLongWithCheck(maxSecs, 0));
		q.setMinAmount(Double.parseDouble(minAmount.isEmpty() ? "0" : minAmount));
		q.setMaxAmount(Double.parseDouble(maxAmount.isEmpty() ? "0" : maxAmount));
		q.setComments(comments);
		q.setSortAndDirection(sortColumn);

		return eventServer.getEvents(q);
	}

	public EventStatistics getPairMonthlyStatistics(
			@QueryParam("accountNumber") String account) {
		if (!prevQuery.isSingleId()) {
			return new EventStatistics(); // to return an empty structure
		}
		return prevStatistics; // generated when previous query was executed
	}

	public EventStatistics getPairDailyStatistics(
			@QueryParam("accountNumber") String account,
			@QueryParam("year") int year, @QueryParam("month") int month)

	{
		if (!prevQuery.isSingleId()) {
			return new EventStatistics(); // to return an empty structure
		}
		if (!prevStatistics.account.equals(account)) {
			logger.debug("Account does not match the last one loaded");
			return new EventStatistics(); // to return an empty structure
		}

		EventStatistics stats = new EventStatistics();

		return stats;
	}

	@Override
	public Response exportEventsCSV(String[] account, int start, int limit,
			String minAmount, String maxAmount, String minSecs, String maxSecs,
			String comments, String sortColumn) {

		TimeReporter t = new TimeReporter("Export to CSV", logger);
		DirectedEvents de = getEvents(account, start, limit, minAmount,
				maxAmount, minSecs, maxSecs, comments, sortColumn);
		String fileLabel = "UnknownAccount";
		if (account != null && account.length > 0) {
			
			fileLabel = StringUtils.toDelimitedString(account, "_");
		}
		String fname = "KBB_Events_" + fileLabel + ".csv";
		DirectedEventsToCSV converter = new DirectedEventsToCSV();
		String csvString = converter.toCSV(de);
		ResponseBuilder response = null;
		if (ValidationUtils.isValid(csvString)) {
			response = Response.ok(csvString.getBytes());
			response.header("Content-Disposition", "inline; filename=\""
					+ fname + "\"");
		} else {
			logger.error("No data for CSV export, check parameters.");
			response = Response.serverError();
		}
		t.logElapsed();
		return response.build();
	}

	@Override
	public Response exportEventsXLS(String[] account, int start, int limit,
			String minAmount, String maxAmount, String minSecs, String maxSecs,
			String comments, String sortColumn) {
		TimeReporter t = new TimeReporter("Export to XLS", logger);
		DirectedEvents de = getEvents(account, start, limit, minAmount,
				maxAmount, minSecs, maxSecs, comments, sortColumn);
		ByteArrayOutputStream baos = new ByteArrayOutputStream(2048);
		DirectedEventsToXLS converter = new DirectedEventsToXLS();
		converter.toXLS(de, baos,
				(de.getResultCount() <= ExportUtil.MAX_XLS_RESULTS));
		String fileLabel = "UnknownAccount";
		if (account != null && account.length > 0) {
			
			fileLabel = StringUtils.toDelimitedString(account, "_");
		}
		String fname = "KBB_Events_" + fileLabel + ".xls";
		ResponseBuilder response = Response.ok(baos.toByteArray());
		response.header("Content-Disposition", "inline; filename=\"" + fname
				+ "\"");
		try {
			baos.flush();
		} catch (IOException e1) {
			logger.error(e1.getMessage());
		}
		try {
			baos.close();
		} catch (IOException e) {
			logger.error(e.getMessage());
		}
		t.logElapsed();
		return response.build();
	}

}
