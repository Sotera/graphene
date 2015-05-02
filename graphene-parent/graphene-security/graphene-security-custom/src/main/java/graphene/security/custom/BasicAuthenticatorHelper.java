package graphene.security.custom;

import graphene.business.commons.exception.BusinessException;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idlhelper.AuthenticatorHelper;
import graphene.util.validator.ValidationUtils;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
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

	private final Logger logger;

	private final G_UserDataAccess userDataAccess;
	// Use this to save the logged in user to the session.
	private final ApplicationStateManager applicationStateManager;

	@Inject
	private Request request;

	@Inject
	public BasicAuthenticatorHelper(final G_UserDataAccess userDataAccess,
			final ApplicationStateManager applicationStateManager, final Logger logger) {
		this.userDataAccess = userDataAccess;
		this.applicationStateManager = applicationStateManager;
		this.logger = logger;
	}

	@Override
	public boolean isUserObjectCreated() {
		return applicationStateManager.exists(G_User.class);

	}

	@Override
	public void login(final String username, final String password) throws AvroRemoteException, BusinessException {
		G_User user = userDataAccess.getByUsername(username);
		if (ValidationUtils.isValid(user) && ValidationUtils.isValid(user.getId())) {
			user = userDataAccess.loginUser(user.getId(), password);
			applicationStateManager.set(G_User.class, user);
			request.getSession(true).setAttribute(AUTH_TOKEN, user);
		} else {
			logger.error("Could not login user with username " + username);
		}
	}

	@Override
	public Object loginAndRedirect(final String grapheneLogin, final String graphenePassword,
			final boolean grapheneRememberMe, final RequestGlobals requestGlobals,
			final LoginContextService loginContextService, final Response response, final Messages messages,
			final AlertManager alertManager) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean loginAuthenticatedUser(final String username) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void logout() {
		// this removes the session state object.
		applicationStateManager.set(G_User.class, null);

		// this is the older way of doing it, setting the attribute as null
		final Session session = request.getSession(false);
		if (session != null) {
			session.setAttribute(AUTH_TOKEN, null);
			session.invalidate();
		}
	}

}
