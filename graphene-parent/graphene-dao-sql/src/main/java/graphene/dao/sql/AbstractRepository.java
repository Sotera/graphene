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
