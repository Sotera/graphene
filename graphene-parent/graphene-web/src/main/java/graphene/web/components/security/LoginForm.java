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

import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idlhelper.AuthenticatorHelper;

import java.io.IOException;

import org.apache.shiro.util.StringUtils;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.corelib.components.TextField;
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

	private static final Logger logger = LoggerFactory.getLogger(LoginForm.class);

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String username;

	@Inject
	private G_UserDataAccess userDataAccess;

	@Property
	@Persist(PersistenceConstants.FLASH)
	private String userPassword;

	@Inject
	private AuthenticatorHelper authenticatorHelper;
	@Property
	private boolean rememberMe;

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
	@Inject
	private AlertManager alertManager;

	@Component(id = "grapheneLoginForm")
	private Form grapheneLoginForm;

	@Component(id = "usernameField")
	private TextField grapheneLoginField;

	@Component(id = "passwordField")
	private TextField passwordField;

	@Inject
	@Symbol(value = G_SymbolConstants.USER_NAME_VALIDATION)
	private String userNamePattern;

	@Inject
	@Symbol(value = G_SymbolConstants.USER_PASSWORD_VALIDATION)
	private String userPasswordPattern;

	public String getLoginMessage() {
		if (StringUtils.hasText(loginMessage)) {
			return loginMessage;
		} else {
			return " ";
		}
	}

	public Object onActionFromGrapheneLoginForm() throws IOException {

		return authenticatorHelper.loginAndRedirect(username, userPassword, rememberMe, requestGlobals,
				loginContextService, response, messages, alertManager);

	}

	void onValidateFromGrapheneLoginForm() {
		// Error if the names don't contain letters only
		// if (username != null) {
		// if (!username.matches(userNamePattern)) {
		// grapheneLoginForm.recordError(grapheneLoginField,
		// "Login does not meet requirements.");
		// }
		// }

		// if (userPassword != null) {
		// if (!userPassword.matches(userPasswordPattern)) {
		// grapheneLoginForm.recordError(passwordField,
		// "Password does not meet requirements.");
		// }
		// }
	}

	public void setLoginMessage(final String loginMessage) {
		this.loginMessage = loginMessage;
	}
}
