package graphene.web.security;

import graphene.model.idl.G_SymbolConstants;
import graphene.web.annotations.AnonymousAccess;
import graphene.web.pages.Index;
import graphene.web.pages.Login;
import graphene.web.pages.Register;
import graphene.web.services.Authenticator;

import java.io.IOException;

import javax.inject.Inject;

import org.apache.tapestry5.Link;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.runtime.Component;
import org.apache.tapestry5.services.ComponentEventRequestParameters;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.services.ComponentSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.PageRenderRequestParameters;
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

	private final PageRenderLinkSource renderLinkSource;

	private final ComponentSource componentSource;
	@Inject
	private Logger logger;
	private final Response response;

	private final Authenticator authenticator;

	@Symbol(G_SymbolConstants.GRAPHENE_WEB_CORE_PREFIX)
	private String corePrefix="";

	private String defaultPage;

	private String signinPage;

	private String signupPage;

	public AuthenticationFilter(PageRenderLinkSource renderLinkSource,
			ComponentSource componentSource, Response response,
			Authenticator authenticator) {
		this.renderLinkSource = renderLinkSource;
		this.componentSource = componentSource;
		this.response = response;
		this.authenticator = authenticator;
		defaultPage = corePrefix + "/" + Index.class.getSimpleName();
		signinPage = corePrefix + "/" + Login.class.getSimpleName();
		signupPage = corePrefix + "/" + Register.class.getSimpleName();
	}

	public void handleComponentEvent(
			ComponentEventRequestParameters parameters,
			ComponentRequestHandler handler) throws IOException {

		if (dispatchedToLoginPage(parameters.getActivePageName())) {
			return;
		}

		handler.handleComponentEvent(parameters);

	}

	public void handlePageRender(PageRenderRequestParameters parameters,
			ComponentRequestHandler handler) throws IOException {

		if (dispatchedToLoginPage(parameters.getLogicalPageName())) {
			return;
		}

		handler.handlePageRender(parameters);
	}

	/**
	 * Note that the logicalPageName can/will have a core prefix appended to it.
	 * So the strings we compare it to must also have the same prefix.
	 * 
	 * @param logicalPageName
	 * @return
	 * @throws IOException
	 */
	private boolean dispatchedToLoginPage(String logicalPageName)
			throws IOException {
		if (logicalPageName == null) {
			Link link = renderLinkSource.createPageRenderLink(Login.class);
			logger.error("Requested pagename was null. Sending redirect to Login");
			response.sendRedirect(link);
			return true;
		}
		if (authenticator.isLoggedIn()) {
			// Logged user should not go back to Signin or Signup

			if (signinPage.equalsIgnoreCase(logicalPageName)
					|| signupPage.equalsIgnoreCase(logicalPageName)) {
				Link link = renderLinkSource.createPageRenderLink(defaultPage);
				logger.debug("Sending redirect to default page");
				response.sendRedirect(link);
				return true;
			}
			// otherwise let them go to whatever page it was they wanted.
			return false;
		}
		// At this point, the user is not logged in.

		// load the page the user wanted to go to.
		Component page = componentSource.getPage(logicalPageName);

		if (page.getClass().isAnnotationPresent(AnonymousAccess.class)) {
			// if the page was available without being logged in, send to that
			// (don't adjust redirect)
			return false;
		} else {
			// The page they wanted required a login. Send them to the login
			// page.
			Link link = renderLinkSource.createPageRenderLink(Login.class);
			logger.debug("Sending redirect to page:" + logicalPageName);
			response.sendRedirect(link);
		}
		return true;
	}
}
