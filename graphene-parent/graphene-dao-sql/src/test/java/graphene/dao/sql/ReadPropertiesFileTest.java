package graphene.dao.sql;

import graphene.dao.sql.DAOSQLModule;
import graphene.util.UtilModule;
import graphene.util.db.DBConnectionPoolService;
import graphene.util.db.MainDB;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.slf4j.Logger;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class ReadPropertiesFileTest {
	@Test
	public void testRegistry() {
		AssertJUnit.assertNotNull(cp);

	}

	@Test
	public void testConnectionPool() {
		AssertJUnit.assertNotNull(cp);

	}

	@Test
	public void testLogger() {
		AssertJUnit.assertNotNull(logger);

	}

	protected Registry registry;
	protected DBConnectionPoolService cp;
	protected Logger logger;

	@BeforeSuite
	public void setup() {

		RegistryBuilder builder = new RegistryBuilder();
		builder.add(UtilModule.class);
		builder.add(DAOSQLModule.class);
		// builder.add(ConnectionPoolModule.class);
		registry = builder.build();
		registry.performRegistryStartup();
		cp = registry.getService(DBConnectionPoolService.class, MainDB.class);
		logger = registry.getService(Logger.class);
	}
}
