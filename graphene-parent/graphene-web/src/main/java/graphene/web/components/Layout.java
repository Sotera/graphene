package graphene.web.components;

import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_Workspace;
import graphene.web.pages.pub.Login;
import graphene.web.security.AuthenticatorHelper;

import java.util.List;
import java.util.Locale;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.func.Tuple;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.got5.tapestry5.jquery.ImportJQueryUI;
import org.slf4j.Logger;
import org.tynamo.security.services.SecurityService;

/**
 * 
 * @author djue
 * 
 */
// @Exclude(stylesheet = { "core" })
@Import(stylesheet = { "context:/core/css/t5default.css", "context:/core/css/bootstrap.min.css",
		"context:/core/css/font-awesome.min.css", "context:/core/css/graphene-production.css",
		"context:/core/css/pace-radar.css", "context:/core/css/graphene-skins.css", "context:/core/css/demo.css",
		"context:/core/css/googlefonts.css" }, library = { "context:/core/js/logout.js" })
@ImportJQueryUI(theme = "context:/core/js/libs/jquery/jquery-ui-1.10.3.min.js")
public class Layout {
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_VERSION)
	private String appVersion;
	@Inject
	private AssetSource assetSource;

	@Inject
	private AuthenticatorHelper authenticatorHelper;

	@Inject
	private ComponentClassResolver componentClassResolver;

	@Environmental
	protected JavaScriptSupport jss;

	@Inject
	private Locale locale;

	@Inject
	private Logger logger;
	@Inject
	private Messages messages;

	@Property
	private Tuple<String, String> page;

	@Inject
	private ComponentResources resources;

	@Inject
	@Property
	private SecurityService securityService;

	@SessionState(create = false)
	private G_User user;

	@Inject
	private G_UserDataAccess userDataAccess;
	private boolean userExists;

	@Property
	@SessionState(create = false)
	private List<G_Workspace> workspaces;

	void afterRender() {
		jss.addInitializerCall("makeLogout", "logout");
	}

	public JSONObject getJgrowlParams() {
		final JSONObject json = new JSONObject("position", "bottom-right");
		return json;

	}

	/**
	 * 
	 * @return a link to be put inside a javascript component, which lets the
	 *         user log out. This link will trigger the onLogout() event.
	 */
	public String getLogoutEventLink() {
		final Link l = resources.createEventLink("logout");
		return l.toString();
	}

	@SetupRender
	public void loginIfNeeded() {
		if (!authenticatorHelper.isUserObjectCreated() && securityService.isAuthenticated()) {
			authenticatorHelper.loginAuthenticatedUser((String) securityService.getSubject().getPrincipal());
		}
		if (userExists) {
			try {
				workspaces = userDataAccess.getWorkspacesForUser(user.getId());
			} catch (final AvroRemoteException e) {
				logger.error(e.getMessage());
			}
		}
	}

	@Log
	public Object onLogout() {
		authenticatorHelper.logout();
		return Login.class;
	}
}
