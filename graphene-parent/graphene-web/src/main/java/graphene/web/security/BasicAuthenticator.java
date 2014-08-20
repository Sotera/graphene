package graphene.web.security;

import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.web.model.BusinessException;

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
@Deprecated
public class BasicAuthenticator implements AuthenticatorHelper {

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
			throws AvroRemoteException, BusinessException {
		G_User user = service.getByUsername(username);
		if (user != null) {
			user = service.loginUser(user.getId(), password);
			applicationStateManager.set(G_User.class, user);
			request.getSession(true).setAttribute(AUTH_TOKEN, user);
		}
	}

	public boolean isUserObjectCreated() {
		return applicationStateManager.exists(G_User.class);

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

}
