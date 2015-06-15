#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.pages;

import graphene.model.idl.G_VisualType;
import graphene.model.idl.G_Workspace;
import graphene.web.annotations.PluginPage;
import graphene.web.pages.SimpleBasePage;

import org.apache.tapestry5.annotations.Property;

/**
 * Start page of application ${groupId}-web.
 */
@PluginPage(visualType = G_VisualType.TOP, menuName = "Home", icon = "fa fa-lg fa-fw fa-home")
public class Index extends SimpleBasePage {

	@Property
	private G_Workspace currentWorkspace;

}
