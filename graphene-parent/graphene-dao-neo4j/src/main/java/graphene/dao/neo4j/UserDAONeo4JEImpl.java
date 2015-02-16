package graphene.dao.neo4j;

import graphene.business.commons.exception.DataAccessException;
import graphene.dao.UserDAO;
import graphene.dao.neo4j.annotations.UserGraph;
import graphene.model.idl.AuthenticationException;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserFields;
import graphene.util.ExceptionUtil;
import graphene.util.crypto.PasswordHash;
import graphene.util.validator.ValidationUtils;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.joda.time.DateTime;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;

public class UserDAONeo4JEImpl extends GenericUserSpaceDAONeo4jE implements UserDAO {

	// all the fields for any classes we can search on here
	@Inject
	Logger logger;

	PasswordHash passwordHasher = new PasswordHash();

	public UserDAONeo4JEImpl() {
		// TODO Auto-generated constructor stub
	}

	public UserDAONeo4JEImpl(@UserGraph final Neo4JEmbeddedService service) {
		n4jService = service;
	}

	@Override
	public long countUsers(final String partialName) {
		long n = 0;
		try (Transaction tx = beginTx()) {
			final String queryString = "start n = node(*) where n." + G_UserFields.username + " =~ '.*" + partialName
					+ ".*' return count(*) as c;";
			final Map<String, Object> parameters = new HashMap<String, Object>();
			final ResourceIterator<Object> resultIterator = n4jService.getExecutionEngine()
					.execute(queryString, parameters).columnAs("c");
			n = (long) resultIterator.next();
			tx.success();
		}
		return n;

	}

	private Node createUniqueUser(final String username, final String password) {
		Node result = null;
		ResourceIterator<Node> resultIterator = null;
		try (Transaction tx = beginTx()) {
			final String queryString = "MERGE (n:" + GrapheneNeo4JConstants.userLabel.name() + " {"
					+ G_UserFields.username.name() + ": {theUserName}}) RETURN n";
			final Map<String, Object> parameters = new HashMap<>();
			parameters.put("theUserName", username);
			resultIterator = n4jService.getExecutionEngine().execute(queryString, parameters).columnAs("n");
			result = resultIterator.next();
			setPasswordHash(result, password);
			resultIterator.close();
			tx.success();
			return result;
		}
	}

	@Override
	public boolean delete(final String id) {
		boolean success = false;
		try (Transaction tx = beginTx()) {
			final Node u = getUserNodeById(id);
			for (final Relationship r : u.getRelationships()) {
				r.delete();
			}
			u.delete();
			tx.success();
			success = true;
		} catch (final Exception e) {
			logger.error(ExceptionUtil.getRootCauseMessage(e));
		}
		return success;
	}

	@Override
	public boolean disable(final String id) {
		try (Transaction tx = beginTx()) {
			getUserNodeById(id).setProperty("active", false);
			tx.success();
		}
		return true;
	}

	@Override
	public boolean enable(final String id) {
		try (Transaction tx = beginTx()) {
			getUserNodeById(id).setProperty("active", true);
			tx.success();
		}
		return true;
	}

	@Override
	public List<G_User> getAll() {
		final List<G_User> list = new ArrayList<G_User>();
		try (Transaction tx = beginTx()) {
			try (ResourceIterator<Node> iter = n4jService.getGgo()
					.getAllNodesWithLabel(GrapheneNeo4JConstants.userLabel).iterator()) {
				while (iter.hasNext()) {
					final G_User d = userFunnel.from(iter.next());
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

	@Override
	public G_User getById(final String id) {
		final Node n = getUserNodeById(id);
		G_User g = null;
		try (Transaction tx = beginTx()) {
			g = userFunnel.from(n);
			tx.success();
		} catch (final Exception e) {
			logger.error(e.getMessage());

		}
		return g;
	}

	// @Override
	// public G_User getByUser(int id) {
	// Node n = getUserNodeById(id);
	// return (n == null ? null : createDetached(n));
	// }

	@Override
	public G_User getByUsername(final String username) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getPasswordHash(final String id, final String password) {
		String hash = null;
		try {
			hash = passwordHasher.createHash(password);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			logger.error("Error getting password hash for id " + id + " : " + e.getMessage());

		}
		return hash;
	}

	// @PostInjection
	@Override
	public void initialize() throws DataAccessException {
		if (n4jService.connectToGraph()) {
			logger.debug("Constructing UserDAONeo4JImpl hooked up to " + n4jService.getLocation());
			n4jService.createNewIndex(GrapheneNeo4JConstants.userLabel, G_UserFields.username.name());

		} else {
			logger.error("Could not connect to graph database");

			throw new DataAccessException("Could not connect to graph database");
		}
	}

	@Override
	public boolean isExistingId(final String id) {
		return (getUserNodeById(id) == null) ? false : true;
	}

	@Override
	public boolean isExistingUsername(final String username) {
		return (getByUsername(username) == null) ? false : true;
	}

	@Override
	public G_User loginAuthenticatedUser(final String id) {
		return userFunnel.from(getUserNodeById(id));
	}

	@Override
	public G_User loginUser(final String id, final String password) throws AuthenticationException {
		final Node node = getUserNodeById(id);
		try (Transaction tx = beginTx()) {

			if (node != null) {
				final String hash = (String) node.getProperty(G_UserFields.hashedpassword.name());
				try {
					if (passwordHasher.validatePassword(password, hash)) {

						node.setProperty(G_UserFields.lastlogin.name(), DateTime.now().getMillis());
					}
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					logger.error("Error logging in, could not validate password for " + id + " : " + e.getMessage());

				}
			} else {
				logger.error("No user with id " + id);
			}
			tx.success();

		} catch (final Exception e) {
			logger.error(e.getMessage());
			throw new AuthenticationException(e.getMessage());
		}
		return userFunnel.from(node);
	}

	// @Override
	// public G_User save(G_User d) {
	// try (Transaction tx = beginTx()) {
	// Node n = getUserNodeById(d.getId());
	// setSafeProperty(n, G_UserFields.created.name(), d.getCreated());
	// setSafeProperty(n, G_UserFields.active.name(), d.getActive());
	// setSafeProperty(n, G_UserFields.avatar.name(), d.getAvatar());
	// setSafeProperty(n, G_UserFields.email.name(), d.getEmail());
	// setSafeProperty(n, G_UserFields.fullname.name(), d.getFullname());
	// setSafeProperty(n, G_UserFields.lastlogin.name(), d.getLastlogin());
	// setSafeProperty(n, G_UserFields.username.name(), d.getUsername());
	// tx.success();
	// return userFunnel.from(n);
	// } catch (Exception e) {
	// logger.error(e.getMessage());
	// return null;
	// }
	// }

	/**
	 * Note this behaves slightly differently than the non detached version.
	 */
	@Override
	public G_User save(final G_User d) {

		G_User user = null;
		if (!ValidationUtils.isValid(d.getUsername())) {
			return null;
		}
		ResourceIterator<Node> resultIterator = null;
		try (Transaction tx = beginTx()) {
			final String queryString = "MERGE (n:" + GrapheneNeo4JConstants.userLabel.name() + " {"
					+ G_UserFields.username.name() + ": {var}}) RETURN n";
			final Map<String, Object> parameters = new HashMap<>();
			parameters.put("var", d.getUsername());
			resultIterator = n4jService.getExecutionEngine().execute(queryString, parameters).columnAs("n");

			final Node n = resultIterator.next();
			n.setProperty(G_UserFields.created.name(), DateTime.now().getMillis());
			n.setProperty(G_UserFields.active.name(), d.getActive());
			n.setProperty(G_UserFields.avatar.name(), d.getAvatar());
			n.setProperty(G_UserFields.email.name(), d.getEmail());
			n.setProperty(G_UserFields.fullname.name(), d.getFullname());
			n.setProperty(G_UserFields.lastlogin.name(), 0l);
			n.setProperty(G_UserFields.numberlogins.name(), d.getNumberlogins());
			n.setProperty(G_UserFields.username.name(), d.getUsername());

			user = userFunnel.from(n);// TODO: test to make sure this has all
										// the
										// fields we want.
			resultIterator.close();
			tx.success();
		}

		return user;
	}

	public Node setPasswordHash(final Node n, final String password) {
		// NOTE: Does not check to see if the password is a good one, only that
		// it is set.
		if (ValidationUtils.isValid(password)) {
			// then update
			try {
				final String hash = passwordHasher.createHash(password);
				logger.info("Updating password hash to " + hash);
				n.setProperty(G_UserFields.hashedpassword.name(), hash);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				logger.error("Error setting password hash for user " + n.getProperty(G_UserFields.username.name())
						+ " : " + e.getMessage());
			}

		}
		return n;
	}

	@Override
	public boolean updatePasswordHash(final String id, final String password) {
		try (Transaction tx = beginTx()) {
			setPasswordHash(getUserNodeById(id), password);
			tx.success();
		} catch (final Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

}
