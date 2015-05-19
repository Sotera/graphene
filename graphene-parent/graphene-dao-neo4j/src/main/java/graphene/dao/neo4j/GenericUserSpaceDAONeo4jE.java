package graphene.dao.neo4j;

import graphene.business.commons.exception.DataAccessException;
import graphene.dao.neo4j.funnel.G_UserSpaceRelationshipTypeFunnel;
import graphene.dao.neo4j.funnel.GroupFunnel;
import graphene.dao.neo4j.funnel.UserFunnel;
import graphene.dao.neo4j.funnel.WorkspaceFunnel;
import graphene.model.idl.G_GroupFields;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.model.idl.G_UserFields;
import graphene.model.idl.G_WorkspaceFields;
import graphene.util.ExceptionUtil;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.slf4j.Logger;

public class GenericUserSpaceDAONeo4jE {
	protected G_UserSpaceRelationshipTypeFunnel relfunnel = new G_UserSpaceRelationshipTypeFunnel();

	@Inject
	protected GroupFunnel groupFunnel;
	@Inject
	protected UserFunnel userFunnel;
	@Inject
	protected WorkspaceFunnel workspaceFunnel;

	@Inject
	protected G_PropertyKeyTypeAccess propertyKeyTypeAccess;
	@Inject
	Logger logger;

	protected Neo4JEmbeddedService n4jService;

	public Transaction beginTx() {
		return n4jService.beginTx();
	}

	protected boolean dropIndex(final Label x) {
		// START SNIPPET: dropIndex
		try (Transaction tx = n4jService.getGraphDb().beginTx()) {
			for (final IndexDefinition indexDefinition : n4jService.getGraphDb().schema().getIndexes(x)) {
				indexDefinition.drop();
			}

			tx.success();
		} catch (final Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
		// END SNIPPET: dropIndex
	}

	protected Node getGroupNode(final String id) {
		Node n = null;
		try (Transaction tx = beginTx()) {
			for (final Node node : n4jService.getGraphDb().findNodesByLabelAndProperty(
					GrapheneNeo4JConstants.groupLabel, G_GroupFields.id.name(), id)) {
				n = node;
			}
			tx.success();
		}
		return n;
	}

	protected Node getGroupNodeByGroupname(final String groupname) {
		Node n = null;
		try (Transaction tx = beginTx()) {
			for (final Node node : n4jService.getGraphDb().findNodesByLabelAndProperty(
					GrapheneNeo4JConstants.groupLabel, G_GroupFields.name.name(), groupname)) {
				n = node;
			}
			tx.success();
		}
		return n;
	}

	protected Node getUserNodeById(final String id) {
		Node n = null;
		try (Transaction tx = beginTx()) {
			for (final Node node : n4jService.getGraphDb().findNodesByLabelAndProperty(
					GrapheneNeo4JConstants.userLabel, G_UserFields.id.name(), id)) {
				n = node;
			}
			tx.success();
		} catch (final Exception e) {
			logger.error(ExceptionUtil.getRootCauseMessage(e));
		}
		if (n == null) {
			logger.warn("Could not find a user with id '" + id + "'");
		}
		return n;
	}

	protected Node getUserNodeByUsername(final String username) {
		Node n = null;
		try (Transaction tx = beginTx()) {
			for (final Node node : n4jService.getGraphDb().findNodesByLabelAndProperty(
					GrapheneNeo4JConstants.userLabel, G_UserFields.username.name(), username)) {
				n = node;
			}
			tx.success();
		} catch (final Exception e) {
			logger.error(ExceptionUtil.getRootCauseMessage(e));
		}
		if (n == null) {
			logger.warn("Could not find a user with id '" + username + "'");
		}
		return n;
	}

	protected Node getWorkspaceNodeById(final String id) {
		Node n = null;
		try (Transaction tx = beginTx()) {
			for (final Node node : n4jService.getGraphDb().findNodesByLabelAndProperty(
					GrapheneNeo4JConstants.workspaceLabel, G_WorkspaceFields.id.name(), id)) {
				n = node;
			}
			tx.success();
		} catch (final Exception e) {
			logger.error(ExceptionUtil.getRootCauseMessage(e));
		}
		if (n == null) {
			logger.warn("Could not find workspace with id '" + id + "'");
		}
		return n;
	}

	public void initialize() throws DataAccessException {
		// TODO Auto-generated method stub

	}

	/**
	 * 
	 * @param n
	 * @param k
	 * @param o
	 * @return true if a property was set, false if it already existed.
	 */
	protected boolean setPropertyIfUndefined(final Node n, final String k, final Object o) {
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
	protected boolean setSafeProperty(final Node n, final String k, final Object o) {
		if (o != null) {
			n.setProperty(k, o);
			return true;
		}
		return false;
	}
}
