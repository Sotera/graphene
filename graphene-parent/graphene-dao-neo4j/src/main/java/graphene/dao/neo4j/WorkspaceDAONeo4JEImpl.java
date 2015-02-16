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

public class WorkspaceDAONeo4JEImpl extends GenericUserSpaceDAONeo4jE implements WorkspaceDAO {

	public WorkspaceDAONeo4JEImpl(@UserGraph final Neo4JEmbeddedService service) {
		n4jService = service;
	}

	@Override
	public long countWorkspaces(final String userId, final String partialName) {
		long n = 0;

		try (Transaction tx = beginTx()) {
			String filterOnTitle = "";
			{
				if (ValidationUtils.isValid(partialName)) {
					filterOnTitle = " where w." + G_WorkspaceFields.title + " =~ '.*" + partialName + ".*' ";
				}
			}
			final String queryString = "match (u:" + GrapheneNeo4JConstants.userLabel.name() + " {" + G_UserFields.id
					+ ":'" + userId + "'})-[r:" + G_UserSpaceRelationshipType.EDITOR_OF + "]-(w:"
					+ GrapheneNeo4JConstants.workspaceLabel + ") " + filterOnTitle + " return count(*) as c;";
			final Map<String, Object> parameters = new HashMap<String, Object>();
			final ResourceIterator<Object> resultIterator = n4jService.getExecutionEngine()
					.execute(queryString, parameters).columnAs("c");
			n = (long) resultIterator.next();
			tx.success();
		}

		return n;
	}

	@Override
	public boolean delete(final String workspaceId) {
		boolean success = false;
		try (Transaction tx = beginTx()) {
			final Node n = getWorkspaceNodeById(workspaceId);
			n.delete();
			tx.success();
			success = true;
		} catch (final Exception e) {
			success = false;
		}
		return success;
	}

	private boolean deleteWorkspace(final Node wNode) {
		final boolean success = false;
		try (Transaction tx = beginTx()) {
			for (final Relationship r : wNode.getRelationships()) {
				r.delete();
			}
			wNode.delete();
			tx.success();
			return true;
		} catch (final Exception e) {
			logger.error("Error deleting workspace.");
		}
		return success;
	}

	// @Override
	// public boolean deleteWorkspaceIfUnused(int workspaceId) {
	// boolean success = false;
	// Node wNode;
	// wNode = getWorkspaceNodeById(workspaceId);
	// if (wNode == null) {
	// success = false;
	// } else {
	// try (Transaction tx = beginTx()) {
	// // If the node has NO incoming edges of Editor, then delete it.
	// if (!wNode
	// .getRelationships(
	// Direction.INCOMING,
	// relfunnel
	// .to(G_UserSpaceRelationshipType.EDITOR_OF))
	// .iterator().hasNext()) {
	// success = deleteWorkspace(wNode);
	// }
	// tx.success();
	// } catch (Exception e) {
	// logger.error(e.getMessage());
	// success = false;
	// }
	// }
	// if (success) {
	// logger.debug("Deleted workspace " + workspaceId);
	// } else {
	// logger.debug("Did not delete workspace " + workspaceId);
	// }
	// return success;
	// }

	@Override
	public List<G_Workspace> getAll() {
		final List<G_Workspace> list = new ArrayList<G_Workspace>();
		try (Transaction tx = beginTx()) {
			try (ResourceIterator<Node> iter = n4jService.getGgo()
					.getAllNodesWithLabel(GrapheneNeo4JConstants.groupLabel).iterator()) {
				while (iter.hasNext()) {
					final G_Workspace d = workspaceFunnel.from(iter.next());
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

	// @Override
	// public G_Workspace getOrCreateWorkspace(G_Workspace g) {
	// Node n = null;
	// ResourceIterator<Node> resultIterator = null;
	// try (Transaction tx = beginTx()) {
	// String queryString = "MERGE (n:"
	// + GrapheneNeo4JConstants.workspaceLabel.name() + " {"
	// + G_WorkspaceFields.id.name() + ": {var}}) RETURN n";
	// Map<String, Object> parameters = new HashMap<>();
	// parameters.put("var", g.getId());
	// resultIterator = n4jService.getExecutionEngine()
	// .execute(queryString, parameters).columnAs("n");
	// n = resultIterator.next();
	// resultIterator.close();
	// setSafeProperty(n, G_WorkspaceFields.created.name(), DateTime.now()
	// .getMillis());
	// setSafeProperty(n, G_WorkspaceFields.modified.name(), DateTime
	// .now().getMillis());
	// setSafeProperty(n, G_WorkspaceFields.active.name(), true);
	// setSafeProperty(n, G_WorkspaceFields.json.name(), g.getJson());
	// setSafeProperty(n, G_WorkspaceFields.title.name(), g.getTitle());
	//
	// tx.success();
	// }
	// return workspaceFunnel.from(n);
	// }

	@Override
	public G_Workspace getById(final String id) {
		final Node n = getWorkspaceNodeById(id);
		return (n == null ? null : workspaceFunnel.from(n));
	}

	@Override
	public G_PersistedGraph getExistingGraph(final String graphSeed, final String username, final String timeStamp) {
		// TODO Auto-generated method stub
		return null;
	}

	// @PostInjection
	@Override
	public void initialize() {
		if (n4jService.connectToGraph()) {
			logger.debug("Constructing WorkspaceDAO hooked up to " + n4jService.getLocation());
			n4jService.createNewIndex(GrapheneNeo4JConstants.workspaceLabel, G_WorkspaceFields.id.name());
		} else {
			logger.error("Could not connect to graph, so WorkspaceDAO was not constructed.");
		}
	}

	@Override
	public G_Workspace save(final G_Workspace g) {
		ResourceIterator<Node> resultIterator = null;

		try (Transaction tx = beginTx()) {
			final String queryString = "MERGE (n:" + GrapheneNeo4JConstants.workspaceLabel.name() + " {"
					+ G_WorkspaceFields.id.name() + ": {var}}) RETURN n";
			final Map<String, Object> parameters = new HashMap<>();
			parameters.put("var", g.getId());
			resultIterator = n4jService.getExecutionEngine().execute(queryString, parameters).columnAs("n");
			final Node n = resultIterator.next();
			setSafeProperty(n, G_WorkspaceFields.created.name(), g.getCreated());
			setSafeProperty(n, G_WorkspaceFields.modified.name(), g.getModified());
			setSafeProperty(n, G_WorkspaceFields.active.name(), true);

			setSafeProperty(n, G_WorkspaceFields.title.name(), g.getTitle());
			tx.success();
		}
		return g;
	}

	@Override
	public G_PersistedGraph saveGraph(final G_PersistedGraph pg) {
		// TODO Auto-generated method stub
		return null;
	}

}
