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
import graphene.model.idl.G_Group;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserGroup;

import java.util.List;

public interface UserGroupDAO {
	public boolean addToGroup(String userId, String groupId);

	public boolean delete(String id);

	List<G_UserGroup> getAll();

	public G_UserGroup getById(String id);

	public abstract List<G_UserGroup> getGroupMembershipsForGroupId(String groupId);

	public List<G_UserGroup> getGroupMembershipsForUserId(String userId);

	public abstract List<G_UserGroup> getGroupMembershipsForUserIdAndGroupId(final String userId, final String groupId);

	public List<G_Group> getGroupsForUserId(String userId);

	public List<G_User> getUsersByGroupId(String groupId);

	public void initialize() throws DataAccessException;

	public boolean removeFromGroup(String userId, String groupId);

	public G_UserGroup save(G_UserGroup g);
}
