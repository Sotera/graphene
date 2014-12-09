package graphene.web.pages.admin;

import graphene.dao.LoggingDAO;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_VisualType;
import graphene.model.query.EntityQuery;
import graphene.web.annotations.PluginPage;

import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.got5.tapestry5.jquery.ImportJQueryUI;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

//@Import(library = {
//		"context:core/js/plugin/datatables/jquery.dataTables-cust.min.js",
//		"context:core/js/plugin/datatables/ColReorder.min.js",
//		"context:core/js/plugin/datatables/FixedColumns.min.js",
//		"context:core/js/plugin/datatables/ColVis.min.js",
//		"context:core/js/plugin/datatables/ZeroClipboard.js",
//		"context:core/js/plugin/datatables/media/js/TableTools.min.js",
//		"context:core/js/plugin/datatables/DT_bootstrap.js" })
@Import(stylesheet = {
		"context:core/js/plugin/datatables/media/css/TableTools_JUI.css",
		"context:core/js/plugin/datatables/media/css/TableTools.css" })
@ImportJQueryUI(theme = "context:core/js/libs/jquery/jquery-ui-1.10.3.min.js")
@PluginPage(visualType = G_VisualType.ADMIN, menuName = "Query Audit", icon = "fa fa-lg fa-fw fa-info-circle")
public class QueryAudit {
	@Property
	private DateTimeFormatter ISODate = ISODateTimeFormat.dateTime();
	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private ComponentResources componentResources;

	@Property
	private EntityQuery currentQuery;
	@Inject
	private LoggingDAO dao;

	@Property
	private G_SearchTuple<String> currentTuple;

	@Property
	private List<EntityQuery> list;
	private BeanModel<EntityQuery> model;
	@Inject
	private ComponentResources resources;

	@Inject
	private Request request;

	public boolean isAjax() {
		return request.isXHR();
	}

	// Generally useful bits and pieces
	public BeanModel<EntityQuery> getModel() {
		// TODO: Move the initialization to setupRender
		this.model = beanModelSource.createDisplayModel(EntityQuery.class,
				resources.getMessages());
		return model;
	}

	/**
	 * l - Length changing
	 * 
	 * f - Filtering input
	 * 
	 * t - The table!
	 * 
	 * i - Information
	 * 
	 * p - Pagination
	 * 
	 * r - pRocessing
	 * 
	 * < and > - div elements
	 * 
	 * <"class" and > - div with a class
	 * 
	 * Examples: <"wrapper"flipt>, <lf<t>ip>
	 * 
	 * @return
	 */
	public JSONObject getOptions() {
		// String sDom = "<\"widget-body-toolbar\"lfip><t>";
		// String sDom =
		// "<'dt-top-row'><'dt-wrapper't><'dt-row dt-bottom-row'<'row'<'col-sm-6'i><'col-sm-6 text-right'p>>";
		// String sDom =
		// "<\"widget-body-toolbar\"i>rt<\"bottom\"flp><\"clear\">";
		String sDom = "TC<\"clear\">Rlfrtip";
		String oLanguage = "'sSearch' : 'Search all columns:'";
		JSONObject json = new JSONObject("bJQueryUI", "true", "sDom", sDom,
				"oLanguage", oLanguage, "bSortCellsTop", "true");

		return json;
	}

	@SetupRender
	private void loadQueries() {
		list = dao.getQueries(null, null, 200);
	}

	@Inject
	private JavaScriptSupport jss;

	@AfterRender
	void addScript() {
		//
		// String clientId = "#queryList";
		// String tableId = "queryList";
		// jss.addScript("$(\"%s\").keyup(function() {" + tableId
		// + ".fnFilter(this.value, " + tableId
		// + ".oApi._fnVisibleToColumnIndex(" + tableId
		// + ".fnSettings(), $(\"thead input\").index(this)));});",
		// clientId);
		// jss.addScript(
		// "$(\"%s\").each(function(i) {this.initVal = this.value;});",
		// clientId);
		// jss.addScript(
		// "$(\"%s\").focus(function() {if (this.className == \"search_init\") {this.className = \"\";this.value = \"\";}});",
		// clientId);
		//
		// jss.addScript(
		// "$(\"%s\").blur(function(i) {if (this.value == \"\") {	this.className =\"search_init\";this.value = this.initVal;}});	",
		// clientId);
		//
		//
	}
}
