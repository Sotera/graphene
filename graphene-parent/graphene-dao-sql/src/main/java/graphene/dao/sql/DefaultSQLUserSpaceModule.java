/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
