package graphene.hts.keywords;

import java.util.Arrays;
import java.util.Iterator;
import java.util.SortedSet;
import java.util.TreeSet;

public class KeywordExtractorImpl implements KeywordExtractor {
	/* (non-Javadoc)
	 * @see graphene.hts.keywords.KeywordExtractor#getSimpleKeywords(java.lang.String)
	 */
	@Override
	public SortedSet<String> getSimpleKeywords(String narrative) {
		TreeSet<String> keyWords = new TreeSet<String>(
				Arrays.asList(((String) narrative).split(" ")));
		for (Iterator<String> iter = keyWords.iterator(); iter.hasNext();) {
			String k = iter.next();
			if (k.length() < 2) {
				iter.remove();
			}
		}
		return keyWords;
	}

}
