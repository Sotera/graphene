package graphene.dao.sql;

import graphene.dao.sql.guice.ConnectionContext;

import java.sql.Connection;

import javax.inject.Inject;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

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

	public long getModifiedTime() {
		return DateTime.now(DateTimeZone.UTC).getMillis();
	}

	@Inject
	private ConnectionContext context;

	public Connection getConnection() {
		return context.getConnection();
	}

	public void initialize() {
		// TODO Auto-generated method stub

	}

	private SQLQuery query() {
		return new SQLQuery(getConnection(), configuration);
	}

	protected SQLQuery from(Expression<?> expression) {
		return query().from(expression);
	}

	protected SQLInsertClause insert(RelationalPath<?> path) {
		return new SQLInsertClause(getConnection(), configuration, path);
	}

	protected SQLUpdateClause update(RelationalPath<?> path) {
		return new SQLUpdateClause(getConnection(), configuration, path);
	}

	protected SQLDeleteClause delete(RelationalPath<?> path) {
		return new SQLDeleteClause(getConnection(), configuration, path);
	}
}
