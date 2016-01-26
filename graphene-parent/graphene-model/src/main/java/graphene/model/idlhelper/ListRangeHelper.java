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
package graphene.model.idlhelper;

import graphene.model.idl.G_ListRange;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SingletonRange;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

public class ListRangeHelper extends G_ListRange {

	/**
	 * Get a singleton or a list as a list of objects. You handle the casting to
	 * the right type.
	 * 
	 * @param range
	 * @return
	 */
	public static List<Object> rangeValue(final Object range) {
		if (range instanceof G_ListRange) {
			return ((G_ListRange) range).getValues();
		} else if (range instanceof G_SingletonRange) {
			final List<Object> list = new ArrayList<Object>();
			final Object value = ((G_SingletonRange) range).getValue();
			if (value != null) {
				list.add(value);
			}
			return list;
		}
		return null;
	}

	public static String toString(final Object range) {
		return String.valueOf(rangeValue(range));
	}

	public ListRangeHelper(final G_PropertyType type, final Collection<? extends String> value) {
		final List<Object> list = new ArrayList<Object>();
		list.addAll(value);
		setValues(list);
		setType(type);
	}

	public ListRangeHelper(final G_PropertyType type, final String... value) {
		final List<Object> list = new ArrayList<Object>();
		for (final String v : value) {
			if (v != null) {
				list.add(v);
			}
		}
		setValues(list);
		setType(type);
	}

	public ListRangeHelper(Object value) {
		G_PropertyType type = G_PropertyType.STRING;

		if ((value != null) && !(value instanceof List)) {
			type = G_PropertyType.STRING;

			if (value instanceof Set) {
				value = Lists.newArrayList(value);
			}
		}

		setValues((List) value);
		setType(type);
	}

	public ListRangeHelper(final Object value, final G_PropertyType type) {
		if (value instanceof Set) {
			setValues(new ArrayList((Set) value));
		} else if (value instanceof List) {
			setValues((List) value);
		} else if (value instanceof Object[]) {
			setValues(Arrays.asList((Object[]) value));
		}
		setType(type);
	}
}
