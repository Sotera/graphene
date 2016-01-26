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

import java.util.regex.Pattern;

public class EmailToName extends AbstractExtractor {

	private final static String RE = "([\\w\\-]([\\.\\w])+[\\w]+(@|\\s+at\\s+|\\s*[\\[\\(]\\s*at\\s*[\\)\\]]\\s*)([\\w\\-]+(\\.|\\s*[\\[\\(]\\s*(\\.|dot)\\s*[\\)\\]]\\s*))+[A-Za-z]{2,4})";

	public EmailToName() {
		System.out.println(this.getClass().getCanonicalName() + " is Creating pattern " + RE);
		p = Pattern.compile(RE);
	}

	@Override
	public String getIdType() {
		return "Potential Alias";
	}

	@Override
	public String getNodetype() {
		return "Extracted" + G_CanonicalPropertyType.USERNAME.name();
	}

	@Override
	public String getRelationType() {
		return "Potential Alias";
	}

	@Override
	public String getRelationValue() {
		return "Potential Alias from Email Address";
	}

	private String nameFix(final String name) {
		if ((name == null) || (name.length() == 0)) {
			return name;
		} else {
			return ("" + name.charAt(0)).toUpperCase() + name.substring(1);
		}
	}

	@Override
	public String postProcessMatch(String match) {
		match = match.replaceAll("\\s+at\\s+|\\s*[\\[\\(]\\s*at\\s*[\\)\\]]\\s*", "@").replaceAll(
				"\\s*[\\[\\(]\\s*(\\.|dot)\\s*[\\)\\]]\\s*", ".");
		final int atpos = match.indexOf("@");
		if (atpos == -1) {
			return null;
		}
		String name = match.substring(0, atpos);

		final int dotpos = name.indexOf(".");
		if (dotpos != -1) {
			try {
				final String first = name.substring(0, dotpos);
				final String last = name.substring(dotpos + 1);
				name = nameFix(first) + " " + nameFix(last);
				name = name.trim();
			} catch (final Exception e) {
				System.out.println("Error fixing name " + name);
				return null;
			}
		}
		return name;
	}
}
