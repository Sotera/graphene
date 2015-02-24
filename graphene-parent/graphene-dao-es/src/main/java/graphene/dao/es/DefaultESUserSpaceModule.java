package graphene.dao.es;

import graphene.dao.GroupDAO;
import graphene.dao.PermissionDAO;
import graphene.dao.RoleDAO;
import graphene.dao.UserDAO;
import graphene.dao.UserGroupDAO;
import graphene.dao.UserRoleDAO;
import graphene.dao.UserWorkspaceDAO;
import graphene.dao.WorkspaceDAO;
import graphene.dao.es.impl.GroupDAOESImpl;
import graphene.dao.es.impl.RoleDAOESImpl;
import graphene.dao.es.impl.UserDAOESImpl;
import graphene.dao.es.impl.UserGroupDAOESImpl;
import graphene.dao.es.impl.UserRoleDAOESImpl;
import graphene.dao.es.impl.UserWorkspaceDAOESImpl;
import graphene.dao.es.impl.WorkspaceDAOESImpl;
import graphene.services.SimplePermissionDAOImpl;

import org.apache.tapestry5.ioc.ServiceBinder;

public class DefaultESUserSpaceModule {
	public static void bind(final ServiceBinder binder) {
		// Userspace
		binder.bind(GroupDAO.class, GroupDAOESImpl.class).eagerLoad();
		binder.bind(WorkspaceDAO.class, WorkspaceDAOESImpl.class).eagerLoad();
		binder.bind(UserDAO.class, UserDAOESImpl.class).eagerLoad();
		binder.bind(UserGroupDAO.class, UserGroupDAOESImpl.class);
		binder.bind(UserWorkspaceDAO.class, UserWorkspaceDAOESImpl.class);
		binder.bind(UserRoleDAO.class, UserRoleDAOESImpl.class);
		binder.bind(RoleDAO.class, RoleDAOESImpl.class);
		binder.bind(PermissionDAO.class, SimplePermissionDAOImpl.class).eagerLoad();
	}

}
