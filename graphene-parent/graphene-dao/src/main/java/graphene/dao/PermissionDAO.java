package graphene.dao;

import graphene.model.idl.G_Permission;
import graphene.model.idl.G_Role;

import java.util.List;

public interface PermissionDAO {
	List<G_Permission> getForRole(G_Role role);
	List<G_Permission> getForRole(String role);
}
