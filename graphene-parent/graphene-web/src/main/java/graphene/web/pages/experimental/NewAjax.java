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

package graphene.web.pages.experimental;

import graphene.model.idl.G_VisualType;
import graphene.web.annotations.PluginPage;

import java.util.Date;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.OnEvent;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@Import(library = "context:core/js/testJSON.js")
@PluginPage(visualType = G_VisualType.EXPERIMENTAL, menuName = "Ajax test", icon = "fa fa-lg fa-fw fa-cogs")
public class NewAjax {
	@Inject
	private ComponentResources resources;

	@Inject
	private Request request;

	@Inject
	private JavaScriptSupport javaScriptSupport;

	public String getEventLink() {
		return resources.createEventLink("MyCustomEventName").toURI();
	}

	void afterRender() {
		javaScriptSupport.addScript("setupEvent('%s', '%s');", "linkId",
				getEventLink());
	}

	@OnEvent(value = "MyCustomEventName")
	JSONArray myEventHandler() {
		// check if this is a AJAX request
		if (request.isXHR()) {
			String queryParameter1 = request.getParameter("queryParameter1");
			String queryParameter2 = request.getParameter("queryParameter2");
			JSONArray object = new JSONArray();
			object.put(1, "\n" + queryParameter1.toUpperCase());
			object.put(2, "\n" + queryParameter2.toUpperCase());
			object.put(3, getMessage());
			// Make your real payload
			return object;
		}
		return null;
	}

	private String getMessage() {
		return "SUCCESS at " + (new Date()).toString();
	}
}
