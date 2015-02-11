package graphene.web.model;

import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;

import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.grid.SortConstraint;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class UserFilteredDataSource implements GridDataSource {
	private final G_UserDataAccess userDataAccess;
	private final String partialName;
	@Inject
	private Logger logger;
	private int startIndex;
	private List<G_User> preparedResults;

	public UserFilteredDataSource(final G_UserDataAccess userDataAccess, final String partialName) {
		this.userDataAccess = userDataAccess;
		this.partialName = partialName;
	}

	@Override
	public int getAvailableRows() {
		try {
			return userDataAccess.countUsers(partialName);
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		return 0;
	}

	@Override
	public Class<G_User> getRowType() {
		return G_User.class;
	}

	@Override
	public Object getRowValue(final int index) {
		return preparedResults.get(index - startIndex);
	}

	@Override
	public void prepare(final int startIndex, final int endIndex, final List<SortConstraint> sortConstraints) {
		try {
			preparedResults = userDataAccess.getByPartialUsername(partialName, startIndex, (endIndex - startIndex) + 1);
		} catch (final AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		this.startIndex = startIndex;
	}

}
