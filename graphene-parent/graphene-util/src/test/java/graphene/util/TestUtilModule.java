package graphene.util;

import graphene.util.fs.DiskCache;
import graphene.util.fs.KryoDiskCache;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TestUtilModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(DiskCache.class, KryoDiskCache.class);

	}

	public static Logger buildLogger(Class clazz) {
		return LoggerFactory.getLogger(clazz);
	}

	// make configuration from 'test.properties' on the classpath available as
	// symbols
	public PropertiesFileSymbolProvider buildClasspathPropertiesFileSymbolProvider(
			Logger logger) {
		return new PropertiesFileSymbolProvider(logger, "test.properties", true);
	}

	// make configuration from 'test2.properties' on the filesystem available as
	// symbols
	public PropertiesFileSymbolProvider buildFilesystemPropertiesFileSymbolProvider(
			Logger logger) {
		return new PropertiesFileSymbolProvider(logger,
				"src/test/resources/some/path/to/a/file/test2.properties",
				false);
	}

	public static void contributeSymbolSource(
			OrderedConfiguration<SymbolProvider> configuration,
			@InjectService("ClasspathPropertiesFileSymbolProvider") SymbolProvider classpathPropertiesFileSymbolProvider,
			@InjectService("FilesystemPropertiesFileSymbolProvider") SymbolProvider filesystemPropertiesFileSymbolProvider) {
		configuration.add("ClasspathPropertiesFile",
				classpathPropertiesFileSymbolProvider,
				"after:SystemProperties", "before:ApplicationDefaults");

		configuration.add("FilesystemPropertiesFile",
				filesystemPropertiesFileSymbolProvider,
				"after:ClasspathPropertiesFile", "before:ApplicationDefaults");
	}

}
