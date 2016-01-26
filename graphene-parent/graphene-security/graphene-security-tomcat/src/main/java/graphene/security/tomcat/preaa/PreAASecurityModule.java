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

package graphene.security.tomcat.preaa;

import graphene.dao.LoggingDAO;
import graphene.dao.RoleDAO;
import graphene.model.idl.G_Role;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_UserInterfaceMode;
import graphene.model.idl.G_UserLoginEvent;
import graphene.model.idl.G_Workspace;
import graphene.model.idlhelper.AuthenticatorHelper;
import graphene.util.validator.ValidationUtils;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.apache.avro.AvroRemoteException;
import org.apache.shiro.mgt.SubjectFactory;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.internal.services.CookieSource;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.IOCSymbols;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.ServiceOverride;
import org.apache.tapestry5.services.ApplicationStateContribution;
import org.apache.tapestry5.services.ApplicationStateCreator;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.tynamo.security.Security;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.services.SecurityFilterChainFactory;
import org.tynamo.security.services.SecurityModule;
import org.tynamo.security.services.impl.SecurityFilterChain;

/**
 * Pre-Authentication/Authorization Module
 * 
 * (Could also be through of as Post)
 * 
 * Use this module if you application sits behind a proxy (such as a Tomcat+LDAP
 * configuration) which has already authenticated and authorized the user.
 * 
 * This means that you are only required to sniff the user principle from the
 * request--you don't care about user login or registration pages, etc.
 * 
 * @author djue
 * 
 */
@SubModule({ SecurityModule.class })
public class PreAASecurityModule {
	public static void bind(final ServiceBinder binder) {
		binder.bind(AuthenticatorHelper.class, PreAAHelper.class).eagerLoad();
		binder.bind(AuthorizingRealm.class, PreAASecurityRealm.class).withId("PreAA").eagerLoad();
	}

	/**
	 * This is just for the form validators, it will apply the correct client
	 * side validation. You still need to handle server side validation.
	 * 
	 * @param configuration
	 */
	public static void contributeApplicationDefaults(final MappedConfiguration<String, Object> configuration) {
		configuration.add(SecuritySymbols.LOGIN_URL, "/");
		configuration.add(SecuritySymbols.UNAUTHORIZED_URL, "/graphene/pub/pagedenied");
		configuration.add(SecuritySymbols.SUCCESS_URL, "/graphene/index");
		configuration.add(SecuritySymbols.REDIRECT_TO_SAVED_URL, "true");
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

	@Contribute(HttpServletRequestFilter.class)
	@Security
	public static void contributeFilter(final Configuration<SecurityFilterChain> configuration,
			final SecurityFilterChainFactory factory, final WebSecurityManager securityManager,

			@Inject @Symbol(G_SymbolConstants.EXTERNAL_ADMIN_ROLE_NAME) final String externalAdminRoleName,
			@Inject @Symbol(G_SymbolConstants.EXTERNAL_USER_ROLE_NAME) final String externalUserRoleName) {

		// Allow access to the login and registration pages
		configuration.add(factory.createChain("/graphene/pub/**").add(factory.anon()).build());
		configuration.add(factory.createChain("/assets/**").add(factory.anon()).build());
		configuration.add(factory.createChain("/core/**").add(factory.anon()).build());

		configuration
				.add(factory.createChain("/graphene/admin/**").add(factory.roles(), externalAdminRoleName).build());
		// configuration.add(factory.createChain("/*").add(factory.authc()).build());
		// configuration.add(factory.createChain("/**").add(factory.authc()).build());
		/*
		 * force the whole app to authenticate
		 */

	}

	@Contribute(WebSecurityManager.class)
	public static void contributeWebSecurityManager(final Configuration<Realm> configuration,
			@InjectService("PreAA") final Realm realm) {
		configuration.add(realm);
	}

	@Contribute(ServiceOverride.class)
	public static void setUpApplicationServiceOverrides(final MappedConfiguration<Class, Object> config) {
		config.addInstance(SubjectFactory.class, PreAASubjectFactory.class);
	}

	@SuppressWarnings("rawtypes")
	@Contribute(ApplicationStateManager.class)
	public void addApplicationStateCreators(
			final MappedConfiguration<Class, ApplicationStateContribution> configuration,
			final HttpServletRequest request, final G_UserDataAccess userDataAccess, final CookieSource cookieSource,
			@Inject final LoggingDAO loggingDao,
			@Inject @Symbol(G_SymbolConstants.EXTERNAL_ADMIN_ROLE_NAME) final String externalAdminRoleName,
			final RoleDAO rDao, final Logger logger) {

		final ApplicationStateCreator<G_User> currentUserCreator = new ApplicationStateCreator<G_User>() {
			@Override
			public G_User create() {
				G_User user = new G_User();
				try {
					final String username = request.getRemoteUser();
					user = userDataAccess.getByUsername(username);
					final G_Role adminRole = rDao.getRoleByRolename("admin");
					if (ValidationUtils.isValid(user)) {
						if (user != null) {
							user.setNumberlogins(user.getNumberlogins() + 1);
							user.setLastlogin(DateTime.now().getMillis());
							userDataAccess.saveUser(user);
							if (request.isUserInRole(externalAdminRoleName)) {
								userDataAccess.addRole(user, adminRole);
							}
						}
						// applicationStateManager.set(G_User.class, user);
						logger.debug("Found user account " + user.getId() + " for username " + username);
						// request.getSession(true).setAttribute(AUTH_TOKEN,
						// user);
						final G_UserLoginEvent ule = new G_UserLoginEvent();
						ule.setTimeInitiated(DateTime.now().getMillis());
						ule.setUserName(username);
						ule.setUserId(user.getId());
						// success = true;
						loggingDao.recordUserLoginEvent(ule);
					} else if (ValidationUtils.isValid(username)) {
						logger.debug("Creating a new user account for " + username);

						// create the account for the user
						final G_User d = new G_User();
						d.setActive(true);
						d.setAvatar("unknown.png");
						d.setEmail(username + "@preauthenticateduser.com");
						d.setFullname(username);
						d.setUimode(G_UserInterfaceMode.DOMAIN_EXPERT);
						d.setLastlogin(DateTime.now().getMillis());
						d.setModified(DateTime.now().getMillis());
						d.setCreated(DateTime.now().getMillis());
						d.setUsername(username);
						user = userDataAccess.registerUser(d, "password", true);
						if (request.isUserInRole(externalAdminRoleName)) {
							final boolean success = userDataAccess.addRole(user, adminRole);
							logger.debug("Adding admin role: " + success);
						}
						// TODO: Update the user last login date, etc.
						// applicationStateManager.set(G_User.class, user);
						logger.debug("Done creating useraccount for " + username + ", logging user in.");
						final G_UserLoginEvent ule = new G_UserLoginEvent();
						ule.setTimeInitiated(DateTime.now().getMillis());
						ule.setUserName(username);
						ule.setUserId(user.getId());
						// success = true;
						loggingDao.recordUserLoginEvent(ule);
					}
				} catch (final AvroRemoteException e) {
					e.printStackTrace();
				}
				return user;
			}
		};

		configuration.add(G_User.class, new ApplicationStateContribution(PersistenceConstants.SESSION,
				currentUserCreator));
	}

	@Contribute(RequestHandler.class)
	public void addRequestFilters(final OrderedConfiguration<RequestFilter> configuration,
			@InjectService("currentUserFilter") final RequestFilter currentUserFilter) {
		configuration.add("currentUser", currentUserFilter, "before:ResteasyRequestFilter,after:StoreIntoGlobals");
	}

	public RequestFilter buildCurrentUserFilter(final G_UserDataAccess userService,
			final ApplicationStateManager applicationStateManager, final HttpServletRequest servletRequest) {
		return new RequestFilter() {
			/**
			 * Thanks Kalle!
			 */

			@Override
			public boolean service(final Request request, final Response response, final RequestHandler handler)
					throws IOException {
				// TODO Auto-generated method stub
				if ((servletRequest.getRemoteUser() != null) && !applicationStateManager.exists(G_User.class)) {
					applicationStateManager.get(G_User.class);
				}
				return handler.service(request, response);
			}

		};
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