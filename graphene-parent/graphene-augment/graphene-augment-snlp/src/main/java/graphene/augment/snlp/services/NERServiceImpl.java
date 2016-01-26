/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
