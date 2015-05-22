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

import graphene.model.idl.G_BoundedRange;
import graphene.model.idl.G_DistributionRange;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_Frequency;
import graphene.model.idl.G_GeoData;
import graphene.model.idl.G_ListRange;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_Provenance;
import graphene.model.idl.G_SearchResult;
import graphene.model.idl.G_SingletonRange;
import graphene.model.idl.G_Uncertainty;
import graphene.util.StringUtils;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PropertyHelper extends G_Property {

	static Logger logger = LoggerFactory.getLogger(PropertyHelper.class);

	public static PropertyHelper from(final G_Property p) {
		if (p == null) {
			return null;
		}
		if (p instanceof PropertyHelper) {
			return (PropertyHelper) p;
		}

		final PropertyHelper x = new PropertyHelper(p.getKey(), p.getFriendlyText(), p.getStyleType(),
				p.getSingletonRange(), p.getListRange(), p.getBoundedRange(), p.getDistributionRange(),
				p.getProvenance(), p.getUncertainty(), p.getTags());
		return x;
	}

	public static Object getListValue(final G_Property property) {
		if (ValidationUtils.isValid(property) && ValidationUtils.isValid(property.getListRange())) {
			return property.getListRange().getValues();
		} else {
			return null;
		}
	}

	public static Object getListValueByKey(final List<G_Property> props, final String key) {
		final G_Property p = getPropertyByKey(props, key);
		if (p == null) {
			return null;
		} else {
			return getListValue(getPropertyByKey(props, key));
		}
	}

	/**
	 * This is an ugly mess since it's O(n) and we may have many properties.
	 * Also, such functionality should be available in the main object. XXX: Fix
	 * this by changing the IDL to use a map of G_Properties instead of a list.
	 * We're already treating it like a map since we assume there are only one
	 * of each key below.
	 * 
	 * @param props
	 * @param key
	 * @return
	 */
	public static G_Property getPropertyByKey(final List<G_Property> props, final String key) {
		if (props == null) {
			return null;
		}
		for (final G_Property prop : props) {
			if (prop.getKey().equalsIgnoreCase(key)) {
				return prop;
			}
		}
		return null;
	}

	public static Double getSingletonDoubleValueByKey(final G_SearchResult currentSearchResult, final String key) {
		final G_Entity e = (G_Entity) currentSearchResult.getResult();
		return getSingletonDoubleValueByKey(e.getProperties(), key);
	}

	public static Double getSingletonDoubleValueByKey(final Map<String, G_Property> entityProperties, final String key) {
		if (ValidationUtils.isValid(entityProperties)) {
			final G_Property property = entityProperties.get(key);
			return (Double) getSingletonValue(property);
		} else {
			return null;
		}
	}

	public static Long getSingletonLongByKey(final G_SearchResult currentSearchResult, final String key) {
		final G_Entity e = (G_Entity) currentSearchResult.getResult();
		return getSingletonLongByKey(e.getProperties(), key);
	}

	public static Long getSingletonLongByKey(final Map<String, G_Property> entityProperties, final String key) {
		if (ValidationUtils.isValid(entityProperties)) {
			final G_Property property = entityProperties.get(key);
			return (Long) getSingletonValue(property);
		} else {
			return null;
		}
	}

	public static Long getSingletonLongByKey(final Map<String, G_Property> entityProperties, final String key,
			final Long defaultValue) {
		if (ValidationUtils.isValid(entityProperties)) {
			final G_Property property = entityProperties.get(key);
			return (Long) getSingletonValue(property);
		} else {
			return defaultValue;
		}
	}

	public static String getSingletonStringByKey(final G_SearchResult currentSearchResult, final String key) {
		final G_Entity e = (G_Entity) currentSearchResult.getResult();
		return getSingletonStringByKey(e.getProperties(), key);
	}

	public static String getSingletonStringByKey(final Map<String, G_Property> entityProperties, final String key) {
		if (ValidationUtils.isValid(entityProperties)) {
			final G_Property property = entityProperties.get(key);
			return (String) getSingletonValue(property);
		} else {
			return null;
		}
	}

	public static String getSingletonStringByKey(final Map<String, G_Property> entityProperties, final String key,
			final String defaultValue) {
		if (ValidationUtils.isValid(entityProperties)) {
			final G_Property property = entityProperties.get(key);
			return (String) getSingletonValue(property);
		} else {
			return defaultValue;
		}
	}

	public static Object getSingletonValue(final G_Property property) {
		if (ValidationUtils.isValid(property) && ValidationUtils.isValid(property.getSingletonRange())) {
			return property.getSingletonRange().getValue();
		} else {
			return null;
		}
	}

	public static Object getSingletonValueByKey(final List<G_Property> props, final String key) {
		if (props == null) {
			return null;
		} else {
			return getSingletonValue(getPropertyByKey(props, key));
		}
	}

	public static String getStringifiedValue(final G_Property property, final String subDelimiter) {
		if (ValidationUtils.isValid(property)) {

			if (ValidationUtils.isValid(property.getSingletonRange())) {
				return property.getSingletonRange().toString();
			} else if (ValidationUtils.isValid(property.getListRange())) {
				return StringUtils.toDelimitedString(property.getListRange().getValues().toArray(), subDelimiter);
			} else if (ValidationUtils.isValid(property.getBoundedRange())) {
				return StringUtils.toDelimitedString(subDelimiter, property.getBoundedRange().getStart(), property
						.getBoundedRange().getEnd());
			} else if (ValidationUtils.isValid(property.getDistributionRange())) {
				final List<G_Frequency> f = property.getDistributionRange().getDistribution();
				return StringUtils.toDelimitedString(f.toArray(), subDelimiter);
			}

			return null;
		} else {
			// logger.warn("Property was null, perhaps it's key was not defined for the source document.");
			return null;
		}
	}

	public PropertyHelper(final G_PropertyTag tag, final Date value) {
		this(tag.name(), tag.name(), value, Collections.singletonList(tag));
	}

	public PropertyHelper(final G_PropertyTag tag, final double value) {
		this(tag.name(), tag.name(), value, Collections.singletonList(tag));
	}

	public PropertyHelper(final G_PropertyTag tag, final String value) {
		this(tag.name(), tag.name(), value, Collections.singletonList(tag));
	}

	public PropertyHelper(final String key, final Object value, final G_PropertyTag tag) {
		setKey(key);
		setFriendlyText(key.replaceAll("([a-z])([A-Z0-9])", "$1 $2").replace('_', ' '));
		setProvenance(null);
		setUncertainty(null);
		setTags(new ArrayList<G_PropertyTag>(2));
		setSingletonRange(new SingletonRangeHelper(value));

		getTags().add(tag);
	}

	/**
	 * 
	 * @param key
	 * @param friendlyText
	 * @param date
	 *            will be the singleton range value
	 * @param tags
	 */
	public PropertyHelper(final String key, final String friendlyText, final Date date, final List<G_PropertyTag> tags) {
		this(key, friendlyText, date.getTime(), G_PropertyType.DATE, tags);
	}

	/**
	 * 
	 * @param key
	 * @param friendlyText
	 * @param value
	 *            a singleton range value
	 * @param tags
	 */
	public PropertyHelper(final String key, final String friendlyText, final double value,
			final List<G_PropertyTag> tags) {
		this(key, friendlyText, value, G_PropertyType.DOUBLE, tags);
	}

	public PropertyHelper(final String key, final String friendlyText, final G_GeoData value,
			final List<G_PropertyTag> tags) {
		this(key, friendlyText, value, G_PropertyType.GEO, tags);
	}

	public PropertyHelper(final String key, final String friendlyText, final G_PropertyType type,
			final List<Object> value, final G_PropertyTag tag) {
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(null);
		setUncertainty(null);
		setTags(new ArrayList<G_PropertyTag>(2));
		setListRange(new ListRangeHelper(value, type));
		getTags().add(tag);
	}

	/**
	 * 
	 * @param key
	 * @param friendlyText
	 * @param type
	 * @param value
	 *            will be converted to a list for you
	 * @param tag
	 */
	public PropertyHelper(final String key, final String friendlyText, final G_PropertyType type, final Set value,
			final G_PropertyTag tag) {
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(null);
		setUncertainty(null);
		setTags(new ArrayList<G_PropertyTag>(2));
		setListRange(new ListRangeHelper(value, type));
		getTags().add(tag);
	}

	public PropertyHelper(final String key, final String friendlyText, final G_PropertyType type, final Set value,
			final G_PropertyTag tag, final String styleType) {
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(null);
		setUncertainty(null);
		setTags(new ArrayList<G_PropertyTag>(2));
		setListRange(new ListRangeHelper(value, type));
		getTags().add(tag);
		setStyleType(styleType);
	}

	/**
	 * 
	 * @param key
	 * @param friendlyText
	 * @param provenance
	 * @param uncertainty
	 * @param tags
	 * @param range
	 *            any kind of range object
	 */
	public PropertyHelper(final String key, final String friendlyText, final G_Provenance provenance,
			final G_Uncertainty uncertainty, final List<G_PropertyTag> tags, final G_SingletonRange singletonRange,
			final G_ListRange listRange, final G_BoundedRange boundedRange, final G_DistributionRange distRange) {
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(provenance);
		setUncertainty(uncertainty);
		setTags(tags);
		setSingletonRange(singletonRange);
		setListRange(listRange);
		setBoundedRange(boundedRange);
		setDistributionRange(distRange);
	}

	public PropertyHelper(final String key, final String friendlyText, final ListRangeHelper value,
			final List<G_PropertyTag> tags) {
		this(key, friendlyText, value, G_PropertyType.STRING, tags);
	}

	/**
	 * 
	 * @param key
	 * @param friendlyText
	 * @param value
	 *            a singleton range value
	 * @param tags
	 */
	public PropertyHelper(final String key, final String friendlyText, final long value, final List<G_PropertyTag> tags) {
		this(key, friendlyText, value, G_PropertyType.LONG, tags);
	}

	/**
	 * 
	 * @param key
	 * @param friendlyText
	 * @param value
	 *            a singleton range value
	 * @param type
	 * @param tag
	 */
	public PropertyHelper(final String key, final String friendlyText, final Object value, final G_PropertyType type,
			final G_PropertyTag tag) {
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(null);
		setUncertainty(null);
		setTags(new ArrayList<G_PropertyTag>(2));
		setSingletonRange(new SingletonRangeHelper(value, type));

		getTags().add(tag);
	}

	public PropertyHelper(final String key, final String friendlyText, final Object value, final G_PropertyType type,
			final G_PropertyTag tag, final String styleString) {
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(null);
		setUncertainty(null);
		setTags(new ArrayList<G_PropertyTag>(2));
		setSingletonRange(new SingletonRangeHelper(value, type));
		setStyleType(styleString);
		getTags().add(tag);
	}

	/**
	 * 
	 * @param key
	 * @param friendlyText
	 * @param value
	 *            a singleton range value
	 * @param type
	 * @param provenance
	 * @param uncertainty
	 * @param tags
	 */
	public PropertyHelper(final String key, final String friendlyText, final Object value, final G_PropertyType type,
			final G_Provenance provenance, final G_Uncertainty uncertainty, final List<G_PropertyTag> tags) {
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(provenance);
		setUncertainty(uncertainty);
		setTags(tags);
		setSingletonRange(new SingletonRangeHelper(value, type));
	}

	/**
	 * 
	 * @param key
	 * @param friendlyText
	 * @param value
	 *            a singleton range value
	 * @param type
	 * @param tags
	 */
	public PropertyHelper(final String key, final String friendlyText, final Object value, final G_PropertyType type,
			final List<G_PropertyTag> tags) {
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(null);
		setUncertainty(null);
		setTags(tags);
		setSingletonRange(new SingletonRangeHelper(value, type));
	}

	/**
	 * 
	 * @param key
	 * @param friendlyText
	 * @param startValue
	 *            bounded range start
	 * @param endValue
	 *            bounded range end
	 * @param type
	 * @param tags
	 */
	public PropertyHelper(final String key, final String friendlyText, final Object startValue, final Object endValue,
			final G_PropertyType type, final List<G_PropertyTag> tags) {
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(null);
		setUncertainty(null);
		setTags(tags);
		setBoundedRange(G_BoundedRange.newBuilder().setStart(startValue).setEnd(endValue).setType(type).build());
	}

	public PropertyHelper(final String key, final String friendlyText, final String styleType,
			final G_SingletonRange singletonRange, final G_ListRange listRange, final G_BoundedRange boundedRange,
			final G_DistributionRange distributionRange, final G_Provenance provenance,
			final G_Uncertainty uncertainty, final List<G_PropertyTag> tags) {
		super(key, friendlyText, styleType, singletonRange, listRange, boundedRange, distributionRange, provenance,
				uncertainty, tags);
		// TODO Auto-generated constructor stub
	}

	/**
	 * 
	 * @param key
	 * @param friendlyText
	 * @param value
	 *            a singleton range value
	 * @param tags
	 */
	public PropertyHelper(final String key, final String friendlyText, final String value,
			final List<G_PropertyTag> tags) {
		this(key, friendlyText, value, G_PropertyType.STRING, tags);
	}

	/**
	 * 
	 * @return the type of instance of the range, i.e. a
	 *         {@link G_SingletonRange}, etc. Null if not one of the known types
	 */
	public G_PropertyType getType() {
		if (ValidationUtils.isValid(getSingletonRange())) {
			return getSingletonRange().getType();
		} else if (ValidationUtils.isValid(getListRange())) {
			return getListRange().getType();
		} else if (ValidationUtils.isValid(getBoundedRange())) {
			return getBoundedRange().getType();
		} else if (ValidationUtils.isValid(getDistributionRange())) {
			return getDistributionRange().getType();
		}
		return null;
	}

	/**
	 * Look at the range, and return the value object associated with the range.
	 * 
	 * @return
	 */
	public Object getValue() {
		if (ValidationUtils.isValid(getSingletonRange())) {
			return getSingletonRange().getValue();
		} else if (ValidationUtils.isValid(getListRange())) {
			return getListRange().getValues();
		} else if (ValidationUtils.isValid(getBoundedRange())) {
			return getBoundedRange().getStart() != null ? getBoundedRange().getStart() : getBoundedRange().getEnd();
		} else if (ValidationUtils.isValid(getDistributionRange())) {
			return getDistributionRange().getDistribution();
		}

		return null;
	}

	public List<Object> getValues() {
		if (ValidationUtils.isValid(getSingletonRange())) {
			return Collections.singletonList(getSingletonRange().getValue());
		} else if (ValidationUtils.isValid(getListRange())) {
			return getListRange().getValues();
		} else if (ValidationUtils.isValid(getBoundedRange())) {
			return Arrays.asList(getBoundedRange().getStart(), getBoundedRange().getEnd());
		} else if (ValidationUtils.isValid(getDistributionRange())) {
			final List<Object> values = new ArrayList<Object>(getDistributionRange().getDistribution().size());
			values.addAll(getDistributionRange().getDistribution());
			return values;
		} else {
			return Collections.emptyList();
		}
	}

	public boolean hasTag(final G_PropertyTag tag) {
		return getTags().contains(tag);
	}

	public boolean hasValue() {
		return getValue() != null;
	}

}
