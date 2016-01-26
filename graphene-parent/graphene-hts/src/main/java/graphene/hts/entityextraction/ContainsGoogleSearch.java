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

package graphene.hts.entityextraction;

import graphene.model.idl.G_CanonicalRelationshipType;

import java.util.regex.Pattern;

public class ContainsGoogleSearch extends AbstractExtractor {
	private final static String RE_PATTERN = "Google Search";

	public ContainsGoogleSearch() {
		p = Pattern.compile(RE_PATTERN);
	}

	@Override
	public String getIdType() {
		return "Contains Phrase";
	}

	@Override
	public String getNodetype() {
		return "Extracted Phrase";
	}

	@Override
	public String getRelationType() {
		return G_CanonicalRelationshipType.CONTAINED_IN.name();
	}

	@Override
	public String getRelationValue() {
		return "Contains Phrase";
	}

	@Override
	public String postProcessMatch(final String match) {
		return match;
	}
}
