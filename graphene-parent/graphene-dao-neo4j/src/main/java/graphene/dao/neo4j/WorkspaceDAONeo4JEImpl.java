package graphene.dao.neo4j;

import graphene.dao.WorkspaceDAO;
import graphene.dao.neo4j.annotations.UserGraph;
import graphene.model.graph.G_PersistedGraph;
import graphene.model.idl.G_UserFields;
import graphene.model.idl.G_UserSpaceRelationshipType;
import graphene.model.idl.G_Workspace;
import graphene.model.idl.G_WorkspaceFields;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;

public class WorkspaceDAONeo4JEImpl extends GenericUserSpaceDAONeo4jE implements
		WorkspaceDAO {

	public WorkspaceDAONeo4JEImpl(@UserGraph Neo4JEmbeddedService service) {
		this.n4jService = service;
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
	public long countWorkspaces(String userId, String partialName) {
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
	public boolean delete(String workspaceId) {
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

//	@Override
//	public boolean deleteWorkspaceIfUnused(int workspaceId) {
//		boolean success = false;
//		Node wNode;
//		wNode = getWorkspaceNodeById(workspaceId);
//		if (wNode == null) {
//			success = false;
//		} else {
//			try (Transaction tx = beginTx()) {
//				// If the node has NO incoming edges of Editor, then delete it.
//				if (!wNode
//						.getRelationships(
//								Direction.INCOMING,
//								relfunnel
//										.to(G_UserSpaceRelationshipType.EDITOR_OF))
//						.iterator().hasNext()) {
//					success = deleteWorkspace(wNode);
//				}
//				tx.success();
//			} catch (Exception e) {
//				logger.error(e.getMessage());
//				success = false;
//			}
//		}
//		if (success) {
//			logger.debug("Deleted workspace " + workspaceId);
//		} else {
//			logger.debug("Did not delete workspace " + workspaceId);
//		}
//		return success;
//	}

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
				G_Workspace d = workspaceFunnel.from((Node) resultIterator
						.next());
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
	public List<G_Workspace> findWorkspaces(String userId, String partialName,
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

				G_Workspace d = workspaceFunnel.from((Node) resultIterator
						.next());// createDetached((Node)
									// resultIterator.next());
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
					G_Workspace d = workspaceFunnel.from(iter.next());
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

//	@Override
//	public G_Workspace getOrCreateWorkspace(G_Workspace g) {
//		Node n = null;
//		ResourceIterator<Node> resultIterator = null;
//		try (Transaction tx = beginTx()) {
//			String queryString = "MERGE (n:"
//					+ GrapheneNeo4JConstants.workspaceLabel.name() + " {"
//					+ G_WorkspaceFields.id.name() + ": {var}}) RETURN n";
//			Map<String, Object> parameters = new HashMap<>();
//			parameters.put("var", g.getId());
//			resultIterator = n4jService.getExecutionEngine()
//					.execute(queryString, parameters).columnAs("n");
//			n = resultIterator.next();
//			resultIterator.close();
//			setSafeProperty(n, G_WorkspaceFields.created.name(), DateTime.now()
//					.getMillis());
//			setSafeProperty(n, G_WorkspaceFields.modified.name(), DateTime
//					.now().getMillis());
//			setSafeProperty(n, G_WorkspaceFields.active.name(), true);
//			setSafeProperty(n, G_WorkspaceFields.json.name(), g.getJson());
//			setSafeProperty(n, G_WorkspaceFields.title.name(), g.getTitle());
//
//			tx.success();
//		}
//		return workspaceFunnel.from(n);
//	}

	@Override
	public G_Workspace getById(String id) {
		Node n = getWorkspaceNodeById(id);
		return (n == null ? null : workspaceFunnel.from(n));
	}

	//@PostInjection
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
					g.getCreated());
			setSafeProperty(n, G_WorkspaceFields.modified.name(),
					g.getModified());
			setSafeProperty(n, G_WorkspaceFields.active.name(), true);

			setSafeProperty(n, G_WorkspaceFields.json.name(), g.getJson());
			setSafeProperty(n, G_WorkspaceFields.title.name(), g.getTitle());
			tx.success();
		}
		return g;
	}

	@Override
	public G_PersistedGraph saveGraph(G_PersistedGraph pg) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_PersistedGraph getExistingGraph(String graphSeed, String userName,
			String timeStamp) {
		// TODO Auto-generated method stub
		return null;
	}

}
