package graphene.model.memorydb;

import graphene.model.diskcache.DiskCache;
import graphene.model.diskcache.KryoDiskCache;

import org.apache.tapestry5.ioc.ServiceBinder;

public class MemoryDBModule {

	public final static String MAX_MEMDB_ROWS_PARAMETER = "graphene.memorydb-maxIndexRecords";
	public final static String USE_MEMDB_PARAMETER = "graphene.memorydb-useMemDB";
	/**
	 * For diskCache, if you are going to use it.
	 */
	public static final String CACHEFILELOCATION = "graphene.cache-file-location";

	public static void bind(final ServiceBinder binder) {
		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
		// binder.bind(JavaDiskCache.class);
		binder.bind(DiskCache.class, KryoDiskCache.class);

	}
}
