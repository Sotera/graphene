package graphene.dao.sql;

import graphene.model.idl.G_SymbolConstants;
import graphene.util.db.DBConnectionPoolService;
import graphene.util.db.MainDB;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.services.ClasspathResourceSymbolProvider;
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
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_URL) String serverUrl,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_USERNAME) String userName,
			@Inject @Symbol(G_SymbolConstants.MIDTIER_SERVER_PASSWORD) String userPassword,
			final Logger logger) throws Exception {

		// TODO: Check the other variables for safety.

		return new DBConnectionPoolService(logger, serverUrl, userName,
				userPassword, true);
	}

}
