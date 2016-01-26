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

package graphene.augment.snlp;

import java.util.List;

import edu.stanford.nlp.ie.AbstractSequenceClassifier;
import edu.stanford.nlp.ie.crf.CRFClassifier;
import edu.stanford.nlp.io.IOUtils;
import edu.stanford.nlp.ling.CoreAnnotations;
import edu.stanford.nlp.ling.CoreLabel;

/**
 * This is a demo of calling CRFClassifier programmatically.
 * <p>
 * Usage:
 * {@code java -mx400m -cp "stanford-ner.jar:." NERDemo [serializedClassifier [fileName]] }
 * <p>
 * If arguments aren't specified, they default to
 * classifiers/english.all.3class.distsim.crf.ser.gz and some hardcoded sample
 * text.
 * <p>
 * To use CRFClassifier from the command line:
 * </p>
 * <blockquote>
 * {@code java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier [classifier] -textFile [file] }
 * </blockquote>
 * <p>
 * Or if the file is already tokenized and one word per line, perhaps in a
 * tab-separated value format with extra columns for part-of-speech tag, etc.,
 * use the version below (note the 's' instead of the 'x'):
 * </p>
 * <blockquote>
 * {@code java -mx400m edu.stanford.nlp.ie.crf.CRFClassifier -loadClassifier [classifier] -testFile [file] }
 * </blockquote>
 * 
 * @author Jenny Finkel
 * @author Christopher Manning
 */

public class NERDemo {

	public static void main(String[] args) throws Exception {

		String serializedClassifier = "src/main/resources/edu/stanford/nlp/models/ner/english.all.3class.caseless.distsim.crf.ser.gz";

		if (args.length > 0) {
			serializedClassifier = args[0];
		}

		AbstractSequenceClassifier<CoreLabel> classifier = CRFClassifier
				.getClassifier(serializedClassifier);

		/*
		 * For either a file to annotate or for the hardcoded text example, this
		 * demo file shows two ways to process the output, for teaching
		 * purposes. For the file, it shows both how to run NER on a String and
		 * how to run it on a whole file. For the hard-coded String, it shows
		 * how to run it on a single sentence, and how to do this and produce an
		 * inline XML output format.
		 */
		if (args.length > 1) {
			String fileContents = IOUtils.slurpFile(args[1]);
			List<List<CoreLabel>> out = classifier.classify(fileContents);
			for (List<CoreLabel> sentence : out) {
				for (CoreLabel word : sentence) {
					System.out.print(word.word() + '/'
							+ word.get(CoreAnnotations.AnswerAnnotation.class)
							+ ' ');
				}
				System.out.println();
			}
			System.out.println("---");
			out = classifier.classifyFile(args[1]);
			for (List<CoreLabel> sentence : out) {
				for (CoreLabel word : sentence) {
					System.out.print(word.word() + '/'
							+ word.get(CoreAnnotations.AnswerAnnotation.class)
							+ ' ');
				}
				System.out.println();
			}

		} else {
			String[] example = {
					"Good afternoon Rajat Raina, how are you today?",
					"I go to school at Stanford University, which is located in California." };
			for (String str : example) {
				System.out.println(classifier.classifyToString(str));
			}
			System.out.println("---");

			for (String str : example) {
				// This one puts in spaces and newlines between tokens, so just
				// print not println.
				System.out.print(classifier.classifyToString(str, "slashTags",
						false));
			}
			System.out.println("---");

			for (String str : example) {
				System.out.println(classifier.classifyWithInlineXML(str));
			}
			System.out.println("---");

			for (String str : example) {
				System.out.println(classifier
						.classifyToString(str, "xml", true));
			}
			System.out.println("---");

			int i = 0;
			for (String str : example) {
				for (List<CoreLabel> lcl : classifier.classify(str)) {
					for (CoreLabel cl : lcl) {
						System.out.print(i++ + ": ");
						System.out.println(cl.toShorterString());
					}
				}
			}
		}
	}

}
