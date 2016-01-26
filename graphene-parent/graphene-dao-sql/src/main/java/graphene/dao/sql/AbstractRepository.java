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

import graphene.dao.sql.guice.ConnectionContext;

import java.sql.Connection;

import javax.inject.Inject;

import org.joda.time.DateTime;

import com.mysema.query.sql.Configuration;
import com.mysema.query.sql.RelationalPath;
import com.mysema.query.sql.SQLQuery;
import com.mysema.query.sql.dml.SQLDeleteClause;
import com.mysema.query.sql.dml.SQLInsertClause;
import com.mysema.query.sql.dml.SQLUpdateClause;
import com.mysema.query.types.Expression;

public abstract class AbstractRepository {
	@Inject
	private Configuration configuration;

	@Inject
	private ConnectionContext context;

	protected SQLDeleteClause delete(final RelationalPath<?> path) {
		return new SQLDeleteClause(getConnection(), configuration, path);
	}

	protected SQLQuery from(final Expression<?> expression) {
		return query().from(expression);
	}

	public Connection getConnection() {
		return context.getConnection();
	}

	public long getModifiedTime() {
		return DateTime.now().getMillis();
	}

	public void initialize() {
		// TODO Auto-generated method stub

	}

	protected SQLInsertClause insert(final RelationalPath<?> path) {
		return new SQLInsertClause(getConnection(), configuration, path);
	}

	private SQLQuery query() {
		return new SQLQuery(getConnection(), configuration);
	}

	protected SQLUpdateClause update(final RelationalPath<?> path) {
		return new SQLUpdateClause(getConnection(), configuration, path);
	}
}
