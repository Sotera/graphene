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
import graphene.util.crypto.PasswordHash;
import graphene.util.validator.ValidationUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
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

	PasswordHash passwordHasher = new PasswordHash();

	@Override
	public G_Workspace addNewWorkspaceForUser(int userId, G_Workspace workspace) {
		// save the workspace
		G_Workspace w = wDao.getOrCreateWorkspace(workspace);
		// add user as editor
		if (w == null
				|| !wDao.addRelationToWorkspace(userId,
						G_UserSpaceRelationshipType.EDITOR_OF, w.getId())) {
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
	public int countWorkspaces(int userId, String partialWorkspaceName) {
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
	public G_Workspace createFirstWorkspaceForUser(int userId) {

		G_Workspace w = createTempWorkspaceForUser(userId);
		w.setTitle("First Workspace");
		wDao.getOrCreateWorkspace(w);
		wDao.addRelationToWorkspace(userId,
				G_UserSpaceRelationshipType.CREATOR_OF, w.getId());
		wDao.addRelationToWorkspace(userId,
				G_UserSpaceRelationshipType.EDITOR_OF, w.getId());
		return w;
	}

	@Override
	public G_Workspace createTempWorkspaceForUser(int userId) {

		G_User user = getUser(userId);
		DateTime time = DateTime.now();
		G_Workspace w = new G_Workspace();
		w.setActive(true);
		w.setCreated(time.getMillis());
		w.setModified(time.getMillis());
		w.setDescription("New Workspace");
		w.setTitle(user.getUsername() + "-Workspace"
				+ time.toString("YYYYmmDD-HHMMSS"));
		w.setDatamap("");
		w.setJson("");
		w.setQueries("");

		return w;
	}

	@Override
	public boolean deleteUser(int userId) {
		return uDao.delete(userId);
	}

	@Override
	public boolean deleteWorkspace(int userId, int workspaceId)
			throws UnauthorizedActionException {
		if (wDao.hasRelationship(userId, workspaceId,
				G_UserSpaceRelationshipType.CREATOR_OF)) {
			return wDao.deleteWorkspaceById(workspaceId);

		} else {
			String errorStr = "User " + userId
					+ " did not have permission to delete Workspace "
					+ workspaceId + ".";
			logger.error(errorStr);
			throw new UnauthorizedActionException(errorStr);
		}
	}

	@Override
	public boolean deleteWorkspaceIfUnused(int userId, int workspaceId)
			throws UnauthorizedActionException {
		if (wDao.hasRelationship(userId, workspaceId,
				G_UserSpaceRelationshipType.CREATOR_OF)) {
			return wDao.deleteWorkspaceIfUnused(workspaceId);

		} else {
			String errorStr = "User " + userId
					+ " did not have permission to delete Workspace "
					+ workspaceId + ".";
			logger.error(errorStr);
			throw new UnauthorizedActionException(errorStr);
		}
	}

	@Override
	public List<G_Workspace> findWorkspaces(int userId, String partialName,
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
	public G_User getByUsername(String username) {
		// TODO: Put business logic here to let admins see all users.
		return uDao.getByUsername(username);
	}

	@Override
	public G_User getUser(int userId) {
		// TODO: Add business logic to restrict this to Admins, or create other
		// methods for inquiring about read only properties of other users.
		return uDao.getById(userId);
	}

	@Override
	public G_Workspace getWorkspace(int userId, int workspaceId)
			throws UnauthorizedActionException {
		if (!ValidationUtils.isValid(userId)) {
			throw new UnauthorizedActionException(
					"The username provided was not valid");
		} else if (!ValidationUtils.isValid(workspaceId)) {
			throw new UnauthorizedActionException(
					"The workspace id provided was not valid");
		}
		if (wDao.hasRelationship(userId, workspaceId,
				G_UserSpaceRelationshipType.CREATOR_OF,
				G_UserSpaceRelationshipType.EDITOR_OF,
				G_UserSpaceRelationshipType.REVIEWER_OF)) {
			return wDao.getWorkspaceById(workspaceId);
		} else {
			String errorStr = "User " + userId
					+ " did not have permission to view Workspace "
					+ workspaceId + ".";
			logger.error(errorStr);
			throw new UnauthorizedActionException(errorStr);
		}

	}

	@Override
	public List<G_Workspace> getWorkspacesForUser(int userId) {
		// TODO: Put business logic here to let admins see all workspaces.
		return wDao.getWorkspacesForUser(userId);
	}

	@Override
	public G_User loginUser(int userId, String password)
			throws AuthenticationException {
		G_User u = uDao.loginUser(userId, password);
		if (u == null) {
			throw new AuthenticationException("Could not login user " + userId
					+ ".  Check username and password.");
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
	public boolean removeUserFromWorkspace(int userId, int workspaceId) {
		return wDao.removeUserFromWorkspace(userId, workspaceId);
	}

	@Override
	public boolean removeUserPermissionFromWorkspace(int userId,
			String permission, int workspaceId) throws AvroRemoteException {
		return wDao.removeUserPermissionFromWorkspace(userId, permission,
				workspaceId);
	}

	@Override
	public G_Group saveGroup(G_Group g) {
		gDao.save(g);
		return g;
	}

	@Override
	public G_User saveUser(G_User user) {
		user.setModified(DateTime.now().getMillis());
		return uDao.save(user);
	}

	@Override
	public G_Workspace saveWorkspace(int userId, G_Workspace workspace)
			throws UnauthorizedActionException {
		// save the workspace
		if (wDao.hasRelationship(userId, workspace.getId(),
				G_UserSpaceRelationshipType.CREATOR_OF,
				G_UserSpaceRelationshipType.EDITOR_OF)) {
			workspace.setModified(DateTime.now().getMillis());
			return wDao.save(workspace);
		} else {
			String errorStr = "User " + userId
					+ " did not have permission to save Workspace "
					+ workspace.getId() + ".";
			logger.error(errorStr);
			throw new UnauthorizedActionException(errorStr);
		}
	}

	@Override
	public boolean setUserPassword(int userId, String newPassword) {
		String hash;
		boolean success = false;
		try {
			hash = passwordHasher.createHash(newPassword);
			success = uDao.updatePassword(userId, hash);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return success;
	}

	@Override
	public List<G_Workspace> getWorkspacesOrCreateNewForUser(int userId)
			throws AvroRemoteException {
		List<G_Workspace> workspaces = getWorkspacesForUser(userId);
		if (workspaces.size() == 0) {
			G_Workspace g = createFirstWorkspaceForUser(userId);
			workspaces.add(g);
		}
		return workspaces;
	}

	@Override
	public List<G_Role> getRolesByUser(int id) throws AvroRemoteException {
		return rDao.getForUser(id);
	}

	@Override
	public List<G_Permission> getPermissionsByRole(G_Role role)
			throws AvroRemoteException {
		return pDao.getForRole(role);
	}

	@Override
	public boolean userExists(int userId) throws AvroRemoteException {
		return uDao.isExisting(userId);
	}

	@Override
	public boolean usernameExists(String username) throws AvroRemoteException {
		return uDao.isExisting(username);
	}

}
