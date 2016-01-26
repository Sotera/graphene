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

public class IPAddressExtractor extends AbstractExtractor {
	private final static String RE = "(?<First>2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.(?<Second>2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.(?<Third>2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.(?<Fourth>2[0-4]\\d|25[0-5]|[01]?\\d\\d?)";

	public IPAddressExtractor() {
		System.out.println(this.getClass().getCanonicalName() + " is Creating pattern " + RE);
		p = Pattern.compile(RE);
	}

	@Override
	public String getIdType() {
		return "Potential URL";
	}

	@Override
	public String getNodetype() {
		return "Extracted" + G_CanonicalPropertyType.URL.name();
	}

	@Override
	public String getRelationType() {
		return "Website";
	}

	@Override
	public String getRelationValue() {
		return "Potential Website";
	}
}
