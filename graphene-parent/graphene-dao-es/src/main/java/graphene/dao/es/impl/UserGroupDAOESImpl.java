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

import graphene.dao.GroupDAO;
import graphene.dao.UserDAO;
import graphene.dao.UserGroupDAO;
import graphene.dao.es.BasicESDAO;
import graphene.dao.es.ESRestAPIConnection;
import graphene.dao.es.JestModule;
import graphene.model.idl.G_Group;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserGroup;
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

public class UserGroupDAOESImpl extends BasicESDAO implements UserGroupDAO {
	private static final String TYPE = "usergroup";

	@Inject
	@Symbol(JestModule.ES_USERGROUP_INDEX)
	private String indexName;

	@Inject
	private UserDAO userDAO;

	@Inject
	private GroupDAO groupDAO;
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_DELETE_USER_GROUP)
	private boolean enableDelete;

	public UserGroupDAOESImpl(final ESRestAPIConnection c, final Logger logger) {
		auth = null;
		this.c = c;
		mapper = new ObjectMapper(); // can reuse, share globally
		this.logger = logger;
		setType(TYPE);
	}

	@Override
	public boolean addToGroup(final String userId, final String groupId) {
		boolean success = false;
		if (ValidationUtils.isValid(userId, groupId)) {
			final List<G_UserGroup> list = getGroupMembershipsForUserIdAndGroupId(userId, groupId);
			if (!ValidationUtils.isValid(list)) {
				final G_UserGroup ug = save(new G_UserGroup(null, groupId, userId, getModifiedTime()));
				if (ValidationUtils.isValid(ug)) {
					logger.debug("User was successfully added to group");
					success = true;
				} else {
					logger.error("Unable to add user to group, problem during save.");
				}
			} else {
				logger.debug("The user already appears to have a membership to the specified group.");
			}
		} else {
			logger.error("A valid userId and groupId are required. Was give userId: " + userId + " and groupId: "
					+ groupId);
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
	public List<G_UserGroup> getAll() {
		return getAllResults().getSourceAsObjectList(G_UserGroup.class);
	}

	@Override
	public G_UserGroup getById(final String id) {
		return getResultsById(id).getSourceAsObject(G_UserGroup.class);
	}

	@Override
	public List<G_UserGroup> getGroupMembershipsForGroupId(final String groupId) {
		final List<G_UserGroup> resultObject = getByField("groupId", groupId).getSourceAsObjectList(G_UserGroup.class);
		return resultObject;
	}

	@Override
	public List<G_UserGroup> getGroupMembershipsForUserId(final String userId) {
		final List<G_UserGroup> resultObject = getByField("userId", userId).getSourceAsObjectList(G_UserGroup.class);
		return resultObject;
	}

	@Override
	public List<G_UserGroup> getGroupMembershipsForUserIdAndGroupId(final String userId, final String groupId) {
		final List<G_UserGroup> memberships = getByJoinFields("userId", userId, "groupId", groupId)
				.getSourceAsObjectList(G_UserGroup.class);
		return memberships;
	}

	@Override
	public List<G_Group> getGroupsForUserId(final String userId) {
		final List<G_Group> returnValue = new ArrayList<G_Group>(0);
		try {
			for (final G_UserGroup r : getGroupMembershipsForUserId(userId)) {
				final G_Group foundObject = groupDAO.getById(r.getGroupId());
				if (foundObject != null) {
					returnValue.add(foundObject);
				}
			}
		} catch (final Exception e) {
			logger.error("Groups for user: " + e.getMessage());
		}
		return returnValue;
	}

	@Override
	public List<G_User> getUsersByGroupId(final String groupId) {
		final List<G_User> returnValue = new ArrayList<G_User>(3);
		final List<G_UserGroup> resultObject = getByField("groupId", groupId).getSourceAsObjectList(G_UserGroup.class);
		for (final G_UserGroup r : resultObject) {
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
	public boolean removeFromGroup(final String userId, final String groupId) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery("userId", userId))
				.must(QueryBuilders.matchQuery("groupId", groupId)));
		final Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(indexName).addType(type)
				.setParameter("timeout", defaultESTimeout).build();

		logger.debug(searchSourceBuilder.toString());
		JestResult result;
		boolean success = false;
		try {
			result = c.getClient().execute(search);
			final G_UserGroup resultObject = result.getSourceAsObject(G_UserGroup.class);
			// logger.debug(resultObject);
			success = delete(resultObject.getId());

		} catch (final Exception e) {
			logger.error("Remove from Group: " + e.getMessage());
		}
		return success;
	}

	@Override
	public G_UserGroup save(final G_UserGroup g) {
		G_UserGroup returnVal = null;
		if (ValidationUtils.isValid(g)) {
			g.setModified(getModifiedTime());
			if (g.getId() == null) {
				g.setId(saveObject(g, g.getId(), indexName, type, false));
			}
			saveObject(g, g.getId(), indexName, type, true);
			returnVal = g;
		} else {
			logger.error("Attempted to save a null user group object!");
		}
		return returnVal;
	}
}
