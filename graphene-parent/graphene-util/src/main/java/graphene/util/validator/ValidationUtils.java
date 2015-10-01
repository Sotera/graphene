package graphene.util.validator;

import java.sql.Timestamp;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * TODO: Move this to graphene model so we can validate avro idl classes.
 * 
 * @author djue
 * 
 */
public class ValidationUtils {
	private static final Logger logger = LoggerFactory.getLogger(ValidationUtils.class);

	/**
	 * Returns the first valid object in the list.
	 * 
	 * @param obs
	 * @return
	 */
	public static Object firstNonNull(final Object... obs) {
		for (final Object o : obs) {
			if (isValid(o)) {
				return o;
			}
		}
		return null;
	}

	public static Double getSafeDouble(final Number d, final double i) {
		return d == null ? i : d.doubleValue();
	}

	public static double getSafeDouble(final String s, final double i) {
		double d = i;
		try {
			d = (Double.parseDouble(s));
		} catch (final Exception e) {
			logger.debug("Error converting string to double :" + s);
		}
		return d;
	}

	public static long getSafeLong(final Timestamp d, final long i) {
		return d == null ? i : d.getTime();
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
	public static Map<String, Object> getSafeProperties(final Map<String, Object> unsafeProps) {
		if (unsafeProps == null) {
			return null;
		} else {
			final Map<String, Object> safeProps = new HashMap<String, Object>(4);
			for (final String key : unsafeProps.keySet()) {
				if (isValid(key)) {
					final Object obj = unsafeProps.get(key);
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
	 * 
	 * @param t
	 * @return false if the collection was null or empty.
	 */
	public static boolean isValid(final Collection t) {
		boolean isvalid = true;
		if ((t == null) || (t.size() == 0)) {
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
	public static boolean isValid(final double t) {
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
	public static boolean isValid(final int t) {
		boolean isvalid = true;
		if (t <= 0) {
			isvalid = false;
		}
		return isvalid;
	}

	public static boolean isValid(final Iterable t) {
		boolean isvalid = true;
		if ((t == null) || (t.iterator() == null) || !t.iterator().hasNext()) {
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
	public static boolean isValid(final long t) {
		boolean isvalid = true;
		if (t <= 0) {
			isvalid = false;
		}
		return isvalid;
	}

	/**
	 * A convenience method for testing property (or other) maps. Makes sure the
	 * map has meaningful data inside.
	 * 
	 * @param properties
	 *            the map to test for validity
	 * @return true if the map is not null and has at least one key/value pair.
	 */
	public static boolean isValid(final Map<String, Object> properties) {
		boolean isvalid = true;
		if (properties == null) {
			isvalid = false;
		} else if (properties.isEmpty()) {
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
	public static boolean isValid(final Number t) {
		boolean isvalid = true;
		if ((t == null) || (t.doubleValue() <= 0) || (t.longValue() <= 0)) {
			isvalid = false;
		}
		return isvalid;
	}

	public static boolean isValid(final Object t) {
		boolean isvalid = true;
		if (t == null) {
			isvalid = false;
		} else if (String.class.isAssignableFrom(t.getClass())) {
			isvalid = isValid((String) t);
		} else if (Number.class.isAssignableFrom(t.getClass())) {
			isvalid = isValid((Number) t);
		} else if (Boolean.class.isAssignableFrom(t.getClass())) {
			isvalid = isValid(t);
		} else {
			isvalid = isValid(t.toString());
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
	public static boolean isValid(final Object... objects) {
		boolean isvalid = true;
		if (objects == null) {
			isvalid = false;
		} else {
			for (final Object o : objects) {
				if (!isValid(o)) {
					isvalid = false;
					break;
				}
			}
		}
		return isvalid;
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
	public static boolean isValid(final String s) {
		// We assume true unless proven false
		boolean isvalid = true;
		if (s == null) {
			isvalid = false;
		} else if (s.trim().isEmpty()) {
			isvalid = false;
		}
		return isvalid;
	}

	public static boolean isValid(final Timestamp t) {
		boolean isvalid = true;
		if (t == null) {
			isvalid = false;
		}
		return isvalid;
	}
}
