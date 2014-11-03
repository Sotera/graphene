package graphene.dao.es;

import graphene.dao.LoggingDAO;
import io.searchbox.client.JestClient;
import io.searchbox.core.Index;
import io.searchbox.indices.mapping.PutMapping;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.elasticsearch.index.mapper.DocumentMapper;
import org.elasticsearch.index.mapper.core.StringFieldMapper;
import org.elasticsearch.index.mapper.object.RootObjectMapper;
import org.joda.time.DateTime;

import com.fasterxml.jackson.databind.ObjectMapper;

public class LoggingDAOESImpl implements LoggingDAO {
	private JestClient jestClient;
	private ESRestAPIConnection c;
	private String auth;
	private ObjectMapper mapper;

	@Inject
	public LoggingDAOESImpl(ESRestAPIConnection c, JestClient jestClient) {
		this.auth = null;
		this.c = c;
		this.jestClient = jestClient;
		this.mapper = new ObjectMapper(); // can reuse, share globally
	}

	public void setup() {
		Object source;
		RootObjectMapper.Builder romb = new RootObjectMapper.Builder(
				"g_log_mapping").add(new StringFieldMapper.Builder("message")
				.store(true));

		DocumentMapper dm = new DocumentMapper.Builder("g_index", null, romb)
				.build(null);
		String expectedMappingSource = dm.mappingSource().toString();

		PutMapping putMapping = new PutMapping.Builder("logging_index",
				"logging", expectedMappingSource).build();
		try {
			jestClient.execute(putMapping);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void index(Object o, String indexName, String type, String id) {
		Index index = new Index.Builder(o).index(indexName).type(type).id(id)
				.build();
		try {
			jestClient.execute(index);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public boolean recordQuery(String queryString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean recordLogin(String userName, DateTime date) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean recordExport(String queryString) {
		// TODO Auto-generated method stub
		return false;
	}

}
