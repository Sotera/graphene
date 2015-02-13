/**
 * 
 */
package graphene.dao;

import graphene.model.idl.G_GraphViewEvent;
import graphene.model.idl.G_ReportViewEvent;
import graphene.model.idl.G_UserLoginEvent;
import graphene.model.query.BasicQuery;
import graphene.model.query.EntityQuery;

import java.util.List;

import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.interactions.TemporalGraphQuery;

/**
 * An empty implementation of the logging interface.
 * 
 * @author djue
 * 
 */
public class LoggingDAONullImpl implements LoggingDAO {

	@Override
	public List<Object> getAllEvents(final String userId, final String partialTerm, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TemporalGraphQuery> getGraphQueries(final String userId, final String partialTerm, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_GraphViewEvent> getGraphViewEvents(final String userId, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see graphene.dao.LoggingDAO#getQueries(java.lang.String,
	 * java.lang.String, int)
	 */
	@Override
	public List<EntityQuery> getQueries(final String userId, final String partialTerm, final int offset, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_ReportViewEvent> getReportViewEvents(final String userId, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see graphene.dao.LoggingDAO#recordLogin(java.lang.String,
	 * org.joda.time.DateTime)
	 */

	/*
	 * (non-Javadoc)
	 * 
	 * @see graphene.dao.LoggingDAO#recordExport(java.lang.String)
	 */
	@Override
	public boolean recordExport(final String queryString) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void recordGraphViewEvent(final G_GraphViewEvent q) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recordLoginEvent(final G_UserLoginEvent e) {
		// TODO Auto-generated method stub

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * graphene.dao.LoggingDAO#recordQuery(graphene.model.query.EntityQuery)
	 */
	@Override
	public void recordQuery(final BasicQuery sq) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recordQuery(final V_GraphQuery q) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recordReportViewEvent(final G_ReportViewEvent q) {
		// TODO Auto-generated method stub

	}

}
