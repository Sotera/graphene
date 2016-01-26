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

package graphene.dao;

import java.util.Set;

import org.apache.tapestry5.ioc.annotations.UsesConfiguration;

/**
 * A service for allowing implementors/users to add words that should not be
 * used in node creation.
 * 
 * @author djue
 * 
 */
@UsesConfiguration(String.class)
public interface StopWordService {
	/**
	 * Add one or more word to the list.
	 * 
	 * @param words
	 */
	public abstract void addWords(String... words);

	/**
	 * Clear the stopword list
	 * 
	 * @return true if the clear operation completed successfully.
	 */
	public abstract boolean clear();

	/**
	 * Return stopwords
	 * 
	 * @return
	 */
	public abstract Set<String> getStopwords();

	/**
	 * See if any of the words provided are stop words.
	 * 
	 * @param words
	 * @return true if none of the words are stopwords. False if any of the
	 *         words are stopwords.
	 */
	public abstract boolean isValid(String... words);

	/**
	 * Remove one or more a words from the list.
	 * 
	 * @param words
	 * @return
	 */
	public abstract boolean removeWords(String... words);

	/**
	 * Set the stopwords to the provided collection.
	 * 
	 * @param stopwords
	 */
	public abstract void setStopwords(Set<String> stopwords);

	/**
	 * Splits a string into individual words, and applies isValid to them.
	 * 
	 * @param sentence
	 * @return true if none of the words in the sentence were stopwords. False
	 *         if any stopwords were contained in the sentence.
	 */
	public abstract boolean tokensAreValid(String sentence);
}
