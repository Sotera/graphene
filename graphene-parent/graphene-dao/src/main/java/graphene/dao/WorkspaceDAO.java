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
import graphene.model.graph.G_PersistedGraph;
import graphene.model.idl.G_Workspace;

import java.util.List;

public interface WorkspaceDAO {

	public long countWorkspaces(String id, String partialName);

	public boolean delete(String id);

	public List<G_Workspace> getAll();

	// public G_Workspace getOrCreateWorkspace(G_Workspace g);

	public G_Workspace getById(String id);

	public G_PersistedGraph getExistingGraph(String graphSeed, String username, String timeStamp);

	public void initialize() throws DataAccessException;

	/**
	 * Make sure that the creation date and modified date are set.
	 * 
	 * @param g
	 *            the workspace to save. Note that any authorization to do so is
	 *            done at a higher level.
	 * @return the workspace, with any slight modifications that the DAO might
	 *         have to do.
	 */
	public G_Workspace save(G_Workspace g);

	/**
	 * Used to persist a graph to a backend.
	 * 
	 * @param graphSeed
	 * @param username
	 * @param timeStamp
	 * @param graphJSONdata
	 * @return
	 */
	public G_PersistedGraph saveGraph(G_PersistedGraph pg);
}
