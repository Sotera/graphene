package graphene.dao.neo4j.funnel;

import graphene.dao.neo4j.Neo4JEmbeddedService;
import graphene.dao.neo4j.annotations.UserGraph;
import graphene.model.funnels.Funnel;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserFields;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.joda.time.DateTime;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

public class UserFunnel implements Funnel<Node, G_User> {
	private final Neo4JEmbeddedService n4jService;

	@Inject
	public UserFunnel(@UserGraph final Neo4JEmbeddedService n4jService2) {
		n4jService = n4jService2;
	}

	@Override
	public G_User from(final Node f) {
		G_User d = null;
		if (f != null) {
			try (Transaction tx = n4jService.beginTx()) {
				d = new G_User();
				d.setCreated(new DateTime(f.getProperty(G_UserFields.created.name(), 0l)).getMillis());
				d.setActive((boolean) f.getProperty(G_UserFields.active.name(), true));
				d.setAvatar((String) f.getProperty(G_UserFields.avatar.name(), "unknown.png"));
				d.setEmail((String) f.getProperty(G_UserFields.email.name(), "no email"));
				d.setFullname((String) f.getProperty(G_UserFields.fullname.name(), "no name"));

				d.setHashedpassword((String) f.getProperty(G_UserFields.hashedpassword.name(), "no hashed password"));

				d.setLastlogin(new DateTime(f.getProperty(G_UserFields.lastlogin.name(), 0l)).getMillis());

				d.setNumberlogins((int) f.getProperty(G_UserFields.numberlogins.name(), 0));
				d.setUsername((String) f.getProperty(G_UserFields.username.name(), "username"));
				tx.success();
			}

		}
		return d;
	}

	@Override
	public Node to(final G_User f) {
		// Node d = new Node();
		// d.setActive(f.getActive());
		// d.setAvatar(f.getAvatar());
		// d.setCreated(new Timestamp(f.getCreated()));
		// d.setEmail(f.getEmail());
		// d.setFullname(f.getFullname());
		// d.setHashedpassword(f.getHashedpassword());
		// d.setLastlogin(new Timestamp(f.getLastlogin()));
		// d.setModified(new Timestamp(f.getModified()));
		// d.setNumberlogins(f.getNumberlogins());
		// d.setId(d.getId());
		// d.setUsername(f.getUsername());

		// return d;
		return null;
	}
}