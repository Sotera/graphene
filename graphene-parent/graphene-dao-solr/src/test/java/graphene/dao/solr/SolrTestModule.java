package graphene.dao.solr;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;

/**
 * 
 * @author djue
 * 
 */
public class SolrTestModule {

	@Contribute(SymbolProvider.class)
	@ApplicationDefaults
	public static void defaultsSymbols(
			MappedConfiguration<String, String> configuration) {
	}

	public static void bind(ServiceBinder binder) {

	}
}