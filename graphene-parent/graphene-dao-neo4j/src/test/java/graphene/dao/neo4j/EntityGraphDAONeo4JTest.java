package graphene.dao.neo4j;

import graphene.dao.EntityGraphDAO;
import graphene.model.query.EntityGraphQuery;

import java.util.List;

import mil.darpa.vande.legacy.InfoVisGraphAdjacency;

import org.apache.tapestry5.ioc.Registry;
import org.apache.tapestry5.ioc.RegistryBuilder;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class EntityGraphDAONeo4JTest {
	private EntityGraphDAO<InfoVisGraphAdjacency, EntityGraphQuery> dao;

	@Test
	public void f() throws Exception {
		EntityGraphQuery q = new EntityGraphQuery();
		q.setAttribute("Daniel");
		List<InfoVisGraphAdjacency> list;

		list = dao.findByQuery(q);

		for (InfoVisGraphAdjacency l : list) {

			System.out.println("\n\n" + l.toString());
		}

	}

	@BeforeClass
	public void beforeClass() {

		// TODO Auto-generated constructor stub
		System.out.println("EntityGraphDAONeo4JTest");
		Registry registry;
		RegistryBuilder builder = new RegistryBuilder();
		builder.add(DAONeo4JTestModule.class);
		registry = builder.build();
		registry.performRegistryStartup();
		dao = registry.getService(EntityGraphDAO.class);

	}

}
