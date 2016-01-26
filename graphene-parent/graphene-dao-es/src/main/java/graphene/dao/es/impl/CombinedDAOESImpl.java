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

/**
 * 
 */
package graphene.dao.es.impl;

import graphene.business.commons.exception.DataAccessException;
import graphene.dao.DocumentBuilder;
import graphene.dao.es.BasicESDAO;
import graphene.dao.es.ESRestAPIConnection;
import graphene.dao.es.JestModule;
import graphene.model.idl.G_DataAccess;
import graphene.model.idl.G_DateRange;
import graphene.model.idl.G_DirectionFilter;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_LevelOfDetail;
import graphene.model.idl.G_Link;
import graphene.model.idl.G_LinkEntityTypeFilter;
import graphene.model.idl.G_LinkTag;
import graphene.model.idl.G_PropertyDescriptors;
import graphene.model.idl.G_SearchResult;
import graphene.model.idl.G_SearchResults;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_TransactionResults;
import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.avro.AvroRemoteException;
import org.apache.commons.collections.CollectionUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * This will become like the influent kiva dataAccessDataView class, which is a
 * base implementation (in this case, for an ES backend)
 * 
 * 
 * 
 * For G_EntityQuery, property match descriptors:
 * 
 * TargetSchema = Index
 * 
 * Key = type
 * 
 * Variable = field
 * 
 * @author djue
 * 
 */
public class CombinedDAOESImpl extends BasicESDAO implements G_DataAccess {
	private static final String TYPE = "_all";

	@Inject
	@Symbol(JestModule.ES_DEFAULT_TIMEOUT)
	protected String defaultESTimeout;

	@Inject
	@Symbol(JestModule.ES_SEARCH_INDEX)
	private String indexName;

	@Inject
	@Symbol(JestModule.ES_SERVER)
	private String host;

	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_MAX_SEARCH_RESULTS)
	private Long maxSearchResults;

	@Inject
	private DocumentBuilder db;

	@Inject
	public CombinedDAOESImpl(final ESRestAPIConnection c, final Logger logger) {
		this.logger = logger;
		auth = null;
		this.c = c;

	}

	@Override
	public Map<String, List<G_Entity>> getAccounts(final List<String> entities) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_TransactionResults getAllTransactions(final G_EntityQuery q) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_PropertyDescriptors getDescriptors() throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_Entity> getEntities(final List<String> entities, final G_LevelOfDetail levelOfDetail)
			throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<G_Link>> getFlowAggregation(final List<String> entities, final List<String> focusEntities,
			final G_DirectionFilter direction, final G_LinkEntityTypeFilter entityType, final G_LinkTag tag,
			final G_DateRange date) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getReadiness() {
		return 1.0;
	}

	@Override
	public Map<String, List<G_Link>> getTimeSeriesAggregation(final List<String> entities,
			final List<String> focusEntities, final G_LinkTag tag, final G_DateRange date) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	@PostInjection
	public void initialize() {
		logger.debug("Doing specific initialization!");
		setIndex(indexName);
		setType(TYPE);
		super.initialize();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public G_SearchResults search(final G_EntityQuery pq) {
		// TODO: Use a helper class
		final G_SearchResults results = G_SearchResults.newBuilder().setTotal(0)
				.setResults(new ArrayList<G_SearchResult>()).build();

		final List<G_SearchResult> resultsList = new ArrayList<G_SearchResult>();
		JestResult jestResult = new JestResult(null);
		try {
			final io.searchbox.core.Search.Builder action = buildSearchAction(pq);
			JestClient jestClient = c.getClient();
			synchronized(jestClient) {
			    jestResult = jestClient.execute(action.build());
			}
		} catch (final DataAccessException e) {
			e.printStackTrace();
		} catch (final Exception e) {
			e.printStackTrace();
		}
		// logger.debug(jestResult.getJsonString());
		JsonNode rootNode;
		long totalNumberOfPossibleResults = 0l;
		try {
			rootNode = mapper.readValue(jestResult.getJsonString(), JsonNode.class);

			if ((rootNode != null) && (rootNode.get("hits") != null) && (rootNode.get("hits").get("total") != null)) {
				totalNumberOfPossibleResults = rootNode.get("hits").get("total").asLong();
				logger.debug("Found " + totalNumberOfPossibleResults + " hits in hitparent!");
				final ArrayNode actualListOfHits = getListOfHitsFromResult(jestResult);

				for (int i = 0; i < actualListOfHits.size(); i++) {
					final JsonNode currentHit = actualListOfHits.get(i);
					if (ValidationUtils.isValid(currentHit)) {
						final G_SearchResult result = db.buildSearchResultFromDocument(i, currentHit, pq);
						if (result == null) {
							logger.error("could not build search result from hit " + currentHit.toString());
						}
						CollectionUtils.addIgnoreNull(resultsList, result);
					} else {
						logger.error("Invalid search result at index " + i + " for query " + pq.toString());
					}
				}
			}
		} catch (final IOException e) {
			e.printStackTrace();
		}
		results.setResults(resultsList);
		results.setTotal(totalNumberOfPossibleResults);
		return results;
	}

	@Override
	public Void setReady(final boolean b) {
		return null;
		// TODO Auto-generated method stub

	}
}
