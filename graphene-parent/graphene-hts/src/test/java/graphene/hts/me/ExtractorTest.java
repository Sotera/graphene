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

import graphene.hts.entityextraction.AccountExtractor;
import graphene.hts.entityextraction.CreditCardExtractor;
import graphene.hts.entityextraction.EmailExtractor;
import graphene.hts.entityextraction.Extractor;
import graphene.hts.entityextraction.PhoneExtractor;
import graphene.hts.entityextraction.USSSNExtractor;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class ExtractorTest {

	@Test
	public void testAccount() {
		final Extractor e = new AccountExtractor();
		System.out.println("Account: " + e.extract("asdfsa us123412341234 aasdfasd as"));
		// AssertJUnit
		// .assertTrue(e.extract("212-23-3123").contains("212-23-3123"));

	}

	@Test
	public void testCC() {
		final Extractor e = new CreditCardExtractor();
		System.out.println("CC: " + e.extract("asdfsa 4123123412341234 aasdfasd as"));
		System.out.println("CC: " + e.extract("asdfsa 6011-1234-1234-1234 aasdfasd as"));
		System.out.println("CC: " + e.extract("asdfsa 4123-12341234-1234 aasdfasd as"));
		System.out.println("CC: " + e.extract("4123-12341234-1234"));
		System.out.println("CC: " + e.extract("asdfsa 120001002052644268005202 aasdfasd as"));

	}

	@Test
	public void testEmail() {
		final Extractor e = new EmailExtractor();
		AssertJUnit.assertTrue(e.extract("bob@gmail.com").contains("bob@gmail.com"));
		AssertJUnit.assertTrue(e.extract("asdfasdf bob@gmail.com asdfasdf").contains("bob@gmail.com"));
		AssertJUnit.assertTrue(e.extract("#@@$ bob.asdf@gmail.com 123q4erwqe5243t").contains("bob.asdf@gmail.com"));
		System.out.println("Email:" + e.extract("asdfasdf bob@gmail.com asdfasdf"));
		System.out.println("Email:" + e.extract("asdfasdf bob at gmail.com asdfasdf"));
		System.out.println("Email:" + e.extract("asdfasdf bob ( at ) gmail (dot) com asdfasdf"));
		System.out.println("Email:" + e.extract("asdfasdf bob [at] gmail (dot) net asdfasdf"));
		System.out.println("Email:" + e.extract("asdfasdf bob.a.sdf@gmail.com asdfasdf"));
	}

	@Test
	public void testPhones() {
		final Extractor e = new PhoneExtractor();
		System.out.println("Phone: " + e.extract("asdfsa (800) 555-1212 aasdfasd as"));
		System.out.println("Phone: " + e.extract("asdfsa 800-555-1212 aasdfasd as"));
		System.out.println("Phone: " + e.extract("asdfsa 2425551212 aasdfasd as"));

	}

	@Test
	public void testSSN() {
		final Extractor e = new USSSNExtractor();
		System.out.println("SSN:" + e.extract("212-23-3123"));
		// AssertJUnit
		// .assertTrue(e.extract("212-23-3123").contains("212-23-3123"));

	}
}
