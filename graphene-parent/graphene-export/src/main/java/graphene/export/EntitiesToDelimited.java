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

package graphene.export;

import graphene.model.idl.G_Entity;
import graphene.model.idl.G_Property;
import graphene.model.idlhelper.PropertyHelper;

import java.util.List;

public class EntitiesToDelimited {
	public String toDelimited(final List<G_Entity> lt, final List<String> keys, final String delimiter,
			final String subDelimiter) {
		// String eol = System.getProperty("line.separator");
		final String eol = "\r\n"; // users want windows format - the above
									// would be
									// UNIX

		final StringBuilder result = new StringBuilder();

		for (final G_Entity r : lt) {
			for (final String k : keys) {
				final G_Property property = PropertyHelper.getPropertyByKey((List<G_Property>) r.getProperties()
						.values(), k);
				PropertyHelper.getStringifiedValue(property, subDelimiter);
			}
			result.append(eol);
		}

		return result.toString();

	}
}
