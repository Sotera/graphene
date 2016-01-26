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

package graphene.security.noop;

import graphene.business.commons.exception.BusinessException;
import graphene.dao.LoggingDAO;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_UserLoginEvent;
import graphene.model.idlhelper.AuthenticatorHelper;
import graphene.util.validator.ValidationUtils;

import java.io.IOException;

import javax.servlet.http.HttpSession;

import org.apache.avro.AvroRemoteException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.internal.services.LoginContextService;
import org.tynamo.security.services.SecurityService;

/**
 * Intended to encapsulate the creation/destruction logic for the user sso.
 * 
 * @author djue
 * 
 */

public class NoAuthenticatorHelper implements AuthenticatorHelper {

	public static final String AUTH_TOKEN = "authToken";

	// Use this to save the logged in user to the session.
	private final ApplicationStateManager applicationStateManager;

	private final Logger logger;

	private final SecurityService securityService;

	private final G_UserDataAccess userDataAccess;

	@SessionState(create = false)
	private G_User user;

	private boolean userExists;

	@Inject
	@Symbol(SecuritySymbols.REDIRECT_TO_SAVED_URL)
	private boolean redirectToSavedUrl;

	@Inject
	protected LoggingDAO loggingDao;

	@Inject
	private RequestGlobals rq;

	@Inject
	public NoAuthenticatorHelper(final G_UserDataAccess userDataAccess,
			final ApplicationStateManager applicationStateManager, final Logger logger,
			final SecurityService securityService) {
		this.userDataAccess = userDataAccess;
		this.securityService = securityService;
		this.applicationStateManager = applicationStateManager;
		this.logger = logger;
	}

	@Override
	public boolean isUserObjectCreated() {
		final boolean userSsoExists = applicationStateManager.exists(G_User.class);
		final HttpSession session = rq.getHTTPServletRequest().getSession(false);
		if (session != null) {
			return session.getAttribute(AUTH_TOKEN) != null;
		}

		logger.debug(userSsoExists ? "User SSO exists." : "User SSO does not exist.");
		return userSsoExists;
	}

	@Override
	public void login(final String username, final String password) throws AvroRemoteException, BusinessException {

		G_User user = userDataAccess.getByUsername(username);
		if (ValidationUtils.isValid(user)) {
			if (ValidationUtils.isValid(user.getId())) {
				user = userDataAccess.loginUser(user.getId(), password);
				applicationStateManager.set(G_User.class, user);
				// request.getSession(true).setAttribute(AUTH_TOKEN, user);
			} else {
				logger.error("The user object was lacking an id for username: " + username);
				throw new BusinessException("Could not login user with username " + username);
			}
		} else {
			logger.error("Could not login user with username " + username);
			throw new BusinessException("Could not login user with username " + username);
		}
	}

	@Override
	public Object loginAndRedirect(final String grapheneLogin, final String graphenePassword,
			final boolean grapheneRememberMe, final RequestGlobals requestGlobals,
			final LoginContextService loginContextService, final Response response, final Messages messages,
			final AlertManager alertManager) {

		final Subject currentUser = securityService.getSubject();
		final G_UserLoginEvent ule = new G_UserLoginEvent();
		ule.setTimeInitiated(DateTime.now().getMillis());
		ule.setUserName(grapheneLogin);
		ule.setUserId(grapheneLogin);
		loggingDao.recordUserLoginEvent(ule);

		if (currentUser == null) {
			logger.error("Subject can't be null");
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, messages.get("AuthenticationError"));
			return null;
		}

		/**
		 * We store the password entered into this token. It will later be
		 * compared to the hashed version using whatever hashing routine is set
		 * in the Realm.
		 */
		final UsernamePasswordToken token = new UsernamePasswordToken(grapheneLogin, graphenePassword);
		token.setRememberMe(grapheneRememberMe);

		try {
			currentUser.login(token);
			login(grapheneLogin, graphenePassword);
			// final SavedRequest savedRequest =
			// WebUtils.getAndClearSavedRequest(requestGlobals.getHTTPServletRequest());
			//
			// if ((savedRequest != null) &&
			// savedRequest.getMethod().equalsIgnoreCase("GET")) {
			// if (ValidationUtils.isValid(savedRequest.getRequestUrl())) {
			// response.sendRedirect(savedRequest.getRequestUrl());
			// logger.debug("A redirect request was sent, returning null response for authentication helper.");
			// //
			// loginContextService.redirectToSavedRequest(savedRequest.getRequestUrl());
			// // return savedRequest.getRequestUrl();
			// return null;
			// } else {
			// logger.warn("Can't redirect to saved request.");
			// return loginContextService.getSuccessPage();
			// }
			// }

			if (redirectToSavedUrl) {
				String requestUri = loginContextService.getSuccessPage();
				if (!requestUri.startsWith("/")) {
					requestUri = "/" + requestUri;
				}
				loginContextService.redirectToSavedRequest(requestUri);
				// this isn't working but should: return requestUri;
				// return loginContextService.getSuccessPage();
				return null;
			}
			// requestGlobals.getHTTPServletRequest().getCookies();
			// if (cookies != null) {
			// for (final Cookie cookie : cookies) {
			// if (WebUtils.SAVED_REQUEST_KEY.equals(cookie.getName())) {
			// final String requestUri = cookie.getValue();
			// WebUtils.issueRedirect(requestGlobals.getHTTPServletRequest(),
			// requestGlobals.getHTTPServletResponse(), requestUri);
			// return null;
			// }
			// }
			// }
			return loginContextService.getSuccessPage();
		} catch (final UnknownAccountException e) {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, messages.get("AccountDoesNotExist"));
		} catch (final IncorrectCredentialsException e) {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, messages.get("WrongPassword"));
		} catch (final LockedAccountException e) {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, messages.get("AccountLocked"));
		} catch (final AvroRemoteException e) {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, messages.get("InternalAuthenticationError"));
			logger.error(e.getMessage());
		} catch (final BusinessException e) {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, messages.get("InternalAuthenticationError"));
			logger.error(e.getMessage());
		} catch (final IOException e) {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, messages.get("InternalAuthenticationError"));
			logger.error(e.getMessage());
		}
		return null;
	}

	/**
	 * Login a user who is previously authenticated
	 * 
	 * @param username
	 */
	@Override
	public boolean loginAuthenticatedUser(final String username) {
		boolean success = false;
		G_User user;
		try {
			user = userDataAccess.getByUsername(username);
			if (user != null) {
				user = userDataAccess.loginAuthenticatedUser(user.getId());
				applicationStateManager.set(G_User.class, user);
				// request.getSession(true).setAttribute(AUTH_TOKEN, user);
			}
			success = true;
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		return success;
	}

	@Override
	public Object logout() {
		logger.debug(securityService.isAuthenticated() ? "During Logout: User is authenticated"
				: "During Logout: User is not authenticated");
		logger.debug(userExists ? "During Logout: User SSO exists" : "During Logout: User SSO does not exist");

		// this removes the session state object.
		applicationStateManager.set(G_User.class, null);

		securityService.getSubject().logout();
		
		return null;
	}
	
	@Override
	public String getUsername() {
		return null;
	}
	
}
