/**
 * 
 */
package graphene.services;

import graphene.dao.LoggingDAO;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_ExportEvent;
import graphene.model.idl.G_GraphViewEvent;
import graphene.model.idl.G_ReportViewEvent;
import graphene.model.idl.G_UserLoginEvent;

import java.util.List;

import mil.darpa.vande.interactions.TemporalGraphQuery;

/**
 * An No Operation (NoOp) implementation of the logging interface. It doesn't do
 * anything, but gives you a class to bind to.
 * 
 * @author djue
 * 
 */
public class LoggingDAONoOpImpl implements LoggingDAO {

	@Override
	public List<Object> getAllEvents(final String userId, final String partialTerm, final int offset, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<TemporalGraphQuery> getGraphQueries(final String userId, final String partialTerm, final int offset,
			final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_GraphViewEvent> getGraphViewEvents(final String userId, final int offset, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_EntityQuery> getQueries(final String userId, final String partialTerm, final int offset,
			final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_ReportViewEvent> getReportViewEvents(final String userId, final int offset, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserLoginEvent> getUserLoginEvents(final String userId, final int offset, final int limit) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() {
		// TODO Auto-generated method stub

	}

	@Override
	public void recordExportEvent(final G_ExportEvent q) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recordGraphViewEvent(final G_GraphViewEvent q) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recordQuery(final G_EntityQuery sq) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recordReportViewEvent(final G_ReportViewEvent q) {
		// TODO Auto-generated method stub

	}

	@Override
	public void recordUserLoginEvent(final G_UserLoginEvent e) {
		// TODO Auto-generated method stub

	}
}
