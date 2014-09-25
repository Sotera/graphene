package graphene.hts.sentences;

import java.util.Collection;

public interface SentenceExtractor {

	public abstract Collection<String> getSimpleSentences(String narrative);

}