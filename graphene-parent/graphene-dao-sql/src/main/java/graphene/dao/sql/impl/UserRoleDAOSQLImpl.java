package graphene.dao.sql.impl;

import graphene.business.commons.exception.DataAccessException;
import graphene.dao.UserRoleDAO;
import graphene.model.idl.G_Role;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserRole;

import java.util.List;

public class UserRoleDAOSQLImpl implements UserRoleDAO {

	@Override
	public boolean add(final String userId, final String roleId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean delete(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<G_UserRole> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_UserRole getById(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserRole> getByRoleId(final String roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserRole> getByUserId(final String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserRole> getByUserIdAndRoleId(final String userId, final String roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_Role> getRolesForUserId(final String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_User> getUsersForRoleId(final String roleId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean removeFromRole(final String userId, final String roleId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public G_UserRole save(final G_UserRole g) {
		// TODO Auto-generated method stub
		return null;
	}

}
