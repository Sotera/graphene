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

package graphene.hts.me;

import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class FamilyReferenceExtractorTest {

	private FamilyReferenceExtractor f;

	@BeforeClass
	public void setup() {
		f = new FamilyReferenceExtractor();
	}

	// FIXME: this had been working in the past. Check the regexes.
	// @Test
	public void findParentsTest() {

		Assert.assertEquals(
				f.getParent("S/ O WILL I AM RIKER , KLINGON HOLDING CELL, DUSTY PLANET, "),
				"WILL I AM RIKER ");
		Assert.assertEquals(
				f.getParent("S / O SPOCK, ZABUL TERRAGUN, SMALL VILLAGE NEAR SOLACE VIEW, "),
				"SPOCK");
		Assert.assertEquals(
				f.getParent("S/OJAMES T. KIRK, STARSHIP Enterprise, Delta Quadrant"),
				"JAMES T. KIRK");
		Assert.assertEquals(
				f.getParent("asdfS/ODavid Regex, Super Chicken Street, Sometown Malaysia"),
				"David Regex");
		Assert.assertEquals(f.getParent("S/O Sponge Bob"), "Sponge Bob");
	}

	@Test
	public void cleanNamesTest() {
		Assert.assertEquals(f.cleanName("DAVID Regex"), "DAVID Regex");
		Assert.assertEquals(f.cleanName("DAVID Reg Ex"), "DAVID Reg Ex");
		Assert.assertEquals(f.cleanName("DAVID      Reg        Ex"),
				"DAVID Reg Ex");

		Assert.assertEquals(f.cleanName("  DAVID Regex"), "DAVID Regex");
		Assert.assertEquals(f.cleanName("  DAVID Reg Ex"), "DAVID Reg Ex");
		Assert.assertEquals(f.cleanName(" DAVID      Reg        Ex"),
				"DAVID Reg Ex");
	}

}
