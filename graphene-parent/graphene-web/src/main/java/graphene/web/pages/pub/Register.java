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

package graphene.web.pages.pub;

import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idlhelper.AuthenticatorHelper;
import graphene.util.ExceptionUtil;
import graphene.web.annotations.AnonymousAccess;
import graphene.web.pages.Index;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.internal.services.LoginContextService;
import org.tynamo.security.services.SecurityService;

/**
 * This page the user can create an account
 * 
 */
@AnonymousAccess
public class Register {
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;
	@Inject
	@Path("context:/core/img/logo_graphene_dark_wide.png")
	@Property
	private Asset imgLogo;
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_VERSION)
	private String appVersion;

	@Inject
	private G_UserDataAccess userDataAccess;

	@Property
	@Validate("required,email")
	@Persist(PersistenceConstants.FLASH)
	private String email;

	@Property
	@Validate("required, minlength=1, maxlength=50")
	@Persist(PersistenceConstants.FLASH)
	private String firstName;
	@Property
	@Validate("required, minlength=1, maxlength=50")
	@Persist(PersistenceConstants.FLASH)
	private String lastName;
	@Inject
	private Logger logger;
	@Inject
	private Messages messages;

	@Property
	@Validate("password")
	private String password;

	@Component
	private Form registerForm;
	@Inject
	private AlertManager alertManager;
	@InjectPage
	private Login signin;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.THEME_PATH)
	private String themePath;

	@Property
	@Validate("username")
	@Persist(PersistenceConstants.FLASH)
	private String username;

	@Property
	@Validate("password")
	@Persist(PersistenceConstants.FLASH)
	private String verifyPassword;

	@Inject
	private Response response;

	@Inject
	private RequestGlobals requestGlobals;
	@Inject
	private AuthenticatorHelper authenticatorHelper;
	@Inject
	private SecurityService securityService;
	@Persist(PersistenceConstants.FLASH)
	private String loginMessage;
	@Inject
	private LoginContextService loginContextService;
	@Inject
	@Symbol(SecuritySymbols.REDIRECT_TO_SAVED_URL)
	private boolean redirectToSavedUrl;
	private final boolean rememberMe = true;
	private final boolean createWorkspace = true;

	@OnEvent(value = EventConstants.VALIDATE, component = "RegisterForm")
	public void checkForm() {
		if (!verifyPassword.equals(password)) {
			registerForm.recordError(messages.get("error.verifypassword"));
		}
	}

	@OnEvent(value = EventConstants.SUCCESS, component = "RegisterForm")
	public Object proceedSignup() {
		username = username.toLowerCase();
		try {
			if (userDataAccess.usernameExists(username)) {
				registerForm.recordError(messages.get("error.userexists"));
				return null;
			} else {

				final String fullName = firstName + " " + lastName;
				G_User tempUser = new G_User();
				tempUser.setFullname(fullName);
				tempUser.setEmail(email);
				tempUser.setUsername(username);

				// Get the version that has been registered, because it will
				// have added business logic.
				tempUser = userDataAccess.registerUser(tempUser, password, createWorkspace);
				if (tempUser != null) {
					// log out any previously logged in account.
					authenticatorHelper.logout();

					// tell the user what's about to happen.
					alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS, "You are being logged in.");

					// do the login with the newly created user.
					return authenticatorHelper.loginAndRedirect(username, password, rememberMe, requestGlobals,
							loginContextService, response, messages, alertManager);
				} else {
					logger.error("An error occured while registering the user.");
					alertManager.alert(Duration.SINGLE, Severity.ERROR, "An error occured while registering the user.");
				}
				return Index.class;
			}
		} catch (final Exception ex) {
			final String message = ExceptionUtil.getRootCauseMessage(ex);
			alertManager.alert(Duration.SINGLE, Severity.ERROR, "ERROR: " + message);
			logger.error(message);
			ex.printStackTrace();

		}
		return null;
	}
}
