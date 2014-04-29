package graphene.search;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 
 * @author djue
 * 
 */
public class SearchCriterionBuilder {

	/**
	 * Breaks up a search string into its boolean parts, and returns a list of
	 * SearchCriterion
	 * 
	 * @param str
	 * @return
	 */
	public static List<SearchCriterion> build(String str) {

		ArrayList<SearchCriterion> search = new ArrayList<SearchCriterion>();
/*
		if (!str.contains("\"") && !str.contains("+")) {
			// has not used valid syntax so look for all the words.
			for (String s:str.split(" ")) {
				SearchCriterion e = new SearchCriterion(s);
				e.setMustHave(true);
				search.add(e);
			}
			return search;
		}
*/		
		if (str != null && !str.trim().isEmpty()) {

			Matcher m = Pattern.compile("([^\"]\\S*|\".+?\")\\s*").matcher(str);

			while (m.find()) {

				String s = m.group(1);
				s = s.replace("\"", "");
				search.add(new SearchCriterion(s));
			}

		}
		return search;
	}
}
