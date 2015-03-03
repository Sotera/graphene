package graphene.dao.es;

import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.query.EntityQuery;
import graphene.util.G_CallBack;
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
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonArray;
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
public class BasicESDAO<T extends JestResult, Q extends EntityQuery> {

	private static final int MAX_TO_GET_AT_ONCE = 1000000;
	private static final int PAGESIZE = 200;
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

	public long count() {
		final String query = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()).toString();
		try {
			final CountResult result = c.getClient().execute(
					new Count.Builder().query(query).addIndex(index).addType(type)
							.setParameter("timeout", defaultESTimeout).build());
			return result.getCount().longValue();
		} catch (final Exception e) {
			logger.error("Error counting users " + e.getMessage());
		}
		return 0;
	}

	public long count(final Q pq) throws Exception {
		if (ValidationUtils.isValid(pq) && ValidationUtils.isValid(pq.getAttributeList())) {
			pq.getAttributeList().get(0);
			String schema = pq.getSchema();
			if (!ValidationUtils.isValid(schema)) {

				schema = c.getIndexName();
				logger.debug("Setting schema to " + schema);
			}
			final String term = pq.getAttributeList().get(0).getValue();
			final long x = c.performCount(null, host, schema, null, null, term);
			return x;
		}
		logger.warn("Did not find any values for " + pq);
		return 0;
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

	protected JestResult getResultsById(final String id) {
		return getResultsById(id, index, type);
	}

	protected JestResult getResultsById(final String id, final String customIndex, final String customType) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		// searchSourceBuilder.query(QueryBuilders.matchQuery("_id", id));
		// This should be faster.
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

	public boolean performCallback(final long offset, final long maxResults,
			final G_CallBack<JestResult, EntityQuery> cb, final EntityQuery q) {
		JestResult result = new JestResult(null);
		final SearchSourceBuilder ssb = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery()).sort("_id");

		final ArrayList<String> excludes = new ArrayList<String>();
		for (final G_SearchTuple<String> a : q.getAttributeList()) {
			if (a.getSearchType().equals(G_SearchType.COMPARE_NOTINCLUDE)) {
				excludes.add(a.getValue());
			}
		}
		if (ValidationUtils.isValid(excludes)) {
			ssb.fetchSource(null, excludes.toArray(new String[excludes.size()]));
		}
		/**
		 * Make a search object first.
		 */
		final Search search = new Search.Builder(ssb.toString()).addIndex(index).addType(type)
				.setParameter(Parameters.SIZE, PAGESIZE).setParameter(Parameters.SEARCH_TYPE, SearchType.SCAN)
				.setParameter("timeout", defaultESTimeout).setParameter(Parameters.SCROLL, "5m").build();
		logger.debug(ssb.toString());

		try {
			result = c.getClient().execute(search);
			// The first query will not have any results, just the scroll id
			assert (result.isSucceeded());
			String scrollId = result.getJsonObject().get("_scroll_id").getAsString();
			logger.debug(" scan completed, scroll id is " + scrollId);
			int currentResultSize = 0;
			int pageNumber = 1;
			do {
				final SearchScroll scroll = new SearchScroll.Builder(scrollId, "5m").build();
				result = c.getClient().execute(scroll);
				scrollId = result.getJsonObject().get("_scroll_id").getAsString();
				final JsonArray hits = result.getJsonObject().getAsJsonObject("hits").getAsJsonArray("hits");
				currentResultSize = hits.size();
				logger.debug("finished scrolling page # " + pageNumber++ + " which had " + currentResultSize + " hits.");
				cb.callBack(result, q);
			} while (currentResultSize > 0);

		} catch (final Exception e) {
			e.printStackTrace();
			logger.error("Problem in callback " + e.getMessage());
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
