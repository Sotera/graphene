package graphene.rest.services;

import graphene.rest.ws.ReloadableEchoResource;
import graphene.rest.ws.ReloadableEchoResourceImpl;

import org.apache.tapestry5.internal.InternalConstants;
import org.apache.tapestry5.internal.InternalSymbols;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.services.ApplicationDefaults;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.tynamo.resteasy.ResteasyPackageManager;
import org.tynamo.resteasy.ResteasySymbols;

public class AppModule
{

	public static void bind(ServiceBinder binder)
	{
		binder.bind(ReloadableEchoResource.class, ReloadableEchoResourceImpl.class);

	}


	/**
	 * Contributions to the RESTeasy main Application, insert all your RESTeasy singleton services here.
	 * <p/>
	 *
	 */
	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons,
	                                         ReloadableEchoResource reloadableEchoResource)
	{
		singletons.add(reloadableEchoResource);
	}

	@Contribute(SymbolProvider.class)
	@ApplicationDefaults
	public static void provideSymbols(MappedConfiguration<String, String> configuration)
	{
		configuration.add(ResteasySymbols.MAPPING_PREFIX, "/mycustomresteasyprefix");
		configuration.add(InternalConstants.TAPESTRY_APP_PACKAGE_PARAM, "graphene.rest");
		 configuration.add(InternalSymbols.APP_PACKAGE_PATH, "graphene/rest");
	}

	@Contribute(ResteasyPackageManager.class)
	public static void resteasyPackageManager(Configuration<String> configuration)
	{
		configuration.add("graphene.rest.ws.autobuild");
	}

}
