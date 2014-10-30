package graphene.dao;

import org.joda.time.DateTime;

public interface LoggingDAO {

	public abstract boolean recordQuery(String queryString);

	public abstract boolean recordLogin(String userName, DateTime date);

	public abstract boolean recordExport(String queryString);
}
