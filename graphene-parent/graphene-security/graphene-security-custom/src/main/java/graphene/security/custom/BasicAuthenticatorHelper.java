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

package graphene.security.custom;

import javax.servlet.http.HttpServletRequest;

import graphene.business.commons.exception.BusinessException;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idlhelper.AuthenticatorHelper;
import graphene.util.validator.ValidationUtils;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.Session;
import org.slf4j.Logger;
import org.tynamo.security.internal.services.LoginContextService;

/**
 * Basic Security Realm implementation to be replaced with Shiro integration
 * from Tynamo.
 */
@Deprecated
public class BasicAuthenticatorHelper implements AuthenticatorHelper {

	public static final String AUTH_TOKEN = "authToken";

	private final Logger logger;

	private final G_UserDataAccess userDataAccess;
	// Use this to save the logged in user to the session.
	private final ApplicationStateManager applicationStateManager;

	@Inject
	private Request request;
	
	@Inject
	private RequestGlobals rq;

	@Inject
	public BasicAuthenticatorHelper(final G_UserDataAccess userDataAccess,
			final ApplicationStateManager applicationStateManager, final Logger logger) {
		this.userDataAccess = userDataAccess;
		this.applicationStateManager = applicationStateManager;
		this.logger = logger;
	}

	@Override
	public boolean isUserObjectCreated() {
		return applicationStateManager.exists(G_User.class);

	}

	@Override
	public void login(final String username, final String password) throws AvroRemoteException, BusinessException {
		G_User user = userDataAccess.getByUsername(username);
		if (ValidationUtils.isValid(user) && ValidationUtils.isValid(user.getId())) {
			user = userDataAccess.loginUser(user.getId(), password);
			applicationStateManager.set(G_User.class, user);
			request.getSession(true).setAttribute(AUTH_TOKEN, user);
		} else {
			logger.error("Could not login user with username " + username);
		}
	}

	@Override
	public Object loginAndRedirect(final String grapheneLogin, final String graphenePassword,
			final boolean grapheneRememberMe, final RequestGlobals requestGlobals,
			final LoginContextService loginContextService, final Response response, final Messages messages,
			final AlertManager alertManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean loginAuthenticatedUser(final String username) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public Object logout() {
		// this removes the session state object.
		applicationStateManager.set(G_User.class, null);

		// this is the older way of doing it, setting the attribute as null
		final Session session = request.getSession(false);
		if (session != null) {
			session.setAttribute(AUTH_TOKEN, null);
			session.invalidate();
		}
		
		return null;
	}
	
	@Override
	public String getUsername() {
		String username = null;
		HttpServletRequest request = rq.getHTTPServletRequest();
		if (request != null) {
			username = request.getRemoteUser();
		}
		
		return username;
	}

}
