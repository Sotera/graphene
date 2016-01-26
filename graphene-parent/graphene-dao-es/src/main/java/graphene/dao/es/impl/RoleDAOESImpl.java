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

/**
 * 
 */
package graphene.dao.es.impl;

import graphene.dao.RoleDAO;
import graphene.dao.es.BasicESDAO;
import graphene.dao.es.ESRestAPIConnection;
import graphene.dao.es.JestModule;
import graphene.model.idl.G_Role;
import graphene.model.idl.G_SymbolConstants;
import graphene.util.validator.ValidationUtils;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * @author djue
 * 
 */
public class RoleDAOESImpl extends BasicESDAO implements RoleDAO {
	private static final String TYPE = "role";
	@Inject
	@Symbol(JestModule.ES_ROLE_INDEX)
	private String indexName;
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_DELETE_ROLES)
	private boolean enableDelete;

	public RoleDAOESImpl(final ESRestAPIConnection c, final Logger logger) {
		auth = null;
		this.c = c;
		mapper = new ObjectMapper(); // can reuse, share globally
		this.logger = logger;
		setType(TYPE);
	}

	@Override
	public G_Role create(final G_Role g) {
		G_Role group = getRoleByRolename(g.getName());
		if (!ValidationUtils.isValid(group)) {
			group = save(g);
		}
		return group;
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
	public List<G_Role> getAll() {
		return getAllResults().getSourceAsObjectList(G_Role.class);
	}

	@Override
	public G_Role getById(final String id) {
		return getResultsById(id).getSourceAsObject(G_Role.class);
	}

	@Override
	public G_Role getRoleByRolename(final String name) {
		return getByField("name", name).getSourceAsObject(G_Role.class);
	}

	@Override
	@PostInjection
	public void initialize() {
		setIndex(indexName);
		setType(TYPE);
		super.initialize();
	}

	@Override
	public G_Role save(final G_Role g) {
		G_Role returnVal = null;
		if (ValidationUtils.isValid(g)) {
			g.setModified(getModifiedTime());
			if (g.getId() == null) {
				g.setId(saveObject(g, g.getId(), indexName, type, false));
			}
			saveObject(g, g.getId(), indexName, type, true);
			returnVal = g;
		} else {
			logger.error("Attempted to save a null role object!");
		}
		return returnVal;
	}
}
