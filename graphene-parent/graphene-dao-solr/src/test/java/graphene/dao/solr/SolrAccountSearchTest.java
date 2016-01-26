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

package graphene.dao.solr;

import graphene.model.query.UnifiedLedgerQuery;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

@Deprecated
public class SolrAccountSearchTest {

	private static Logger l = LoggerFactory
			.getLogger(SolrAccountSearchTest.class);
	private Registry registry;

	@BeforeClass
	public static void setUpClass() throws Exception {
	}

	@AfterClass
	public static void tearDownClass() throws Exception {
	}

	// private LedgerSolrDAO solrService;
	private UnifiedLedgerQuery lq;

	public SolrAccountSearchTest() {
	}

	@AfterMethod
	public final void cleanupThread() {
		registry.cleanupThread();
	}

	@BeforeMethod
	public void setUp() {
	}

	@BeforeSuite
	public final void setup_registry() {
		RegistryBuilder builder = new RegistryBuilder();
		builder.add(SolrTestModule.class);

		registry = builder.build();
		registry.performRegistryStartup();
		// solrService = registry.getService(LedgerSolrDAO.class);

	}

	@AfterSuite
	public final void shutdown_registry() {
		registry.shutdown();
		registry = null;
	}

	@AfterMethod
	public void tearDown() {
	}

	@Test
	public void testFindParentChildAccounts() {
		l.debug("Start");
		lq = new UnifiedLedgerQuery();
		final String accountNumber = "1003202046778";
		lq.addAccountNumber(accountNumber);
		lq.setFindRelatedAccounts(true);
		// Set<String> results = solrService.getChildren(accountNumber);
		// l.debug(results.toString());
		// AssertJUnit.assertEquals(1, results.size());
		l.debug("End");
	}

	@Test
	public void testFindChildChildAccounts() {
		l.debug("Start");
		lq = new UnifiedLedgerQuery();
		final String accountNumber = "60453888";
		lq.addAccountNumber(accountNumber);
		lq.setFindRelatedAccounts(true);
		// Set<String> results = solrService.getChildren(accountNumber);
		// l.debug(results.toString());
		// AssertJUnit.assertEquals(0, results.size());
		l.debug("End");
	}

	@Test
	public void testFindParentParentAccounts() {
		l.debug("Start");
		lq = new UnifiedLedgerQuery();
		final String accountNumber = "1003202046778";
		lq.addAccountNumber(accountNumber);
		lq.setFindRelatedAccounts(true);
		// Set<String> results = solrService.getParents(accountNumber);
		// l.debug(results.toString());
		// AssertJUnit.assertEquals(0, results.size());
		l.debug("End");
	}

	@Test
	public void testFindChildParentAccounts() {
		l.debug("Start");
		lq = new UnifiedLedgerQuery();
		final String accountNumber = "60453888";
		lq.addAccountNumber(accountNumber);
		lq.setFindRelatedAccounts(true);
	//	Set<String> results = solrService.getParents(accountNumber);
	//	l.debug(results.toString());
	//	AssertJUnit.assertEquals(1, results.size());
		l.debug("End");
	}

	@Test
	public void testFindAccountsRelatedToParent() {
		l.debug("Start");
		lq = new UnifiedLedgerQuery();
		final String accountNumber = "1003202046778";
		lq.addAccountNumber(accountNumber);
		lq.setFindRelatedAccounts(true);
		// Set<String> results = solrService.getRelatedAccounts(accountNumber);
		// l.debug(results.toString());
		// AssertJUnit.assertEquals(2, results.size());
		l.debug("End");
	}

	@Test
	public void testFindAccountsRelatedToChild() {
		l.debug("Start");
		lq = new UnifiedLedgerQuery();
		final String accountNumber = "60453888";
		lq.addAccountNumber(accountNumber);
		lq.setFindRelatedAccounts(true);
		// Set<String> results = solrService.getRelatedAccounts(lq);
		// l.debug(results.toString());
		// AssertJUnit.assertEquals(2, results.size());
		l.debug("End");
	}

	@Test
	public void testFindAccountsRelatedToChild2() {
		l.debug("Start");
		lq = new UnifiedLedgerQuery();
		final String accountNumber = "60453888";
		lq.addAccountNumber(accountNumber);
		lq.setFindRelatedAccounts(true);
		// Set<String> results = solrService.getRelatedAccounts(accountNumber);
		// l.debug(results.toString());
		// AssertJUnit.assertEquals(2, results.size());
		l.debug("End");
	}
}
