package graphene.services;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

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

		this.stopwords.addAll(s);
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
		boolean valid = false;
		if (words == null) {
			// there were no stopwords
			valid = true;
		} else if (Collections.disjoint(stopwords, Arrays.asList(words))) {
			// returns true if the two collections have no elements in
			// common
			valid = true;
		}
		return valid;
	}

	@Override
	public void addWords(String... words) {
		if (words != null) {
			stopwords.addAll(Arrays.asList(words));
		}
	}

}
