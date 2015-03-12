/**
 * 
 */
package graphene.model.idlhelper;

import graphene.model.idl.G_ListRange;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SingletonRange;

import java.util.List;
import java.util.Set;

import com.google.common.collect.Lists;

public class ListRangeHelper extends G_ListRange {

	public static Object rangeValue(final Object range) {
		return range instanceof G_SingletonRange ? ((G_SingletonRange) range).getValue() : null;
	}

	public static String toString(final Object range) {
		return String.valueOf(rangeValue(range));
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
		setValues((List) value);
		setType(type);
	}
}
