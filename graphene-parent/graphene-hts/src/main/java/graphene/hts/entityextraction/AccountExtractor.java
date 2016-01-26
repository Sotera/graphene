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

import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_EntityTag;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class AccountExtractor extends AbstractExtractor {

	// private final static String RE = "([a-z]{0,2}\\d{12,})";
	/**
	 * Matches twice, so it's not as efficient, but prevents report ids from
	 * becoming accounts
	 */
	private final static String RE = "(([a-zA-Z]{0,3}\\d{12,}(?<!(31000|30000|DCN:\\s|BSA:\\s)\\d*))|((?<=(account|Account\\sNumber)\\s{0,3}\\#?\\s{0,3})[a-zA-Z]{0,3}\\d{5,}))";

	public AccountExtractor() {
		System.out.println(this.getClass().getCanonicalName() + " is Creating pattern " + RE);
		p = Pattern.compile(RE);
	}

	@Override
	public List<G_EntityTag> getEntityTags() {
		final List<G_EntityTag> tags = new ArrayList<G_EntityTag>();
		tags.add(G_EntityTag.ACCOUNT);
		return tags;
	}

	@Override
	public String getIdType() {
		return "Potential Account";
	}

	@Override
	public String getNodetype() {
		return "Extracted" + G_CanonicalPropertyType.ACCOUNT.name();
	}

	@Override
	public String getRelationType() {
		return G_CanonicalRelationshipType.COMMERCIAL_ID_OF.name();
	}

	@Override
	public String getRelationValue() {
		return "Potential Account";
	}

}
