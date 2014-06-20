package graphene.hts.me;

import graphene.model.idl.G_Gender;
import graphene.util.Tuple;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author djue
 * 
 */
public class FamilyReferenceExtractor {
	/**
	 * Patterns to build for:
	 * 
	 * Son of
	 * 
	 * Son
	 * 
	 * S/O
	 * 
	 * S / O
	 * 
	 * S/ O
	 * 
	 * S /O
	 * 
	 * O/S
	 * 
	 * SO
	 * 
	 * 
	 * 
	 * 
	 * Stop words to remove false positives (if these words are found in the
	 * match, don't use them)
	 * 
	 * Village City Town Street Apt
	 * 
	 */
	private Pattern sonOfPattern = null;

	private Pattern daughterOfPattern = null;

	private Pattern wifeOfPattern = null;

	public FamilyReferenceExtractor() {
		sonOfPattern = Pattern.compile(".*[Ss]\\s*[\\.\\/]\\s*[Oo]\\s*([\\w\\s]+),(.*)");
		daughterOfPattern = Pattern.compile(".*[Dd]\\s*[\\/]\\s*[Oo]\\s*(.+),.*");
		wifeOfPattern = Pattern.compile(".*[Ww]\\s*[\\.\\/]\\s*[Oo]\\s*(.+),.*");
	}

	/**
	 * Removes leading and trailing whitespace, and replaces multiple contiguous
	 * whitespaces with one space.
	 * 
	 * @param name
	 * @return
	 */
	public String cleanName(String name) {
		// remove leading and trailing white spaces.
		// replace multiple contiguous spaces with one space
		return name.trim().replaceAll("\\s+", " ");
	}

	/**
	 * Use getParentAndImputedChildGender instead.
	 * 
	 * @param s
	 * @return
	 */
	@Deprecated
	public String getParent(String s) {
		Matcher m = sonOfPattern.matcher(s);
		String parent = null;
		if (m.find()) {
			parent = m.group(1);
		}
		return parent;
	}

	public Tuple<String, G_Gender> getParentAndImputedChildGender(String s) {
		if (s == null || s.isEmpty()) {
			return null;
		}
		Tuple<String, G_Gender> retval = new Tuple<String, G_Gender>();

		Matcher m = sonOfPattern.matcher(s);
		if (m.find()) {
			retval.set(m.group(1), G_Gender.MALE);
		} else {
			m = daughterOfPattern.matcher(s);
			if (m.find()) {
				retval.set(m.group(1), G_Gender.FEMALE);
			}
			// If we make a parentOfPattern, we'd put that here and impute the
			// Gender as unknown
		}
		return retval;
	}

	public Tuple<String, G_Gender> getSpouseAndImputedSpouseGender(String s) {
		Tuple<String, G_Gender> retval = new Tuple<String, G_Gender>();
		Matcher m = wifeOfPattern.matcher(s);
		if (m.find()) {
			retval.set(m.group(1), G_Gender.MALE);
		}
		// Our problem set doesn't require us to create a husbandOfPattern
		// because of the nature of the data.
		return retval;
	}
}
