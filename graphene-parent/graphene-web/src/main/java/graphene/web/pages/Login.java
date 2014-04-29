package graphene.web.pages;

import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.util.ExceptionUtil;
import graphene.web.annotations.AnonymousAccess;
import graphene.web.services.Authenticator;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Component;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Form;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

/**
 * 
 * @author djue
 * 
 */
@AnonymousAccess
public class Login {
	@Property
	private String flashmessage;
	@Inject
	@Path("context:/core/img/logo_graphene_dark_wide.png")
	@Property
	private Asset imgLogo;

	@Property
	private String username;

	@Property
	private String password;

	@Inject
	private Authenticator authenticator;

	@Component
	private Form loginForm;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;

	@Property
	@Inject
	@Symbol(SymbolConstants.APPLICATION_VERSION)
	private String appVersion;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.THEME_PATH)
	private String themePath;
	@Inject
	private Messages messages;

	@SessionState(create = false)
	private G_User user;
	@Inject
	private AlertManager manager;
	@Inject
	private Logger logger;

	@Log
	public Object onSubmitFromLoginForm() {
		try {
			//See if user accidentally supplied an email address, and try to strip out the username.
			if (username.contains("@")) {
				username = username.split("@")[0];
			}
			authenticator.login(username, password);
		} catch (AvroRemoteException ex) {
			String message = ExceptionUtil.getRootCauseMessage(ex);
			manager.alert(Duration.SINGLE, Severity.ERROR, "ERROR: " + message);
			logger.error(message);
			return null;
		}

		return Index.class;
	}

	public String getFlashMessage() {
		return flashmessage;
	}

	public void setFlashMessage(String flashmessage) {
		this.flashmessage = flashmessage;
	}

}
