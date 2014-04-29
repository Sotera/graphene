package graphene.dao.neo4j;

import graphene.model.idl.G_RelationshipType;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class Neo4JServiceTest {
	private Neo4JEmbeddedService dataService;
	private Neo4JEmbeddedService userService;

	@BeforeSuite
	public void setup() {
		Registry registry;
		RegistryBuilder builder = new RegistryBuilder();
		builder.add(DAONeo4JTestModule.class);
		registry = builder.build();
		registry.performRegistryStartup();
		dataService = registry.getService("TestDataGraph", Neo4JEmbeddedService.class);
		userService = registry.getService("TestUserGraph", Neo4JEmbeddedService.class);

	}

	@Test
	public void test01() {
		AssertJUnit.assertTrue(dataService.connectToGraph());
		populateSample();
		printEverything();
		dataService.removeAll();
		dataService.shutDown();

	}

	public void printEverything() {
		System.out.println("---Relationships---");
		for (Relationship r : dataService.getGgo().getAllRelationships()) {
			for (String k : r.getPropertyKeys()) {
				System.out.println("Key " + k + " value:" + r.getProperty(k));

			}
		}
		System.out.println("---Nodes---");
		for (Node n : dataService.getGgo().getAllNodes()) {
			for (String k : n.getPropertyKeys()) {
				System.out.println("Key " + k + " value:" + n.getProperty(k));

			}
		}
	}

	/**
	 * Test to see if we can run the test twice.
	 */
	@Test(dependsOnMethods = { "test01" })
	public void test02() {
		AssertJUnit.assertTrue(dataService.connectToGraph());
		populateSample();
		printEverything();
		dataService.removeAll();
		dataService.shutDown();
	}

	public void make_mutual(Node n1, Node n2, G_RelationshipType type) {
		Relationship r1 = n1.createRelationshipTo(n2, type);
		//FIXME: This may be unnecessary
		r1.setProperty("Direction", "forward");
		Relationship r2 = n2.createRelationshipTo(n1, type);
		//FIXME: This may be unnecessary
		r2.setProperty("Direction", "reverse");
	}

	public void make_one_way(Node n1, Node n2, G_RelationshipType type) {
		Relationship r = n1.createRelationshipTo(n2, type);
		r.setProperty("Reason", "Just Because");
	}

	private Node create_person(String name) {
		Node n = dataService.getGraphDb().createNode();
		n.setProperty("name", name);
		return n;
	}

	public void populateSample() {

		// START SNIPPET: transaction
		Transaction tx = dataService.getGraphDb().beginTx();

		// Updating operations go here
		// END SNIPPET: transaction
		// START SNIPPET: addData
		Node johnathan = create_person("Johnathan");
		Node mark = create_person("Mark");
		Node phil = create_person("Phil");
		Node mary = create_person("Mary");
		Node luke = create_person("Luke");
		make_mutual(johnathan, mark, G_RelationshipType.FRIENDS);
		make_mutual(mark, mary, G_RelationshipType.FRIENDS);
		make_mutual(mark, phil, G_RelationshipType.FRIENDS);
		make_mutual(phil, mary, G_RelationshipType.MARRIED);
		make_mutual(phil, luke, G_RelationshipType.ENEMIES);
		make_one_way(mary, mark, G_RelationshipType.LOVES);
		// END SNIPPET: addData

		// // // START SNIPPET: readData
		// System.out.println(johnathan.getProperty("message"));
		//
		// System.out.println(mark.getProperty("message"));

		// START SNIPPET: transaction
		tx.success();

		// END SNIPPET: transaction
	}
}
