package graphene.dao;

import graphene.model.idl.G_UserSpaceRelationshipType;
import graphene.model.idl.G_Workspace;

import java.util.List;

public interface WorkspaceDAO {
	public G_Workspace addNewWorkspace(int userId, G_Workspace w);

	/**
	 * TODO: Consider changing this to a string based relation/permission, to
	 * remove tie to Neo4J
	 * 
	 * @param username
	 * @param rel
	 * @param workspaceid
	 * @return
	 */
	public boolean addRelationToWorkspace(int id,
			G_UserSpaceRelationshipType rel, int workspaceid);

	public long countWorkspaces(String partialName);

	public long countWorkspaces(int id, String partialName);

	public boolean deleteWorkspaceById(int id);

	public boolean deleteWorkspaceIfUnused(int id);

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
	public List<G_Workspace> findWorkspaces(int userId, String partialName,
			int offset, int limit);

	public List<G_Workspace> getAllWorkspaces();

	public G_Workspace getOrCreateWorkspace(G_Workspace g);

	public G_Workspace getWorkspaceById(int id);

	public List<G_Workspace> getWorkspacesForUser(int userId);

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
	public boolean hasRelationship(int userId, int workspaceid,
			G_UserSpaceRelationshipType... rel);

	public boolean removeUserFromWorkspace(int userId, int workspaceId);

	public boolean removeUserPermissionFromWorkspace(int userId,
			String permission, int workspaceId);

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
