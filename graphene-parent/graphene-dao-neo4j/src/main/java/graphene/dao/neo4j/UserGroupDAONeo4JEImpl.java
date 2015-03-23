package graphene.dao.neo4j;

import graphene.dao.UserGroupDAO;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_Group;
import graphene.model.idl.G_GroupFields;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserFields;
import graphene.model.idl.G_UserGroup;

import java.util.ArrayList;
import java.util.List;

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

public class UserGroupDAONeo4JEImpl extends GenericUserSpaceDAONeo4jE implements UserGroupDAO {

	@Override
	public boolean addToGroup(final String username, final String groupname) {
		try (Transaction tx = beginTx()) {
			final Node u = getUserNodeByUsername(username);
			final Node g = getGroupNodeByGroupname(groupname);
			u.createRelationshipTo(g, DynamicRelationshipType.withName(G_CanonicalRelationshipType.MEMBER_OF.name()));
			tx.success();
			return true;
		}
	}

	@Override
	public boolean delete(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<G_UserGroup> getAll() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_UserGroup getById(final String id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserGroup> getGroupMembershipsForGroupId(final String groupId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserGroup> getGroupMembershipsForUserId(final String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_UserGroup> getGroupMembershipsForUserIdAndGroupId(final String userId, final String groupId) {
		return null;
	}

	@Override
	public List<G_Group> getGroupsForUserId(final String username) {
		final List<G_Group> list = new ArrayList<G_Group>();
		try (Transaction tx = beginTx();
				ResourceIterator<Node> users = n4jService
						.getGraphDb()
						.findNodesByLabelAndProperty(GrapheneNeo4JConstants.userLabel, G_UserFields.username.name(),
								username).iterator()) {

			if (users.hasNext()) {

				final Node j = users.next();
				final TraversalDescription traversalDescription = n4jService
						.getGraphDb()
						.traversalDescription()
						.depthFirst()
						.evaluator(Evaluators.excludeStartPosition())
						.relationships(DynamicRelationshipType.withName(G_CanonicalRelationshipType.MEMBER_OF.name()),
								Direction.OUTGOING)
						.relationships(DynamicRelationshipType.withName(G_CanonicalRelationshipType.PART_OF.name()),
								Direction.OUTGOING);
				final Traverser traverser = traversalDescription.traverse(j);
				for (final Path path : traverser) {
					final Node n = path.endNode();
					if (n.hasLabel(GrapheneNeo4JConstants.groupLabel)) {
						final G_Group d = groupFunnel.from(n);
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

	@Override
	public List<G_User> getUsersByGroupId(final String groupName) {
		final List<G_User> list = new ArrayList<G_User>();
		try (Transaction tx = beginTx();
				ResourceIterator<Node> g = n4jService
						.getGraphDb()
						.findNodesByLabelAndProperty(GrapheneNeo4JConstants.groupLabel, G_GroupFields.name.name(),
								groupName).iterator()) {
			if (g.hasNext()) {
				final Node j = g.next();
				final TraversalDescription traversalDescription = n4jService
						.getGraphDb()
						.traversalDescription()
						.breadthFirst()
						.evaluator(
								Evaluators.includeWhereLastRelationshipTypeIs(DynamicRelationshipType
										.withName(G_CanonicalRelationshipType.MEMBER_OF.name())));

				final Traverser traverser = traversalDescription.traverse(j);
				for (final Path path : traverser) {
					final Node n = path.endNode();
					final G_User d = userFunnel.from(n);
					if (d != null) {
						list.add(d);
					}
				}
				tx.success();
			}
		}
		return list;
	}

	private boolean removeFromGroup(final Node u, final Node g) {
		if ((u == null) || (g == null)) {
			logger.warn("Could not perform removeUserFromWorkspace");
			return false;
		}
		try (Transaction tx = beginTx()) {
			for (final Relationship r : u.getRelationships(Direction.OUTGOING,
					DynamicRelationshipType.withName(G_CanonicalRelationshipType.MEMBER_OF.name()))) {
				if (r.getEndNode().hasLabel(GrapheneNeo4JConstants.groupLabel) && r.getEndNode().equals(g)) {
					r.delete();
				}
			}
			tx.success();
			return true;
		}
	}

	@Override
	public boolean removeFromGroup(final String userId, final String groupId) {
		return removeFromGroup(getUserNodeById(userId), getGroupNode(groupId));
	}

	@Override
	public G_UserGroup save(final G_UserGroup g) {
		// TODO Auto-generated method stub
		return null;
	}

}
