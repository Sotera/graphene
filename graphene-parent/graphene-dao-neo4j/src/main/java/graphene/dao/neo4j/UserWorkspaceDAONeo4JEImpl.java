package graphene.dao.neo4j;

import graphene.dao.UserDAO;
import graphene.dao.UserWorkspaceDAO;
import graphene.dao.WorkspaceDAO;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserFields;
import graphene.model.idl.G_UserSpaceRelationshipType;
import graphene.model.idl.G_UserWorkspace;
import graphene.model.idl.G_Workspace;
import graphene.util.ExceptionUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

public class UserWorkspaceDAONeo4JEImpl extends GenericUserSpaceDAONeo4jE
		implements UserWorkspaceDAO {

	@Override
	public boolean addRelationToWorkspace(String userId,
			G_UserSpaceRelationshipType rel, String id) {
		boolean success = false;
		boolean createRelationship = true;
		Node u = getUserNodeById(userId);
		Node w = getWorkspaceNodeById(id);
		if (u == null) {
			logger.error("Could not find user " + userId);
			return false;
		} else if (w == null) {
			logger.error("Could not find workspace " + id);
			return false;
		}
		try (Transaction tx = beginTx()) {
			for (Relationship r : u.getRelationships(relfunnel.to(rel))) {
				logger.debug("r.getEndNode().getId() " + r.getEndNode().getId());
				logger.debug("wNode.getId() " + u.getId());
				if (r.getEndNode().getId() == w.getId()) {
					createRelationship = false;
					break;
				}
			}

			success = true;
			tx.success();
		}
		if (success) {
			if (createRelationship) {
				try (Transaction tx = beginTx()) {
					u.createRelationshipTo(w, relfunnel.to(rel));
					tx.success();
				}
			}
			return true;
		} else {
			return false;
		}
	}

	@Override
	public List<G_Workspace> getWorkspacesForUser(String userId) {
		List<G_Workspace> list = new ArrayList<G_Workspace>();
		try (Transaction tx = beginTx()) {
			String queryString = "match (n:"
					+ GrapheneNeo4JConstants.userLabel.name() + ")-[r:"
					+ G_UserSpaceRelationshipType.EDITOR_OF.name()
					+ "]-w where n." + G_UserFields.id + " = '" + userId
					+ "' return w";
			Map<String, Object> parameters = new HashMap<String, Object>();
			ResourceIterator<Object> resultIterator = n4jService
					.getExecutionEngine().execute(queryString, parameters)
					.columnAs("w");
			while (resultIterator.hasNext()) {
				G_Workspace d = workspaceFunnel.from((Node) resultIterator.next());
				if (d != null) {
					list.add(d);
				}
			}
			resultIterator.close();
			// tx.success(); //FIXME: this causes an error, although in similar
			// RO cases it hasn't caused an error.
		}
		return list;
	}

	@Override
	public boolean hasRelationship(String userId, String id,
			G_UserSpaceRelationshipType... rel) {
		Node u = getUserNodeById(userId);
		Node w = getWorkspaceNodeById(id);
		if (u == null || w == null) {
			logger.warn("Could not find the user or workspace requested.");
			return false;
		}
		boolean has = false;
		try (Transaction tx = beginTx()) {
			// iterate through all the relationships of the given types.
			List<RelationshipType> relList = new ArrayList<RelationshipType>();
			for (G_UserSpaceRelationshipType r : rel) {
				relList.add(relfunnel.to(r));
			}
			Iterable<Relationship> matchingRels = w.getRelationships(relList
					.toArray(new RelationshipType[0]));
			for (Relationship r : matchingRels) {
				if (r.getStartNode().getId() == u.getId()) {
					has = true;
					break;
				}
			}
			tx.success();
		} catch (Exception e) {
			logger.error(ExceptionUtil.getRootCauseMessage(e));
		}
		return has;
	}

	@Inject
	private WorkspaceDAO workspaceDAO;
	@Inject
	private UserDAO userDAO;

	@Override
	public boolean removeUserFromWorkspace(String userId, String workspaceId) {
		boolean success = false;
		Node uNode, wNode;
		uNode = getUserNodeById(userId);
		wNode = getWorkspaceNodeById(workspaceId);
		if (uNode != null && wNode != null) {
			try (Transaction tx = beginTx()) {

				for (Relationship r : uNode.getRelationships(relfunnel
						.to(G_UserSpaceRelationshipType.EDITOR_OF))) {
					logger.debug("r.getEndNode().getId() "
							+ r.getEndNode().getId());
					logger.debug("wNode.getId() " + wNode.getId());
					if (r.getEndNode().getId() == wNode.getId()) {
						r.delete();
						success = true;
						break;
					}
				}
				tx.success();
			}
		}
		return success;
	}

	@Override
	public boolean removeUserPermissionFromWorkspace(String userId,
			String permission, String workspaceId) {
		boolean success = false;
		Node uNode, wNode;
		uNode = getUserNodeById(userId);
		wNode = getWorkspaceNodeById(workspaceId);
		G_UserSpaceRelationshipType rel = G_UserSpaceRelationshipType
				.valueOf(permission);
		if (uNode != null && wNode != null) {
			try (Transaction tx = beginTx()) {
				for (Relationship r : uNode.getRelationships(relfunnel
						.to(G_UserSpaceRelationshipType.EDITOR_OF))) {
					if (r.getOtherNode(uNode).equals(wNode)) {
						logger.info("Removing relationship '" + rel
								+ "' between " + userId + " and " + workspaceId);
						r.delete();
						success = true;
						break;
					}
				}
				tx.success();
			}
		}
		return success;
	}

	@Override
	public G_UserWorkspace save(G_UserWorkspace g) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_User> getUsersForWorkspace(String workspaceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int countUsersForWorkspace(String workspaceId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean deleteWorkspaceRelations(String workspaceId) {
		// TODO Auto-generated method stub
		return false;
	}

}
