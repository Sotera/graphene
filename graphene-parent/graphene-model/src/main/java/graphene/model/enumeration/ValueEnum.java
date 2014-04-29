package graphene.model.enumeration;

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