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

package graphene.services;

import graphene.dao.GroupDAO;
import graphene.dao.PermissionDAO;
import graphene.dao.RoleDAO;
import graphene.dao.UserDAO;
import graphene.dao.UserGroupDAO;
import graphene.dao.UserRoleDAO;
import graphene.dao.UserWorkspaceDAO;
import graphene.dao.WorkspaceDAO;
import graphene.model.idl.AuthenticationException;
import graphene.model.idl.G_Group;
import graphene.model.idl.G_Permission;
import graphene.model.idl.G_PropertyDescriptors;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_Role;
import graphene.model.idl.G_SearchResults;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_UserRole;
import graphene.model.idl.G_UserSpaceRelationshipType;
import graphene.model.idl.G_Workspace;
import graphene.model.idl.UnauthorizedActionException;
import graphene.util.crypto.PasswordHash;
import graphene.util.validator.ValidationUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.joda.time.DateTime;
import org.slf4j.Logger;

/**
 * 
 * @author djue
 * 
 */
public class UserServiceImpl implements G_UserDataAccess {
	@Inject
	private GroupDAO gDao;
	@Inject
	private Logger logger;
	PasswordHash passwordHasher = new PasswordHash();
	@Inject
	private PermissionDAO pDao;
	@Inject
	private RoleDAO rDao;
	@Inject
	private UserDAO uDao;
	@Inject
	private UserGroupDAO ugDao;
	@Inject
	private UserWorkspaceDAO uwDao;
	@Inject
	private UserRoleDAO urDao;
	@Inject
	private WorkspaceDAO wDao;

	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_ADMIN_GROUP_NAME)
	private String adminGroupName;

	@Inject
	@Symbol(G_SymbolConstants.ENABLE_DELETE_UNUSED_WORKSPACES)
	private boolean deleteUnusedWorkspaces;
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_DELETE_WORKSPACES)
	private boolean deleteWorkspaces;
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_WORKSPACES)
	private boolean enableWorkspaces;

	@Override
	public G_Workspace addNewWorkspaceForUser(final String userId, final G_Workspace workspace) {
		// save the workspace
		try {
			final G_Workspace w = wDao.save(workspace);
			if (w == null) {
				logger.error("Could not create new workspace");
			} else {
				final boolean success1 = uwDao.addRelationToWorkspace(userId, G_UserSpaceRelationshipType.CREATOR_OF,
						w.getId());
				final boolean success2 = uwDao.addRelationToWorkspace(userId, G_UserSpaceRelationshipType.EDITOR_OF,
						w.getId());
				if (!success1 && success2) {
					logger.error("Could not create editor/creator relationship for workspace");
				}
			}

		} catch (final Exception e) {
			logger.error("Could not create editor relationship for workspace ", e);
		}
		return workspace;
	}

	@Override
	public boolean addRole(final G_User d, final G_Role r) throws AvroRemoteException {
		boolean success = false;
		if (ValidationUtils.isValid(d, r)) {
			success = urDao.add(d.getId(), r.getId());
		} else {
			logger.error("User or Role was null, user '" + d + "', role '" + r + "'");
		}
		return success;
	}

	@Override
	public G_Workspace createFirstWorkspaceForUser(final String userId) {
		G_Workspace w = createTempWorkspaceForUser(userId);

		if (ValidationUtils.isValid(w)) {
			w.setTitle("First Workspace");
			w = wDao.save(w);
			if (ValidationUtils.isValid(w)) {
				uwDao.addRelationToWorkspace(userId, G_UserSpaceRelationshipType.CREATOR_OF, w.getId());
				uwDao.addRelationToWorkspace(userId, G_UserSpaceRelationshipType.EDITOR_OF, w.getId());
			}
		} else {
			logger.error("Could not create a first workspace for user id " + userId);
		}
		return w;
	}

	@Override
	public G_Workspace createTempWorkspaceForUser(final String userId) {

		final G_User user = getUser(userId);
		G_Workspace w = null;
		if (ValidationUtils.isValid(user)) {
			// we need a valid user in order to create the workspace.
			final DateTime time = DateTime.now();
			w = new G_Workspace();
			w.setActive(true);
			w.setCreated(time.getMillis());
			w.setModified(time.getMillis());
			w.setDescription("New Workspace");
			w.setTitle(user.getUsername() + " - Workspace" + time.toString("YYYYmmDD-HHMMSS"));

		} else {
			logger.error("Could not find user " + userId + " to create a temp workspace for.");
		}
		return w;
	}

	@Override
	public boolean deleteUser(final String userId) {
		if (deleteWorkspaces) {
			final List<G_Workspace> workspacesForUser = getWorkspacesForUser(userId);
			for (final G_Workspace w : workspacesForUser) {
				try {
					deleteWorkspaceIfUnused(userId, w.getId());
				} catch (final Exception e) {
					logger.error("Could not delete workspace " + w.getId(), e);
				}
			}
		}
		return uDao.delete(userId);
	}

	@Override
	public boolean deleteWorkspace(final String userId, final String workspaceId) throws UnauthorizedActionException {
		if (uwDao.hasRelationship(userId, workspaceId, G_UserSpaceRelationshipType.CREATOR_OF)) {
			return wDao.delete(workspaceId);

		} else {
			final String errorStr = "User " + userId + " did not have permission to delete Workspace " + workspaceId
					+ ".";
			logger.error(errorStr);
			throw new UnauthorizedActionException(errorStr);
		}
	}

	@Override
	public boolean deleteWorkspaceIfUnused(final String userId, final String workspaceId)
			throws UnauthorizedActionException {
		boolean success = false;
		if (deleteUnusedWorkspaces) {
			if (ValidationUtils.isValid(userId)
					&& uwDao.hasRelationship(userId, workspaceId, G_UserSpaceRelationshipType.CREATOR_OF)) {
				uwDao.deleteWorkspaceRelations(workspaceId);
				wDao.delete(workspaceId);
				success = true;
			} else if (uwDao.countUsersForWorkspace(workspaceId) == 0) {
				// when no user id is provided.
				wDao.delete(workspaceId);
				success = true;
			} else {
				final String errorStr = "User " + userId + " did not have permission to delete Workspace "
						+ workspaceId + ".";
				logger.error(errorStr);
				throw new UnauthorizedActionException(errorStr);
			}
		} else {
			logger.debug("Delete unused workspaces disabled.");
		}
		return success;
	}

	@Override
	public G_User getByUsername(final String username) {
		return uDao.getByUsername(username);
	}

	@Override
	public List<G_Group> getGroupsByUserId(final String userId) throws AvroRemoteException {
		return ugDao.getGroupsForUserId(userId);
	}

	@Override
	public List<G_Workspace> getLatestWorkspacesForUser(final String userId, final int quantity)
			throws AvroRemoteException {
		return uwDao.getMostRecentWorkspacesForUser(userId, quantity);

	}

	@Override
	public List<G_Permission> getPermissionsByRole(final G_Role role) throws AvroRemoteException {
		List<G_Permission> list = new ArrayList<G_Permission>();
		if (ValidationUtils.isValid(role)) {
			list = pDao.getForRole(role);
		} else {
			logger.error("No valid role provided to get permissions for.");
		}
		return list;
	}

	@Override
	public List<G_Role> getRolesByUser(final String id) throws AvroRemoteException {
		return urDao.getRolesForUserId(id);
	}

	@Override
	public G_User getUser(final String userId) {
		// TODO: Add business logic to restrict this to Admins, or create other
		// methods for inquiring about read only properties of other users.
		return uDao.getById(userId);
	}

	@Override
	public G_Workspace getWorkspace(final String userId, final String workspaceId) throws UnauthorizedActionException {
		if (!ValidationUtils.isValid(userId)) {
			throw new UnauthorizedActionException("The username provided was not valid");
		} else if (!ValidationUtils.isValid(workspaceId)) {
			throw new UnauthorizedActionException("The workspace id provided was not valid");
		}
		if (uwDao.hasRelationship(userId, workspaceId, G_UserSpaceRelationshipType.CREATOR_OF,
				G_UserSpaceRelationshipType.EDITOR_OF, G_UserSpaceRelationshipType.REVIEWER_OF)) {
			return wDao.getById(workspaceId);
		} else {
			final String errorStr = "User " + userId + " did not have permission to view Workspace " + workspaceId
					+ ".";
			logger.error(errorStr);
			throw new UnauthorizedActionException(errorStr);
		}

	}

	@Override
	public List<G_Workspace> getWorkspacesForUser(final String userId) {
		return uwDao.getWorkspacesForUser(userId);
	}

	@Override
	public List<G_Workspace> getWorkspacesOrCreateNewForUser(final String userId) throws AvroRemoteException {
		final List<G_Workspace> workspaces = getWorkspacesForUser(userId);
		if (workspaces.size() == 0) {
			logger.debug("No workspaces found for userId " + userId + ". Creating a new one.");
			final G_Workspace g = createFirstWorkspaceForUser(userId);
			if (g != null) {
				workspaces.add(g);
			}
		}
		return workspaces;
	}

	@Override
	public boolean isAdmin(final String userId) {
		boolean userIsAnAdmin = false;
		final G_Role role = rDao.getRoleByRolename("admin");
		if (ValidationUtils.isValid(userId, role)) {

			final List<G_UserRole> list = urDao.getByUserIdAndRoleId(userId, role.getId());
			if (ValidationUtils.isValid(list)) {
				userIsAnAdmin = true;
			} else {
				logger.debug("Userid " + userId + " does not have role " + role.getName());
			}
		} else {
			logger.error("Could not find admin group with name " + adminGroupName);
		}
		return userIsAnAdmin;
	}

	@Override
	public G_User loginAuthenticatedUser(final String userId) throws AvroRemoteException, AuthenticationException {
		return uDao.loginAuthenticatedUser(userId);
	}

	@Override
	public G_User loginUser(final String userId, final String password) throws AuthenticationException {
		final G_User u = uDao.loginUser(userId, password);
		if (u == null) {
			throw new AuthenticationException("Could not login user " + userId + ".  Check username and password.");
		}
		return u;
	}

	@Override
	public G_User registerUser(G_User d, final String password, final boolean createWorkspace)
			throws AvroRemoteException {
		// Perform any business logic
		if (ValidationUtils.isValid(d)) {
			logger.debug("Beginning user registration process.");
			if (!ValidationUtils.isValid(d.getAvatar())) {
				d.setAvatar("unknown.png");
			}
			try {
				final String hash = passwordHasher.createHash(password);
				d.setHashedpassword(hash);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				logger.error("Could not store hashed password for new user registration " + e.getMessage());
			}
			d.setActive(true);
			d.setLastlogin(0l);
			d.setNumberlogins(0);
			d.setCreated(DateTime.now().getMillis());
			d.setId(null);
			logger.debug("Saving a new user...");
			d = uDao.save(d);
			if (ValidationUtils.isValid(d)) {
				logger.debug("User registered!");
				addRole(d, rDao.getRoleByRolename("user"));
				if (createWorkspace) {

					final G_Workspace w = createFirstWorkspaceForUser(d.getId());
					if (w == null) {
						logger.error("Error creating new workspace for newly registered user!");
					}
				}
			} else {
				logger.error("Error registering new user!");
			}

		} else {
			logger.error("Could not register null user!");
		}

		return d;
	}

	@Override
	public boolean removeRole(final G_User d, final G_Role r) throws AvroRemoteException {
		return urDao.removeFromRole(d.getId(), r.getId());
	}

	@Override
	public boolean removeUserFromWorkspace(final String userId, final String workspaceId) {
		return uwDao.removeUserFromWorkspace(userId, workspaceId);
	}

	@Override
	public boolean removeUserPermissionFromWorkspace(final String userId, final String permission,
			final String workspaceId) throws AvroRemoteException {
		boolean success = false;
		if (ValidationUtils.isValid(permission)) {
			success = uwDao.removeUserPermissionFromWorkspace(userId, permission, workspaceId);
		}
		if (success && (uwDao.countUsersForWorkspace(workspaceId) == 0)) {
			wDao.delete(workspaceId);
		} else {
			logger.error("Was not able to remove permission " + permission + " from workspace " + workspaceId);
		}
		return success;
	}

	@Override
	public G_Group saveGroup(final G_Group g) {
		gDao.save(g);
		return g;
	}

	@Override
	public G_User saveUser(final G_User user) {
		user.setModified(DateTime.now().getMillis());
		return uDao.save(user);
	}

	@Override
	public G_Workspace saveWorkspace(final String userId, final G_Workspace workspace)
			throws UnauthorizedActionException {

		// save the workspace
		if (uwDao.hasRelationship(userId, workspace.getId(), G_UserSpaceRelationshipType.CREATOR_OF,
				G_UserSpaceRelationshipType.EDITOR_OF)) {
			logger.debug("User has permission to save to this workspace.");
			workspace.setModified(DateTime.now().getMillis());
			return wDao.save(workspace);
		}
		
		final String errorStr = "User " + userId + " did not have permission to save Workspace "
				+ workspace.getId() + ".";
		logger.error(errorStr);
		throw new UnauthorizedActionException(errorStr);
	}

	@Override
	public boolean setUserPassword(final String userId, final String newPassword) {
		String hash;
		boolean success = false;
		try {
			hash = passwordHasher.createHash(newPassword);
			success = uDao.updatePasswordHash(userId, hash);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			logger.error("setUserPassword " + e.getMessage());
		}
		if (!success) {
			logger.error("Problem saving password hash");
		}
		return success;
	}

	@Override
	public boolean userExists(final String userId) throws AvroRemoteException {
		return uDao.isExistingId(userId);
	}

	@Override
	public boolean usernameExists(final String username) throws AvroRemoteException {
		final boolean exists = uDao.isExistingUsername(username);
		if (exists) {
			logger.warn("Username already exists");
		} else {
			logger.info("Username has not been taken");
		}
		return exists;
	}
	
	@Override
	public G_PropertyDescriptors getDescriptors() {
	    logger.debug("getDescriptors() invoked");
	    return null;
	}
	
	@Override
	public G_SearchResults search(java.util.List<graphene.model.idl.G_PropertyMatchDescriptor> terms, long start, long max) {
	    logger.debug("search() invoked");
	    return null;
	}
}
