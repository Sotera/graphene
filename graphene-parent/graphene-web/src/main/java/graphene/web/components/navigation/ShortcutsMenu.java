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
import graphene.model.idl.G_User;
import graphene.model.idl.G_Workspace;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

public class ShortcutsMenu {
	@SessionState(create = false)
	@Property
	private G_User user;

	@Property
	@SessionState(create = false)
	private List<G_Workspace> workspaces;
	@Property
	private boolean workspacesExists;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_WORKSPACES)
	protected boolean workspacesEnabled;
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_MANAGED_SECURITY)
	protected boolean applicationManagedSecurity;

	public boolean getShortcutsAvailable() {
		if (workspacesEnabled || applicationManagedSecurity) {
			return true;
		} else {
			return false;
		}
	}
}
