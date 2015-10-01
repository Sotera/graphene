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
import graphene.util.validator.ValidationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityHelper extends G_Entity {

	public static G_Entity fromJson(final String json) throws IOException {
		return SerializationHelper.fromJson(json, G_Entity.getClassSchema());
	}

	public static List<G_Property> getPropertiesByTag(final G_Entity entity, final G_PropertyTag tag) {
		final List<G_Property> list = new ArrayList<G_Property>(1);
		if ((entity != null) && (entity.getProperties() != null)) {
			for (final G_Property x : entity.getProperties().values()) {
				if (x.getTags().contains(tag)) {
					list.add(x);
				}
			}
			Collections.sort(list);
		}
		return list;
	}

	public static List<G_Property> getPropertiesByTags(final G_Entity entity, final G_PropertyTag... tags) {
		final List<G_Property> list = new ArrayList<G_Property>(1);
		if ((entity != null) && (entity.getProperties() != null)) {
			for (final G_Property x : entity.getProperties().values()) {
				if (!Collections.disjoint(x.getTags(), Arrays.asList(tags))) {
					list.add(x);
				}
			}
			Collections.sort(list);
		}
		return list;
	}

	public static Object getPropertyValue(final G_Entity entity, final String key) {
		if ((entity != null) && (entity.getProperties() != null)) {
			final G_Property prop = entity.getProperties().get(key);

			if (prop != null) {
				if (ValidationUtils.isValid(prop.getSingletonRange())) {
					return prop.getSingletonRange().getValue();
				} else if (ValidationUtils.isValid(prop.getListRange())) {
					return prop.getListRange().getValues();
				} else if (ValidationUtils.isValid(prop.getBoundedRange())) {
					return prop.getBoundedRange();
				} else if (ValidationUtils.isValid(prop.getBoundedRange())) {
					return prop.getDistributionRange();
				} else {
					// error
					return null;
				}
			}
		}
		return null;
	}

	public static List<G_Entity> listFromJson(final String json) throws IOException {
		return SerializationHelper.listFromJson(json, G_Entity.getClassSchema());
	}

	public static Map<String, List<G_Entity>> mapFromJson(final String json) throws IOException {
		return SerializationHelper.mapFromJson(json, G_Entity.getClassSchema());
	}

	private static Map<String, G_Property> merge(final Map<String, G_Property> list1,
			final Map<String, G_Property> list2) {
		final Map<String, G_Property> merged = new HashMap<String, G_Property>(list1);
		merged.putAll(list2);
		return merged;
	}

	public static String toJson(final G_Entity entity) throws IOException {
		return SerializationHelper.toJson(entity);
	}

	public static String toJson(final List<G_Entity> entities) throws IOException {
		return SerializationHelper.toJson(entities, G_Entity.getClassSchema());
	}

	public static String toJson(final Map<String, List<G_Entity>> entities) throws IOException {
		return SerializationHelper.toJson(entities, G_Entity.getClassSchema());
	}

	public EntityHelper(final String id, final List<G_EntityTag> tagList, final G_Provenance provenance,
			final G_Uncertainty uncertainty, final Map<String, G_Property> properties) {
		super(id, new ArrayList<G_EntityTag>(tagList), provenance, uncertainty, new HashMap<String, G_Property>(
				properties));
	}

	public EntityHelper(final String id, final String label, final String type, final G_EntityTag tag,
			final Map<String, G_Property> properties) {
		this(id, label, type, Collections.singletonList(tag), new HashMap<String, G_Property>(properties));
	}

	public EntityHelper(final String id, final String label, final String type, final List<G_EntityTag> tagList,
			final Map<String, G_Property> properties) {

		this(id, tagList, null, null, merge(properties, new HashMap<String, G_Property>() {
			/**
			 * 
			 */
			private static final long serialVersionUID = 1L;

			{
				put(G_PropertyTag.LABEL.name(), new PropertyHelper(G_PropertyTag.LABEL, label));
				put(G_PropertyTag.TYPE.name(), new PropertyHelper(G_PropertyTag.TYPE, type));
			}
		}));
	}

	public String getId() {
		return getUid();
	}

	// public String getLabel() {
	// PropertyHelper label = null;
	// for (final G_Property r : getProperties().values()) {
	// if (r.getTags().contains(G_PropertyTag.LABEL)) {
	// label = PropertyHelper.from(r);
	// }
	// }
	// // final PropertyHelper label =
	// // getFirstProperty(G_PropertyTag.LABEL.name());
	// return (String) (label != null ? label.getValue() : null);
	// }

	public String toJson() throws IOException {
		return SerializationHelper.toJson(this);
	}
}
