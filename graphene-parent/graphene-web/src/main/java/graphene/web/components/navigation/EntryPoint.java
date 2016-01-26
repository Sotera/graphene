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

package graphene.web.components.navigation;

import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_Workspace;
import graphene.util.validator.ValidationUtils;
import graphene.web.pages.workspace.Manage;

import java.util.List;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

public class EntryPoint {
	@Property
	private G_Workspace currentWorkspace;

	@Inject
	private G_UserDataAccess userDataAccess;

	@Property
	@Persist
	private boolean highlightZoneUpdates;
	@Property
	private String listWorkspaceId;
	@Property
	@SessionState(create = false)
	protected List<G_Workspace> workspaces;
	@Property
	@SessionState(create = false)
	protected G_Workspace currentlySelectedWorkspace;
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
	@Property
	protected boolean workspacesExists;
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_WORKSPACES)
	@Property
	private boolean workspacesEnabled;

	@Inject
	private Logger logger;

	@Inject
	private AlertManager alertManager;

	@Property
	private boolean currentSelectedWorkspaceExists;

	@InjectPage
	private Manage detailPage;

	public boolean getWorkspacesAvailable() {
		boolean available = false;
		if (workspacesExists && ValidationUtils.isValid(workspaces)) {
			available = true;
		}
		return available;
	}

	public String getZoneUpdateFunction() {
		return highlightZoneUpdates ? "highlight" : "show";
	}

	boolean onSelected(final String workspaceId) {

		boolean foundById = false;
		for (final G_Workspace w : workspaces) {
			if (w.getId().equals(workspaceId)) {
				currentlySelectedWorkspace = w;
				foundById = true;
			}
		}
		if (foundById) {
			alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS, "Changed to workspace "
					+ currentlySelectedWorkspace.getTitle());
		} else {
			// Note, we also let an previously selected currentWorkspace
			// stay the way it was.
			final String warnMessage = "Unable to load workspace id " + workspaceId
					+ ". Try a different workspace or create a new one.";
			logger.warn(warnMessage);
			alertManager.alert(Duration.SINGLE, Severity.WARN, "WARN: " + warnMessage);
		}
		return true;
	}

	Object onToCreate() {
		detailPage.setAction("create");
		// detailPage.initializeFor(detail);
		return detailPage;
	}
}
