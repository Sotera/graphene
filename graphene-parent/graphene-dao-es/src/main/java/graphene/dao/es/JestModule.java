package graphene.dao.es;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.services.ClasspathResourceSymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolSource;

public class JestModule {
	public static void bind(ServiceBinder binder) {

	}

	public final static String ES_SERVER = "graphene.es-server";
	public final static String ES_SEARCH_INDEX = "graphene.es-search-index";
	public static final String ES_USER_INDEX = "graphene.es-user-index";
	public static final String ES_GROUP_INDEX = "graphene.es-group-index";
	public static final String ES_WORKSPACE_INDEX = "graphene.es-workspace-index";
	public static final String ES_USERGROUP_INDEX = "graphene.es-usergroup-index";
	public static final String ES_USERWORKSPACE_INDEX = "graphene.es-userworkspace-index";
	public static final String ES_LOGGING_INDEX = "graphene.es-logging-index";
	public static final String ES_PERSISTED_GRAPH_INDEX = "graphene.es-persisted-graph-index";
	public static final String ES_PERSISTED_GRAPH_TYPE = "graphene.es-persisted-graph-type";

	/**
	 * Tell Tapestry to look for an elastic search properties file in the
	 * WEB-INF/classes folder of the war.
	 * 
	 * @param configuration
	 */
	@Contribute(SymbolSource.class)
	public void contributePropertiesFileAsSymbols(
			OrderedConfiguration<SymbolProvider> configuration) {
		configuration.add("DatabaseConfiguration",
				new ClasspathResourceSymbolProvider("es.properties"),
				"after:SystemProperties", "before:ApplicationDefaults");
	}

	/**
	 * 
	 * @param server
	 * @return
	 */
	public static JestClient buildJestClient(
			@Symbol(ES_SERVER) final String server) {

		// Construct a new Jest client according to configuration via factory
		JestClientFactory factory = new JestClientFactory();
		HttpClientConfig clientConfig = new HttpClientConfig.Builder(server)
				.build();
		factory.setHttpClientConfig(clientConfig);

		JestClient c = factory.getObject();

		return c;
	}

}
