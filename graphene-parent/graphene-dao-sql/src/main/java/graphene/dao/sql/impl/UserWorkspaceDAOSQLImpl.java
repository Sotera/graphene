package graphene.dao.sql.impl;

import graphene.business.commons.exception.DataAccessException;
import graphene.dao.UserWorkspaceDAO;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserSpaceRelationshipType;
import graphene.model.idl.G_UserWorkspace;
import graphene.model.idl.G_Workspace;

import java.util.List;

public class UserWorkspaceDAOSQLImpl implements UserWorkspaceDAO {

	@Override
	public boolean addRelationToWorkspace(final String userId, final G_UserSpaceRelationshipType rel,
			final String workspaceId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int countUsersForWorkspace(final String workspaceId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteWorkspaceRelations(final String workspaceId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<G_UserWorkspace> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_UserWorkspace getById(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserWorkspace> getByUserId(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserWorkspace> getByUserIdAndWorkspaceId(final String userId, final String workspaceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserWorkspace> getByWorkspaceId(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_Workspace> getMostRecentWorkspacesForUser(final String userId, final int quantity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_User> getUsersForWorkspace(final String workspaceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_Workspace> getWorkspacesForUser(final String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasRelationship(final String userId, final String workspaceid,
			final G_UserSpaceRelationshipType... rel) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void initialize() throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean removeUserFromWorkspace(final String userId, final String workspaceId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeUserPermissionFromWorkspace(final String userId, final String permission,
			final String workspaceId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public G_UserWorkspace save(final G_UserWorkspace g) {
		// TODO Auto-generated method stub
		return null;
	}

}
