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
