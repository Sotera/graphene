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

/**
 * 
 */
package graphene.dao.titan;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.tinkerpop.rexster.client.RexsterClient;
import com.tinkerpop.rexster.client.RexsterClientFactory;

/**
 * @author djue
 * 
 */
public class RexterConnectionTest {

	public static  Logger logger = LoggerFactory
			.getLogger(RexterConnectionTest.class);

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) {
		RexsterClient client;
		try {
			client = RexsterClientFactory.open("localhost", "graph");

			List<Map<String, Object>> result;
			result = client.execute("g.V('name','saturn').in('father').map");
			logger.debug(result.toString());
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
	}

}
