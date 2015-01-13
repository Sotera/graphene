package graphene.dao;

import graphene.util.Tuple;

import java.util.Collection;

public interface IconService {

	public abstract void addPattern(String pattern, boolean caseSensitive,
			String iconClass, String reason);

	/**
	 * Returns a list of icon styles that match the list of keys provided.
	 * 
	 * @param keys
	 * @return
	 */
	// public abstract Collection<String> getIconsForKeys(String... keys);

	/**
	 * Returns a list of icon styles that match up with the contents of the text
	 * provided.
	 * 
	 * 
	 * @param text
	 * @param otherKeys
	 *            usually this is the search term(s) used to get the narrative.
	 *            The narrative is checked to see if it contains the term(s)
	 * @return
	 */
	public abstract Collection<Tuple<String, String>> getIconsForText(
			String text, String... otherKeys);

	/**
	 * Returns a list of tuples with <icon style, count> as the values, where
	 * count > 0
	 * 
	 * @param text
	 * @return
	 */
	public abstract Collection<Tuple<String, String>> getIconsForTextWithCount(
			String text, String... otherKeys);

	public abstract void removePattern(String pattern, boolean caseSensitive);
}
