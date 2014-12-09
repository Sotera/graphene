package graphene.dao.es;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestResult;
import io.searchbox.core.Delete;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.DeleteIndex;
import io.searchbox.indices.IndicesExists;

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
	protected String auth;
	protected String type = "defaultType";

	public boolean indexExists() {
		boolean success = false;
		if (index == null) {
			logger.warn("Index was not initialized! Cannot check for existence.");
		} else {
			try {
				JestResult result = jestClient
						.execute(new IndicesExists.Builder(index).build());
				success = result.isSucceeded();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return success;

	}

	public String getIndex() {
		return index;
	}

	public long getModifiedTime() {
		return DateTime.now(DateTimeZone.UTC).getMillis();
	}

	public void setIndex(String index) {
		this.index = index;
	}


	public void initialize() {
		if (!indexExists()) {
			try {
				createIndex(jestClient, index);
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public boolean delete(String id) {
		boolean success = false;
		try {
			JestResult result = jestClient
					.execute((new Delete.Builder(id)).index(index)
							.type(type).build());
			success = true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return success;
	}

	public void recreateIndex() {
		try {
			deleteIndex(jestClient, index);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			createIndex(jestClient, index);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void deleteIndex(final JestClient jestClient, String indexName)
			throws Exception {
		DeleteIndex deleteIndex = new DeleteIndex.Builder(indexName).build();
		jestClient.execute(deleteIndex);
	}

	protected void createIndex(final JestClient jestClient, String indexName)
			throws Exception {
		logger.debug("Creating index " + indexName + " with client "
				+ jestClient.toString());
		// create new index (if u have this in elasticsearch.yml and prefer
		// those defaults, then leave this out
		ImmutableSettings.Builder settings = ImmutableSettings
				.settingsBuilder();
		settings.put("number_of_shards", 3);
		settings.put("number_of_replicas", 0);
		JestResult execute = jestClient.execute(new CreateIndex.Builder(indexName).settings(
				settings.build().getAsMap()).build());
		logger.debug(execute.toString());
	}
}
