/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.web.pages.admin;

import graphene.dao.DataSourceListDAO;
import graphene.dao.UserDAO;
import graphene.dao.UserWorkspaceDAO;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_UserWorkspace;
import graphene.model.idl.G_VisualType;
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
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.BeanModelSource;
import org.apache.tapestry5.services.ajax.AjaxResponseRenderer;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;

@PluginPage(visualType = G_VisualType.ADMIN, menuName = "Manage Users", icon = "fa fa-lg fa-fw fa-list-alt")
public class Manage extends SimpleBasePage {

	@Inject
	private G_UserDataAccess userDataAccess;

	@Property
	private final DateTimeFormatter ISODate = ISODateTimeFormat.dateTime();

	@Inject
	private BeanModelSource beanModelSource;

	@Inject
	private ComponentResources componentResources;

	@Property
	private G_User currentUser;

	@Property
	private String currentFilter;
	@Property
	private List<String> currentFilters;
	@Property
	private G_UserWorkspace currentUserWorkspace;

	@Property
	@Persist
	private List<G_User> userList;
	@Persist
	private BeanModel<G_User> userModel;
	@Persist
	private BeanModel<G_UserWorkspace> userWorkspaceModel;
	@Inject
	private ComponentResources resources;

	@Property
	@Persist
	private List<G_UserWorkspace> userWorkspaceList;

	@Property
	private String userId;

	@InjectPage
	private CombinedEntitySearchPage searchPage;

	@Inject
	private AjaxResponseRenderer ajax;
	@Inject
	private DataSourceListDAO dao;

	private List<String> reportViewIds;

	private List<String> reportIds;

	@InjectComponent
	private Zone userListZone;
	@InjectComponent
	private Zone userWorkspaceListZone;

	@Inject
	private UserDAO userDao;

	@Inject
	private UserWorkspaceDAO userWorkspaceDao;

	public JSONObject getOptions() {

		final JSONObject json = new JSONObject(
				"bJQueryUI",
				"true",
				"bAutoWidth",
				"true",
				"sDom",
				"<\"col-sm-4\"f><\"col-sm-4\"i><\"col-sm-4\"l><\"row\"<\"col-sm-12\"p><\"col-sm-12\"r>><\"row\"<\"col-sm-12\"t>><\"row\"<\"col-sm-12\"ip>>");
		// Sort by score then by date.
		json.put("aaSorting", new JSONArray().put(new JSONArray().put(1).put("desc")));
		// new JSONObject().put("aTargets", new JSONArray().put(0, 4));
		// final JSONObject sortType = new JSONObject("sType", "formatted-num");
		// final JSONArray columnArray = new JSONArray();
		// columnArray.put(4, sortType);

		// json.put("aoColumns", columnArray);
		json.put("oLanguage", new JSONObject("sSearch", "Filter:"));
		return json;
	}

	// Generally useful bits and pieces
	public BeanModel<G_User> getUserModel() {
		if (userModel == null) {
			userModel = beanModelSource.createDisplayModel(G_User.class, resources.getMessages());
			userModel.exclude("schema", "hashedpassword", "avatar");
			userModel.addEmpty("action");
			userModel.reorder("action", "username", "id", "email", "fullname", "modified");
		}
		return userModel;
	}

	public BeanModel<G_UserWorkspace> getUserWorkspaceModel() {
		if (userWorkspaceModel == null) {
			userWorkspaceModel = beanModelSource.createDisplayModel(G_UserWorkspace.class, resources.getMessages());
			userWorkspaceModel.exclude("schema", "hashedpassword");

			userWorkspaceModel.addEmpty("action");
			userWorkspaceModel.reorder("action", "id", "userId", "role", "workspaceId", "modified");
		}
		return userWorkspaceModel;
	}

	@SetupRender
	private void loadData() {
		userList = userDao.getAll();
		userWorkspaceList = userWorkspaceDao.getAll();
	}

	@OnEvent("delete")
	void onDelete(final String id, final String username) {
		boolean success;
		if (ValidationUtils.isValid(id)) {

			try {
				success = userDataAccess.deleteUser(id);
				if (success) {
					alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS, "Removed user " + id);
				} else {
					alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Could not remove user " + id);
				}
			} catch (final AvroRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else if (ValidationUtils.isValid(username)) {
			/*
			 * This was added because in some prototype code, users would be
			 * created without the id field being re-saved properly, thus we
			 * only had the username to look it up by. GetByUsername now stores
			 * the id in the return object, so we can delete it.
			 */
			final G_User userByUsername = userDao.getByUsername(username);
			// user2.setId("TaggedForDeletion");
			// user2 = userDao.save(user2);
			if (ValidationUtils.isValid(userByUsername)) {
				try {
					success = userDataAccess.deleteUser(userByUsername.getId());
					if (success) {
						alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS,
								"Removed user " + userByUsername.getId());
					} else {
						alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Could not remove user "
								+ userByUsername.getId());
					}
				} catch (final AvroRemoteException e) {
					logger.error("Error removing user", e);
					alertManager.alert(Duration.TRANSIENT, Severity.ERROR, e.getMessage());
				}
			} else {
				final String errorMsg = "Could not remove user with username " + username + " or userid "
						+ userByUsername.getId();
				logger.error(errorMsg);
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR, errorMsg);
			}
		} else {
			final String errorMsg = "Could not find user to remove with username " + username + " or userid " + userId;
			logger.error(errorMsg);
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, errorMsg);
		}

		userList = userDao.getAll();
		if (request.isXHR()) {

			ajax.addRender(userListZone);
			ajax.addRender(userWorkspaceListZone);
		}
	}

	@OnEvent("deleteuserworkspace")
	void onDeleteUserWorkspace(final String id) {
		if (ValidationUtils.isValid(id)) {
			final boolean success = userWorkspaceDao.delete(id);
			if (success) {
				alertManager.alert(Duration.TRANSIENT, Severity.SUCCESS, "Removed workspace relation " + id);
			} else {
				logger.error("Could not delete workspace relation with id: " + id);
				alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "Could not remove workspace relation " + id);
			}
		} else {
			logger.error("Could not delete workspace relation because no id was provided.");
			alertManager.alert(Duration.TRANSIENT, Severity.ERROR, "No valid workspace relation to delete");
		}
		// refresh the list.
		userWorkspaceList = userWorkspaceDao.getAll();
		if (request.isXHR()) {
			ajax.addRender(userWorkspaceListZone);
		}
	}

}
