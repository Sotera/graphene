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
import graphene.model.idl.G_CanonicalPropertyType;

import org.apache.tapestry5.ioc.annotations.Inject;

public class SearchLegend {
	@Inject
	private StyleService style;

	public String getStyleFor(final String nodeType, final String searchValue) {
		return style.getStyle(nodeType, false);
	}

	public String getStyleForAddress() {
		return style.getStyle(G_CanonicalPropertyType.ADDRESS.name(), false);
	}

	public String getStyleForEmail() {
		return style.getStyle(G_CanonicalPropertyType.EMAIL_ADDRESS.name(), false);
	}

	public String getStyleForGovId() {
		return style.getStyle(G_CanonicalPropertyType.GOVERNMENTID.name(), false);
	}

	public String getStyleForHighlight() {
		return style.getHighlightStyle();
	}

	public String getStyleForName() {
		return style.getStyle(G_CanonicalPropertyType.NAME.name(), false);
	}

	public String getStyleForOccupation() {
		return style.getStyle(G_CanonicalPropertyType.OCCUPATION.name(), false);
	}

	public String getStyleForOther() {
		return style.getStyle(G_CanonicalPropertyType.OTHER.name(), false);
	}

	public String getStyleForPhone() {
		return style.getStyle(G_CanonicalPropertyType.PHONE.name(), false);
	}

	public String getStyleForVisa() {
		return style.getStyle(G_CanonicalPropertyType.OTHER.name(), false);
	}
}
