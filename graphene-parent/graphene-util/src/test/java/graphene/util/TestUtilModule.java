package graphene.util;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtilModule {
	public static void bind(final ServiceBinder binder) {

	}

	public static Logger buildLogger(final Class clazz) {
		return LoggerFactory.getLogger(clazz);
	}

	public static void contributeSymbolSource(
			final OrderedConfiguration<SymbolProvider> configuration,
			@InjectService("ClasspathPropertiesFileSymbolProvider") final SymbolProvider classpathPropertiesFileSymbolProvider,
			@InjectService("FilesystemPropertiesFileSymbolProvider") final SymbolProvider filesystemPropertiesFileSymbolProvider) {
		configuration.add("ClasspathPropertiesFile", classpathPropertiesFileSymbolProvider, "after:SystemProperties",
				"before:ApplicationDefaults");

		configuration.add("FilesystemPropertiesFile", filesystemPropertiesFileSymbolProvider,
				"after:ClasspathPropertiesFile", "before:ApplicationDefaults");
	}

	// make configuration from 'test.properties' on the classpath available as
	// symbols
	public PropertiesFileSymbolProvider buildClasspathPropertiesFileSymbolProvider(final Logger logger) {
		return new PropertiesFileSymbolProvider(logger, "test.properties", true);
	}

	// make configuration from 'test2.properties' on the filesystem available as
	// symbols
	public PropertiesFileSymbolProvider buildFilesystemPropertiesFileSymbolProvider(final Logger logger) {
		return new PropertiesFileSymbolProvider(logger, "src/test/resources/some/path/to/a/file/test2.properties",
				false);
	}

}
