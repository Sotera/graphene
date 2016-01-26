/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.dao.es.impl;

import graphene.dao.RoleDAO;
import graphene.dao.UserDAO;
import graphene.dao.UserRoleDAO;
import graphene.dao.es.BasicESDAO;
import graphene.dao.es.ESRestAPIConnection;
import graphene.dao.es.JestModule;
import graphene.model.idl.G_Role;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserRole;
import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestResult;
import io.searchbox.core.Search;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserRoleDAOESImpl extends BasicESDAO implements UserRoleDAO {
	private static final String TYPE = "userrole";

	@Inject
	@Symbol(JestModule.ES_USERROLE_INDEX)
	private String indexName;

	@Inject
	private UserDAO userDAO;

	@Inject
	private RoleDAO roleDAO;
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_DELETE_USER_ROLE)
	private boolean enableDelete;

	public UserRoleDAOESImpl(final ESRestAPIConnection c, final Logger logger) {
		auth = null;
		this.c = c;
		mapper = new ObjectMapper(); // can reuse, share globally
		this.logger = logger;
		setType(TYPE);
	}

	@Override
	public boolean add(final String userId, final String roleId) {
		boolean success = false;
		if (ValidationUtils.isValid(userId, roleId)) {
			final List<G_UserRole> list = getByUserIdAndRoleId(userId, roleId);
			if (!ValidationUtils.isValid(list)) {
				final G_UserRole ug = save(new G_UserRole(null, roleId, userId, getModifiedTime()));
				if (ValidationUtils.isValid(ug)) {
					logger.debug("User " + userId + " was successfully added to role " + roleId);
					success = true;
				} else {
					logger.error("Unable to add user to role, problem during save.");
				}
			} else {
				logger.debug("The user already appears to have a membership to the specified role.");
			}
		} else {
			logger.error("add(): A valid userId and roleId are required. Was given userId: " + userId + " and roleId: "
					+ roleId);
		}
		return success;
	}

	@Override
	public boolean delete(final String id) {
		if (enableDelete) {
			return super.delete(id);
		} else {
			logger.debug("Delete disabled.");
			return false;
		}
	}

	@Override
	public List<G_UserRole> getAll() {
		return getAllResults().getSourceAsObjectList(G_UserRole.class);
	}

	@Override
	public G_UserRole getById(final String id) {
		return getResultsById(id).getSourceAsObject(G_UserRole.class);
	}

	@Override
	public List<G_UserRole> getByRoleId(final String roleId) {
		return getByField("roleId", roleId).getSourceAsObjectList(G_UserRole.class);
	}

	@Override
	public List<G_UserRole> getByUserId(final String userId) {
		return getByField("userId", userId).getSourceAsObjectList(G_UserRole.class);
	}

	@Override
	public List<G_UserRole> getByUserIdAndRoleId(final String userId, final String roleId) {
		final List<G_UserRole> memberships = getByJoinFields("userId", userId, "roleId", roleId).getSourceAsObjectList(
				G_UserRole.class);
		return memberships;
	}

	@Override
	public List<G_Role> getRolesForUserId(final String userId) {
		final List<G_Role> returnValue = new ArrayList<G_Role>(0);
		for (final G_UserRole r : getByUserId(userId)) {
			final G_Role foundObject = roleDAO.getById(r.getRoleId());
			if (foundObject != null) {
				returnValue.add(foundObject);
				// logger.debug("User " + userId + " has role " +
				// foundObject.getName());
			}
		}
		return returnValue;
	}

	@Override
	public List<G_User> getUsersForRoleId(final String roleId) {
		final List<G_User> returnValue = new ArrayList<G_User>(0);
		for (final G_UserRole r : getByRoleId(roleId)) {
			final G_User foundObject = userDAO.getById(r.getUserId());
			if (foundObject != null) {
				returnValue.add(foundObject);
			}
		}
		return returnValue;
	}

	@Override
	@PostInjection
	public void initialize() {
		setIndex(indexName);
		setType(TYPE);
		super.initialize();
	}

	@Override
	public boolean removeFromRole(final String userId, final String roleId) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("userId", userId))
				.must(QueryBuilders.matchQuery("roleId", roleId)));
		final Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(indexName).addType(type)
				.setParameter("timeout", defaultESTimeout).build();

		logger.debug(searchSourceBuilder.toString());
		JestResult result;
		boolean success = false;
		try {
			result = c.getClient().execute(search);
			final G_UserRole resultObject = result.getSourceAsObject(G_UserRole.class);
			// logger.debug(resultObject);
			success = delete(resultObject.getId());

		} catch (final Exception e) {
			logger.error("Remove from Role: " + e.getMessage());
		}
		return success;
	}

	@Override
	public G_UserRole save(final G_UserRole g) {
		G_UserRole returnVal = null;
		if (ValidationUtils.isValid(g)) {
			g.setModified(getModifiedTime());
			if (g.getId() == null) {
				g.setId(saveObject(g, g.getId(), indexName, type, false));
			}
			saveObject(g, g.getId(), indexName, type, true);
			returnVal = g;
		} else {
			logger.error("Attempted to save a null user role object!");
		}
		return returnVal;
	}
}
