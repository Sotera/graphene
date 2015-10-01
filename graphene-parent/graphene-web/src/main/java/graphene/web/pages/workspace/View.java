package graphene.web.pages.workspace;

import graphene.dao.DataSourceListDAO;
import graphene.dao.UserWorkspaceDAO;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_ReportViewEvent;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_UserSpaceRelationshipType;
import graphene.model.idl.G_VisualType;
import graphene.model.idl.G_Workspace;
import graphene.model.idl.UnauthorizedActionException;
import graphene.model.idlhelper.RangeHelper;
//import graphene.model.query.EntityQuery;
import graphene.util.ExceptionUtil;
import graphene.util.validator.ValidationUtils;
import graphene.web.annotations.PluginPage;
import graphene.web.pages.CombinedEntitySearchPage;
import graphene.web.pages.SimpleBasePage;

import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.PageRenderLinkSource;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

@PluginPage(visualType = G_VisualType.VIEW_WORKSPACE, menuName = "View Workspace", icon = "fa fa-lg fa-fw fa-list-alt")
public class View extends SimpleBasePage {
	@Property
	@SessionState(create = false)
	private G_Workspace currentSelectedWorkspace;

	@Property
	private boolean currentSelectedWorkspaceExists;

	@Inject
	private G_UserDataAccess userDataAccess;

	@Property
	private final DateTimeFormatter ISODate = ISODateTimeFormat.dateTime();

	@Inject
	private BeanModelSource beanModelSource;

	@Inject
	private ComponentResources componentResources;

	@Property
	private G_EntityQuery currentQuery;
	@Property
	private G_PropertyMatchDescriptor currentPmd;
	@Property
	private String currentFilter;
	@Property
	private List<String> currentFilters;
	@Property
	private G_PropertyMatchDescriptor currentTuple;
	@Property
	private G_ReportViewEvent currentReportView;

	@Property
	private List<G_EntityQuery> searchQueryList;
	@Persist
	private BeanModel<G_EntityQuery> entityQuerymodel;
	@Persist
	private BeanModel<G_ReportViewEvent> reportViewmodel;
	@Inject
	private ComponentResources resources;

	@Property
	private List<G_ReportViewEvent> reportViewList;

	@Property
	private String workspaceId;

	@InjectPage
	private CombinedEntitySearchPage searchPage;

	@Inject
	private AjaxResponseRenderer ajax;
	@Inject
	private DataSourceListDAO dao;

	private List<String> reportViewIds;

	private List<String> reportIds;

	@InjectComponent
	private Zone reportViewListZone;
	@InjectComponent
	private Zone searchQueryListZone;

	@Inject
	private UserWorkspaceDAO uwDao;

	@Inject
	private PageRenderLinkSource pageRenderLinkSource;

	@Persist
	private G_EntityQuery selectedQuery;

	public String getCurrentPMDValues() {
		return RangeHelper.toString(currentPmd);
	}

	// Generally useful bits and pieces
	public BeanModel<G_EntityQuery> getEntityQueryModel() {
		if (entityQuerymodel == null) {
			entityQuerymodel = beanModelSource.createDisplayModel(G_EntityQuery.class, resources.getMessages());
			entityQuerymodel.exclude("caseSensitive", "searchFreeText", "initiatorId", "minimumscore", "minsecs",
					"maxsecs", "sortcolumn", "sortfield", "firstresult", "maxresult", "datasource", "userId",
					"username", "sortascending", "id", "schema", "targetSchema", "propertyMatchDescriptors");
			entityQuerymodel.addEmpty("action");
			entityQuerymodel.addEmpty("queryDescription");
			entityQuerymodel.reorder("action", "queryDescription", "timeinitiated");
		}
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
		json.put("aaSorting", new JSONArray().put(new JSONArray().put(2).put("desc")));
		// new JSONObject().put("aTargets", new JSONArray().put(0, 4));
		// final JSONObject sortType = new JSONObject("sType", "formatted-num");
		// final JSONArray columnArray = new JSONArray();
		// columnArray.put(4, sortType);

		// json.put("aoColumns", columnArray);
		json.put("oLanguage", new JSONObject("sSearch", "Filter:"));
		return json;
	}

	public String getPageLink() {
		if (ValidationUtils.isValid(currentReportView.getReportPageLink())
				&& !currentReportView.getReportPageLink().startsWith("/")) {
			logger.debug("Returning " + currentReportView.getReportPageLink());
			return currentReportView.getReportPageLink();
		}
		return null;
	}

	public List<G_PropertyMatchDescriptor> getQueryParameters() {
		return currentQuery.getPropertyMatchDescriptors();
	}

	public BeanModel<G_ReportViewEvent> getReportViewModel() {
		if (reportViewmodel == null) {
			reportViewmodel = beanModelSource.createDisplayModel(G_ReportViewEvent.class, resources.getMessages());
			reportViewmodel.exclude("schema", "id", "userId", "username", "reportpagelink");
			reportViewmodel.addEmpty("action");
			reportViewmodel.reorder("action", "reportId", "reporttype", "timeinitiated");
			reportViewmodel.get("action").sortable(true);
		}
		return reportViewmodel;
	}

	@SetupRender
	private void loadQueries() {
		if (currentSelectedWorkspaceExists) {
			logger.debug("Current workspace exists, making sure user still has access rights.");
			if (uwDao.hasRelationship(user.getId(), currentSelectedWorkspace.getId(),
					G_UserSpaceRelationshipType.EDITOR_OF, G_UserSpaceRelationshipType.REVIEWER_OF)) {
				searchQueryList = currentSelectedWorkspace.getQueryObjects();
				reportViewList = currentSelectedWorkspace.getSavedReports();
			} else {
				logger.error("User tried to view a workspace without rights to it.");
				// User is no longer an editor or reviewer of this workspace.
				currentSelectedWorkspace = null;
				searchQueryList = null;
				reportViewList = null;
			}
		} else {
			logger.warn("No current workspace selected, so viewer will not show anything.");
		}
	}

	void onActivate(final String workspaceId) {
		this.workspaceId = workspaceId;
		if (userExists) {
			if (ValidationUtils.isValid(workspaceId)) {
				try {
					logger.info("Attempting to retrieve workspace " + workspaceId + " for user " + user.getUsername());
					currentSelectedWorkspace = userDataAccess.getWorkspace(user.getId(), workspaceId);

				} catch (final AvroRemoteException e) {
					logger.error(ExceptionUtil.getRootCauseMessage(e));
					alertManager.alert(Duration.SINGLE, Severity.ERROR, "You are not authorized to view the workspace "
							+ workspaceId + ".");
				}
			} else if (currentSelectedWorkspaceExists) {
				// view previously selected workspace
				logger.info("Viewing the workspace that was already current " + currentSelectedWorkspace.getId()
						+ " for user " + user.getUsername());
			}
		} else {
			alertManager.alert(Duration.UNTIL_DISMISSED, Severity.ERROR, "You must be logged in to view a workspace");
			logger.error("User was not logged in and tried to view " + workspaceId);
		}
	}

	void onDelete(final String id, final long timeInitiated) {

		G_ReportViewEvent reportToDelete = null;
		for (final G_ReportViewEvent x : currentSelectedWorkspace.getSavedReports()) {
			if (x.getReportId().equals(id) && (x.getTimeInitiated() == timeInitiated)) {
				reportToDelete = x;
			}
		}
		if (reportToDelete != null) {
			final boolean success = currentSelectedWorkspace.getSavedReports().remove(reportToDelete);
			if (success) {
				alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS, "Removed saved report " + id);
			} else {
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Could not remove saved report " + id);
			}
		} else {
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Could not find report to remove with id " + id);
		}
		try {
			currentSelectedWorkspace = userDataAccess.saveWorkspace(user.getId(), currentSelectedWorkspace);

		} catch (final UnauthorizedActionException e) {
			logger.error(e.getMessage());
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		reportViewList = currentSelectedWorkspace.getSavedReports();
		if (request.isXHR()) {
			ajax.addRender(reportViewListZone);
		}
	}

	void onDeleteQuery(final long timeInitiated) {
		for (int i = 1; i < currentSelectedWorkspace.getQueryObjects().size(); i++) {
			if (currentSelectedWorkspace.getQueryObjects().get(i).getTimeInitiated() == timeInitiated) {
				currentSelectedWorkspace.getQueryObjects().remove(i);
				break;
			}
		}
		// if (q != null) {
		//
		// final boolean success =
		// currentSelectedWorkspace.getQueryObjects().remove(q);
		// if (success) {
		// alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS,
		// "Removed saved query " + timeInitiated);
		// } else {
		// alertManager
		// .alert(Duration.TRANSIENT, Severity.ERROR,
		// "Could not remove query report " + timeInitiated);
		// }
		// } else {
		// alertManager.alert(Duration.TRANSIENT, Severity.ERROR,
		// "Could not find query to remove with id "
		// + timeInitiated);
		// }
		try {
			currentSelectedWorkspace = userDataAccess.saveWorkspace(user.getId(), currentSelectedWorkspace);
		} catch (final UnauthorizedActionException e) {
			logger.error(e.getMessage());
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		searchQueryList = currentSelectedWorkspace.getQueryObjects();
		if (request.isXHR()) {
			ajax.addRender(searchQueryListZone);
		}
	}

	// onPassivate() is called by Tapestry to get the activation context to put
	// in the id.
	String onPassivate() {
		if (currentSelectedWorkspaceExists) {
			return currentSelectedWorkspace.getId();
		} else {
			return null;
		}
	}

	public Object onSearch(final G_EntityQuery q) {
		logger.debug("onSelectedFromSearch, prebuilt is " + q);
		selectedQuery = q;
		searchPage.setPrebuilt(selectedQuery);
		return searchPage;
	}

	public Object onSuccess() {
		logger.debug("In View, prebuilt is " + selectedQuery);
		searchPage.setPrebuilt(selectedQuery);
		return searchPage;
	}
}
