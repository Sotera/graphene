package graphene.rest.ws.impl;

import graphene.model.idl.G_BoundedRange;
import graphene.model.idl.G_DataAccess;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_TransactionResults;
import graphene.model.idlhelper.ListRangeHelper;
import graphene.model.idlhelper.QueryHelper;
import graphene.rest.ws.EventSearchRS;
import graphene.util.FastNumberUtils;
import graphene.util.StringUtils;
import graphene.util.stats.TimeReporter;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSearchRSImpl implements EventSearchRS {
	static Logger logger = LoggerFactory.getLogger(EventSearchRSImpl.class);
	private G_DataAccess dao;

	@Override
	public G_TransactionResults getEvents(final String identifiers, int offset, final int limit, final String minSecs,
			final String maxSecs, final String comments, final boolean intersectionOnly) {
		final TimeReporter t = new TimeReporter("Getting events", logger);

		if (offset < 0) {
			offset = 0;
		}
		final G_EntityQuery q = new G_EntityQuery();

		q.setFirstResult((long) offset);
		q.setMaxResult((long) limit);

		final G_PropertyMatchDescriptor timeRange = G_PropertyMatchDescriptor
				.newBuilder()
				.setKey("time")
				.setBoundedRange(
						G_BoundedRange.newBuilder().setType(G_PropertyType.DATE)
								.setStart(FastNumberUtils.parseLongWithCheck(minSecs, 0))
								.setEnd(FastNumberUtils.parseLongWithCheck(maxSecs, 0)).setInclusive(true).build())
				.build();
		final G_PropertyMatchDescriptor identifierList = G_PropertyMatchDescriptor
				.newBuilder()
				.setKey("identifiers")
				.setListRange(
						new ListRangeHelper(G_PropertyType.STRING, StringUtils.tokenizeToStringArray(identifiers, ",")))
				.build();

		final G_PropertyMatchDescriptor commentsList = G_PropertyMatchDescriptor
				.newBuilder()
				.setKey("comments")
				.setListRange(
						new ListRangeHelper(G_PropertyType.STRING, StringUtils.tokenizeToStringArray(comments, ",")))
				.build();

		final QueryHelper qh = new QueryHelper(timeRange, identifierList, commentsList);
		// final List<G_PropertyMatchDescriptor<String>> tupleList =
		// SearchTypeHelper.processSearchList(identifiers,
		// G_Constraint.CONTAINS);

		/*
		 * Note that we are purposefully using the same list for either side of
		 * the event. The intersectionOnly flag (default false) completes the
		 * nature of the request, which is to find all events with the
		 * identifiers on either side of the event.
		 */

		t.logElapsed();
		try {
			return dao.getAllTransactions(qh);
		} catch (final AvroRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public G_TransactionResults getEvents(final String from, final String to, int offset, final int limit,
			final String minSecs, final String maxSecs, final String comments, final boolean intersectionOnly) {
		final TimeReporter t = new TimeReporter("Getting events", logger);

		if (offset < 0) {
			offset = 0;
		}
		final G_EntityQuery q = new G_EntityQuery();
		q.setFirstResult((long) offset);
		q.setMaxResult((long) limit);
		// q.setFirstResult(offset);
		// q.setMaxResult(limit);
		// q.setMinSecs(FastNumberUtils.parseLongWithCheck(minSecs, 0));
		// q.setMaxSecs(FastNumberUtils.parseLongWithCheck(maxSecs, 0));
		final G_PropertyMatchDescriptor timeRange = G_PropertyMatchDescriptor
				.newBuilder()
				.setKey("time")
				.setBoundedRange(
						G_BoundedRange.newBuilder().setType(G_PropertyType.DATE)
								.setStart(FastNumberUtils.parseLongWithCheck(minSecs, 0))
								.setEnd(FastNumberUtils.parseLongWithCheck(maxSecs, 0)).setInclusive(true).build())
				.build();
		/*
		 * Note that the intersectionOnly flag (default true) completes the
		 * nature of this request, which is to only show events between specific
		 * identifiers on particular sides.
		 */
		// q.setIntersectionOnly(intersectionOnly);
		final G_PropertyMatchDescriptor fromList = G_PropertyMatchDescriptor.newBuilder().setKey("from")
				.setListRange(new ListRangeHelper(G_PropertyType.STRING, StringUtils.tokenizeToStringArray(from, ",")))
				.build();
		final G_PropertyMatchDescriptor toList = G_PropertyMatchDescriptor.newBuilder().setKey("to")
				.setListRange(new ListRangeHelper(G_PropertyType.STRING, StringUtils.tokenizeToStringArray(to, ",")))
				.build();
		// q.setSources(SearchTypeHelper.processSearchList(from,
		// G_Constraint.CONTAINS));
		// q.setDestinations(SearchTypeHelper.processSearchList(to,
		// G_Constraint.CONTAINS));
		// q.setPayloadKeywords(SearchTypeHelper.processSearchList(comments,
		// G_Constraint.CONTAINS));
		final G_PropertyMatchDescriptor commentsList = G_PropertyMatchDescriptor
				.newBuilder()
				.setKey("comments")
				.setListRange(
						new ListRangeHelper(G_PropertyType.STRING, StringUtils.tokenizeToStringArray(comments, ",")))
				.build();
		final QueryHelper qh = new QueryHelper(timeRange, fromList, toList, commentsList);
		G_TransactionResults s = null;
		try {
			s = dao.getAllTransactions(qh);
		} catch (final AvroRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		t.logAsCompleted();
		return s;
	}

	@PostInjection
	public void initialize() {
		logger.debug("Event Search now available");
	}

}
