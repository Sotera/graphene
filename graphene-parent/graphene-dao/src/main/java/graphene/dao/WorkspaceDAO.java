package graphene.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.graph.G_PersistedGraph;
import graphene.model.idl.G_Workspace;

import java.util.List;

public interface WorkspaceDAO {

	/**
	 * Count of any workspaces that have the partial title
	 * 
	 * @param partialName
	 * @return
	 */
	public long countWorkspaces(String partialName);

	public long countWorkspaces(String id, String partialName);

	public boolean delete(String id);

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

	// public G_Workspace getOrCreateWorkspace(G_Workspace g);

	public G_Workspace getById(String id);

	/**
	 * Make sure that the creation date and modified date are set.
	 * 
	 * @param g
	 *            the workspace to save. Note that any authorization to do so is
	 *            done at a higher level.
	 * @return the workspace, with any slight modifications that the DAO might
	 *         have to do.
	 */
	public G_Workspace save(G_Workspace g);

	public void initialize() throws DataAccessException;

	/**
	 * Used to persist a graph to a backend.
	 * 
	 * @param graphSeed
	 * @param userName
	 * @param timeStamp
	 * @param graphJSONdata
	 * @return 
	 */
	public G_PersistedGraph saveGraph(G_PersistedGraph pg);

	public G_PersistedGraph getExistingGraph(String graphSeed, String userName, String timeStamp);
}
