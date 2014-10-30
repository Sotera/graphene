package graphene.services;

import java.util.Set;

import org.apache.tapestry5.ioc.annotations.UsesConfiguration;

@UsesConfiguration(String.class)
public interface StopWordService {

	public abstract void addWords(String... words);

	// public abstract void removeWords(String... words);

	boolean tokensAreValid(String sentence);

	boolean isValid(String... words);

	Set<String> getStopwords();

	void setStopwords(Set<String> stopwords);


}
