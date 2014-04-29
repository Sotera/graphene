package graphene.web.pages;

import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_VisualType;
import graphene.model.idl.G_Workspace;
import graphene.util.ExceptionUtil;
import graphene.web.annotations.PluginPage;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

@PluginPage(visualType = G_VisualType.META)
public class ViewWorkspace {
	@Property
	private G_Workspace currentWorkspace = null;

	@SessionState(create = false)
	private G_User user;

	private boolean userExists;
	@Inject
	private G_UserDataAccess service;
	@Persist
	private String workspaceId;

	@Inject
	private AlertManager alertManager;
	@Inject
	private Logger logger;

	@Property
	@SessionState(create = false)
	private G_Workspace currentSelectedWorkspace;

	@Property
	private boolean currentSelectedWorkspaceExists;

	void onActivate(final String workspaceId) {
		this.workspaceId = workspaceId;
		if (userExists) {
			if (workspaceId != null) {
				try {
					logger.info("Attempting to retrieve workspace "
							+ workspaceId + " for user " + user.getUsername());
					this.currentWorkspace = service.getWorkspace(
							user.getUsername(), workspaceId);
				} catch (AvroRemoteException e) {
					logger.error(ExceptionUtil.getRootCauseMessage(e));
					alertManager.alert(Duration.SINGLE, Severity.ERROR,
							"You are not authorized to view the workspace "
									+ workspaceId + ".");
				}
			}
		} else {
			alertManager.alert(Duration.UNTIL_DISMISSED, Severity.ERROR,
					"You must be logged in to view a workspace");
			logger.error("User was not logged in and tried to view "
					+ workspaceId);
		}
	}

	// onPassivate() is called by Tapestry to get the activation context to put
	// in the id.
	String onPassivate() {
		if (currentWorkspace != null) {
			return currentWorkspace.getWorkspaceid();
		} else {
			return null;
		}
	}
}
