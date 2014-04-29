package graphene.model.idl;

/**
 * We'll see how well this works. The idea is that certain core functionality is
 * exposed through some known services, especially for constructing query
 * parameters in REST calls.
 * 
 * For instance, if the server side responds that you should use a comma for
 * multiple values, your js front end can now construct query parameters using
 * commas. This allows the back end to drive parts of the front end (such as
 * displaying supported search types, etc)
 * 
 * 
 * See this reference for valid delimiters:
 * http://tools.ietf.org/html/rfc3986#appendix-A
 * 
 * TODO: May need to change property type delimiter if it conflicts
 * 
 * @author djue
 * 
 */
public enum G_Delimiter {
	INVALID(999, "INVALID", "", "INVALID"), MULTIPLE_VALUE(
			0,
			"multiple",
			",",
			"Item1,Item2,Item3"), SEARCH_TYPE(
			1,
			"Search Type",
			":",
			"<SearchType>:Item1"), PROPERTY_TYPE(
			2,
			"Property Type",
			"=",
			"<PropertyType>=Item1");

	public static G_Delimiter fromValue(String v) {
		for (G_Delimiter c : values()) {
			if (c.getValueString().equalsIgnoreCase(v)) {
				return c;
			}
		}
		return INVALID;
	}

	public static G_Delimiter fromIndex(int v) {
		for (G_Delimiter c : values()) {
			if (c.index == v) {
				return c;
			}
		}
		return INVALID;
	}

	private String friendlyName;
	private int index;

	private String usage;

	private String valueString;

	G_Delimiter(int i, String friendlyName, String valueString, String usage) {
		this.setIndex(i);
		this.setFriendlyName(friendlyName);
		this.setUsage(usage);
		this.setValueString(valueString);
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public int getIndex() {
		return index;
	}

	public String getUsage() {
		return usage;
	}

	public String getValueString() {
		return valueString;
	}

	public void setFriendlyName(String friendlyName) {
		this.friendlyName = friendlyName;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public void setUsage(String usage) {
		this.usage = usage;
	}

	public void setValueString(String valueString) {
		this.valueString = valueString;
	}
}
