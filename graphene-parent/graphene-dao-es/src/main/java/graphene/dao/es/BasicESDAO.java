package graphene.dao.es;

import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class BasicESDAO {

	protected JestClient jestClient;
	protected ObjectMapper mapper;
	private String index;
	protected Logger logger;
	protected ESRestAPIConnection c;
	protected String auth = null;
	protected String type = "defaultType";

	@Inject
	@Symbol(JestModule.ES_READ_DELAY_MS)
	protected long ES_READ_DELAY_MS;

	protected void createIndex(final JestClient jestClient, final String indexName) throws Exception {
		logger.debug("Creating index " + indexName + " with client " + jestClient.toString());
		// create new index (if u have this in elasticsearch.yml and prefer
		// those defaults, then leave this out
		final ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();
		settings.put("number_of_shards", 3);
		settings.put("number_of_replicas", 0);
		final JestResult execute = jestClient.execute(new CreateIndex.Builder(indexName).settings(
				settings.build().getAsMap()).build());
		logger.debug(execute.toString());
	}

	public boolean delete(final String id) {
		boolean success = false;
		try {
			jestClient.execute((new Delete.Builder(id)).index(index).type(type).build());
			success = true;
		} catch (final Exception e) {
			logger.error(e.getMessage());
		}
		return success;
	}

	protected void deleteIndex(final JestClient jestClient, final String indexName) throws Exception {
		final DeleteIndex deleteIndex = new DeleteIndex.Builder(indexName).build();
		jestClient.execute(deleteIndex);
	}

	public String getIndex() {
		return index;
	}

	public long getModifiedTime() {
		return DateTime.now(DateTimeZone.UTC).getMillis();
	}

	public String getType() {
		return type;
	}

	public boolean indexExists() {
		boolean success = false;
		if (index == null) {
			logger.warn("Index was not initialized! Cannot check for existence.");
		} else {
			try {
				final JestResult result = jestClient.execute(new IndicesExists.Builder(index).build());
				success = result.isSucceeded();
			} catch (final Exception e) {
				logger.error(e.getMessage());
			}
		}
		return success;

	}

	public void initialize() {
		if (!indexExists() && ValidationUtils.isValid(index)) {
			try {
				createIndex(jestClient, index);
			} catch (final Exception e) {
				logger.error(e.getMessage());
			}
		}
	}

	public void recreateIndex() {
		try {
			deleteIndex(jestClient, index);
		} catch (final Exception e) {
			logger.error(e.getMessage());
		}
		try {
			createIndex(jestClient, index);
		} catch (final Exception e) {
			logger.error(e.getMessage());
		}
	}

	public void setIndex(final String index) {
		this.index = index;
	}

	public void setType(final String type) {
		this.type = type;
	}
}
