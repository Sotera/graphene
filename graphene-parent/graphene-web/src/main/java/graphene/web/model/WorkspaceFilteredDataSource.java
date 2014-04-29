package graphene.web.model;

import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_Workspace;

import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;

public class WorkspaceFilteredDataSource implements GridDataSource {
	private G_UserDataAccess service;
	private String partialName;

	private int startIndex;
	private List<G_Workspace> preparedResults;
	private String userId;

	public WorkspaceFilteredDataSource(G_UserDataAccess workspaceFinderService,
			String userId, String partialName) {
		this.service = workspaceFinderService;
		this.userId = userId;
		this.partialName = partialName;
	}

	@Override
	public int getAvailableRows() {
		try {
			return service.countWorkspaces(userId, partialName);
		} catch (AvroRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return startIndex;
		
	}

	@Override
	public void prepare(final int startIndex, final int endIndex,
			final List<SortConstraint> sortConstraints) {

		try {
			preparedResults = service.findWorkspaces(userId, partialName,
					startIndex, endIndex - startIndex + 1);
		} catch (AvroRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		this.startIndex = startIndex;
	}

	@Override
	public Object getRowValue(final int index) {
		return preparedResults.get(index - startIndex);
	}

	@Override
	public Class<G_Workspace> getRowType() {
		return G_Workspace.class;
	}

}
