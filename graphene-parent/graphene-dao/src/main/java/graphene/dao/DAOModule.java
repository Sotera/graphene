package graphene.dao;

import graphene.model.idl.G_UserDataAccess;
import graphene.services.UserServiceImpl;

import org.apache.tapestry5.ioc.ServiceBinder;

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
		binder.bind(G_UserDataAccess.class, UserServiceImpl.class);
	}
}
