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

import java.io.IOException;

import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author Martin Papy
 * 
 */
public class XSSRequestFilterImpl implements RequestFilter {
	private static final Logger logger = LoggerFactory.getLogger(XSSRequestFilterImpl.class);

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.apache.tapestry5.services.RequestFilter#service(org.apache.tapestry5
	 * .services.Request, org.apache.tapestry5.services.Response,
	 * org.apache.tapestry5.services.RequestHandler)
	 */
	@Override
	public boolean service(final Request request, final Response response, final RequestHandler handler)
			throws IOException {
		if (logger.isDebugEnabled()) {
			// logger.debug("Wrapping Tapestry Request in XSSRequestWrapper");
		}
		return handler.service(new XSSRequestWrapper(request), response);
	}

}