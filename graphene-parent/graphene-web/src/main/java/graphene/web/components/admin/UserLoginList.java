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

package graphene.web.components.admin;

import graphene.dao.LoggingDAO;
import graphene.model.idl.G_UserLoginEvent;
import graphene.web.components.BasicDataTable;

import java.util.List;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;

public class UserLoginList extends BasicDataTable {
	@Inject
	protected LoggingDAO loggingDao;
	@Property
	private G_UserLoginEvent current;

	@Persist
	private BeanModel<G_UserLoginEvent> model;
	@Property
	private List<G_UserLoginEvent> list;

	public BeanModel<G_UserLoginEvent> getModel() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(G_UserLoginEvent.class, resources.getMessages());
			model.exclude("id", "schema", "userid");
			model.reorder("timeinitiated", "username");
		}
		return model;
	}

	@SetupRender
	private void loadQueries() {
		list = loggingDao.getUserLoginEvents(null, 0, 200000);
	}
}
