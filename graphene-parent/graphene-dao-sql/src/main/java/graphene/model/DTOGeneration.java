/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.model;

import graphene.dao.sql.DBConnectionPoolService;
import graphene.util.db.MainDB;
import graphene.util.db.SecondaryDB;

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
 */
public class DTOGeneration {
	private static Registry registry;

	// private static DBConnectionPoolService cp;
	private static Logger logger = LoggerFactory.getLogger(DTOGeneration.class);

	private static DBConnectionPoolService secondaryDB;

	private static DBConnectionPoolService mainDB;

	public static void generateDTO(final DBConnectionPoolService cp, final String tablePrefix, final String packageName) {
		java.sql.Connection conn = null;
		try {
			conn = cp.getConnection();

			final MetaDataExporter exporter = new MetaDataExporter();
			exporter.setPackageName(packageName);
			// exporter.setSchemaPattern("");

			exporter.setTargetFolder(new File("src/main/java"));

			// here we set up this object that will be applied to all beans
			// (DTOs)
			final BeanSerializer bs = new BeanSerializer();
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
		} catch (final SQLException e) {
			logger.error(e.getMessage());
		} catch (final Exception e) {
			logger.error(e.getMessage());
		} finally {
			try {
				if ((conn != null) && !conn.isClosed()) {
					conn.close();
				}
			} catch (final SQLException e) {
				logger.error(e.getMessage());
			}
		}
	}

	public static void main(final String[] args) throws Exception {
		setup();
		generateDTO(mainDB, "%", "graphene.model.sql");
		generateDTO(secondaryDB, "G_%", "graphene.model.sql");
	}

	public static void setup() {

		final RegistryBuilder builder = new RegistryBuilder();
		builder.add(DTOGenerationModule.class);
		registry = builder.build();
		registry.performRegistryStartup();
		mainDB = registry.getService(DBConnectionPoolService.class, MainDB.class);
		secondaryDB = registry.getService(DBConnectionPoolService.class, SecondaryDB.class);
	}

	private DTOGeneration() {
		// TODO Auto-generated constructor stub
	}

}
