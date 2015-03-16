package graphene.rest.services;

import graphene.dao.DAOModule;
import graphene.rest.ws.MetaSearchRS;
import graphene.rest.ws.impl.MetaSearchRSImpl;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.SubModule;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 */
@SubModule({ DAOModule.class })
public class RestModule {
	public static void bind(final ServiceBinder binder) {
		binder.bind(MetaSearchRS.class, MetaSearchRSImpl.class);
	}

	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(final Configuration<Object> singletons, final MetaSearchRS restService) {
		singletons.add(restService);
	}
}
