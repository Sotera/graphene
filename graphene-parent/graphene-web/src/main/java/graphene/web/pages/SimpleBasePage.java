package graphene.web.pages;

import graphene.model.idl.G_User;
import graphene.web.security.AuthenticatorHelper;

import org.apache.shiro.SecurityUtils;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;
import org.tynamo.security.services.SecurityService;

public class SimpleBasePage {
	@SessionState(create = false)
	private G_User user;

	private boolean userExists;

//	@Inject
//	private IAuthenticator authenticator;
//	
	/**
	 * @return the authenticator
	 */
//	public final Authenticator getAuthenticator() {
//		return authenticator;
//	}
	@Inject
	private SecurityService securityService;
	@Inject
	private Logger logger;
	@Inject
	private Messages messages;

	protected Messages getMessages() {
		return messages;
	}
//	void onActivate() {
//		if(securityService.getSubject().isAuthenticated()  && !authenticator.isLoggedIn()){
//			authenticator.login(securityService.getSubject().getPrincipal());
//		}
//		if (securityService.getSubject().isAuthenticated() && !applicationStateManager.exists(CurrentUser.class)) {
//			CurrentUser currentUser = applicationStateManager.get(CurrentUser.class);
//			currentUser.merge(securityService.getSubject().getPrincipal());
//		}
//	}
	/**
	 * @return the userExists
	 */
	public final boolean isUserExists() {
		return userExists;
	}

//	@Log
//	public Object onLogout() {
//		if (userExists) {
//			logger.debug("Logging out for user " + user.toString());
//		} else {
//			logger.warn("Calling logout, but no user was logged in");
//		}
//		authenticator.logout();
//		return Index.class;
//	}
	
	@Inject
	private Request request;
	
//	
//	Object onActionFromLogout() {
//		// Need to call this explicitly to invoke onlogout handlers (for remember me etc.)
//		SecurityUtils.getSubject().logout();
//		try {
//			// the session is already invalidated, but need to cause an exception since tapestry doesn't know about it
//			// and you'll get a container exception message instead without this. Unfortunately, there's no way of
//			// configuring Shiro to not invalidate sessions right now. See DefaultSecurityManager.logout()
//			// There's a similar issues in Tapestry - Howard has fixed, but no in T5.2.x releases yet
//			request.getSession(false).invalidate();
//		} catch (Exception e) {
//		}
//
//		return this;
//	}
	/**
	 * @return the user
	 */
	public final G_User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public final void setUser(G_User user) {
		this.user = user;
	}

}
