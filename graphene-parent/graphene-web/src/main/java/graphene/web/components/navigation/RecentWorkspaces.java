package graphene.web.components.navigation;

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
	private AlertManager alertManager;

	@Property
	@SessionState(create = false)
	private G_Workspace currentSelectedWorkspace;
	@Property
	private boolean currentSelectedWorkspaceExists;

	@Property
	private G_Workspace currentWorkspace;
	@Inject
	private G_UserDataAccess userDataAccess;

	@Property
	private DateTimeFormatter ISODate = ISODateTimeFormat.date();

	@Inject
	private Logger logger;

	@SessionState(create = false)
	private G_User user;

	private boolean userExists;

	@Property
	@SessionState(create = false)
	private List<G_Workspace> workspaces;

	private boolean workspacesExists;

	@SetupRender
	boolean listWorkspaces() {
		if (userExists && !workspacesExists) {
			try {
				workspaces = userDataAccess.getWorkspacesForUser(user.getId());

			} catch (AvroRemoteException e) {
				workspaces = null;
				e.printStackTrace();
			}
		}
		if (workspacesExists && !currentSelectedWorkspaceExists) {
			selectMostRecentWorkspace();
		}
		return workspaces != null ? true : false;
	}

	private void selectMostRecentWorkspace() {
		Long modified = 0l, mostRecent = 0l;
		for (G_Workspace w : workspaces) {
			modified = w.getModified();
			if (modified > mostRecent) {
				currentSelectedWorkspace = w;
			}
		}
	}

	@OnEvent("makecurrent")
	private void makeCurrent(String workspaceId) {
		try {
			if (workspaces != null) {
				boolean foundById = false;
				for (G_Workspace w : workspaces) {
					if (w.getId().equals(workspaceId)) {
						currentSelectedWorkspace = w;
						foundById = true;
					}
				}
				// TODO: instead of hitting the service layer again, just pull
				// from
				// the list of workspaces we got before.
				currentSelectedWorkspace = userDataAccess.getWorkspace(
						user.getId(), workspaceId);
				if (foundById) {
					alertManager.alert(
							Duration.TRANSIENT,
							Severity.SUCCESS,
							"Changed to workspace "
									+ currentSelectedWorkspace.getTitle());
				} else {
					// Note, we also let an previously selected currentWorkspace
					// stay the way it was.
					String warnMessage = "Unable to load workspace id "
							+ workspaceId
							+ ". Try a different workspace or create a new one.";
					logger.warn(warnMessage);
					alertManager.alert(Duration.SINGLE, Severity.WARN, "WARN: "
							+ warnMessage);
				}
			} else {
				String message = "There was an error loading workspaces for your account.";
				logger.warn(message);
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR,
						"ERROR: " + message);
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
