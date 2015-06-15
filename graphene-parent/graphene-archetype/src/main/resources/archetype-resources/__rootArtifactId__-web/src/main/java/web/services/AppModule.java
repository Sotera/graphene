#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.services;

import graphene.augment.mitie.web.services.MITIEMod;
import graphene.augment.snlp.web.services.AugmentSNLPModule;
import graphene.dao.es.JestModule;
import ${package}.dao.${projectName}DAOModule;
import ${package}.model.graphserver.GraphServerModule;
import graphene.model.idl.G_SymbolConstants;
import graphene.rest.services.RestModule;
import graphene.security.noop.NoSecurityModule;
import graphene.util.PropertiesFileSymbolProvider;
import graphene.util.UtilModule;
import graphene.web.services.GrapheneModule;
import graphene.web.services.SearchBrokerService;

import java.util.Map;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.services.BeanBlockContribution;
import org.apache.tapestry5.services.BeanBlockSource;
import org.apache.tapestry5.services.DisplayBlockContribution;
import org.slf4j.Logger;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 */
@SubModule({ ${projectName}DAOModule.class, AppRestModule.class, NoSecurityModule.class, GraphServerModule.class,
		GrapheneModule.class, RestModule.class, UtilModule.class, AugmentSNLPModule.class })
public class AppModule {

	public static void bind(final ServiceBinder binder) {
		// binder.bind(MyServiceInterface.class, MyServiceImpl.class);

		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
		binder.bind(SearchBrokerService.class, SearchBrokerServiceImpl.class);
	}

	public static void contributeApplicationDefaults(final MappedConfiguration<String, Object> configuration) {
		configuration.override(G_SymbolConstants.APPLICATION_NAME, "Graphene");
		configuration.add(G_SymbolConstants.EXT_PATH, "/${groupId}-web/graph.html?&entity=");
		configuration.override(G_SymbolConstants.ENABLE_FREE_TEXT_EXTRACTION, false);
		configuration.override(G_SymbolConstants.DEFAULT_MAX_SEARCH_RESULTS, 200);
		configuration.override(G_SymbolConstants.ENABLE_GRAPH_QUERY_PATH, true);
		configuration.override(G_SymbolConstants.ENABLE_DELETE_WORKSPACES, true);

		configuration.add(MITIEMod.ENABLED, "true");
		configuration.override(JestModule.ES_DEFAULT_TIMEOUT, "30s");
		configuration.override(G_SymbolConstants.ENABLE_WORKSPACES, "false");
		configuration.override(G_SymbolConstants.ENABLE_LOGGING, false);
	}

	/**
	 * This is contributed so that the bean display will ignore Map and Class
	 * fields inside the beans we pass it.
	 * 
	 * @param configuration
	 */
	public static void contributeDefaultDataTypeAnalyzer(final MappedConfiguration<Class, String> configuration) {
		configuration.override(Object.class, "objectType");
		configuration.add(Class.class, "");
		configuration.add(Map.class, "");
	}

	public static void contributeSymbolSource(final OrderedConfiguration<SymbolProvider> configuration,
			@InjectService("ColorsSymbolProvider") final SymbolProvider c) {
		configuration.add("ColorsPropertiesFile", c, "after:SystemProperties", "before:ApplicationDefaults");
	}

	@Contribute(BeanBlockSource.class)
	public static void providePropertyBlocks(final Configuration<BeanBlockContribution> configuration) {
		final DisplayBlockContribution displayBlock = new DisplayBlockContribution("objectType", "reports/blocks",
				"objectBlock");
		configuration.add(displayBlock);
	}

	public PropertiesFileSymbolProvider buildColorsSymbolProvider(final Logger logger) {
		return new PropertiesFileSymbolProvider(logger, "graphene_optional_colors02.properties", true);
	}
}
