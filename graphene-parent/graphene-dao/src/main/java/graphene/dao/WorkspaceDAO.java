package graphene.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.graph.G_PersistedGraph;
import graphene.model.idl.G_Workspace;

import java.util.List;

public interface WorkspaceDAO {

	public long countWorkspaces(String id, String partialName);

	public boolean delete(String id);

	public List<G_Workspace> getAll();

	// public G_Workspace getOrCreateWorkspace(G_Workspace g);

	public G_Workspace getById(String id);

	public G_PersistedGraph getExistingGraph(String graphSeed, String username, String timeStamp);

	public void initialize() throws DataAccessException;

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

	/**
	 * Used to persist a graph to a backend.
	 * 
	 * @param graphSeed
	 * @param username
	 * @param timeStamp
	 * @param graphJSONdata
	 * @return
	 */
	public G_PersistedGraph saveGraph(G_PersistedGraph pg);
}
