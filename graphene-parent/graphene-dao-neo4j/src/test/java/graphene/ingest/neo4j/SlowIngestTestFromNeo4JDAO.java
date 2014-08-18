package graphene.ingest.neo4j;

import graphene.dao.UserDAO;
import graphene.dao.neo4j.Neo4JEmbeddedService;
import graphene.dao.neo4j.annotations.UserGraph;
import graphene.model.idl.G_User;

import java.util.Random;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.AssertJUnit;
import org.testng.annotations.BeforeSuite;
import org.testng.annotations.Test;

public class SlowIngestTestFromNeo4JDAO {

	private UserDAO dao;
	private Logger logger = LoggerFactory
			.getLogger(SlowIngestTestFromNeo4JDAO.class);
	int numberOfNodes = 100;

	int numberOfRandomUpdates = 500;

	private Neo4JEmbeddedService service;

	@Test
	public void testCount() {
		logger.debug("=========================testCount");
		try (Transaction tx = service.getGraphDb().beginTx()) {
			G_User u = new G_User();
			u.setAvatar("bugatti.png");
			u.setUsername("djue");
			u=dao.createOrUpdate(u);
			dao.updatePassword(u.getUsername(), "password");
			u.setAvatar("venom.png");
			u.setUsername("wjue");
			dao.createOrUpdate(u);

			AssertJUnit.assertEquals(1, dao.countUsers("djue"));
			AssertJUnit.assertEquals(2, dao.countUsers("jue"));
			AssertJUnit.assertEquals(2, dao.countUsers("ue"));

			AssertJUnit.assertEquals(1, dao.countUsers("w"));

			logger.debug(dao.getByPartialUsername("y").toString());
			AssertJUnit.assertEquals(0, dao.countUsers("y"));
			tx.success();
		}
	}

	@Test
	public void f() {
		logger.debug("=========================f");
		try (Transaction tx = service.getGraphDb().beginTx()) {
			G_User u = new G_User();
			u.setAvatar("bugatti.png");
			u.setUsername("djue");
			G_User x = dao.createOrUpdate(u);
			dao.updatePassword(u.getUsername(), "password");
			// logger.debug("Explicit tx from a separate injection");

			logger.info(x.toString());
			tx.success();
		}
	}

	@BeforeSuite
	public void setup() {
		System.out.println("Slow Ingest Test");
		RegistryBuilder builder = new RegistryBuilder();
		builder.add(IngestNeo4JTestModule.class);
		Registry registry = builder.build();
		registry.performRegistryStartup();
		dao = registry.getService(UserDAO.class);
		service = registry.getService(Neo4JEmbeddedService.class,
				UserGraph.class);
	}

	@Test
	public void testAdvisedTx() {
		logger.debug("=========================testAdvisedTx");
		try (Transaction tx = service.getGraphDb().beginTx()) {
			G_User u = new G_User();
			u.setAvatar("bugatti.png");
			u.setUsername("sampleUser2");
			G_User x = dao.createOrUpdate(u);
			dao.updatePassword(u.getUsername(), "password");
			logger.info("About to print");

			for (int i = 1; i < numberOfRandomUpdates; i++) {
				logger.debug("Round " + i + " Modifying...");
			}
			tx.success();
		}
	}

	@Test
	public void testComplexChange01() {
		logger.debug("=========================testComplexChange01");
		try (Transaction tx = service.getGraphDb().beginTx()) {
			G_User u = new G_User();
			u.setAvatar("bugatti.png");
			
			for (int i = 0; i < 10; i++) {
				logger.debug("Adding user " + i);
				u.setUsername("complexChange01-" + i);
				G_User x = dao.createOrUpdate(u);
				if (i > 1) {
					int previousUser = (i - 1);
					logger.debug("Getting previous user " + previousUser);
					G_User y = dao.getByUsername("complexChange01-"
							+ previousUser);
					if (y == null) {
						logger.error("complexChange01-" + previousUser
								+ " was null");
					} else {
						logger.debug("Modifying previous user " + previousUser);

					}
				}
			}
			tx.success();
		}
	}

	@Test
	public void testComplexChange02() {
		logger.debug("=========================testComplexChange02");
		try (Transaction tx = service.getGraphDb().beginTx()) {
			G_User u = new G_User();
			u.setAvatar("bugatti.png");

			for (int i = 0; i < numberOfNodes; i++) {
				logger.debug("Adding user " + i);
				u.setUsername("complexChange02-" + i);
				G_User x = dao.createOrUpdate(u);
			}
			Random generator = new Random();

			for (int j = 0; j < numberOfRandomUpdates; j++) {
				int k = generator.nextInt(numberOfNodes - 1);
				logger.debug("Modifying user " + k);
				G_User y = dao.getByUsername("complexChange02-" + k);
			}
			tx.success();
		}
	}

	@Test
	public void testComplexChange02aams() {
		logger.debug("=========================testComplexChange02aams");
		try (Transaction tx = service.getGraphDb().beginTx()) {
			G_User u = new G_User();
			u.setAvatar("mercedes.png");

			for (int i = 0; i < numberOfNodes; i++) {
				logger.debug("Adding user " + i);
				u.setUsername("complexChange02-" + i);
				G_User x = dao.createOrUpdate(u);
			}
			Random generator = new Random();

			for (int j = 0; j < numberOfRandomUpdates; j++) {
				int k = generator.nextInt(numberOfNodes - 1);
				logger.debug("Modifying user " + k);
				G_User y = dao.getByUsername("complexChange02-" + k);
			}
			tx.success();
		}
	}

}
