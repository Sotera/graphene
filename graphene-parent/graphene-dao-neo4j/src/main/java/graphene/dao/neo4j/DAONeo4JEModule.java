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
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Marker;
import org.apache.tapestry5.ioc.annotations.Match;

/**
 * A module for the Neo4j Embedded DAOs
 * 
 * @author djue
 * 
 */
public class DAONeo4JEModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(EntityGraphDAO.class, EntityGraphDAONeo4JEImpl.class);
		binder.bind(Neo4JEmbeddedService.class).withId("UnifiedEntity")
				.withMarker(DataGraph.class).scope(ScopeConstants.DEFAULT);
		binder.bind(Neo4JEmbeddedService.class).withId("UserGraph")
				.withMarker(UserGraph.class).scope(ScopeConstants.DEFAULT);
		binder.bind(GroupDAO.class, GroupDAONeo4JEImpl.class);
		binder.bind(WorkspaceDAO.class, WorkspaceDAONeo4JEImpl.class);
		binder.bind(UserDAO.class, UserDAONeo4JEImpl.class).eagerLoad();
		
	}

	
	
	/**
	 * Builds the advisor for the UserGraph database. Note that since we can
	 * have multiple Neo4J instances running, we have to build the advise for
	 * each one. In this case it is for the UserGraph. This is because it is
	 * creating a transaction around methods that read from the database, and it
	 * has to create a transaction from the same database the object is from.
	 */
	@Marker(UserGraph.class)
	public static Neo4JTransactionalAdvisor buildTxAdviceForUserGraph(
			@UserGraph Neo4JEmbeddedService s) {
		return new Neo4JTransactionalAdvisorImpl(s);
	}

	/**
	 * Applies the UserGraph advisor to classes that match the @Match
	 * annotation. Additionally the methods need to be marked.
	 * 
	 * @param advisor
	 * @param receiver
	 */
	@Match("Annotated*")
	// Matches any class that starts with Annotated
	public static void adviseTransactionally(
			@UserGraph Neo4JTransactionalAdvisor advisor,
			MethodAdviceReceiver receiver) {
		advisor.addTransactionAdvice(receiver);
	}

	/**
	 * This will supply the config file location for a service id X which
	 * matches contributeX(...)
	 * 
	 * @param configuration
	 */
	@Contribute(Neo4JEmbeddedService.class)
	@DataGraph
	public static void contributeDataGraph(
			MappedConfiguration<String, String> configuration) {
		configuration.add("propertiesFileLocation", "DataGraph.properties");
	}

	/**
	 * This will supply the config file location for a service id X which
	 * matches contributeX(...)
	 * 
	 * @param configuration
	 */
	@Contribute(Neo4JEmbeddedService.class)
	@UserGraph
	public static void contributeUserGraph(
			MappedConfiguration<String, String> configuration) {
		configuration.add("propertiesFileLocation", "UserGraph.properties");
	}
}
