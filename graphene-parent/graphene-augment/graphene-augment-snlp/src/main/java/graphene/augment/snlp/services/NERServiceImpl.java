package graphene.augment.snlp.services;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;
import graphene.augment.snlp.model.NERResult;

public class NERServiceImpl implements NERService {
	@Inject
	private CRFClassifier<CoreLabel> classifier;

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * graphene.augment.snlp.services.NERService#getResults(java.lang.String)
	 */
	@Override
	public List<NERResult> getResults(final String text) {
		final List<List<CoreLabel>> classify = classifier.classify(text);
		final List<NERResult> results = new ArrayList<NERResult>();
		for (final List<CoreLabel> coreLabels : classify) {
			for (final CoreLabel coreLabel : coreLabels) {
				final String word = coreLabel.word();
				final String answer = coreLabel.get(CoreAnnotations.AnswerAnnotation.class);
				if (!"O".equals(answer)) {
					results.add(new NERResult(word, answer));
				}
			}
		}
		return results;
	}
}
