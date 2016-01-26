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

import graphene.dao.UserDAO;
import graphene.dao.UserWorkspaceDAO;
import graphene.dao.WorkspaceDAO;
import graphene.dao.es.BasicESDAO;
import graphene.dao.es.ESRestAPIConnection;
import graphene.dao.es.JestModule;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserSpaceRelationshipType;
import graphene.model.idl.G_UserWorkspace;
import graphene.model.idl.G_Workspace;
import graphene.util.validator.ValidationUtils;
import io.searchbox.core.Count;
import io.searchbox.core.CountResult;
import io.searchbox.core.Delete;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserWorkspaceDAOESImpl extends BasicESDAO implements UserWorkspaceDAO {
	private static final String TYPE = "userworkspace";
	@Inject
	@Symbol(JestModule.ES_USERWORKSPACE_INDEX)
	private String indexName;
	@Inject
	private UserDAO userDAO;
	@Inject
	private WorkspaceDAO workspaceDAO;
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_DELETE_USER_WORKSPACES)
	private boolean enableDelete;

	public UserWorkspaceDAOESImpl(final ESRestAPIConnection c, final Logger logger) {
		auth = null;
		this.c = c;
		mapper = new ObjectMapper(); // can reuse, share globally
		this.logger = logger;
		setType(TYPE);
	}

	@Override
	public boolean addRelationToWorkspace(final String userId, final G_UserSpaceRelationshipType rel,
			final String workspaceid) {
		final G_UserWorkspace ug = save(new G_UserWorkspace(null, workspaceid, userId, getModifiedTime(), rel));

		if (ug != null) {
			logger.debug("Added user " + userId + " as " + rel.name() + " of workspace " + workspaceid);
			return true;
		} else {
			logger.error("Could not create relationship for user " + userId + " as " + rel.name() + " of workspace "
					+ workspaceid);
			return false;
		}
	}

	@Override
	public int countUsersForWorkspace(final String workspaceId) {
		final String query = new SearchSourceBuilder().query(QueryBuilders.matchQuery("id", workspaceId)).toString();
		try {
			final CountResult result = c.getClient().execute(
					new Count.Builder().query(query).addIndex(indexName).addType(type)
							.setParameter("timeout", defaultESTimeout).build());
			return (int) result.getCount().longValue();
		} catch (final Exception e) {
			logger.error("Count users for workspace: " + e.getMessage());
		}
		return 0;
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
	public boolean deleteWorkspaceRelations(final String workspaceId) {
		boolean success = false;
		try {
			c.getClient().execute(
					(new Delete.Builder(QueryBuilders.matchQuery("workspaceId", workspaceId).toString()))
							.index(getIndex()).type(type).setParameter("timeout", defaultESTimeout).build());
			success = true;
		} catch (final Exception e) {
			logger.error("Delete workspace relations: " + e.getMessage());
		}
		return success;
	}

	@Override
	public List<G_UserWorkspace> getAll() {
		return getAllResults().getSourceAsObjectList(G_UserWorkspace.class);
	}

	@Override
	public G_UserWorkspace getById(final String id) {
		return getResultsById(id).getSourceAsObject(G_UserWorkspace.class);
	}

	@Override
	public List<G_UserWorkspace> getByUserId(final String id) {
		return getByField("userId", id).getSourceAsObjectList(G_UserWorkspace.class);
	}

	@Override
	public List<G_UserWorkspace> getByUserIdAndWorkspaceId(final String userId, final String workspaceId) {
		final List<G_UserWorkspace> memberships = getByJoinFields("userId", userId, "workspaceId", workspaceId)
				.getSourceAsObjectList(G_UserWorkspace.class);
		return memberships;
	}

	@Override
	public List<G_UserWorkspace> getByWorkspaceId(final String id) {
		return getByField("workspaceId", id).getSourceAsObjectList(G_UserWorkspace.class);
	}

	@Override
	public List<G_Workspace> getMostRecentWorkspacesForUser(final String userId, final int quantity) {
		if (quantity == 0) {
			return new ArrayList<G_Workspace>();
		}
		final List<G_Workspace> returnValue = getWorkspacesForUser(userId);

		Collections.sort(returnValue, new Comparator<G_Workspace>() {
			@Override
			public int compare(final G_Workspace o1, final G_Workspace o2) {
				return o2.getModified().compareTo(o1.getModified());
			}
		});

		if (returnValue.size() < quantity) {
			return returnValue;
		} else {
			return returnValue.subList(0, quantity);
		}
	}

	@Override
	public List<G_User> getUsersForWorkspace(final String workspaceId) {

		final List<G_User> returnValue = new ArrayList<G_User>(0);
		for (final G_UserWorkspace r : getByWorkspaceId(workspaceId)) {
			final G_User foundObject = userDAO.getById(r.getUserId());
			if (foundObject != null) {
				returnValue.add(foundObject);
			}
		}
		return returnValue;
	}

	@Override
	public List<G_Workspace> getWorkspacesForUser(final String userId) {
		final List<G_Workspace> returnValue = new ArrayList<G_Workspace>(0);

		/*
		 * The reason for this is because we may have several relationships to
		 * the same workspace, but we only want to return the unique set of
		 * workspaces (no duplicates). So collect the workspace ids in a set
		 * first.
		 */
		final Set<String> workspaceIds = new HashSet<String>();

        List<G_UserWorkspace> ws = getByUserId(userId);
		for (final G_UserWorkspace r : ws) {
			workspaceIds.add(r.getWorkspaceId());
		}
		if (workspaceIds.isEmpty()) {
			logger.warn("User " + userId + " does not appear to have any workspaces.  This may be ok.");
		} else {
			for (final String id : workspaceIds) {
				final G_Workspace foundObject = workspaceDAO.getById(id);
				if (foundObject != null) {
					returnValue.add(foundObject);
				}
			}
		}
		return returnValue;
	}

	@Override
	public boolean hasRelationship(final String userId, final String workspaceId,
			final G_UserSpaceRelationshipType... relations) {
		boolean success = false;
		final List<G_UserWorkspace> resultObject = getByUserIdAndWorkspaceId(userId, workspaceId);
		for (final G_UserWorkspace r : resultObject) {
			for (final G_UserSpaceRelationshipType relation : relations) {
				if (r.getRole().equals(relation)) {
					success = true;
				}
			}
		}

		return success;
	}

	@Override
	@PostInjection
	public void initialize() {
		setIndex(indexName);
		setType(TYPE);
		super.initialize();
	}

	@Override
	public boolean removeUserFromWorkspace(final String userId, final String workspaceId) {
		boolean success = false;
		for (final G_UserWorkspace r : getByUserIdAndWorkspaceId(userId, workspaceId)) {
			// remove each user binding that matched, should only be one or
			// two.
			// FIXME: There is a possible bug here if the id was not valid.
			logger.debug("Deleting user-workspace relation " + r.getId());
			success = delete(r.getId());
		}
        if (success && ES_READ_DELAY_MS > 0) {
            try {
                Thread.sleep(ES_READ_DELAY_MS);
            }
            catch (Exception e) {
                logger.error("removeUserPermissionFromWorkspace " + e.getMessage());
            }
        }
		return success;
	}

	@Override
	public boolean removeUserPermissionFromWorkspace(final String userId, final String permission,
			final String workspaceId) {
		boolean success = false;
		for (final G_UserWorkspace r : getByUserIdAndWorkspaceId(userId, workspaceId)) {
			if (r.getRole().name().equals(permission)) {
				// FIXME: There is a possible bug here if the id was not valid.
				success = delete(r.getId());
			}
		}

		return success;
	}

	@Override
	public G_UserWorkspace save(final G_UserWorkspace g) {
		G_UserWorkspace returnVal = null;
		if (ValidationUtils.isValid(g)) {
			g.setModified(getModifiedTime());
			if (g.getId() == null) {
				g.setId(saveObject(g, g.getId(), indexName, type, false));
			}
			saveObject(g, g.getId(), indexName, type, true);
			returnVal = g;
		} else {
			logger.error("Attempted to save a null user workspace object!");
		}
		return returnVal;
	}

}
