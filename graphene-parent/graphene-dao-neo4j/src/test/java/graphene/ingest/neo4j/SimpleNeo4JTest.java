package graphene.ingest.neo4j;

import graphene.dao.UserDAO;
import graphene.model.idl.G_RelationshipType;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.factory.GraphDatabaseFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class SimpleNeo4JTest {

	private UserDAO dao;
	private Logger logger = LoggerFactory.getLogger(SimpleNeo4JTest.class);
	int numberOfNodes = 100;

	int numberOfRandomUpdates = 500;

	// private Neo4JService service;
	private GraphDatabaseFactory graphDatabaseFactory;
	private GraphDatabaseService graphDb;

	@BeforeSuite
	public void a() {
		System.out.println("Slow Ingest Test");
		RegistryBuilder builder = new RegistryBuilder();
		builder.add(Neo4JTestModuleMinimum.class);
		Registry registry = builder.build();
		registry.performRegistryStartup();
		// service = registry.getService(Neo4JService.class, UserGraph.class);
		// service.connectToGraph();

		if (graphDatabaseFactory == null) {
			graphDatabaseFactory = new GraphDatabaseFactory();
		}
		graphDb = graphDatabaseFactory
				.newEmbeddedDatabase("target/reallysimpletest");

	}

	@Test
	public void b() {
		logger.debug("=========================b");
		try (Transaction tx = graphDb.beginTx()) {
			Node firstNode = graphDb.createNode();
			firstNode.setProperty("message", "Hello, ");
			Node secondNode = graphDb.createNode();
			// secondNode.setProperty("message", "World!");
			Relationship relationship = firstNode.createRelationshipTo(
					secondNode, DynamicRelationshipType
							.withName(G_RelationshipType.KNOWS.name()));
			tx.success();
		}
	}

}
