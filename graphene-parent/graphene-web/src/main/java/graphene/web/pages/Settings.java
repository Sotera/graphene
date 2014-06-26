package graphene.web.pages;

import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.web.pages.pub.Login;
import graphene.web.security.AuthenticatorHelper;

import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.tynamo.security.services.SecurityService;

/**
 * Allows the user to modify password and other settings
 * 
 * @author djue
 */
public class Settings {
	@Inject
	private G_UserDataAccess service;

	@Inject
	private Messages messages;

	@InjectPage
	private Login loginPage;

	@Property
	private String password;

	@Property
	private String verifyPassword;

	@Component
	private Form settingsForm;

	private String userId;

	@SessionState(create = false)
	private G_User user;

	private boolean userExists;


	@Inject
	private Logger logger;

	@Inject
	private SecurityService securityService;

	public Object onSuccess() {
		if (!verifyPassword.equals(password)) {
			settingsForm.recordError(messages.get("error.verifypassword"));
			return null;
		} else {
			settingsForm.clearErrors();
		}
		// TODO: Check to make sure this is the optimal ordering of operations
		try {
			service.setUserPassword(user.getUsername(), password);
			loginPage
					.setFlashMessage(messages.get("settings.password-changed"));
		} catch (Exception e) {
			// if the DAO didn't update successfully, tell them so.
			logger.error("Unable to update password for user "
					+ user.getUsername());
			loginPage.setFlashMessage(messages
					.get("settings.password-not-changed"));
		}

		// authenticator.logout();
		user = null;
		securityService.getSubject().logout();
		// Send the user to the login page.
		return loginPage;
	}
}
