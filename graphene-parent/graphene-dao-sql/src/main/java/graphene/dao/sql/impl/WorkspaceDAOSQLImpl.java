package graphene.dao.sql.impl;

import graphene.business.commons.exception.DataAccessException;
import graphene.dao.WorkspaceDAO;
import graphene.model.graph.G_PersistedGraph;
import graphene.model.idl.G_Workspace;

import java.util.List;

public class WorkspaceDAOSQLImpl implements WorkspaceDAO {

	@Override
	public long countWorkspaces(final String id, final String partialName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<G_Workspace> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_Workspace getById(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_PersistedGraph getExistingGraph(final String graphSeed, final String username, final String timeStamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public G_Workspace save(final G_Workspace g) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_PersistedGraph saveGraph(final G_PersistedGraph pg) {
		// TODO Auto-generated method stub
		return null;
	}

}
