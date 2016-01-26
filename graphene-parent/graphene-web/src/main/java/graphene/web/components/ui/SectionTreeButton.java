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

package graphene.web.components.ui;

import graphene.dao.StyleService;
import graphene.util.FastNumberUtils;
import graphene.util.validator.ValidationUtils;

import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class SectionTreeButton {
	@Inject
	protected StyleService style;

	@Property
	@Parameter(required = true, autoconnect = true)
	private String typeName;

	@Property
	@Parameter(autoconnect = true)
	private String typeCount;
	@Property
	@Parameter(required = false, autoconnect = true)
	private String color;

	@Inject
	private Logger logger;

	public String getCardinality() {
		if (ValidationUtils.isValid(typeCount)) {
			final int c = FastNumberUtils.parseIntWithCheck(typeCount);
			if (c > 1) {
				return typeCount;
			} else {
				return null;
			}
		} else {
			return typeCount;
		}
	}

	public String getStyle() {
		return style.getStyle(color, false);
	}
}
