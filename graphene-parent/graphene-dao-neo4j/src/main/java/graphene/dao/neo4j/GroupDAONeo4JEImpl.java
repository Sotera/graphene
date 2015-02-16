package graphene.dao.neo4j;

import graphene.dao.GroupDAO;
import graphene.dao.neo4j.annotations.UserGraph;
import graphene.model.idl.G_Group;
import graphene.model.idl.G_GroupFields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

public class GroupDAONeo4JEImpl extends GenericUserSpaceDAONeo4jE implements GroupDAO {
	private final Neo4JEmbeddedService n4jService;

	public GroupDAONeo4JEImpl(@UserGraph final Neo4JEmbeddedService service) {
		n4jService = service;
	}

	private G_Group createDetached(final Node u) {
		G_Group d = null;
		try (Transaction tx = beginTx()) {
			d = groupFunnel.from(u);
			tx.success();
		}
		return d;
	}

	@Override
	public G_Group createGroup(final G_Group gd) {
		G_Group g = null;
		Node n;
		try (Transaction tx = beginTx()) {
			n = groupFunnel.to(gd);
			tx.success();
		}
		g = createDetached(n);
		return g;
	}

	@Override
	public boolean delete(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<G_Group> getAll() {
		final List<G_Group> list = new ArrayList<G_Group>();
		try (Transaction tx = beginTx()) {
			try (ResourceIterator<Node> iter = n4jService.getGgo()
					.getAllNodesWithLabel(GrapheneNeo4JConstants.groupLabel).iterator()) {
				while (iter.hasNext()) {
					final G_Group d = createDetached(iter.next());
					if (d != null) {
						list.add(d);
					}
				}
			}
		} catch (final Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		return list;
	}

	@Override
	public G_Group getById(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_Group getGroupByGroupname(final String groupname) {
		return createDetached(getGroupNodeByGroupname(groupname));
	}

	// @PostInjection
	@Override
	public void initialize() {
		if (n4jService.connectToGraph()) {
			logger.debug("Constructing GroupDAONeo4jImpl hooked up to " + n4jService.getLocation());
			n4jService.createNewIndex(GrapheneNeo4JConstants.groupLabel, G_GroupFields.name.name());

		} else {
			logger.error("Could not connect to graph, so GroupDAONeo4jImpl was not constructed.");
		}
	}

	@Override
	public G_Group save(G_Group g) {
		ResourceIterator<Node> resultIterator = null;
		try (Transaction tx = beginTx()) {
			final String queryString = "MERGE (n:" + GrapheneNeo4JConstants.groupLabel.name() + " {"
					+ G_GroupFields.name.name() + ": {var}}) RETURN n";
			final Map<String, Object> parameters = new HashMap<>();
			parameters.put("var", g.getName());
			resultIterator = n4jService.getExecutionEngine().execute(queryString, parameters).columnAs("n");
			final Node n = resultIterator.next();
			n.setProperty(G_GroupFields.name.name(), g.getName());
			g = createDetached(n);
			tx.success();
		}
		return g;
	}

}
