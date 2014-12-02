package graphene.web.pages;

import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_VisualType;
import graphene.model.idl.G_Workspace;
import graphene.web.annotations.PluginPage;

import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

/**
 * Start page of application.
 */

@PluginPage(visualType = G_VisualType.TOP, menuName = "Dashboard", icon = "fa fa-lg fa-fw fa-code-home")
public class Index extends SimpleBasePage {

	@Property
	private G_Workspace currentWorkspace;

	@Property
	@Inject
	@Symbol(SymbolConstants.TAPESTRY_VERSION)
	private String tapestryVersion;

	@Inject
	private G_UserDataAccess userDataAccess;

	@Property
	private List<G_Workspace> workspaces;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_VERSION)
	private String appVersion;

	
}
