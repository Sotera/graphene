package graphene.web.pages.admin;

import graphene.model.idl.G_VisualType;
import graphene.web.annotations.PluginPage;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

@PluginPage(visualType = G_VisualType.HIDDEN, menuName = "System Status", icon = "fa fa-lg fa-fw fa-question")
public class Status {

	@Inject
	@Path("context:core/js/plugin/flot/jquery.flot.cust.js")
	@Property
	private Asset flot_cust;

	@Inject
	@Path("context:core/js/plugin/flot/jquery.flot.resize.js")
	@Property
	private Asset flot_resize;

	@Inject
	@Path("context:core/js/plugin/flot/jquery.flot.fillbetween.js")
	@Property
	private Asset flot_fillbetween;

	@Inject
	@Path("context:core/js/plugin/flot/jquery.flot.orderBar.js")
	@Property
	private Asset flot_orderBar;

	@Inject
	@Path("context:core/js/plugin/flot/jquery.flot.pie.js")
	@Property
	private Asset flot_pie;

	@Inject
	@Path("context:core/js/plugin/flot/jquery.flot.tooltip.js")
	@Property
	private Asset flot_tooltip;

}
