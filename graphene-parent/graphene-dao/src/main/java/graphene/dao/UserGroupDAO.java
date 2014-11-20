package graphene.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.G_Group;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserGroup;

import java.util.List;

public interface UserGroupDAO {

	public boolean addToGroup(String username, String groupname);

	public List<G_User> getUsersByGroup(String groupName);

	public List<G_Group> getGroupsForUser(String username);

	public boolean removeFromGroup(String userId, String groupId);

	public void initialize()  throws DataAccessException;

	public G_UserGroup save(G_UserGroup g);
}
