package graphene.augment.mitie.web.services;

import graphene.augment.mitie.dao.MITIERestAPIConnection;
import graphene.augment.mitie.dao.MITIERestAPIConnectionImpl;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.services.ClasspathResourceSymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.services.LibraryMapping;

/**
 * Renamed to MITIEMod because of string length issues in the manifest.mf keeps
 * the module from autoloading.
 * 
 * @author djue
 * 
 */
public class MITIEMod {

	public static final String BASIC_AUTH = "mitie.basic-auth";
	public static final String BASE_URL = "mitie.base-url";
	public static final String ENABLED = "mitie.enabled";

	public static void contributeComponentClassResolver(final Configuration<LibraryMapping> configuration) {
		configuration.add(new LibraryMapping("mitie", "graphene.augment.mitie.web"));
	}

	public MITIERestAPIConnection buildMITIEConnection(@Symbol(BASIC_AUTH) final String auth,
			@Symbol(BASE_URL) final String url) {
		final MITIERestAPIConnection m = new MITIERestAPIConnectionImpl(url, auth);
		return m;
	}

	public void contributeApplicationDefaults(final MappedConfiguration<String, String> configuration) {

		// MITIE defaults (if no mitie.properties file is provided, or not in
		// catalina.properties, etc)
		configuration.add(BASIC_AUTH, "");// obviously just an example
		configuration.add(BASE_URL, "http://localhost/mitie");// obviously just
																// an example
	}

	/**
	 * Tell Tapestry to look for an MITIE properties file in the WEB-INF/classes
	 * folder of the war.
	 * 
	 * @param configuration
	 */
	@Contribute(SymbolSource.class)
	public void contributePropertiesFileAsSymbols(final OrderedConfiguration<SymbolProvider> configuration) {
		configuration.add("MitieConfiguration", new ClasspathResourceSymbolProvider("mitie.properties"),
				"after:SystemProperties", "before:ApplicationDefaults");
	}
}
