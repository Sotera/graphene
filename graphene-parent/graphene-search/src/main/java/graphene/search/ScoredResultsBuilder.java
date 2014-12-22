package graphene.search;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ScoredResultsBuilder {

	public static List<String> build(String searchString,
			Collection<String> source, boolean caseSensitive) {
		int score;
		List<String> scoredResults = new ArrayList<String>();
		List<SearchResult> matches = new ArrayList<SearchResult>();
		SearchMatcher sm = new SearchMatcher(searchString, caseSensitive);

		for (String target : source) {
			score = sm.calcScore(target);
			if (score > 0) {
				matches.add(new SearchResult(target, score));
			}
		}

		// sort the matches by score (uses a custom comparator internally)
		Collections.sort(matches);

		// this is perhaps inefficient, and we may want to make use of the
		// scores later.
		// TODO: create a version that returns scored results list.
		for (SearchResult r : matches) {
			scoredResults.add(r.getStr());
		}
		return scoredResults;
	}

}
