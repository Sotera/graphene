package graphene.web.components.workspace;

import graphene.model.idl.G_ReportViewEvent;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_Workspace;
import graphene.web.pages.workspace.Manage;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.Request;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.slf4j.Logger;

/**
 * List of workspaces for CRUD
 * 
 * @author djue
 * 
 */
@Events({ WorkspaceList.SELECTED })
public class WorkspaceList {
	public static final String SELECTED = "selected";

	// Parameters
	@Property
	private final DateTimeFormatter ISODate = ISODateTimeFormat.date();
	@Inject
	private BeanModelSource beanModelSource;

	@Inject
	private ComponentResources componentResources;

	@InjectComponent
	private Zone listZone;
    
	@Inject
	private Logger logger;
	// The code

	@Inject
	private AlertManager alertManager;

	@Persist
	private BeanModel<G_Workspace> model;

	// Screen fields

	@Parameter(required = true)
	@Property
	private String partialName;
	@Parameter(required = false)
	@Property
	private String title;

	@Inject
	private Request request;
	@Inject
	private ComponentResources resources;

	@Parameter(required = true)
	@Property
	private String selectedWorkspaceId;

	@Property
	private G_ReportViewEvent currentReport;

	@Inject
	private G_UserDataAccess userDataAccess;
	@SessionState(create = false)
	private G_User user;
	private boolean userExists;

	@Property
	private G_Workspace workspace;

	@Property
	@SessionState
	private List<G_Workspace> workspaces;

	public String getLinkCSSClass() {
		if ((workspace != null) && (workspace.getId().equals(selectedWorkspaceId)))
			return "active";

		return "";
	}

	public List<G_Workspace> getListOfWorkspaces() {
		if (userExists) {
			try {
				logger.debug("Updating list of workspaces");
				workspaces = userDataAccess.getWorkspacesForUser(user.getId());
			} catch (final AvroRemoteException e) {
				logger.error(e.getMessage());
			}
        } else {
			logger.error("No user name to get workspaces for.");
			workspaces = null;
		}
		return workspaces;
	}

	// Generally useful bits and pieces
	public BeanModel<G_Workspace> getModel() {
		if (model == null) {
			// TODO: Move the initialization to setupRender
			model = beanModelSource.createDisplayModel(G_Workspace.class, resources.getMessages());
			model.exclude("schema", "active", "reports", "id");
			model.reorder("title", "description", "savedreports", "queryObjects", "modified", "created");
		}
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

		final JSONObject json = new JSONObject(
				"bJQueryUI",
				"true",
				"bAutoWidth",
				"true",
				"sDom",
				"<\"col-sm-4\"f><\"col-sm-4\"i><\"col-sm-4\"l><\"row\"<\"col-sm-12\"p><\"col-sm-12\"r>><\"row\"<\"col-sm-12\"t>><\"row\"<\"col-sm-12\"ip>>");
		// Sorts the columns of workspace tables.
		json.put("aaSorting", new JSONArray().put(new JSONArray().put(0).put("asc")));
		// new JSONObject().put("aTargets", new JSONArray().put(0, 4));
		// final JSONObject sortType = new JSONObject("sType", "formatted-num");
		// final JSONArray columnArray = new JSONArray();
		// columnArray.put(4, sortType);

		// json.put("aoColumns", columnArray);
		json.put("oLanguage", new JSONObject("sSearch", "Filter:"));
		return json;
	}

	// Getters

	public boolean isAjax() {
		return request.isXHR();
	}

	// Handle event "selected"
	boolean onSelected(final String workspaceId) {
		// Return false, which means we haven't handled the event so bubble it
		// up.
		// This method is here solely as documentation, because without this
		// method the event would bubble up anyway.

		return false;
	}
}