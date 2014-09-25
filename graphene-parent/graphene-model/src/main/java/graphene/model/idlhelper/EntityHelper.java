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
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_Provenance;
import graphene.model.idl.G_Uncertainty;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class EntityHelper extends G_Entity {

	public EntityHelper(String id, List<G_EntityTag> tagList,
			G_Provenance provenance, G_Uncertainty uncertainty,
			List<G_Property> properties) {
		super(id, new ArrayList<G_EntityTag>(tagList), provenance, uncertainty,
				new ArrayList<G_Property>(properties));
	}

	public EntityHelper(String id, String label, String type,
			List<G_EntityTag> tagList, List<G_Property> properties) {
		this(id, tagList, null, null, merge(
				properties,
				Arrays.asList(new G_Property[] {
						new PropertyHelper(G_PropertyTag.LABEL, label),
						new PropertyHelper(G_PropertyTag.TYPE, type) })));
	}

	private static List<G_Property> merge(List<G_Property> list1,
			List<G_Property> list2) {
		List<G_Property> merged = new ArrayList<G_Property>(list1);
		merged.addAll(list2);
		return merged;
	}

	public EntityHelper(String id, String label, String type, G_EntityTag tag,
			List<G_Property> properties) {
		this(id, label, type, Collections.singletonList(tag),
				new ArrayList<G_Property>(properties));
	}

	public PropertyHelper getFirstProperty(String key) {
		for (G_Property property : getProperties()) {
			if (property.getKey().equals(key))
				return PropertyHelper.from(property);
		}
		return null;
	}

	public String getId() {
		return (String) getUid();
	}

	public String getLabel() {
		PropertyHelper label = getFirstProperty(G_PropertyTag.LABEL.name());
		return (String) (label != null ? label.getValue() : null);
	}

	public String toJson() throws IOException {
		return SerializationHelper.toJson(this);
	}

	public static String toJson(G_Entity entity) throws IOException {
		return SerializationHelper.toJson(entity);
	}

	public static String toJson(List<G_Entity> entities) throws IOException {
		return SerializationHelper.toJson(entities, G_Entity.getClassSchema());
	}

	public static String toJson(Map<String, List<G_Entity>> entities)
			throws IOException {
		return SerializationHelper.toJson(entities, G_Entity.getClassSchema());
	}

	public static G_Entity fromJson(String json) throws IOException {
		return SerializationHelper.fromJson(json, G_Entity.getClassSchema());
	}

	public static List<G_Entity> listFromJson(String json) throws IOException {
		return SerializationHelper
				.listFromJson(json, G_Entity.getClassSchema());
	}

	public static Map<String, List<G_Entity>> mapFromJson(String json)
			throws IOException {
		return SerializationHelper.mapFromJson(json, G_Entity.getClassSchema());
	}

	public static PropertyHelper getFirstProperty(G_Entity entity, String key) {
		if (entity != null && entity.getProperties() != null) {
			for (G_Property property : entity.getProperties()) {
				if (property.getKey().equals(key))
					return PropertyHelper.from(property);
			}
		}
		return null;
	}

	public static PropertyHelper getFirstPropertyByTag(G_Entity entity,
			G_PropertyTag tag) {
		if (entity != null && entity.getProperties() != null) {
			for (G_Property property : entity.getProperties()) {
				if (property.getTags().contains(tag))
					return PropertyHelper.from(property);
			}
		}
		return null;
	}

	public static List<G_Property> getPropertiesByTags(G_Entity entity,
			G_PropertyTag... tags) {
		List<G_Property> list = new ArrayList<G_Property>(1);
		if (entity != null && entity.getProperties() != null) {
			for (G_Property x : entity.getProperties()) {
				if (!Collections.disjoint(x.getTags(), Arrays.asList(tags))) {
					list.add(x);
				}
			}
			Collections.sort(list);
		}
		return list;
	}

	public static List<G_Property> getPropertiesByTag(G_Entity entity,
			G_PropertyTag tag) {
		List<G_Property> list = new ArrayList<G_Property>(1);
		if (entity != null && entity.getProperties() != null) {
			for (G_Property x : entity.getProperties()) {
				if (x.getTags().contains(tag)) {
					list.add(x);
				}
			}
			Collections.sort(list);
		}
		return list;
	}

	public static List<G_Property> getPropertiesByKey(G_Entity entity,
			String key) {
		List<G_Property> list = new ArrayList<G_Property>(1);
		if (entity != null) {
			for (G_Property x : entity.getProperties()) {
				if (x.getKey().equalsIgnoreCase(key)) {
					list.add(x);
				}
			}
			Collections.sort(list);
		}
		return list;
	}
}
