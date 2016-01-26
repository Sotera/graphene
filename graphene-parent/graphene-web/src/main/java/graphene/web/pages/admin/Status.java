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

package graphene.web.pages.admin;

import graphene.model.idl.G_VisualType;
import graphene.web.annotations.PluginPage;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

@PluginPage(visualType = G_VisualType.HIDDEN, menuName = "System Status", icon = "fa fa-lg fa-fw fa-question")
public class Status {

	@Inject
	@Path("context:core/js/plugin/flot/jquery.flot.cust.js")
	@Property
	private Asset flot_cust;

	@Inject
	@Path("context:core/js/plugin/flot/jquery.flot.resize.js")
	@Property
	private Asset flot_resize;

	@Inject
	@Path("context:core/js/plugin/flot/jquery.flot.fillbetween.js")
	@Property
	private Asset flot_fillbetween;

	@Inject
	@Path("context:core/js/plugin/flot/jquery.flot.orderBar.js")
	@Property
	private Asset flot_orderBar;

	@Inject
	@Path("context:core/js/plugin/flot/jquery.flot.pie.js")
	@Property
	private Asset flot_pie;

	@Inject
	@Path("context:core/js/plugin/flot/jquery.flot.tooltip.js")
	@Property
	private Asset flot_tooltip;

}
