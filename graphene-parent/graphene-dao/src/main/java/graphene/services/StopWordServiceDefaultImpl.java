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

package graphene.services;

import graphene.dao.StopWordService;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * This is a case INsensitive stop word service, used to determine eligibilty
 * for searches/graph traversal, etc.
 * 
 * @author djue
 * 
 */
public class StopWordServiceDefaultImpl implements StopWordService {
	private Set<String> stopwords = new HashSet<String>(1);

	/**
	 * @return the stopwords
	 */
	@Override
	public final Set<String> getStopwords() {
		return stopwords;
	}

	/**
	 * @param stopwords
	 *            the stopwords to set
	 */
	@Override
	public final void setStopwords(Set<String> stopwords) {
		this.stopwords = stopwords;
	}

	/**
	 * Allow the service to be initialized with a collection of stop words.
	 * 
	 * @param s
	 */
	public StopWordServiceDefaultImpl(Collection<String> s) {
		for (String s1 : s) {
			this.stopwords.add(s1.toLowerCase());
		}
	}

	public StopWordServiceDefaultImpl() {
		stopwords = new HashSet<String>();
	}

	@Override
	public boolean tokensAreValid(String sentence) {
		// split on white spaces and commas and periods
		return isValid(sentence.split("[\\s,\\.]*"));
	}

	@Override
	public boolean isValid(String... words) {
		for (String w : words) {
			if (w.length() <= 1) {
				return false;
			}
			if (stopwords.contains(w.toLowerCase())) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void addWords(String... words) {
		if (words != null) {
			stopwords.addAll(Arrays.asList(words));
		}
	}

	@Override
	public boolean removeWords(String... words) {
		boolean somethingRemoved = false;
		for (String w : words) {
			if(stopwords.remove(w)){
				somethingRemoved = true;
			}
		}
		return somethingRemoved;
	}

	@Override
	public boolean clear() {
		try {
			stopwords.clear();
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
