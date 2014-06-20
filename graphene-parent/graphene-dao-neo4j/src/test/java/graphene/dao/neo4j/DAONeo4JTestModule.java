package graphene.dao.neo4j;

import graphene.dao.EntityGraphDAO;
import graphene.dao.GroupDAO;
import graphene.dao.UserDAO;
import graphene.dao.WorkspaceDAO;
import graphene.dao.neo4j.advice.Neo4JTransactionalAdvisor;
import graphene.dao.neo4j.advice.Neo4JTransactionalAdvisorImpl;
import graphene.dao.neo4j.annotations.DataGraph;
import graphene.dao.neo4j.annotations.UserGraph;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Match;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A module for the Neo4j DAOs using QueryDSL classes and generated classes.
 * 
 * @author djue
 * 
 */
public class DAONeo4JTestModule {
	public static void bind(ServiceBinder binder) {
		//binder.bind(EntityGraphDAO.class, EntityGraphDAONeo4JEImpl.class);
		binder.bind(Neo4JEmbeddedService.class).withId("TestDataGraph")
				.withMarker(DataGraph.class);
		binder.bind(Neo4JEmbeddedService.class).withId("TestUserGraph")
				.withMarker(UserGraph.class);
		binder.bind(GroupDAO.class, GroupDAONeo4JEImpl.class).eagerLoad();
		binder.bind(WorkspaceDAO.class, WorkspaceDAONeo4JEImpl.class)
				.eagerLoad();
		binder.bind(UserDAO.class, UserDAONeo4JEImpl.class).eagerLoad();
	}

	@Marker(UserGraph.class)
	public static Neo4JTransactionalAdvisor buildTxAdviceForUserGraph(
			@UserGraph Neo4JEmbeddedService s) {
		return new Neo4JTransactionalAdvisorImpl(s);
	}

	// @Advise(serviceInterface = AnnotatedAdviseMeService.class)
	@Match("Annotated*")
	// Matches anything that starts with Annotated
	public static void adviseTransactionally(
			@UserGraph Neo4JTransactionalAdvisor advisor,
			MethodAdviceReceiver receiver) {
		advisor.addTransactionAdvice(receiver);
	}

	public static EntityGraphDAO buildEntityGraphDAO(
			@InjectService("TestDataGraph") Neo4JEmbeddedService service) {
		return new EntityGraphDAONeo4JEImpl(service);
	}

	public static Logger buildLogger() {
		return LoggerFactory.getLogger(DAONeo4JTestModule.class);
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
