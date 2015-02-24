package ${package}.web.services;

import ${package}.dao.InstagramDAOModule;
import ${package}.model.graphserver.GraphServerModule;
import graphene.model.idl.G_SymbolConstants;
import graphene.rest.services.RestModule;
import graphene.util.PropertiesFileSymbolProvider;
import graphene.util.UtilModule;
import graphene.web.security.NoSecurityModule;
import graphene.web.services.GrapheneModule;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.slf4j.Logger;

/**
 * This module is automatically included as part of the Tapestry IoC Registry if
 * <em>tapestry.execution-mode</em> includes <code>development</code>.
 */
@SubModule({ ${projectName}DAOModule.class, AppRestModule.class,
		GraphServerModule.class, GrapheneModule.class, RestModule.class,
		UtilModule.class, NoSecurityModule.class })
public class DevelopmentModule {

	public static void bind(ServiceBinder binder) {
		// binder.bind(MyServiceInterface.class, MyServiceImpl.class);

		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.

	}

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, Object> configuration) {
		configuration.add(G_SymbolConstants.APPLICATION_NAME,
				"Graphene-${projectName} Mock");
		configuration.add(G_SymbolConstants.APPLICATION_CONTACT, "{projectName}");
		configuration.add(SymbolConstants.APPLICATION_VERSION,
				"${graphene.application-version}-DEV");
	}

	public PropertiesFileSymbolProvider buildColorsSymbolProvider(Logger logger) {
		return new PropertiesFileSymbolProvider(logger,
				"graphene_optional_colors01.properties", true);
	}

	public static void contributeSymbolSource(
			OrderedConfiguration<SymbolProvider> configuration,
			@InjectService("ColorsSymbolProvider") SymbolProvider c) {
		configuration.add("ColorsPropertiesFile", c, "after:SystemProperties",
				"before:ApplicationDefaults");
	}
}