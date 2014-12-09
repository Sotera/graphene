/**
 * 
 */
package graphene.dao;

import graphene.business.commons.ReportViewEvent;
import graphene.model.query.BasicQuery;
import graphene.model.query.EntityQuery;

import java.util.List;

import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.interactions.TemporalGraphQuery;

import org.joda.time.DateTime;

/**
 * An empty implementation of the logging interface.
 * 
 * @author djue
 *
 */
public class LoggingDAONullImpl implements LoggingDAO {

	/* (non-Javadoc)
	 * @see graphene.dao.LoggingDAO#recordQuery(java.lang.String)
	 */
	@Override
	public boolean recordQuery(String queryString) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see graphene.dao.LoggingDAO#recordLogin(java.lang.String, org.joda.time.DateTime)
	 */
	@Override
	public boolean recordLogin(String userName, DateTime date) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see graphene.dao.LoggingDAO#recordExport(java.lang.String)
	 */
	@Override
	public boolean recordExport(String queryString) {
		// TODO Auto-generated method stub
		return false;
	}

	/* (non-Javadoc)
	 * @see graphene.dao.LoggingDAO#recordQuery(graphene.model.query.EntityQuery)
	 */
	@Override
	public void recordQuery(BasicQuery sq) {
		// TODO Auto-generated method stub

	}

	/* (non-Javadoc)
	 * @see graphene.dao.LoggingDAO#getQueries(java.lang.String, java.lang.String, int)
	 */
	@Override
	public List<EntityQuery> getQueries(String userId, String partialTerm,
			int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void recordQuery(V_GraphQuery q) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public List<TemporalGraphQuery> getGraphQueries(String userId,
			String partialTerm, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<ReportViewEvent> getReportViewEvents(String userId, int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Object> getAllEvents(String userId, String partialTerm,
			int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void recordReportViewEvent(ReportViewEvent q) {
		// TODO Auto-generated method stub
		
	}

	

}
