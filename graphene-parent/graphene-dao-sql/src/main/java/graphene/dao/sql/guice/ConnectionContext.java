package graphene.dao.sql.guice;

import graphene.util.db.DBConnectionPoolService;

import java.sql.Connection;
import java.sql.SQLException;

import javax.inject.Inject;

public class ConnectionContext {

    private final DBConnectionPoolService dataSource;
    
    private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<Connection>();

    @Inject
    public ConnectionContext(DBConnectionPoolService dataSource) {
        this.dataSource = dataSource;
    }
    
    public Connection getConnection(boolean create) {
        Connection connection = connectionHolder.get();
        if (!create || connection != null) {
            return connection;
        }
        try {
            connection = dataSource.getConnection();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        connectionHolder.set(connection);
        return connection;
    }
    
    public Connection getConnection() {
        return connectionHolder.get();
    }

    public void removeConnection() {
        connectionHolder.remove();
    }
}
