package graphene.util.db;

import graphene.util.fs.FileUtils;
import graphene.util.jvm.JVMHelper;

import java.sql.Connection;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import com.jolbox.bonecp.BoneCP;
import com.jolbox.bonecp.BoneCPConfig;

/**
 * TODO: Implement the ability to set bonecp connection pool info from a file.
 * TODO: Implement and test pool failover and restart
 * 
 * @author djue
 * 
 */
public class DBConnectionPoolService {
	@Inject
	private Logger logger;
	private boolean databaseEnabled = true;
	private String userName, url, password;
	private BoneCP connectionPool;
	private boolean initialized = false;


	public boolean isDatabaseEnabled() {
		return databaseEnabled;
	}

	

	public void setDatabaseEnabled(boolean databaseEnabled) {
		this.databaseEnabled = databaseEnabled;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String username) {
		this.userName = username;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public BoneCP getConnectionPool() {
		return connectionPool;
	}

	public void setConnectionPool(BoneCP connectionPool) {
		this.connectionPool = connectionPool;
	}

	/**
	 * Construct a connection pool object.
	 * 
	 * @param l
	 * @param url
	 * @param userName
	 * @param password
	 * @param lazy
	 * @throws Exception
	 */
	public DBConnectionPoolService(Logger l, String url, String userName,
			String password, boolean lazy) throws Exception {
		logger = l;
		this.userName = userName;
		this.password = password;
		this.url = FileUtils.convertSystemProperties(url);
		if (!lazy && databaseEnabled) {
			init();
		}
	}

	public void init() throws Exception {
		// setup the connection pool
		BoneCPConfig config = new BoneCPConfig();
		config.setJdbcUrl(url);
		config.setUsername(userName);
		config.setPassword(password);
		config.setConnectionTimeoutInMs(500);
		config.setMinConnectionsPerPartition(5);
		config.setMaxConnectionsPerPartition(10);
		config.setPartitionCount(1);
		config.setAcquireRetryDelayInMs(1000);
		// config.setCloseConnectionWatch(false);

		/*
		 * This setting has two effects: Increased speed and circumventing a
		 * false warning in the snapshot of BoneCP that we are using
		 */

		config.setDisableConnectionTracking(false);
		initialized = enableDatabase(config);
		// We no longer have access to the server name to ping.
		// if (NetworkUtils
		// .isServerAlive(serverName,
		// FastNumberUtils.parseIntWithCheck(serverPort))) {
		// initialized = enableDatabase(config);
		// } else {
		// initialized = false;
		// throw new Exception("Could not reach the database server "
		// + serverName + " with a ping.");
		// }
	}

	private void registerShutdownHook(final BoneCP cp) {
		// Registers a shutdown hook for the Neo4j instance so that it
		// shuts down nicely when the VM exits (even if you "Ctrl-C" the
		// running application).
		Runtime.getRuntime().addShutdownHook(new Thread() {
			@Override
			public void run() {
				if (cp != null) {
					logger.info("Shuting down Connection Pool for " + url
							+ " from registerShutdownHook");
					cp.shutdown();
					JVMHelper.suggestGC();
				}
			}
		});
	}

	private boolean enableDatabase(BoneCPConfig config) throws Exception {
		if (databaseEnabled == true && initialized == false) {
			try {
				logger.debug("Connecting to database at " + url);
				logger.debug("Connecting to database with config: " + config);
				// Attempt manual shutdown
				shutdown();
				connectionPool = new BoneCP(config);

				// Register for automatic shutdown if JVM stops
				registerShutdownHook(connectionPool);
				// Explicitly get a connection, which forces bonecp to init in
				// correct classloader.
				Connection connection = connectionPool.getConnection();
				if (connection != null) {
					logger.info("Connection successful!");
					/*
					 * Statement stmt = connection.createStatement(); // do
					 * something simple to test that the remote database is //
					 * up. ResultSet rs = stmt.executeQuery("SELECT 1"); while
					 * (rs.next()) { if (!"1".equals(rs.getString(1))) { throw
					 * new Exception(
					 * "Problem executing test statement on database " + url); }
					 * 
					 * }
					 */
					initialized = true;
					// rs.close();
					// stmt.close();
					connection.close();
				} else {
					logger.warn("Could not create connection pool for " + url);
				}
				connection = null;
			} catch (Exception e) {
				e.printStackTrace();
				initialized = false;
				throw e;
			}
		}
		return initialized;
	}

	public boolean isInitialized() {
		return initialized;
	}

	public void setInitialized(boolean initialized) {
		this.initialized = initialized;
	}

	public Connection getConnection() throws Exception {
		if (!initialized) {
			init();
		}
		return connectionPool.getConnection();
	}

	/**
	 * Shutdown the pool iif it's not null.
	 */
	public void shutdown() {
		if (connectionPool != null) {
			connectionPool.shutdown();
		}
		initialized = false;
	}
}
