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

package graphene.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserSpaceRelationshipType;
import graphene.model.idl.G_UserWorkspace;
import graphene.model.idl.G_Workspace;

import java.util.List;

public interface UserWorkspaceDAO {
	/**
	 * 
	 * @param username
	 * @param rel
	 * @param workspaceId
	 * @return
	 */
	boolean addRelationToWorkspace(String userId, G_UserSpaceRelationshipType rel, String workspaceId);

	int countUsersForWorkspace(String workspaceId);

	boolean delete(String id);

	/**
	 * Usually called when deleting a workspace, we want to cascade the deletion
	 * of any relations to the deleted workspace!
	 * 
	 * @param workspaceId
	 */
	boolean deleteWorkspaceRelations(String workspaceId);

	List<G_UserWorkspace> getAll();

	G_UserWorkspace getById(String id);

	List<G_UserWorkspace> getByUserId(String id);

	List<G_UserWorkspace> getByUserIdAndWorkspaceId(String userId, String workspaceId);

	List<G_UserWorkspace> getByWorkspaceId(String id);

	List<G_Workspace> getMostRecentWorkspacesForUser(String userId, int quantity);

	List<G_User> getUsersForWorkspace(String workspaceId);

	List<G_Workspace> getWorkspacesForUser(String userId);

	/**
	 * Return true if the workspace has any of the provided relations to the
	 * user. This is used to verify that the user has the right to save, update,
	 * delete or view the workspace.
	 * 
	 * @param username
	 * @param workspaceid
	 * @param rel
	 * @return true if the user had one or more of the rels to the workspace
	 */
	boolean hasRelationship(String userId, String workspaceid, G_UserSpaceRelationshipType... rel);

	void initialize() throws DataAccessException;

	boolean removeUserFromWorkspace(String userId, String workspaceId);

	boolean removeUserPermissionFromWorkspace(String userId, String permission, String workspaceId);

	G_UserWorkspace save(G_UserWorkspace g);
}
