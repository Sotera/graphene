package graphene.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.G_Role;

import java.util.List;

public interface RoleDAO {
	G_Role create(G_Role g_Role);

	boolean delete(String id);

	List<G_Role> getAll();

	G_Role getById(String id);

	G_Role getRoleByRolename(String groupname);

	void initialize() throws DataAccessException;

	G_Role save(G_Role g);

}
