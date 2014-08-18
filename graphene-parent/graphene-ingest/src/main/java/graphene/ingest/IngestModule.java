package graphene.ingest;

import graphene.dao.UnifiedCommunicationEventDAO;
import graphene.dao.sql.DAOSQLModule;
import graphene.util.UtilModule;
import graphene.util.fs.DiskCache;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.slf4j.Logger;

@SubModule({ UtilModule.class, DAOSQLModule.class })
public class IngestModule {
	public static void bind(ServiceBinder binder) {
		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
		binder.bind(UnifiedEntityIterator.class);
	}

	public static UnifiedCommunicationEventIterator buildUnifiedCommunicationEventIterator(
			Logger logger, DiskCache<?> diskCache,
			UnifiedCommunicationEventDAO<?, ?> dao) {
		return new UnifiedCommunicationEventIterator(logger, diskCache, dao);
	}

}
