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
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
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
	@InjectComponent
	private Zone recentWorkspacesZone;

	@Property
	@SessionState(create = false)
	private G_Workspace currentSelectedWorkspace;

	@Property
	private boolean currentSelectedWorkspaceExists;
	@Property
	private G_Workspace currentListItem;

	@Inject
	private G_UserDataAccess userDataAccess;
	@Property
	private final DateTimeFormatter ISODate = ISODateTimeFormat.date();

	@Inject
	private Logger logger;

	@SessionState(create = false)
	private G_User user;

	private boolean userExists;

	@Property
	@SessionState(create = false)
	private List<G_Workspace> workspaces;

	private boolean workspacesExists;

	@Inject
	private Request request;

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	public Zone getZoneComponent() {
		return recentWorkspacesZone;
	}

	@SetupRender
	boolean listWorkspaces() {

		if (userExists) {
			updateListOfWorkspaces();
			if ((!currentSelectedWorkspaceExists || !workspacesExists)) {
				selectMostRecentWorkspace();
			}
		} else {
			currentSelectedWorkspace = null;
			workspaces = null;
		}
		return workspacesExists;
	}

	@OnEvent("makecurrent")
	private void makeCurrent(final String workspaceId) {
		try {
			if (workspaces != null) {
				boolean foundById = false;
				for (final G_Workspace w : workspaces) {
					if (w.getId().equals(workspaceId)) {
						currentSelectedWorkspace = w;
						foundById = true;
					}
				}
				// TODO: instead of hitting the service layer again, just pull
				// from
				// the list of workspaces we got before.
				// currentSelectedWorkspace =
				// userDataAccess.getWorkspace(user.getId(), workspaceId);
				if (foundById) {
					alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS, "Changed to workspace "
							+ currentSelectedWorkspace.getTitle());
				} else {
					// Note, we also let an previously selected currentWorkspace
					// stay the way it was.
					final String warnMessage = "Unable to load workspace id " + workspaceId
							+ ". Try a different workspace or create a new one.";
					logger.warn(warnMessage);
					alertManager.alert(Duration.SINGLE, Severity.WARN, "WARN: " + warnMessage);
				}
			} else {
				final String message = "There was an error loading workspaces for your account.";
				logger.warn(message);
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "ERROR: " + message);
			}
		} catch (final Exception e) {
			final String errorString = "Could not make workspace with id " + workspaceId + " the current selection. "
					+ ExceptionUtil.getRootCauseMessage(e);
			logger.error(errorString);
			alertManager.alert(Duration.SINGLE, Severity.ERROR, "ERROR: " + errorString);
		}
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(recentWorkspacesZone);
		}
	}

	private void selectMostRecentWorkspace() {
		// logger.debug("Selecting most recent workspace");
		if (workspaces.size() > 1) {
			// already sorted, just grab the top one.
			currentSelectedWorkspace = workspaces.get(0);
		} else {
			currentSelectedWorkspace = null;
		}
	}

	private void updateListOfWorkspaces() {
		try {
			// already sorted for us
			workspaces = userDataAccess.getLatestWorkspacesForUser(user.getId(), 10);
		} catch (final AvroRemoteException e) {
			workspaces = null;
			logger.error(e.getMessage());
		}
	}
}
