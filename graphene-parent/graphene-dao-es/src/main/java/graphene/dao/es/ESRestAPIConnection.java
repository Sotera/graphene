package graphene.dao.es;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.G_EntityQuery;
import io.searchbox.client.JestClient;
import io.searchbox.core.Search;

public interface ESRestAPIConnection {

	public abstract void createIndex(String indexName, int shards, int replicas);

	public abstract void createIndex(String indexName, String settings);

	public abstract void deleteIndex(String indexName) throws Exception;

	public abstract JestClient getClient();

	public abstract String getIndexName();

	public abstract long performCount(String basicAuth, String baseUrl, String index, String type, String fieldName,
			String term) throws DataAccessException;

	public abstract String performQuery(String basicAuth, String baseurl, G_EntityQuery q) throws DataAccessException;

	public abstract String performQuery(final String basicAuth, final String baseurl, final String index,
			final String type, final Search action) throws DataAccessException;

	public abstract String performQuery(String basicAuth, String baseurl, String index, String type, String fieldName,
			String term, long from, long size) throws DataAccessException;

	public abstract void setIndexName(String indexName);

}