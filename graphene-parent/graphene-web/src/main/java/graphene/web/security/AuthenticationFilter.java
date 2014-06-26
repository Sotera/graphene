package graphene.web.security;

import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.web.annotations.AnonymousAccess;
import graphene.web.annotations.ProtectedPage;
import graphene.web.commons.IIntermediatePage;
import graphene.web.pages.Index;
import graphene.web.pages.infrastructure.PageDenied;
import graphene.web.pages.pub.Login;
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
	private final String autoLoginStr = System
			.getProperty("graphene.auto-login");

	private enum AuthCheckResult {
		AUTHENTICATE, AUTHORIZED, DENIED, RELOAD_XHR;
	}

	private ApplicationStateManager sessionStateManager;
	private static final String COMPONENT_PARAM_PREFIX = "t:";
	private final AuthenticatorHelper authenticator;
	private final ComponentSource componentSource;
	@Symbol(G_SymbolConstants.GRAPHENE_WEB_CORE_PREFIX)
	private String corePrefix = "";
	private String defaultPage;
	@Inject
	private Logger logger;
	private final PageRenderLinkSource renderLinkSource;

	private final Request request;

	private final Response response;

	private String signinPage;

	private String signupPage;

	public AuthenticationFilter(PageRenderLinkSource renderLinkSource,
			ComponentSource componentSource, Response response,
			Request request, AuthenticatorHelper authenticator) {
		this.renderLinkSource = renderLinkSource;
		this.componentSource = componentSource;
		this.response = response;
		this.request = request;
		this.authenticator = authenticator;
		defaultPage = corePrefix + "/" + Index.class.getSimpleName();
		signinPage = corePrefix + "/" + Login.class.getSimpleName();
		signupPage = corePrefix + "/" + Register.class.getSimpleName();
	}

	public AuthCheckResult checkAuthorityToPage(String requestedPageName)
			throws IOException {

		// Does the page have security annotations @ProtectedPage or
		// @RolesAllowed?

		Component page = componentSource.getPage(requestedPageName);
		boolean protectedPage = page.getClass().getAnnotation(
				ProtectedPage.class) != null;
		RolesAllowed rolesAllowed = page.getClass().getAnnotation(
				RolesAllowed.class);

		// If the security annotations on the page conflict in meaning, then
		// error

		if (rolesAllowed != null && !protectedPage) {
			throw new IllegalStateException(
					"Page \""
							+ requestedPageName
							+ "\" is annotated with @RolesAllowed but not @ProtectedPage.");
		}

		// If page is public (ie. not protected), then everyone is authorised to
		// it so allow access

		if (!protectedPage) {
			return AuthCheckResult.AUTHORIZED;
		}

		// If request is AJAX with no session, return an AJAX response that
		// forces reload of the page

		if (request.isXHR() && request.getSession(false) == null) {
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

		Link pageProtectedLink = renderLinkSource
				.createPageRenderLinkWithContext(PageDenied.class,
						requestedPageName);
		response.sendRedirect(pageProtectedLink);
		return AuthCheckResult.DENIED;

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
			if (isAutoLoginOn()) {
				autoLogin(1L);
				return true;
			}
		}

		return false;
	}

	private G_UserDataAccess service;

	/**
	 * Automatically logs you in as the given user. Intended for use in
	 * development environment only.
	 */
	private void autoLogin(Long userId) {
		// Lazy-load the business services locator because it is only needed for
		// auto-login
		try {
			G_User user = service.getUser("eli");

			// Visit visit = new Visit(user);
			logger.info(user.getUsername() + " has been auto-logged-in.");
			sessionStateManager.set(G_User.class, user);
		} catch (Exception e) {
			throw new IllegalStateException(e);
		}
	}

	private boolean isAuthorised(RolesAllowed rolesAllowed) throws IOException {
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

	/**
	 * Checks the value of system property jumpstart.auto-login. If "true" then
	 * returns true; if "false" then return false; if not set then returns
	 * false.
	 */
	private boolean isAutoLoginOn() {
		boolean autoLogin = false;
		if (autoLoginStr == null) {
			autoLogin = false;
		} else if (autoLoginStr.equalsIgnoreCase("true")) {
			autoLogin = true;
		} else if (autoLoginStr.equalsIgnoreCase("false")) {
			autoLogin = false;
		} else {
			throw new IllegalStateException(
					"System property jumpstart.auto-login has been set to \""
							+ autoLoginStr
							+ "\".  Please set it to \"true\" or \"false\".  If not specified at all then it will default to \"false\".");
		}
		return autoLogin;
	}

	private Link createLinkToRequestedPage(String requestedPageName,
			EventContext eventContext) {

		// Create a link to the page you wanted.

		Link linkToRequestedPage;

		if (eventContext instanceof EmptyEventContext) {
			linkToRequestedPage = renderLinkSource
					.createPageRenderLink(requestedPageName);
		} else {
			Object[] args = new String[eventContext.getCount()];
			for (int i = 0; i < eventContext.getCount(); i++) {
				args[i] = eventContext.get(String.class, i);
			}
			linkToRequestedPage = renderLinkSource
					.createPageRenderLinkWithContext(requestedPageName, args);
		}

		// Add any activation request parameters (AKA query parameters).

		List<String> parameterNames = request.getParameterNames();

		for (String parameterName : parameterNames) {
			linkToRequestedPage.removeParameter(parameterName);
			if (!parameterName.startsWith(COMPONENT_PARAM_PREFIX)) {
				linkToRequestedPage.addParameter(parameterName,
						request.getParameter(parameterName));
			}
		}

		return linkToRequestedPage;
	}

	private Link createLoginPageLinkWithMemory(Link requestedPageLink) {

		IIntermediatePage loginPage = (IIntermediatePage) componentSource
				.getPage(Login.class);
		loginPage.setNextPageLink(requestedPageLink);
		Link loginPageLink = renderLinkSource.createPageRenderLink(Login.class);

		return loginPageLink;
	}



	@Override
	public void handleComponentEvent(
			ComponentEventRequestParameters parameters,
			ComponentRequestHandler handler) throws IOException {

		AuthCheckResult result = checkAuthorityToPage(parameters
				.getActivePageName());

		if (result == AuthCheckResult.AUTHORIZED) {
			handler.handleComponentEvent(parameters);
		} else if (result == AuthCheckResult.DENIED) {
			// The method will have set the response to redirect to the
			// PageDenied page.
			return;
		} else if (result == AuthCheckResult.RELOAD_XHR) {

			// Return an AJAX response that reloads the page.

			Link requestedPageLink = createLinkToRequestedPage(
					parameters.getActivePageName(),
					parameters.getPageActivationContext());
			OutputStream os = response
					.getOutputStream("application/json;charset=UTF-8");
			os.write(("{\"redirectURL\":\"" + requestedPageLink.toAbsoluteURI() + "\"}")
					.getBytes());
			os.close();
			return;
		} else if (result == AuthCheckResult.AUTHENTICATE) {

			// Redirect to the Login page, with memory of the request.

			Link requestedPageLink = createLinkToRequestedPage(
					parameters.getActivePageName(),
					parameters.getPageActivationContext());
			Link loginPageLink = createLoginPageLinkWithMemory(requestedPageLink);

			response.sendRedirect(loginPageLink);
		} else {
			throw new IllegalStateException(result.toString());
		}

	}
	@Override
	public void handlePageRender(PageRenderRequestParameters parameters,
			ComponentRequestHandler handler) throws IOException {

		AuthCheckResult result = checkAuthorityToPage(parameters
				.getLogicalPageName());

		if (result == AuthCheckResult.AUTHORIZED) {
			handler.handlePageRender(parameters);
		} else if (result == AuthCheckResult.DENIED) {
			// The method will have set the response to redirect to the
			// PageDenied page.
			return;
		} else if (result == AuthCheckResult.AUTHENTICATE) {

			// Redirect to the Login page, with memory of the request.

			Link requestedPageLink = createLinkToRequestedPage(
					parameters.getLogicalPageName(),
					parameters.getActivationContext());
			Link loginPageLink = createLoginPageLinkWithMemory(requestedPageLink);

			response.sendRedirect(loginPageLink);
		} else {
			throw new IllegalStateException(result.toString());
		}

	}
//
//	/**
//	 * Note that the logicalPageName can/will have a core prefix appended to it.
//	 * So the strings we compare it to must also have the same prefix.
//	 * 
//	 * @param logicalPageName
//	 * @param requestedPageLink
//	 * @return true if a redirect was sent to the login page.
//	 * @throws IOException
//	 */
//	private boolean dispatchedToLoginPage(String logicalPageName,
//			Link requestedPageLink) throws IOException {
//		if (logicalPageName == null) {
//			Link link = renderLinkSource.createPageRenderLink(Login.class);
//			logger.error("Requested pagename was null. Sending redirect to Login");
//			response.sendRedirect(link);
//			return true;
//		}
//		if (authenticator.isLoggedIn()) {
//			// Logged user should not go back to Signin or Signup
//
//			if (signinPage.equalsIgnoreCase(logicalPageName)
//					|| signupPage.equalsIgnoreCase(logicalPageName)) {
//				Link link = renderLinkSource.createPageRenderLink(defaultPage);
//				logger.debug("Sending redirect to default page");
//				response.sendRedirect(link);
//				return true;
//			}
//			// otherwise let them go to whatever page it was they wanted.
//			return false;
//		}
//		// At this point, the user is not logged in.
//
//		// load the page the user wanted to go to.
//		Component page = componentSource.getPage(logicalPageName);
//
//		if (page.getClass().isAnnotationPresent(AnonymousAccess.class)) {
//			// if the page was available without being logged in, send to that
//			// (don't adjust redirect)
//			return false;
//		} else {
//			// The page they wanted required a login. Send them to the login
//			// page.
//			// Link link = renderLinkSource.createPageRenderLink(Login.class);
//
//			Link link = createLoginPageLinkWithMemory(requestedPageLink);
//			logger.debug("Sending redirect to page:" + logicalPageName);
//			response.sendRedirect(link);
//		}
//		return true;
//	}
	//
	// public void handleComponentEvent(
	// ComponentEventRequestParameters parameters,
	// ComponentRequestHandler handler) throws IOException {
	// Link requestedPageLink = createLinkToRequestedPage(
	// parameters.getActivePageName(),
	// parameters.getPageActivationContext());
	// if (dispatchedToLoginPage(parameters.getActivePageName(),
	// requestedPageLink)) {
	// return;
	// }
	//
	// handler.handleComponentEvent(parameters);
	//
	// }
	//
	// public void handlePageRender(PageRenderRequestParameters parameters,
	// ComponentRequestHandler handler) throws IOException {
	// Link requestedPageLink = createLinkToRequestedPage(
	// parameters.getLogicalPageName(),
	// parameters.getActivationContext());
	// if (dispatchedToLoginPage(parameters.getLogicalPageName(),
	// requestedPageLink)) {
	// return;
	// }
	//
	// handler.handlePageRender(parameters);
	// }

}
