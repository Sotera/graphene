package graphene.dao;

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_EdgeType;
import graphene.model.idl.G_EdgeTypeAccess;
import graphene.model.idl.G_IdType;
import graphene.model.idl.G_NodeTypeAccess;
import graphene.model.idl.G_PropertyKey;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.model.idl.G_UserDataAccess;
import graphene.services.EventServerImpl;
import graphene.services.FederatedEventGraphImpl;
import graphene.services.FederatedPropertyGraphImpl;
import graphene.services.G_EdgeTypeAccessImpl;
import graphene.services.G_NodeTypeAccessImpl;
import graphene.services.G_PropertyKeyTypeAccessImpl;
import graphene.services.UserServiceImpl;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;

/**
 * While this DAO Module is mostly for sharing the interface definitions (and
 * hence very closely tied to the core model module), this is also a reasonable
 * place to bind the business logic services that sit on top of the DAOs (which
 * are wired later)
 * 
 * @author djue
 * 
 */
public class DAOModule {

	public static void bind(ServiceBinder binder) {
		binder.bind(G_UserDataAccess.class, UserServiceImpl.class).eagerLoad();
		binder.bind(EventServer.class, EventServerImpl.class);
		binder.bind(FederatedPropertyGraphServer.class,
				FederatedPropertyGraphImpl.class);
		binder.bind(FederatedEventGraphServer.class,
				FederatedEventGraphImpl.class);
		binder.bind(G_NodeTypeAccess.class, G_NodeTypeAccessImpl.class);
		binder.bind(G_EdgeTypeAccess.class, G_EdgeTypeAccessImpl.class);
		binder.bind(G_PropertyKeyTypeAccess.class,
				G_PropertyKeyTypeAccessImpl.class);
	
	}

	@Contribute(G_NodeTypeAccess.class)
	public static void contributeNodeTypes(
			MappedConfiguration<String, G_IdType> configuration) {
		long i = 0;
		for (G_CanonicalPropertyType e : G_CanonicalPropertyType.values()) {
			// XXX:Fix me
			G_IdType n = G_IdType.newBuilder().setName(e.name()).setIndex(i)
					.setShortName(e.name()).setFriendlyName(e.name())
					.setTableSource("MyTable").build();
			i++;
			configuration.add(e.name(), n);
		}
	}

	@Contribute(G_EdgeTypeAccess.class)
	public static void contributeEdgeTypes(
			MappedConfiguration<String, G_EdgeType> configuration) {
		for (G_CanonicalRelationshipType e : G_CanonicalRelationshipType
				.values()) {
			G_EdgeType n = new G_EdgeType(e.name(), e.name(), e.toString(),
					(long) e.ordinal());
			configuration.add(e.name(), n);
		}
	}

	@Contribute(G_PropertyKeyTypeAccess.class)
	public static void contributePropertyKeys(
			MappedConfiguration<String, G_PropertyKey> configuration) {
		for (G_CanonicalPropertyType e : G_CanonicalPropertyType.values()) {
			G_PropertyKey n = new G_PropertyKey(e.name(), e.name(), e.name(),
					0l);
			configuration.add(e.name(), n);
		}
	}

}
