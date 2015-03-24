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
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package graphene.model.idlhelper;

import graphene.model.idl.G_GeoData;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SingletonRange;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SingletonRangeHelper extends G_SingletonRange {

	private static Logger logger = LoggerFactory.getLogger(SingletonRangeHelper.class);

	public static Object rangeValue(final Object range) {
		if (range instanceof G_SingletonRange) {
			return ((G_SingletonRange) range).getValue();
		} else if (range instanceof SingletonRangeHelper) {
			return ((SingletonRangeHelper) range).getValue();
		} else {
			logger.error("Range value was not G_SingletonRange or SingletonRangeHelper: " + range);
			return null;
		}
	}

	public static String toString(final Object range) {
		return String.valueOf(rangeValue(range));
	}

	public SingletonRangeHelper(Object value) {
		G_PropertyType type = G_PropertyType.STRING;

		if ((value != null) && !(value instanceof String)) {
			type = G_PropertyType.STRING;

			if (value instanceof Number) {
				final Number number = (Number) value;

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

	public SingletonRangeHelper(final Object value, final G_PropertyType type) {
		setValue(value);
		setType(type);
	}
}
