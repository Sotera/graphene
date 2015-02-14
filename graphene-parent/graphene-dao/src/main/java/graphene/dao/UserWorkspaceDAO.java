package graphene.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserSpaceRelationshipType;
import graphene.model.idl.G_UserWorkspace;
import graphene.model.idl.G_Workspace;

import java.util.List;

public interface UserWorkspaceDAO {
	/**
	 * TODO: Consider changing this to a string based relation/permission, to
	 * remove tie to Neo4J
	 * 
	 * @param username
	 * @param rel
	 * @param workspaceId
	 * @return
	 */
	public boolean addRelationToWorkspace(String userId, G_UserSpaceRelationshipType rel, String workspaceId);

	public int countUsersForWorkspace(String workspaceId);

	public boolean delete(String id);

	/**
	 * Usually called when deleting a workspace, we want to cascade the deletion
	 * of any relations to the deleted workspace!
	 * 
	 * @param workspaceId
	 */
	public boolean deleteWorkspaceRelations(String workspaceId);

	public List<G_UserWorkspace> getAllWorkspaceRelations();

	public List<G_User> getUsersForWorkspace(String workspaceId);

	public List<G_Workspace> getWorkspacesForUser(String userId);

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
	public boolean hasRelationship(String userId, String workspaceid, G_UserSpaceRelationshipType... rel);

	public void initialize() throws DataAccessException;

	public boolean removeUserFromWorkspace(String userId, String workspaceId);

	public boolean removeUserPermissionFromWorkspace(String userId, String permission, String workspaceId);

	public G_UserWorkspace save(G_UserWorkspace g);
}
