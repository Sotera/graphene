package graphene.hts.entityextraction;

public class EmailToName {

	/**
	 * For Enron
	 * 
	 * TODO: Move this to HTS module, or make it part of the ETL process --djue
	 * 
	 * @param email
	 * @return an approximation of the user name
	 */
	public String emailToName(String email) {
		int atpos = email.indexOf("@");
		if (atpos == -1)
			return null;
		String name = email.substring(0, atpos);

		int dotpos = name.indexOf(".");
		if (dotpos != -1) {
			try {
				String first = name.substring(0, dotpos);
				String last = name.substring(dotpos + 1);
				name = nameFix(first) + " " + nameFix(last);
				name = name.trim();
			} catch (Exception e) {
				System.out.println("Error fixing name " + name);
				return null;
			}

		}
		return name;
	}

	private String nameFix(String name) {
		if (name == null || name.length() == 0) {
			return name;
		} else {
			return ("" + name.charAt(0)).toUpperCase() + name.substring(1);
		}
	}
}
