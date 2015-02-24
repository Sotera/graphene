/**
 * 
 */
package graphene.dao.es.impl;

import graphene.dao.CombinedDAO;
import graphene.dao.DocumentBuilder;
import graphene.dao.es.BasicESDAO;
import graphene.dao.es.ESRestAPIConnection;
import graphene.dao.es.JestModule;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.query.EntityQuery;
import graphene.model.view.GrapheneResults;
import graphene.util.G_CallBack;
import graphene.util.validator.ValidationUtils;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
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
	public long count(final EntityQuery pq) throws Exception {
		if (ValidationUtils.isValid(pq) && ValidationUtils.isValid(pq.getAttributeList())) {
			pq.getAttributeList().get(0);
			String schema = pq.getSchema();
			if (!ValidationUtils.isValid(schema)) {
				schema = c.getIndexName();
			}
			final String term = pq.getAttributeList().get(0).getValue();
			final long x = c.performCount(null, host, schema, "_all", "", term);
			return x;
		}
		logger.warn("Did not find any values for " + pq);
		return 0;
	}

	@Override
	public List<Object> findById(final EntityQuery pq) {
		logger.debug("Query " + pq);
		final List<Object> objects = new ArrayList<Object>();
		String schema = pq.getSchema();
		if (!ValidationUtils.isValid(schema)) {
			schema = indexName;
		}
		String jsonString = null;
		if ((pq.getAttributeList().size() == 1)) {
			final G_SearchTuple<String> tuple = pq.getAttributeList().get(0);
			jsonString = getResultsById(tuple.getValue(), schema, null).getJsonString();
			logger.debug(jsonString);
		} else {
			final StringBuffer sb = new StringBuffer();
			// Dead simple, just coalesces the values as one long phrase
			for (final G_SearchTuple<String> qi : pq.getAttributeList()) {
				sb.append(qi.getValue() + " ");
			}
			final String terms = sb.toString().trim();
			if (ValidationUtils.isValid(terms)) {
				logger.debug("Searching for terms: " + terms + " from query " + pq);
				// Let's decide that at least half of the terms listed need to
				// appear.
				// Integer halfTerms = pq.getAttributeList().size() / 2;
				// if (halfTerms <= 1) {
				// halfTerms = 1;
				// }

				final MatchQueryBuilder qbc = QueryBuilders.matchPhraseQuery("_all", terms.toString());

				final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
				searchSourceBuilder.query(qbc);
				if (pq.getMaxResult() == 0) {
					logger.warn("NO MAX RESULT SUPPLIED FOR EntityQuery!  Setting to one.");
					pq.setMaxResult(1l);
				}
				logger.debug("SSB: \n" + searchSourceBuilder.toString());
				final Search action = new Search.Builder(searchSourceBuilder.toString()).addIndex(indexName)
						.setParameter("from", pq.getFirstResult()).setParameter("size", pq.getMaxResult())
						.setParameter("timeout", defaultESTimeout).build();
				logger.debug("Action:\n" + action.toString());
				SearchResult result;
				try {
					result = c.getClient().execute(action);
					jsonString = result.getJsonString();
				} catch (final Exception e) {
					logger.error("Error executing search: " + e.getMessage());
				}

			}
		}
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
							final Object entity = db.buildEntityFromDocument(i, currentHit);
							objects.add(entity);
						}
					}
				} else {
					logger.warn("Response was unexpected: " + jsonString);
				}
			} catch (final IOException e) {
				logger.error("Mapping results from search: " + e.getMessage());
			}
		}
		return objects;
	}

	@Override
	public List<Object> findByQuery(final EntityQuery pq) throws Exception {
		final GrapheneResults<Object> gr = findByQueryWithMeta(pq);
		return gr.getResults();
	}

	@Override
	public GrapheneResults<Object> findByQueryWithMeta(final EntityQuery pq) throws Exception {
		final GrapheneResults<Object> results = new GrapheneResults<Object>();
		final List<Object> objects = new ArrayList<Object>();

		final String _qResp = c.performQuery(null, host, pq);

		JsonNode rootNode;
		rootNode = mapper.readValue(_qResp, JsonNode.class);
		int totalNumberOfPossibleResults = 0;
		if ((rootNode != null) && (rootNode.get("hits") != null) && (rootNode.get("hits").get("total") != null)) {
			totalNumberOfPossibleResults = rootNode.get("hits").get("total").asInt();
			logger.debug("Found " + totalNumberOfPossibleResults + " hits in hitparent!");
			results.setNumberOfResultsTotal(totalNumberOfPossibleResults);
			final List<JsonNode> hits = rootNode.get("hits").findValues("hits");
			final ArrayNode actualListOfHits = (ArrayNode) hits.get(0);

			// Go through the results, and keep a map of
			// id->DocumentGraphParser.SCORE, as well as
			// ids to fetch
			logger.debug("actualListOfHits was serialized into  " + actualListOfHits.size() + " object(s)");
			results.setNumberOfResultsReturned(actualListOfHits.size());
			for (int i = 0; i < actualListOfHits.size(); i++) {
				final JsonNode currentHit = actualListOfHits.get(i);
				if (ValidationUtils.isValid(currentHit)) {
					final Object entity = db.buildEntityFromDocument(i, currentHit);
					objects.add(entity);
				} else {
					logger.error("Invalid search result at index " + i + " for query " + pq.toString());
				}
			}
		}

		results.setResults(objects);
		return results;
	}

	@Override
	public List<Object> getAll(final long offset, final long maxResults) throws Exception {
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

	@Override
	public boolean performCallback(final long offset, final long maxResults, final G_CallBack<Object, EntityQuery> cb,
			final EntityQuery q) {
		// TODO Auto-generated method stub
		try {
			for (final Object obj : findByQuery(q)) {
				cb.callBack(obj, q);
			}
		} catch (final Exception e) {
			logger.error("Error performing callback: ", e);
		}
		return true;
	}

	@Override
	public void setReady(final boolean b) {
		// TODO Auto-generated method stub

	}
}
