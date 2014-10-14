package graphene.hts.entityextraction;

import graphene.util.validator.ValidationUtils;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class AbstractExtractor implements Extractor {

	private Matcher m;
	protected Pattern p;

	public Collection<String> extract(String source) {
		Set<String> matchList = new HashSet<String>();
		if (ValidationUtils.isValid(source)) {
			m = p.matcher(source);
			while (m.find()) {

				matchList.add(postProcessMatch(m.group(1)));

			}
		}
		return matchList;
	}

	/**
	 * Adds the abilty to post process the match so that it more closely
	 * resembles structured entities. Override in your concrete class if needed.
	 * 
	 * @param group
	 * @return
	 */
	private String postProcessMatch(String match) {
		return match;
	}

}