package graphene.web.pages.admin;

import graphene.dao.LoggingDAO;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_VisualType;
import graphene.model.query.EntityQuery;
import graphene.web.annotations.PluginPage;

import java.util.List;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BeanModelSource;
import org.got5.tapestry5.jquery.ImportJQueryUI;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

@Import(stylesheet = {
		"context:core/js/plugin/datatables/media/css/TableTools_JUI.css",
		"context:core/js/plugin/datatables/media/css/TableTools.css" })
@ImportJQueryUI(theme = "context:core/js/libs/jquery/jquery-ui-1.10.3.min.js")
@PluginPage(visualType = G_VisualType.ADMIN, menuName = "Query Audit", icon = "fa fa-lg fa-fw fa-info-circle")
public class QueryAudit {
	@Property
	private final DateTimeFormatter ISODate = ISODateTimeFormat.dateTime();
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

	// Generally useful bits and pieces
	public BeanModel<EntityQuery> getModel() {
		// TODO: Move the initialization to setupRender
		model = beanModelSource.createDisplayModel(EntityQuery.class,
				resources.getMessages());
		model.exclude("caseSensitive", "searchFreeText", "initiatorId",
				"attributevalues", "minimumscore", "minsecs", "maxsecs",
				"sortcolumn", "sortfield", "firstresult", "maxresult",
				"datasource", "userid", "sortascending", "id", "schema");

		model.get("AttributeList").sortable(true);
		return model;
	}

	public JSONObject getOptions() {

		final JSONObject json = new JSONObject(
				"bJQueryUI",
				"true",
				"bAutoWidth",
				"true",
				"sDom",
				"<\"col-sm-4\"f><\"col-sm-4\"i><\"col-sm-4\"l><\"row\"<\"col-sm-12\"p><\"col-sm-12\"r>><\"row\"<\"col-sm-12\"t>><\"row\"<\"col-sm-12\"ip>>");
		// Sort by score then by date.
		json.put("aaSorting",
				new JSONArray().put(new JSONArray().put(0).put("desc")));
		new JSONObject().put("aTargets", new JSONArray().put(0, 4));
		final JSONObject sortType = new JSONObject("sType", "formatted-num");
		final JSONArray columnArray = new JSONArray();
		columnArray.put(4, sortType);

		// json.put("aoColumns", columnArray);
		return json;
	}

	@SetupRender
	private void loadQueries() {
		list = dao.getQueries(null, null, 0, 200000);
	}
}
