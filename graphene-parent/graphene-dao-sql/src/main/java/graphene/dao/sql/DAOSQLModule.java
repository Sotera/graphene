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

package graphene.dao.sql;

import graphene.dao.sql.util.JDBCUtil;
import graphene.model.idl.G_SymbolConstants;
import graphene.util.db.MainDB;
import graphene.util.jvm.JVMHelper;

import java.sql.Connection;
import java.sql.Driver;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Enumeration;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.services.ClasspathResourceSymbolProvider;
import org.apache.tapestry5.ioc.services.RegistryShutdownHub;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.slf4j.Logger;

/**
 * A module for the SQL DAOs using QueryDSL classes and generated classes.
 * 
 * @author djue
 * 
 */

public class DAOSQLModule {
	public static void bind(final ServiceBinder binder) {
		binder.bind(JDBCUtil.class).eagerLoad();
	}

	/**
	 * Node that when injecting a String symbol you must also add the @Inject
	 * annotation in addition to the @Symbol annotation.
	 * 
	 * 
	 * @param serverURL
	 * @param serverName
	 * @param serverPort
	 * @param databaseName
	 * @param username
	 * @param userPassword
	 * @param logger
	 * @return
	 * @throws Exception
	 */
	@Marker(MainDB.class)
	public static DBConnectionPoolService buildGrapheneConnectionPool(final RegistryShutdownHub ptm,
			@Inject final JDBCUtil util, @Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_URL) final String serverUrl,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_USERNAME) final String username,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_PASSWORD) final String userPassword, final Logger logger)
			throws Exception {

		final DBConnectionPoolService cps = new DBConnectionPoolService(logger, util, serverUrl, username,
				userPassword, true);

		ptm.addRegistryShutdownListener(new Runnable() {
			@Override
			public void run() {
				if (cps != null) {
					logger.info("Shuting down Connection Pool for " + cps.getUrl()
							+ " from buildGrapheneConnectionPool");
					Connection conn;
					try {
						conn = cps.getConnection();
						final java.sql.Statement statement = conn.createStatement();
						statement.executeUpdate("SHUTDOWN");
						statement.close();

						// Note that calling close on the connection here gets a
						// NPE.
					} catch (final Exception e) {
						logger.error(e.getMessage());
					}
					JVMHelper.suggestGC();
				}

				// Now unregister any JDBC drivers
				final Enumeration<Driver> drivers = DriverManager.getDrivers();
				while (drivers.hasMoreElements()) {
					final Driver driver = drivers.nextElement();
					try {
						DriverManager.deregisterDriver(driver);
						logger.info(String.format("deregistering jdbc driver: %s", driver));
					} catch (final SQLException e) {
						logger.error(String.format("Error deregistering driver %s", driver), e);
					}

				}
			}
		});

		return cps;

	}

	/**
	 * Tell Tapestry to look for a database properties file in the
	 * WEB-INF/classes folder of the war.
	 * 
	 * @param configuration
	 */
	@Contribute(SymbolSource.class)
	public void contributePropertiesFileAsSymbols(final OrderedConfiguration<SymbolProvider> configuration) {
		configuration.add("DatabaseConfiguration", new ClasspathResourceSymbolProvider("database.properties"),
				"after:SystemProperties", "before:ApplicationDefaults");
	}
}
