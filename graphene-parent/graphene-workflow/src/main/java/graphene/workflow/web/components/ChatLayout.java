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

package graphene.workflow.web.components;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.alerts.AlertStorage;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.AssetSource;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

@Import(stylesheet = "context:bootstrap/css/bootstrap.css")
public class ChatLayout {
	private static final MenuGroup[] MENU_GROUPS = { new MenuGroup("Examples",
			new MenuItem("Chat", "ChatDemo")) };

	@SessionState(create = false)
	private AlertStorage alertsStorage;

	@Inject
	private ComponentResources resources;

	@Inject
	private AssetSource assetSource;

	@Inject
	private JavaScriptSupport jss;

	@Property
	private MenuGroup menuGroup;

	@Property
	private MenuItem menuItem;

	public boolean isAlerts() {
		return (alertsStorage != null && !alertsStorage.getAlerts().isEmpty());
	}

	public String getPageClass() {
		return resources.getPageName().equalsIgnoreCase(menuItem.page) ? "active"
				: null;
	}

	public MenuGroup[] getMenuGroups() {
		return MENU_GROUPS;
	}

	public static class MenuGroup {
		public String label;
		public MenuItem[] items;

		MenuGroup(String label, MenuItem... items) {
			this.label = label;
			this.items = items;
		}
	}

	public static class MenuItem {
		public String label;
		public String page;

		MenuItem(String label, String page) {
			super();
			this.label = label;
			this.page = page;
		}
	}
}
