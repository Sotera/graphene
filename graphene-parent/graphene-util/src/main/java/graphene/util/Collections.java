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

package graphene.util;

import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A place for useful collections. If available from Google/Apache/etc, use
 * those instead and remove from here.
 * 
 * @author djue
 * 
 */
public class Collections {
	/**
	 * Sorts the map by the values of the kv pair.
	 * 
	 * @param unsortedMap
	 * @param order
	 * @param filterBelow
	 * @return
	 */
	public static Map<String, Integer> sortByComparator(final Map<String, Integer> unsortedMap, final boolean order,
			final int filterBelow) {
		final List<Entry<String, Integer>> list = new LinkedList<Entry<String, Integer>>(unsortedMap.entrySet());
		java.util.Collections.sort(list, new Comparator<Entry<String, Integer>>() {

			@Override
			public int compare(final Entry<String, Integer> o1, final Entry<String, Integer> o2) {
				if (order) {
					return o1.getValue().compareTo(o2.getValue());
				} else {
					return o2.getValue().compareTo(o1.getValue());
				}
			}
		});

		final Map<String, Integer> sortedMap = new LinkedHashMap<String, Integer>();
		for (final Entry<String, Integer> entry : list) {
			if (entry.getValue() >= filterBelow) {
				sortedMap.put(entry.getKey(), entry.getValue());
			}
		}
		return sortedMap;
	}
}
