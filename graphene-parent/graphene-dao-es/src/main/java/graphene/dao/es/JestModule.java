package graphene.dao.es;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Symbol;

public class JestModule {
	public static void bind(ServiceBinder binder) {

	}

	public final static String ES_SERVER = "graphene.es-server";

	/**
	 * 
	 * @param server
	 * @return
	 */
	public static JestClient buildJestClient(
			@Symbol(ES_SERVER) final String server) {

		// if (System.getenv("SEARCHBOX_URL") != null) {
		// // Heroku
		// connectionUrl = System.getenv("SEARCHBOX_URL");
		//
		// } else if (System.getenv("VCAP_SERVICES") != null) {
		// // CloudFoundry
		// Map result = new ObjectMapper().readValue(
		// System.getenv("VCAP_SERVICES"), HashMap.class);
		// connectionUrl = (String) ((Map) ((Map) ((List) result
		// .get("searchly-n/a")).get(0)).get("credentials"))
		// .get("uri");
		// } else {
		// // generic or CloudBees
		// connectionUrl = "http://site:your-api-key@api.searchbox.io";
		// // connectionUrl = "http://localhost:9200"
		// }

		// Configuration

		// Construct a new Jest client according to configuration via factory
		JestClientFactory factory = new JestClientFactory();
		HttpClientConfig clientConfig = new HttpClientConfig.Builder(server)
				.build();
		factory.setHttpClientConfig(clientConfig);

		JestClient c = factory.getObject();

		return c;
	}
}
