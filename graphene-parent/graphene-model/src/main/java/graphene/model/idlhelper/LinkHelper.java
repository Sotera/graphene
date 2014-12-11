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
import java.util.List;
import java.util.Map;

public class LinkHelper extends G_Link {

	public LinkHelper(G_LinkTag tag, String source, String target,
			List<G_Property> props) {
		setTags(Collections.singletonList(tag));
		setDirected(true);
		setProvenance(null);
		setUncertainty(null);
		setSource(source);
		setTarget(target);
		setProperties(new ArrayList<G_Property>(props));
	}

	public String toJson() throws IOException {
		return SerializationHelper.toJson(this);
	}

	public static String toJson(G_Link link) throws IOException {
		return SerializationHelper.toJson(link);
	}

	public static String toJson(List<G_Link> links) throws IOException {
		return SerializationHelper.toJson(links, G_Link.getClassSchema());
	}

	public static String toJson(Map<String, List<G_Link>> map)
			throws IOException {
		return SerializationHelper.toJson(map, G_Link.getClassSchema());
	}

	public static G_Entity fromJson(String json) throws IOException {
		return SerializationHelper.fromJson(json, G_Entity.getClassSchema());
	}

	public static List<G_Link> listFromJson(String json) throws IOException {
		return SerializationHelper.listFromJson(json, G_Link.getClassSchema());
	}

	public static Map<String, List<G_Link>> mapFromJson(String json)
			throws IOException {
		return SerializationHelper.mapFromJson(json, G_Link.getClassSchema());
	}

	public static PropertyHelper getFirstProperty(G_Link link, String key) {
		for (G_Property property : link.getProperties()) {
			if (property.getKey().equals(key))
				return PropertyHelper.from(property);
		}
		return null;
	}

	public static PropertyHelper getFirstPropertyByTag(G_Link link,
			G_PropertyTag tag) {
		for (G_Property property : link.getProperties()) {
			if (property.getTags().contains(tag))
				return PropertyHelper.from(property);
		}
		return null;
	}

	public static List<PropertyHelper> getProperties(G_Link link, String key) {
		List<PropertyHelper> matches = new ArrayList<PropertyHelper>();
		for (G_Property property : link.getProperties()) {
			if (property.getKey().equals(key))
				matches.add(PropertyHelper.from(property));
		}
		return matches;

	}
}
