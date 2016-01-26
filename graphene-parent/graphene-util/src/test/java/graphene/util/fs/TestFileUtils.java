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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class TestFileUtils {

	@Test
	public void confirmWindowsRegex() {
		Pattern p = Pattern.compile(FileUtils.ENVIRONMENTAL_VAR_REGEX);
		Matcher m = p.matcher("%TOMCAT_HOME%"); // get a matcher object
		AssertJUnit.assertTrue(m.find());
		AssertJUnit.assertEquals("TOMCAT_HOME", m.group(3));
	}

	@Test
	public void confirmUnixRegex1() {
		Pattern p = Pattern.compile(FileUtils.ENVIRONMENTAL_VAR_REGEX);
		Matcher m = p.matcher("${TOMCAT_HOME}"); // get a matcher object
		AssertJUnit.assertTrue(m.find());
		AssertJUnit.assertEquals("TOMCAT_HOME", m.group(1));
	}

	@Test
	public void confirmUnixRegex2() {
		Pattern p = Pattern.compile(FileUtils.ENVIRONMENTAL_VAR_REGEX);
		Matcher m = p.matcher("$TOMCAT_HOME"); // get a matcher object
		AssertJUnit.assertTrue(m.find());
		AssertJUnit.assertEquals("TOMCAT_HOME", m.group(2));
	}

	@Test
	public void convertWithSpaces() {
		String s = FileUtils.convertSystemProperties("%CUDA_HOME%/test/testdb");
		System.out.println(s);
	}

	@Test
	public void convertInline() {
		String s = FileUtils
				.convertSystemProperties("jdbc:databasetype:file:%CATALINA_HOME%/test/testdb");
		System.out.println(s);
	}

}
