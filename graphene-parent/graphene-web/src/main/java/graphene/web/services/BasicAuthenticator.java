package graphene.web.services;

import graphene.model.idl.AuthenticationException;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Session;
import org.slf4j.Logger;

/**
 * Basic Security Realm implementation to be replaced with Shiro integration
 * from Tynamo.
 */
public class BasicAuthenticator implements Authenticator {

	public static final String AUTH_TOKEN = "authToken";

	@Inject
	public BasicAuthenticator(G_UserDataAccess s,
			ApplicationStateManager applicationStateManager, Logger logger) {
		this.service = s;
		this.applicationStateManager = applicationStateManager;
		this.logger = logger;
	}

	private Logger logger;
	private G_UserDataAccess service;

	// Use this to save the logged in user to the session.
	private ApplicationStateManager applicationStateManager;

	@Inject
	private Request request;

	public void login(String username, String password)
			throws AvroRemoteException {

		G_User user = service.loginUser(username, password);
		applicationStateManager.set(G_User.class, user);
		request.getSession(true).setAttribute(AUTH_TOKEN, user);

	}

	public boolean isLoggedIn() {
		return applicationStateManager.exists(G_User.class);
		// Session session = request.getSession(false);
		// if (session != null) {
		// return session.getAttribute(AUTH_TOKEN) != null;
		// }
		// return false;
	}

	public void logout() {
		// this removes the session state object.
		applicationStateManager.set(G_User.class, null);

		// this is the older way of doing it, setting the attribute as null
		Session session = request.getSession(false);
		if (session != null) {
			session.setAttribute(AUTH_TOKEN, null);
			session.invalidate();
		}
	}

	public G_User getLoggedUser() {
		G_User user = null;

		if (isLoggedIn()) {

			user = (G_User) request.getSession(true).getAttribute(AUTH_TOKEN);
			logger.info("User was found in the session");
		} else {
			throw new IllegalStateException("The user is not logged in! ");
		}
		return user;
	}

}
