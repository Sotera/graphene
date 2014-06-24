package graphene.dao;

import graphene.model.idl.G_Role;

import java.util.List;

public interface RoleDAO {
	List<G_Role> getForUsername(String username);
}
