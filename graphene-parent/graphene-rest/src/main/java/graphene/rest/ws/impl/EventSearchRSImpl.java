package graphene.rest.ws.impl;

import graphene.dao.UnifiedCommunicationEventDAO;
import graphene.model.idl.G_Link;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;
import graphene.model.query.DirectedEventQuery;
import graphene.model.query.SearchTypeHelper;
import graphene.rest.ws.EventSearchRS;
import graphene.util.FastNumberUtils;
import graphene.util.stats.TimeReporter;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class EventSearchRSImpl implements EventSearchRS {

	static Logger logger = LoggerFactory.getLogger(EventSearchRSImpl.class);

	@Inject
	private UnifiedCommunicationEventDAO dao;

	@Override
	public List<G_Link> getEvents(String identifiers, int offset, int limit,
			String minSecs, String maxSecs, String comments,
			boolean intersectionOnly) {
		TimeReporter t = new TimeReporter("Getting events", logger);

		if (offset < 0) {
			offset = 0;
		}
		DirectedEventQuery q = new DirectedEventQuery();
		q.setFirstResult(offset);
		q.setMaxResult(limit);
		q.setMinSecs(FastNumberUtils.parseLongWithCheck(minSecs, 0));
		q.setMaxSecs(FastNumberUtils.parseLongWithCheck(maxSecs, 0));
		q.setIntersectionOnly(intersectionOnly);

		List<G_SearchTuple<String>> tupleList = SearchTypeHelper
				.processSearchList(identifiers, G_SearchType.COMPARE_CONTAINS);

		/*
		 * Note that we are purposefully using the same list for either side of
		 * the event. The intersectionOnly flag (default false) completes the
		 * nature of the request, which is to find all events with the
		 * identifiers on either side of the event.
		 */
		q.setSources(tupleList);
		q.setDestinations(tupleList);

		q.setPayloadKeywords(SearchTypeHelper.processSearchList(comments,
				G_SearchType.COMPARE_CONTAINS));

		t.logElapsed();
		return dao.getLinks(q);

	}

	@Override
	public List<G_Link> getEvents(String from, String to, int offset,
			int limit, String minSecs, String maxSecs, String comments,
			boolean intersectionOnly) {
		TimeReporter t = new TimeReporter("Getting events", logger);

		if (offset < 0) {
			offset = 0;
		}
		DirectedEventQuery q = new DirectedEventQuery();
		q.setFirstResult(offset);
		q.setMaxResult(limit);
		q.setMinSecs(FastNumberUtils.parseLongWithCheck(minSecs, 0));
		q.setMaxSecs(FastNumberUtils.parseLongWithCheck(maxSecs, 0));
		/*
		 * Note that the intersectionOnly flag (default true) completes the
		 * nature of this request, which is to only show events between specific
		 * identifiers on particular sides.
		 */
		q.setIntersectionOnly(intersectionOnly);

		q.setSources(SearchTypeHelper.processSearchList(from,
				G_SearchType.COMPARE_CONTAINS));
		q.setDestinations(SearchTypeHelper.processSearchList(to,
				G_SearchType.COMPARE_CONTAINS));
		q.setPayloadKeywords(SearchTypeHelper.processSearchList(comments,
				G_SearchType.COMPARE_CONTAINS));

		List<G_Link> s = dao.getLinks(q);
		t.logAsCompleted();
		return s;
	}

}
