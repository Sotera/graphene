package graphene.dao.es;

import graphene.dao.LoggingDAO;
import graphene.model.idl.G_GraphViewEvent;
import graphene.model.idl.G_ReportViewEvent;
import graphene.model.idl.G_UserLoginEvent;
import graphene.model.query.BasicQuery;
import graphene.model.query.EntityQuery;
import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;

import java.util.ArrayList;
import java.util.List;

import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.interactions.TemporalGraphQuery;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoggingDAODefaultESImpl extends BasicESDAO implements LoggingDAO {

	@Inject
	@Symbol(JestModule.ES_LOGGING_INDEX)
	private String indexName;

	@Inject
	@Symbol(JestModule.ES_LOGGING_SEARCH_TYPE)
	private String searchQueryType;

	@Inject
	@Symbol(JestModule.ES_LOGGING_GRAPHQUERY_TYPE)
	private String graphQueryType;

	@Inject
	@Symbol(JestModule.ES_LOGGING_REPORT_VIEW_TYPE)
	private String reportViewType;
	@Inject
	@Symbol(JestModule.ES_LOGGING_USER_LOGIN_TYPE)
	private String userLoginType;

	@Inject
	@Symbol(JestModule.ES_LOGGING_EXPORT_TYPE)
	private String exportType;

	@Inject
	public LoggingDAODefaultESImpl(final ESRestAPIConnection c, final Logger logger) {
		auth = null;
		this.c = c;
		mapper = new ObjectMapper(); // can reuse, share globally
		this.logger = logger;
	}

	@Override
	public List<Object> getAllEvents(final String userId, final String partialTerm, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TemporalGraphQuery> getGraphQueries(final String userId, final String partialTerm, final int limit) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		if (ValidationUtils.isValid(userId)) {

			if (ValidationUtils.isValid(partialTerm)) {
				// use the partial name to filter
				searchSourceBuilder.query(QueryBuilders.filteredQuery(QueryBuilders.fuzzyQuery("value", partialTerm),
						FilterBuilders.andFilter(FilterBuilders.termFilter("userId", userId))));
			} else {
				// don't filter on name, get all of them.
				searchSourceBuilder.query(QueryBuilders.matchQuery("userId", userId));
			}
		} else {
			if (ValidationUtils.isValid(partialTerm)) {
				// use the partial name to filter
				searchSourceBuilder.query(QueryBuilders.fuzzyQuery("value", partialTerm));
			} else {
				searchSourceBuilder.query(QueryBuilders.matchAllQuery());
			}
		}
		final SortBuilder byDate = SortBuilders.fieldSort("timeInitiated").order(SortOrder.DESC).ignoreUnmapped(true);

		final Search search = new Search.Builder(searchSourceBuilder.sort(byDate).toString()).addIndex(indexName)
				.setParameter("timeout", defaultESTimeout).addType(graphQueryType).build();
		System.out.println(searchSourceBuilder.toString());
		JestResult result;
		List<TemporalGraphQuery> returnValue = new ArrayList<TemporalGraphQuery>(0);
		try {
			result = c.getClient().execute(search);
			returnValue = result.getSourceAsObjectList(TemporalGraphQuery.class);
			// for (final TemporalGraphQuery u : returnValue) {
			// System.out.println(u);
			// }
		} catch (final Exception e) {
			logger.error("getGraphQueries " + e.getMessage());
		}
		return returnValue;
	}

	@Override
	public List<G_GraphViewEvent> getGraphViewEvents(final String userId, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<EntityQuery> getQueries(final String userId, final String partialTerm, final int offset, final int limit) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		if (ValidationUtils.isValid(userId)) {

			if (ValidationUtils.isValid(partialTerm)) {
				// use the partial name to filter
				searchSourceBuilder.query(QueryBuilders.filteredQuery(QueryBuilders.fuzzyQuery("value", partialTerm),
						FilterBuilders.andFilter(FilterBuilders.termFilter("userId", userId))));
			} else {
				// don't filter on name, get all of them.
				searchSourceBuilder.query(QueryBuilders.matchQuery("userId", userId));
			}
		} else {
			if (ValidationUtils.isValid(partialTerm)) {
				// use the partial name to filter
				searchSourceBuilder.query(QueryBuilders.fuzzyQuery("value", partialTerm));
			} else {
				searchSourceBuilder.query(QueryBuilders.matchAllQuery());
			}
		}
		final SortBuilder byDate = SortBuilders.fieldSort("timeInitiated").order(SortOrder.DESC).ignoreUnmapped(false);

		final Search search = new Search.Builder(searchSourceBuilder.sort(byDate).toString()).addIndex(indexName)
				.setParameter("timeout", defaultESTimeout).addType(searchQueryType).setParameter("from", offset)
				.setParameter("size", limit).build();
		System.out.println(searchSourceBuilder.toString());
		JestResult result;
		List<EntityQuery> returnValue = new ArrayList<EntityQuery>(0);
		try {
			result = c.getClient().execute(search);
			// System.out.println(result);
			returnValue = result.getSourceAsObjectList(EntityQuery.class);

			// for (final EntityQuery u : returnValue) {
			// System.out.println(u);
			// }
		} catch (final Exception e) {
			logger.error("getQueries " + e.getMessage());
		}
		return returnValue;

	}

	@Override
	public List<G_ReportViewEvent> getReportViewEvents(final String userId, final int limit) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		if (ValidationUtils.isValid(userId)) {

			// don't filter on name, get all of them.
			searchSourceBuilder.query(QueryBuilders.matchQuery("userId", userId)).size(limit);

		} else {

			searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(limit);

		}
		final SortBuilder byDate = SortBuilders.fieldSort("timeInitiated").order(SortOrder.DESC).ignoreUnmapped(true);

		final Search search = new Search.Builder(searchSourceBuilder.sort(byDate).toString()).addIndex(indexName)
				.setParameter("timeout", defaultESTimeout).addType(reportViewType).build();
		System.out.println(searchSourceBuilder.toString());
		JestResult result;
		List<G_ReportViewEvent> returnValue = new ArrayList<G_ReportViewEvent>(0);
		try {
			result = c.getClient().execute(search);
			returnValue = result.getSourceAsObjectList(G_ReportViewEvent.class);
			// for (final G_ReportViewEvent u : returnValue) {
			// System.out.println(u);
			// }
		} catch (final Exception e) {
			logger.error("getReportViewEvents " + e.getMessage());
		}
		return returnValue;
	}

	@PostInjection
	@Override
	public void initialize() {
		setIndex(indexName);
		// default type, although we can have multiple types
		setType(searchQueryType);
		super.initialize();
	}

	@Override
	public boolean recordExport(final String queryString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void recordGraphViewEvent(final G_GraphViewEvent q) {
		if (ValidationUtils.isValid(q)) {
			if (q.getId() == null) {
				q.setId(saveObject(q, q.getId(), indexName, graphQueryType));
			}
			saveObject(q, q.getId(), indexName, graphQueryType);
		} else {
			logger.error("Attempted to save a null G_GraphViewEvent!");
		}
		return;

	}

	@Override
	public void recordLoginEvent(final G_UserLoginEvent e) {
		if (ValidationUtils.isValid(e)) {
			if (e.getId() == null) {
				e.setId(saveObject(e, e.getId(), indexName, userLoginType));
			}
			saveObject(e, e.getId(), indexName, userLoginType);
		} else {
			logger.error("Attempted to save a null G_UserLoginEvent!");
		}
		return;
	}

	@Override
	public void recordQuery(final BasicQuery q) {
		if (ValidationUtils.isValid(q)) {
			if (q.getId() == null) {
				q.setId(saveObject(q, q.getId(), indexName, searchQueryType));
			}
			saveObject(q, q.getId(), indexName, searchQueryType);
		} else {
			logger.error("Attempted to save a null BasicQuery!");
		}
		return;
	}

	@Override
	public void recordQuery(final V_GraphQuery q) {
		if (ValidationUtils.isValid(q)) {
			if (q.getId() == null) {
				q.setId(saveObject(q, q.getId(), indexName, graphQueryType));
			}
			saveObject(q, q.getId(), indexName, graphQueryType);
		} else {
			logger.error("Attempted to save a null V_GraphQuery!");
		}
		return;
	}

	@Override
	public void recordReportViewEvent(final G_ReportViewEvent q) {
		if (ValidationUtils.isValid(q)) {
			if (q.getId() == null) {
				q.setId(saveObject(q, q.getId(), indexName, reportViewType));
			}
			saveObject(q, q.getId(), indexName, reportViewType);
		} else {
			logger.error("Attempted to save a null G_ReportViewEvent!");
		}
		return;
	}

}
