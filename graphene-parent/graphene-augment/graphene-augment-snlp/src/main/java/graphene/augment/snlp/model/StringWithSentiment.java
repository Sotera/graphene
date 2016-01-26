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

public class StringWithSentiment {

	public StringWithSentiment(String line, String css) {
		this.line = line;
		this.css = css;
	}

	private String line = "", css = "";

	/**
	 * @return the line
	 */
	public final String getLine() {
		return line;
	}

	/**
	 * @param line
	 *            the line to set
	 */
	public final void setLine(String line) {
		this.line = line;
	}

	/**
	 * @return the css
	 */
	public final String getCss() {
		return css;
	}

	/**
	 * @param css
	 *            the css to set
	 */
	public final void setCss(String css) {
		this.css = css;
	}
}
