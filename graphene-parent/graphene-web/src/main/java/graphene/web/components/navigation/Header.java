package graphene.web.components.navigation;

import graphene.model.idl.G_SymbolConstants;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

public class Header {
	@Inject
	@Path("context:/core/img/logo_graphene_dark_wide.png")
	@Property
	private Asset imgLogoWideDark;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_VERSION)
	private String appVersion;
}
