package graphene.dao.es;

import graphene.business.commons.ReportViewEvent;
import graphene.dao.LoggingDAO;
import graphene.model.query.BasicQuery;
import graphene.model.query.EntityQuery;
import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.client.JestResultHandler;
import io.searchbox.core.Index;
import io.searchbox.core.Search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

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
import org.joda.time.DateTime;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoggingDAODefaultESImpl extends BasicESDAO implements LoggingDAO {
	// private static final String SEARCHQUERYTYPE = "search";
	// private static final String GRAPHQUERYTYPE = "graphquery";
	// private static final String REPORTVIEWTYPE = "reportview";
	// private static final String EXPORTTYPE = "export";
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
	@Symbol(JestModule.ES_LOGGING_EXPORT_TYPE)
	private String exportType;

	@Inject
	public LoggingDAODefaultESImpl(final ESRestAPIConnection c, final JestClient jestClient, final Logger logger) {
		auth = null;
		this.c = c;
		this.jestClient = jestClient;
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
				.addType(graphQueryType).build();
		System.out.println(searchSourceBuilder.toString());
		JestResult result;
		List<TemporalGraphQuery> returnValue = new ArrayList<TemporalGraphQuery>(0);
		try {
			result = jestClient.execute(search);
			returnValue = result.getSourceAsObjectList(TemporalGraphQuery.class);
			for (final TemporalGraphQuery u : returnValue) {
				System.out.println(u);
			}
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue;
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
				.addType(searchQueryType).setParameter("from", offset).setParameter("size", limit).build();
		System.out.println(searchSourceBuilder.toString());
		JestResult result;
		List<EntityQuery> returnValue = new ArrayList<EntityQuery>(0);
		try {
			result = jestClient.execute(search);
			System.out.println(result);
			returnValue = result.getSourceAsObjectList(EntityQuery.class);

			for (final EntityQuery u : returnValue) {
				System.out.println(u);
			}
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returnValue;

	}

	@Override
	public List<ReportViewEvent> getReportViewEvents(final String userId, final int limit) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		if (ValidationUtils.isValid(userId)) {

			// don't filter on name, get all of them.
			searchSourceBuilder.query(QueryBuilders.matchQuery("userId", userId));

		} else {

			searchSourceBuilder.query(QueryBuilders.matchAllQuery());

		}
		final SortBuilder byDate = SortBuilders.fieldSort("timeInitiated").order(SortOrder.DESC).ignoreUnmapped(true);

		final Search search = new Search.Builder(searchSourceBuilder.sort(byDate).toString()).addIndex(indexName)
				.addType(reportViewType).build();
		System.out.println(searchSourceBuilder.toString());
		JestResult result;
		List<ReportViewEvent> returnValue = new ArrayList<ReportViewEvent>(0);
		try {
			result = jestClient.execute(search);
			returnValue = result.getSourceAsObjectList(ReportViewEvent.class);
			for (final ReportViewEvent u : returnValue) {
				System.out.println(u);
			}
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
	public boolean recordLogin(final String userName, final DateTime date) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void recordQuery(final BasicQuery q) {
		Index saveAction;
		if (ValidationUtils.isValid(q)) {
			if (!ValidationUtils.isValid(q.getId())) {
				// auto id
				saveAction = new Index.Builder(q).index(indexName).type(searchQueryType).build();
			} else {
				// use id that was provided
				saveAction = new Index.Builder(q).index(indexName).id(q.getId()).type(searchQueryType).build();
			}
			try {
				jestClient.executeAsync(saveAction, new JestResultHandler() {

					@Override
					public void completed(final Object result) {
						logger.debug("Logging to server completed.");
					}

					@Override
					public void failed(final Exception ex) {
						logger.error("Error saving log to server! " + ex.getMessage());
					}

				});

			} catch (ExecutionException | InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			logger.error("Attempted to save a null user object!");
		}
		return;
	}

	@Override
	public boolean recordQuery(final String queryString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void recordQuery(final V_GraphQuery q) {
		Index saveAction;
		if (ValidationUtils.isValid(q)) {

			if (!ValidationUtils.isValid(q.getId())) {
				// auto id
				saveAction = new Index.Builder(q).index(indexName).type(graphQueryType).build();
			} else {
				// use id that was provided
				saveAction = new Index.Builder(q).index(indexName).id(q.getId()).type(graphQueryType).build();
			}

			try {
				jestClient.executeAsync(saveAction, new JestResultHandler() {

					@Override
					public void completed(final Object result) {
						logger.debug("Logging to server completed.");
					}

					@Override
					public void failed(final Exception ex) {
						logger.error("Error saving log to server! " + ex.getMessage());
					}

				});

			} catch (ExecutionException | InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			logger.error("Attempted to save a null user object!");
		}
		return;
	}

	@Override
	public void recordReportViewEvent(final ReportViewEvent q) {
		Index saveAction;
		if (ValidationUtils.isValid(q)) {
			if (!ValidationUtils.isValid(q.getId())) {
				// auto id
				saveAction = new Index.Builder(q).index(indexName).type(reportViewType).build();
			} else {
				// use id that was provided
				saveAction = new Index.Builder(q).index(indexName).id(q.getId()).type(reportViewType).build();
			}
			try {
				final JestResult result = jestClient.execute(saveAction);
				if (!ValidationUtils.isValid(q.getId()) && ValidationUtils.isValid(result.getValue("_id"))) {

				}
			} catch (ExecutionException | InterruptedException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (final Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			logger.error("Attempted to save a null user object!");
		}
		return;

	}

}
