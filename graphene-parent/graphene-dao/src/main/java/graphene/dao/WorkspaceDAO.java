package graphene.dao;

import graphene.model.idl.G_UserSpaceRelationshipType;
import graphene.model.idl.G_Workspace;

import java.util.List;

public interface WorkspaceDAO {
	public G_Workspace addNewWorkspace(String username, G_Workspace w);

	/**
	 * TODO: Consider changing this to a string based relation/permission, to remove tie to Neo4J
	 * @param username
	 * @param rel
	 * @param workspaceid
	 * @return
	 */
	public boolean addRelationToWorkspace(String username,
			G_UserSpaceRelationshipType rel, String workspaceid);

	public long countWorkspaces(String partialName);

	public long countWorkspaces(String userId, String partialName);

	public boolean deleteWorkspaceById(String workspaceId);

	public boolean deleteWorkspaceIfUnused(String workspaceId);

	/**
	 * Find workspaces by partial name of the title
	 * 
	 * @param partialName
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<G_Workspace> findWorkspaces(String partialName, int offset,
			int limit);

	/**
	 * Find workspaces by partial name where the userid is an editor of the
	 * workspace.
	 * 
	 * @param userId
	 * @param partialName
	 * @param offset
	 * @param limit
	 * @return
	 */
	public List<G_Workspace> findWorkspaces(String userId, String partialName,
			int offset, int limit);

	public List<G_Workspace> getAllWorkspaces();

	public G_Workspace getOrCreateWorkspace(G_Workspace g);

	public G_Workspace getWorkspaceById(String id);

	public List<G_Workspace> getWorkspacesForUser(String username);

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
	public boolean hasRelationship(String username, String workspaceid,
			G_UserSpaceRelationshipType... rel);

	public boolean removeUserFromWorkspace(String username, String workspaceId);

	public boolean removeUserPermissionFromWorkspace(String username,
			String permission, String workspaceId);

	/**
	 * 
	 * @param g
	 *            the workspace to save. Note that any authorization to do so is
	 *            done at a higher level.
	 * @return the workspace, with any slight modifications that the DAO might
	 *         have to do.
	 */
	public G_Workspace save(G_Workspace g);

}
