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

import graphene.dao.UserWorkspaceDAO;
import graphene.dao.WorkspaceDAO;
import graphene.dao.es.BasicESDAO;
import graphene.dao.es.ESRestAPIConnection;
import graphene.dao.es.JestModule;
import graphene.model.graph.G_PersistedGraph;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_Workspace;
import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestResult;
import io.searchbox.core.Index;
import io.searchbox.core.Search;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.apache.commons.lang3.StringUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class WorkspaceDAOESImpl extends BasicESDAO implements WorkspaceDAO {

	private static final String TYPE = "workspace";
	@Inject
	@Symbol(JestModule.ES_WORKSPACE_INDEX)
	private String indexName;
	@Inject
	@Symbol(JestModule.ES_PERSISTED_GRAPH_INDEX)
	private String persistedGraphIndexName;

	@Inject
	@Symbol(JestModule.ES_PERSISTED_GRAPH_TYPE)
	private String persistedGraphType;
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_DELETE_WORKSPACES)
	private boolean enableDelete;

	@Inject
	UserWorkspaceDAO uwDAO;

	public WorkspaceDAOESImpl(final ESRestAPIConnection c, final Logger logger) {
		this.c = c;
		mapper = new ObjectMapper(); // can reuse, share globally
		this.logger = logger;
		setType(TYPE);
	}

	@Override
	public long countWorkspaces(final String id, final String partialName) {
		final List<G_Workspace> workspacesForUser = uwDAO.getWorkspacesForUser(id);
		long count = 0;
		for (final G_Workspace w : workspacesForUser) {
			if (StringUtils.containsIgnoreCase(partialName, w.getTitle())) {
				count++;
			}
		}
		return count;
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
	public List<G_Workspace> getAll() {
		return getAllResults().getSourceAsObjectList(G_Workspace.class);
	}

	@Override
	public G_Workspace getById(final String id) {
		return getResultsById(id).getSourceAsObject(G_Workspace.class);
	}

	@Override
	public G_PersistedGraph getExistingGraph(final String graphSeed, final String username, final String timeStamp) {

		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		// For all users at this point!!!
		searchSourceBuilder.query(QueryBuilders.matchQuery("graphSeed", graphSeed));

		final Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(persistedGraphIndexName)
				.setParameter("timeout", defaultESTimeout).addType(persistedGraphType).build();
		// logger.debug(searchSourceBuilder.toString());
		JestResult result;
		G_PersistedGraph resultObject = null;
		try {
			result = c.getClient().execute(search);
			if (result != null) {
				// logger.debug(result);
				resultObject = result.getSourceAsObject(G_PersistedGraph.class);
				if (resultObject != null) {
					resultObject.setId((String) result.getValue("_id"));
					// logger.debug(resultObject);
				} else {
					logger.error("No result was found for query: \n" + searchSourceBuilder.toString());
				}
			} else {
				logger.error("Existing Graph Result was null!\n" + searchSourceBuilder.toString());
			}
		} catch (final Exception e) {
			logger.error("get Existing Graph: " + e.getMessage());
		}
		return resultObject;
	}

	@Override
	@PostInjection
	public void initialize() {
		setIndex(indexName);
		setType(TYPE);
		super.initialize();
	}

	@Override
	public G_Workspace save(final G_Workspace g) {
		G_Workspace returnVal = null;
		if (ValidationUtils.isValid(g)) {
			g.setModified(getModifiedTime());
			if (g.getId() == null) {
//			    String wid = saveObject(g, g.getId(), indexName, type, false);
			    String wid = saveObject(g, g.getId(), indexName, type, true);
				g.setId(wid);
			}
			saveObject(g, g.getId(), indexName, type, true);
			returnVal = g;
		} else {
			logger.error("Attempted to save a null workspace object!");
		}

		return returnVal;
	}

	@Override
	public G_PersistedGraph saveGraph(final G_PersistedGraph pg) {
		final G_PersistedGraph returnVal = pg;
		returnVal.setModified(getModifiedTime());
		Index index;
		// This makes it unique only to the seed
		returnVal.setId(pg.getGraphSeed());
		index = new Index.Builder(returnVal).index(persistedGraphIndexName).id(returnVal.getId())
				.type(persistedGraphType).build();
		try {
			c.getClient().execute(index);
		} catch (ExecutionException | InterruptedException | IOException e) {
			logger.error("Save graph: " + e.getMessage());
		} catch (final Exception e) {
			logger.error("Save graph: " + e.getMessage());
		}
		return returnVal;
	}

}
