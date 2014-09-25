package graphene.dao.es;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.query.EntityQuery;

public interface ESRestAPIConnection {

	public abstract String performQuery(String basicAuth, String baseurl,
			String index, String type, String fieldName, String term,
			long from, long size) throws DataAccessException;

	public abstract String performQuery(String basicAuth, String baseurl,
			String index, String type, EntityQuery q)
			throws DataAccessException;

	public abstract long performCount(String basicAuth, String baseUrl,
			String index, String type, String fieldName, String term)
			throws DataAccessException;

}