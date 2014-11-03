package graphene.dao;

import org.joda.time.DateTime;

/**
 * DAO for recording user initiated events, errors and system status to a
 * persistent store for later analysis and auditing.
 * 
 * @author djue
 * 
 */
public interface LoggingDAO {

	/**
	 * For recording query terms (or queries that were executed, with all their
	 * options) for auditing and analysis.
	 * 
	 * @param queryString
	 * @return
	 */
	public abstract boolean recordQuery(String queryString);

	/**
	 * Record the event of a user logging in.
	 * @param userName
	 * @param date
	 * @return
	 */
	public abstract boolean recordLogin(String userName, DateTime date);

	/**
	 * Record an export event and what values were used to initiate the export.
	 * 
	 * @param queryString
	 * @return
	 */
	public abstract boolean recordExport(String queryString);
}
