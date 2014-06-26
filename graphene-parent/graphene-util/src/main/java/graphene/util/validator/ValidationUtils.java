package graphene.util.validator;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ValidationUtils {
	private static final Logger logger = LoggerFactory
			.getLogger(ValidationUtils.class);

	/**
	 * A convenience method for testing property (or other) maps. Makes sure the
	 * map has meaningful data inside.
	 * 
	 * @param properties
	 *            the map to test for validity
	 * @return true if the map is not null and has at least one key/value pair.
	 */
	public static boolean isValid(Map<String, Object> properties) {
		boolean isvalid = true;
		if (properties == null) {
			isvalid = false;
		} else if (properties.isEmpty()) {
			isvalid = false;
		}
		return isvalid;
	}

	/**
	 * Used for Neo4J and possibly other parts of code. Removes invalid values
	 * from a map. This lets you put values from beans into a map without having
	 * to check each bean property for null before inserting (because that step
	 * is easy to forget). The Neo4J code that ingests properties, at least for
	 * the bulk inserter in 1.9, does not check for null map values, which will
	 * cause a NPE. However it is very common for data from the original sources
	 * to have null values.
	 * 
	 * @param unsafeProps
	 * @return
	 */
	public static Map<String, Object> getSafeProperties(
			Map<String, Object> unsafeProps) {
		if (unsafeProps == null) {
			return null;
		} else {
			Map<String, Object> safeProps = new HashMap<String, Object>(4);
			for (String key : unsafeProps.keySet()) {
				if (isValid(key)) {
					Object obj = unsafeProps.get(key);
					if (obj != null) {
						if (obj instanceof Timestamp) {
							// store it as a long, or else we get unknown
							// property type error
							safeProps.put(key, ((Timestamp) obj).getTime());
						} else {
							safeProps.put(key, obj);
						}
					}
				}
			}
			return safeProps;
		}
	}

	/**
	 * A convenience method for testing strings. Makes sure the string has
	 * meaningful data inside.
	 * 
	 * @param s
	 *            the string to test for validity
	 * @return true if s is not null and also has characters other than
	 *         whitespace.
	 */
	public static boolean isValid(String s) {
		// We assume true unless proven false
		boolean isvalid = true;
		if (s == null) {
			isvalid = false;
		} else if (s.trim().isEmpty()) {
			isvalid = false;
		}
		return isvalid;
	}

	/**
	 * Be careful with this one, and know how it works. The intended use is to
	 * check to see if a query parameter was set or not. And usually, if this
	 * returns false we won't take that variable into consideration.
	 * 
	 * @param t
	 * @return false if the number is less than or equal to 0
	 */
	public static boolean isValid(int t) {
		boolean isvalid = true;
		if (t <= 0) {
			isvalid = false;
		}
		return isvalid;
	}

	/**
	 * Be careful with this one, and know how it works. The intended use is to
	 * check to see if a query parameter was set or not. And usually, if this
	 * returns false we won't take that variable into consideration.
	 * 
	 * @param t
	 * @return false if the number is less than or equal to 0
	 */
	public static boolean isValid(long t) {
		boolean isvalid = true;
		if (t <= 0) {
			isvalid = false;
		}
		return isvalid;
	}

	/**
	 * Be careful with this one, and know how it works. The intended use is to
	 * check to see if a query parameter was set or not. And usually, if this
	 * returns false we won't take that variable into consideration.
	 * 
	 * @param t
	 * @return false if the number is less than or equal to 0
	 */
	public static boolean isValid(double t) {
		boolean isvalid = true;
		if (t <= 0) {
			isvalid = false;
		}
		return isvalid;
	}

	/**
	 * Be careful with this one, and know how it works. The intended use is to
	 * check to see if a query parameter was set or not. And usually, if this
	 * returns false we won't take that variable into consideration.
	 * 
	 * @param t
	 * @return false if the number is less than or equal to 0
	 */
	public static boolean isValid(Number t) {
		boolean isvalid = true;
		if (t == null || t.doubleValue() <= 0 || t.intValue() <= 0) {
			isvalid = false;
		}
		return isvalid;
	}

	public static boolean isValid(Object t) {
		boolean isvalid = true;
		if (t == null) {
			isvalid = false;
		} else if (String.class.isAssignableFrom(t.getClass())) {
			isvalid = isValid((String) t);
		} else if (Number.class.isAssignableFrom(t.getClass())) {
			isvalid = isValid((Number) t);
		} else if (Boolean.class.isAssignableFrom(t.getClass())) {
			isvalid = isValid((Boolean) t);
		}
		return isvalid;
	}

	/**
	 * 
	 * @param t
	 * @return false if the collection was null or empty.
	 */
	public static boolean isValid(Collection t) {
		boolean isvalid = true;
		if (t == null || t.size() == 0) {
			isvalid = false;
		}
		return isvalid;
	}

	public static boolean isValid(Iterable t) {
		boolean isvalid = true;
		if (t == null || t.iterator() == null || !t.iterator().hasNext()) {
			isvalid = false;
		}
		return isvalid;
	}

	public static boolean isValid(Timestamp t) {
		boolean isvalid = true;
		if (t == null) {
			isvalid = false;
		}
		return isvalid;
	}

	/**
	 * Pass a list of objects. If all are valid, return true. If any are
	 * invalid, immediately return false.
	 * 
	 * @param objects
	 * @return
	 */
	public static boolean isValid(Object... objects) {
		boolean isvalid = true;
		if (objects == null) {
			isvalid = false;
		} else {
			for (Object o : objects) {
				if (!isValid(o)) {
					isvalid = false;
					break;
				}
			}
		}
		return isvalid;
	}

	public static Double getSafeDouble(Number d, double i) {
		return d == null ? i : d.doubleValue();
	}

	public static long getSafeLong(Timestamp d, long i) {
		return d == null ? i : d.getTime();
	}

	public static double getSafeDouble(String s, double i) {
		double d = i;
		try {
			d = (Double.parseDouble(s));
		} catch (Exception e) {
			logger.debug("Error converting string to double :" + s);
		}
		return d;
	}
}
