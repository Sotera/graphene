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

package graphene.util;

import org.apache.tapestry5.ioc.ServiceBinder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UtilModule {
	public static final String PASSWORD_HASH_ENCODING = "graphene.password-hash-encoding";

	public static void bind(final ServiceBinder binder) {
		// Make bind() calls on the binder object to define most IoC services.
		// Use service builder methods (example below) when the implementation
		// is provided inline, or requires more initialization than simply
		// invoking the constructor.
		// binder.bind(JavaDiskCache.class);
		binder.bind(ColorUtil.class);
	}

	public static Logger buildLogger(final Class clazz) {
		return LoggerFactory.getLogger(clazz);
	}

}
