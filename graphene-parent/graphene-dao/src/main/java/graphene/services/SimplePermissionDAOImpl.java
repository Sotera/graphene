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

/**
 * 
 */
package graphene.services;

import graphene.dao.PermissionDAO;
import graphene.model.idl.G_Permission;
import graphene.model.idl.G_Role;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.joda.time.DateTime;
import org.slf4j.Logger;

/**
 * @author djue
 * 
 */
public class SimplePermissionDAOImpl implements PermissionDAO {

	private static final G_Permission READ = new G_Permission("R", "read", "Read Permission", DateTime.now()
			.getMillis());
	private static final G_Permission WRITE = new G_Permission("W", "write", "Write Permission", DateTime.now()
			.getMillis());
	private static final G_Permission EXECUTE = new G_Permission("E", "execute", "Execute Permission", DateTime.now()
			.getMillis());
	private static final G_Permission DELETE = new G_Permission("D", "delete", "Delete Permission", DateTime.now()
			.getMillis());
	@Inject
	private Logger logger;

	public SimplePermissionDAOImpl() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public List<G_Permission> getForRole(final G_Role role) {
		return getForRole(role.getName());
	}

	@Override
	public List<G_Permission> getForRole(final String role) {
		final ArrayList<G_Permission> list = new ArrayList<G_Permission>();
		if ("admin".equals(role)) {
			list.add(READ);
			list.add(WRITE);
			list.add(EXECUTE);
			list.add(DELETE);
		} else {
			list.add(READ);
		}
		logger.debug("Returning permissions: " + list);
		return list;
	}

}
