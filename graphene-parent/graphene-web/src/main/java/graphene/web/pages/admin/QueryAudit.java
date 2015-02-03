package graphene.web.pages.admin;

import graphene.model.idl.G_ReportViewEvent;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_VisualType;
import graphene.model.query.EntityQuery;
import graphene.web.annotations.PluginPage;
import graphene.web.pages.SimpleBasePage;

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

@Import(stylesheet = { "context:core/js/plugin/datatables/media/css/TableTools_JUI.css",
		"context:core/js/plugin/datatables/media/css/TableTools.css" })
@ImportJQueryUI(theme = "context:core/js/libs/jquery/jquery-ui-1.10.3.min.js")
@PluginPage(visualType = G_VisualType.ADMIN, menuName = "Query Audit", icon = "fa fa-lg fa-fw fa-info-circle")
public class QueryAudit extends SimpleBasePage {
	@Property
	private final DateTimeFormatter ISODate = ISODateTimeFormat.dateTime();
	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private ComponentResources componentResources;

	@Property
	private EntityQuery currentQuery;

	@Property
	private String currentFilter;
	@Property
	private List<String> currentFilters;
	@Property
	private G_SearchTuple<String> currentTuple;
	@Property
	private G_ReportViewEvent currentReportView;

	@Property
	private List<EntityQuery> searchQueryList;
	private BeanModel<EntityQuery> entityQuerymodel;
	private BeanModel<G_ReportViewEvent> reportViewmodel;
	@Inject
	private ComponentResources resources;
	@Property
	private List<G_ReportViewEvent> reportViewList;

	// Generally useful bits and pieces
	public BeanModel<EntityQuery> getEntityQueryModel() {
		// TODO: Move the initialization to setupRender
		entityQuerymodel = beanModelSource.createDisplayModel(EntityQuery.class, resources.getMessages());
		entityQuerymodel.exclude("caseSensitive", "searchFreeText", "initiatorId", "attributevalues", "minimumscore",
				"minsecs", "maxsecs", "sortcolumn", "sortfield", "firstresult", "maxresult", "datasource", "userid",
				"sortascending", "id", "schema");

		entityQuerymodel.get("AttributeList").sortable(true);
		return entityQuerymodel;
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
		json.put("aaSorting", new JSONArray().put(new JSONArray().put(0).put("desc")));
		new JSONObject().put("aTargets", new JSONArray().put(0, 4));
		final JSONObject sortType = new JSONObject("sType", "formatted-num");
		final JSONArray columnArray = new JSONArray();
		columnArray.put(4, sortType);

		// json.put("aoColumns", columnArray);
		json.put("oLanguage", new JSONObject("sSearch", "Filter:"));
		return json;
	}

	public BeanModel<G_ReportViewEvent> getReportViewModel() {

		reportViewmodel = beanModelSource.createDisplayModel(G_ReportViewEvent.class, resources.getMessages());
		reportViewmodel.exclude("schema", "userid", "id");

		reportViewmodel.reorder("timeinitiated", "username", "reportid", "reporttype", "reportpagelink");

		return reportViewmodel;
	}

	@SetupRender
	private void loadQueries() {
		searchQueryList = loggingDao.getQueries(null, null, 0, 200000);
		reportViewList = loggingDao.getReportViewEvents(null, 200000);
	}
}
