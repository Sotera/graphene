package graphene.web.pages;

import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_VisualType;
import graphene.model.idl.G_Workspace;
import graphene.web.annotations.PluginPage;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

/**
 * Start page of application.
 */

@PluginPage(visualType = G_VisualType.TOP, menuName = "Dashboard", icon = "fa fa-lg fa-fw fa-home")
public class Index extends SimpleBasePage {

	@Property
	private G_Workspace currentWorkspace;

	@Inject
	private G_UserDataAccess userDataAccess;

}
