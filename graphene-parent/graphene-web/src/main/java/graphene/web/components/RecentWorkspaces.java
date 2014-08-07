package graphene.web.components;

import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_Workspace;
import graphene.util.ExceptionUtil;

import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;

/**
 * List the last N most recently edited workspaces
 * 
 * @author djue
 * 
 */
public class RecentWorkspaces {
	@Inject
	private G_UserDataAccess dao;

	@Property
	private DateTimeFormatter ISODate = ISODateTimeFormat.date();
	@SessionState(create = false)
	private G_User user;

	private boolean userExists;
	@Property
	private List<G_Workspace> workspaces;

	@Property
	private G_Workspace currentWorkspace;

	@Property
	@SessionState(create = false)
	private G_Workspace currentSelectedWorkspace;

	@Property
	private boolean currentSelectedWorkspaceExists;

	@SetupRender
	boolean listWorkspaces() {

		if (userExists) {
			try {
				workspaces = dao.getWorkspacesForUser(user.getUsername());
			} catch (AvroRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return workspaces != null ? true : false;
	}

	@Inject
	private Logger logger;
	@Inject
	private AlertManager alertManager;

	@OnEvent("makecurrent")
	private void makeCurrent(String workspaceId) {
		try {
			// TODO: instead of hitting the service layer again, just pull from
			// the list of workspaces we got before.
			currentSelectedWorkspace = dao.getWorkspace(user.getUsername(),
					workspaceId);
			if (currentSelectedWorkspace == null) {
				String warnMessage = "Unable to load workspace id "
						+ workspaceId
						+ ". Try a different workspace or create a new one.";
				logger.warn(warnMessage);
				alertManager.alert(Duration.SINGLE, Severity.WARN, "WARN: "
						+ warnMessage);
			} else {
				alertManager.alert(
						Duration.TRANSIENT,
						Severity.SUCCESS,
						"Changed to workspace "
								+ currentSelectedWorkspace.getTitle());
			}
		} catch (Exception e) {
			String errorString = "Could not make workspace with id "
					+ workspaceId + " the current selection. "
					+ ExceptionUtil.getRootCauseMessage(e);
			logger.error(errorString);
			alertManager.alert(Duration.SINGLE, Severity.ERROR, "ERROR: "
					+ errorString);
		}
		// updateTitle();
	}
}
