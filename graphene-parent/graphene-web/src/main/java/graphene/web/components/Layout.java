package graphene.web.components;

import graphene.model.idl.G_SymbolConstants;
import graphene.web.pages.pub.Login;
import graphene.web.security.AuthenticatorHelper;

import java.util.Locale;

import org.apache.shiro.authz.annotation.RequiresAuthentication;
import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.Link;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.func.Tuple;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.ComponentClassResolver;
import org.got5.tapestry5.jquery.ImportJQueryUI;
import org.slf4j.Logger;

import com.trsvax.bootstrap.annotations.Exclude;

/**
 * 
 * @author djue
 * 
 */
@Exclude(stylesheet = { "core" })
@Import(stylesheet = { "context:/core/css/bootstrap.min.css",
		"context:/core/css/font-awesome.min.css",
		"context:/core/css/graphene-production.css",
		"context:/core/css/graphene-skins.css", "context:/core/css/demo.css",
		"context:/core/css/googlefonts.css" })
@ImportJQueryUI(theme = "context:/core/js/libs/jquery-ui-1.10.3.min.js")
//@RequiresAuthentication
public class Layout {
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;

	@Property
	@Inject
	@Symbol(SymbolConstants.APPLICATION_VERSION)
	private String appVersion;

	@Inject
	private AssetSource assetSource;

	@Inject
	private ComponentClassResolver componentClassResolver;
	@Inject
	@Path("context:/core/js/app.js")
	@Property
	private Asset jsApp;
	@Inject
	@Path("context:/core/js/demo.js")
	@Property
	private Asset jsDemo;
	@Inject
	@Path("context:/core/js/libs/jquery-2.0.2.min.js")
	@Property
	private Asset jsLib1;
	@Inject
	@Path("context:/core/js/plugin/select2/select2.min.js")
	@Property
	private Asset jsLib10;
	@Inject
	@Path("context:/core/js/plugin/bootstrap-slider/bootstrap-slider.min.js")
	@Property
	private Asset jsLib11;
	@Inject
	@Path("context:/core/js/plugin/msie-fix/jquery.mb.browser.min.js")
	@Property
	private Asset jsLib12;
	@Inject
	@Path("context:/core/js/libs/jquery-ui-1.10.3.min.js")
	@Property
	private Asset jsLib2;
	@Inject
	@Path("context:/core/js/bootstrap/bootstrap.min.js")
	@Property
	private Asset jsLib3;
	//
	@Inject
	@Path("context:/core/js/notification/GNotification.min.js")
	@Property
	private Asset jsLib4;
	@Inject
	@Path("context:/core/js/gwidgets/g.widget.min.js")
	@Property
	private Asset jsLib5;
	//
	@Inject
	@Path("context:/core/js/plugin/easy-pie-chart/jquery.easy-pie-chart.min.js")
	@Property
	private Asset jsLib6;
	@Inject
	@Path("context:/core/js/plugin/sparkline/jquery.sparkline.min.js")
	@Property
	private Asset jsLib7;

	@Inject
	@Path("context:/core/js/plugin/jquery-validate/jquery.validate.min.js")
	@Property
	private Asset jsLib8;

	@Inject
	@Path("context:/core/js/plugin/masked-input/jquery.maskedinput.min.js")
	@Property
	private Asset jsLib9;

	@Inject
	@Path("context:/core/js/plugin/pace/pace.min.js")
	@Property
	private Asset jsLibPace;

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

	@Property
	@Inject
	@Symbol(G_SymbolConstants.THEME_PATH)
	private String themePath;

	/**
	 * 
	 * @return a link to be put inside a javascript component, which lets the
	 *         user log out. This link will trigger the onLogout() event.
	 */
	public String getLogoutEventLink() {
		Link l = resources.createEventLink("logout");
		return l.toString();
	}

	@Inject
	private AuthenticatorHelper authenticator;

	@Log
	public Object onLogout() {
		authenticator.logout();
		return Login.class;
	}

	/**
	 * Workaround for HTML5 support, which doesn't have a DTD.
	 * 
	 * @return
	 */
	// @SetupRender
	// final void renderDocType(final MarkupWriter writer) {
	// writer.getDocument().raw("<!DOCTYPE html>");
	// }

	// public void setUser(G_User s) {
	// user = s;
	// }
}
