package graphene.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a case INsensitive stop word service, used to determine eligibilty
 * for searches/graph traversal, etc.
 * 
 * @author djue
 * 
 */
public class StopWordServiceImpl implements StopWordService {
	private Set<String> stopwords = new HashSet<String>(1);

	/**
	 * @return the stopwords
	 */
	@Override
	public final Set<String> getStopwords() {
		return stopwords;
	}

	/**
	 * @param stopwords
	 *            the stopwords to set
	 */
	@Override
	public final void setStopwords(Set<String> stopwords) {
		this.stopwords = stopwords;
	}

	/**
	 * Allow the service to be initialized with a collection of stop words.
	 * 
	 * @param s
	 */
	public StopWordServiceImpl(Collection<String> s) {
		for (String s1 : s) {
			this.stopwords.add(s1.toLowerCase());
		}
	}

	public StopWordServiceImpl() {
		stopwords = new HashSet<String>();
	}

	@Override
	public boolean tokensAreValid(String sentence) {
		// split on white spaces and commas and periods
		return isValid(sentence.split("[\\s,\\.]*"));
	}

	@Override
	public boolean isValid(String... words) {
		for (String w : words) {
			if (w.length() <= 1) {
				return false;
			}
			if (stopwords.contains(w.toLowerCase())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void addWords(String... words) {
		if (words != null) {
			stopwords.addAll(Arrays.asList(words));
		}
	}

	@Override
	public boolean removeWords(String... words) {
		boolean somethingRemoved = false;
		for (String w : words) {
			if(stopwords.remove(w)){
				somethingRemoved = true;
			}
		}
		return somethingRemoved;
	}

	@Override
	public boolean clear() {
		try {
			stopwords.clear();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
