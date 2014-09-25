package graphene.hts.sentences;

import java.util.Collection;
import java.util.List;

import graphene.util.StringUtils;

public class SentenceExtractorImpl implements SentenceExtractor {
	public SentenceExtractorImpl() {
		// TODO Auto-generated constructor stub
	}

	/* (non-Javadoc)
	 * @see graphene.hts.sentences.SentenceExtractor#getSimpleSentences(java.lang.String)
	 */
	@Override
	public Collection<String> getSimpleSentences(String narrative) {
		String text = narrative;
		text = StringUtils.cleanUpAllCaps(text, 0.2d);
		List<String> sentences = StringUtils.convertToSentences(text);
		return sentences;
	}
}
