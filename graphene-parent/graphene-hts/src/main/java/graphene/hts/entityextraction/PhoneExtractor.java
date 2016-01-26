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

import java.util.regex.Pattern;

public class PhoneExtractor extends AbstractExtractor {
	/*
	 * http://stackoverflow.com/questions/123559/a-comprehensive-regex-for-phone-
	 * number-validation
	 * 
	 * Here's a regex for a 7 or 10 digit number, with extensions allowed,
	 * delimiters are spaces, dashes, or periods:
	 */
	private final static String RE_PHONE2 = "\\s((?<!(\\d|[,.]))(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:\\(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9])\\s*\\)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})(?:\\s*(?:#|x\\.?|ext\\.?|extension)\\s*(\\d+))?(?=\\D))";

	/*
	 * 
	 * here it is without the extension section
	 */
	private final static String RE_PHONE3 = "(?:(?:\\+?1\\s*(?:[.-]\\s*)?)?(?:(\\s*([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]‌​)\\s*)|([2-9]1[02-9]|[2-9][02-8]1|[2-9][02-8][02-9]))\\s*(?:[.-]\\s*)?)?([2-9]1[02-‌​9]|[2-9][02-9]1|[2-9][02-9]{2})\\s*(?:[.-]\\s*)?([0-9]{4})";

	/*
	 * http://stackoverflow.com/questions/2113908/what-regular-expression-will-match
	 * -valid-international-phone-numbers
	 */
	private final static String RE_PHONE4 = "((?<!(\\d|[,.]))(9[976]\\d|8[987530]\\d|6[987]\\d|5[90]\\d|42\\d|3[875]\\d|2[98654321]\\d|9[8543210]|8[6421]|6[6543210]|5[87654321]|4[987654310]|3[9643210]|2[70]|7|1)\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*\\d\\W*(\\d{1,2})(?=\\D))";

	/*
	 * http://stackoverflow.com/questions/123559/a-comprehensive-regex-for-phone-
	 * number-validation
	 */
	private final static String RE_PHONE5 = "(?:(?:\\(?(?:00|\\+)([1-4]\\d\\d|[1-9]\\d?)\\)?)?[\\-\\.\\ \\\\\\/]?)?((?:\\(?\\d{1,}\\)?[\\-\\.\\ \\\\\\/]?){0,})(?:[\\-\\.\\ \\\\\\/]?(?:#|ext\\.?|extension|x)[\\-\\.\\ \\\\\\/]?(\\d+))?";

	private final static String RE_PHONE6 = "((\\(?[1-9]\\d{2}\\)?[ -.])?\\D(([1-9]\\d{2}(?:-|\\s*)){1,2}[1-9]\\d{3}))\\b";

	public PhoneExtractor() {
		System.out.println(this.getClass().getCanonicalName() + " is Creating pattern " + RE_PHONE2);
		p = Pattern.compile(RE_PHONE2);
	}

	@Override
	public String getIdType() {
		return "Potential Phone Number";
	}

	@Override
	public String getNodetype() {
		return "Extracted" + G_CanonicalPropertyType.PHONE.name();
	}

	@Override
	public String getRelationType() {
		return G_CanonicalRelationshipType.COMMUNICATION_ID_OF.name();
	}

	@Override
	public String getRelationValue() {
		return "Potential Phone Number";
	}

	@Override
	public String postProcessMatch(final String match) {
		// TODO Auto-generated method stub
		return super.postProcessMatch(match.replaceAll("[\\n\\s ,\\(\\)-.]", ""));
	}
}
