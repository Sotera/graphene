package graphene.dao.sql;

import graphene.dao.GroupDAO;
import graphene.dao.PermissionDAO;
import graphene.dao.RoleDAO;
import graphene.dao.UserDAO;
import graphene.dao.UserGroupDAO;
import graphene.dao.UserRoleDAO;
import graphene.dao.UserWorkspaceDAO;
import graphene.dao.WorkspaceDAO;
import graphene.dao.sql.impl.GroupDAOSQLImpl;
import graphene.dao.sql.impl.RoleDAOSQLImpl;
import graphene.dao.sql.impl.UserDAOSQLImpl;
import graphene.dao.sql.impl.UserGroupDAOSQLImpl;
import graphene.dao.sql.impl.UserRoleDAOSQLImpl;
import graphene.dao.sql.impl.UserWorkspaceDAOSQLImpl;
import graphene.dao.sql.impl.WorkspaceDAOSQLImpl;
import graphene.services.SimplePermissionDAOImpl;

import org.apache.tapestry5.ioc.ServiceBinder;

public class DefaultSQLUserSpaceModule {
	public static void bind(final ServiceBinder binder) {
		// Userspace
		binder.bind(GroupDAO.class, GroupDAOSQLImpl.class).eagerLoad();
		binder.bind(WorkspaceDAO.class, WorkspaceDAOSQLImpl.class).eagerLoad();
		binder.bind(UserDAO.class, UserDAOSQLImpl.class).eagerLoad();
		binder.bind(UserGroupDAO.class, UserGroupDAOSQLImpl.class);
		binder.bind(UserWorkspaceDAO.class, UserWorkspaceDAOSQLImpl.class);
		binder.bind(UserRoleDAO.class, UserRoleDAOSQLImpl.class);
		binder.bind(RoleDAO.class, RoleDAOSQLImpl.class);
		binder.bind(PermissionDAO.class, SimplePermissionDAOImpl.class).eagerLoad();
	}
}
