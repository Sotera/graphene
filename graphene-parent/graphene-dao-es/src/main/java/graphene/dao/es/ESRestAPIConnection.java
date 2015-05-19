package graphene.dao.es;

import io.searchbox.client.JestClient;

public interface ESRestAPIConnection {

	public abstract void createIndex(String indexName, int shards, int replicas);

	public abstract void createIndex(String indexName, String settings);

	public abstract void deleteIndex(String indexName) throws Exception;

	public abstract JestClient getClient();

	public abstract String getIndexName();

	public abstract void setIndexName(String indexName);

}