package graphene.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.G_Group;

import java.util.List;

public interface GroupDAO {
	public G_Group createGroup(G_Group g);

	public boolean delete(String id);

	public List<G_Group> getAll();

	public G_Group getById(String id);

	public G_Group getGroupByGroupname(String groupname);

	public void initialize() throws DataAccessException;

	public G_Group save(G_Group g);

}
