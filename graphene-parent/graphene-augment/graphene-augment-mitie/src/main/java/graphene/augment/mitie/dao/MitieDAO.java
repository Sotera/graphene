package graphene.augment.mitie.dao;

import graphene.augment.mitie.model.MitieResponse;
import graphene.business.commons.exception.DataAccessException;

public interface MitieDAO {

	public abstract MitieResponse augment(String input)
			throws DataAccessException;

}