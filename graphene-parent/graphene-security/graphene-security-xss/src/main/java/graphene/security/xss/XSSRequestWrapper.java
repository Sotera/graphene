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

package graphene.security.xss;

/*
 * Copyright (c) 2012 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.util.regex.Pattern;

import org.apache.tapestry5.services.DelegatingRequest;
import org.apache.tapestry5.services.Request;
import org.owasp.esapi.ESAPI;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class XSSRequestWrapper extends DelegatingRequest {
	private static final Logger logger = LoggerFactory.getLogger(XSSRequestWrapper.class);

	private static Pattern[] patterns = new Pattern[] {
			// Script fragments
			Pattern.compile("<script>(.*?)</script>", Pattern.CASE_INSENSITIVE),
			// src='...'
			Pattern.compile("src[\r\n]*=[\r\n]*\"([^\"]*(?<!\\.jpg|png|gif|jpeg))\"", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE | Pattern.DOTALL), // Escape All src="*"
															// (but keep images)
			Pattern.compile("src[\r\n]*=[\r\n]*'([^']*(?<!\\.jpg|png|gif|jpeg))'", Pattern.CASE_INSENSITIVE
					| Pattern.MULTILINE | Pattern.DOTALL), // Escape All src='*'
															// (but keep images)
			// lonely script tags
			Pattern.compile("</script>", Pattern.CASE_INSENSITIVE),
			Pattern.compile("<script(.*?)>", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			// eval(...)
			Pattern.compile("eval\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			// expression(...)
			Pattern.compile("expression\\((.*?)\\)", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL),
			// javascript:...
			Pattern.compile("javascript:", Pattern.CASE_INSENSITIVE),
			// vbscript:...
			Pattern.compile("vbscript:", Pattern.CASE_INSENSITIVE),
			// onload(...)=...
			Pattern.compile("onload(.*?)=", Pattern.CASE_INSENSITIVE | Pattern.MULTILINE | Pattern.DOTALL) };

	public XSSRequestWrapper(final Request request) {
		super(request);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tapestry5.services.Request#getHeader(java.lang.String)
	 */
	@Override
	public String getHeader(final String name) {
		return stripXSS(super.getHeader(name));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.apache.tapestry5.services.Request#getParameter(java.lang.String)
	 */
	@Override
	public String getParameter(final String name) {
		return stripXSS(super.getParameter(name));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.tapestry5.services.Request#getParameters(java.lang.String)
	 */
	@Override
	public String[] getParameters(final String name) {
		final String[] orig = super.getParameters(name);
		String[] stripped = null;
		if (orig != null) {
			stripped = new String[orig.length];
			for (int i = 0; i < orig.length; i++) {
				stripped[i] = stripXSS(orig[i]);
			}
		}
		return stripped;
	}

	private String stripXSS(final String value) {
		String clearedValue = value;
		if (clearedValue != null) {
			// It's highly recommended to use the ESAPI library to avoid encoded
			// attacks.
			clearedValue = ESAPI.encoder().canonicalize(clearedValue);

			// Avoid null characters
			clearedValue = clearedValue.replaceAll("\0", "");

			// Remove all sections that match a pattern
			for (final Pattern scriptPattern : patterns) {
				clearedValue = scriptPattern.matcher(clearedValue).replaceAll("");
			}
		}
		if (logger.isDebugEnabled() && (value != null)) {
			final StringBuffer debugMsg = new StringBuffer();
			debugMsg.append("\n############# Start XssFiltering ##############\n");
			debugMsg.append("Input Value :\n");
			debugMsg.append(value + "\n");
			debugMsg.append("----------------------------------------------\n");
			debugMsg.append("Output Value :\n");
			debugMsg.append(clearedValue + "\n");
			debugMsg.append("############# End XssFiltering ##############\n");
			logger.debug(debugMsg.toString());
		}
		return clearedValue;
	}
}