package graphene.dao.neo4j.funnel;

import graphene.dao.neo4j.GrapheneNeo4JConstants;
import graphene.dao.neo4j.Neo4JEmbeddedService;
import graphene.model.Funnel;
import graphene.model.idl.G_Group;
import graphene.model.idl.G_GroupFields;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;

public class GroupFunnel implements Funnel<G_Group, Node> {

	private Neo4JEmbeddedService n4jService;

	@Inject
	public GroupFunnel(Neo4JEmbeddedService n4jService2) {
		n4jService = n4jService2;
	}

	@Override
	public G_Group from(Node f) {
		G_Group d = null;
		if (f != null) {
			// try (Transaction tx = beginTx()) {

			d = new G_Group();
			d.setName((String) f.getProperty(G_GroupFields.name.name(),
					"Undefined Group"));
			d.setDescription((String) f.getProperty(
					G_GroupFields.description.name(), "No Description"));
			// tx.success();
			// }
		}
		return d;

	}

	@Override
	public Node to(G_Group f) {

		String queryString = "MERGE (n:"
				+ GrapheneNeo4JConstants.groupLabel.name() + " {"
				+ G_GroupFields.name.name() + ": {var}}) RETURN n";
		Map<String, Object> parameters = new HashMap<>();
		parameters.put("var", f.getName());
		ResourceIterator<Node> resultIterator = null;
		resultIterator = n4jService.getExecutionEngine()
				.execute(queryString, parameters).columnAs("n");
		Node n = resultIterator.next();
		n.setProperty(G_GroupFields.name.name(), f.getName());
		n.setProperty(G_GroupFields.description.name(), f.getDescription());
		n.setProperty(G_GroupFields.id.name(), f.getId());

		return n;
	}
}
