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

package graphene.hts.keywords;

import graphene.hts.entityextraction.AbstractExtractor;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_EntityTag;

import java.util.List;
import java.util.regex.Pattern;

public class KeywordExtractorImpl extends AbstractExtractor {
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * graphene.hts.keywords.KeywordExtractor#getSimpleKeywords(java.lang.String
	 * )
	 */
	private final static String RE_1 = "([\\w\\d]{3,})";

	// @Override
	// public SortedSet<String> getSimpleKeywords(String narrative) {
	// TreeSet<String> keyWords = new TreeSet<String>(
	// Arrays.asList(((String) narrative).split("([\\w\\d]{3,})")));
	// for (Iterator<String> iter = keyWords.iterator(); iter.hasNext();) {
	// String k = iter.next();
	// if (k.length() < 2) {
	// iter.remove();
	// }
	// }
	// return keyWords;
	// }

	public KeywordExtractorImpl() {
		p = Pattern.compile(RE_1);
	}

	@Override
	public List<G_EntityTag> getEntityTags() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getIdType() {
		return "Keyword";
	}

	@Override
	public String getNodetype() {
		return "Extracted" + G_CanonicalPropertyType.ANY.name();
	}

	@Override
	public String getRelationType() {
		return G_CanonicalRelationshipType.IN_DOCUMENT.name();
	}

	@Override
	public String getRelationValue() {
		return "Keyword in document";
	}

}
