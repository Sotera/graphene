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
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idlhelper.RangeHelper;
import graphene.web.components.BasicDataTable;

import java.util.List;

import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.BeanModel;
import org.apache.tapestry5.ioc.annotations.Inject;

public class EntityQueryList extends BasicDataTable {
	@Inject
	protected LoggingDAO loggingDao;
	@Property
	private G_EntityQuery current;

	@Property
	private G_PropertyMatchDescriptor currentPmd;

	@Persist
	private BeanModel<G_EntityQuery> model;

	@Property
	private List<G_EntityQuery> list;

	public String getCurrentRange() {
		return RangeHelper.toString(currentPmd);
	}

	public BeanModel<G_EntityQuery> getModel() {
		if (model == null) {
			model = beanModelSource.createDisplayModel(G_EntityQuery.class, resources.getMessages());
			model.exclude("caseSensitive", "searchFreeText", "initiatorId", "attributevalues", "minimumscore",
					"minsecs", "maxsecs", "sortcolumn", "sortfield", "firstresult", "maxresult", "datasource",
					"userId", "sortascending", "id", "schema");

			model.get("propertyMatchDescriptors").sortable(true);
			model.reorder("timeinitiated", "username", "propertyMatchDescriptors");
		}
		return model;
	}

	@SetupRender
	private void loadQueries() {
		list = loggingDao.getQueries(null, null, 0, 1000);
	}
}
