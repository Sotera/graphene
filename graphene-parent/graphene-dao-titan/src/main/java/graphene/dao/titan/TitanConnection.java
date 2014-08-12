/**
 * 
 */
package graphene.dao.titan;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.rexster.client.RexsterClient;
import com.tinkerpop.rexster.client.RexsterClientFactory;

/**
 * @author djue
 * 
 */
public class TitanConnection {

	public static  Logger logger = LoggerFactory
			.getLogger(TitanConnection.class);

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		RexsterClient client;
		try {
			client = RexsterClientFactory.open("localhost", "graph");

			List<Map<String, Object>> result;
			result = client.execute("g.V('name','saturn').in('father').map");
			logger.debug(result.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
