package graphene.dao.neo4j;

import graphene.model.idl.G_EdgeTypeAccess;
import graphene.model.idl.G_NodeTypeAccess;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.model.idl.G_UserFields;
import graphene.util.ExceptionUtil;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.slf4j.Logger;

public class GenericUserSpaceDAONeo4jE {
	@Inject
	protected G_EdgeTypeAccess edgeTypeAccess;

	@Inject
	protected G_NodeTypeAccess nodeTypeAccess;

	@Inject
	protected G_PropertyKeyTypeAccess propertyKeyTypeAccess;
	@Inject
	Logger logger;

	public Transaction beginTx() {
		return n4jService.beginTx();
	}

	protected Neo4JEmbeddedService n4jService;

	protected Node getUserNodeById(int id) {
		Node n = null;
		try (Transaction tx = beginTx()) {
			for (Node node : n4jService.getGraphDb()
					.findNodesByLabelAndProperty(
							GrapheneNeo4JConstants.userLabel,
							G_UserFields.id.name(), id)) {
				n = node;
			}
			tx.success();
		} catch (Exception e) {
			logger.error(ExceptionUtil.getRootCauseMessage(e));
		}
		if (n == null) {
			logger.warn("Could not find a user with id '" + id + "'");
		}
		return n;
	}

	protected Node getUserNodeByUsername(String username) {
		Node n = null;
		try (Transaction tx = beginTx()) {
			for (Node node : n4jService.getGraphDb()
					.findNodesByLabelAndProperty(
							GrapheneNeo4JConstants.userLabel,
							G_UserFields.username.name(), username)) {
				n = node;
			}
			tx.success();
		} catch (Exception e) {
			logger.error(ExceptionUtil.getRootCauseMessage(e));
		}
		if (n == null) {
			logger.warn("Could not find a user with id '" + username + "'");
		}
		return n;
	}

	/**
	 * 
	 * @param n
	 * @param k
	 * @param o
	 * @return true if a property was set, false if it already existed.
	 */
	protected boolean setPropertyIfUndefined(Node n, String k, Object o) {
		if (!n.hasProperty(k)) {
			n.setProperty(k, o);
			return true;
		}
		return false;
	}

	/**
	 * For use with update commands. Will set the property if the Object
	 * parameter is not null.
	 * 
	 * @param n
	 * @param k
	 * @param o
	 * @return
	 */
	protected boolean setSafeProperty(Node n, String k, Object o) {
		if (o != null) {
			n.setProperty(k, o);
			return true;
		}
		return false;
	}

	protected boolean dropIndex(Label x) {
		// START SNIPPET: dropIndex
		try (Transaction tx = n4jService.getGraphDb().beginTx()) {
			for (IndexDefinition indexDefinition : n4jService.getGraphDb()
					.schema().getIndexes(x)) {
				indexDefinition.drop();
			}

			tx.success();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
		// END SNIPPET: dropIndex
	}
}
