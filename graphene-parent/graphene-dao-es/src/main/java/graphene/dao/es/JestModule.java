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

package graphene.dao.es;

import io.searchbox.client.JestClient;
import io.searchbox.client.JestClientFactory;
import io.searchbox.client.config.HttpClientConfig;
import io.searchbox.client.http.JestHttpClient;

import java.util.concurrent.TimeUnit;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.services.ClasspathResourceSymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolSource;

public class JestModule {
	public final static String ES_SERVER = "graphene.es-server";
	public final static String ES_ENABLE_DISCOVERY = "graphene.es-enable-discovery";
	public final static String ES_ENABLE_MULTITHREADED = "graphene.es-enable-multithreaded";
	public final static String ES_SEARCH_INDEX = "graphene.es-search-index";
	public static final String ES_USER_INDEX = "graphene.es-user-index";
	public static final String ES_GROUP_INDEX = "graphene.es-group-index";
	public static final String ES_ROLE_INDEX = "graphene.es-role-index";
	public static final String ES_WORKSPACE_INDEX = "graphene.es-workspace-index";
	public static final String ES_USERGROUP_INDEX = "graphene.es-usergroup-index";
	public static final String ES_USERWORKSPACE_INDEX = "graphene.es-userworkspace-index";
	public static final String ES_USERROLE_INDEX = "graphene.es-userrole-index";

	public static final String ES_PERSISTED_GRAPH_INDEX = "graphene.es-persisted-graph-index";
	public static final String ES_PERSISTED_GRAPH_TYPE = "graphene.es-persisted-graph-type";
	public static final String ES_DEFAULT_TIMEOUT = "graphene.es-default-timeout";

	public static final String ES_LOGGING_INDEX = "graphene.es-logging-index";
	public static final String ES_LOGGING_SEARCH_TYPE = "graphene.es-logging-search-type";
	public static final String ES_LOGGING_GRAPHQUERY_TYPE = "graphene.es-logging-graph-query-type";
	public static final String ES_LOGGING_REPORT_VIEW_TYPE = "graphene.es-logging-report-view-type";
	public static final String ES_LOGGING_EXPORT_TYPE = "graphene.es-logging-export-type";
	public static final String ES_LOGGING_USER_LOGIN_TYPE = "graphene.es-logging-user-login-type";

	public static final String ES_READ_DELAY_MS = "graphene.es-read-delay-ms";

	public static final String ES_INGEST_INDEX = "ingest_index";
	public static final String ES_INGEST_TYPE = "ingest_type";
	public static final String ES_INGEST_ENCODED_FILE_TYPE = "ingest_encoded_file_type";
	public static final String ES_CROSSREFERENCE_TYPE = "crossreference_type";

	private static JestHttpClient c;

	public static void bind(final ServiceBinder binder) {

	}

	/**
	 * 
	 * @param server
	 * @return
	 */
	public static JestClient buildJestClient(@Symbol(ES_SERVER) final String server,
			@Symbol(ES_ENABLE_DISCOVERY) final boolean discovery,
			@Symbol(ES_ENABLE_MULTITHREADED) final boolean multithreaded) {
		if (c == null) {
			// Construct a new Jest client according to configuration via
			// factory
			final JestClientFactory factory = new JestClientFactory();
			final HttpClientConfig clientConfig = new HttpClientConfig.Builder(server)
					.discoveryFrequency(3600000, TimeUnit.MILLISECONDS).discoveryEnabled(discovery)
					.multiThreaded(multithreaded).readTimeout(30000).build();

			factory.setHttpClientConfig(clientConfig);

			c = (JestHttpClient) factory.getObject();
		}
		return c;
	}

	public void contributeApplicationDefaults(final MappedConfiguration<String, Object> configuration) {
		// Elastic Search defaults (if no es.properties file is provided)
		// note that elastic search index names MUST be lower case!!!!
		configuration.add(JestModule.ES_SERVER, "localhost:9200");
		configuration.add(JestModule.ES_ENABLE_DISCOVERY, false);
		configuration.add(JestModule.ES_ENABLE_MULTITHREADED, true);
		configuration.add(JestModule.ES_SEARCH_INDEX, "searchindex");
		configuration.add(JestModule.ES_DEFAULT_TIMEOUT, "30s");
		/**
		 * The default for a ES refresh is 1 second (1000ms), which allows read
		 * consistency.
		 */
		configuration.add(JestModule.ES_READ_DELAY_MS, 1000);

		// Userspace defaults
		configuration.add(JestModule.ES_USER_INDEX, "grapheneuser");
		configuration.add(JestModule.ES_GROUP_INDEX, "graphenegroup");
		configuration.add(JestModule.ES_ROLE_INDEX, "graphenerole");
		configuration.add(JestModule.ES_WORKSPACE_INDEX, "grapheneworkspace");
		configuration.add(JestModule.ES_USERWORKSPACE_INDEX, "grapheneuserworkspace");
		configuration.add(JestModule.ES_USERGROUP_INDEX, "grapheneusergroup");
		configuration.add(JestModule.ES_USERROLE_INDEX, "grapheneuserrole");
		// Logging defaults
		configuration.add(JestModule.ES_LOGGING_INDEX, "graphenelogging");
		configuration.add(JestModule.ES_LOGGING_EXPORT_TYPE, "export");
		configuration.add(JestModule.ES_LOGGING_GRAPHQUERY_TYPE, "graphquery");
		configuration.add(JestModule.ES_LOGGING_REPORT_VIEW_TYPE, "reportview");
		configuration.add(JestModule.ES_LOGGING_SEARCH_TYPE, "search");
		configuration.add(JestModule.ES_LOGGING_USER_LOGIN_TYPE, "userlogin");

		// Graph persist defaults
		configuration.add(JestModule.ES_PERSISTED_GRAPH_INDEX, "graphenepersistedgraphs");
		configuration.add(JestModule.ES_PERSISTED_GRAPH_TYPE, "csgraph");

	}

	/**
	 * Tell Tapestry to look for an elastic search properties file in the
	 * WEB-INF/classes folder of the war.
	 * 
	 * @param configuration
	 */
	@Contribute(SymbolSource.class)
	public void contributePropertiesFileAsSymbols(final OrderedConfiguration<SymbolProvider> configuration) {
		configuration.add("DatabaseConfiguration", new ClasspathResourceSymbolProvider("es.properties"),
				"after:SystemProperties", "before:ApplicationDefaults");
	}

}
