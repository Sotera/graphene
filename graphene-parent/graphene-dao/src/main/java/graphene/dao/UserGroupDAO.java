package graphene.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.G_Group;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserGroup;

import java.util.List;

public interface UserGroupDAO {
	public boolean addToGroup(String userId, String groupId);

	public boolean delete(String id);

	public abstract List<G_UserGroup> getGroupMembershipsForGroupId(String groupId);

	public List<G_UserGroup> getGroupMembershipsForUserId(String userId);

	public abstract List<G_UserGroup> getGroupMembershipsForUserIdAndGroupId(final String userId, final String groupId);

	public List<G_Group> getGroupsForUserId(String userId);

	public List<G_User> getUsersByGroupId(String groupId);

	public void initialize() throws DataAccessException;

	public boolean removeFromGroup(String userId, String groupId);

	public G_UserGroup save(G_UserGroup g);
}
