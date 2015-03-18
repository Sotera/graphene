package graphene.dao.es;

import graphene.dao.DocumentBuilder;
import graphene.model.idl.G_CallBack;
import graphene.model.idl.G_Constraint;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_ListRange;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SearchResult;
import graphene.model.idl.G_SingletonRange;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idlhelper.ListRangeHelper;
import graphene.model.idlhelper.PropertyMatchDescriptorHelper;
import graphene.model.idlhelper.QueryHelper;
import graphene.model.idlhelper.SingletonRangeHelper;
import graphene.util.stats.TimeReporter;
import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestResult;
import io.searchbox.core.Count;
import io.searchbox.core.CountResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.Search.Builder;
import io.searchbox.core.SearchScroll;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.params.Parameters;
import io.searchbox.params.SearchType;

import java.io.IOException;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.google.gson.JsonObject;

/**
 * Note that when you extend this class, do not have a logger in your extended
 * class. Use the logger provided here, and see other implementations' examples
 * on how to set up the constructor and post injection initializer.
 * 
 * @author djue
 * @param <T>
 * @param <QUERYOBJECT>
 * 
 */
public class BasicESDAO<T extends JestResult> {

	public static final String ESTYPE = "estype";
	private static final int MAX_TO_GET_AT_ONCE = 1000000;
	private static final int PAGESIZE = 200;
	public static final String ESID = "esid";
	protected ObjectMapper mapper;
	private String index;
	protected Logger logger;
	protected ESRestAPIConnection c;
	protected String auth = null;
	protected String type = "_all";
	@Inject
	@Symbol(JestModule.ES_READ_DELAY_MS)
	protected long ES_READ_DELAY_MS;
	@Inject
	@Symbol(JestModule.ES_DEFAULT_TIMEOUT)
	protected String defaultESTimeout;
	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_MAX_SEARCH_RESULTS)
	protected long defaultMaxSearchResults;
	@Inject
	@Symbol(JestModule.ES_SERVER)
	private String host;
	@Inject
	private DocumentBuilder db;

	protected BoolQueryBuilder buildBooleanConstraints(final PropertyMatchDescriptorHelper pmdh, BoolQueryBuilder bool) {

		final String key = pmdh.getKey();
		final Object r = pmdh.getRange();
		final List<Object> values = ListRangeHelper.rangeValue(r);

		boolean createdNew = false;
		if (bool == null) {
			bool = QueryBuilders.boolQuery();
			createdNew = true;
		}
		boolean constraintUsed = false;
		for (final Object text : values) {

			final G_Constraint constraint = pmdh.getConstraint();
			logger.debug("Using constraint " + constraint + " with key " + key + " on value " + text);
			switch (constraint) {
			case REQUIRED_EQUALS:
				bool = bool.must(QueryBuilders.matchPhraseQuery(key, text));
				constraintUsed = true;
				break;
			case COMPARE_CONTAINS:
				bool = bool.must(QueryBuilders.matchPhraseQuery(key, text));
				constraintUsed = true;
				break;
			case COMPARE_EQUALS:
				bool = bool.must(QueryBuilders.matchPhraseQuery(key, text));
				constraintUsed = true;
				break;
			case COMPARE_STARTSWITH:
				bool = bool.must(QueryBuilders.matchPhraseQuery(key, text));
				constraintUsed = true;
				break;
			case COMPARE_NOTINCLUDE:
				bool = bool.mustNot(QueryBuilders.matchPhraseQuery(key, text));
				constraintUsed = true;
				break;
			default:
				break;
			}
		}
		if ((constraintUsed == false) && (createdNew == true)) {
			bool = null;
		}
		return bool;
	}

	protected io.searchbox.core.Search.Builder buildSearchAction(final G_EntityQuery pq) {
		final Set<String> esTypes = new LinkedHashSet<String>();
		final Set<String> esIds = new LinkedHashSet<String>();
		if (ValidationUtils.isValid(pq) && ValidationUtils.isValid(pq.getPropertyMatchDescriptors())) {

			String schema = pq.getTargetSchema();
			if (!ValidationUtils.isValid(schema)) {
				schema = c.getIndexName();
			}
			logger.debug("Setting index to " + schema);
			BoolQueryBuilder bool = null;

			for (final G_PropertyMatchDescriptor d : pq.getPropertyMatchDescriptors()) {
				final PropertyMatchDescriptorHelper pmdh = PropertyMatchDescriptorHelper.from(d);
				final String key = pmdh.getKey();
				final Object r = pmdh.getRange();
				pmdh.getVariable();
				if (key.equals(ESTYPE)) {

					if (ValidationUtils.isValid(r)) {
						if (r instanceof G_SingletonRange) {
							esTypes.add((String) ((G_SingletonRange) r).getValue());
						} else if (r instanceof G_ListRange) {
							esTypes.addAll((Collection<? extends String>) r);
						}

						logger.debug("Adding type, types are now " + esTypes.toArray());
					}
				}
				if (key.equals(ESID)) {
					if (ValidationUtils.isValid(r)) {
						if (r instanceof G_SingletonRange) {
							esIds.add((String) ((G_SingletonRange) r).getValue());
						} else if (r instanceof G_ListRange) {
							esIds.addAll((Collection<? extends String>) r);
						}
						logger.debug("Adding id, ids are now " + esIds.toArray());
					}
				} else {
					bool = buildBooleanConstraints(pmdh, bool);
				}
			}

			SearchSourceBuilder ssb = new SearchSourceBuilder();
			String queryString = null;
			if (bool != null) {
				queryString = bool.toString();
			} else if (esIds.size() > 1) {
				logger.debug("Using an id filter query instead");
				queryString = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
						FilterBuilders.termsFilter("_id", esIds)).toString();
			} else {
				queryString = QueryBuilders.matchAllQuery().toString();
			}
			ssb = ssb.query(queryString);
			/*
			 * This is available as long as we don't do scan.
			 */
			// ssb = ssb.sort("_id");
			logger.debug(ssb.toString());
		}

		final io.searchbox.core.Search.Builder action = new Search.Builder(QueryBuilders.matchAllQuery().toString())
				.setParameter("timeout", defaultESTimeout);
		if (ValidationUtils.isValid(pq.getTargetSchema())) {
			action.addIndex(pq.getTargetSchema());
			logger.debug("adding index: " + pq.getTargetSchema());
		}
		if (ValidationUtils.isValid(esTypes)) {
			action.addType(esTypes);
			logger.debug("adding types: " + pq.getTargetSchema());
		}

		/**
		 * Set scrolling options for callback
		 */
		action.setParameter(Parameters.SIZE, PAGESIZE);
		logger.debug("We built query " + action.toString());
		return action;
	}

	public long count() {
		final G_PropertyMatchDescriptor pmdh = G_PropertyMatchDescriptor.newBuilder()
				.setConstraint(G_Constraint.COMPARE_CONTAINS).setKey(ESTYPE)
				.setRange(new SingletonRangeHelper(type, G_PropertyType.STRING)).build();
		final G_EntityQuery q = new QueryHelper(pmdh);
		return count(q);
	}

	public long count(final G_EntityQuery pq) {
		long longCount = 0l;
		try {
			final io.searchbox.core.Count.Builder action = new Count.Builder()
					.setParameter("timeout", defaultESTimeout);
			if (ValidationUtils.isValid(pq) && ValidationUtils.isValid(pq.getPropertyMatchDescriptors())) {

				pq.getPropertyMatchDescriptors().get(0);
				String schema = pq.getTargetSchema();
				if (!ValidationUtils.isValid(schema)) {
					schema = c.getIndexName();
				}
				BoolQueryBuilder bool = null;
				for (final G_PropertyMatchDescriptor d : pq.getPropertyMatchDescriptors()) {
					final PropertyMatchDescriptorHelper pmdh = PropertyMatchDescriptorHelper.from(d);
					final String key = pmdh.getKey();
					final Object r = pmdh.getRange();
					pmdh.getVariable();
					if (key.equals(ESTYPE)) {
						if (ValidationUtils.isValid(r)) {
							if (r instanceof G_SingletonRange) {
								final String type = (String) ((G_SingletonRange) r).getValue();
								action.addType(type);
								logger.debug("added type: " + type);

							} else if (r instanceof G_ListRange) {
								action.addType((Collection<? extends String>) r);
							}
						}
					} else {
						bool = buildBooleanConstraints(pmdh, bool);
					}
				}

				SearchSourceBuilder ssb = new SearchSourceBuilder();
				if (bool != null) {
					ssb = ssb.query(bool);
				} else {
					ssb = ssb.query(QueryBuilders.matchAllQuery());
				}
				logger.debug(ssb.toString());
				action.query(ssb.toString());

				if (ValidationUtils.isValid(pq.getTargetSchema())) {
					action.addIndex(pq.getTargetSchema());
					logger.debug("index: " + pq.getTargetSchema());
				}

			}
			final CountResult result = c.getClient().execute(action.build());
			longCount = result.getCount().longValue();
			return longCount;
		} catch (final Exception e) {
			logger.error("performCount Could not connect to one of the external resources needed for your request: "
					+ e.getMessage());
		}
		logger.debug("Found a count of: " + longCount);
		return longCount;
	}

	protected void createIndex(final String indexName) throws Exception {
		logger.debug("Creating index " + indexName + " with client " + c.getClient().toString());
		// create new index (if u have this in elasticsearch.yml and prefer
		// those defaults, then leave this out
		final ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();
		settings.put("number_of_shards", 3);
		settings.put("number_of_replicas", 0);
		final JestResult execute = c.getClient().execute(
				new CreateIndex.Builder(indexName).settings(settings.build().getAsMap())
						.setParameter("timeout", defaultESTimeout).build());
		logger.debug(execute.toString());
	}

	public boolean delete(final String id) {
		boolean success = false;
		try {
			final JestResult result = c.getClient().execute(
					(new Delete.Builder(id)).index(index).type(type).setParameter("timeout", defaultESTimeout).build());
			success = result.isSucceeded();
			if (success) {
				logger.debug("Successfully deleted id " + id);
			} else {
				logger.error("Could not delete id '" + id + "' : " + result.getJsonString());
			}
		} catch (final Exception e) {
			logger.error("delete " + e.getMessage());
		}

		return success;
	}

	protected void deleteIndex(final String indexName) throws Exception {
		c.deleteIndex(indexName);
	}

	public boolean exists(final String id) {
		final String query = new SearchSourceBuilder().query(
				QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), FilterBuilders.termFilter("_id", id)))
				.toString();
		try {
			final CountResult result = c.getClient().execute(
					new Count.Builder().query(query).addIndex(index).addType(type)
							.setParameter("timeout", defaultESTimeout).build());
			logger.debug("Exists found count " + result.getCount());
			return (result.getCount() > 0);
		} catch (final Exception e) {
			logger.error("Error determining existence " + e.getMessage());
		}
		logger.error("Error determining existence ");
		return false;
	}

	public JestResult getAllResults() {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(MAX_TO_GET_AT_ONCE);
		// .sort("modified")
		final Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(index).addType(type)
				.setParameter("timeout", defaultESTimeout).build();
		logger.debug(searchSourceBuilder.toString());
		JestResult result = new JestResult(null);
		try {
			result = c.getClient().execute(search);
		} catch (final Exception e) {
			logger.error("Get all: " + e.getMessage());
		}
		return result;
	}

	public JestResult getByField(final String field, final String value) {
		JestResult result = new JestResult(null);
		if (ValidationUtils.isValid(field, value)) {
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			// Use the match phrase query so it doesn't tokenize the value.
			searchSourceBuilder.query(QueryBuilders.matchPhraseQuery(field, value));
			final Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(index).addType(type)
					.setParameter("timeout", defaultESTimeout).build();
			logger.debug(searchSourceBuilder.toString());

			try {
				result = c.getClient().execute(search);
			} catch (final Exception e) {
				logger.error("Problem getting by field: " + field + " value: " + value, e.getMessage());
			}
		} else {
			logger.error("A null field or value was provided.");
		}
		return result;
	}

	public JestResult getByJoinFields(final String field1, final String value1, final String field2, final String value2) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(field1, value1))
				.must(QueryBuilders.matchQuery(field2, value2)));
		final Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(index).addType(type)
				.setParameter("timeout", defaultESTimeout).build();
		JestResult result = new JestResult(null);
		try {
			result = c.getClient().execute(search);
		} catch (final Exception e) {
			logger.error("User Roles for user: " + e.getMessage());
		}
		return result;
	}

	public String getIdByFirstHit(final JestResult jr) {
		String id = null;
		try {
			id = ((JsonObject) ((JsonObject) jr.getJsonObject().get("hits")).getAsJsonArray("hits").get(0)).get("_id")
					.getAsString();
		} catch (final Exception e) {
			logger.error("Problem getting id from first hit: ", e);
		}

		return id;
	}

	public String getIndex() {
		return index;
	}

	public long getModifiedTime() {
		return DateTime.now().getMillis();
	}

	/**
	 * When given just the id. Uses default index and no types for this dao.
	 * 
	 * @param id
	 * @return
	 */
	protected JestResult getResultsById(final String id) {
		// DONT use _all for type when searching by id.
		return getResultsById(id, index, null);
	}

	/**
	 * When given the id and a type (that is not _all). Uses default index for
	 * this dao.
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	protected JestResult getResultsById(final String id, final String type) {
		return getResultsById(id, index, type);
	}

	/**
	 * When given an id a type and an index.
	 * 
	 * @param id
	 * @param customIndex
	 * @param customType
	 * @return
	 */
	protected JestResult getResultsById(final String id, final String customIndex, final String customType) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		searchSourceBuilder.query(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
				FilterBuilders.termFilter("_id", id)));
		final Builder sb = new Search.Builder(searchSourceBuilder.toString()).setParameter("timeout", defaultESTimeout);
		logger.debug(searchSourceBuilder.toString());
		if (ValidationUtils.isValid(customIndex)) {
			sb.addIndex(customIndex);
		} else {
			// sb.addIndex("_all");
		}
		if (ValidationUtils.isValid(customType)) {
			sb.addType(customType);
		}

		JestResult result = new JestResult(null);

		try {
			final Search search = sb.build();
			logger.debug("Using schema " + customIndex);
			logger.debug("Using type " + customType);
			result = c.getClient().execute(search);
			logger.debug(result.getJsonString());
		} catch (final Exception e) {
			logger.error("getResultsById: " + e.getMessage());
		}
		return result;
	}

	public JestResult getSpecifiedFields(final String... field) {
		JestResult result = new JestResult(null);
		final SearchSourceBuilder ssb = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery())
				.size(MAX_TO_GET_AT_ONCE).sort("_id");
		if (ValidationUtils.isValid(field)) {
			ssb.fields(field);
		}
		final Search search = new Search.Builder(ssb.toString()).addIndex(index).addType(type)
				.setParameter("timeout", defaultESTimeout).build();
		logger.debug(ssb.toString());

		try {

			result = c.getClient().execute(search);
		} catch (final Exception e) {
			logger.error("Problem getting by ids with field: " + field, e.getMessage());
		}
		return result;
	}

	public String getType() {
		return type;
	}

	public boolean indexExists() {
		boolean success = false;
		if (!ValidationUtils.isValid(index)) {
			logger.warn("Index variable was not initialized! Cannot check for existence.");
		} else {
			try {
				final JestResult result = c.getClient().execute(new IndicesExists.Builder(index).build());
				success = result.isSucceeded();
			} catch (final Exception e) {
				logger.error("indexExists " + e.getMessage());
			}
		}
		return success;

	}

	public void initialize() {
		if (ValidationUtils.isValid(index)) {
			if (indexExists()) {
				logger.debug("Index " + index + " already exists.  This is fine.");
			} else {
				try {
					createIndex(index);
				} catch (final Exception e) {
					logger.error("Problem initializing index: ", e);
				}
			}
		} else {
			logger.error("Could not check for existance of index because index variable was not defined.");
		}
	}

	public boolean performCallback(final long offset, final long maxResults, final G_CallBack cb, final G_EntityQuery pq) {
		JestResult jestResult = new JestResult(null);

		try {

			final boolean scrolling = true;
			if (scrolling) {
				// io.searchbox.core.Search.Builder action =
				// buildSearchAction(pq);
				// action.setParameter(Parameters.SEARCH_TYPE, SearchType.SCAN);
				// action = action.setParameter(Parameters.SIZE, "200");
				// action = action.setParameter(Parameters.SCROLL, "5m");
				final SearchSourceBuilder ssb = new SearchSourceBuilder().query(QueryBuilders.matchPhraseQuery("_all",
						"aksoy"));

				/**
				 * Make a search object first.
				 */
				final Search search = new Search.Builder(ssb.toString()).addIndex(index)
						.setParameter(Parameters.SIZE, PAGESIZE).setParameter(Parameters.SEARCH_TYPE, SearchType.SCAN)
						.setParameter("timeout", defaultESTimeout).setParameter(Parameters.SCROLL, "1m").build();
				logger.debug(ssb.toString());

				// jestResult = c.getClient().execute(action.build());
				jestResult = c.getClient().execute(search);
				// The first query will not have any results, just the scroll id

				if (jestResult.isSucceeded()) {
					logger.debug("execution completed (expected no results, just setting up scroll)");
					logger.debug(jestResult.getJsonString());
					int currentResultSize = 0;
					int pageNumber = 1;
					do {
						final String scrollId = jestResult.getJsonObject().get("_scroll_id").getAsString();
						logger.debug("Next scroll id is " + scrollId);
						final SearchScroll scroll = new SearchScroll.Builder(scrollId, "1m").build();
						final TimeReporter tr = new TimeReporter("Executing scroll " + pageNumber, logger);
						jestResult = c.getClient().execute(scroll);
						tr.logAsCompleted();
						// Get the next scroll id

						final JsonNode rootNode = mapper.readValue(jestResult.getJsonString(), JsonNode.class);
						final List<JsonNode> hits = rootNode.get("hits").findValues("hits");

						final ArrayNode actualListOfHits = (ArrayNode) hits.get(0);
						currentResultSize = actualListOfHits.size();
						for (int i = 0; i < actualListOfHits.size(); i++) {
							final JsonNode currentHit = actualListOfHits.get(i);
							if (ValidationUtils.isValid(currentHit)) {
								final G_SearchResult sr = db.buildSearchResultFromDocument(i, currentHit, pq);
								cb.execute(sr, pq);
							}
						}
						logger.debug("finished scrolling page # " + pageNumber++ + " which had " + currentResultSize
								+ " hits.");

					} while (currentResultSize > 0);
				} else {
					logger.error("Scroll failed with " + jestResult.getErrorMessage());
				}
			} else {

				io.searchbox.core.Search.Builder action = buildSearchAction(pq);
				// action.setParameter(Parameters.SEARCH_TYPE, SearchType.SCAN)
				action = action.setParameter(Parameters.SIZE, "200");

				// The first query will not have any results, just the scroll id

				int currentResultSize = 0;
				int pageNumber = 0;
				do {
					jestResult = c.getClient().execute(action.build());
					logger.debug(jestResult.getJsonString());
					final JsonNode rootNode = mapper.readValue(jestResult.toString(), JsonNode.class);
					final List<JsonNode> hits = rootNode.get("hits").findValues("hits");

					final ArrayNode actualListOfHits = (ArrayNode) hits.get(0);
					currentResultSize = actualListOfHits.size();
					for (int i = 0; i < actualListOfHits.size(); i++) {
						final JsonNode currentHit = actualListOfHits.get(i);
						if (ValidationUtils.isValid(currentHit)) {
							final G_SearchResult sr = db.buildSearchResultFromDocument(i, currentHit, pq);
							cb.execute(sr, pq);
						}
					}
					logger.debug("finished scrolling page # " + pageNumber + " which had " + currentResultSize
							+ " hits.");
					pageNumber++;
					action.setParameter("from", (pageNumber * 200));
				} while (currentResultSize > 0);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public void recreateIndex() {
		try {
			deleteIndex(index);
		} catch (final Exception e) {
			logger.error("recreateIndex " + e.getMessage());
		}
		try {
			createIndex(index);
		} catch (final Exception e) {
			logger.error("recreateIndex " + e.getMessage());
		}
	}

	public String saveObject(final Object g, final String id, final String indexName, final String type,
			final boolean useDelay) {
		Index saveAction;
		if (!ValidationUtils.isValid(id)) {
			saveAction = new Index.Builder(g).index(indexName).type(type).setParameter("timeout", defaultESTimeout)
					.build();
		} else {
			saveAction = new Index.Builder(g).index(indexName).id(id).type(type)
					.setParameter("timeout", defaultESTimeout).build();
		}
		String generatedId = null;
		try {

			final JestResult result = c.getClient().execute(saveAction);
			final Object oid = result.getValue("_id");
			if (ValidationUtils.isValid(oid)) {
				generatedId = oid.toString();
				if (useDelay && (ES_READ_DELAY_MS > 0)) {
					Thread.sleep(ES_READ_DELAY_MS);
				}
			} else {
				logger.error("Error getting saved object's id: " + result.getJsonString());
				generatedId = null;
			}
		} catch (ExecutionException | InterruptedException | IOException e) {
			logger.error("saveObject " + e.getMessage());
		} catch (final Exception e) {
			logger.error("saveObject " + e.getMessage());
		}
		return generatedId;
	}

	public void setIndex(final String index) {
		this.index = index;
	}

	public void setType(final String type) {
		this.type = type;
	}
}
