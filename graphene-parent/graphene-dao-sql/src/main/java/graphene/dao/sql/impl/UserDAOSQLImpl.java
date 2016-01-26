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
import graphene.dao.UserDAO;
import graphene.model.idl.AuthenticationException;
import graphene.model.idl.G_User;

import java.util.List;

public class UserDAOSQLImpl implements UserDAO {

	@Override
	public long countUsers(final String partialName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean disable(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean enable(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<G_User> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_User getById(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_User getByUsername(final String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPasswordHash(final String id, final String password) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isExistingId(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isExistingUsername(final String username) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public G_User loginAuthenticatedUser(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_User loginUser(final String id, final String password) throws AuthenticationException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_User save(final G_User user) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean updatePasswordHash(final String id, final String passwordHash) {
		// TODO Auto-generated method stub
		return false;
	}

}
