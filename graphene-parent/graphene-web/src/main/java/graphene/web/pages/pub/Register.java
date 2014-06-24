package graphene.web.pages.pub;

import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.util.ExceptionUtil;
import graphene.web.annotations.AnonymousAccess;
import graphene.web.pages.Index;

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
import org.slf4j.Logger;

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
//	@Inject
//	private Authenticator authenticator;
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
//				if (dao.registerUser(tempUser) != null) {
//					// tempUser = null;
//					dao.setUserPassword(username, password);
//					try {
//						authenticator.login(username, password);
//					} catch (AuthenticationException ex) {
//						manager.alert(Duration.SINGLE, Severity.ERROR,
//								"ERROR: Authentication process has failed");
//						String message = "Authentication process has failed";// ExceptionUtil.getRootCauseMessage(ex);
//						manager.alert(Duration.SINGLE, Severity.ERROR,
//								"ERROR: " + message);
//						logger.error(ExceptionUtil.getRootCauseMessage(ex));
//						ex.printStackTrace();
//						registerForm
//								.recordError("Authentication process has failed");
//						return this;
//					}
//					return Index.class;
//				}
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
