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
import graphene.model.idl.G_ReportViewEvent;
import graphene.web.components.BasicDataTable;

import java.util.List;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ReportViewList extends BasicDataTable {
	@Inject
	protected LoggingDAO loggingDao;
	@Property
	private G_ReportViewEvent current;
	@Persist
	private BeanModel<G_ReportViewEvent> model;
	@Property
	private List<G_ReportViewEvent> list;

	public BeanModel<G_ReportViewEvent> getModel() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(G_ReportViewEvent.class, resources.getMessages());
			model.exclude("schema", "userId", "id");
			model.reorder("timeinitiated", "username", "reportId", "reporttype", "reportpagelink");
		}
		return model;
	}

	@SetupRender
	private void loadQueries() {
		list = loggingDao.getReportViewEvents(null, 0, 200000);
	}
}
