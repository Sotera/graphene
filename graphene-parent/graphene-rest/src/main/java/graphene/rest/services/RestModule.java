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

package graphene.rest.services;

import graphene.dao.DAOModule;
import graphene.rest.ws.MetaSearchRS;
import graphene.rest.ws.impl.MetaSearchRSImpl;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.SubModule;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 */
@SubModule({ DAOModule.class })
public class RestModule {
	public static void bind(final ServiceBinder binder) {
		binder.bind(MetaSearchRS.class, MetaSearchRSImpl.class).eagerLoad();
	}

	@Contribute(javax.ws.rs.core.Application.class)
	public static void contributeApplication(final Configuration<Object> singletons, final MetaSearchRS restService) {
		singletons.add(restService);
	}
}
