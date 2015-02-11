package graphene.dao.neo4j;

import graphene.dao.UserGroupDAO;
import graphene.model.idl.G_CanonicalRelationshipType;
import graphene.model.idl.G_EdgeType;
import graphene.model.idl.G_Group;
import graphene.model.idl.G_GroupFields;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserFields;
import graphene.model.idl.G_UserGroup;

import java.util.ArrayList;
import java.util.List;

import org.apache.avro.AvroRemoteException;
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

public class UserGroupDAONeo4JEImpl  extends GenericUserSpaceDAONeo4jE implements UserGroupDAO {

	@Override
	public boolean addToGroup(String username, String groupname) {
		try (Transaction tx = beginTx()) {
			Node u = getUserNodeByUsername(username);
			Node g = getGroupNodeByGroupname(groupname);
			G_EdgeType edgeType = edgeTypeAccess
					.getCommonEdgeType(G_CanonicalRelationshipType.MEMBER_OF);
			u.createRelationshipTo(g,
					DynamicRelationshipType.withName(edgeType.getName()));
			tx.success();
			return true;
		} catch (AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		return false;
	}

	@Override
	public List<G_User> getUsersByGroup(String groupName) {
		List<G_User> list = new ArrayList<G_User>();
		try (Transaction tx = beginTx();
				ResourceIterator<Node> g = n4jService
						.getGraphDb()
						.findNodesByLabelAndProperty(
								GrapheneNeo4JConstants.groupLabel,
								G_GroupFields.name.name(), groupName)
						.iterator()) {
			G_EdgeType memberOf = edgeTypeAccess
					.getCommonEdgeType(G_CanonicalRelationshipType.MEMBER_OF);
			if (g.hasNext()) {
				Node j = g.next();
				TraversalDescription traversalDescription = n4jService
						.getGraphDb()
						.traversalDescription()
						.breadthFirst()
						.evaluator(
								Evaluators
										.includeWhereLastRelationshipTypeIs(DynamicRelationshipType
												.withName(memberOf.getName())));

				Traverser traverser = traversalDescription.traverse(j);
				for (Path path : traverser) {
					Node n = path.endNode();
					G_User d = userFunnel.from(n);
					if (d != null) {
						list.add(d);
					}
				}
				tx.success();
			}
		} catch (AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		return list;
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

				try {
					G_EdgeType memberOf = edgeTypeAccess
							.getCommonEdgeType(G_CanonicalRelationshipType.MEMBER_OF);

					G_EdgeType partOf = edgeTypeAccess
							.getCommonEdgeType(G_CanonicalRelationshipType.PART_OF);
					Node j = users.next();
					TraversalDescription traversalDescription = n4jService
							.getGraphDb()
							.traversalDescription()
							.depthFirst()
							.evaluator(Evaluators.excludeStartPosition())
							.relationships(
									DynamicRelationshipType.withName(memberOf
											.getName()), Direction.OUTGOING)
							.relationships(
									DynamicRelationshipType.withName(partOf
											.getName()), Direction.OUTGOING);
					Traverser traverser = traversalDescription.traverse(j);
					for (Path path : traverser) {
						Node n = path.endNode();
						if (n.hasLabel(GrapheneNeo4JConstants.groupLabel)) {
							G_Group d = groupFunnel.from(n);
							if (d != null) {
								list.add(d);
							}
						}
					}
				} catch (AvroRemoteException e) {
					logger.error(e.getMessage());
				}
			}
			tx.success();
		}
		return list;
	}

	@Override
	public boolean removeFromGroup(String userId, String groupId) {
		return removeFromGroup(getUserNodeById(userId), getGroupNode(groupId));
	}

	private boolean removeFromGroup(Node u, Node g) {
		if (u == null || g == null) {
			logger.warn("Could not perform removeUserFromWorkspace");
			return false;
		}
		try (Transaction tx = beginTx()) {
			G_EdgeType memberOf = edgeTypeAccess
					.getCommonEdgeType(G_CanonicalRelationshipType.MEMBER_OF);
			for (Relationship r : u.getRelationships(Direction.OUTGOING,
					DynamicRelationshipType.withName(memberOf.getName()))) {
				if (r.getEndNode().hasLabel(GrapheneNeo4JConstants.groupLabel)
						&& r.getEndNode().equals(g)) {
					r.delete();
				}
			}
			tx.success();
			return true;
		} catch (AvroRemoteException e) {
			logger.error(e.getMessage());
		}
		return false;
	}
	@Override
	public G_UserGroup save(G_UserGroup g) {
		// TODO Auto-generated method stub
		return null;
	}




}
