package graphene.web.security;

import graphene.business.commons.exception.BusinessException;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.util.validator.ValidationUtils;

import java.io.IOException;

import org.apache.avro.AvroRemoteException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.internal.services.LoginContextService;
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

	private SecurityService securityService;

	private G_UserDataAccess userDataAccess;

	@SessionState(create = false)
	private G_User user;

	private boolean userExists;

	@Inject
	@Symbol(SecuritySymbols.REDIRECT_TO_SAVED_URL)
	private boolean redirectToSavedUrl;

	@Inject
	public ShiroAuthenticatorHelper(G_UserDataAccess userDataAccess,
			ApplicationStateManager applicationStateManager, Logger logger,
			SecurityService securityService) {
		this.userDataAccess = userDataAccess;
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

	@Override
	public Object loginAndRedirect(String grapheneLogin,
			String graphenePassword, boolean grapheneRememberMe,
			RequestGlobals requestGlobals,
			LoginContextService loginContextService, Response response,
			Messages messages, AlertManager alertManager) {

		Subject currentUser = securityService.getSubject();

		if (currentUser == null) {
			logger.error("Subject can't be null");
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR,
					messages.get("AuthenticationError"));
			return null;
		}

		/**
		 * We store the password entered into this token. It will later be
		 * compared to the hashed version using whatever hashing routine is set
		 * in the Realm.
		 */
		UsernamePasswordToken token = new UsernamePasswordToken(grapheneLogin,
				graphenePassword);
		token.setRememberMe(grapheneRememberMe);

		try {
			currentUser.login(token);
			login(grapheneLogin, graphenePassword);
			SavedRequest savedRequest = WebUtils
					.getAndClearSavedRequest(requestGlobals
							.getHTTPServletRequest());

			if (savedRequest != null
					&& savedRequest.getMethod().equalsIgnoreCase("GET")) {
				if (ValidationUtils.isValid(savedRequest.getRequestUrl())) {
					response.sendRedirect(savedRequest.getRequestUrl());
					logger.debug("A redirect request was sent, returning null response for authentication helper.");
					// loginContextService.redirectToSavedRequest(savedRequest.getRequestUrl());
					return null;
				} else {
					logger.warn("Can't redirect to saved request.");
					return loginContextService.getSuccessPage();
				}
			} else if (redirectToSavedUrl) {
				String requestUri = loginContextService.getSuccessPage();
				if (!requestUri.startsWith("/")) {
					requestUri = "/" + requestUri;
				}
				// loginContextService.redirectToSavedRequest(requestUri);
				// this isn't working but should: return requestUri;
				return loginContextService.getSuccessPage();
			}
			// Cookie[] cookies =
			// requestGlobals.getHTTPServletRequest().getCookies();
			// if (cookies != null) for (Cookie cookie : cookies) if
			// (WebUtils.SAVED_REQUEST_KEY.equals(cookie.getName())) {
			// String requestUri = cookie.getValue();
			// WebUtils.issueRedirect(requestGlobals.getHTTPServletRequest(),
			// requestGlobals.getHTTPServletResponse(), requestUri);
			// return null;
			// }
			return loginContextService.getSuccessPage();
		} catch (UnknownAccountException e) {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR,
					messages.get("AccountDoesNotExist"));
		} catch (IncorrectCredentialsException e) {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR,
					messages.get("WrongPassword"));
		} catch (LockedAccountException e) {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR,
					messages.get("AccountLocked"));
		} catch (AvroRemoteException e) {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR,
					messages.get("InternalAuthenticationError"));
			e.printStackTrace();
		} catch (BusinessException e) {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR,
					messages.get("InternalAuthenticationError"));
			e.printStackTrace();
		} catch (IOException e) {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR,
					messages.get("InternalAuthenticationError"));
			e.printStackTrace();
		}
		return null;
	}

	public void login(String username, String password)
			throws AvroRemoteException, BusinessException {

		G_User user = userDataAccess.getByUsername(username);
		if (ValidationUtils.isValid(user)
				&& ValidationUtils.isValid(user.getId())) {
			user = userDataAccess.loginUser(user.getId(), password);

			applicationStateManager.set(G_User.class, user);
			// request.getSession(true).setAttribute(AUTH_TOKEN, user);
		} else {
			logger.error("Could not login user with username " + username);
			throw new BusinessException("Could not login user with username " + username);
		}
	}

	/**
	 * Login a user who is previously authenticated
	 * 
	 * @param username
	 */
	@Override
	public void loginAuthenticatedUser(String username) {
		G_User user;
		try {
			user = userDataAccess.getByUsername(username);
			if (user != null) {
				user = userDataAccess.loginAuthenticatedUser(user.getId());

				applicationStateManager.set(G_User.class, user);
				// request.getSession(true).setAttribute(AUTH_TOKEN, user);
			}
		} catch (AvroRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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
