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

package graphene.rest.integration;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.testng.Assert;
import org.tynamo.test.AbstractContainerTest;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class ResteasyIntegrationTest extends AbstractContainerTest
{

	//@BeforeClass
	public void startContainer() throws Exception
	{
		String reserveNetworkPort = System.getProperty("reserved.network.port");

		if (reserveNetworkPort != null)
		{
			port = Integer.valueOf(reserveNetworkPort);
			BASEURI = "http://localhost:" + port + "/";
		}

		super.startContainer();
	}

	//@Test
	public void testPingResource() throws Exception
	{
		HtmlPage page = webClient.getPage(BASEURI + "mycustomresteasyprefix/ping");
		assertXPathPresent(page, "//h1[contains(text(),'PONG')]");
	}

	//@Test
	public void testEchoResource() throws Exception
	{
		ClientRequest request = new ClientRequest(BASEURI + "mycustomresteasyprefix/echo/Hellow World!");
		ClientResponse<String> response = request.get(String.class);
		Assert.assertEquals(response.getStatus(), 200);
		Assert.assertEquals(response.getEntity(), "Hellow World!");
	}
}
