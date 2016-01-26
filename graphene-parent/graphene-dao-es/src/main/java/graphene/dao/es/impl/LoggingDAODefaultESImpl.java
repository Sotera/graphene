/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.dao.es.impl;

import graphene.dao.LoggingDAO;
import graphene.dao.es.BasicESDAO;
import graphene.dao.es.ESRestAPIConnection;
import graphene.dao.es.JestModule;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_EntityQueryEvent;
import graphene.model.idl.G_ExportEvent;
import graphene.model.idl.G_GraphViewEvent;
import graphene.model.idl.G_ReportViewEvent;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_UserLoginEvent;
import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import mil.darpa.vande.interactions.TemporalGraphQuery;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.SortBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.elasticsearch.search.sort.SortOrder;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoggingDAODefaultESImpl extends BasicESDAO implements LoggingDAO {

	ExecutorService executor = Executors.newSingleThreadExecutor();
	@Inject
	@Symbol(JestModule.ES_LOGGING_INDEX)
	private String indexName;

	@Inject
	@Symbol(JestModule.ES_LOGGING_SEARCH_TYPE)
	private String searchQueryType;
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_DELETE_LOGS)
	private boolean enableDelete;
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_LOGGING)
	private boolean enableLogging;

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
	
	@PostInjection
	public void listenForShutdown(RegistryShutdownHub hub) {
		hub.addRegistryShutdownListener(new Runnable() {
			public void run() {
				executor.shutdown();
			}
		});
	}

	@Override
	public boolean delete(final String id) {
		if (enableDelete) {
			return super.delete(id);
		} else {
			logger.debug("Delete disabled.");
			return false;
		}
	}

	@Override
	public List<Object> getAllEvents(final String userId, final String partialTerm, final int offset, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TemporalGraphQuery> getGraphQueries(final String userId, final String partialTerm, final int offset,
			final int limit) {
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

		final Search search = new Search.Builder(searchSourceBuilder.sort(byDate).from(offset).size(limit).toString())
				.addIndex(indexName).setParameter("timeout", defaultESTimeout).addType(graphQueryType).build();
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
	public List<G_GraphViewEvent> getGraphViewEvents(final String userId, final int offset, final int limit) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		if (ValidationUtils.isValid(userId)) {
			searchSourceBuilder.query(QueryBuilders.matchQuery("userId", userId));
		} else {
			searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		}
		final SortBuilder byDate = SortBuilders.fieldSort("timeInitiated").order(SortOrder.DESC).ignoreUnmapped(false);

		final Search search = new Search.Builder(searchSourceBuilder.sort(byDate).from(offset).size(limit).toString())
				.addIndex(indexName).setParameter("timeout", defaultESTimeout).addType(graphQueryType)
				.setParameter("from", 0).setParameter("size", limit).build();
		System.out.println(searchSourceBuilder.toString());
		JestResult result;
		List<G_GraphViewEvent> returnValue = new ArrayList<G_GraphViewEvent>(0);
		try {
			result = c.getClient().execute(search);
			returnValue = result.getSourceAsObjectList(G_GraphViewEvent.class);
		} catch (final Exception e) {
			logger.error("getGraphViewEvents " + e.getMessage());
		}
		return returnValue;

	}

	@Override
	public List<G_EntityQuery> getQueries(final String userId, final String partialTerm, final int offset,
			final int limit) {
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
		final String ssb = searchSourceBuilder.sort(byDate).from(offset).size(limit).toString();
		final Search search = new Search.Builder(ssb).addIndex(indexName).setParameter("timeout", defaultESTimeout)
				.addType(searchQueryType).setParameter("from", offset).setParameter("size", limit).build();
		System.out.println("index: " + indexName + " type:" + searchQueryType);
		System.out.println(ssb);
		System.out.println(search.getURI());
		JestResult result;
		List<G_EntityQuery> returnValue = new ArrayList<G_EntityQuery>(0);
		try {
			result = c.getClient().execute(search);
			// System.out.println(result);
			returnValue = result.getSourceAsObjectList(G_EntityQuery.class);

			// for (final G_EntityQuery u : returnValue) {
			// System.out.println(u);
			// }
		} catch (final Exception e) {
			logger.error("getQueries " + e.getMessage());
			e.printStackTrace();
		}
		return returnValue;

	}

	@Override
	public List<G_ReportViewEvent> getReportViewEvents(final String userId, final int offset, final int limit) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		if (ValidationUtils.isValid(userId)) {

			// don't filter on name, get all of them.
			searchSourceBuilder.query(QueryBuilders.matchQuery("userId", userId));

		} else {

			searchSourceBuilder.query(QueryBuilders.matchAllQuery());

		}
		final SortBuilder byDate = SortBuilders.fieldSort("timeInitiated").order(SortOrder.DESC).ignoreUnmapped(true);

		final Search search = new Search.Builder(searchSourceBuilder.sort(byDate).from(offset).size(limit).toString())
				.addIndex(indexName).setParameter("timeout", defaultESTimeout).addType(reportViewType).build();
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

	@Override
	public List<G_UserLoginEvent> getUserLoginEvents(final String userId, final int offset, final int limit) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		if (ValidationUtils.isValid(userId)) {
			searchSourceBuilder.query(QueryBuilders.matchQuery("userId", userId));
		} else {
			searchSourceBuilder.query(QueryBuilders.matchAllQuery());
		}
		final SortBuilder byDate = SortBuilders.fieldSort("timeInitiated").order(SortOrder.DESC).ignoreUnmapped(false);

		final Search search = new Search.Builder(searchSourceBuilder.sort(byDate).from(offset).size(limit).toString())
				.addIndex(indexName).setParameter("timeout", defaultESTimeout).addType(userLoginType)
				.setParameter("from", offset).setParameter("size", limit).build();
		System.out.println(searchSourceBuilder.toString());
		JestResult result;
		List<G_UserLoginEvent> returnValue = new ArrayList<G_UserLoginEvent>(0);
		try {
			result = c.getClient().execute(search);
			returnValue = result.getSourceAsObjectList(G_UserLoginEvent.class);
		} catch (final Exception e) {
			logger.error("getGraphViewEvents " + e.getMessage());
		}
		return returnValue;
	}

	@Override
	@PostInjection
	public void initialize() {
		setIndex(indexName);
		setType(searchQueryType);
		super.initialize();
	}

	@Override
	public void recordExportEvent(final G_ExportEvent q) {
		if (enableLogging) {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					saveObject(q, q.getId(), indexName, graphQueryType, false);
				}
			});
		}
	}

	@Override
	public void recordGraphViewEvent(final G_GraphViewEvent q) {
		if (enableLogging) {
			if (ValidationUtils.isValid(q)) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						saveObject(q, q.getId(), indexName, graphQueryType, false);
					}
				});
				// q.setId(saveObject(q, q.getId(), indexName, graphQueryType,
				// false));
				// }
				// saveObject(q, q.getId(), indexName, graphQueryType);
			} else {
				logger.error("Attempted to save a null G_GraphViewEvent!");
			}
		}
		return;

	}

	@Override
	public void recordQueryEvent(final G_EntityQueryEvent q) {
		if (enableLogging) {
			if (ValidationUtils.isValid(q)) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						saveObject(q, q.getQ().getId(), indexName, searchQueryType, false);
					}
				});
			} else {
				logger.error("Attempted to save a null BasicQuery!");
			}
		}
		return;
	}

	@Override
	public void recordReportViewEvent(final G_ReportViewEvent q) {
		if (enableLogging) {
			if (ValidationUtils.isValid(q)) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						saveObject(q, q.getId(), indexName, reportViewType, false);
					}
				});

			} else {
				logger.error("Attempted to save a null G_ReportViewEvent!");
			}
		}
		return;
	}

	@Override
	public void recordUserLoginEvent(final G_UserLoginEvent e) {
		if (enableLogging) {
			if (ValidationUtils.isValid(e)) {
				executor.execute(new Runnable() {
					@Override
					public void run() {
						saveObject(e, e.getId(), indexName, userLoginType, false);
					}
				});
			} else {
				logger.error("Attempted to save a null G_UserLoginEvent!");
			}
		}
		return;
	}

}
