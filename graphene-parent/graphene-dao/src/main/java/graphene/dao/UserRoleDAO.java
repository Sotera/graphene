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
import graphene.model.idl.G_Role;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserRole;

import java.util.List;

public interface UserRoleDAO {
	public boolean add(String userId, String roleId);

	public boolean delete(String id);

	public List<G_UserRole> getAll();

	public G_UserRole getById(String id);

	public abstract List<G_UserRole> getByRoleId(String roleId);

	public List<G_UserRole> getByUserId(String userId);

	public abstract List<G_UserRole> getByUserIdAndRoleId(final String userId, final String roleId);

	public List<G_Role> getRolesForUserId(String userId);

	public List<G_User> getUsersForRoleId(String roleId);

	public void initialize() throws DataAccessException;

	public boolean removeFromRole(String userId, String roleId);

	public G_UserRole save(G_UserRole g);
}
