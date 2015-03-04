package graphene.util;

import graphene.util.db.JDBCUtil;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilModule {
	public static final String PASSWORD_HASH_ENCODING = "graphene.password-hash-encoding";

	public static void bind(final ServiceBinder binder) {
		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
		// binder.bind(JavaDiskCache.class);

		binder.bind(JDBCUtil.class).eagerLoad();
		binder.bind(ColorUtil.class);
	}

	public static Logger buildLogger(final Class clazz) {
		return LoggerFactory.getLogger(clazz);
	}

}
