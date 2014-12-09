package graphene.model.enumeration;

/**
 * This is mostly used for manually defining data properties for sources which
 * are not in an RDBMS, or are not covered by QueryDSL code generation. (i.e.
 * Solr). Here you can define an implementation of an Enum which contains a
 * value, and the enum field which can be properly named. Then you can construct
 * queries using string concatenation. The idea here is to keep misspellings out
 * of the query by having a hard type to reference. It is not required to use
 * this class or implementations of it.
 * 
 * @author djue
 * 
 * Deprecated, use a String instead.
 */
@Deprecated
public abstract interface ValueEnum {

	/**
	 * Value.
	 * 
	 * @return the string
	 */
	String value();

	/**
	 * a way to look up the enum using a String.
	 * 
	 * @param v
	 *            the String to compare to the value field in the enum.
	 * @return The <? extends ValueEnum> that had a value field v.
	 *         Implementations can also return a catch-all or invalid enum if
	 *         the value wasn't found.
	 * 
	 */
	ValueEnum fromValue(final String v);

	boolean isColumnValueValid(final String v);
}