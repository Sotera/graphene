package graphene.services;

import graphene.dao.GroupDAO;
import graphene.dao.PermissionDAO;
import graphene.dao.RoleDAO;
import graphene.dao.UserDAO;
import graphene.dao.UserGroupDAO;
import graphene.dao.UserWorkspaceDAO;
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
	private WorkspaceDAO wDao;

	@Override
	public G_Workspace addNewWorkspaceForUser(final String userId,
			final G_Workspace workspace) {
		// save the workspace
		try {
			final G_Workspace w = wDao.save(workspace);
			if (w == null) {
				logger.error("Could not create new workspace");
			} else {
				final boolean success = uwDao.addRelationToWorkspace(userId,
						G_UserSpaceRelationshipType.EDITOR_OF,
						workspace.getId());
				if (!success) {
					logger.error("Could not create editor relationship for workspace");
				}
			}

		} catch (final Exception e) {
			e.printStackTrace();
			logger.error("Could not create editor relationship for workspace "
					+ e.getMessage());
		}
		return workspace;
	}

	@Override
	public int countUsers(final String partialName) {
		// TODO: Put business logic here to let admins see all users.
		final long lcount = uDao.countUsers(partialName);
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
	public int countWorkspaces(final String userId,
			final String partialWorkspaceName) {
		// TODO: Put business logic here to let admins see all workspaces.
		final long lcount = wDao.countWorkspaces(userId, partialWorkspaceName);
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
	public G_Workspace createFirstWorkspaceForUser(final String userId) {
		G_Workspace w = createTempWorkspaceForUser(userId);
		w.setTitle("First Workspace");
		w = wDao.save(w);
		if (ValidationUtils.isValid(w)) {
			uwDao.addRelationToWorkspace(userId,
					G_UserSpaceRelationshipType.CREATOR_OF, w.getId());
			uwDao.addRelationToWorkspace(userId,
					G_UserSpaceRelationshipType.EDITOR_OF, w.getId());
		}
		return w;
	}

	@Override
	public G_Workspace createTempWorkspaceForUser(final String userId) {

		final G_User user = getUser(userId);
		final DateTime time = DateTime.now();
		final G_Workspace w = new G_Workspace();
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
	public boolean deleteUser(final String userId) {
		return uDao.delete(userId);
	}

	@Override
	public boolean deleteWorkspace(final String userId, final String workspaceId)
			throws UnauthorizedActionException {
		if (uwDao.hasRelationship(userId, workspaceId,
				G_UserSpaceRelationshipType.CREATOR_OF)) {
			return wDao.delete(workspaceId);

		} else {
			final String errorStr = "User " + userId
					+ " did not have permission to delete Workspace "
					+ workspaceId + ".";
			logger.error(errorStr);
			throw new UnauthorizedActionException(errorStr);
		}
	}

	@Override
	public boolean deleteWorkspaceIfUnused(final String userId,
			final String workspaceId) throws UnauthorizedActionException {
		if (uwDao.hasRelationship(userId, workspaceId,
				G_UserSpaceRelationshipType.CREATOR_OF)) {
			wDao.delete(workspaceId);
			uwDao.deleteWorkspaceRelations(workspaceId);
			return true;
		} else {
			final String errorStr = "User " + userId
					+ " did not have permission to delete Workspace "
					+ workspaceId + ".";
			logger.error(errorStr);
			throw new UnauthorizedActionException(errorStr);
		}
	}

	@Override
	public List<G_Workspace> findWorkspaces(final String userId,
			final String partialName, final int offset, final int limit) {
		// TODO: Put business logic here to let admins see all workspaces.
		return uwDao.getWorkspacesForUser(userId);
	}

	@Override
	public List<G_User> getByPartialUsername(final String partialName,
			final int offset, final int limit) {
		// TODO: Put business logic here to let admins see all users.
		return uDao.getByPartialUsername(partialName, offset, limit);
	}

	@Override
	public G_User getByUsername(final String username) {
		// TODO: Put business logic here to let admins see all users.
		return uDao.getByUsername(username);
	}

	@Override
	public List<G_Permission> getPermissionsByRole(final G_Role role)
			throws AvroRemoteException {
		return pDao.getForRole(role);
	}

	@Override
	public List<G_Role> getRolesByUser(final String id)
			throws AvroRemoteException {
		return rDao.getForUser(id);
	}

	@Override
	public G_User getUser(final String userId) {
		// TODO: Add business logic to restrict this to Admins, or create other
		// methods for inquiring about read only properties of other users.
		return uDao.getById(userId);
	}

	@Override
	public G_Workspace getWorkspace(final String userId,
			final String workspaceId) throws UnauthorizedActionException {
		if (!ValidationUtils.isValid(userId)) {
			throw new UnauthorizedActionException(
					"The username provided was not valid");
		} else if (!ValidationUtils.isValid(workspaceId)) {
			throw new UnauthorizedActionException(
					"The workspace id provided was not valid");
		}
		if (uwDao.hasRelationship(userId, workspaceId,
				G_UserSpaceRelationshipType.CREATOR_OF,
				G_UserSpaceRelationshipType.EDITOR_OF,
				G_UserSpaceRelationshipType.REVIEWER_OF)) {
			return wDao.getById(workspaceId);
		} else {
			final String errorStr = "User " + userId
					+ " did not have permission to view Workspace "
					+ workspaceId + ".";
			logger.error(errorStr);
			throw new UnauthorizedActionException(errorStr);
		}

	}

	@Override
	public List<G_Workspace> getWorkspacesForUser(final String userId) {
		// TODO: Put business logic here to let admins see all workspaces.
		return uwDao.getWorkspacesForUser(userId);
	}

	@Override
	public List<G_Workspace> getWorkspacesOrCreateNewForUser(final String userId)
			throws AvroRemoteException {
		final List<G_Workspace> workspaces = getWorkspacesForUser(userId);
		if (workspaces.size() == 0) {
			logger.debug("No workspaces found for userId " + userId
					+ ". Creating a new one.");
			final G_Workspace g = createFirstWorkspaceForUser(userId);
			workspaces.add(g);
		}
		return workspaces;
	}

	@Override
	public G_User loginAuthenticatedUser(final String userId)
			throws AvroRemoteException, AuthenticationException {
		return uDao.loginAuthenticatedUser(userId);
	}

	@Override
	public G_User loginUser(final String userId, final String password)
			throws AuthenticationException {
		final G_User u = uDao.loginUser(userId, password);
		if (u == null) {
			throw new AuthenticationException("Could not login user " + userId
					+ ".  Check username and password.");
		}
		return u;
	}

	@Override
	public G_User registerUser(G_User d, final String password,
			final boolean createWorkspace) throws AvroRemoteException {
		// Perform any business logic
		if (ValidationUtils.isValid(d)) {
			if (!ValidationUtils.isValid(d.getAvatar())) {
				d.setAvatar("unknown.png");
			}
			try {
				final String hash = passwordHasher.createHash(password);
				d.setHashedpassword(hash);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				logger.error("Could not store hashed password for new user registration");
			}
			d.setActive(true);
			d.setLastlogin(0l);
			d.setNumberlogins(0);
			d = uDao.save(d);
			if (ValidationUtils.isValid(d)) {
				logger.debug("User registered!");
			} else {
				logger.error("Error registering new user!");
			}
		} else {
			logger.error("Could not register null user!");
		}

		return d;
	}

	@Override
	public boolean removeUserFromWorkspace(final String userId,
			final String workspaceId) {
		return uwDao.removeUserFromWorkspace(userId, workspaceId);
	}

	@Override
	public boolean removeUserPermissionFromWorkspace(final String userId,
			final String permission, final String workspaceId)
			throws AvroRemoteException {
		final boolean success = uwDao.removeUserPermissionFromWorkspace(userId,
				permission, workspaceId);
		if (success) {
			if (uwDao.countUsersForWorkspace(workspaceId) == 0) {
				wDao.delete(workspaceId);
			}
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
	public G_Workspace saveWorkspace(final String userId,
			final G_Workspace workspace) throws UnauthorizedActionException {
		// save the workspace
		if (uwDao.hasRelationship(userId, workspace.getId(),
				G_UserSpaceRelationshipType.CREATOR_OF,
				G_UserSpaceRelationshipType.EDITOR_OF)) {
			workspace.setModified(DateTime.now().getMillis());
			return wDao.save(workspace);
		} else {
			final String errorStr = "User " + userId
					+ " did not have permission to save Workspace "
					+ workspace.getId() + ".";
			logger.error(errorStr);
			throw new UnauthorizedActionException(errorStr);
		}
	}

	@Override
	public boolean setUserPassword(final String userId, final String newPassword) {
		String hash;
		boolean success = false;
		try {
			hash = passwordHasher.createHash(newPassword);
			success = uDao.updatePasswordHash(userId, hash);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (!success) {
			logger.error("Problem saving password hash");
		}
		return success;
	}

	@Override
	public boolean userExists(final String userId) throws AvroRemoteException {
		return uDao.isExisting(userId);
	}

	@Override
	public boolean usernameExists(final String username)
			throws AvroRemoteException {
		final boolean exists = uDao.isExisting(username);
		if (exists) {
			logger.warn("Username already exists");
		} else {
			logger.info("Username has not been taken");
		}
		return exists;
	}

}
