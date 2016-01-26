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

package graphene.dao.titan;

import graphene.dao.UserDAO;
import graphene.model.idl.G_User;

import java.util.Random;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.Test;

import com.thinkaurelius.titan.core.TitanGraph;

public class SlowIngestTestFromTitanDAO {

	private UserDAO dao;
	private Logger logger = LoggerFactory
			.getLogger(SlowIngestTestFromTitanDAO.class);
	int numberOfNodes = 100;

	int numberOfRandomUpdates = 500;

	private TitanGraph service;

	@Test
	public void testComplexChange02() {
		logger.debug("=========================testComplexChange02");

		G_User u = new G_User();
		u.setAvatar("bugatti.png");

		for (int i = 0; i < numberOfNodes; i++) {
			logger.debug("Adding user " + i);
			u.setUsername("complexChange02-" + i);
			G_User x = dao.save(u);
		}
		Random generator = new Random();

		for (int j = 0; j < numberOfRandomUpdates; j++) {
			int k = generator.nextInt(numberOfNodes - 1);
			logger.debug("Modifying user " + k);
			G_User y = dao.getByUsername("complexChange02-" + k);
		}

	}

}
