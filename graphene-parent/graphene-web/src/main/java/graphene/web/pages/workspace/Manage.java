package graphene.web.pages.workspace;

import graphene.model.idl.G_VisualType;
import graphene.web.annotations.PluginPage;
import graphene.web.components.workspace.WorkspaceEditor.Mode;

import javax.inject.Inject;

import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

@PluginPage(visualType = G_VisualType.SETTINGS, menuName = "Manage Workspaces", icon = "fa fa-lg fa-fw fa-list-alt")
public class Manage {

	// The activation context

	@Inject
	private AjaxResponseRenderer ajaxResponseRenderer;

	@Property
	@ActivationRequestParameter
	private Mode editorMode;

	// Screen fields

	@Property
	@ActivationRequestParameter
	private String editorWorkspaceId;

	@InjectComponent
	private Zone editorZone;
	// @InjectComponent
	// private RecentWorkspaces recentWorkspaces;

	@Property
	@Persist
	private boolean highlightZoneUpdates;

	// Generally useful bits and pieces

	@Property
	private String listWorkspaceId;

	@InjectComponent
	private Zone listZone;

	@Property
	// If we use @ActivationRequestParameter instead of @Persist, then our
	// handler for filter form success would have
	// to render more than just the listZone, it would have to render all other
	// links and forms: it would need a zone
	// around the "Create..." link so it could render it; and it would render
	// the editorZone, which would be destructive
	// if the user has been typing into Create or Update. Alternatively, it
	// could use a custom JavaScript callback to
	// update the partialName in all other links and forms - see
	// AjaxResponseRenderer#addCallback(JavaScriptCallback).
	@Persist
	private String partialName;

	@Inject
	private Request request;

	// The code

	// onPassivate() is called by Tapestry to get the activation context to put
	// in the URL.

	public String getZoneUpdateFunction() {
		return highlightZoneUpdates ? "highlight" : "show";
	}

	// onActivate() is called by Tapestry to pass in the activation context from
	// the URL.

	void onActivate() {
		listWorkspaceId = editorWorkspaceId;
	}

	Object onCancelConfirmDeleteFromEditor(final String workspaceId) {
		editorMode = Mode.REVIEW;
		editorWorkspaceId = workspaceId;
		listWorkspaceId = workspaceId;
		return null;
	}

	void onCancelCreateFromEditor() {
		editorMode = null;
		editorWorkspaceId = null;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(editorZone);
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// CREATE
	// /////////////////////////////////////////////////////////////////////

	// Handle event "toCreate" from this page

	void onCancelUpdateFromEditor(final String workspaceId) {
		editorMode = Mode.REVIEW;
		editorWorkspaceId = workspaceId;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(editorZone);
		}
	}

	// Handle event "cancelCreate" from component "editor"

	void onFailedConfirmDeleteFromEditor(final String workspaceId) {
		editorMode = Mode.CONFIRM_DELETE;
		editorWorkspaceId = workspaceId;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(editorZone);
		}
	}

	// Handle event "successfulCreate" from component "editor"

	void onFailedCreateFromEditor() {
		editorMode = Mode.CREATE;
		editorWorkspaceId = null;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(editorZone);
		}
	}

	// Handle event "failedCreate" from component "editor"

	void onFailedDeleteFromEditor(final String workspaceId) {
		editorMode = Mode.REVIEW;
		editorWorkspaceId = workspaceId;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(editorZone);
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// REVIEW
	// /////////////////////////////////////////////////////////////////////

	// Handle event "selected" from component "list"

	void onFailedUpdateFromEditor(final String workspaceId) {
		editorMode = Mode.UPDATE;
		editorWorkspaceId = workspaceId;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(editorZone);
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// UPDATE
	// /////////////////////////////////////////////////////////////////////

	// Handle event "toUpdate" from component "editor"

	// /////////////////////////////////////////////////////////////////////
	// FILTER
	// /////////////////////////////////////////////////////////////////////

	// Handle event "filter" from component "list"

	void onFilterFromList() {
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(listZone);
		}
	}

	// Handle event "cancelUpdate" from component "editor"

	// Object[] onPassivate() {
	//
	// if (request.isXHR()) {
	// arpEditorMode = editorMode;
	// arpEditorWorkspaceId = editorWorkspaceId;
	//
	// return null;
	// } else {
	//
	// if (editorMode == null) {
	// return null;
	// } else if (editorMode == Mode.CREATE) {
	// return new Object[] { editorMode };
	// } else if ((editorMode == Mode.REVIEW) || (editorMode == Mode.UPDATE)
	// || (editorMode == Mode.CONFIRM_DELETE)) {
	// return new Object[] { editorMode, editorWorkspaceId };
	// } else {
	// throw new IllegalStateException(editorMode.toString());
	// }
	// }
	//
	// }

	// Handle event "successfulUpdate" from component "editor"

	void onSelectedFromList(final String workspaceId) {
		editorMode = Mode.REVIEW;
		editorWorkspaceId = workspaceId;
		listWorkspaceId = workspaceId;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(listZone).addRender(editorZone);
		}
	}

	// Handle event "failedUpdate" from component "editor"

	void onSuccessfulConfirmDeleteFromEditor(final String workspaceId) {
		editorMode = null;
		editorWorkspaceId = workspaceId;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(editorZone);
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// DELETE
	// /////////////////////////////////////////////////////////////////////

	// Handle event "successfulDelete" from component "editor"

	void onSuccessfulCreateFromEditor(final String workspaceId) {
		editorMode = Mode.REVIEW;
		editorWorkspaceId = workspaceId;
		listWorkspaceId = workspaceId;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(listZone).addRender(editorZone);
		}
	}

	// Handle event "failedDelete" from component "editor"

	void onSuccessfulDeleteFromEditor(final String workspaceId) {
		editorMode = null;
		editorWorkspaceId = null;
		listWorkspaceId = null;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(listZone).addRender(editorZone);
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// CONFIRM DELETE - used only when JavaScript is disabled.
	// /////////////////////////////////////////////////////////////////////

	// Handle event "toConfirmDelete" from component "editor"

	void onSuccessfulUpdateFromEditor(final String workspaceId) {
		editorMode = Mode.REVIEW;
		editorWorkspaceId = workspaceId;
		listWorkspaceId = workspaceId;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(listZone).addRender(editorZone);
		}
	}

	// Handle event "cancelConfirmDelete" from component "editor"

	void onToConfirmDeleteFromEditor(final String workspaceId) {
		editorMode = Mode.CONFIRM_DELETE;
		editorWorkspaceId = workspaceId;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(editorZone);
		}
	}

	// Handle event "successfulConfirmDelete" from component "editor"

	void onToCreate() {
		editorMode = Mode.CREATE;
		editorWorkspaceId = null;
		listWorkspaceId = null;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(listZone).addRender(editorZone);
		}
	}

	// Handle event "failedConfirmDelete" from component "editor"

	void onToUpdateFromEditor(final String workspaceId) {
		editorMode = Mode.UPDATE;
		editorWorkspaceId = workspaceId;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(editorZone);
		}
	}

	// /////////////////////////////////////////////////////////////////////
	// GETTERS
	// /////////////////////////////////////////////////////////////////////

	void setupRender() {
		listWorkspaceId = editorWorkspaceId;
	}
}
