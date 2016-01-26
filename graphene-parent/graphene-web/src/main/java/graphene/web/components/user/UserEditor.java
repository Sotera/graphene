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

package graphene.web.components.user;

import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.util.validator.ValidationUtils;
import graphene.web.components.CustomForm;

import java.text.Format;
import java.text.SimpleDateFormat;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;
import org.tynamo.security.services.SecurityService;

/**
 * This component will trigger the following events on its container (which in
 * this example is the page):
 * {@link UserEditor.graphene.web.components.user.UserEditor#CANCEL_CREATE} ,
 * {@link UserEditor.graphene.web.components.user.UserEditor#SUCCESSFUL_CREATE}
 * (String username),
 * {@link UserEditor.graphene.web.components.user.UserEditor#FAILED_CREATE} ,
 * {@link UserEditor.graphene.web.components.user.UserEditor#TO_UPDATE}
 * (StrusernameonId),
 * {@link UserEditor.graphene.web.components.user.UserEditor#CANCEL_UPDATE}
 * (String username),
 * {@link UserEditor.graphene.web.components.user.UserEditor#SUCCESSFUL_UPDATE}
 * (String username),
 * {@link UserEditor.graphene.web.components.user.UserEditor#FAILED_UPDATE}
 * (String username), username),
 * {@link UserEditor.graphene.web.components.user.UserEditor#SUCCESSFUL_DELETE}
 * (String username),
 * {@link UserEditor.graphene.web.components.user.UserEditor#FAILED_DELETE}
 * (String username),
 * {@link UserEditor.graphene.web.components.user.UserEditor#TO_CONFIRM_DELETE}
 * (String username),
 * {@link UserEditor.graphene.web.components.user.UserEditor#CANCEL_CONFIRM_DELETE}
 * (String username),
 * {@link UserEditor.graphene.web.components.user.UserEditor#SUCCESSFUL_CONFIRM_DELETE}
 * (String username),
 * {@link UserEditor.graphene.web.components.user.UserEditor#FAILED_CONFIRM_DELETE}
 * (String username)
 */
// @Events is applied to a component solely to document what events it may
// trigger. It is not checked at runtime.
@Events({ UserEditor.CANCEL_CREATE, UserEditor.SUCCESSFUL_CREATE, UserEditor.FAILED_CREATE, UserEditor.TO_UPDATE,
		UserEditor.CANCEL_UPDATE, UserEditor.SUCCESSFUL_UPDATE, UserEditor.FAILED_UPDATE, UserEditor.SUCCESSFUL_DELETE,
		UserEditor.FAILED_DELETE, UserEditor.TO_CONFIRM_DELETE, UserEditor.CANCEL_CONFIRM_DELETE,
		UserEditor.SUCCESSFUL_CONFIRM_DELETE, UserEditor.FAILED_CONFIRM_DELETE })
public class UserEditor {
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

	@Property
	private G_User aUser;

	@Inject
	private ComponentResources componentResources;

	@Component
	private Form confirmDeleteForm;

	@Component
	private CustomForm createForm;

	// Screen fields

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String deleteMessage;

	private final String editModeStr = System.getProperty("jumpstart.demo-mode");

	// Work fields

	@Inject
	private Logger logger;

	// Generally useful bits and pieces

	@Inject
	private Messages messages;

	@Parameter(required = true)
	@Property
	private Mode mode;

	@Inject
	private Request request;

	@Inject
	@Property
	private SecurityService securityService;
	// Parameters

	@Component
	private CustomForm updateForm;

	@SessionState(create = false)
	private G_User user;

	// setupRender() is called by Tapestry right before it starts rendering the
	// component.
	@Inject
	private G_UserDataAccess userDataAccess;
	@SuppressWarnings("unused")
	private boolean userExists;
	// The code
	@Parameter(required = true)
	@Property
	private String userId;

	// This carries version through the redirect that follows a server-side
	// validation failure.
	@Persist(PersistenceConstants.FLASH)
	private G_User versionFlash;

	public Format getDateFormat() {
		return new SimpleDateFormat(getDatePattern());
	}

	// /////////////////////////////////////////////////////////////////////
	// CREATE
	// /////////////////////////////////////////////////////////////////////

	// Handle event "cancelCreate"

	public String getDatePattern() {
		return "dd/MM/yyyy";
	}

	// Component "createForm" bubbles up the PREPARE event when it is rendered
	// or submitted

	public boolean isModeConfirmDelete() {
		return mode == Mode.CONFIRM_DELETE;
	}

	// Component "createForm" bubbles up the VALIDATE event when it is submitted

	public boolean isModeCreate() {
		return mode == Mode.CREATE;
	}

	// Component "createForm" bubbles up SUCCESS or FAILURE when it is
	// submitted, depending on whether VALIDATE
	// records an error

	public boolean isModeNull() {
		return mode == null;
	}

	public boolean isModeReview() {
		return mode == Mode.REVIEW;
	}

	// /////////////////////////////////////////////////////////////////////
	// REVIEW
	// /////////////////////////////////////////////////////////////////////

	// /////////////////////////////////////////////////////////////////////
	// UPDATE
	// /////////////////////////////////////////////////////////////////////

	// Handle event "toUpdate"

	public boolean isModeUpdate() {
		return mode == Mode.UPDATE;
	}

	// Handle event "cancelUpdate"

	boolean onCancelConfirmDelete(final int userId) {
		// Return false, which means we haven't handled the event so bubble it
		// up.
		// This method is here solely as documentation, because without this
		// method the event would bubble up anyway.
		return false;
	}

	// Component "updateForm" bubbles up the PREPARE_FOR_RENDER event during
	// form render

	boolean onCancelCreate() {
		// Return false, which means we haven't handled the event so bubble it
		// up.
		// This method is here solely as documentation, because without this
		// method the event would bubble up anyway.
		return false;
	}

	// Component "updateForm" bubbles up the PREPARE_FOR_SUBMIT event during
	// form submission

	boolean onCancelUpdate(final int userId) {
		// Return false, which means we haven't handled the event so bubble it
		// up.
		// This method is here solely as documentation, because without this
		// method the event would bubble up anyway.
		return false;
	}

	// Component "updateForm" bubbles up the VALIDATE event when it is submitted

	boolean onDelete(final String userId, final Integer personVersion) {
		this.userId = userId;

		// If request is AJAX then the user has pressed Delete..., was presented
		// with a Confirm dialog, and OK'd it.

		if (request.isXHR()) {
			boolean successfulDelete = false;

			if ((editModeStr != null) && editModeStr.equals("true")) {
				deleteMessage = "Sorry, but Delete is currently disabled.";
			} else {

				try {
					successfulDelete = userDataAccess.deleteUser(userId);

				} catch (final Exception e) {
					// Display the cause. In a real system we would try harder
					// to get a user-friendly message.
					deleteMessage = e.getMessage();
				}

			}

			if (successfulDelete) {
				// Trigger new event "successfulDelete" (which in this example
				// will bubble up to the page).
				componentResources.triggerEvent(SUCCESSFUL_DELETE, new Object[] { userId }, null);
			} else {
				// Trigger new event "failedDelete" (which in this example will
				// bubble up to the page).
				componentResources.triggerEvent(FAILED_DELETE, new Object[] { userId }, null);
			}
		}

		// Else, (JavaScript disabled) user has pressed Delete..., but not yet
		// confirmed so go to confirmation mode.

		else {
			// Trigger new event "toConfirmDelete" (which in this example will
			// bubble up to the page).
			componentResources.triggerEvent(TO_CONFIRM_DELETE, new Object[] { userId }, null);
		}

		// We don't want "delete" to bubble up, so we return true to say we've
		// handled it.
		return true;
	}

	// Component "updateForm" bubbles up SUCCESS or FAILURE when it is
	// submitted, depending on whether VALIDATE
	// records an error

	boolean onFailureFromConfirmDeleteForm() {
		versionFlash = aUser;

		// Rather than letting "failure" bubble up which doesn't say what you
		// were trying to do, we trigger new event
		// "failedDelete". It will bubble up because we don't have a handler
		// method for it.
		componentResources.triggerEvent(FAILED_CONFIRM_DELETE, new Object[] { aUser.getUsername() }, null);
		// We don't want "failure" to bubble up, so we return true to say we've
		// handled it.
		return true;
	}

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

	// /////////////////////////////////////////////////////////////////////
	// DELETE
	// /////////////////////////////////////////////////////////////////////

	// Handle event "delete"

	boolean onFailureFromUpdateForm() {
		if (!request.isXHR()) {
			// versionFlash = person.getVersion();
		}

		// Rather than letting "failure" bubble up which doesn't say what you
		// were trying to do, we trigger new event
		// "failedUpdate". It will bubble up because we don't have a handler
		// method for it.
		componentResources.triggerEvent(FAILED_UPDATE, new Object[] { userId }, null);
		// We don't want "failure" to bubble up, so we return true to say we've
		// handled it.
		return true;
	}

	// /////////////////////////////////////////////////////////////////////
	// CONFIRM DELETE - used only when JavaScript is disabled.
	// /////////////////////////////////////////////////////////////////////

	// Handle event "cancelConfirmDelete"

	void onPrepareForRenderFromConfirmDeleteForm() {
		try {
			aUser = userDataAccess.getUser(userId);
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		// Handle null person in the template.

		// If the form has errors then we're redisplaying after a redirect.
		// Form will restore your input values but it's up to us to restore
		// Hidden values.

		if (confirmDeleteForm.getHasErrors()) {
			if (aUser != null) {
				aUser = versionFlash;
			}
		}
	}

	// Component "confirmDeleteForm" bubbles up the PREPARE_FOR_RENDER event
	// during form render

	void onPrepareForRenderFromUpdateForm(final String userId) {
		this.userId = userId;

		if (request.isXHR()) {

			// If the form is valid then we're not redisplaying due to error, so
			// get the person.

			if (updateForm.isValid()) {
				try {
					aUser = userDataAccess.getUser(this.userId);
				} catch (final AvroRemoteException e) {
					logger.error(e.getMessage());
				}
				// Handle null person in the template.
			}

		} else {
			try {
				aUser = userDataAccess.getUser(userId);
			} catch (final AvroRemoteException e) {
				logger.error(e.getMessage());
			}
			// Handle null person in the template.

			// If the form has errors then we're redisplaying after a redirect.
			// Form will restore your input values but it's up to us to restore
			// Hidden values.

			if (updateForm.getHasErrors()) {
				if (aUser != null) {
					// person.setVersion(versionFlash);
					aUser = versionFlash;
				}
			}

		}

	}

	// Component "confirmDeleteForm" bubbles up the PREPARE_FOR_SUBMIT event
	// during form submission

	void onPrepareForSubmitFromConfirmDeleteForm() {
		// Get objects for the form fields to overlay.
		try {
			aUser = userDataAccess.getUser(userId);
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}

		if (aUser == null) {
			aUser = new G_User();
			confirmDeleteForm.recordError("Person has already been deleted by another process.");
		}
	}

	// Component "confirmDeleteForm" bubbles up the VALIDATE event when it is
	// submitted

	void onPrepareForSubmitFromUpdateForm(final String userId) {
		this.userId = userId;

		// Get objects for the form fields to overlay.
		try {
			aUser = userDataAccess.getUser(this.userId);
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}

		if (aUser == null) {
			aUser = new G_User();
			updateForm.recordError("Person has been deleted by another process.");
		}
	}

	// Component "confirmDeleteForm" bubbles up SUCCESS or FAILURE when it is
	// submitted, depending on whether
	// VALIDATE records an error

	void onPrepareFromCreateForm() throws Exception {
		// Instantiate a Person for the form data to overlay.
		aUser = new G_User();
	}

	boolean onSuccessFromConfirmDeleteForm() {
		// We want to tell our containing page explicitly what person we've
		// deleted, so we trigger new event
		// "successfulDelete" with a parameter. It will bubble up because we
		// don't have a handler method for it.
		componentResources.triggerEvent(SUCCESSFUL_CONFIRM_DELETE, new Object[] { aUser.getUsername() }, null);
		// We don't want "success" to bubble up, so we return true to say we've
		// handled it.
		return true;
	}

	// /////////////////////////////////////////////////////////////////////
	// OTHER
	// /////////////////////////////////////////////////////////////////////

	// Getters

	boolean onSuccessFromCreateForm() {
		// We want to tell our containing page explicitly what person we've
		// created, so we trigger new event
		// "successfulCreate" with a parameter. It will bubble up because we
		// don't have a handler method for it.
		componentResources.triggerEvent(SUCCESSFUL_CREATE, new Object[] { aUser.getUsername() }, null);
		// We don't want "success" to bubble up, so we return true to say we've
		// handled it.
		return true;
	}

	boolean onSuccessFromUpdateForm() {
		// We want to tell our containing page explicitly what person we've
		// updated, so we trigger new event
		// "successfulUpdate" with a parameter. It will bubble up because we
		// don't have a handler method for it.
		componentResources.triggerEvent(SUCCESSFUL_UPDATE, new Object[] { userId }, null);
		// We don't want "success" to bubble up, so we return true to say we've
		// handled it.
		return true;
	}

	boolean onToUpdate(final int userId) {
		// Return false, which means we haven't handled the event so bubble it
		// up.
		// This method is here solely as documentation, because without this
		// method the event would bubble up anyway.
		return false;
	}

	void onValidateFromConfirmDeleteForm() {

		if (confirmDeleteForm.getHasErrors()) {
			// We get here only if a server-side validator detected an error.
			return;
		}

		if ((editModeStr != null) && editModeStr.equals("true")) {
			confirmDeleteForm.recordError("Sorry, but Delete is not allowed in Demo mode.");
		} else {

			try {
				userDataAccess.deleteUser(userId);
			} catch (final Exception e) {
				// Display the cause. In a real system we would try harder to
				// get a user-friendly message.
				confirmDeleteForm.recordError(e.getMessage());
			}

		}

	}

	void onValidateFromCreateForm() {

		if (createForm.getHasErrors()) {
			// We get here only if a server-side validator detected an error.
			return;
		}

		if ((editModeStr != null) && editModeStr.equals("true")) {
			createForm.recordError("Sorry, but Create is not allowed in Demo mode.");
			return;
		}

		try {
			aUser = userDataAccess.registerUser(aUser, "temppassword", false);
		} catch (final Exception e) {
			// Display the cause. In a real system we would try harder to get a
			// user-friendly message.
			createForm.recordError(e.getMessage());
		}
	}

	void onValidateFromUpdateForm() {

		if (updateForm.getHasErrors()) {
			// We get here only if a server-side validator detected an error.
			return;
		}

		try {
			userDataAccess.saveUser(aUser);
		} catch (final Exception e) {
			// Display the cause. In a real system we would try harder to get a
			// user-friendly message.
			updateForm.recordError(e.getMessage());
		}
	}

	void setupRender() {

		if (mode == Mode.REVIEW) {
			if (ValidationUtils.isValid(userId)) {
				aUser = null;
				// Handle null person in the template.
			} else {
				try {
					aUser = userDataAccess.getUser(userId);
				} catch (final AvroRemoteException e) {
					logger.error(e.getMessage());
				}
				// Handle null person in the template.
			}
		}

	}
}
