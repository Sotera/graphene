package graphene.dao.sql.impl;

import graphene.business.commons.exception.DataAccessException;
import graphene.dao.UserDAO;
import graphene.model.idl.AuthenticationException;
import graphene.model.idl.G_User;

import java.util.List;

public class UserDAOSQLImpl implements UserDAO {

	@Override
	public long countUsers(final String partialName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean disable(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean enable(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<G_User> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_User getById(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_User getByUsername(final String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPasswordHash(final String id, final String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isExistingId(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistingUsername(final String username) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public G_User loginAuthenticatedUser(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_User loginUser(final String id, final String password) throws AuthenticationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_User save(final G_User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updatePasswordHash(final String id, final String passwordHash) {
		// TODO Auto-generated method stub
		return false;
	}

}
