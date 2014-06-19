package graphene.util;

import graphene.util.db.JDBCUtil;
import graphene.util.fs.DiskCache;
import graphene.util.fs.KryoDiskCache;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilModule {
	public static void bind(ServiceBinder binder) {
		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
		// binder.bind(JavaDiskCache.class);
		binder.bind(DiskCache.class, KryoDiskCache.class);
		binder.bind(JDBCUtil.class).eagerLoad();
	}

	public static Logger buildLogger(Class clazz) {
		return LoggerFactory.getLogger(clazz);
	}

}
