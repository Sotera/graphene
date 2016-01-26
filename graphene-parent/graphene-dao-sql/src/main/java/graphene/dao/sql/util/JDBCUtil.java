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

package graphene.dao.sql.util;

import java.util.Collection;

import org.apache.tapestry5.ioc.annotations.UsesOrderedConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A utility class for finding JDBC drivers on the classpath. In a customer
 * level module, a contribution is made to the this service in the form of a
 * list of one or more strings which represent the name of the driver to search
 * for. The drivers are then searched for in the order that they were
 * contributed.
 * 
 * @author djue
 * 
 */
@UsesOrderedConfiguration(String.class)
public class JDBCUtil {

	private final Logger logger = LoggerFactory.getLogger(JDBCUtil.class);
	private final Collection<String> driverStrings;

	public JDBCUtil(final Collection<String> drivers) {
		driverStrings = drivers;
		findJDBCDrivers();
	}

	/**
	 * A convenience/info utility for doing ClassForName on jdbc drivers. May
	 * need to be done whenever [re]enabling a connection pool, so that the
	 * drivers are found on the class path.
	 */
	public void findJDBCDrivers() {
		if (driverStrings != null) {
			for (final String driver : driverStrings) {
				try {
					final Class<?> c = Class.forName(driver);
					if (c != null) {
						logger.info("+++++++++ SUCCESS finding " + driver);
					}
				} catch (final ClassNotFoundException e1) {
					logger.warn("======== Could not find " + driver + " on classpath");
					// e1.printStackTrace();
				}
			}
		}
	}
}
