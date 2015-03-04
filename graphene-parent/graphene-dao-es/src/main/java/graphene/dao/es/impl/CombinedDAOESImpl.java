/**
 * 
 */
package graphene.dao.es.impl;

import graphene.dao.CombinedDAO;
import graphene.dao.DocumentBuilder;
import graphene.dao.es.BasicESDAO;
import graphene.dao.es.ESRestAPIConnection;
import graphene.dao.es.JestModule;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_SearchResult;
import graphene.model.idl.G_SearchResults;
import graphene.model.idl.G_SymbolConstants;
import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * @author djue
 * 
 */
public class CombinedDAOESImpl extends BasicESDAO implements CombinedDAO {
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
		mapper = new ObjectMapper(); // can reuse, share globally
		setType(TYPE);
	}

	@Override
	public long count(final G_EntityQuery pq) throws Exception {
		if (ValidationUtils.isValid(pq) && ValidationUtils.isValid(pq.getAttributeList())) {
			pq.getAttributeList().get(0);
			String schema = pq.getTargetSchema();
			if (!ValidationUtils.isValid(schema)) {
				schema = c.getIndexName();
			}
			final String term = (String) pq.getAttributeList().get(0).getValue();
			final long x = c.performCount(null, host, schema, "_all", "", term);
			return x;
		}
		logger.warn("Did not find any values for " + pq);
		return 0;
	}

	@Override
	public G_SearchResult findById(final G_EntityQuery pq) {
		logger.debug("Query " + pq);

		String schema = pq.getTargetSchema();
		if (!ValidationUtils.isValid(schema)) {
			schema = indexName;
		}

		final String id = (String) pq.getAttributeList().get(0).getValue();
		final JestResult resultsById = getResultsById(id);
		final String jsonString = resultsById.getJsonString();
		G_SearchResult sr = null;
		if (jsonString != null) {
			JsonNode rootNode;
			try {
				rootNode = mapper.readValue(jsonString, JsonNode.class);

				int _totalResults = -1;
				if ((_totalResults == -1) && (rootNode != null) && (rootNode.get("hits") != null)
						&& (rootNode.get("hits").get("total") != null)) {
					_totalResults = rootNode.get("hits").get("total").asInt();
					logger.debug("Found " + _totalResults + " hits in hitparent!");
					final List<JsonNode> hits = rootNode.get("hits").findValues("hits");
					final ArrayNode actualListOfHits = (ArrayNode) hits.get(0);

					logger.debug("actualListOfHits was serialized into  " + actualListOfHits.size() + " object(s)");
					for (int i = 0; i < actualListOfHits.size(); i++) {
						final JsonNode currentHit = actualListOfHits.get(i);
						if (ValidationUtils.isValid(currentHit)) {
							final Object entity = db.buildSearchResultFromDocument(i, currentHit, pq);

							sr = G_SearchResult.newBuilder().setResult(entity).setScore(1.0d).build();
						}
					}
				} else {
					logger.warn("Response was unexpected: " + jsonString);
				}
			} catch (final IOException e) {
				logger.error("Mapping results from search: " + e.getMessage());
			}
		}
		return sr;
	}

	@Override
	public G_SearchResults findByQuery(final G_EntityQuery pq) throws Exception {

		return findByQueryWithMeta(pq);
	}

	@Override
	public G_SearchResults findByQueryWithMeta(final G_EntityQuery pq) throws Exception {

		final List<G_SearchResult> resultsList = new ArrayList<G_SearchResult>();

		final String _qResp = c.performQuery(null, host, pq);

		JsonNode rootNode;
		rootNode = mapper.readValue(_qResp, JsonNode.class);
		int totalNumberOfPossibleResults = 0;
		if ((rootNode != null) && (rootNode.get("hits") != null) && (rootNode.get("hits").get("total") != null)) {
			totalNumberOfPossibleResults = rootNode.get("hits").get("total").asInt();
			logger.debug("Found " + totalNumberOfPossibleResults + " hits in hitparent!");

			final List<JsonNode> hits = rootNode.get("hits").findValues("hits");
			final ArrayNode actualListOfHits = (ArrayNode) hits.get(0);

			for (int i = 0; i < actualListOfHits.size(); i++) {
				final JsonNode currentHit = actualListOfHits.get(i);
				if (ValidationUtils.isValid(currentHit)) {

					final Object entity = db.buildSearchResultFromDocument(i, currentHit, pq);
					resultsList.add(G_SearchResult.newBuilder().setResult(entity)
							.setScore(currentHit.get("_score").asDouble()).build());
				} else {
					logger.error("Invalid search result at index " + i + " for query " + pq.toString());
				}
			}
		}
		final G_SearchResults results = G_SearchResults.newBuilder().setResults(resultsList)
				.setTotal(totalNumberOfPossibleResults).build();

		return results;
	}

	@Override
	public G_SearchResults getAll(final long offset, final long maxResults) throws Exception {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public double getReadiness() {
		return 1.0;
	}

	@Override
	@PostInjection
	public void initialize() {
		setIndex(indexName);
		setType(TYPE);
		super.initialize();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	// @Override
	// public boolean performCallback(final long offset, final long maxResults,
	// final G_CallBack<Object, G_EntityQuery> cb,
	// final G_EntityQuery q) {
	// // TODO Auto-generated method stub
	// try {
	// for (final Object obj : findByQuery(q)) {
	// cb.callBack(obj, q);
	// }
	// } catch (final Exception e) {
	// logger.error("Error performing callback: ", e);
	// }
	// return true;
	// }

	@Override
	public void setReady(final boolean b) {
		// TODO Auto-generated method stub

	}
}
