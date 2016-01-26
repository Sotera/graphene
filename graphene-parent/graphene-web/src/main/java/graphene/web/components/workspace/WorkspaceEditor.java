/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.web.components.workspace;

import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_ReportViewEvent;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_Workspace;
import graphene.util.ExceptionUtil;
import graphene.util.time.JodaTimeUtil;
import graphene.util.validator.ValidationUtils;
import graphene.web.components.CustomForm;
import graphene.web.pages.workspace.Manage;

import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Request;
import org.joda.time.DateTime;
import org.slf4j.Logger;

@Events({ WorkspaceEditor.CANCEL_CREATE, WorkspaceEditor.SUCCESSFUL_CREATE, WorkspaceEditor.FAILED_CREATE,
		WorkspaceEditor.TO_UPDATE, WorkspaceEditor.CANCEL_UPDATE, WorkspaceEditor.SUCCESSFUL_UPDATE,
		WorkspaceEditor.FAILED_UPDATE, WorkspaceEditor.SUCCESSFUL_DELETE, WorkspaceEditor.FAILED_DELETE,
		WorkspaceEditor.TO_CONFIRM_DELETE, WorkspaceEditor.CANCEL_CONFIRM_DELETE,
		WorkspaceEditor.SUCCESSFUL_CONFIRM_DELETE, WorkspaceEditor.FAILED_CONFIRM_DELETE })
public class WorkspaceEditor {
	public enum Mode {
		CONFIRM_DELETE, CREATE, REVIEW, UPDATE;
	}

	public static final String CANCEL_CONFIRM_DELETE = "cancelConfirmDelete";
	public static final String CANCEL_CREATE = "cancelCreate";
	public static final String CANCEL_UPDATE = "cancelUpdate";
	public static final String FAILED_CONFIRM_DELETE = "failedConfirmDelete";
	public static final String FAILED_CREATE = "failedCreate";
	public static final String FAILED_DELETE = "failedDelete";
	public static final String FAILED_UPDATE = "failedUpdate";
	public static final String SUCCESSFUL_CONFIRM_DELETE = "successfulConfirmDelete";
	public static final String SUCCESSFUL_CREATE = "successfulCreate";
	public static final String SUCCESSFUL_DELETE = "successfulDelete";
	public static final String SUCCESSFUL_UPDATE = "successfulUpdate";
	public static final String TO_CONFIRM_DELETE = "toConfirmDelete";

	public static final String TO_UPDATE = "toUpdate";

	@Inject
	private ComponentResources componentResources;
	
	@InjectContainer
	private Manage managePage;
	   
	// Parameters

	@Component
	private Form confirmDeleteForm;

	@Component
	private CustomForm createForm;

	@Persist(PersistenceConstants.FLASH)
	private DateTime dateFlash;
	// Generally useful bits and pieces
	@Property
	@Persist(PersistenceConstants.FLASH)
	private String deleteMessage;

	@Inject
	@Symbol(G_SymbolConstants.ENABLE_DELETE_WORKSPACES)
	private boolean enableDelete;
    
    @Property
    @SessionState(create = false)
    private List<G_Workspace> workspaces;
    
	// Work fields

	@Inject
	private Logger logger;

	@Inject
	private Messages messages;

	@Parameter(required = true)
	@Property
	private Mode mode;

	@Inject
	private Request request;

	@Component
	private CustomForm updateForm;

	@SessionState(create = false)
	private G_User user;

	@Inject
	private G_UserDataAccess userDataAccess;

	private boolean userExists;

	// This carries version through the redirect that follows a server-side
	// validation failure.
	@Persist(PersistenceConstants.FLASH)
	private Integer versionFlash;

	// The code

	// setupRender() is called by Tapestry right before it starts rendering the
	// component.

	// /////////////////////////////////////////////////////////////////////
	// CREATE
	// /////////////////////////////////////////////////////////////////////

	// Handle event "cancelCreate"

	@Property
	private G_Workspace workspace;

	// Component "createForm" bubbles up the PREPARE event when it is rendered
	// or submitted
	@Property
	private G_ReportViewEvent currentReport;
	@Property
	private G_EntityQuery currentQuery;
	@Property
	private G_PropertyMatchDescriptor currentPmd;
	@Parameter(required = true)
	@Property
	private String workspaceId;

	// Component "createForm" bubbles up the VALIDATE event when it is submitted

	@Property
	@SessionState(create = false)
	private G_Workspace currentSelectedWorkspace;

	// Component "createForm" bubbles up SUCCESS or FAILURE when it is
	// submitted, depending on whether VALIDATE
	// records an error

	@Property
	private boolean currentSelectedWorkspaceExists;

	@Inject
	private AlertManager alertManager;

	// /////////////////////////////////////////////////////////////////////
	// REVIEW
	// /////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////
	// UPDATE
	// /////////////////////////////////////////////////////////////////////

	// Handle event "toUpdate"

	@Property
	private String currentFilter;

	// Handle event "cancelUpdate"

	@Property
	private G_PropertyMatchDescriptor currentTuple;

	// Component "updateForm" bubbles up the PREPARE_FOR_RENDER event during
	// form render

	public Format getDateFormat() {
		return new SimpleDateFormat(getDatePattern());
	}

	// Component "updateForm" bubbles up the PREPARE_FOR_SUBMIT event during
	// form submission

	public String getDatePattern() {
		return "yyyy/MM/dd";
	}

	public boolean isModeConfirmDelete() {
		return mode == Mode.CONFIRM_DELETE;
	}

	// Component "updateForm" bubbles up SUCCESS or FAILURE when it is
	// submitted, depending on whether VALIDATE
	// records an error

	public boolean isModeCreate() {
		return mode == Mode.CREATE;
	}

	public boolean isModeNull() {
		return mode == null;
	}

	// /////////////////////////////////////////////////////////////////////
	// DELETE
	// /////////////////////////////////////////////////////////////////////

	// Handle event "delete"

	public boolean isModeReview() {
		return mode == Mode.REVIEW;
	}

	// /////////////////////////////////////////////////////////////////////
	// CONFIRM DELETE - used only when JavaScript is disabled.
	// /////////////////////////////////////////////////////////////////////

	public boolean isModeUpdate() {
		return mode == Mode.UPDATE;
	}

	// Component "confirmDeleteForm" bubbles up the PREPARE_FOR_RENDER event
	// during form render

	boolean onCancelConfirmDelete(final String workspaceId) {
		// Return false, which means we haven't handled the event so bubble it
		// up.
		// This method is here solely as documentation, because without this
		// method the event would bubble up anyway.
		return false;
	}

	Object onCancelCreate() {
		// Return false, which means we haven't handled the event so bubble it
		// up.
		// This method is here solely as documentation, because without this
		// method the event would bubble up anyway.
	    mode = null;
	    return managePage;
	}

	Object onCancelUpdate(final String workspaceId) {
		// Return false, which means we haven't handled the event so bubble it
		// up.
		// This method is here solely as documentation, because without this
		// method the event would bubble up anyway.
	    mode = Mode.REVIEW;
	    return managePage;
	}

	// Component "confirmDeleteForm" bubbles up SUCCESS or FAILURE when it is
	// submitted, depending on whether
	// VALIDATE records an error

	// Component "updateForm" bubbles up the VALIDATE event when it is submitted
	/**
	 * TODO: What we really want is to remove the user's relationship with this
	 * workspace. If no other users are attached to this workspace, then delete
	 * it completely.
	 * 
	 * @param workspaceId
	 * @param workspaceVersion
	 * @return
	 */
	Object onDelete(final String workspaceId) {
		this.workspaceId = workspaceId;

		// If request is AJAX then the user has pressed Delete..., was presented
		// with a Confirm dialog, and OK'd it.

		if (request.isXHR()) {
			boolean successfulDelete = false;

			if (!enableDelete) {
				deleteMessage = "Sorry, but Delete is not allowed at this time.";
			} else {
				try {
					// TODO: It would be a wise idea to allow revisions of the
					// workspaces, for history's sake.
					successfulDelete = userDataAccess.removeUserFromWorkspace(user.getId(), workspaceId);
 					if (userDataAccess.deleteWorkspaceIfUnused(null, workspaceId)) {
						logger.debug("Deleted workspace because it was unused.");
						for (G_Workspace workspace : workspaces) {
						    if (workspace.getId().equals(workspaceId)) {
						        workspaces.remove(workspace);
						        break;
						    }
						}
					}
            
				} catch (final Exception e) {
					// Display the cause. In a real system we would try harder
					// to get a user-friendly message.
					logger.error(e.getMessage());
					deleteMessage = "Unable to delete at this time.";
				}
			}

			if (successfulDelete) {
			    mode = null;
	            this.workspaceId = null;
			    boolean deletedCurrentlySelected = currentSelectedWorkspace.getId().equals(workspaceId);
			    managePage.updateOnSuccessfulDeleteFromEditor(deletedCurrentlySelected);
			} else {
			    mode = Mode.REVIEW;
			}
		}

		// Else, (JavaScript disabled) user has pressed Delete..., but not yet
		// confirmed so go to confirmation mode.

		else {
			// Trigger new event "toConfirmDelete" (which in this example will
			// bubble up to the page).
			componentResources.triggerEvent(TO_CONFIRM_DELETE, new Object[] { workspaceId }, null);
		}

		// Need to refresh the whole page
		return managePage;
	}

	void onFailureFromConfirmDeleteForm() {
		if (ValidationUtils.isValid(workspace)) {
			// versionFlash = workspace.getVersion();
			dateFlash = JodaTimeUtil.toDateTime(workspace.getModified());

			mode = Mode.CONFIRM_DELETE;
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// OTHER
	// /////////////////////////////////////////////////////////////////////

	// Getters

	void onFailureFromCreateForm() {
	    mode = Mode.CREATE;
	}

	void onFailureFromUpdateForm() {
	    mode = Mode.UPDATE;
	}

	void onPrepareForRenderFromConfirmDeleteForm() {
		try {
			workspace = userDataAccess.getWorkspace(user.getId(), workspaceId);
		} catch (final AvroRemoteException e) {
			workspace = null;
			logger.error(e.getMessage());
		}
		// Handle null workspace in the template.

		// If the form has errors then we're redisplaying after a redirect.
		// Form will restore your input values but it's up to us to restore
		// Hidden values.

		if (confirmDeleteForm.getHasErrors()) {
			if (workspace != null) {
				workspace.setModified(dateFlash.getMillis());
				// workspace.setVersion(versionFlash);
			}
		}
	}

	void onPrepareForRenderFromUpdateForm(final String workspaceId) {
		this.workspaceId = workspaceId;

		// If the form is valid then we're not redisplaying due to error, so
		// get the workspace.

		if (updateForm.isValid()) {
			try {
				workspace = userDataAccess.getWorkspace(user.getId(), this.workspaceId);
			} catch (final AvroRemoteException e) {
				workspace = null;
				logger.error(e.getMessage());
			}
		}
	}

	void onPrepareForSubmitFromConfirmDeleteForm() {
		// Get objects for the form fields to overlay.
		try {
			workspace = userDataAccess.getWorkspace(user.getId(), workspaceId);
		} catch (final AvroRemoteException e) {
			workspace = null;
			logger.error(e.getMessage());
			updateForm.recordError(ExceptionUtil.getRootCauseMessage(e));
		}
	}

	void onPrepareForSubmitFromUpdateForm(final String workspaceId) {
		this.workspaceId = workspaceId;

		// Get objects for the form fields to overlay.
		try {
			workspace = userDataAccess.getWorkspace(user.getId(), this.workspaceId);
		} catch (final AvroRemoteException e) {
			workspace = null;
			logger.error(e.getMessage());
			updateForm.recordError("Workspace has been deleted by another process.  "
					+ ExceptionUtil.getRootCauseMessage(e));
		}
	}

	void onPrepareFromCreateForm() throws Exception {
		// Instantiate a Workspace for the form data to overlay.
		if (userExists) {
			workspace = userDataAccess.createTempWorkspaceForUser(user.getId());
			if (workspace == null) {
				logger.error("Unable to create a new workspace for user " + user.getId());
			}
		} else {
			// disallow a new workspace to be created if no user is logged in.
			workspace = null;
		}
	}

	boolean onSuccessFromConfirmDeleteForm() {
		// We want to tell our containing page explicitly what workspace we've
		// deleted, so we trigger new event
		// "successfulDelete" with a parameter. It will bubble up because we
		// don't have a handler method for it.
		if (userExists && ValidationUtils.isValid(workspace)) {
			boolean deleted = false;
			try {
				deleted = userDataAccess.deleteWorkspace(user.getId(), workspace.getId());
				// Reset the current selected workspace to NOTHING
				if (currentSelectedWorkspaceExists && currentSelectedWorkspace.getId().equals(workspace.getId())) {
					currentSelectedWorkspace = null;
				}
				mode = null;
			} catch (final AvroRemoteException e) {
				logger.error(ExceptionUtil.getRootCauseMessage(e));
				// TODO: Add a popup alert
			}
			if (deleted == false) {
				// TODO: Add a popup alert
				componentResources.triggerEvent(FAILED_DELETE, new Object[] { workspace.getId() }, null);
			}
		} 
		logger.error("Tried to delete a null workspace, or user does not exist");
		
		// We don't want "success" to bubble up, so we return true to say
		// we've
		// handled it.
		return true;
	}

	// public String getWorkspaceRegion() {
	// // Follow the same naming convention that the Select component uses
	// return messages.get(Regions.class.getSimpleName() + "."
	// + workspace.getRegion().name());
	// }

	Object onSuccessFromCreateForm() {
		// We want to tell our containing page explicitly what workspace we've
		// created, so we trigger new event
		// "successfulCreate" with a parameter. It will bubble up because we
		// don't have a handler method for it.
		if (ValidationUtils.isValid(workspace)) {
		    mode = Mode.REVIEW;
		    workspaceId = workspace.getId();
		    
		    managePage.updateOnSuccessfulCreateFromEditor(workspaceId);
		}
		// Need to refresh the whole page
		return managePage;
	}

	Object onSuccessFromUpdateForm() {
	    mode = Mode.REVIEW;
		
		// Need to refresh the whole page
	    return managePage;
	}

	boolean onToUpdate(final String workspaceId) {
		// Return false, which means we haven't handled the event so bubble it
		// up.
		// This method is here solely as documentation, because without this
		// method the event would bubble up anyway.
		return false;
	}

	@Log
	void onValidateFromConfirmDeleteForm() {

		if (confirmDeleteForm.getHasErrors()) {
			// We get here only if a server-side validator detected an error.
			return;
		}

		if (!enableDelete) {
			confirmDeleteForm.recordError("Sorry, but Delete is not allowed at this time.");
		} else {

			try {
				final boolean success = userDataAccess.deleteWorkspace(user.getId(), workspaceId);
				if (success) {
					alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS, "Successfully deleted workspace");
				} else {
					alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Failed to delete workspace");
				}
			} catch (final Exception e) {
				// Display the cause. In a real system we would try harder to
				// get a user-friendly message.
				confirmDeleteForm.recordError(ExceptionUtil.getRootCauseMessage(e));
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Failed to delete workspace");
			}
		}
	}

	void onValidateFromCreateForm() {

		if (createForm.getHasErrors()) {
			// We get here only if a server-side validator detected an error.
			return;
		}

		if (!enableDelete) {
			createForm.recordError("Sorry, but Create is not allowed in Demo mode.");
			return;
		}

		try {
			workspace = userDataAccess.addNewWorkspaceForUser(user.getId(), workspace);
			if (ValidationUtils.isValid(workspace)) {
				logger.info("Successfully created workspace " + workspace.getId());
				alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS, "Successfully created workspace");
			} else {
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Failed to create workspace");
			}

		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}
	}

	void onValidateFromUpdateForm() {

		if (updateForm.getHasErrors()) {
			// We get here only if a server-side validator detected an error.
			return;
		}

		try {
			workspace = userDataAccess.saveWorkspace(user.getId(), workspace);
			if (ValidationUtils.isValid(workspace)) {
				if (currentSelectedWorkspaceExists && workspace.getId().equals(currentSelectedWorkspace.getId())) {
					currentSelectedWorkspace = workspace;
				}
				logger.info("Successfully updated workspace " + workspace.getId());
				alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS,
						"Successfully updated workspace " + workspace.getTitle());
			} else {
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Failed to update workspace");
			}
		} catch (final Exception e) {
			logger.error("Could not update workspace" + workspace.getId() + " " + e.getMessage());
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Failed to update workspace");

			// Display the cause. In a real system we would try harder to get a
			// user-friendly message.
			updateForm.recordError(ExceptionUtil.getRootCauseMessage(e));
		}
	}

	@SetupRender
	void switchModes() {
		if (mode == Mode.REVIEW) {
			if (!ValidationUtils.isValid(workspaceId)) {
				workspace = null;
				// Handle null workspace in the template.
			} else if (ValidationUtils.isValid(user)) {

				try {
					logger.info("Attempting to retrieve workspace " + workspaceId + " for user " + user.getId());
					// FIXME: Something is awry here; we sometimes get 'core' as
					// a workspace id, and that fails of course.
					workspace = userDataAccess.getWorkspace(user.getId(), workspaceId);

				} catch (final Exception e) {
					logger.error(e.getMessage());
					alertManager
							.alert(Duration.SINGLE, Severity.ERROR, "Problem creating workspace: " + e.getMessage());
					workspace = null;
				}
				// Handle null workspace in the template.
			} else {
				/*
				 * This can happen if all of these are true:
				 * 
				 * 1) the server is restarted
				 * 
				 * 2) the user goes to a review url
				 * 
				 * 3) the No Security Module is enabled, so it doesn't force
				 * them to log in.
				 */
				logger.error("User was not logged in, but needs to be logged in to view this page.");
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR,
						"User was not logged in, but needs to be logged in to view this page.");
			}
		}
	}
}