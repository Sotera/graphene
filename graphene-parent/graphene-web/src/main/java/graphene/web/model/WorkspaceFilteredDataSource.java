package graphene.web.model;

import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_Workspace;

import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class WorkspaceFilteredDataSource implements GridDataSource {
	private final G_UserDataAccess userDataAccess;
	private final String partialName;
	@Inject
	private Logger logger;
	private int startIndex;
	private List<G_Workspace> preparedResults;
	private final String userId;

	public WorkspaceFilteredDataSource(final G_UserDataAccess userDataAccess, final String userId,
			final String partialName) {
		this.userDataAccess = userDataAccess;
		this.userId = userId;
		this.partialName = partialName;
	}

	@Override
	public int getAvailableRows() {
		try {
			return userDataAccess.countWorkspaces(userId, partialName);
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		return startIndex;

	}

	@Override
	public Class<G_Workspace> getRowType() {
		return G_Workspace.class;
	}

	@Override
	public Object getRowValue(final int index) {
		return preparedResults.get(index - startIndex);
	}

	@Override
	public void prepare(final int startIndex, final int endIndex, final List<SortConstraint> sortConstraints) {

		try {
			preparedResults = userDataAccess.findWorkspaces(userId, partialName, startIndex,
					(endIndex - startIndex) + 1);
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}

		this.startIndex = startIndex;
	}

}
