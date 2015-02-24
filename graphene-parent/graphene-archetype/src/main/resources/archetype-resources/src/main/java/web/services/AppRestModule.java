package ${package}.web.services;

import graphene.rest.ws.CSGraphServerRS;
import graphene.rest.ws.DataSourceServerRS;
import graphene.rest.ws.EntityServerRS;
import graphene.rest.ws.EventServerRS;
import graphene.rest.ws.GraphmlServerRS;
import graphene.rest.ws.UDSessionRS;
import graphene.rest.ws.impl.CSGraphServerRSImpl;
import graphene.rest.ws.impl.DataSourceServerRSImpl;
import graphene.rest.ws.impl.EntityServerRSImpl;
import graphene.rest.ws.impl.GraphmlServerRSImpl;
import graphene.rest.ws.impl.UDSessionRSImpl;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.tynamo.resteasy.ResteasyPackageManager;
import org.tynamo.resteasy.ResteasySymbols;

/**
 * Bind all your REST service interfaces to their implementations here. This
 * module is loaded by {@link AppModule} in the services package.
 * 
 * @author djue
 * 
 */
public class AppRestModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(EntityServerRS.class, EntityServerRSImpl.class);
		binder.bind(GraphmlServerRS.class, GraphmlServerRSImpl.class);

		binder.bind(UDSessionRS.class, UDSessionRSImpl.class); // MFM

		binder.bind(DataSourceServerRS.class, DataSourceServerRSImpl.class);
		binder.bind(CSGraphServerRS.class, CSGraphServerRSImpl.class);

	}

	/**
	 * Contributions to the RESTeasy main Application, insert all your RESTeasy
	 * singleton services here.
	 * <p/>
	 * 
	 */

	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons,
			GraphmlServerRS restService) {
		singletons.add(restService);
	}

	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons,
			CSGraphServerRS restService) {
		singletons.add(restService);
	}


	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons,
			EventServerRS restService) {
		singletons.add(restService);
	}

	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons,
			EntityServerRS restService) {
		singletons.add(restService);
	}

	// MFM added 1/3/14
	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons,
			UDSessionRS restService) {
		singletons.add(restService);
	}

	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons,
			DataSourceServerRS restService) {
		singletons.add(restService);
	}

	@Contribute(SymbolProvider.class)
	@ApplicationDefaults
	public static void provideSymbols(
			MappedConfiguration<String, String> configuration) {
		configuration.add(ResteasySymbols.MAPPING_PREFIX, "/rest");

		// This disables the autoscanning of ${package}.web.rest
		configuration.add(ResteasySymbols.AUTOSCAN_REST_PACKAGE, "false");
	}

	/**
	 * Inside this method, add any packages that contain the annotated
	 * interfaces for REST services. The actual mapping (binding) of individual
	 * implementations to the services is done at the top of this class.
	 * 
	 * NOTE Only for autobuilding, which we aren't using here.
	 * 
	 * @param configuration
	 */
	@Contribute(ResteasyPackageManager.class)
	public static void resteasyPackageManager(
			Configuration<String> configuration) {
		configuration.add("${package}.web.rest.autobuild");
	}

}
