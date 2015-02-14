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

public class UserWorkspaceDAONeo4JEImpl extends GenericUserSpaceDAONeo4jE implements UserWorkspaceDAO {

	@Inject
	private WorkspaceDAO workspaceDAO;

	@Inject
	private UserDAO userDAO;

	@Override
	public boolean addRelationToWorkspace(final String userId, final G_UserSpaceRelationshipType rel, final String id) {
		boolean success = false;
		boolean createRelationship = true;
		final Node u = getUserNodeById(userId);
		final Node w = getWorkspaceNodeById(id);
		if (u == null) {
			logger.error("Could not find user " + userId);
			return false;
		} else if (w == null) {
			logger.error("Could not find workspace " + id);
			return false;
		}
		try (Transaction tx = beginTx()) {
			for (final Relationship r : u.getRelationships(relfunnel.to(rel))) {
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
	public int countUsersForWorkspace(final String workspaceId) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean delete(final String id) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean deleteWorkspaceRelations(final String workspaceId) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public List<G_UserWorkspace> getAllWorkspaceRelations() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_User> getUsersForWorkspace(final String workspaceId) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_Workspace> getWorkspacesForUser(final String userId) {
		final List<G_Workspace> list = new ArrayList<G_Workspace>();
		try (Transaction tx = beginTx()) {
			final String queryString = "match (n:" + GrapheneNeo4JConstants.userLabel.name() + ")-[r:"
					+ G_UserSpaceRelationshipType.EDITOR_OF.name() + "]-w where n." + G_UserFields.id + " = '" + userId
					+ "' return w";
			final Map<String, Object> parameters = new HashMap<String, Object>();
			final ResourceIterator<Object> resultIterator = n4jService.getExecutionEngine()
					.execute(queryString, parameters).columnAs("w");
			while (resultIterator.hasNext()) {
				final G_Workspace d = workspaceFunnel.from((Node) resultIterator.next());
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
	public boolean hasRelationship(final String userId, final String id, final G_UserSpaceRelationshipType... rel) {
		final Node u = getUserNodeById(userId);
		final Node w = getWorkspaceNodeById(id);
		if ((u == null) || (w == null)) {
			logger.warn("Could not find the user or workspace requested.");
			return false;
		}
		boolean has = false;
		try (Transaction tx = beginTx()) {
			// iterate through all the relationships of the given types.
			final List<RelationshipType> relList = new ArrayList<RelationshipType>();
			for (final G_UserSpaceRelationshipType r : rel) {
				relList.add(relfunnel.to(r));
			}
			final Iterable<Relationship> matchingRels = w.getRelationships(relList.toArray(new RelationshipType[0]));
			for (final Relationship r : matchingRels) {
				if (r.getStartNode().getId() == u.getId()) {
					has = true;
					break;
				}
			}
			tx.success();
		} catch (final Exception e) {
			logger.error(ExceptionUtil.getRootCauseMessage(e));
		}
		return has;
	}

	@Override
	public boolean removeUserFromWorkspace(final String userId, final String workspaceId) {
		boolean success = false;
		Node uNode, wNode;
		uNode = getUserNodeById(userId);
		wNode = getWorkspaceNodeById(workspaceId);
		if ((uNode != null) && (wNode != null)) {
			try (Transaction tx = beginTx()) {

				for (final Relationship r : uNode.getRelationships(relfunnel.to(G_UserSpaceRelationshipType.EDITOR_OF))) {
					logger.debug("r.getEndNode().getId() " + r.getEndNode().getId());
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
	public boolean removeUserPermissionFromWorkspace(final String userId, final String permission,
			final String workspaceId) {
		boolean success = false;
		Node uNode, wNode;
		uNode = getUserNodeById(userId);
		wNode = getWorkspaceNodeById(workspaceId);
		final G_UserSpaceRelationshipType rel = G_UserSpaceRelationshipType.valueOf(permission);
		if ((uNode != null) && (wNode != null)) {
			try (Transaction tx = beginTx()) {
				for (final Relationship r : uNode.getRelationships(relfunnel.to(G_UserSpaceRelationshipType.EDITOR_OF))) {
					if (r.getOtherNode(uNode).equals(wNode)) {
						logger.info("Removing relationship '" + rel + "' between " + userId + " and " + workspaceId);
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
	public G_UserWorkspace save(final G_UserWorkspace g) {
		// TODO Auto-generated method stub
		return null;
	}

}
