package graphene.dao.neo4j.funnel;

import graphene.model.Funnel;
import graphene.model.idl.G_User;

import java.sql.Timestamp;

import org.neo4j.graphdb.Node;

public class UserFunnel implements Funnel<Node, G_User> {

	@Override
	public G_User from(Node f) {
		G_User c = new G_User();
		// c.setActive(f.getActive());
		// c.setAvatar(f.getAvatar());
		// c.setCreated(f.getCreated().getTime());
		// c.setEmail(f.getEmail());
		// c.setFullname(f.getFullname());
		// c.setHashedpassword(f.getHashedpassword());
		// c.setId(f.getId());
		// c.setModified(f.getModified().getTime());
		// c.setLastlogin(f.getLastlogin().getTime());
		// c.setNumberlogins(f.getNumberlogins());
		// c.setUsername(f.getUsername());
		return c;
	}

	@Override
	public Node to(G_User f) {
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