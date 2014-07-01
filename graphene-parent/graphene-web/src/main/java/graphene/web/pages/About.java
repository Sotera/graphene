package graphene.web.pages;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_VisualType;
import graphene.web.annotations.PluginPage;

@PluginPage(visualType = G_VisualType.META, menuName = "About Graphene", icon = "fa fa-lg fa-fw fa-question")
public class About {
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;

	@Property
	@Inject
	@Symbol(SymbolConstants.APPLICATION_VERSION)
	private String appVersion;
}
