package graphene.web.model;

import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;

import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;

public class UserFilteredDataSource implements GridDataSource {
	private G_UserDataAccess userDataAccess;
	private String partialName;

	private int startIndex;
	private List<G_User> preparedResults;

	public UserFilteredDataSource(G_UserDataAccess userDataAccess, String partialName) {
		this.userDataAccess = userDataAccess;
		this.partialName = partialName;
	}

	@Override
	public int getAvailableRows() {
		try {
			return (int) userDataAccess.countUsers(partialName);
		} catch (AvroRemoteException e) {
			e.printStackTrace();
		}
		return 0;
	}

	@Override
	public void prepare(final int startIndex, final int endIndex, final List<SortConstraint> sortConstraints) {
		try {
			preparedResults = userDataAccess.getByPartialUsername(partialName, startIndex, endIndex - startIndex + 1);
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
	public Class<G_User> getRowType() {
		return G_User.class;
	}

}
