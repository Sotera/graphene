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

package graphene.dao.sql.guice;

import graphene.dao.sql.DBConnectionPoolService;

import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ConnectionContext {

	private static final Logger logger = LoggerFactory.getLogger(ConnectionContext.class);
	private final DBConnectionPoolService dataSource;

	private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();

	@Inject
	public ConnectionContext(final DBConnectionPoolService dataSource) {
		this.dataSource = dataSource;
	}

	public Connection getConnection() {
		return connectionHolder.get();
	}

	public Connection getConnection(final boolean create) {
		Connection connection = connectionHolder.get();
		if (!create || (connection != null)) {
			return connection;
		}
		try {
			connection = dataSource.getConnection();
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		} catch (final Exception e) {
			logger.error(e.getMessage());
		}
		connectionHolder.set(connection);
		return connection;
	}

	public void removeConnection() {
		connectionHolder.remove();
	}
}
