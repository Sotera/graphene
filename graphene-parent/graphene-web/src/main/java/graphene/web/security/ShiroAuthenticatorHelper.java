package graphene.web.security;

import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.web.model.BusinessException;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;
import org.tynamo.security.services.SecurityService;

/**
 * Intended to encapsulate the creation/destruction logic for the user sso.
 * 
 * @author djue
 * 
 */

public class ShiroAuthenticatorHelper implements AuthenticatorHelper {

	public static final String AUTH_TOKEN = "authToken";

	// Use this to save the logged in user to the session.
	private ApplicationStateManager applicationStateManager;

	private Logger logger;
	@Inject
	private Request request;
	private SecurityService securityService;

	private G_UserDataAccess service;

	@SessionState(create = false)
	private G_User user;

	private boolean userExists;

	@Inject
	public ShiroAuthenticatorHelper(G_UserDataAccess s,
			ApplicationStateManager applicationStateManager, Logger logger,
			SecurityService securityService) {
		this.service = s;
		this.securityService = securityService;
		this.applicationStateManager = applicationStateManager;
		this.logger = logger;
	}

	public boolean isUserObjectCreated() {
		boolean userSsoExists = applicationStateManager.exists(G_User.class);
		// Session session = request.getSession(false);
		// if (session != null) {
		// return session.getAttribute(AUTH_TOKEN) != null;
		// }
		// return false;
		logger.debug(userSsoExists ? "User SSO exists."
				: "User SSO does not exist.");
		return userSsoExists;
	}

	public void login(String username, String password)
			throws AvroRemoteException, BusinessException {

		G_User user = service.loginUser(username, password);
		applicationStateManager.set(G_User.class, user);
		// request.getSession(true).setAttribute(AUTH_TOKEN, user);

	}

	public void logout() {

		logger.debug(securityService.isAuthenticated() ? "During Logout: User is authenticated"
				: "During Logout: User is not authenticated");
		logger.debug(userExists ? "During Logout: User SSO exists"
				: "During Logout: User SSO does not exist");

		// this removes the session state object.
		applicationStateManager.set(G_User.class, null);

		securityService.getSubject().logout();
	}

}
