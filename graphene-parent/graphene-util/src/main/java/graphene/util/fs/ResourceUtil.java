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

package graphene.util.fs;

import graphene.util.UtilRuntimeException;

import java.io.InputStream;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ResourceUtil {
	static private final Logger LOGGER = LoggerFactory.getLogger(ResourceUtil.class);
	static private ResourceUtil myClass = null;

	private ResourceUtil() {
	}

	static public Properties getAsProperties(String resourceName) throws UtilRuntimeException {
		return getAsProperties(resourceName, Thread.currentThread().getContextClassLoader());
	}

	static public Properties getAsProperties(String resourceName, ClassLoader classLoader) throws UtilRuntimeException {

		Properties p;

		if (resourceName == null) {
			throw new IllegalArgumentException("resourceName is null");
		}
		if (myClass == null) {
			myClass = new ResourceUtil();
		}

		InputStream resourceStream = null;
		try {
			// Try local

			resourceStream = classLoader.getResourceAsStream("/" + resourceName);

			// If not found, try classpath

			if (resourceStream == null) {
				resourceStream = classLoader.getResourceAsStream(resourceName);
			}
			
			// If not found, then get out

			if (resourceStream == null) {
				LOGGER.error("Could not load properties from resource \"" + resourceName + "\".  Check the classpath.");
				System.err.println("Could not load properties from resource \"" + resourceName
						+ "\".  Check the classpath.");
				throw new UtilRuntimeException("Could not find resource \"" + resourceName
						+ "\".  Check the classpath.");
			}
			
			// Load the properties!

			p = new Properties();
			p.load(resourceStream);
		}
		catch (UtilRuntimeException e) {
			throw e;
		}
		catch (Exception e) {
			throw new UtilRuntimeException("Could not load properties from resource \"" + resourceName
					+ "\".  Check the classpath.", e);
		}
		finally {
			if (resourceStream != null) {
				try {
					resourceStream.close();
				}
				catch (Throwable ignore) {
				}
			}
		}

		return p;

	}
	
	static public String getExtension(String path) {
		String[] strings = path.split("\\.");
		return strings[strings.length - 1];
	}
}
