package graphene.dao.neo4j;

import graphene.dao.UserDAO;
import graphene.dao.neo4j.annotations.UserGraph;
import graphene.model.idl.AuthenticationException;
import graphene.model.idl.G_GroupFields;
import graphene.model.idl.G_RelationshipType;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserFields;
import graphene.model.query.DataAccessException;
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
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Path;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.ResourceIterator;
import org.neo4j.graphdb.Transaction;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.TraversalDescription;
import org.neo4j.graphdb.traversal.Traverser;
import org.slf4j.Logger;

public class UserDAONeo4JEImpl extends GenericUserSpaceDAONeo4jE implements
		UserDAO {

	// all the fields for any classes we can search on here
	@Inject
	Logger logger;

	PasswordHash passwordHasher = new PasswordHash();

	public UserDAONeo4JEImpl() {
		// TODO Auto-generated constructor stub
	}

	public UserDAONeo4JEImpl(@UserGraph Neo4JEmbeddedService service) {
		this.n4jService = service;
	}

	@Override
	public long countUsers(String partialName) {
		long n = 0;
		try (Transaction tx = beginTx()) {
			String queryString = "start n = node(*) where n."
					+ G_UserFields.username + " =~ '.*" + partialName
					+ ".*' return count(*) as c;";
			Map<String, Object> parameters = new HashMap<String, Object>();
			ResourceIterator<Object> resultIterator = n4jService
					.getExecutionEngine().execute(queryString, parameters)
					.columnAs("c");
			n = (long) resultIterator.next();
			tx.success();
		}
		return n;

	}

	private G_User createDetached(Node u) {
		G_User d = null;
		if (u != null) {
			try (Transaction tx = beginTx()) {
				d = new G_User();
				d.setAccountcreated(new DateTime(u.getProperty(
						G_UserFields.accountcreated.name(), 0l)).getMillis());
				d.setActive((boolean) u.getProperty(G_UserFields.active.name(),
						true));
				d.setAvatar((String) u.getProperty(G_UserFields.avatar.name(),
						""));
				d.setEmail((String) u.getProperty(G_UserFields.email.name(),
						"no email"));
				d.setFullname((String) u.getProperty(
						G_UserFields.fullname.name(), "no name"));

				d.setHashedpassword((String) u.getProperty(
						G_UserFields.hashedpassword.name(),
						"no hashed password"));

				d.setLastlogin(new DateTime(u.getProperty(
						G_UserFields.lastlogin.name(), 0l)).getMillis());

				d.setNumberlogins((int) u.getProperty(
						G_UserFields.numberlogins.name(), 0));
				d.setUsername((String) u.getProperty(
						G_UserFields.username.name(), "username"));
				tx.success();
			}

		}
		return d;
	}

	/**
	 * Note this behaves slightly differently than the non detached version.
	 */
	@Override
	public G_User createOrUpdate(G_User d) {

		G_User user = null;
		if (!ValidationUtils.isValid(d.getUsername())) {
			return null;
		}
		ResourceIterator<Node> resultIterator = null;
		try (Transaction tx = beginTx()) {
			String queryString = "MERGE (n:"
					+ GrapheneNeo4JConstants.userLabel.name() + " {"
					+ G_UserFields.username.name() + ": {var}}) RETURN n";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("var", d.getUsername());
			resultIterator = n4jService.getExecutionEngine()
					.execute(queryString, parameters).columnAs("n");

			Node n = resultIterator.next();
			n.setProperty(G_UserFields.accountcreated.name(), DateTime.now()
					.getMillis());
			n.setProperty(G_UserFields.active.name(), d.getActive());
			n.setProperty(G_UserFields.avatar.name(), d.getAvatar());
			n.setProperty(G_UserFields.email.name(), d.getEmail());
			n.setProperty(G_UserFields.fullname.name(), d.getFullname());
			n.setProperty(G_UserFields.lastlogin.name(), 0l);
			n.setProperty(G_UserFields.numberlogins.name(), d.getNumberlogins());
			n.setProperty(G_UserFields.username.name(), d.getUsername());

			user = createDetached(n);// TODO: test to make sure this has all the
										// fields we want.
			resultIterator.close();
			tx.success();
		}

		return user;
	}

	private Node createUniqueUser(String username, String password) {
		Node result = null;
		ResourceIterator<Node> resultIterator = null;
		try (Transaction tx = beginTx()) {
			String queryString = "MERGE (n:"
					+ GrapheneNeo4JConstants.userLabel.name() + " {"
					+ G_UserFields.username.name()
					+ ": {theUserName}}) RETURN n";
			Map<String, Object> parameters = new HashMap<>();
			parameters.put("theUserName", username);
			resultIterator = n4jService.getExecutionEngine()
					.execute(queryString, parameters).columnAs("n");
			result = resultIterator.next();
			setPasswordHash(result, password);
			resultIterator.close();
			tx.success();
			return result;
		}
	}

	@Override
	public boolean delete(String username) {
		boolean success = false;
		try (Transaction tx = beginTx()) {
			Node u = getUserNodeByUsername(username);
			for (Relationship r : u.getRelationships()) {
				r.delete();
			}
			u.delete();
			tx.success();
			success = true;
		} catch (Exception e) {
			logger.error(ExceptionUtil.getRootCauseMessage(e));
		}
		return success;
	}

	@Override
	public boolean disable(String username) {
		try (Transaction tx = beginTx()) {
			getUserNodeByUsername(username).setProperty("active", false);
			tx.success();
		}
		return true;
	}

	@Override
	public boolean enable(String username) {
		try (Transaction tx = beginTx()) {
			getUserNodeByUsername(username).setProperty("active", true);
			tx.success();
		}
		return true;
	}

	@Override
	public List<G_User> getAllUsers() {
		List<G_User> list = new ArrayList<G_User>();
		try (Transaction tx = beginTx()) {
			try (ResourceIterator<Node> iter = n4jService.getGgo()
					.getAllNodesWithLabel(GrapheneNeo4JConstants.userLabel)
					.iterator()) {
				while (iter.hasNext()) {
					G_User d = createDetached(iter.next());
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
	public List<G_User> getByGroups(String groupName) {
		List<G_User> list = new ArrayList<G_User>();
		try (Transaction tx = beginTx();
				ResourceIterator<Node> g = n4jService
						.getGraphDb()
						.findNodesByLabelAndProperty(
								GrapheneNeo4JConstants.groupLabel,
								G_GroupFields.groupname.name(), groupName)
						.iterator()) {

			if (g.hasNext()) {
				Node j = g.next();
				TraversalDescription traversalDescription = n4jService
						.getGraphDb()
						.traversalDescription()
						.breadthFirst()
						.evaluator(
								Evaluators
										.includeWhereLastRelationshipTypeIs(G_RelationshipType.MEMBER_OF));

				Traverser traverser = traversalDescription.traverse(j);
				for (Path path : traverser) {
					Node n = path.endNode();
					G_User d = createDetached(n);
					if (d != null) {
						list.add(d);
					}
				}
				tx.success();
			}
		}
		return list;
	}

	/**
	 * this way seems to be the old way of using indexes, which means we need to
	 * grab the index names from the service. Do we grab them by the label, or
	 * by the field name, or both?
	 */
	@Override
	public List<G_User> getByPartialUsername(String partialName) {
		List<G_User> list = new ArrayList<G_User>();
		try (Transaction tx = beginTx()) {
			String queryString = "start n = node(*) where n."
					+ G_UserFields.username + " =~ '.*" + partialName
					+ ".*' return n, count(*) as c;";
			Map<String, Object> parameters = new HashMap<String, Object>();
			ResourceIterator<Object> resultIterator = n4jService
					.getExecutionEngine().execute(queryString, parameters)
					.columnAs("n");
			while (resultIterator.hasNext()) {
				list.add(createDetached((Node) resultIterator.next()));
			}
			tx.success();
		}
		return list;
	}

	@Override
	public List<G_User> getByPartialUsername(String partialName, int offset,
			int limit) {
		List<G_User> list = new ArrayList<G_User>();
		try (Transaction tx = beginTx()) {
			String queryString = "start n = node(*) where n."
					+ G_UserFields.username + " =~ '.*" + partialName
					+ ".*' return n order by n." + G_UserFields.username
					+ " skip " + offset + " limit " + limit;
			Map<String, Object> parameters = new HashMap<String, Object>();
			ResourceIterator<Object> resultIterator = n4jService
					.getExecutionEngine().execute(queryString, parameters)
					.columnAs("n");
			while (resultIterator.hasNext()) {
				list.add(createDetached((Node) resultIterator.next()));
			}
			tx.success();
		}
		return list;
	}

	@Override
	public G_User getByUsername(String username) {
		Node n = getUserNodeByUsername(username);
		return (n == null ? null : createDetached(n));
	}

	@PostInjection
	public void initialize() throws DataAccessException {
		if (n4jService.connectToGraph()) {
			logger.debug("Constructing UserDAONeo4JImpl hooked up to "
					+ n4jService.getLocation());
			n4jService.createNewIndex(GrapheneNeo4JConstants.userLabel,
					G_UserFields.username.name());

		} else {
			logger.error("Could not connect to graph database");

			throw new DataAccessException("Could not connect to graph database");
		}
	}

	@Override
	public boolean isExisting(String username) {
		return (getUserNodeByUsername(username) == null) ? false : true;
	}

	@Override
	public G_User loginUser(String username, String password)
			throws AuthenticationException {
		Node node = getUserNodeByUsername(username);
		try (Transaction tx = beginTx()) {

			if (node != null) {
				String hash = (String) node
						.getProperty(G_UserFields.hashedpassword.name());
				try {
					if (passwordHasher.validatePassword(password, hash)) {

						node.setProperty(G_UserFields.lastlogin.name(),
								DateTime.now(DateTimeZone.UTC).getMillis());
					}
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					logger.error("Error logging in, could not validate password for "
							+ username);
					e.printStackTrace();
				}
			} else {
				logger.error("No user with username " + username);
			}
			tx.success();

		} catch (Exception e) {
			logger.error(e.getMessage());
			throw new AuthenticationException(e.getMessage());
		}
		return createDetached(node);
	}

	public Node setPasswordHash(Node n, String password) {
		// NOTE: Does not check to see if the password is a good one, only that
		// it is set.
		if (ValidationUtils.isValid(password)) {
			// then update
			try {
				String hash = passwordHasher.createHash(password);
				logger.info("Updating password hash to " + hash);
				n.setProperty(G_UserFields.hashedpassword.name(), hash);
			} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
				logger.error("Error setting password hash for user "
						+ n.getProperty(G_UserFields.username.name()));
				e.printStackTrace();
			}

		}
		return n;
	}

	@Override
	public G_User save(G_User d) {
		try (Transaction tx = beginTx()) {
			Node n = getUserNodeByUsername(d.getUsername());
			setSafeProperty(n, G_UserFields.accountcreated.name(),
					d.getAccountcreated());
			setSafeProperty(n, G_UserFields.active.name(), d.getActive());
			setSafeProperty(n, G_UserFields.avatar.name(), d.getAvatar());
			setSafeProperty(n, G_UserFields.email.name(), d.getEmail());
			setSafeProperty(n, G_UserFields.fullname.name(), d.getFullname());
			setSafeProperty(n, G_UserFields.lastlogin.name(), d.getLastlogin());
			setSafeProperty(n, G_UserFields.username.name(), d.getUsername());
			tx.success();
			return createDetached(n);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return null;
		}
	}

	@Override
	public boolean updatePassword(String username, String password) {
		try (Transaction tx = beginTx()) {
			setPasswordHash(getUserNodeByUsername(username), password);
			tx.success();
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		return true;
	}

	@Override
	public String getPasswordHash(String username, String password) {
		String hash = null;
		try {
			hash = passwordHasher.createHash(password);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			logger.error("Error getting password hash for user " + username);
			e.printStackTrace();
		}
		return hash;
	}

}
