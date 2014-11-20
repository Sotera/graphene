package graphene.web.components;

import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_Workspace;
import graphene.web.model.WorkspaceFilteredDataSource;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
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
	private DateTimeFormatter ISODate = ISODateTimeFormat.date();
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
	private AlertManager manager;

	private BeanModel<G_Workspace> model;

	// Screen fields

	@Parameter(required = true)
	@Property
	private String partialName;

	@Inject
	private Request request;
	@Inject
	private ComponentResources resources;

	@Parameter(required = true)
	@Property
	private String selectedWorkspaceId;

	@Inject
	private G_UserDataAccess service;
	@SessionState(create = false)
	private G_User user;
	private boolean userExists;

	@Property
	private G_Workspace workspace;

	public String getLinkCSSClass() {
		if (workspace != null && workspace.getId() == selectedWorkspaceId) {
			return "active";
		} else {
			return "";
		}
	}

	// Generally useful bits and pieces
	public BeanModel<G_Workspace> getModel() {
		// TODO: Move the initialization to setupRender
		this.model = beanModelSource.createDisplayModel(G_Workspace.class,
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
		String sDom = "<\"widget-body-toolbar\"lfip><t>";
		// String sDom =
		// "<\"widget-body-toolbar\"i>rt<\"bottom\"flp><\"clear\">";
		// String sDom = "TC<\"clear\">Rlfrtip";
		JSONObject json = new JSONObject("bJQueryUI", "true", "sDom", sDom);

		return json;
	}

	// Getters
	/**
	 * Change this to return all workspaces if the user has the right
	 * privileges.
	 * 
	 * @return
	 */
	public GridDataSource getWorkspaces() {
		if (userExists) {
			return new WorkspaceFilteredDataSource(service, user.getId(),
					partialName);
		} else {
			logger.error("No user name to get workspaces for.");
			return null;
		}
	}

	public boolean isAjax() {
		return request.isXHR();
	}

	// Handle event "selected"
	boolean onSelected(String workspaceId) {
		// Return false, which means we haven't handled the event so bubble it
		// up.
		// This method is here solely as documentation, because without this
		// method the event would bubble up anyway.
		return false;
	}

	boolean onSuccessFromFilterForm() {
		// Trigger new event "filter" which will bubble up.
		componentResources.triggerEvent("filter", null, null);
		// We don't want "success" to bubble up, so we return true to say we've
		// handled it.
		return true;
	}
}