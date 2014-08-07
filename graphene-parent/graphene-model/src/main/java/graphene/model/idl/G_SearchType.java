package graphene.model.idl;

public enum G_SearchType {

	// static final public int COMPARE_SIMPLE = 0;
	// static final public int COMPARE_EQUALS = 1;
	// static final public int COMPARE_LESS = 2;
	// static final public int COMPARE_GREATER = 3;
	// static final public int COMPARE_INCLUDE = 4;
	// static final public int COMPARE_NOTINCLUDE = 5;
	// static final public int COMPARE_REGEX = 6;
	// static final public int COMPARE_SOUNDSLIKE = 7;
	// static final public int COMPARE_INVALID = 999;
	COMPARE_SIMPLE(
			0,
			"simple",
			"Uses LIKE, which traverses through all your data and can be slower. "),
	COMPARE_EQUALS(1, "equals", "Only finds exact matches."),
	COMPARE_LESS(
			2,
			"less",
			"Finds values that are less than the supplied value."),

	COMPARE_GREATER(
			3,
			"greater",
			"Finds values that are greater than the supplied value."),
	COMPARE_INCLUDE(
			4,
			"include",
			"Finds values that include the supplied value."),
	COMPARE_NOTINCLUDE(
			5,
			"exclude",
			"Finds values that do not include the supplied value."),

	COMPARE_REGEX(6, "regex", "For advanced users only."),
	COMPARE_SOUNDSLIKE(
			7,
			"soundslike",
			"If available, uses algorithms to find words that sound similar."),

	COMPARE_ENDSWITH(
			8,
			"endsWith",
			"Finds values that end with the supplied value."),
	COMPARE_STARTSWITH(
			9,
			"startsWith",
			"Finds values that start with the supplied value."),

	COMPARE_CONTAINS(
			10,
			"contains",
			"CONTAINS in uses a special text index and can be faster than LIKE, if set up correctly."),

	COMPARE_INVALID(999, "", "Invalid Search Type");
	private int index;

	// probably a bad name, let's FIXME later.
	private String valueString, description;

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	G_SearchType(int i, String valueString, String description) {
		this.setIndex(i);
		this.setValueString(valueString);
		this.setDescription(description);
	}

	/**
	 * Use this to look up the enum by String
	 * 
	 * @param v
	 *            the enum valueString you want to look up, case INsensitive.
	 * @return the search type with the value v, or COMPARE_INVALID
	 */
	public static G_SearchType fromValue(String v) {
		for (G_SearchType c : values()) {
			if (c.getValueString().equalsIgnoreCase(v)) {
				return c;
			}
		}
		return COMPARE_INVALID;
	}

	/**
	 * Use this to look up the enum by integer index
	 * 
	 * @param v
	 * @return the search type with the index v, or COMPARE_INVALID
	 */
	public static G_SearchType fromValue(int v) {
		for (G_SearchType c : values()) {
			if (c.getIndex() == v) {
				return c;
			}
		}
		return COMPARE_INVALID;
	}

	/**
	 * Use this if you are comparing search types to an integer
	 * 
	 * @return the value
	 */
	public int getIndex() {
		return index;
	}

	/**
	 * @param value
	 *            the value to set
	 */
	public void setIndex(int value) {
		this.index = value;
	}

	/**
	 * Use this if you are comparing search types to a String, or printing the
	 * type.
	 * 
	 * @return the valueString
	 */
	public String getValueString() {
		return valueString;
	}

	/**
	 * @param valueString
	 *            the valueString to set
	 */
	public void setValueString(String valueString) {
		this.valueString = valueString;
	}

}
