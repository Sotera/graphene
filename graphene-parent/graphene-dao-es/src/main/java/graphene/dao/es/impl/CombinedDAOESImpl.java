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
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_SearchResult;
import graphene.model.idl.G_SearchResults;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_TransactionResults;
import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestResult;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
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
		mapper = new ObjectMapper(); // can reuse, share globally
		setType(TYPE);
	}

	//
	// @Override
	// public G_SearchResult findById(final G_EntityQuery pq) {
	// logger.debug("Query " + pq);
	//
	// String schema = pq.getTargetSchema();
	// if (!ValidationUtils.isValid(schema)) {
	// schema = indexName;
	// }
	//
	// final String id = (String)
	// pq.getPropertyMatchDescriptors().get(0).getValue();
	// final JestResult resultsById = getResultsById(id);
	// final String jsonString = resultsById.getJsonString();
	// G_SearchResult sr = null;
	// if (jsonString != null) {
	// JsonNode rootNode;
	// try {
	// rootNode = mapper.readValue(jsonString, JsonNode.class);
	//
	// int _totalResults = -1;
	// if ((_totalResults == -1) && (rootNode != null) && (rootNode.get("hits")
	// != null)
	// && (rootNode.get("hits").get("total") != null)) {
	// _totalResults = rootNode.get("hits").get("total").asInt();
	// logger.debug("Found " + _totalResults + " hits in hitparent!");
	// final List<JsonNode> hits = rootNode.get("hits").findValues("hits");
	// final ArrayNode actualListOfHits = (ArrayNode) hits.get(0);
	//
	// logger.debug("actualListOfHits was serialized into  " +
	// actualListOfHits.size() + " object(s)");
	// for (int i = 0; i < actualListOfHits.size(); i++) {
	// final JsonNode currentHit = actualListOfHits.get(i);
	// if (ValidationUtils.isValid(currentHit)) {
	// sr = db.buildSearchResultFromDocument(i, currentHit, pq);
	// }
	// }
	// } else {
	// logger.warn("Response was unexpected: " + jsonString);
	// }
	// } catch (final IOException e) {
	// logger.error("Mapping results from search: " + e.getMessage());
	// }
	// }
	// return sr;
	// }

	@Override
	public G_SearchResults findByQuery(final G_EntityQuery pq) {
		final G_SearchResults results = G_SearchResults.newBuilder().setTotal(0)
				.setResults(new ArrayList<G_SearchResult>()).build();

		final List<G_SearchResult> resultsList = new ArrayList<G_SearchResult>();
		JestResult jestResult = new JestResult(null);
		try {

			final io.searchbox.core.Search.Builder action = buildSearchAction(pq);
			jestResult = c.getClient().execute(action.build());

			// _qResp = c.performQuery(null, host, pq);
		} catch (final DataAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		JsonNode rootNode;
		try {
			rootNode = mapper.readValue(jestResult.toString(), JsonNode.class);

			int totalNumberOfPossibleResults = 0;
			if ((rootNode != null) && (rootNode.get("hits") != null) && (rootNode.get("hits").get("total") != null)) {
				totalNumberOfPossibleResults = rootNode.get("hits").get("total").asInt();
				logger.debug("Found " + totalNumberOfPossibleResults + " hits in hitparent!");

				final List<JsonNode> hits = rootNode.get("hits").findValues("hits");
				final ArrayNode actualListOfHits = (ArrayNode) hits.get(0);

				for (int i = 0; i < actualListOfHits.size(); i++) {
					final JsonNode currentHit = actualListOfHits.get(i);
					if (ValidationUtils.isValid(currentHit)) {
						final G_SearchResult result = db.buildSearchResultFromDocument(i, currentHit, pq);
						resultsList.add(result);
					} else {
						logger.error("Invalid search result at index " + i + " for query " + pq.toString());
					}
				}
			}
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		results.setResults(resultsList);
		results.setTotal((long) resultsList.size());

		return results;
	}

	@Override
	public Map<String, List<G_Entity>> getAccounts(final List<String> entities) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_SearchResults getAll(final long offset, final long maxResults) {
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
		setIndex(indexName);
		setType(TYPE);
		super.initialize();
	}

	@Override
	public boolean isReady() {
		return true;
	}

	@Override
	public G_SearchResults search(final List<G_PropertyMatchDescriptor> terms, final long start, final long max)
			throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Void setReady(final boolean b) {
		return null;
		// TODO Auto-generated method stub

	}
}
