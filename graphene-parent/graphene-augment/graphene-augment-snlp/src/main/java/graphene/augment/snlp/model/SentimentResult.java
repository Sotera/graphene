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

package graphene.augment.snlp.model;

import java.util.List;

public class SentimentResult {
	private String keyword;
	private List<StringWithSentiment> sentiments;

	public SentimentResult() {
		// TODO Auto-generated constructor stub
	}
	
	public SentimentResult(String keyword, List<StringWithSentiment> sentiments) {
		super();
		this.keyword = keyword;
		this.sentiments = sentiments;
	}

	/**
	 * @return the keyword
	 */
	public final String getKeyword() {
		return keyword;
	}

	/**
	 * @return the sentiments
	 */
	public final List<StringWithSentiment> getSentiments() {
		return sentiments;
	}

	/**
	 * @param keyword
	 *            the keyword to set
	 */
	public final void setKeyword(String keyword) {
		this.keyword = keyword;
	}

	/**
	 * @param sentiments
	 *            the sentiments to set
	 */
	public final void setSentiments(List<StringWithSentiment> sentiments) {
		this.sentiments = sentiments;
	}

}
