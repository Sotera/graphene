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

package graphene.web.pages;

import graphene.dao.LoggingDAO;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_Workspace;

import java.util.List;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;

public class SimpleBasePage {

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	protected String appName;
	@Property
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_WORKSPACES)
	protected String workspacesEnabled;
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_VERSION)
	protected String appVersion;
	@Property
	@SessionState(create = false)
	protected List<G_Workspace> workspaces;

	protected boolean workspacesExists;
	@Inject
	protected LoggingDAO loggingDao;

	@Inject
	protected AlertManager alertManager;

	@Property
	@Persist
	private boolean highlightZoneUpdates;

	@SessionState(create = false)
	protected G_User user;

	protected boolean userExists;

	@Inject
	protected Logger logger;

	@Inject
	protected Messages messages;

	@Inject
	protected Request request;

	protected Messages getMessages() {
		return messages;
	}

	/**
	 * @return the user
	 */
	public final G_User getUser() {
		return user;
	}

	public String getZoneUpdateFunction() {
		return highlightZoneUpdates ? "highlight" : "show";
	}

	/**
	 * @return the userExists
	 */
	public final boolean isUserExists() {
		return userExists;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public final void setUser(final G_User user) {
		this.user = user;
	}

}
