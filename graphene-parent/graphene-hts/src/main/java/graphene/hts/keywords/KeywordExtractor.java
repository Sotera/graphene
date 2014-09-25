package graphene.hts.keywords;

import java.util.SortedSet;

public interface KeywordExtractor {

	public abstract SortedSet<String> getSimpleKeywords(String narrative);

}