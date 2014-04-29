package graphene.ingest;

import graphene.dao.UnifiedCommunicationEventDAO;
import graphene.dao.neo4j.DAONeo4JEModule;
import graphene.dao.sql.DAOSQLModule;
import graphene.util.UtilModule;
import graphene.util.fs.DiskCache;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.slf4j.Logger;

@SubModule({ UtilModule.class, DAOSQLModule.class, DAONeo4JEModule.class })
public class IngestModule {
	public static void bind(ServiceBinder binder) {
		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
		binder.bind(UnifiedEntityIterator.class);



	}

	// public static UnifiedEntityIterator buildEntityRefIterator(Logger logger,
	// DiskCache<?> diskCache, UnifiedEntityDAO<?, ?> dao) {
	// return new UnifiedEntityIterator(logger, diskCache, dao);
	// }

	public static UnifiedCommunicationEventIterator buildUnifiedCommunicationEventIterator(
			Logger logger, DiskCache<?> diskCache,
			UnifiedCommunicationEventDAO<?, ?> dao) {
		return new UnifiedCommunicationEventIterator(logger, diskCache, dao);
	}

	// public static EntityRefBatchOptimizer buildEntityRefBatchOptimizer(
	// Logger logger, DiskCache<?> diskCache, UnifiedEntityDAO<?, ?> dao) {
	// return new EntityRefBatchOptimizer(logger, diskCache, dao);
	// }
	//
	// public static EntityRefExporter buildEntityRefExporter(Logger logger,
	// DiskCache<?> diskCache, UnifiedEntityDAO<?, ?> dao) {
	// return new EntityRefExporter(logger, diskCache, dao);
	// }

}
