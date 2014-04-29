package graphene.web.components;

import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_Workspace;
import graphene.model.idl.UnauthorizedActionException;
import graphene.util.ExceptionUtil;
import graphene.util.time.JodaTimeUtil;

import java.text.Format;
import java.text.SimpleDateFormat;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.joda.time.DateTime;
import org.slf4j.Logger;

@Events({ WorkspaceEditor.CANCEL_CREATE, WorkspaceEditor.SUCCESSFUL_CREATE,
		WorkspaceEditor.FAILED_CREATE, WorkspaceEditor.TO_UPDATE,
		WorkspaceEditor.CANCEL_UPDATE, WorkspaceEditor.SUCCESSFUL_UPDATE,
		WorkspaceEditor.FAILED_UPDATE, WorkspaceEditor.SUCCESSFUL_DELETE,
		WorkspaceEditor.FAILED_DELETE, WorkspaceEditor.TO_CONFIRM_DELETE,
		WorkspaceEditor.CANCEL_CONFIRM_DELETE,
		WorkspaceEditor.SUCCESSFUL_CONFIRM_DELETE,
		WorkspaceEditor.FAILED_CONFIRM_DELETE })
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

	private final String enableDelete = System
			.getProperty("jumpstart.demo-mode");

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
	private G_UserDataAccess userService;

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

	@Parameter(required = true)
	@Property
	private String workspaceId = null;

	// Component "createForm" bubbles up the VALIDATE event when it is submitted

	public Format getDateFormat() {
		return new SimpleDateFormat(getDatePattern());
	}

	// Component "createForm" bubbles up SUCCESS or FAILURE when it is
	// submitted, depending on whether VALIDATE
	// records an error

	public String getDatePattern() {
		return "dd/MM/yyyy";
	}

	public boolean isModeConfirmDelete() {
		return mode == Mode.CONFIRM_DELETE;
	}

	// /////////////////////////////////////////////////////////////////////
	// REVIEW
	// /////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////
	// UPDATE
	// /////////////////////////////////////////////////////////////////////

	// Handle event "toUpdate"

	public boolean isModeCreate() {
		return mode == Mode.CREATE;
	}

	// Handle event "cancelUpdate"

	public boolean isModeNull() {
		return mode == null;
	}

	// Component "updateForm" bubbles up the PREPARE_FOR_RENDER event during
	// form render

	public boolean isModeReview() {
		return mode == Mode.REVIEW;
	}

	// Component "updateForm" bubbles up the PREPARE_FOR_SUBMIT event during
	// form submission

	public boolean isModeUpdate() {
		return mode == Mode.UPDATE;
	}

	boolean onCancelConfirmDelete(String workspaceId) {
		// Return false, which means we haven't handled the event so bubble it
		// up.
		// This method is here solely as documentation, because without this
		// method the event would bubble up anyway.
		return false;
	}

	// Component "updateForm" bubbles up SUCCESS or FAILURE when it is
	// submitted, depending on whether VALIDATE
	// records an error

	boolean onCancelCreate() {
		// Return false, which means we haven't handled the event so bubble it
		// up.
		// This method is here solely as documentation, because without this
		// method the event would bubble up anyway.
		return false;
	}

	boolean onCancelUpdate(String workspaceId) {
		// Return false, which means we haven't handled the event so bubble it
		// up.
		// This method is here solely as documentation, because without this
		// method the event would bubble up anyway.
		return false;
	}

	// /////////////////////////////////////////////////////////////////////
	// DELETE
	// /////////////////////////////////////////////////////////////////////

	// Handle event "delete"

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
	boolean onDelete(String workspaceId, String workspaceVersion) {
		this.workspaceId = workspaceId;

		// If request is AJAX then the user has pressed Delete..., was presented
		// with a Confirm dialog, and OK'd it.

		if (request.isXHR()) {
			boolean successfulDelete = false;

			if (enableDelete != null && enableDelete.equals("true")) {
				deleteMessage = "Sorry, but Delete is not allowed at this time.";
			} else {

				try {
					// TODO: It would be a wise idea to allow revisions of the
					// workspaces, for historys sake.
					successfulDelete = userService.removeUserFromWorkspace(
							user.getUsername(), workspaceId);
					// dao.deleteWorkspaceById(workspaceId);// ,
					// workspaceVersion);

				} catch (Exception e) {
					// Display the cause. In a real system we would try harder
					// to get a user-friendly message.
					logger.error(e.getMessage());
					deleteMessage = "Unable to delete at this time.";
				}

			}

			if (successfulDelete) {
				// Trigger new event "successfulDelete" (which in this example
				// will bubble up to the page).
				componentResources.triggerEvent(SUCCESSFUL_DELETE,
						new Object[] { workspaceId }, null);
			} else {
				// Trigger new event "failedDelete" (which in this example will
				// bubble up to the page).
				componentResources.triggerEvent(FAILED_DELETE,
						new Object[] { workspaceId }, null);
			}
		}

		// Else, (JavaScript disabled) user has pressed Delete..., but not yet
		// confirmed so go to confirmation mode.

		else {
			// Trigger new event "toConfirmDelete" (which in this example will
			// bubble up to the page).
			componentResources.triggerEvent(TO_CONFIRM_DELETE,
					new Object[] { workspaceId }, null);
		}

		// We don't want "delete" to bubble up, so we return true to say we've
		// handled it.
		return true;
	}

	// /////////////////////////////////////////////////////////////////////
	// CONFIRM DELETE - used only when JavaScript is disabled.
	// /////////////////////////////////////////////////////////////////////

	// Handle event "cancelConfirmDelete"

	boolean onFailureFromConfirmDeleteForm() {
		// versionFlash = workspace.getVersion();
		dateFlash = JodaTimeUtil.toDateTime(workspace.getLastmodified());
		// Rather than letting "failure" bubble up which doesn't say what you
		// were trying to do, we trigger new event
		// "failedDelete". It will bubble up because we don't have a handler
		// method for it.
		componentResources.triggerEvent(FAILED_CONFIRM_DELETE,
				new Object[] { workspace.getWorkspaceid() }, null);
		// We don't want "failure" to bubble up, so we return true to say we've
		// handled it.
		return true;
	}

	// Component "confirmDeleteForm" bubbles up the PREPARE_FOR_RENDER event
	// during form render

	boolean onFailureFromCreateForm() {
		// Rather than letting "failure" bubble up which doesn't say what you
		// were trying to do, we trigger new event
		// "failedCreate". It will bubble up because we don't have a handler
		// method for it.
		componentResources.triggerEvent(FAILED_CREATE, null, null);
		// We don't want "failure" to bubble up, so we return true to say we've
		// handled it.
		return true;
	}

	// Component "confirmDeleteForm" bubbles up the PREPARE_FOR_SUBMIT event
	// during form submission

	boolean onFailureFromUpdateForm() {
		if (!request.isXHR()) {
			dateFlash = JodaTimeUtil.toDateTime(workspace.getLastmodified());
			// versionFlash = workspace.getVersion();

		}

		// Rather than letting "failure" bubble up which doesn't say what you
		// were trying to do, we trigger new event
		// "failedUpdate". It will bubble up because we don't have a handler
		// method for it.
		componentResources.triggerEvent(FAILED_UPDATE,
				new Object[] { workspaceId }, null);
		// We don't want "failure" to bubble up, so we return true to say we've
		// handled it.
		return true;
	}

	// Component "confirmDeleteForm" bubbles up the VALIDATE event when it is
	// submitted

	void onPrepareForRenderFromConfirmDeleteForm() {
		try {
			workspace = userService.getWorkspace(user.getUsername(),
					this.workspaceId);
		} catch (AvroRemoteException e) {
			workspace = null;
			e.printStackTrace();
		}
		// Handle null workspace in the template.

		// If the form has errors then we're redisplaying after a redirect.
		// Form will restore your input values but it's up to us to restore
		// Hidden values.

		if (confirmDeleteForm.getHasErrors()) {
			if (workspace != null) {
				workspace.setLastmodified(dateFlash.getMillis());
				// workspace.setVersion(versionFlash);
			}
		}
	}

	// Component "confirmDeleteForm" bubbles up SUCCESS or FAILURE when it is
	// submitted, depending on whether
	// VALIDATE records an error

	void onPrepareForRenderFromUpdateForm(String workspaceId) {
		this.workspaceId = workspaceId;

		if (request.isXHR()) {

			// If the form is valid then we're not redisplaying due to error, so
			// get the workspace.

			if (updateForm.isValid()) {
				try {
					workspace = userService.getWorkspace(user.getUsername(),
							this.workspaceId);
				} catch (AvroRemoteException e) {
					workspace = null;
					e.printStackTrace();
				}
			}

		} else {
			try {
				workspace = userService.getWorkspace(user.getUsername(),
						this.workspaceId);
			} catch (AvroRemoteException e) {
				workspace = null;
				e.printStackTrace();
				updateForm.recordError(ExceptionUtil.getRootCauseMessage(e));
			}
			// Handle null workspace in the template.

			// If the form has errors then we're redisplaying after a redirect.
			// Form will restore your input values but it's up to us to restore
			// Hidden values.

			if (updateForm.getHasErrors()) {
				if (workspace != null) {
					workspace.setLastmodified(dateFlash.getMillis());
					// workspace.setVersion(versionFlash);
				}
			}

		}

	}

	void onPrepareForSubmitFromConfirmDeleteForm() {
		// Get objects for the form fields to overlay.
		try {
			workspace = userService.getWorkspace(user.getUsername(),
					this.workspaceId);
		} catch (AvroRemoteException e) {
			workspace = null;
			e.printStackTrace();
			updateForm.recordError(ExceptionUtil.getRootCauseMessage(e));
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// OTHER
	// /////////////////////////////////////////////////////////////////////

	// Getters

	void onPrepareForSubmitFromUpdateForm(String workspaceId) {
		this.workspaceId = workspaceId;

		// Get objects for the form fields to overlay.
		try {
			workspace = userService.getWorkspace(user.getUsername(),
					this.workspaceId);
		} catch (AvroRemoteException e) {
			workspace = null;
			e.printStackTrace();
			updateForm.recordError(ExceptionUtil.getRootCauseMessage(e));
		}

		// if (workspace == null) {
		// workspace = new G_Workspace();
		// updateForm
		// .recordError("Workspace has been deleted by another process.");
		// }
	}

	void onPrepareFromCreateForm() throws Exception {
		// Instantiate a Workspace for the form data to overlay.
		if (userExists) {
			workspace = userService.createTempWorkspaceForUser(user
					.getUsername());
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
		if (workspace != null) {
			boolean deleted = false;
			try {
				deleted = userService.deleteWorkspaceIfUnused(
						user.getUsername(), workspace.getWorkspaceid());
				componentResources.triggerEvent(SUCCESSFUL_CONFIRM_DELETE,
						new Object[] { workspace.getWorkspaceid() }, null);
			} catch (AvroRemoteException e) {
				logger.error(ExceptionUtil.getRootCauseMessage(e));
				// TODO: Add a popup alert
			}
			if (deleted == false) {
				// TODO: Add a popup alert
				componentResources.triggerEvent(FAILED_DELETE,
						new Object[] { workspace.getWorkspaceid() }, null);
			}
			// We don't want "success" to bubble up, so we return true to say
			// we've
			// handled it.
			return true;
		} else {
			logger.error("Tried to delete a null workspace");
			// We don't want "success" to bubble up, so we return true to say
			// we've
			// handled it.
			return true;
		}
	}

	boolean onSuccessFromCreateForm() {
		// We want to tell our containing page explicitly what workspace we've
		// created, so we trigger new event
		// "successfulCreate" with a parameter. It will bubble up because we
		// don't have a handler method for it.
		try {
			workspace = userService.addNewWorkspaceForUser(user.getUsername(),
					workspace);
			componentResources.triggerEvent(SUCCESSFUL_CREATE,
					new Object[] { workspace.getWorkspaceid() }, null);
		} catch (AvroRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		// We don't want "success" to bubble up, so we return true to say we've
		// handled it.

		return true;
	}

	@Property
	@SessionState(create = false)
	private G_Workspace currentSelectedWorkspace;

	@Property
	private boolean currentSelectedWorkspaceExists;

	boolean onSuccessFromUpdateForm() {
		// We want to tell our containing page explicitly what workspace we've
		// updated, so we trigger new event
		// "successfulUpdate" with a parameter. It will bubble up because we
		// don't have a handler method for it.
		try {
			workspace = userService
					.saveWorkspace(user.getUsername(), workspace);
			logger.info("Successfully updated workspace "
					+ workspace.getWorkspaceid());
			componentResources.triggerEvent(SUCCESSFUL_UPDATE,
					new Object[] { workspaceId }, null);
			if (currentSelectedWorkspaceExists) {
				if (currentSelectedWorkspace.getWorkspaceid().equals(
						workspace.getWorkspaceid())) {
					currentSelectedWorkspace = workspace;
				}
			}
		} catch (AvroRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			logger.error("Could not update workspace"
					+ workspace.getWorkspaceid());
			componentResources.triggerEvent(FAILED_UPDATE,
					new Object[] { workspaceId }, null);
		}
		// We don't want "success" to bubble up, so we return true to say we've
		// handled it.
		return true;
	}

	boolean onToUpdate(String workspaceId) {
		// Return false, which means we haven't handled the event so bubble it
		// up.
		// This method is here solely as documentation, because without this
		// method the event would bubble up anyway.
		return false;
	}

	// public String getWorkspaceRegion() {
	// // Follow the same naming convention that the Select component uses
	// return messages.get(Regions.class.getSimpleName() + "."
	// + workspace.getRegion().name());
	// }

	void onValidateFromConfirmDeleteForm() {

		if (confirmDeleteForm.getHasErrors()) {
			// We get here only if a server-side validator detected an error.
			return;
		}

		if (enableDelete != null && enableDelete.equals("true")) {
			confirmDeleteForm
					.recordError("Sorry, but Delete is not allowed in Demo mode.");
		} else {

			try {
				userService.deleteWorkspace(user.getUsername(), workspaceId);// (workspaceId,
				// workspace.getVersion());
			} catch (Exception e) {
				// Display the cause. In a real system we would try harder to
				// get a user-friendly message.
				confirmDeleteForm.recordError(ExceptionUtil
						.getRootCauseMessage(e));
			}

		}

	}

	void onValidateFromCreateForm() {

		if (createForm.getHasErrors()) {
			// We get here only if a server-side validator detected an error.
			return;
		}

		if (enableDelete != null && enableDelete.equals("true")) {
			createForm
					.recordError("Sorry, but Create is not allowed in Demo mode.");
			return;
		}

		try {
			workspace = userService.addNewWorkspaceForUser(user.getUsername(),
					workspace);
		} catch (Exception e) {
			// Display the cause. In a real system we would try harder to get a
			// user-friendly message.
			createForm.recordError(ExceptionUtil.getRootCauseMessage(e));
		}
	}

	void onValidateFromUpdateForm() {

		if (updateForm.getHasErrors()) {
			// We get here only if a server-side validator detected an error.
			return;
		}

		try {
			userService.saveWorkspace(user.getUsername(), workspace);
		} catch (Exception e) {
			// Display the cause. In a real system we would try harder to get a
			// user-friendly message.
			updateForm.recordError(ExceptionUtil.getRootCauseMessage(e));
		}
	}

	@SetupRender
	@Log
	void switchModes() {
		if (mode == Mode.REVIEW) {
			if (workspaceId == null) {
				workspace = null;
				// Handle null workspace in the template.
			} else {
				try {
					logger.info("Attempting to retrieve workspace "
							+ workspaceId + " for user " + user.getUsername());
					//FIXME: Something is awry here; we sometimes get 'core' as a workspace id, and that fails of course.
					workspace = userService.getWorkspace(user.getUsername(),
							workspaceId);
				} catch (AvroRemoteException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					workspace = null;
				}
				// Handle null workspace in the template.
			}
		}

	}
}