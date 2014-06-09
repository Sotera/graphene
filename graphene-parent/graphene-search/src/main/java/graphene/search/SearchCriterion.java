package graphene.search;

public class SearchCriterion {
	private boolean contains = false;
	private boolean endsWith = false;
	private boolean mustHave = false;
	private boolean mustNotHave = false;
	private boolean startsWith = false;
	String str;

	public SearchCriterion(String s) {
		setStr(s);
	}

	public String getStr() {
		return str;
	}

	public boolean isContains() {
		return contains;
	}

	public boolean isEndsWith() {
		return endsWith;
	}

	public boolean isMustHave() {
		return mustHave;
	}

	public boolean isMustNotHave() {
		return mustNotHave;
	}

	public boolean isStartsWith() {
		return startsWith;
	}

	public boolean match(String target) {
		if (startsWith) {
			return target.startsWith(str);
		} else if (endsWith) {
			return target.endsWith(str);
		} else if (contains) {
			return target.contains(str);
		} else {
			return str.equalsIgnoreCase(target);
		}
	}

	public void setContains(boolean contains) {
		this.contains = contains;
	}

	public void setEndsWith(boolean endsWith) {
		this.endsWith = endsWith;
	}

	public void setMustHave(boolean mustHave) {
		this.mustHave = mustHave;
	}

	public void setMustNotHave(boolean mustNotHave) {
		this.mustNotHave = mustNotHave;
	}

	public void setStartsWith(boolean startsWith) {
		this.startsWith = startsWith;
	}

	public void setStr(String s) {
		if (s.startsWith("+")) {
			mustHave = true;
			s = s.substring(1);
		}
		if (s.startsWith("-")) {
			mustNotHave = true;
			s = s.substring(1);
		}
		// fallthrough here accounts for +*foo, -foo* etc
		if (s.startsWith("*")) {
			endsWith = true;
			s = s.substring(1);
		}
		if (s.endsWith("*")) {
			startsWith = true;
			s = s.substring(0, s.length() - 1);
		}

		if (startsWith && endsWith) {
			contains = true;
			startsWith = endsWith = false;
		}
		// if not specified, the default should be contains.
		if (!(mustHave || mustNotHave || endsWith || startsWith)) {
			contains = true;
			mustHave = true;
		}

		str = s;

	}

	public String toString() {
		StringBuilder b = new StringBuilder();
		if (mustHave) {
			b.append(" Must Have ");
		}
		if (mustNotHave) {
			b.append(" Must Not Have ");
		}
		if (startsWith) {
			b.append(" Starts With ");
		}
		if (endsWith) {
			b.append(" Ends With ");
		}
		if (contains) {
			b.append(" Contains ");
		}
		b.append("String: " + str);

		return b.toString();
	}
}
