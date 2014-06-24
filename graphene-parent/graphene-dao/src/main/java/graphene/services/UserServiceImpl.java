package graphene.services;

import graphene.dao.GroupDAO;
import graphene.dao.PermissionDAO;
import graphene.dao.RoleDAO;
import graphene.dao.UserDAO;
import graphene.dao.WorkspaceDAO;
import graphene.model.idl.AuthenticationException;
import graphene.model.idl.G_Group;
import graphene.model.idl.G_Permission;
import graphene.model.idl.G_Role;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_UserSpaceRelationshipType;
import graphene.model.idl.G_Workspace;
import graphene.model.idl.UnauthorizedActionException;
import graphene.util.validator.ValidationUtils;

import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.joda.time.DateTime;
import org.slf4j.Logger;

/**
 * 
 * @author djue
 * 
 */
public class UserServiceImpl implements G_UserDataAccess {
	@Inject
	private PermissionDAO pDao;
	@Inject
	private RoleDAO rDao;
	@Inject
	private GroupDAO gDao;
	@Inject
	private Logger logger;
	@Inject
	private UserDAO uDao;
	@Inject
	private WorkspaceDAO wDao;

	@Override
	public G_Workspace addNewWorkspaceForUser(String username,
			G_Workspace workspace) {
		// save the workspace
		G_Workspace w = wDao.getOrCreateWorkspace(workspace);
		// add user as editor
		if (w == null
				|| !wDao.addRelationToWorkspace(username,
						G_UserSpaceRelationshipType.EDITOR_OF,
						w.getWorkspaceid())) {
			logger.error("Could not create editor relationship for workspace");
		}
		return workspace;
	}

	@Override
	public int countUsers(String partialName) {
		// TODO: Put business logic here to let admins see all users.
		long lcount = uDao.countUsers(partialName);
		if (lcount > Integer.MAX_VALUE) {
			// I know this won't ever happen, but I want to make the static code
			// checkers happy.
			logger.error("Too many workspaces");
			return Integer.MAX_VALUE;
		} else {
			return (int) lcount;
		}
	}

	@Override
	public int countWorkspaces(String userId, String partialWorkspaceName) {
		// TODO: Put business logic here to let admins see all workspaces.
		long lcount = wDao.countWorkspaces(userId, partialWorkspaceName);
		if (lcount > Integer.MAX_VALUE) {
			// I know this won't ever happen, but I want to make the static code
			// checkers happy.
			logger.error("Too many workspaces");
			return Integer.MAX_VALUE;
		} else {
			return (int) wDao.countWorkspaces(userId, partialWorkspaceName);
		}
	}

	@Override
	public G_Workspace createFirstWorkspaceForUser(String username) {

		G_Workspace w = createTempWorkspaceForUser(username);
		w.setTitle("First Workspace");
		wDao.getOrCreateWorkspace(w);
		wDao.addRelationToWorkspace(username,
				G_UserSpaceRelationshipType.CREATOR_OF, w.getWorkspaceid());
		wDao.addRelationToWorkspace(username,
				G_UserSpaceRelationshipType.EDITOR_OF, w.getWorkspaceid());
		return w;
	}

	@Override
	public G_Workspace createTempWorkspaceForUser(String username) {
		DateTime time = DateTime.now();
		G_Workspace w = new G_Workspace(true, username, "", "New Workspace",
				username + "-Workspace" + time.toString("YYYYmmDD-HHMMSS"),
				time.getMillis(), time.getMillis());
		return w;
	}

	@Override
	public boolean deleteUser(String username) {
		return uDao.delete(username);
	}

	@Override
	public boolean deleteWorkspace(String username, String workspaceId)
			throws UnauthorizedActionException {
		if (wDao.hasRelationship(username, workspaceId,
				G_UserSpaceRelationshipType.CREATOR_OF)) {
			return wDao.deleteWorkspaceById(workspaceId);

		} else {
			String errorStr = "User " + username
					+ " did not have permission to delete Workspace "
					+ workspaceId + ".";
			logger.error(errorStr);
			throw new UnauthorizedActionException(errorStr);
		}
	}

	@Override
	public boolean deleteWorkspaceIfUnused(String username, String workspaceId)
			throws UnauthorizedActionException {
		if (wDao.hasRelationship(username, workspaceId,
				G_UserSpaceRelationshipType.CREATOR_OF)) {
			return wDao.deleteWorkspaceIfUnused(workspaceId);

		} else {
			String errorStr = "User " + username
					+ " did not have permission to delete Workspace "
					+ workspaceId + ".";
			logger.error(errorStr);
			throw new UnauthorizedActionException(errorStr);
		}
	}

	@Override
	public List<G_Workspace> findWorkspaces(String userId, String partialName,
			int offset, int limit) {
		// TODO: Put business logic here to let admins see all workspaces.
		return wDao.findWorkspaces(userId, partialName, offset, limit);
	}

	@Override
	public List<G_User> getByPartialUsername(String partialName, int offset,
			int limit) {
		// TODO: Put business logic here to let admins see all users.
		return uDao.getByPartialUsername(partialName, offset, limit);
	}

	@Override
	public G_User getUser(String username) {
		// TODO: Add business logic to restrict this to Admins, or create other
		// methods for inquiring about read only properties of other users.
		return uDao.getByUsername(username);
	}

	@Override
	public G_Workspace getWorkspace(String username, String workspaceId)
			throws UnauthorizedActionException {
		if (!ValidationUtils.isValid(username)) {
			throw new UnauthorizedActionException(
					"The username provided was not valid");
		} else if (!ValidationUtils.isValid(workspaceId)) {
			throw new UnauthorizedActionException(
					"The workspace id provided was not valid");
		}
		if (wDao.hasRelationship(username, workspaceId,
				G_UserSpaceRelationshipType.CREATOR_OF,
				G_UserSpaceRelationshipType.EDITOR_OF,
				G_UserSpaceRelationshipType.REVIEWER_OF)) {
			return wDao.getWorkspaceById(workspaceId);
		} else {
			String errorStr = "User " + username
					+ " did not have permission to view Workspace "
					+ workspaceId + ".";
			logger.error(errorStr);
			throw new UnauthorizedActionException(errorStr);
		}

	}

	@Override
	public List<G_Workspace> getWorkspacesForUser(String username) {
		// TODO: Put business logic here to let admins see all workspaces.
		return wDao.getWorkspacesForUser(username);
	}

	@Override
	public G_User loginUser(String username, String password)
			throws AuthenticationException {
		G_User u = uDao.loginUser(username, password);
		if (u == null) {
			throw new AuthenticationException("Could not login user "
					+ username + ".  Check username and password.");
		}
		return u;
	}

	@Override
	public G_User registerUser(G_User d) {
		// Perform any business logic
		if (ValidationUtils.isValid(d)) {
			if (!ValidationUtils.isValid(d.getAvatar())) {
				d.setAvatar("unknown.png");
			}

			d.setActive(true);

			d.setLastlogin(0l);
			d.setNumberlogins(0);
			return uDao.createOrUpdate(d);
		}
		return null;
	}

	@Override
	public boolean removeUserFromWorkspace(String username, String workspaceId) {
		return wDao.removeUserFromWorkspace(username, workspaceId);
	}

	@Override
	public boolean removeUserPermissionFromWorkspace(String username,
			String permission, String workspaceId) throws AvroRemoteException {
		return wDao.removeUserPermissionFromWorkspace(username, permission,
				workspaceId);
	}

	@Override
	public G_Group saveGroup(G_Group g) {
		gDao.save(g);
		return g;
	}

	@Override
	public G_User saveUser(G_User user) {
		user.setLastmodified(DateTime.now().getMillis());
		return uDao.save(user);
	}

	@Override
	public G_Workspace saveWorkspace(String username, G_Workspace workspace)
			throws UnauthorizedActionException {
		// save the workspace
		if (wDao.hasRelationship(username, workspace.getWorkspaceid(),
				G_UserSpaceRelationshipType.CREATOR_OF,
				G_UserSpaceRelationshipType.EDITOR_OF)) {
			workspace.setLastmodified(DateTime.now().getMillis());
			return wDao.save(workspace);
		} else {
			String errorStr = "User " + username
					+ " did not have permission to save Workspace "
					+ workspace.getWorkspaceid() + ".";
			logger.error(errorStr);
			throw new UnauthorizedActionException(errorStr);
		}

	}

	@Override
	public boolean setUserPassword(String username, String newPassword) {
		return uDao.updatePassword(username, newPassword);
	}

	@Override
	public boolean userExists(String username) {
		return uDao.isExisting(username);
	}

	@Override
	public List<G_Workspace> getWorkspacesOrCreateNewForUser(String username)
			throws AvroRemoteException {
		List<G_Workspace> workspaces = getWorkspacesForUser(username);
		if (workspaces.size() == 0) {
			G_Workspace g = createFirstWorkspaceForUser(username);
			workspaces.add(g);
		}
		return workspaces;
	}

	@Override
	public List<G_Role> getRolesByUsername(String username)
			throws AvroRemoteException {
		return rDao.getForUsername(username);
	}

	@Override
	public List<G_Permission> getPermissionsByRole(G_Role role)
			throws AvroRemoteException {
		return pDao.getForRole(role);
	}

	@Override
	public String getPasswordHash(String username, String password) {
		return uDao.getPasswordHash(username, password);
	}

}
