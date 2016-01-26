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

import graphene.util.TestUtilModule;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.slf4j.Logger;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class PropertiesFileSymbolProviderTest {
	protected Logger logger;

	protected Registry registry;

	private SymbolSource s;

	@BeforeSuite
	public void beforeSuite() {

		RegistryBuilder builder = new RegistryBuilder();
		builder.add(TestUtilModule.class);

		registry = builder.build();
		registry.performRegistryStartup();
		s = registry.getService(SymbolSource.class);
		logger = registry.getService(Logger.class);
	}

	@Test
	public void f1() {
		AssertJUnit.assertEquals("bar1", s.valueForSymbol("foo1"));
		AssertJUnit.assertEquals("bar2", s.valueForSymbol("foo2"));
		AssertJUnit.assertEquals("bar3", s.valueForSymbol("foo3"));
		AssertJUnit.assertEquals("bar4", s.valueForSymbol("foo4"));
	}

	@Test
	public void f2() {
		logger.debug(s.expandSymbols("${foo1}"));
	}

	@Test
	public void f3() {
		AssertJUnit.assertEquals("red", s.valueForSymbol("apple"));
		AssertJUnit.assertEquals("orange", s.valueForSymbol("orange"));
		AssertJUnit.assertEquals("green", s.valueForSymbol("pear"));
		AssertJUnit.assertEquals("yellow", s.valueForSymbol("banana"));
	}

	@Test
	public void f4() {
		boolean passed = false;
		try {
			s.valueForSymbol("car");

		} catch (Exception e) {
			passed = true;
		}
		AssertJUnit.assertTrue(passed);
	}
}
