package ${package}.model;

import graphene.util.UtilModule;
import graphene.util.db.DBConnectionPoolService;
import graphene.util.db.MainDB;

import java.io.File;
import java.io.Serializable;
import java.sql.SQLException;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.query.codegen.BeanSerializer;
import com.mysema.query.sql.codegen.MetaDataExporter;

/**
 * 
 * This class uses QueryDSL to create new POJOs based on database tables. It
 * also create the query objects, which can allow QueryDSL to create SQL queries
 * based on fluent construction.
 * 
 * Note that you only need to run this once for the tables you want to generate
 * classes for-- below you'll see that previously generated tables are commented
 * out. When a table changes or is new, then you would run this main() for those
 * tables, which would generate the classes in the appropriate place in the
 * model package. Then you can proceed to build DAO classes around those
 * objects. NOTE: you aren't required to use the QueryDSL Query objects. They
 * make things very clean and easy (your code won't compile if the tables
 * change), but QueryDSL is a little slow when translating results back into the
 * POJOs. As an alternative you can write a straight SQL query over JDBC, etc,
 * and then populate those POJOs yourself in an efficient way.
 * 
 * @author djue
 * 
 * 
 *         FIXME: This needs to be turned into a service that the graphene
 *         customer instances can call, if DTO generation is to be part of the
 *         core/ingest
 * 
 *         Updated: as of BETA2, the DTO classes will now implement serializable
 *         and toString().
 */
public class DTOGeneration {

	private static Registry registry;

	private static Logger logger = LoggerFactory.getLogger(DTOGeneration.class);

	private static DBConnectionPoolService mainDB;

	// private static DBConnectionPoolService secondaryDB;

	/**
	 * Create a basic registry that knows how to create the connection pools
	 * defined in the local src/main/resources/database.properties file.
	 */
	public static void setup() {

		RegistryBuilder builder = new RegistryBuilder();
		builder.add(DTOGenerationModule.class);
		builder.add(UtilModule.class);
		registry = builder.build();
		registry.performRegistryStartup();
		mainDB = registry.getService(DBConnectionPoolService.class,
				MainDB.class);

		// For Instagram, the secondary database is not needed, yet.
		// secondaryDB = registry.getService(DBConnectionPoolService.class,
		// SecondaryDB.class);
	}

	/**
	 * Generate DTO java classes for the tables matched by the regex you provide
	 * in the main.
	 */
	public static void generateDTO(DBConnectionPoolService cp,
			String tablePrefix, String packageName) {
		java.sql.Connection conn = null;
		try {
			conn = cp.getConnection();

			MetaDataExporter exporter = new MetaDataExporter();
			exporter.setPackageName(packageName);
			// exporter.setSchemaPattern("");
			exporter.setTargetFolder(new File("src/main/java"));

			// here we set up this object that will be applied to all beans
			// (DTOs)
			BeanSerializer bs = new BeanSerializer();
			// Here we are telling it to add the toString() method to each bean
			bs.setAddToString(true);
			// Here we are telling it to add 'implements Serializable' to each
			// bean. (no serializable id though, so you may see a warning.)
			bs.addInterface(Serializable.class);
			// then we give the exporter the BeanSerializer
			exporter.setBeanSerializer(bs);

			// example, get all the tables/views that match "foo*"
			exporter.setTableNamePattern(tablePrefix);

			// If you want views as well, change this to true.
			exporter.setExportViews(false);

			// This gets the metadata from the database and then starts creating
			// code for you.
			exporter.export(conn.getMetaData());
			conn.close();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				if (conn != null && !conn.isClosed()) {
					conn.close();
				}
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	/**
	 * Run this to create the DTOs. If you have datasets called widgets and
	 * whizbangs, it may be best to put them in different packages, as specified
	 * by the package string below.
	 */
	public static void main(String[] args) throws Exception {
		setup();
		//generateDTO(mainDB, "F%", "${package}.model.sql.${projectName}");
		generateDTO(mainDB, "G%", "${package}.model.sql.${projectName}");
		System.out.println("done");
	}

}
