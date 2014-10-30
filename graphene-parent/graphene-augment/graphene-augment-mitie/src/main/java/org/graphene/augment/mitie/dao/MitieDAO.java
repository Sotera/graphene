package org.graphene.augment.mitie.dao;

import graphene.business.commons.exception.DataAccessException;

import org.graphene.augment.mitie.model.MitieResponse;

public interface MitieDAO {

	public abstract MitieResponse augment(String input)
			throws DataAccessException;

}