/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package graphene.web.components.security;

import graphene.model.idl.G_UserDataAccess;
import graphene.web.model.BusinessException;
import graphene.web.security.AuthenticatorHelper;

import java.io.IOException;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.util.StringUtils;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Cookies;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.internal.services.LoginContextService;
import org.tynamo.security.services.SecurityService;

/**
 * Login form component
 * 
 */
public class LoginForm {

	private static final Logger logger = LoggerFactory
			.getLogger(LoginForm.class);

	@Property
	private String grapheneLogin;
	@Inject
	private G_UserDataAccess userAccess;
	@Property
	private String graphenePassword;

	@Inject
	private AuthenticatorHelper authenticatorHelper;
	@Property
	private boolean grapheneRememberMe;

	@Persist(PersistenceConstants.FLASH)
	private String loginMessage;

	@Inject
	private Messages messages;

	@Inject
	private Response response;

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private SecurityService securityService;

	@Inject
	private LoginContextService loginContextService;

	@Inject
	private Cookies cookies;

	@Inject
	@Symbol(SecuritySymbols.REDIRECT_TO_SAVED_URL)
	private boolean redirectToSavedUrl;

	public Object onActionFromGrapheneLoginForm() throws IOException {

		Subject currentUser = securityService.getSubject();

		if (currentUser == null) {
			logger.error("Subject can`t be null");
			// throw new IllegalStateException("Subject can`t be null");
			loginMessage = messages.get("AuthenticationError");
			return null;
		}
		if (grapheneLogin.contains("@")) {
			grapheneLogin = grapheneLogin.split("@")[0];
		}

		/**
		 * We store the password entered into this token. It will later be
		 * compared to the hashed version using whatever hashing routine is set
		 * in the Realm.
		 */
		UsernamePasswordToken token = new UsernamePasswordToken(grapheneLogin,
				graphenePassword);
		token.setRememberMe(grapheneRememberMe);

		try {
			currentUser.login(token);
		} catch (UnknownAccountException e) {
			loginMessage = messages.get("AccountDoesNotExists");
			return null;
		} catch (IncorrectCredentialsException e) {
			loginMessage = messages.get("WrongPassword");
			return null;
		} catch (LockedAccountException e) {
			loginMessage = messages.get("AccountLocked");
			return null;
		} catch (AuthenticationException e) {
			loginMessage = messages.get("AuthenticationError");
			return null;
		}
		try {
			authenticatorHelper.login(grapheneLogin, graphenePassword);
		} catch (BusinessException e) {
			loginMessage = messages.get("InternalAuthenticationError");
			e.printStackTrace();
			return null;
		}

		SavedRequest savedRequest = WebUtils
				.getAndClearSavedRequest(requestGlobals.getHTTPServletRequest());

		if (savedRequest != null
				&& savedRequest.getMethod().equalsIgnoreCase("GET")) {
			try {
				response.sendRedirect(savedRequest.getRequestUrl());
				return null;
			} catch (IOException e) {
				logger.warn("Can't redirect to saved request.");
				return loginContextService.getSuccessPage();
			}
		} else if (redirectToSavedUrl) {
			String requestUri = loginContextService.getSuccessPage();
			if (!requestUri.startsWith("/")) {
				requestUri = "/" + requestUri;
			}
			loginContextService.redirectToSavedRequest(requestUri);
			return null;
		}
		// Cookie[] cookies =
		// requestGlobals.getHTTPServletRequest().getCookies();
		// if (cookies != null) for (Cookie cookie : cookies) if
		// (WebUtils.SAVED_REQUEST_KEY.equals(cookie.getName())) {
		// String requestUri = cookie.getValue();
		// WebUtils.issueRedirect(requestGlobals.getHTTPServletRequest(),
		// requestGlobals.getHTTPServletResponse(), requestUri);
		// return null;
		// }
		return loginContextService.getSuccessPage();
	}

	public void setLoginMessage(String loginMessage) {
		this.loginMessage = loginMessage;
	}

	public String getLoginMessage() {
		if (StringUtils.hasText(loginMessage)) {
			return loginMessage;
		} else {
			return " ";
		}
	}
}
