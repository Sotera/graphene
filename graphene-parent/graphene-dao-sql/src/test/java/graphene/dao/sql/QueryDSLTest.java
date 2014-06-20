package graphene.dao.sql;

import graphene.util.UtilModule;
import graphene.util.db.DBConnectionPoolService;

import java.sql.Connection;
import java.util.Properties;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.slf4j.Logger;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class QueryDSLTest {

	protected Registry registry;
	protected DBConnectionPoolService cp;
	protected Logger logger;
//
//	@BeforeSuite
//	public void setup() {
//
//		RegistryBuilder builder = new RegistryBuilder();
//		builder.add(UtilModule.class);
//		builder.add(DAOSQLModule.class);
//		registry = builder.build();
//		registry.performRegistryStartup();
//		cp = registry.getService("GrapheneConnectionPool",
//				DBConnectionPoolService.class);
//		logger = registry.getService(Logger.class);
//	}
//
//	@Test
//	public void testConnection() {
//		try {
//			Connection conn = cp.getConnection();
//			Properties p = conn.getClientInfo();
//			conn.close();
//
//			System.out.println(p.toString());
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}
}
