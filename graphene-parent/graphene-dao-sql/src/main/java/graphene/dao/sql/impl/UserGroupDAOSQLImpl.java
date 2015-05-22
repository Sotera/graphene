package graphene.dao.sql.impl;

import graphene.business.commons.exception.DataAccessException;
import graphene.dao.UserGroupDAO;
import graphene.model.idl.G_Group;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserGroup;

import java.util.List;

public class UserGroupDAOSQLImpl implements UserGroupDAO {

	@Override
	public boolean addToGroup(final String userId, final String groupId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<G_UserGroup> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_UserGroup getById(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserGroup> getGroupMembershipsForGroupId(final String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserGroup> getGroupMembershipsForUserId(final String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserGroup> getGroupMembershipsForUserIdAndGroupId(final String userId, final String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_Group> getGroupsForUserId(final String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_User> getUsersByGroupId(final String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean removeFromGroup(final String userId, final String groupId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public G_UserGroup save(final G_UserGroup g) {
		// TODO Auto-generated method stub
		return null;
	}

}
