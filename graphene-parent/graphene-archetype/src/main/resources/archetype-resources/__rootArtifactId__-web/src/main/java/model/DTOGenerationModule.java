#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model;

import graphene.util.PropertiesFileSymbolProvider;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.internal.services.ClasspathResourceSymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.slf4j.Logger;

/**
 * A module used specifically for the generation of DTO Objects. This may need
 * closer integration with the ingest modules. Note that right now this class
 * uses the same database.properties as the application does for running.
 * Potentially these could be different if the user you log in with can't read
 * the metadata needed by QueryDSL.
 * 
 * @author djue
 * 
 */
public class DTOGenerationModule {
	public static void contributeSymbolSource(
			final OrderedConfiguration<SymbolProvider> configuration,
			@InjectService("ColorsSymbolProvider") final SymbolProvider c) {
		configuration.add("ColorsPropertiesFile", c, "after:SystemProperties",
				"before:ApplicationDefaults");
	}

	public PropertiesFileSymbolProvider buildColorsSymbolProvider(
			final Logger logger) {
		return new PropertiesFileSymbolProvider(logger,
				"graphene_optional_colors01.properties", true);
	}

	@Contribute(SymbolSource.class)
	public void contributePropertiesFileAsSymbols(
			final OrderedConfiguration<SymbolProvider> configuration) {
		configuration.add("DatabaseConfiguration",
				new ClasspathResourceSymbolProvider("database.properties"),
				"after:SystemProperties", "before:ApplicationDefaults");
	}

}
