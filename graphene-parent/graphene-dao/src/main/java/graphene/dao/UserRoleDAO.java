package graphene.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.G_Role;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserRole;

import java.util.List;

public interface UserRoleDAO {
	public boolean add(String userId, String roleId);

	public boolean delete(String id);

	public List<G_UserRole> getAll();

	public G_UserRole getById(String id);

	public abstract List<G_UserRole> getByRoleId(String roleId);

	public List<G_UserRole> getByUserId(String userId);

	public abstract List<G_UserRole> getByUserIdAndRoleId(final String userId, final String roleId);

	public List<G_Role> getRolesForUserId(String userId);

	public List<G_User> getUsersForRoleId(String roleId);

	public void initialize() throws DataAccessException;

	public boolean removeFromRole(String userId, String roleId);

	public G_UserRole save(G_UserRole g);
}
