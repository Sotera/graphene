package graphene.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserSpaceRelationshipType;
import graphene.model.idl.G_UserWorkspace;
import graphene.model.idl.G_Workspace;

import java.util.List;

public interface UserWorkspaceDAO {
	/**
	 * 
	 * @param username
	 * @param rel
	 * @param workspaceId
	 * @return
	 */
	boolean addRelationToWorkspace(String userId, G_UserSpaceRelationshipType rel, String workspaceId);

	int countUsersForWorkspace(String workspaceId);

	boolean delete(String id);

	/**
	 * Usually called when deleting a workspace, we want to cascade the deletion
	 * of any relations to the deleted workspace!
	 * 
	 * @param workspaceId
	 */
	boolean deleteWorkspaceRelations(String workspaceId);

	List<G_UserWorkspace> getAll();

	G_UserWorkspace getById(String id);

	List<G_UserWorkspace> getByUserId(String id);

	List<G_UserWorkspace> getByUserIdAndWorkspaceId(String userId, String workspaceId);

	List<G_UserWorkspace> getByWorkspaceId(String id);

	List<G_Workspace> getMostRecentWorkspacesForUser(String userId, int quantity);

	List<G_User> getUsersForWorkspace(String workspaceId);

	List<G_Workspace> getWorkspacesForUser(String userId);

	/**
	 * Return true if the workspace has any of the provided relations to the
	 * user. This is used to verify that the user has the right to save, update,
	 * delete or view the workspace.
	 * 
	 * @param username
	 * @param workspaceid
	 * @param rel
	 * @return true if the user had one or more of the rels to the workspace
	 */
	boolean hasRelationship(String userId, String workspaceid, G_UserSpaceRelationshipType... rel);

	void initialize() throws DataAccessException;

	boolean removeUserFromWorkspace(String userId, String workspaceId);

	boolean removeUserPermissionFromWorkspace(String userId, String permission, String workspaceId);

	G_UserWorkspace save(G_UserWorkspace g);
}
