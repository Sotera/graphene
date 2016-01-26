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

import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_Workspace;
import graphene.model.idlhelper.AuthenticatorHelper;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ioc.IOCSymbols;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.services.ApplicationStateContribution;
import org.apache.tapestry5.services.ApplicationStateCreator;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.validator.ValidatorMacro;

public class CustomSecurityModule {
	public static void bind(final ServiceBinder binder) {
		binder.bind(AuthenticatorHelper.class, BasicAuthenticatorHelper.class);
	}

	@Contribute(ValidatorMacro.class)
	public static void combineValidators(final MappedConfiguration<String, String> configuration) {
		configuration.add("username", "required, minlength=3, maxlength=15");
		configuration.add("password", "required, minlength=6, maxlength=12");
	}

	public static void contributeApplicationDefaults(final MappedConfiguration<String, Object> configuration) {
		// configuration.add(SecuritySymbols.LOGIN_URL, "/graphene/pub/login");
		// configuration.add(SecuritySymbols.UNAUTHORIZED_URL,
		// "/graphene/pub/pagedenied");
		// configuration.add(SecuritySymbols.SUCCESS_URL, "/graphene/index");
		// configuration.add(SecuritySymbols.REDIRECT_TO_SAVED_URL, "true");
		configuration.add(G_SymbolConstants.APPLICATION_MANAGED_SECURITY, false);

		/*
		 * This is a workaround for a problem with Tynamo Security using Subject
		 * Aware Parallel Executor that is unresolved. Version 0.6 should fix
		 * this.
		 */
		configuration.add(IOCSymbols.THREAD_POOL_CORE_SIZE, "1");

		/*
		 * Defaults that are needed by other parts of the application, but might
		 * not be used.
		 */
		configuration.add(G_SymbolConstants.USER_PASSWORD_VALIDATION,
				"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");
		configuration
				.add(G_SymbolConstants.USER_PASSWORD_VALIDATION_MESSAGE,
						"password must be longer than 8 characters and contain a number, upper and lower case, and a special character (no spaces)");
		configuration.add(G_SymbolConstants.USER_NAME_VALIDATION, "[0-9A-Za-z@.]+");

	}

	@Contribute(ComponentRequestHandler.class)
	public static void contributeComponentRequestHandler(
			final OrderedConfiguration<ComponentRequestFilter> configuration) {
		configuration.addInstance("RequiresLogin", AuthenticationFilter.class);
	}

	/**
	 * Control how new workspaces are created.
	 * 
	 * @param config
	 */
	@Contribute(ApplicationStateManager.class)
	public void provideStateCreators(final MappedConfiguration<Class, ApplicationStateContribution> config) {
		final ApplicationStateCreator<G_Workspace> creator = new ApplicationStateCreator<G_Workspace>() {
			@Override
			public G_Workspace create() {
				return new G_Workspace();
			}
		};
		final ApplicationStateContribution contribution = new ApplicationStateContribution(
				PersistenceConstants.SESSION, creator);
		config.add(G_Workspace.class, contribution);
	}

}
