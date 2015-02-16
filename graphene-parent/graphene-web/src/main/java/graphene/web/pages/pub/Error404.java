package graphene.web.pages.pub;

import graphene.model.idl.G_SymbolConstants;

import javax.servlet.http.HttpServletResponse;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.Response;

public class Error404 {
	@Property
	@Inject
	private Request request;

	@Property
	@Inject
	@Symbol(SymbolConstants.PRODUCTION_MODE)
	private boolean productionMode;
	@Inject
	@Path("context:/core/img/logo_graphene_dark_wide.png")
	@Property
	private Asset imgLogo;
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_VERSION)
	private String appVersion;
	@Property
	private String urlDenied;

	// Other useful bits and pieces

	@Inject
	private Response response;

	// The code

	void onActivate(final String urlDenied) {
		this.urlDenied = urlDenied;
	}

	String onPassivate() {
		return urlDenied;
	}

	public void setupRender() {
		response.setStatus(HttpServletResponse.SC_NOT_FOUND);
	}

}
