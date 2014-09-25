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
import graphene.model.idl.G_GeoData;
import graphene.model.idl.G_ListRange;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_Provenance;
import graphene.model.idl.G_SingletonRange;
import graphene.model.idl.G_Uncertainty;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

public class PropertyHelper extends G_Property {

	public PropertyHelper(String key, String friendlyText, Object value, G_PropertyType type, G_Provenance provenance, G_Uncertainty uncertainty, List<G_PropertyTag> tags) {
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(provenance);
		setUncertainty(uncertainty);
		setTags(tags);
		setRange(new SingletonRangeHelper(value, type));
	}

	public PropertyHelper(String key, Object value, G_PropertyTag tag) {
		setKey(key);
		setFriendlyText(key.replaceAll("([a-z])([A-Z0-9])","$1 $2").replace('_',' '));
		setProvenance(null);
		setUncertainty(null);
		setTags(new ArrayList<G_PropertyTag>(2));
		setRange(new SingletonRangeHelper(value));
		
		getTags().add(tag);
	}

	public PropertyHelper(String key, String friendlyText, Object value, G_PropertyType type, G_PropertyTag tag) {
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(null);
		setUncertainty(null);
		setTags(new ArrayList<G_PropertyTag>(2));
		setRange(new SingletonRangeHelper(value, type));
		
		getTags().add(tag);
	}

	public PropertyHelper(String key, String friendlyText, Object value, G_PropertyType type, List<G_PropertyTag> tags) {
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(null);
		setUncertainty(null);
		setTags(tags);
		setRange(new SingletonRangeHelper(value, type));
	}

	public PropertyHelper(String key, String friendlyText, Object startValue, Object endValue, G_PropertyType type, List<G_PropertyTag> tags) {
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(null);
		setUncertainty(null);
		setTags(tags);
		setRange(G_BoundedRange.newBuilder().setStart(startValue).setEnd(endValue).setType(type));
	}

	public PropertyHelper(String key, String friendlyText, G_Provenance provenance, G_Uncertainty uncertainty, List<G_PropertyTag> tags, Object range) {
		setKey(key);
		setFriendlyText(friendlyText);
		setProvenance(provenance);
		setUncertainty(uncertainty);
		setTags(tags);
		setRange(range);
	}
	
	public static PropertyHelper from(G_Property property) {
		if (property == null) return null;
		if (property instanceof PropertyHelper) return (PropertyHelper) property;
		
		return new PropertyHelper(
				property.getKey(),
				property.getFriendlyText(),
				property.getProvenance(),
				property.getUncertainty(),
				property.getTags(),
				property.getRange());
	}

	public PropertyHelper(G_PropertyTag tag, String value) {
		this(tag.name(), tag.name(), value, Collections.singletonList(tag));
	}

	public PropertyHelper(G_PropertyTag tag, double value) {
		this(tag.name(), tag.name(), value, Collections.singletonList(tag));
	}

	public PropertyHelper(G_PropertyTag tag, Date value) {
		this(tag.name(), tag.name(), value, Collections.singletonList(tag));
	}

	
	public PropertyHelper(String key, String friendlyText, String value, List<G_PropertyTag> tags) {
		this(key, friendlyText, value, G_PropertyType.STRING, tags);
	}
	
	public PropertyHelper(String key, String friendlyText, double value, List<G_PropertyTag> tags) {
		this(key, friendlyText, value, G_PropertyType.DOUBLE, tags);
	}
	
	public PropertyHelper(String key, String friendlyText, Date date, List<G_PropertyTag> tags) {
		this(key, friendlyText, date.getTime(), G_PropertyType.DATE, tags);
	}
	
	public PropertyHelper(String key, String friendlyText, long value, List<G_PropertyTag> tags) {
		this(key, friendlyText, value, G_PropertyType.LONG, tags);
	}
	
	public PropertyHelper(String key, String friendlyText, G_GeoData value, List<G_PropertyTag> tags) {
		this(key, friendlyText, value, G_PropertyType.GEO, tags);
	}

	public static G_Property getPropertyByKey(List<G_Property> props, String key) {
		if (props == null) {
			return null;
		}
		for (G_Property prop : props) {
			if (prop.getKey().equalsIgnoreCase(key)) {
				return prop;
			}
		}
		return null;
	}

	public G_PropertyType getType() {
		Object range = getRange();
		if (range instanceof G_SingletonRange)
			return ((G_SingletonRange)range).getType();
		else if (range instanceof G_ListRange)
			return ((G_ListRange)range).getType();
		else if (range instanceof G_BoundedRange)
			return ((G_BoundedRange)range).getType();
		else if (range instanceof G_DistributionRange) 
			return ((G_DistributionRange)range).getType();
		return null;
	}

	/**
	 * Look at the range, and return the value object associated with the range.
	 * @return
	 */
	public Object getValue() {
		Object range = getRange();
		if (range == null) return null;
		
		if (range instanceof G_SingletonRange) {
			return ((G_SingletonRange)range).getValue();
		}
		else if (range instanceof G_ListRange) {
			return ((G_ListRange)range).getValues().iterator().next();
		}
		else if (range instanceof G_BoundedRange) {
			G_BoundedRange bounded = (G_BoundedRange)range;
			return bounded.getStart() != null ? bounded.getStart() : bounded.getEnd();
		}
		else if (range instanceof G_DistributionRange) {
			G_DistributionRange dist = (G_DistributionRange)range;
			return dist.getDistribution();
		}
		
		return null;
	}
	public List<Object> getValues() {
		Object range = getRange();
		
		if (range != null) {
			if (range instanceof G_SingletonRange) {
				return Collections.singletonList(((G_SingletonRange)range).getValue());
			}
			else if (range instanceof G_ListRange) {
				return ((G_ListRange)range).getValues();
			}
			else if (range instanceof G_BoundedRange) {
				G_BoundedRange bounded = (G_BoundedRange)range;
				return Arrays.asList(bounded.getStart(), bounded.getEnd());
			}
			else if (range instanceof G_DistributionRange) {
				G_DistributionRange dist = (G_DistributionRange)range;
				List<Object> values = new ArrayList<Object>(dist.getDistribution().size());
				values.addAll(dist.getDistribution());
				return values;
			}
		}
		
		return Collections.emptyList();
	}

	public boolean hasTag(G_PropertyTag tag) {
		return getTags().contains(tag);
	}

	public boolean hasValue() {
		return getValue() != null;
	}

	
}
