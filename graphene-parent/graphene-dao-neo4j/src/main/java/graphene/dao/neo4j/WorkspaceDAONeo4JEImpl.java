package graphene.dao.neo4j;

import graphene.dao.WorkspaceDAO;
import graphene.dao.neo4j.annotations.UserGraph;
import graphene.model.idl.G_UserFields;
import graphene.model.idl.G_UserSpaceRelationshipType;
import graphene.model.idl.G_Workspace;
import graphene.model.idl.G_WorkspaceFields;
import graphene.util.ExceptionUtil;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.joda.time.DateTime;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.RelationshipType;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

import scala.Array;

public class WorkspaceDAONeo4JEImpl extends GenericUserSpaceDAONeo4jE implements
		WorkspaceDAO {
	G_UserSpaceRelationshipTypeFunnel relfunnel;

	public WorkspaceDAONeo4JEImpl(@UserGraph Neo4JEmbeddedService service) {
		this.n4jService = service;

	}

	@Override
	public G_Workspace addNewWorkspace(int userId, G_Workspace w) {
		boolean success = false;
		boolean createRelationship = true;
		Node u, wNode;
		u = getUserNodeById(userId);
		wNode = getOrCreateWorkspaceNode(w);
		try (Transaction tx = beginTx()) {
			for (Relationship r : u.getRelationships(relfunnel
					.to(G_UserSpaceRelationshipType.EDITOR_OF))) {
				logger.debug("r.getEndNode().getId() " + r.getEndNode().getId());
				logger.debug("wNode.getId() " + wNode.getId());
				if (r.getEndNode().getId() == wNode.getId()) {
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
					u.createRelationshipTo(wNode,
							relfunnel.to(G_UserSpaceRelationshipType.EDITOR_OF));
					tx.success();
				}
			}
			return w;
		} else {
			logger.error("Could not add new Workspace " + w.getId()
					+ " to user " + userId);
			return null;
		}
	}

	@Override
	public boolean addRelationToWorkspace(int userId,
			G_UserSpaceRelationshipType rel, int id) {
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
	public long countWorkspaces(String partialName) {
		long n = 0;
		try (Transaction tx = beginTx()) {
			String filterOnTitle = "";
			{
				if (ValidationUtils.isValid(partialName)) {
					filterOnTitle = " where w." + G_WorkspaceFields.title
							+ " =~ '.*" + partialName + ".*' ";
				}
			}
			String queryString = "match (w:"
					+ GrapheneNeo4JConstants.workspaceLabel.name() + ") "
					+ filterOnTitle + " return count(*) as c;";
			Map<String, Object> parameters = new HashMap<String, Object>();
			ResourceIterator<Object> resultIterator = n4jService
					.getExecutionEngine().execute(queryString, parameters)
					.columnAs("c");
			n = (long) resultIterator.next();
			tx.success();
		}

		return n;
	}

	@Override
	public long countWorkspaces(int userId, String partialName) {
		long n = 0;

		try (Transaction tx = beginTx()) {
			String filterOnTitle = "";
			{
				if (ValidationUtils.isValid(partialName)) {
					filterOnTitle = " where w." + G_WorkspaceFields.title
							+ " =~ '.*" + partialName + ".*' ";
				}
			}
			String queryString = "match (u:"
					+ GrapheneNeo4JConstants.userLabel.name() + " {"
					+ G_UserFields.id + ":'" + userId + "'})-[r:"
					+ G_UserSpaceRelationshipType.EDITOR_OF + "]-(w:"
					+ GrapheneNeo4JConstants.workspaceLabel + ") "
					+ filterOnTitle + " return count(*) as c;";
			Map<String, Object> parameters = new HashMap<String, Object>();
			ResourceIterator<Object> resultIterator = n4jService
					.getExecutionEngine().execute(queryString, parameters)
					.columnAs("c");
			n = (long) resultIterator.next();
			tx.success();
		}

		return n;
	}

	public G_Workspace createDetached(Node u) {
		G_Workspace d = null;
		if (u != null) {
			try (Transaction tx = beginTx()) {
				d = new G_Workspace();
				d.setActive((boolean) u.getProperty(
						G_WorkspaceFields.active.name(), true));
				d.setJson((String) u.getProperty(G_WorkspaceFields.json.name()));
				d.setModified(new DateTime(u.getProperty(
						G_WorkspaceFields.modified.name(), 0l)).getMillis());
				d.setTitle((String) u.getProperty(G_WorkspaceFields.title
						.name()));
				d.setId((Integer) u.getProperty(G_WorkspaceFields.id.name()));
				tx.success();
			}
		}
		return d;
	}

	private boolean deleteWorkspace(Node wNode) {
		boolean success = false;
		try (Transaction tx = beginTx()) {
			for (Relationship r : wNode.getRelationships()) {
				r.delete();
			}
			wNode.delete();
			tx.success();
			return true;
		} catch (Exception e) {
			logger.error("Error deleting workspace.");
		}
		return success;
	}

	@Override
	public boolean deleteWorkspaceById(int workspaceId) {
		boolean success = false;
		try (Transaction tx = beginTx()) {
			Node n = getWorkspaceNodeById(workspaceId);
			n.delete();
			tx.success();
			success = true;
		} catch (Exception e) {
			success = false;
		}
		return success;
	}

	@Override
	public boolean deleteWorkspaceIfUnused(int workspaceId) {
		boolean success = false;
		Node wNode;
		wNode = getWorkspaceNodeById(workspaceId);
		if (wNode == null) {
			success = false;
		} else {
			try (Transaction tx = beginTx()) {
				// If the node has NO incoming edges of Editor, then delete it.
				if (!wNode
						.getRelationships(
								Direction.INCOMING,
								relfunnel
										.to(G_UserSpaceRelationshipType.EDITOR_OF))
						.iterator().hasNext()) {
					deleteWorkspace(wNode);
				}
				tx.success();
			} catch (Exception e) {
				logger.error(e.getMessage());
				success = false;
			}
			success = true;
		}
		return success;
	}

	@Override
	public List<G_Workspace> findWorkspaces(String partialName, int startIndex,
			int i) {
		List<G_Workspace> list = new ArrayList<G_Workspace>();

		// MATCH (n:GUser {username:'djue'})-[r]->(w:GWorkspace) where
		// w.title
		// =~ '.*New.*' RETURN n,r,w LIMIT 25

		try (Transaction tx = beginTx()) {
			String filterOnTitle = "";
			{
				if (ValidationUtils.isValid(partialName)) {
					filterOnTitle = " where w." + G_WorkspaceFields.title
							+ " =~ '.*" + partialName + ".*' ";
				}
			}
			String queryString = "match (w:"
					+ GrapheneNeo4JConstants.workspaceLabel.name() + ") "
					+ filterOnTitle + " return w";
			Map<String, Object> parameters = new HashMap<String, Object>();
			ResourceIterator<Object> resultIterator = n4jService
					.getExecutionEngine().execute(queryString, parameters)
					.columnAs("w");
			while (resultIterator.hasNext()) {
				G_Workspace d = createDetached((Node) resultIterator.next());
				if (d != null) {
					list.add(d);
				}
			}
			resultIterator.close();
			// tx.success(); //FIXME: this causes an error, although in
			// similar
			// RO cases it hasn't caused an error.

		}
		return list;
	}

	@Override
	public List<G_Workspace> findWorkspaces(int userId, String partialName,
			int startIndex, int i) {
		// MATCH (n:GUser {username:'djue'})-[r]->(m:GWorkspace) where m.title
		// =~ '.*New.*' RETURN n,r,m LIMIT 25
		List<G_Workspace> list = new ArrayList<G_Workspace>();
		try (Transaction tx = beginTx()) {
			String filterOnTitle = "";
			{
				if (ValidationUtils.isValid(partialName)) {
					filterOnTitle = " where w." + G_WorkspaceFields.title
							+ " =~ '.*" + partialName + ".*' ";
				}
			}// match (u:GUser {username:'sam'})-[r:EDITOR_OF]-(w:GWorkspace)
				// return count(*) as c
			String queryString = "match (u:"
					+ GrapheneNeo4JConstants.userLabel.name() + " {"
					+ G_UserFields.id + ":'" + userId + "'})-[r:"
					+ G_UserSpaceRelationshipType.EDITOR_OF + "]-(w:"
					+ GrapheneNeo4JConstants.workspaceLabel + ") "
					+ filterOnTitle + " return w";

			Map<String, Object> parameters = new HashMap<String, Object>();
			ResourceIterator<Object> resultIterator = n4jService
					.getExecutionEngine().execute(queryString, parameters)
					.columnAs("w");
			while (resultIterator.hasNext()) {
				G_Workspace d = createDetached((Node) resultIterator.next());
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
	public List<G_Workspace> getAllWorkspaces() {
		List<G_Workspace> list = new ArrayList<G_Workspace>();
		try (Transaction tx = beginTx()) {
			try (ResourceIterator<Node> iter = n4jService.getGgo()
					.getAllNodesWithLabel(GrapheneNeo4JConstants.groupLabel)
					.iterator()) {
				while (iter.hasNext()) {
					G_Workspace d = createDetached(iter.next());
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
	public G_Workspace getOrCreateWorkspace(G_Workspace g) {
		return createDetached(getOrCreateWorkspaceNode(g));
	}

	private Node getOrCreateWorkspaceNode(G_Workspace g) {
		Node n = null;
		ResourceIterator<Node> resultIterator = null;
		try (Transaction tx = beginTx()) {
			String queryString = "MERGE (n:"
					+ GrapheneNeo4JConstants.workspaceLabel.name() + " {"
					+ G_WorkspaceFields.id.name() + ": {var}}) RETURN n";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("var", g.getId());
			resultIterator = n4jService.getExecutionEngine()
					.execute(queryString, parameters).columnAs("n");
			n = resultIterator.next();
			resultIterator.close();
			setSafeProperty(n, G_WorkspaceFields.created.name(), DateTime.now()
					.getMillis());
			setSafeProperty(n, G_WorkspaceFields.modified.name(), DateTime
					.now().getMillis());
			setSafeProperty(n, G_WorkspaceFields.active.name(), true);
			setSafeProperty(n, G_WorkspaceFields.json.name(), g.getJson());
			setSafeProperty(n, G_WorkspaceFields.title.name(), g.getTitle());

			tx.success();
		}
		// END SNIPPET: updateUsers
		return n;
	}

	@Override
	public G_Workspace getWorkspaceById(int id) {
		Node n = getWorkspaceNodeById(id);
		return (n == null ? null : createDetached(n));
	}

	private Node getWorkspaceNodeById(int id) {
		Node n = null;
		try (Transaction tx = beginTx()) {
			for (Node node : n4jService.getGraphDb()
					.findNodesByLabelAndProperty(
							GrapheneNeo4JConstants.workspaceLabel,
							G_WorkspaceFields.id.name(), id)) {
				n = node;
			}
			tx.success();
		} catch (Exception e) {
			logger.error(ExceptionUtil.getRootCauseMessage(e));
		}
		if (n == null) {
			logger.warn("Could not find workspace with id '" + id + "'");
		}
		return n;
	}

	@Override
	public List<G_Workspace> getWorkspacesForUser(int userId) {
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
				G_Workspace d = createDetached((Node) resultIterator.next());
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
	public boolean hasRelationship(int userId, int id,
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
			Iterable<Relationship> matchingRels = w.getRelationships(relList.toArray(new RelationshipType[0]));
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

	@PostInjection
	public void initialize() {
		if (n4jService.connectToGraph()) {
			logger.debug("Constructing WorkspaceDAO hooked up to "
					+ n4jService.getLocation());
			n4jService.createNewIndex(GrapheneNeo4JConstants.workspaceLabel,
					G_WorkspaceFields.id.name());
		} else {
			logger.error("Could not connect to graph, so WorkspaceDAO was not constructed.");
		}
	}

	@Override
	public boolean removeUserFromWorkspace(int userId, int workspaceId) {
		boolean success = false;
		Node uNode, wNode;
		uNode = getUserNodeById(userId);
		wNode = getWorkspaceNodeById(workspaceId);
		if (uNode != null && wNode != null) {
			try (Transaction tx = beginTx()) {
				
				for (Relationship r : uNode
						.getRelationships(relfunnel.to(G_UserSpaceRelationshipType.EDITOR_OF))) {
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
		deleteWorkspaceIfUnused(workspaceId);
		return success;
	}

	@Override
	public boolean removeUserPermissionFromWorkspace(int userId,
			String permission, int workspaceId) {
		boolean success = false;
		Node uNode, wNode;
		uNode = getUserNodeById(userId);
		wNode = getWorkspaceNodeById(workspaceId);
		G_UserSpaceRelationshipType rel = G_UserSpaceRelationshipType
				.valueOf(permission);
		if (uNode != null && wNode != null) {
			try (Transaction tx = beginTx()) {
				for (Relationship r : uNode.getRelationships(relfunnel.to(G_UserSpaceRelationshipType.EDITOR_OF))) {
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
		deleteWorkspaceIfUnused(workspaceId);
		return success;
	}

	@Override
	public G_Workspace save(G_Workspace g) {
		ResourceIterator<Node> resultIterator = null;

		try (Transaction tx = beginTx()) {
			String queryString = "MERGE (n:"
					+ GrapheneNeo4JConstants.workspaceLabel.name() + " {"
					+ G_WorkspaceFields.id.name() + ": {var}}) RETURN n";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("var", g.getId());
			resultIterator = n4jService.getExecutionEngine()
					.execute(queryString, parameters).columnAs("n");
			Node n = resultIterator.next();
			setSafeProperty(n, G_WorkspaceFields.created.name(),
					g.getModified());
			setSafeProperty(n, G_WorkspaceFields.modified.name(),
					g.getModified());
			setSafeProperty(n, G_WorkspaceFields.active.name(), true);

			setSafeProperty(n, G_WorkspaceFields.json.name(), g.getJson());
			setSafeProperty(n, G_WorkspaceFields.title.name(), g.getTitle());
			tx.success();
		}
		return g;
	}

}
