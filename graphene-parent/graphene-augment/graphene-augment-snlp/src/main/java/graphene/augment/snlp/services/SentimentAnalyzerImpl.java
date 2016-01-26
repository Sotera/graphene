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

import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.neural.rnn.RNNCoreAnnotations;
import edu.stanford.nlp.pipeline.Annotation;
import edu.stanford.nlp.pipeline.StanfordCoreNLP;
import edu.stanford.nlp.sentiment.SentimentCoreAnnotations;
import edu.stanford.nlp.trees.Tree;
import edu.stanford.nlp.util.CoreMap;
import graphene.augment.snlp.model.StringWithSentiment;
import graphene.util.validator.ValidationUtils;

import java.util.Properties;

public class SentimentAnalyzerImpl implements SentimentAnalyzer {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * graphene.augment.snlp.SentimentAnalyzer#findSentiment(java.lang.String)
	 */
	@Override
	public StringWithSentiment findSentiment(String line) {

		Properties props = new Properties();
		props.setProperty("annotators", "tokenize,ssplit,parse,sentiment");
		StanfordCoreNLP pipeline = new StanfordCoreNLP(props);
		int mainSentiment = 0;
		if (ValidationUtils.isValid(line)) {

			int longest = 0;
			Annotation an = pipeline.process(line);
			for (CoreMap sentence : an
					.get(CoreAnnotations.SentencesAnnotation.class)) {
				Tree tree = sentence
						.get(SentimentCoreAnnotations.AnnotatedTree.class);
				int sentiment = RNNCoreAnnotations.getPredictedClass(tree);
				String partText = sentence.toString();
				if (partText.length() > longest) {
					mainSentiment = sentiment;
					longest = partText.length();
				}

			}
		}
		if (mainSentiment == 2 || mainSentiment > 4 || mainSentiment < 0) {
			return null;
		}
		StringWithSentiment sm = new StringWithSentiment(line,
				toCss(mainSentiment));
		return sm;
	}

	private String toCss(int mainSentiment) {
		switch (mainSentiment) {
		case 0:
			return "label label-danger";
		case 1:
			return "label label-danger";
		case 2:
			return "label label-warning";
		case 3:
			return "label label-success";
		case 4:
			return "label label-success";
		default:
			return "";
		}
	}
}
