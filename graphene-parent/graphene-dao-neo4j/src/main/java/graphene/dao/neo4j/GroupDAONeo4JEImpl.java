package graphene.dao.neo4j;

import graphene.dao.GroupDAO;
import graphene.dao.neo4j.annotations.UserGraph;
import graphene.dao.neo4j.funnel.GroupFunnel;
import graphene.model.idl.G_Group;
import graphene.model.idl.G_GroupFields;
import graphene.model.idl.G_RelationshipType;
import graphene.model.idl.G_UserFields;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.DynamicRelationshipType;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;

public class GroupDAONeo4JEImpl extends GenericUserSpaceDAONeo4jE implements
		GroupDAO {
	private Neo4JEmbeddedService n4jService;
	GroupFunnel funnel;

	public GroupDAONeo4JEImpl(@UserGraph Neo4JEmbeddedService service) {
		this.n4jService = service;
		funnel = new GroupFunnel(n4jService);
	}

	@Override
	public boolean addToGroup(String username, String groupname) {
		try (Transaction tx = beginTx()) {
			Node u = getUserNodeByUsername(username);
			Node g = getGroupNodeByGroupname(groupname);
			u.createRelationshipTo(g, DynamicRelationshipType
					.withName(G_RelationshipType.MEMBER_OF.name()));
			tx.success();
			return true;
		}
	}

	private G_Group createDetached(Node u) {
		G_Group d = null;
		try (Transaction tx = beginTx()) {
			d = funnel.from(u);
			tx.success();
		}
		return d;
	}

	@Override
	public G_Group createGroup(G_Group gd) {
		G_Group g = null;
		Node n;
		try (Transaction tx = beginTx()) {
			n = funnel.to(gd);
			tx.success();
		}
		g = createDetached(n);
		return g;
	}

	@Override
	public void deleteGroup(G_Group g) {

	}

	@Override
	public List<G_Group> getAllGroups() {
		List<G_Group> list = new ArrayList<G_Group>();
		try (Transaction tx = beginTx()) {
			try (ResourceIterator<Node> iter = n4jService.getGgo()
					.getAllNodesWithLabel(GrapheneNeo4JConstants.groupLabel)
					.iterator()) {
				while (iter.hasNext()) {
					G_Group d = createDetached(iter.next());
					if (d != null) {
						list.add(d);
					}
				}
			}
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
		return list;
	}

	@Override
	public G_Group getGroupByGroupname(String groupname) {
		return createDetached(getGroupNodeByGroupname(groupname));
	}

	private Node getGroupNodeByGroupname(String groupname) {
		Node n = null;
		try (Transaction tx = beginTx()) {
			for (Node node : n4jService.getGraphDb()
					.findNodesByLabelAndProperty(
							GrapheneNeo4JConstants.groupLabel,
							G_GroupFields.name.name(), groupname)) {
				n = node;
			}
			tx.success();
		}
		return n;
	}

	private Node getGroupNode(int id) {
		Node n = null;
		try (Transaction tx = beginTx()) {
			for (Node node : n4jService.getGraphDb()
					.findNodesByLabelAndProperty(
							GrapheneNeo4JConstants.groupLabel,
							G_GroupFields.id.name(), id)) {
				n = node;
			}
			tx.success();
		}
		return n;
	}

	@Override
	public List<G_Group> getGroupsForUser(String username) {
		List<G_Group> list = new ArrayList<G_Group>();
		try (Transaction tx = beginTx();
				ResourceIterator<Node> users = n4jService
						.getGraphDb()
						.findNodesByLabelAndProperty(
								GrapheneNeo4JConstants.userLabel,
								G_UserFields.username.name(), username)
						.iterator()) {

			if (users.hasNext()) {
				Node j = users.next();
				TraversalDescription traversalDescription = n4jService
						.getGraphDb()
						.traversalDescription()
						.depthFirst()
						.evaluator(Evaluators.excludeStartPosition())
						.relationships(
								DynamicRelationshipType.withName(G_RelationshipType.MEMBER_OF
										.name()), Direction.OUTGOING)
						.relationships(
								DynamicRelationshipType.withName(G_RelationshipType.PART_OF
										.name()), Direction.OUTGOING);
				Traverser traverser = traversalDescription.traverse(j);
				for (Path path : traverser) {
					Node n = path.endNode();
					if (n.hasLabel(GrapheneNeo4JConstants.groupLabel)) {
						G_Group d = createDetached(n);
						if (d != null) {
							list.add(d);
						}
					}
				}
			}
			tx.success();
		}
		return list;
	}

	@PostInjection
	public void initialize() {
		if (n4jService.connectToGraph()) {
			logger.debug("Constructing GroupDAONeo4jImpl hooked up to "
					+ n4jService.getLocation());
			n4jService.createNewIndex(GrapheneNeo4JConstants.groupLabel,
					G_GroupFields.name.name());

		} else {
			logger.error("Could not connect to graph, so GroupDAONeo4jImpl was not constructed.");
		}
	}

	private boolean removeFromGroup(Node u, Node g) {
		if (u == null || g == null) {
			logger.warn("Could not perform removeUserFromWorkspace");
			return false;
		}
		try (Transaction tx = beginTx()) {
			for (Relationship r : u.getRelationships(Direction.OUTGOING,
					DynamicRelationshipType
							.withName(G_RelationshipType.MEMBER_OF.name()))) {
				if (r.getEndNode().hasLabel(GrapheneNeo4JConstants.groupLabel)
						&& r.getEndNode().equals(g)) {
					r.delete();
				}
			}
			tx.success();
			return true;
		}
	}

	@Override
	public G_Group save(G_Group g) {
		ResourceIterator<Node> resultIterator = null;
		try (Transaction tx = beginTx()) {
			String queryString = "MERGE (n:"
					+ GrapheneNeo4JConstants.groupLabel.name() + " {"
					+ G_GroupFields.name.name() + ": {var}}) RETURN n";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("var", g.getName());
			resultIterator = n4jService.getExecutionEngine()
					.execute(queryString, parameters).columnAs("n");
			Node n = resultIterator.next();
			n.setProperty(G_GroupFields.name.name(), g.getName());
			g = createDetached(n);
			tx.success();
		}
		return g;
	}

	@Override
	public G_Group getGroupById(int id) {
		return createDetached(getGroupNode(id));
	}

	@Override
	public boolean removeFromGroup(int userId, int groupId) {
		return removeFromGroup(getUserNodeById(userId), getGroupNode(groupId));
	}
}
