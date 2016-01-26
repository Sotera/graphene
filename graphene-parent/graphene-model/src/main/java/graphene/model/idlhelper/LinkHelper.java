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
 * Copyright (c) 2013-2014 Oculus Info Inc.
 * http://www.oculusinfo.com/
 *
 * Released under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:

 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.

 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package graphene.model.idlhelper;

import graphene.model.idl.G_Entity;
import graphene.model.idl.G_Link;
import graphene.model.idl.G_LinkTag;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinkHelper extends G_Link {

	public static G_Entity fromJson(final String json) throws IOException {
		return SerializationHelper.fromJson(json, G_Entity.getClassSchema());
	}

	@Deprecated
	public static PropertyHelper getFirstProperty(final G_Link link, final String key) {
		for (final G_Property property : link.getProperties().values()) {
			if (property.getKey().equals(key)) {
				return PropertyHelper.from(property);
			}
		}
		return null;
	}

	@Deprecated
	public static PropertyHelper getFirstPropertyByTag(final G_Link link, final G_PropertyTag tag) {
		for (final G_Property property : link.getProperties().values()) {
			if (property.getTags().contains(tag)) {
				return PropertyHelper.from(property);
			}
		}
		return null;
	}

	/**
	 * Note that this is looking at the property key, which is not necessarily
	 * the same as the key in the properties map.
	 * 
	 * @param link
	 * @param key
	 * @return
	 */
	public static List<PropertyHelper> getProperties(final G_Link link, final String key) {
		final List<PropertyHelper> matches = new ArrayList<PropertyHelper>();
		for (final G_Property property : link.getProperties().values()) {
			if (property.getKey().equals(key)) {
				matches.add(PropertyHelper.from(property));
			}
		}
		return matches;

	}

	public static List<G_Link> listFromJson(final String json) throws IOException {
		return SerializationHelper.listFromJson(json, G_Link.getClassSchema());
	}

	public static Map<String, List<G_Link>> mapFromJson(final String json) throws IOException {
		return SerializationHelper.mapFromJson(json, G_Link.getClassSchema());
	}

	public static String toJson(final G_Link link) throws IOException {
		return SerializationHelper.toJson(link);
	}

	public static String toJson(final List<G_Link> links) throws IOException {
		return SerializationHelper.toJson(links, G_Link.getClassSchema());
	}

	public static String toJson(final Map<String, List<G_Link>> map) throws IOException {
		return SerializationHelper.toJson(map, G_Link.getClassSchema());
	}

	public LinkHelper(final G_LinkTag tag, final String source, final String target, final Map<String, G_Property> props) {
		setTags(Collections.singletonList(tag));
		setDirected(true);
		setProvenance(null);
		setUncertainty(null);
		setSource(source);
		setTarget(target);
		setProperties(new HashMap<String, G_Property>(props));
	}

	public String toJson() throws IOException {
		return SerializationHelper.toJson(this);
	}
}
