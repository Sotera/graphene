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

package graphene.dao.sql.impl;

import graphene.business.commons.exception.DataAccessException;
import graphene.dao.UserWorkspaceDAO;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserSpaceRelationshipType;
import graphene.model.idl.G_UserWorkspace;
import graphene.model.idl.G_Workspace;

import java.util.List;

public class UserWorkspaceDAOSQLImpl implements UserWorkspaceDAO {

	@Override
	public boolean addRelationToWorkspace(final String userId, final G_UserSpaceRelationshipType rel,
			final String workspaceId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int countUsersForWorkspace(final String workspaceId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteWorkspaceRelations(final String workspaceId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<G_UserWorkspace> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_UserWorkspace getById(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserWorkspace> getByUserId(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserWorkspace> getByUserIdAndWorkspaceId(final String userId, final String workspaceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserWorkspace> getByWorkspaceId(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_Workspace> getMostRecentWorkspacesForUser(final String userId, final int quantity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_User> getUsersForWorkspace(final String workspaceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_Workspace> getWorkspacesForUser(final String userId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasRelationship(final String userId, final String workspaceid,
			final G_UserSpaceRelationshipType... rel) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void initialize() throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean removeUserFromWorkspace(final String userId, final String workspaceId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean removeUserPermissionFromWorkspace(final String userId, final String permission,
			final String workspaceId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public G_UserWorkspace save(final G_UserWorkspace g) {
		// TODO Auto-generated method stub
		return null;
	}

}
