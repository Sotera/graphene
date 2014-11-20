package graphene.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.G_Group;

import java.util.List;

public interface GroupDAO {
	public G_Group createGroup(G_Group g);

	public G_Group save(G_Group g);

	public void deleteGroup(G_Group g);

	public G_Group getGroupByGroupname(String groupname);

	public G_Group getGroupById(String id);

	public List<G_Group> getAllGroups();

	public void initialize() throws DataAccessException;

}
