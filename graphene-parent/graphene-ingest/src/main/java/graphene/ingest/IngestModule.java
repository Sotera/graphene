package graphene.ingest;

import graphene.dao.DAOModule;
import graphene.util.UtilModule;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;

@SubModule({ UtilModule.class, DAOModule.class })
public class IngestModule {
	public static void bind(final ServiceBinder binder) {
		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
	}

}
