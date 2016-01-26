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

package graphene.web.components.admin;

import java.util.List;
import java.util.regex.Pattern;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.util.InternalUtils;

public class SystemStatus {
	@Inject
	@Symbol(SymbolConstants.PRODUCTION_MODE)
	@Property(write = false)
	private boolean productionMode;

	@Inject
	@Symbol(SymbolConstants.TAPESTRY_VERSION)
	@Property(write = false)
	private String tapestryVersion;
	private static final String PATH_SEPARATOR_PROPERTY = "path.separator";
	private final String pathSeparator = System.getProperty(PATH_SEPARATOR_PROPERTY);
	// Match anything ending in .(something?)path.

	private static final Pattern PATH_RECOGNIZER = Pattern.compile("\\..*path$");

	@Inject
	@Symbol(SymbolConstants.APPLICATION_VERSION)
	@Property(write = false)
	private String applicationVersion;

	@Property
	private String propertyName;

	public String[] getComplexPropertyValue() {
		// Neither : nor ; is a regexp character.

		return getPropertyValue().split(pathSeparator);
	}

	public String getPropertyValue() {
		return System.getProperty(propertyName);
	}

	public List<String> getSystemProperties() {
		return InternalUtils.sortedKeys(System.getProperties());
	}

	public boolean isComplexProperty() {
		return PATH_RECOGNIZER.matcher(propertyName).find() && getPropertyValue().contains(pathSeparator);
	}
}
