package graphene.model.idl;

public enum G_CanonicalTruthValues {
	FALSE(2, "False", "false"), TRUE(1, "True", "true"), UNCERTAIN(
			3,
			"Uncertain",
			"uncertain"), UNKNOWN(4, "Unknown", "unknown"), REDACTED(
			-1,
			"[REDACTED]",
			"REDACTED");

	public static G_CanonicalTruthValues fromIndex(int v) {
		for (G_CanonicalTruthValues c : values()) {
			if (c.index == v) {
				return c;
			}
		}
		return UNKNOWN;
	}

	public static G_CanonicalTruthValues fromValue(String v) {
		for (G_CanonicalTruthValues c : values()) {
			if (c.getValueString().equalsIgnoreCase(v)) {
				return c;
			}
		}
		return UNKNOWN;
	}

	private String friendlyName;

	private int index;

	private String valueString;

	G_CanonicalTruthValues(int index, String friendlyName, String valueString) {
		this.setIndex(index);
		this.setFriendlyName(friendlyName);

		this.setValueString(valueString);
	}

	public String getFriendlyName() {
		return friendlyName;
	}

	public int getIndex() {
		return index;
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

	public void setValueString(String valueString) {
		this.valueString = valueString;
	}
}
