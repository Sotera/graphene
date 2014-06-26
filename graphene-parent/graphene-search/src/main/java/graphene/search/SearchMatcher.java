package graphene.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SearchMatcher {
	private String str;
	private String searchStringWithoutQuotes;
	boolean caseSensitive;
	List<SearchCriterion> search;

	/**
	 * Constructor.
	 * 
	 * @param searchString
	 *            the search string created by the user. Can contain multiple
	 *            words. <BR/>
	 *            + before a word means word must exist. <BR/>
	 *            - means word must not exist. <BR/>
	 *            * before a word means word ends with the string. <BR/>
	 *            * after a word means the word starts with the string. <BR/>
	 *            * before and after the word must contain the string
	 * @param isCaseSensitive
	 *            boolean
	 */
	public SearchMatcher(String searchString, boolean isCaseSensitive) {
		this.str = searchString;
		this.caseSensitive = isCaseSensitive;
		if (!isCaseSensitive) {
			str = str.toLowerCase();
		}
		this.searchStringWithoutQuotes = str.replace("\"", "");

		search = new ArrayList<SearchCriterion>();

		Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(
				searchString);

		while (m.find()) {

			String s = m.group(1);
			s = s.replace("\"", "");
			if (!isCaseSensitive) {
				s = s.toLowerCase();
			}
			search.add(new SearchCriterion(s));
		}

	}

	/**
	 * Compares a target string with the search criteria
	 * 
	 * @param target
	 *            String to compare - may contain several words
	 * @return int representing the score. The higher the score the closer the
	 *         match. -1 means no match
	 */
	public int calcScore(String target) {
		int score = 0;
		if (target != null) {
			if (!this.caseSensitive) {
				target = target.toLowerCase();
			}
			// Start by giving priority to complete matches

			if (target.equals(searchStringWithoutQuotes)) {
				return 90;
			}
			if (target.startsWith(searchStringWithoutQuotes)) {
				return 80;
			}
			if (target.contains(searchStringWithoutQuotes)) {
				return 70;
			}

			// No look at it on a word by word basis

			Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(
					target);
			List<String> words = new ArrayList<String>();
			while (m.find()) {
				String s = m.group(1).replace("\"", "");
				words.add(s);
			}
			for (SearchCriterion sc : search) {
				if (sc.str.contains(" ")) { // derived from a quoted string
					if (target.contains(str)) {
						score++;
					}
					continue;
				}
				int count = 0;
				for (String s : words) {
					if (sc.match(s)) {
						count++;
					}
				}
				if (sc.isMustHave() && count == 0) {
					return -1;
				}
				if (sc.isMustNotHave() && count > 0) {
					return -1;
				}
				if (count > 0) {
					score++;
				}
			}
		}

		return score;
	}

	public List<SearchCriterion> getSearch() {
		return search;
	}

	public boolean isCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean caseSensitive) {
		this.caseSensitive = caseSensitive;
	}

	public void setSearch(List<SearchCriterion> search) {
		this.search = search;
	}
}
