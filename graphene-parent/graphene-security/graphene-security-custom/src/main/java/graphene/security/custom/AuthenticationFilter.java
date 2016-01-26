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
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idlhelper.AuthenticatorHelper;
import graphene.util.validator.ValidationUtils;
import graphene.web.annotations.ProtectedPage;
import graphene.web.commons.IIntermediatePage;
import graphene.web.pages.pub.Login;
import graphene.web.pages.pub.PageDenied;
import graphene.web.pages.pub.Register;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.internal.EmptyEventContext;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PageRenderRequestParameters;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;

/**
 * Intercepts the current page to redirect through the requested page or to the
 * authentication page if login is required. For more understanding read the
 * following tutorial <a href=
 * "http://tapestryjava.blogspot.com/2009/12/securing-tapestry-pages-with.html">
 * Securing Tapestry Pages with annotations </a>
 * 
 * @author karesti
 * @version 1.0
 */
public class AuthenticationFilter implements ComponentRequestFilter {
	private enum AuthCheckResult {
		AUTHENTICATE, AUTHORIZED, DENIED, RELOAD_XHR;
	}

	private ApplicationStateManager sessionStateManager;
	private static final String COMPONENT_PARAM_PREFIX = "t:";
	private final AuthenticatorHelper authenticator;
	private final ComponentSource componentSource;
	@Symbol(G_SymbolConstants.GRAPHENE_WEB_CORE_PREFIX)
	private final String corePrefix = "";
	private final String defaultPage;
	@Inject
	private Logger logger;
	private final PageRenderLinkSource renderLinkSource;

	private final Request request;

	private final Response response;

	private final String signinPage;

	private final String signupPage;

	private G_UserDataAccess userDataAccess;
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_AUTO_LOGIN)
	private boolean enableAutoLogin;
	@Inject
	@Symbol(G_SymbolConstants.AUTO_LOGIN_USERNAME)
	private String autoLoginUsername;

	public AuthenticationFilter(final PageRenderLinkSource renderLinkSource, final ComponentSource componentSource,
			final Response response, final Request request, final AuthenticatorHelper authenticator) {
		this.renderLinkSource = renderLinkSource;
		this.componentSource = componentSource;
		this.response = response;
		this.request = request;
		this.authenticator = authenticator;
		defaultPage = "";// corePrefix + "/" + Index.class.getSimpleName();
		signinPage = corePrefix + "/" + Login.class.getSimpleName();
		signupPage = corePrefix + "/" + Register.class.getSimpleName();
	}

	/**
	 * Automatically logs you in as the given user. Intended for use in
	 * development environment only.
	 */
	private void autoLogin() {
		if (enableAutoLogin) {
			try {
				final G_User user = userDataAccess.getByUsername(autoLoginUsername);
				if (ValidationUtils.isValid(user)) {
					logger.info(user.getUsername() + " has been auto-logged-in.");
					sessionStateManager.set(G_User.class, user);
				} else {
					logger.error("Could not find auto-login user account: " + autoLoginUsername);
				}
			} catch (final Exception e) {
				throw new IllegalStateException(e);
			}
		}
	}

	public AuthCheckResult checkAuthorityToPage(final String requestedPageName) throws IOException {

		// Does the page have security annotations @ProtectedPage or
		// @RolesAllowed?

		final Component page = componentSource.getPage(requestedPageName);
		final boolean protectedPage = page.getClass().getAnnotation(ProtectedPage.class) != null;
		final RolesAllowed rolesAllowed = page.getClass().getAnnotation(RolesAllowed.class);

		// If the security annotations on the page conflict in meaning, then
		// error

		if ((rolesAllowed != null) && !protectedPage) {
			throw new IllegalStateException("Page \"" + requestedPageName
					+ "\" is annotated with @RolesAllowed but not @ProtectedPage.");
		}

		// If page is public (ie. not protected), then everyone is authorised to
		// it so allow access

		if (!protectedPage) {
			return AuthCheckResult.AUTHORIZED;
		}

		// If request is AJAX with no session, return an AJAX response that
		// forces reload of the page

		if (request.isXHR() && (request.getSession(false) == null)) {
			return AuthCheckResult.RELOAD_XHR;
		}

		// If user has not been authenticated, disallow.

		if (!isAuthenticated()) {
			return AuthCheckResult.AUTHENTICATE;
		}

		// If user is authorised to the page, then all is well.

		if (isAuthorised(rolesAllowed)) {
			return AuthCheckResult.AUTHORIZED;
		}

		// Fell through, so redirect to the PageDenied page.

		final Link pageProtectedLink = renderLinkSource.createPageRenderLinkWithContext(PageDenied.class,
				requestedPageName);
		response.sendRedirect(pageProtectedLink);
		return AuthCheckResult.DENIED;

	}

	private Link createLinkToRequestedPage(final String requestedPageName, final EventContext eventContext) {

		// Create a link to the page you wanted.

		Link linkToRequestedPage;

		if (eventContext instanceof EmptyEventContext) {
			linkToRequestedPage = renderLinkSource.createPageRenderLink(requestedPageName);
		} else {
			final Object[] args = new String[eventContext.getCount()];
			for (int i = 0; i < eventContext.getCount(); i++) {
				args[i] = eventContext.get(String.class, i);
			}
			linkToRequestedPage = renderLinkSource.createPageRenderLinkWithContext(requestedPageName, args);
		}

		// Add any activation request parameters (AKA query parameters).

		final List<String> parameterNames = request.getParameterNames();

		for (final String parameterName : parameterNames) {
			linkToRequestedPage.removeParameter(parameterName);
			if (!parameterName.startsWith(COMPONENT_PARAM_PREFIX)) {
				linkToRequestedPage.addParameter(parameterName, request.getParameter(parameterName));
			}
		}

		return linkToRequestedPage;
	}

	private Link createLoginPageLinkWithMemory(final Link requestedPageLink) {

		final IIntermediatePage loginPage = (IIntermediatePage) componentSource.getPage(Login.class);
		loginPage.setNextPageLink(requestedPageLink);
		final Link loginPageLink = renderLinkSource.createPageRenderLink(Login.class);

		return loginPageLink;
	}

	@Override
	public void handleComponentEvent(final ComponentEventRequestParameters parameters,
			final ComponentRequestHandler handler) throws IOException {

		final AuthCheckResult result = checkAuthorityToPage(parameters.getActivePageName());

		if (result == AuthCheckResult.AUTHORIZED) {
			handler.handleComponentEvent(parameters);
		} else if (result == AuthCheckResult.DENIED) {
			// The method will have set the response to redirect to the
			// PageDenied page.
			return;
		} else if (result == AuthCheckResult.RELOAD_XHR) {

			// Return an AJAX response that reloads the page.

			final Link requestedPageLink = createLinkToRequestedPage(parameters.getActivePageName(),
					parameters.getPageActivationContext());
			final OutputStream os = response.getOutputStream("application/json;charset=UTF-8");
			os.write(("{\"redirectURL\":\"" + requestedPageLink.toAbsoluteURI() + "\"}").getBytes());
			os.close();
			return;
		} else if (result == AuthCheckResult.AUTHENTICATE) {

			// Redirect to the Login page, with memory of the request.

			final Link requestedPageLink = createLinkToRequestedPage(parameters.getActivePageName(),
					parameters.getPageActivationContext());
			final Link loginPageLink = createLoginPageLinkWithMemory(requestedPageLink);

			response.sendRedirect(loginPageLink);
		} else {
			throw new IllegalStateException(result.toString());
		}

	}

	@Override
	public void handlePageRender(final PageRenderRequestParameters parameters, final ComponentRequestHandler handler)
			throws IOException {

		final AuthCheckResult result = checkAuthorityToPage(parameters.getLogicalPageName());

		if (result == AuthCheckResult.AUTHORIZED) {
			handler.handlePageRender(parameters);
		} else if (result == AuthCheckResult.DENIED) {
			// The method will have set the response to redirect to the
			// PageDenied page.
			return;
		} else if (result == AuthCheckResult.AUTHENTICATE) {

			// Redirect to the Login page, with memory of the request.

			final Link requestedPageLink = createLinkToRequestedPage(parameters.getLogicalPageName(),
					parameters.getActivationContext());
			final Link loginPageLink = createLoginPageLinkWithMemory(requestedPageLink);

			response.sendRedirect(loginPageLink);
		} else {
			throw new IllegalStateException(result.toString());
		}

	}

	private boolean isAuthenticated() throws IOException {

		// If a Visit already exists in the session then you have already been
		// authenticated
		if (authenticator.isUserObjectCreated()) {
			return true;
		}

		// Else if "auto-login" is on, try auto-logging in.
		// - this facility is for development environment only. It avoids
		// getting you thrown out of the
		// app every time the session clears eg. when app is restarted.

		else {
			if (enableAutoLogin) {
				autoLogin();
				return true;
			}
		}

		return false;
	}

	private boolean isAuthorised(final RolesAllowed rolesAllowed) throws IOException {
		boolean authorised = false;

		if (rolesAllowed == null) {
			authorised = true;
		} else {
			// Here we could check whether the user's role, or perhaps roles,
			// include one of the rolesAllowed.
			// Typically we'd cache the user's roles in the Visit.
		}

		return authorised;
	}

}
