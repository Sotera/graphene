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
import graphene.dao.GroupDAO;
import graphene.model.idl.G_Group;

import java.util.List;

public class GroupDAOSQLImpl implements GroupDAO {

	@Override
	public G_Group createGroup(final G_Group g) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean delete(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<G_Group> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_Group getById(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_Group getGroupByGroupname(final String groupname) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public G_Group save(final G_Group g) {
		// TODO Auto-generated method stub
		return null;
	}

}
