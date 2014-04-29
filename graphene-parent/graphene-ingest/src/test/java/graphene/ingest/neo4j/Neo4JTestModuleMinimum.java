package graphene.ingest.neo4j;

import graphene.dao.EntityGraphDAO;
import graphene.dao.neo4j.EntityGraphDAONeo4JEImpl;
import graphene.dao.neo4j.Neo4JEmbeddedService;
import graphene.dao.neo4j.annotations.DataGraph;
import graphene.dao.neo4j.annotations.UserGraph;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;

/**
 * A module for the Neo4j DAOs using QueryDSL classes and generated classes.
 * 
 * @author djue
 * 
 */
public class Neo4JTestModuleMinimum {
	public static void bind(ServiceBinder binder) {
		binder.bind(EntityGraphDAO.class, EntityGraphDAONeo4JEImpl.class);
		binder.bind(Neo4JEmbeddedService.class).withId("TestDataGraph")
				.withMarker(DataGraph.class).eagerLoad();
		binder.bind(Neo4JEmbeddedService.class).withId("TestUserGraph")
				.withMarker(UserGraph.class).eagerLoad();
	}

	/**
	 * This will supply the config file location for a service id X which
	 * matches contributeX(...)
	 * 
	 * @param configuration
	 */
	@Contribute(Neo4JEmbeddedService.class)
	@DataGraph
	public static void contributeTestDataGraph(
			MappedConfiguration<String, String> configuration) {
		configuration.add("propertiesFileLocation", "TestDataGraph.properties");
	}

	/**
	 * This will supply the config file location for a service id X which
	 * matches contributeX(...)
	 * 
	 * @param configuration
	 */
	@Contribute(Neo4JEmbeddedService.class)
	@UserGraph
	public static void contributeTestUserGraph(
			MappedConfiguration<String, String> configuration) {
		configuration.add("propertiesFileLocation", "TestUserGraph.properties");
	}
}
