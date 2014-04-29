package graphene.web.pages;

import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_VisualType;
import graphene.web.annotations.PluginPage;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

@PluginPage(visualType = G_VisualType.META)
public class Contact {
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_CONTACT)
	private String contact;
}
