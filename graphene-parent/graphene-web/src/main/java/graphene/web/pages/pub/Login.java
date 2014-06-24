package graphene.web.pages.pub;

import graphene.model.idl.G_SymbolConstants;
import graphene.web.annotations.AnonymousAccess;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;

/**
 * 
 * @author djue
 * 
 */
@AnonymousAccess
public class Login {

	@Inject
	private AlertManager alertManager;
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;

	@Property
	@Inject
	@Symbol(SymbolConstants.APPLICATION_VERSION)
	private String appVersion;

	@Inject
	private ComponentResources componentResources;

	@Property
	private String flashmessage;

	@Inject
	@Path("context:/core/img/logo_graphene_dark_wide.png")
	@Property
	private Asset imgLogo;

	@Inject
	private Logger logger;

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private Response response;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.THEME_PATH)
	private String themePath;

	public String getFlashMessage() {
		return flashmessage;
	}

	public void setFlashMessage(String flashmessage) {
		this.flashmessage = flashmessage;
	}

}
