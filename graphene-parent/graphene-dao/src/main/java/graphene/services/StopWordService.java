package graphene.services;

import java.util.Set;

import org.apache.tapestry5.ioc.annotations.UsesConfiguration;

@UsesConfiguration(String.class)
public interface StopWordService {
	/**
	 * Add one or more word to the list.
	 * 
	 * @param words
	 */
	public abstract void addWords(String... words);

	/**
	 * Remove one or more a words from the list.
	 * 
	 * @param words
	 * @return
	 */
	public abstract boolean removeWords(String... words);

	/**
	 * Splits a string into individual words, and applies isValid to them.
	 * 
	 * @param sentence
	 * @return true if none of the words in the sentence were stopwords. False
	 *         if any stopwords were contained in the sentence.
	 */
	public abstract boolean tokensAreValid(String sentence);

	/**
	 * See if any of the words provided are stop words.
	 * 
	 * @param words
	 * @return true if none of the words are stopwords. False if any of the
	 *         words are stopwords.
	 */
	public abstract boolean isValid(String... words);

	/**
	 * Return stopwords
	 * 
	 * @return
	 */
	public abstract Set<String> getStopwords();

	/**
	 * Set the stopwords to the provided collection.
	 * 
	 * @param stopwords
	 */
	public abstract void setStopwords(Set<String> stopwords);

	/**
	 * Clear the stopword list
	 * 
	 * @return true if the clear operation completed successfully.
	 */
	public abstract boolean clear();
}
