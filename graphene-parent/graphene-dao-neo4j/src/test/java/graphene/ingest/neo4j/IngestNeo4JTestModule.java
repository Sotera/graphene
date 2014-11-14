package graphene.ingest.neo4j;

import graphene.dao.EntityGraphDAO;
import graphene.dao.GroupDAO;
import graphene.dao.UserDAO;
import graphene.dao.WorkspaceDAO;
import graphene.dao.neo4j.EntityGraphDAONeo4JEImpl;
import graphene.dao.neo4j.GroupDAONeo4JEImpl;
import graphene.dao.neo4j.Neo4JEmbeddedService;
import graphene.dao.neo4j.UserDAONeo4JEImpl;
import graphene.dao.neo4j.WorkspaceDAONeo4JEImpl;
import graphene.dao.neo4j.advice.Neo4JTransactionalAdvisor;
import graphene.dao.neo4j.advice.Neo4JTransactionalAdvisorImpl;
import graphene.dao.neo4j.annotations.DataGraph;
import graphene.dao.neo4j.annotations.UserGraph;
import graphene.model.idl.G_EdgeTypeAccess;
import graphene.model.idl.G_NodeTypeAccess;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.services.G_EdgeTypeAccessImpl;
import graphene.services.G_NodeTypeAccessImpl;
import graphene.services.G_PropertyKeyTypeAccessImpl;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.MethodAdviceReceiver;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
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
public class IngestNeo4JTestModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(EntityGraphDAO.class, EntityGraphDAONeo4JEImpl.class);
		binder.bind(Neo4JEmbeddedService.class).withId("TestDataGraph")
				.withMarker(DataGraph.class);
		binder.bind(Neo4JEmbeddedService.class).withId("TestUserGraph")
				.withMarker(UserGraph.class);
		binder.bind(GroupDAO.class, GroupDAONeo4JEImpl.class).eagerLoad();
		binder.bind(WorkspaceDAO.class, WorkspaceDAONeo4JEImpl.class)
				.eagerLoad();
		binder.bind(UserDAO.class, UserDAONeo4JEImpl.class).eagerLoad();
		binder.bind(G_NodeTypeAccess.class, G_NodeTypeAccessImpl.class);
		binder.bind(G_EdgeTypeAccess.class, G_EdgeTypeAccessImpl.class);
		binder.bind(G_PropertyKeyTypeAccess.class,
				G_PropertyKeyTypeAccessImpl.class);
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

	//
	// //This is the inline way of doing it. It is preferable to put it in it's
	// own class.
	// @Advise(serviceInterface = AdviseMeService.class)
	// public static void adviseAdviseMeService(
	// final MethodAdviceReceiver receiver, @UserGraph final Neo4JService s) {
	// MethodAdvice advice = new MethodAdvice() {
	// public void advise(MethodInvocation invocation) {
	// System.out
	// .println("===========================About to start a tx");
	// try (Transaction tx = s.getGraphDb().beginTx()) {
	// System.out.println("===========================In a tx");
	// invocation.proceed();
	// tx.success();
	// System.out
	// .println("===========================performed method, still In a tx");
	// } catch (Exception e) {
	// System.out.println("===========================tx failed:"
	// + e.getMessage());
	// throw new RuntimeException("Failed transaction");
	// }
	// }
	// };
	// receiver.adviseAllMethods(advice);
	// }

	public static Logger buildLogger() {
		return LoggerFactory.getLogger(IngestNeo4JTestModule.class);
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
