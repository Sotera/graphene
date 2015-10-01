package graphene.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_EntityQueryEvent;
import graphene.model.idl.G_ExportEvent;
import graphene.model.idl.G_GraphViewEvent;
import graphene.model.idl.G_ReportViewEvent;
import graphene.model.idl.G_UserLoginEvent;

import java.util.List;

import mil.darpa.vande.interactions.TemporalGraphQuery;

/**
 * DAO for recording user initiated events, errors and system status to a
 * persistent store for later analysis and auditing.
 * 
 * @author djue
 * 
 */
public interface LoggingDAO {

	List<Object> getAllEvents(String userId, String partialTerm, int offset, int limit);

	List<TemporalGraphQuery> getGraphQueries(String userId, String partialTerm, int offset, int limit);

	List<G_GraphViewEvent> getGraphViewEvents(String userId, int offset, int limit);

	List<G_EntityQuery> getQueries(String userId, String partialTerm, int offset, int limit);

	List<G_ReportViewEvent> getReportViewEvents(String userId, int offset, int limit);

	List<G_UserLoginEvent> getUserLoginEvents(String userId, int offset, int limit);

	void initialize() throws DataAccessException;;

	/**
	 * Record an export event and what values were used to initiate the export.
	 * 
	 * @param queryString
	 * @return
	 */
	void recordExportEvent(G_ExportEvent q);

	void recordGraphViewEvent(G_GraphViewEvent q);

	/**
	 * For recording query terms (or queries that were executed, with all their
	 * options) for auditing and analysis. Make sure the user information is
	 * included in the query object if you need to log it.
	 * 
	 * @param sq
	 *            The entity query initiated by the user
	 */
	void recordQueryEvent(G_EntityQueryEvent q);

	void recordReportViewEvent(G_ReportViewEvent q);

	/**
	 * Record the event of a user logging in.
	 * 
	 * 
	 * @param e
	 */
	void recordUserLoginEvent(G_UserLoginEvent e);
}
