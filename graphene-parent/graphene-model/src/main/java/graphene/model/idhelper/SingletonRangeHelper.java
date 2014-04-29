package graphene.model.idhelper;

import graphene.model.idl.G_GeoData;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SingletonRange;

import java.util.Date;

public class SingletonRangeHelper extends G_SingletonRange {

	public SingletonRangeHelper(Object value) {
		G_PropertyType type = G_PropertyType.STRING;

		if (value != null && !(value instanceof String)) {
			type = G_PropertyType.STRING;

			if (value instanceof Number) {
				Number number = (Number) value;

				if (number instanceof Integer) {
					type = G_PropertyType.LONG;
					value = Long.valueOf(number.longValue());

				} else if (number instanceof Long) {
					type = G_PropertyType.LONG;

				} else {
					type = G_PropertyType.DOUBLE;
					value = Double.valueOf(number.doubleValue());
				}

			} else if (value instanceof Boolean) {
				type = G_PropertyType.BOOLEAN;

			} else if (value instanceof Date) {
				type = G_PropertyType.DATE;
				value = Long.valueOf(((Date) value).getTime());

			} else if (value instanceof G_GeoData) {
				type = G_PropertyType.GEO;

			} else {
				value = value.toString();
			}
		}

		setValue(value);
		setType(type);
	}

	public SingletonRangeHelper(Object value, G_PropertyType type) {
		setValue(value);
		setType(type);
	}

	public static Object rangeValue(Object range) {
		return range instanceof G_SingletonRange ? ((G_SingletonRange) range)
				.getValue() : null;
	}

	public static String toString(Object range) {
		return String.valueOf(rangeValue(range));
	}
}
