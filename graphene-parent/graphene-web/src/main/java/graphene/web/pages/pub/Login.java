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

package graphene.web.pages.pub;

import graphene.model.idl.G_SymbolConstants;
import graphene.web.annotations.AnonymousAccess;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;

/**
 * 
 * @author djue
 * 
 */
@AnonymousAccess
public class Login {

	@Inject
	private AlertManager alertManager;
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_VERSION)
	private String appVersion;

	@Inject
	private ComponentResources componentResources;

	@Property
	private String flashmessage;

	@Inject
	@Path("context:/core/img/logo_graphene_dark_wide.png")
	@Property
	private Asset imgLogo;

	@Inject
	private Logger logger;

	@Inject
	private RequestGlobals requestGlobals;

	@Inject
	private Response response;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.THEME_PATH)
	private String themePath;

	public String getFlashMessage() {
		return flashmessage;
	}

	public void setFlashMessage(String flashmessage) {
		this.flashmessage = flashmessage;
	}

}
