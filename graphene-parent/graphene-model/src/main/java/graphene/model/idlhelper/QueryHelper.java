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

package graphene.model.idlhelper;

import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_PropertyMatchDescriptor;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.joda.time.DateTime;

public class QueryHelper extends G_EntityQuery {
	public QueryHelper(final G_PropertyMatchDescriptor... pmd) {

		setCaseSensitive(false);
		setFirstResult(0l);
		setInitiatorId("");
		setMaxResult((long) 1);
		setMinimumScore(0.0d);
		setSearchFreeText(true);
		setTargetSchema("");
		setTimeInitiated(DateTime.now().getMillis());
//		setId("" + DateTime.now().getMillis());
		setId(UUID.randomUUID().toString());
		setUserId("Unknown");
		setUsername("Unknown");
		final List<G_PropertyMatchDescriptor> list = new ArrayList<G_PropertyMatchDescriptor>();
		for (final G_PropertyMatchDescriptor pm : pmd) {
			if (pm != null) {
				list.add(pm);
			}
		}
		setPropertyMatchDescriptors(list);
	}

	// TODO: Stop using search tuples and start using property match descriptors
	// public QueryHelper(final List<G_PropertyMatchDescriptor> tuples) {
	// setPropertyMatchDescriptors(tuples);
	// setCaseSensitive(false);
	// final List<String> filters = new ArrayList<String>();
	// setFilters(filters);
	// setFirstResult(0l);
	// setInitiatorId("");
	// setMaxResult((long) 1);
	// setMinimumScore(0.0d);
	// setSearchFreeText(true);
	// setTargetSchema("");
	// setTimeInitiated(DateTime.now().getMillis());
	// setUserId("Unknown");
	// setUsername("Unknown");
	// final List<G_PropertyMatchDescriptor> list = new
	// ArrayList<G_PropertyMatchDescriptor>();
	// setPropertyMatchDescriptors(list);
	// }

	// TODO: Stop using search tuples and start using property match descriptors
	// public QueryHelper(final String value, final G_Constraint g_SearchType,
	// final List<String> filters,
	// final int maxResults, final String schema) {
	// final List<G_PropertyMatchDescriptor> tuples = new
	// ArrayList<G_PropertyMatchDescriptor>();
	// final G_PropertyMatchDescriptor tuple = new
	// G_PropertyMatchDescriptor<String>(value,
	// g_SearchType);
	// tuples.add(tuple);
	// setPropertyMatchDescriptors(tuples);
	// setCaseSensitive(false);
	// setFilters(filters);
	// setFirstResult(0l);
	// setInitiatorId("");
	// setMaxResult((long) maxResults);
	// setMinimumScore(0.0d);
	// setSearchFreeText(true);
	// setTargetSchema(schema);
	// setTimeInitiated(DateTime.now().getMillis());
	// setUserId("Unknown");
	// setUsername("Unknown");
	// final List<G_PropertyMatchDescriptor> list = new
	// ArrayList<G_PropertyMatchDescriptor>();
	// setPropertyMatchDescriptors(list);
	// }
}
