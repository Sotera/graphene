package graphene.web.components;

import graphene.model.idl.G_SymbolConstants;
import graphene.web.security.AuthenticatorHelper;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.got5.tapestry5.jquery.ImportJQueryUI;

import com.trsvax.bootstrap.annotations.Exclude;

/**
 * A layout for pages like login, logout, register, etc.  Where The user has not logged in yet.
 * 
 * @author djue
 * 
 */
@Exclude(stylesheet = { "core" })
@Import(stylesheet = { "context:/core/css/bootstrap.min.css",
		"context:/core/css/font-awesome.min.css",
		"context:/core/css/graphene-production.css",
		"context:/core/css/graphene-skins.css", "context:/core/css/demo.css",
		"context:/core/css/googlefonts.css" },

library = { "context:/core/js/libs/jquery-2.0.2.js",
		"context:/core/js/libs/jquery-ui-1.10.3.min.js",
		"context:/core/js/bootstrap/bootstrap.min.js",

		"context:/core/js/notification/GNotification.min.js",
		"context:/core/js/gwidgets/g.widget.min.js",

		"context:/core/js/plugin/easy-pie-chart/jquery.easy-pie-chart.min.js",
		"context:/core/js/plugin/sparkline/jquery.sparkline.min.js",
		"context:/core/js/plugin/jquery-validate/jquery.validate.min.js",
		"context:/core/js/plugin/masked-input/jquery.maskedinput.min.js",
		"context:/core/js/plugin/select2/select2.min.js",
		"context:/core/js/plugin/bootstrap-slider/bootstrap-slider.min.js",
		"context:/core/js/plugin/msie-fix/jquery.mb.browser.min.js",
		"context:/core/js/demo.js", "context:/core/js/app.js" })
@ImportJQueryUI(theme = "context:/core/js/libs/jquery-ui-1.10.3.min.js")
public class UnauthenticatedLayout {
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;
	
	@Property
	@Inject
	@Symbol(SymbolConstants.APPLICATION_VERSION)
	private String appVersion;
	
	
//	@Inject
//	private IAuthenticator authenticator;

//	@Inject
//	private ComponentClassResolver componentClassResolver;

	@Inject
	private Messages messages;
//
//	@Property
//	private Tuple<String, String> page;

//	@Property
//	private String pageName;

//	@Inject
//	private ComponentResources resources;

	@Property
	private String themePath = "core/";

//	public String getClassForPageName() {
//		return resources.getPageName().equalsIgnoreCase(page.first) ? "active"
//				: null;
//	}

//	public String getIconForPageName() {
//		return page.second;
//	}



	/**
	 * Workaround for HTML5 support, which doesn't have a DTD.
	 * 
	 * @return
	 */
//	@SetupRender
//	final void renderDocType(final MarkupWriter writer) {
//		writer.getDocument().raw("<!DOCTYPE html>");
//	}
}
