package graphene.web.pages.pub;

import java.io.IOException;

import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.util.ExceptionUtil;
import graphene.web.annotations.AnonymousAccess;
import graphene.web.model.BusinessException;
import graphene.web.pages.Index;
import graphene.web.security.AuthenticatorHelper;

import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.IncorrectCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.UnknownAccountException;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.web.util.SavedRequest;
import org.apache.shiro.web.util.WebUtils;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.internal.services.LoginContextService;
import org.tynamo.security.services.SecurityService;

/**
 * This page the user can create an account
 * 
 */
@AnonymousAccess
public class Register {
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;
	@Inject
	@Path("context:/core/img/logo_graphene_dark_wide.png")
	@Property
	private Asset imgLogo;
	@Property
	@Inject
	@Symbol(SymbolConstants.APPLICATION_VERSION)
	private String appVersion;
	//
	// @Inject
	// private Authenticator authenticator;
	@Inject
	private G_UserDataAccess dao;

	@Property
	@Validate("required,email")
	@Persist(PersistenceConstants.FLASH)
	private String email;

	@Property
	@Validate("required, minlength=3, maxlength=50")
	@Persist(PersistenceConstants.FLASH)
	private String firstName;
	@Property
	@Validate("required, minlength=3, maxlength=50")
	@Persist(PersistenceConstants.FLASH)
	private String lastName;
	@Inject
	private Logger logger;
	@Inject
	private Messages messages;

	@Property
	@Validate("password")
	private String password;

	@Component
	private Form registerForm;
	@Inject
	private AlertManager manager;
	@InjectPage
	private Login signin;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.THEME_PATH)
	private String themePath;

	@Property
	@Validate("username")
	@Persist(PersistenceConstants.FLASH)
	private String username;

	@Property
	@Validate("password")
	@Persist(PersistenceConstants.FLASH)
	private String verifyPassword;

	@OnEvent(value = EventConstants.VALIDATE, component = "RegisterForm")
	public void checkForm() {
		if (!verifyPassword.equals(password)) {
			registerForm.recordError(messages.get("error.verifypassword"));
		}
	}

	@Inject
	private Response response;
	@Inject
	private RequestGlobals requestGlobals;
	@Inject
	private AuthenticatorHelper authenticatorHelper;
	@Inject
	private SecurityService securityService;
	@Persist(PersistenceConstants.FLASH)
	private String loginMessage;
	@Inject
	private LoginContextService loginContextService;
	@Inject
	@Symbol(SecuritySymbols.REDIRECT_TO_SAVED_URL)
	private boolean redirectToSavedUrl;

	public Object loginNewRegisteredUser(String grapheneLogin,
			String graphenePassword, boolean grapheneRememberMe)
			throws IOException {

		Subject currentUser = securityService.getSubject();

		if (currentUser == null) {
			logger.error("Subject can`t be null");
			// throw new IllegalStateException("Subject can`t be null");
			loginMessage = messages.get("AuthenticationError");
			return null;
		}
		if (grapheneLogin.contains("@")) {
			grapheneLogin = grapheneLogin.split("@")[0];
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
		} catch (UnknownAccountException e) {
			loginMessage = messages.get("AccountDoesNotExists");
			return null;
		} catch (IncorrectCredentialsException e) {
			loginMessage = messages.get("WrongPassword");
			return null;
		} catch (LockedAccountException e) {
			loginMessage = messages.get("AccountLocked");
			return null;
		} catch (AuthenticationException e) {
			loginMessage = messages.get("AuthenticationError");
			return null;
		}
		try {
			authenticatorHelper.login(grapheneLogin, graphenePassword);
		} catch (BusinessException e) {
			loginMessage = messages.get("InternalAuthenticationError");
			e.printStackTrace();
			return null;
		}

		SavedRequest savedRequest = WebUtils
				.getAndClearSavedRequest(requestGlobals.getHTTPServletRequest());

		if (savedRequest != null
				&& savedRequest.getMethod().equalsIgnoreCase("GET")) {
			try {
				response.sendRedirect(savedRequest.getRequestUrl());
				return null;
			} catch (IOException e) {
				logger.warn("Can't redirect to saved request.");
				return loginContextService.getSuccessPage();
			}
		} else if (redirectToSavedUrl) {
			String requestUri = loginContextService.getSuccessPage();
			if (!requestUri.startsWith("/")) {
				requestUri = "/" + requestUri;
			}
			loginContextService.redirectToSavedRequest(requestUri);
			return null;
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
	}

	@OnEvent(value = EventConstants.SUCCESS, component = "RegisterForm")
	public Object proceedSignup() {
		try {
			if (dao.userExists(username)) {
				registerForm.recordError(messages.get("error.userexists"));
				return null;
			} else {
				String fullName = firstName + " " + lastName;
				G_User tempUser = new G_User();
				tempUser.setFullname(fullName);
				tempUser.setEmail(email);
				tempUser.setUsername(username);

				// Get the version that has been registered, because it will
				// have added business logic.
				if (dao.registerUser(tempUser) != null) {
					// tempUser = null;
					dao.setUserPassword(username, password);
					return loginNewRegisteredUser(username, password, true);
				}
				return Index.class;
			}
		} catch (Exception ex) {
			String message = ExceptionUtil.getRootCauseMessage(ex);
			manager.alert(Duration.SINGLE, Severity.ERROR, "ERROR: " + message);
			logger.error(message);
			ex.printStackTrace();

		}
		return null;
	}
}
