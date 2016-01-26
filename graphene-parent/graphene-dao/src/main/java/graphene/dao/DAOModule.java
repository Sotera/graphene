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

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_PropertyKey;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.model.idl.G_SymbolConstants;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;

/**
 * While this DAO Module is mostly for sharing the interface definitions (and
 * hence very closely tied to the core model module), this is also a reasonable
 * place to bind the business logic services that sit on top of the DAOs (which
 * are wired later)
 * 
 * @author djue
 * 
 */
public class DAOModule {

	public static void bind(final ServiceBinder binder) {

	}

	public static void contributeApplicationDefaults(final MappedConfiguration<String, Object> configuration) {
		configuration.add(G_SymbolConstants.ENABLE_DELETE_USERS, true);

		configuration.add(G_SymbolConstants.ENABLE_DELETE_GROUPS, true);
		configuration.add(G_SymbolConstants.ENABLE_WORKSPACES, true);
		configuration.add(G_SymbolConstants.ENABLE_DELETE_WORKSPACES, true);
		configuration.add(G_SymbolConstants.ENABLE_DELETE_UNUSED_WORKSPACES, true);
		configuration.add(G_SymbolConstants.ENABLE_DELETE_ROLES, false);
		configuration.add(G_SymbolConstants.ENABLE_DELETE_USER_GROUP, true);
		configuration.add(G_SymbolConstants.ENABLE_DELETE_USER_WORKSPACES, true);
		configuration.add(G_SymbolConstants.ENABLE_DELETE_USER_ROLE, true);

		configuration.add(G_SymbolConstants.ENABLE_DELETE_LOGS, false);
		configuration.add(G_SymbolConstants.ENABLE_DELETE_DATASOURCES, false);

	}

	@Contribute(G_PropertyKeyTypeAccess.class)
	public static void contributePropertyKeys(final MappedConfiguration<String, G_PropertyKey> configuration) {
		for (final G_CanonicalPropertyType e : G_CanonicalPropertyType.values()) {
			final G_PropertyKey n = new G_PropertyKey(e.name(), e.name(), e.name(), 0l);
			configuration.add(e.name(), n);
		}
	}

}
