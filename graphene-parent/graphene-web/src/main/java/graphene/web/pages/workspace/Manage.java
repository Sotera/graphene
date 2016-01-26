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

package graphene.web.pages.workspace;

import graphene.model.idl.G_VisualType;
import graphene.web.annotations.PluginPage;
import graphene.web.components.Layout;
import graphene.web.components.navigation.RecentWorkspaces;
import graphene.web.components.workspace.WorkspaceEditor.Mode;
import graphene.web.pages.SimpleBasePage;

import javax.inject.Inject;

import org.apache.tapestry5.annotations.ActivationRequestParameter;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;

@PluginPage(visualType = G_VisualType.MANAGE_WORKSPACES, menuName = "Manage Workspaces", icon = "fa fa-lg fa-fw fa-list-alt")
public class Manage extends SimpleBasePage {

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

	@Property
	private String listWorkspaceId;

	@InjectComponent
	private Zone listZone;
    
    @InjectComponent
    private Layout layout;
  
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

	// The code

	// onPassivate() is called by Tapestry to get the activation context to put
	// in the URL.

	// onActivate() is called by Tapestry to pass in the activation context from
	// the URL.

	void onActivate() {
		listWorkspaceId = editorWorkspaceId;
	}
	
    // Handle event "cancelConfirmDelete" from component "editor"
	Object onCancelConfirmDeleteFromEditor(final String workspaceId) {
		editorMode = Mode.REVIEW;
		editorWorkspaceId = workspaceId;
		listWorkspaceId = workspaceId;
		return null;
	}

	// /////////////////////////////////////////////////////////////////////
	// FILTER
	// /////////////////////////////////////////////////////////////////////

	// Handle event "filter" from component "list"
	void onFilterFromList() {
		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(listZone);
		}
	}

    // Handle event "selected" from component "list"
	void onSelectedFromList(final String workspaceId) {
		editorMode = Mode.REVIEW;
		editorWorkspaceId = workspaceId;
		listWorkspaceId = workspaceId;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(listZone).addRender(editorZone);
		}
	}

	public void updateOnSuccessfulCreateFromEditor(final String workspaceId) {
		listWorkspaceId = workspaceId;
	}

	public void updateOnSuccessfulDeleteFromEditor(boolean deletedCurrentlySelected) {
		listWorkspaceId = null;
		
		if (deletedCurrentlySelected)
		    layout.getHeader().getRecentWorkspaces().selectMostRecentWorkspace();
	}

	void onToConfirmDeleteFromEditor(final String workspaceId) {
		editorMode = Mode.CONFIRM_DELETE;
		// editorWorkspaceId = workspaceId;
		editorMode = null;
		editorWorkspaceId = null;
		listWorkspaceId = null;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(listZone).addRender(editorZone);
		}
	}

	void onToCreate() {
		editorMode = Mode.CREATE;
		editorWorkspaceId = null;
		listWorkspaceId = null;

		if (request.isXHR()) {
			ajaxResponseRenderer.addRender(listZone).addRender(editorZone);
		}
	}

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

	public void setAction(final String string) {
		if ("create".equals(string)) {
			onToCreate();
		}
	}

	void setupRender() {
		listWorkspaceId = editorWorkspaceId;
	}
}
