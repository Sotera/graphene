package graphene.util.net;

import graphene.util.net.NetworkUtils;

import org.testng.AssertJUnit;
import org.testng.annotations.Test;

public class NetworkUtilsTest {
	@Test
	public void test1() {
		AssertJUnit.assertTrue(NetworkUtils.isServerAlive("0.0.0.0", 135));
		//AssertJUnit.assertFalse(NetworkUtils.isServerAliveOn80("localhost"));
		//AssertJUnit.assertFalse(NetworkUtils.isServerAliveOn80("127.0.0.1"));
	}
	@Test
	public void testPing() {
		AssertJUnit.assertTrue(NetworkUtils.pingable("localhost"));
		AssertJUnit.assertFalse(NetworkUtils.pingable("fake"));
	}

}
