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

package graphene.model;

import graphene.dao.sql.DBConnectionPoolService;
import graphene.dao.sql.util.JDBCUtil;
import graphene.model.idl.G_SymbolConstants;
import graphene.util.db.MainDB;
import graphene.util.db.SecondaryDB;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.services.ClasspathResourceSymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.slf4j.Logger;

public class DTOGenerationModule {
	@Contribute(SymbolSource.class)
	public void contributePropertiesFileAsSymbols(
			OrderedConfiguration<SymbolProvider> configuration) {
		configuration.add("DatabaseConfiguration",
				new ClasspathResourceSymbolProvider("database.properties"),
				"after:SystemProperties", "before:ApplicationDefaults");
	}

	/**
	 * Note that when injecting a String symbol you must also add the @Inject
	 * annotation in addition to the @Symbol annotation.
	 * 
	 * 
	 * @param serverUrl
	 * @param username
	 * @param userPassword
	 * @param logger
	 * @param util
	 * @return
	 */
	@Marker(MainDB.class)
	public static DBConnectionPoolService buildMainDBConnectionPool(
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_URL) String serverUrl,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_USERNAME) String username,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_PASSWORD) String userPassword,
			final Logger logger, JDBCUtil util) {
		try {
			return new DBConnectionPoolService(logger, util, serverUrl,
					username, userPassword, true);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	/**
	 * Builder for a second database, if needed. Note that when injecting a
	 * String symbol you must also add the @Inject annotation in addition to the @Symbol
	 * annotation.
	 * 
	 * 
	 * @param serverUrl
	 * @param username
	 * @param userPassword
	 * @param logger
	 * @param util
	 * @return
	 */
	@Marker(SecondaryDB.class)
	public static DBConnectionPoolService buildSecondaryConnectionPool(
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER2_URL) String serverUrl,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER2_USERNAME) String username,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER2_PASSWORD) String userPassword,
			final Logger logger, JDBCUtil util) {
		try {
			return new DBConnectionPoolService(logger, util, serverUrl,
					username, userPassword, true);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}
}
