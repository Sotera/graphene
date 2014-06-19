package graphene.dao.sql;

import graphene.model.idl.G_SymbolConstants;
import graphene.util.db.DBConnectionPoolService;
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
	public static void bind(ServiceBinder binder) {

	}


	@Contribute(SymbolSource.class)
	public void contributePropertiesFileAsSymbols(
			OrderedConfiguration<SymbolProvider> configuration) {
		configuration.add("DatabaseConfiguration",
				new ClasspathResourceSymbolProvider("database.properties"),
				"after:SystemProperties", "before:ApplicationDefaults");
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
	 * @param userName
	 * @param userPassword
	 * @param logger
	 * @return
	 * @throws Exception
	 */
	@Marker(MainDB.class)
	public static DBConnectionPoolService buildGrapheneConnectionPool(
			RegistryShutdownHub ptm,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_URL) String serverUrl,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_USERNAME) String userName,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_PASSWORD) String userPassword,
			final Logger logger) throws Exception {

		final DBConnectionPoolService cps = new DBConnectionPoolService(logger,
				serverUrl, userName, userPassword, true);

		ptm.addRegistryShutdownListener(new Runnable() {
			public void run() {
				if (cps != null) {
					logger.info("Shuting down Connection Pool for "
							+ cps.getUrl()
							+ " from buildGrapheneConnectionPool");
					Connection conn;
					try {
						conn = cps.getConnection();
						java.sql.Statement statement = conn.createStatement();
						statement.executeUpdate("SHUTDOWN");
						statement.close();
						// Note that calling close on the connection here gets a
						// NPE.
					} catch (Exception e) {
						e.printStackTrace();
					}
					JVMHelper.suggestGC();
				}

				// Now unregister any JDBC drivers
				Enumeration<Driver> drivers = DriverManager.getDrivers();
				while (drivers.hasMoreElements()) {
					Driver driver = drivers.nextElement();
					try {
						DriverManager.deregisterDriver(driver);
						logger.info(String.format(
								"deregistering jdbc driver: %s", driver));
					} catch (SQLException e) {
						logger.error(String.format(
								"Error deregistering driver %s", driver), e);
					}

				}
			}
		});

		return cps;

	}
}
