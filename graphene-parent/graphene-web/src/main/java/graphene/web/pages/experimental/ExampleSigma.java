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

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

@Import(stylesheet = { "context:core/js/neo_sigma/css/searchable_vis.css",
		"context:core/js/neo_sigma/css/jquery.fancybox.css" })
@PluginPage(visualType = G_VisualType.EXPERIMENTAL, menuName = "SigmaJs Test", icon = "fa fa-lg fa-fw fa-code-fork")
public class ExampleSigma {
	@Inject
	@Path("context:core/js/neo_sigma/js/searchable_vis.js")
	@Property
	private Asset jsApp1;

	@Inject
	@Path("context:core/js/neo_sigma/js/sigma.min.js")
	@Property
	private Asset jsApp2;
	@Inject
	@Path("context:core/js/neo_sigma/js/sigma.forceatlas2.js")
	@Property
	private Asset jsApp3;
	@Inject
	@Path("context:core/js/neo_sigma/js/sigma.parseGexf.js")
	@Property
	private Asset jsApp4;
	@Inject
	@Path("context:core/js/neo_sigma/js/sigma.parseJson.js")
	@Property
	private Asset jsApp5;
	@Inject
	@Path("context:core/js/neo_sigma/js/jquery.fancybox.pack.js")
	@Property
	private Asset jsApp6;
	
	@Inject
	@Path("context:/core/js/libs/jquery-2.0.2.min.js")
	@Property
	private Asset jsjqLib1;
	
	@Inject
	@Path("context:/core/js/libs/jquery-ui-1.10.3.min.js")
	@Property
	private Asset jsjqLib2;
}
