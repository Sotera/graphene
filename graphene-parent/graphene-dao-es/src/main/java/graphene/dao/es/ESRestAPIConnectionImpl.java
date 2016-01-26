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

package graphene.dao.es;

import graphene.model.idl.G_SymbolConstants;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;
import io.searchbox.core.Search.Builder;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.slf4j.Logger;

public class ESRestAPIConnectionImpl implements ESRestAPIConnection {
	public static final float DEFAULT_MIN_SCORE = 0.5f;

	@Inject
	private Logger logger;

	@Inject
	private JestClient client;

	@Inject
	@Symbol(JestModule.ES_SEARCH_INDEX)
	private String indexName;
	@Inject
	@Symbol(JestModule.ES_DEFAULT_TIMEOUT)
	private String defaultESTimeout;

	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_MAX_SEARCH_RESULTS)
	private Long maxSearchResults;

	// private String createCleanUrl(@Nullable final String basicAuth, final
	// String baseUrl) {
	// if (basicAuth != null) {
	// logger.debug("Auth information provided, using auth info.");
	// final String cleanAuth = basicAuth.replaceAll("@", "%40");
	// final String cleanURL = baseUrl.replace("http://", "http://" + cleanAuth
	// + "@");
	// return cleanURL;
	// }
	// logger.debug("No auth information provided, using without auth info.");
	// return baseUrl;
	// }

	@Override
	public void createIndex(final String indexName, final int shards, final int replicas) {
		try {
			if ((shards > 0) && (replicas > 0)) {
				logger.debug("Creating index " + indexName + " with " + shards + "shard(s) and " + replicas
						+ " replica(s).");
				final ImmutableSettings.Builder sb = ImmutableSettings.settingsBuilder();
				sb.put("number_of_shards", shards);
				sb.put("number_of_replicas", replicas);
				client.execute(new CreateIndex.Builder(indexName).settings(sb.build().getAsMap())
						.setParameter("timeout", defaultESTimeout).build());
			} else {
				logger.debug("Creating index " + indexName + " with default settings");
				client.execute(new CreateIndex.Builder(indexName).setParameter("timeout", defaultESTimeout).build());
			}
		} catch (final Exception e) {
			logger.error("createIndex " + e.getMessage());
		}
	}

	@Override
	public void createIndex(final String indexName, final String settings) {
		try {
			if (settings != null) {
				logger.debug("Creating index " + indexName + " with settings " + settings);
				client.execute(new CreateIndex.Builder(indexName).settings(
						ImmutableSettings.builder().loadFromSource(settings)).build());
			} else {
				logger.debug("Creating index " + indexName + " with default settings");
				client.execute(new CreateIndex.Builder(indexName).build());
			}
		} catch (final Exception e) {
			logger.error("createIndex " + e.getMessage());
		}
	}

	@Override
	public void deleteIndex(final String indexName) throws Exception {
		final DeleteIndex deleteIndex = new DeleteIndex.Builder(indexName).setParameter("timeout", defaultESTimeout)
				.build();
		client.execute(deleteIndex);
	}

	private String executeAction(final Builder sb) {
		final Search action = sb.setParameter("timeout", defaultESTimeout).build();
		logger.debug("Action:\n" + action.toString());
		SearchResult result;
		String resultString = null;
		try {
			result = client.execute(action);
			if (result != null) {
				resultString = result.getJsonString();
			} else {
				logger.error("Result string was null for action " + action.toString());
			}
		} catch (final Exception e) {
			logger.error("executeAction " + e.getMessage());
		}
		return resultString;
	}

	/**
	 * @return the client
	 */
	@Override
	public final JestClient getClient() {
		return client;
	}

	@Override
	public String getIndexName() {
		return indexName;
	}

	// @Override
	// public long performCount(@Nullable final String basicAuth, final String
	// baseUrl, final String index,
	// final String type, final String fieldName, final String term) throws
	// DataAccessException {
	// try {
	// final io.searchbox.core.Count.Builder action = new Count.Builder()
	// .setParameter("timeout", defaultESTimeout);
	// if (ValidationUtils.isValid(term)) {
	// final SearchSourceBuilder searchSourceBuilder = new
	// SearchSourceBuilder();
	//
	// QueryBuilder qb;
	// if (ValidationUtils.isValid(fieldName)) {
	// qb = QueryBuilders.matchPhraseQuery(fieldName, term);
	// } else {
	// qb = QueryBuilders.matchPhraseQuery("_all", term);
	// }
	// final SearchSourceBuilder query = searchSourceBuilder.query(qb);
	// action.query(query.toString());
	// logger.debug(query.toString());
	// }
	// if (ValidationUtils.isValid(index)) {
	// action.addIndex(index);
	// logger.debug("index: " + index);
	// }
	// if (ValidationUtils.isValid(type)) {
	// action.addType(type);
	// logger.debug("type: " + type);
	// }
	//
	// final CountResult result = client.execute(action.build());
	// final long longCount = result.getCount().longValue();
	// logger.debug("Found a count of: " + longCount);
	// return longCount;
	// } catch (final Exception e) {
	// logger.error("performCount " + e.getMessage());
	// throw new DataAccessException(
	// "Could not connect to one of the external resources needed for your request: "
	// + e.getMessage());
	// }
	// }

	// @Override
	// public String performQuery(final String basicAuth, final String baseurl,
	// final G_EntityQuery q)
	// throws DataAccessException {
	//
	// final String retval = null;
	//
	// return retval;
	// }

	//
	// @Override
	// public String performQuery(@Nullable final String basicAuth, final String
	// baseurl, final String index,
	// final String type, final String fieldName, final String term, final long
	// from, final long size)
	// throws DataAccessException {
	// try {
	// final SearchSourceBuilder = new SearchSourceBuilder();
	// final QueryBuilder qb = QueryBuilders.matchQuery(fieldName, term);
	// searchSourceBuilder.query(qb);
	// final Builder sb = new
	// Search.Builder(searchSourceBuilder.toString()).addIndex(index)
	// .setParameter("timeout", defaultESTimeout).setParameter("from",
	// from).setParameter("size", size);
	// return executeAction(sb);
	// } catch (final Exception e) {
	// logger.error("performQuery " + e.getMessage());
	// throw new DataAccessException(
	// "Could not connect to one of the external resources needed for your request: "
	// + e.getMessage());
	// }
	// }

	/**
	 * @param client
	 *            the client to set
	 */
	public final void setClient(final JestClient client) {
		this.client = client;
	}

	@Override
	public void setIndexName(final String indexName) {
		this.indexName = indexName;
	}
}
