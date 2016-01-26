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

#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.web.services;

import graphene.dao.HyperGraphBuilder;
import graphene.dao.es.ESRestAPIConnection;
import io.searchbox.client.JestClient;
import mil.darpa.vande.generic.V_GenericEdge;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GenericNode;

import org.apache.tapestry5.ioc.Registry;
import org.slf4j.Logger;
import org.testng.annotations.BeforeSuite;

public class ServiceTest {

	protected Registry registry;
	protected ESRestAPIConnection c;
	protected Logger logger;
	protected HyperGraphBuilder pgb;
	// protected InteractionGraphBuilder igb;
	protected JestClient client;

	// protected InteractionFinder interactionFinder;

	protected void printGraph(final V_GenericGraph g) {
		System.out.println("=====================");
		for (final V_GenericNode x : g.getNodes().values()) {
			System.out.println(x);
		}
		for (final V_GenericEdge x : g.getEdges().values()) {
			System.out.println(x);
		}
		System.out.println("=====================");
	}

	@BeforeSuite
	public void setup() {

		// final RegistryBuilder builder = new RegistryBuilder();
		// builder.add(TestModule.class);
		// builder.add(JestModule.class);
		// registry = builder.build();
		// registry.performRegistryStartup();
		//
		// logger = registry.getService(Logger.class);
		//
		// client = registry.getService(JestClient.class);
		// pgb = registry.getService("HyperProperty", HyperGraphBuilder.class);
		// c = registry.getService(ESRestAPIConnection.class);

	}
}
