package graphene.web.pages;

import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_VisualType;
import graphene.model.idl.G_Workspace;
import graphene.model.query.EntityQuery;
import graphene.util.ExceptionUtil;
import graphene.util.validator.ValidationUtils;
import graphene.web.annotations.PluginPage;

import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.alerts.Duration;
import org.apache.tapestry5.alerts.Severity;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BeanModelSource;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;

@PluginPage(visualType = G_VisualType.TOP, menuName = "View Workspace", icon = "fa fa-lg fa-fw fa-list-alt")
public class ViewWorkspace {
	@Property
	@SessionState(create = false)
	private G_Workspace currentSelectedWorkspace;

	@SessionState(create = false)
	private G_User user;

	private boolean userExists;
	@Inject
	private G_UserDataAccess userDataAccess;
	@Persist
	private String workspaceId;

	@Inject
	private AlertManager alertManager;
	@Inject
	private Logger logger;

	@Property
	private List<Object> list;
	@Property
	private EntityQuery currentQuery;

	@Inject
	private BeanModelSource beanModelSource;
	@Inject
	private ComponentResources resources;
	private BeanModel<String> model;
	@Property
	private boolean currentSelectedWorkspaceExists;

	@Property
	private G_SearchTuple<String> currentTuple;

	@Property
	private final DateTimeFormatter ISODate = ISODateTimeFormat.dateTime();

	// Generally useful bits and pieces
	public BeanModel<String> getModel() {
		// TODO: Move the initialization to setupRender
		model = beanModelSource.createDisplayModel(String.class,
				resources.getMessages());
		model.exclude("caseSensitive", "searchFreeText", "initiatorId",
				"attributevalues", "minimumscore", "minsecs", "maxsecs",
				"sortcolumn", "sortfield", "firstresult", "maxresult",
				"datasource", "userid", "sortascending", "id", "schema");

		// model.get("AttributeList").sortable(true);
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

	void onActivate(final String workspaceId) {
		this.workspaceId = workspaceId;
		if (userExists) {
			if (ValidationUtils.isValid(workspaceId)) {
				try {
					logger.info("Attempting to retrieve workspace "
							+ workspaceId + " for user " + user.getUsername());
					currentSelectedWorkspace = userDataAccess.getWorkspace(
							user.getId(), workspaceId);
					list = currentSelectedWorkspace.getQueryObjects();
				} catch (final AvroRemoteException e) {
					logger.error(ExceptionUtil.getRootCauseMessage(e));
					alertManager.alert(Duration.SINGLE, Severity.ERROR,
							"You are not authorized to view the workspace "
									+ workspaceId + ".");
				}
			} else if (currentSelectedWorkspaceExists) {
				// view previously selected workspace
				logger.info("Viewing the workspace that was already current "
						+ currentSelectedWorkspace.getId() + " for user "
						+ user.getUsername());
			}
		} else {
			alertManager.alert(Duration.UNTIL_DISMISSED, Severity.ERROR,
					"You must be logged in to view a workspace");
			logger.error("User was not logged in and tried to view "
					+ workspaceId);
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
}
