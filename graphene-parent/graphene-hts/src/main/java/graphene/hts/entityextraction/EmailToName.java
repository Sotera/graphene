package graphene.hts.entityextraction;

public class EmailToName {

	/**
	 * 
	 * @param email
	 * @return an approximation of the user name or handle
	 */
	public String emailToName(final String email) {
		final int atpos = email.indexOf("@");
		if (atpos == -1) {
			return null;
		}
		String name = email.substring(0, atpos);

		final int dotpos = name.indexOf(".");
		if (dotpos != -1) {
			try {
				final String first = name.substring(0, dotpos);
				final String last = name.substring(dotpos + 1);
				name = nameFix(first) + " " + nameFix(last);
				name = name.trim();
			} catch (final Exception e) {
				System.out.println("Error fixing name " + name);
				return null;
			}

		}
		return name;
	}

	private String nameFix(final String name) {
		if ((name == null) || (name.length() == 0)) {
			return name;
		} else {
			return ("" + name.charAt(0)).toUpperCase() + name.substring(1);
		}
	}
}
