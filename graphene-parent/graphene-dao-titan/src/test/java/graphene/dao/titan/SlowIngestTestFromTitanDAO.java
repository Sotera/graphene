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
			G_User x = dao.createOrUpdate(u);
		}
		Random generator = new Random();

		for (int j = 0; j < numberOfRandomUpdates; j++) {
			int k = generator.nextInt(numberOfNodes - 1);
			logger.debug("Modifying user " + k);
			G_User y = dao.getByUsername("complexChange02-" + k);
		}

	}

}
