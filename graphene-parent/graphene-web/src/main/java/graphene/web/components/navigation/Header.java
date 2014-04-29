package graphene.web.components.navigation;

import java.util.Date;

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
	@Inject
	@Path("context:/core/img/flags/us.png")
	@Property
	private Asset imgFlagUS;

	@Property
	private int numberTasks = 5;

	@Property
	private int numberNotifications = 2;
	@Property
	private int numberMessages = 3;
	
	public Date getCurrentTime() {
		return new Date();
	}
	
}
