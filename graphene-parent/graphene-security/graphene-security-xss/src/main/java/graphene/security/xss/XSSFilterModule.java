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

package graphene.security.xss;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;

/**
 * 
 * @author Martin Papy
 * 
 */
public class XSSFilterModule {
	public static void bind(final ServiceBinder binder) {
		binder.bind(RequestFilter.class, XSSRequestFilterImpl.class).withId("XSSRequestFilter");
	}

	/*
	 * XSS Filtering
	 */
	@Contribute(RequestHandler.class)
	public static void requestHandler(final OrderedConfiguration<RequestFilter> configuration,
			@Local final RequestFilter xssFilter) {
		configuration.add("XSSRequestFilter", xssFilter, "after:StaticFiles", "before:StoreIntoGlobals");
	}
}