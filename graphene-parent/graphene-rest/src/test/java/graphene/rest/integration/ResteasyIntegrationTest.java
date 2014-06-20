package graphene.rest.integration;

import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
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
