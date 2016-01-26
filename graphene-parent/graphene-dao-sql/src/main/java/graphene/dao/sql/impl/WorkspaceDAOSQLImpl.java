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
import graphene.dao.WorkspaceDAO;
import graphene.model.graph.G_PersistedGraph;
import graphene.model.idl.G_Workspace;

import java.util.List;

public class WorkspaceDAOSQLImpl implements WorkspaceDAO {

	@Override
	public long countWorkspaces(final String id, final String partialName) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<G_Workspace> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_Workspace getById(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_PersistedGraph getExistingGraph(final String graphSeed, final String username, final String timeStamp) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void initialize() throws DataAccessException {
		// TODO Auto-generated method stub

	}

	@Override
	public G_Workspace save(final G_Workspace g) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_PersistedGraph saveGraph(final G_PersistedGraph pg) {
		// TODO Auto-generated method stub
		return null;
	}

}
