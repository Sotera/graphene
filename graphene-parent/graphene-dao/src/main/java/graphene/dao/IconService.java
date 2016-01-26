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

package graphene.dao;

import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_Property;

import java.util.Collection;
import java.util.List;

public interface IconService {

	public abstract void addPattern(String pattern, boolean caseSensitive, String iconClass, String reason);

	/**
	 * Returns a list of icon styles that match the list of keys provided.
	 * 
	 * @param keys
	 * @return
	 */
	// public abstract Collection<String> getIconsForKeys(String... keys);

	public abstract List<Object> getIconsForText(String narr, G_EntityQuery sq);

	/**
	 * Returns a list of icon styles that match up with the contents of the text
	 * provided.
	 * 
	 * 
	 * @param text
	 * @param otherKeys
	 *            usually this is the search term(s) used to get the narrative.
	 *            The narrative is checked to see if it contains the term(s)
	 * @return
	 */
	public abstract Collection<G_Property> getIconsForText(String text, String... otherKeys);

	/**
	 * Returns a list of tuples with <icon style, count> as the values, where
	 * count > 0
	 * 
	 * @param text
	 * @return
	 */
	public abstract Collection<G_Property> getIconsForTextWithCount(String text, String... otherKeys);

	public abstract void removePattern(String pattern, boolean caseSensitive);
}
