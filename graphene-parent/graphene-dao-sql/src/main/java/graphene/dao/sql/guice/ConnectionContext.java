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
