package graphene.dao.es;

import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestResult;
import io.searchbox.core.Count;
import io.searchbox.core.CountResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.IndicesExists;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BasicESDAO {

	protected ObjectMapper mapper;
	private String index;
	protected Logger logger;
	protected ESRestAPIConnection c;
	protected String auth = null;
	protected String type = "defaultType";
	@Inject
	@Symbol(JestModule.ES_READ_DELAY_MS)
	protected long ES_READ_DELAY_MS;
	@Inject
	@Symbol(JestModule.ES_DEFAULT_TIMEOUT)
	protected String defaultESTimeout;

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

	protected JestResult getAllResults() {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(200000);
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

	public String getIndex() {
		return index;
	}

	public long getModifiedTime() {
		return DateTime.now(DateTimeZone.UTC).getMillis();
	}

	protected JestResult getResultsById(final String id) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchQuery("_id", id));

		final Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(index).addType(type)
				.setParameter("timeout", defaultESTimeout).build();
		JestResult result = new JestResult(null);
		try {
			result = c.getClient().execute(search);
		} catch (final Exception e) {
			logger.error("getResultsById: " + e.getMessage());
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

	public String saveObject(final Object g, final String id, final String indexName, final String type) {
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
				if (ES_READ_DELAY_MS > 0) {
					Thread.sleep(ES_READ_DELAY_MS);
				}
			} else {
				logger.error("Error getting saved object: " + result.getJsonString());
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
