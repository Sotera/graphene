package graphene.web.pages;

import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_VisualType;
import graphene.web.annotations.PluginPage;
import graphene.web.pages.pub.Login;

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
@PluginPage(visualType = G_VisualType.SETTINGS, menuName = "Change Password", icon = "fa fa-lg fa-fw fa-list-alt")
public class Settings {
	@Inject
	private G_UserDataAccess userDataAccess;

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

	@SessionState(create = false)
	private G_User user;
	private boolean userExists;
	@Inject
	private Logger logger;

	@Inject
	private SecurityService securityService;

	public Object onSuccess() {
		if (userExists) {
			if (!verifyPassword.equals(password)) {
				settingsForm.recordError(messages.get("error.verifypassword"));
				return null;
			} else {
				settingsForm.clearErrors();
			}
			boolean success = false;
			try {
				success = userDataAccess.setUserPassword(user.getId(), password);

			} catch (final Exception e) {
				// if the DAO didn't update successfully, tell them so.
				logger.error("Unable to update password for user " + user.getUsername() + " Error: " + e.getMessage());
			}
			if (success) {
				loginPage.setFlashMessage(messages.get("settings.password-changed"));
			} else {
				loginPage.setFlashMessage(messages.get("settings.password-not-changed"));
			}

			// authenticator.logout();
			user = null;
			securityService.getSubject().logout();
		} else {
			logger.error("A user tried to change a password without being logged in.");
			loginPage.setFlashMessage(messages.get("settings.password-not-changed"));
		}
		// Send the user to the login page.
		return loginPage;
	}
}
