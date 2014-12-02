package graphene.web.security;

import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.util.validator.ValidationUtils;
import graphene.web.model.BusinessException;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Session;
import org.slf4j.Logger;
import org.tynamo.security.internal.services.LoginContextService;

/**
 * Basic Security Realm implementation to be replaced with Shiro integration
 * from Tynamo.
 */
@Deprecated
public class BasicAuthenticatorHelper implements AuthenticatorHelper {

	public static final String AUTH_TOKEN = "authToken";

	@Inject
	public BasicAuthenticatorHelper(G_UserDataAccess userDataAccess,
			ApplicationStateManager applicationStateManager, Logger logger) {
		this.userDataAccess = userDataAccess;
		this.applicationStateManager = applicationStateManager;
		this.logger = logger;
	}

	private Logger logger;
	private G_UserDataAccess userDataAccess;

	// Use this to save the logged in user to the session.
	private ApplicationStateManager applicationStateManager;

	@Inject
	private Request request;

	public void login(String username, String password)
			throws AvroRemoteException, BusinessException {
		G_User user = userDataAccess.getByUsername(username);
		if (ValidationUtils.isValid(user)
				&& ValidationUtils.isValid(user.getId())) {
			user = userDataAccess.loginUser(user.getId(), password);
			applicationStateManager.set(G_User.class, user);
			request.getSession(true).setAttribute(AUTH_TOKEN, user);
		} else {
			logger.error("Could not login user with username " + username);
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


	@Override
	public void loginAuthenticatedUser(String username) {
		// TODO Auto-generated method stub
		return;
	}

	@Override
	public Object loginAndRedirect(String loginMessage, String grapheneLogin,
			String graphenePassword, boolean grapheneRememberMe,
			RequestGlobals requestGlobals,
			LoginContextService loginContextService) {
		// TODO Auto-generated method stub
		return null;
	}

}
