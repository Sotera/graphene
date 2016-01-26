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

package graphene.hts.sentences;

import graphene.hts.entityextraction.AbstractExtractor;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.util.StringUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

/**
 * 
 * @author djue
 * 
 */
public class SentenceExtractorImpl extends AbstractExtractor {

	private final static String RE_1 = "([\\w\\d]{3,})";

	public SentenceExtractorImpl() {
		p = Pattern.compile(RE_1);
	}

	@Override
	public Collection<String> extract(String source) {
		final List<String> matchList = new ArrayList<String>();
		source = StringUtils.cleanUpAllCaps(source, 0.2d);
		matchList.addAll(StringUtils.convertToSentences(source));
		return matchList;
	}

	@Override
	public String getIdType() {
		return "Sentence";
	}

	@Override
	public String getNodetype() {
		return "Extracted Sentence";
	}

	@Override
	public String getRelationType() {
		return G_CanonicalRelationshipType.IN_DOCUMENT.name();
	}

	@Override
	public String getRelationValue() {
		return "Contains Sentence";
	}

}
