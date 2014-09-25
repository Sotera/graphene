package graphene.rest.services;

import graphene.dao.DAOModule;
import graphene.rest.ws.EntitySearchRS;
import graphene.rest.ws.EventServerRS;
import graphene.rest.ws.GraphDemo;
import graphene.rest.ws.GraphDemoImpl;
import graphene.rest.ws.MetaSearchRS;
import graphene.rest.ws.impl.EntitySearchRSImpl;
import graphene.rest.ws.impl.EventServerRSImpl;
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
	public static void bind(ServiceBinder binder) {
		binder.bind(EntitySearchRS.class, EntitySearchRSImpl.class);
		binder.bind(GraphDemo.class, GraphDemoImpl.class);
		binder.bind(MetaSearchRS.class, MetaSearchRSImpl.class);
		binder.bind(EventServerRS.class, EventServerRSImpl.class);
	}

	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons,
			GraphDemo restService) {
		singletons.add(restService);
	}

	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons,
			EntitySearchRS restService) {
		singletons.add(restService);
	}

	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons,
			MetaSearchRS restService) {
		singletons.add(restService);
	}

	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(Configuration<Object> singletons,
			EventServerRS restService) {
		singletons.add(restService);
	}
}
