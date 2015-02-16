package graphene.web.pages.admin;

import graphene.model.idl.G_VisualType;
import graphene.web.annotations.PluginPage;
import graphene.web.pages.SimpleBasePage;

import org.apache.shiro.authz.annotation.RequiresRoles;
import org.apache.tapestry5.annotations.Import;
import org.got5.tapestry5.jquery.ImportJQueryUI;

@Import(stylesheet = { "context:core/js/plugin/datatables/media/css/TableTools_JUI.css",
		"context:core/js/plugin/datatables/media/css/TableTools.css" })
@ImportJQueryUI(theme = "context:core/js/libs/jquery/jquery-ui-1.10.3.min.js")
@PluginPage(visualType = G_VisualType.ADMIN, menuName = "Query Audit", icon = "fa fa-lg fa-fw fa-info-circle")
@RequiresRoles("admin")
public class QueryAudit extends SimpleBasePage {

}
