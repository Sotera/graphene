/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

package graphene.dao.es.impl;

import graphene.dao.UserDAO;
import graphene.dao.es.BasicESDAO;
import graphene.dao.es.ESRestAPIConnection;
import graphene.dao.es.JestModule;
import graphene.model.idl.AuthenticationException;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.util.crypto.PasswordHash;
import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestResult;
import io.searchbox.core.Count;
import io.searchbox.core.CountResult;

import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class UserDAOESImpl extends BasicESDAO implements UserDAO {

	private static final String TYPE = "users";

	@Inject
	@Symbol(JestModule.ES_USER_INDEX)
	private String indexName;

	PasswordHash passwordHasher = new PasswordHash();
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_DELETE_USERS)
	private boolean enableDelete;

	@Inject
	public UserDAOESImpl(final ESRestAPIConnection c, final Logger logger) {
		auth = null;
		this.c = c;
		mapper = new ObjectMapper(); // can reuse, share globally
		this.logger = logger;
		setType(TYPE);
	}

	@Override
	public long countUsers(final String partialName) {
		final String query = new SearchSourceBuilder().query(
				QueryBuilders.wildcardQuery("username", "*" + partialName + "*")).toString();
		try {
			final CountResult result = c.getClient().execute(
					new Count.Builder().query(query).addIndex(indexName).addType(type)
							.setParameter("timeout", defaultESTimeout).build());
			return result.getCount().longValue();
		} catch (final Exception e) {
			logger.error("Error counting users " + e.getMessage());
		}
		return 0;
	}

	@Override
	public boolean delete(final String id) {
		if (enableDelete) {
			return super.delete(id);
		} else {
			logger.debug("Delete disabled.");
			return false;
		}
	}

	@Override
	public boolean disable(final String id) {
		final G_User user = getById(id);
		if (user == null) {
			return false;
		} else {
			user.setActive(false);
			final G_User save = save(user);
			if (save == null) {
				return false;
			} else {
				return true;
			}
		}
	}

	@Override
	public boolean enable(final String id) {
		final G_User user = getById(id);
		if (user == null) {
			return false;
		} else {
			user.setActive(true);
			final G_User save = save(user);
			if (save == null) {
				return false;
			} else {
				return true;
			}
		}
	}

	@Override
	public List<G_User> getAll() {
		return getAllResults().getSourceAsObjectList(G_User.class);
	}

	@Override
	public G_User getById(final String id) {
		return getResultsById(id).getSourceAsObject(G_User.class);
	}

	@Override
	public G_User getByUsername(final String username) {
		final JestResult jr = getByField("username", username);
		final G_User resultObject = jr.getSourceAsObject(G_User.class);
		if (ValidationUtils.isValid(resultObject)) {
			if (!ValidationUtils.isValid(resultObject.getId())) {
				logger.debug("Setting the id from the hit result");
				resultObject.setId(getIdByFirstHit(jr));
			}
			logger.debug("Found: " + resultObject.toString());
		} else {
			logger.warn("Could not get user with username " + username
					+ ".  This could be ok if just checking for existence.");
		}
		return resultObject;
	}

	@Override
	public String getPasswordHash(final String id, final String password) {
		String hash = null;
		try {
			hash = passwordHasher.createHash(password);
		} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
			logger.error("Error getting password hash for id " + id);
		}
		return hash;
	}

	@Override
	@PostInjection
	public void initialize() {
		setIndex(indexName);
		setType(TYPE);
		super.initialize();
	}

	@Override
	public boolean isExistingId(final String id) {
		logger.debug("Checking to see if user id exists");
		if (getById(id) != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public boolean isExistingUsername(final String username) {
		logger.debug("Checking to see if username exists");
		if (getByUsername(username) != null) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public G_User loginAuthenticatedUser(final String id) {
		final G_User user = getById(id);
		if (user != null) {
			user.setNumberlogins(user.getNumberlogins() + 1);
			user.setLastlogin(DateTime.now().getMillis());
			// user = save(user);
			// This should be faster, no waiting.
			saveObject(user, user.getId(), indexName, type, false);
		}
		return user;
	}

	@Override
	public G_User loginUser(final String id, final String password) throws AuthenticationException {
		G_User user = null;
		if (ValidationUtils.isValid(id, password)) {
			user = getById(id);
			if (ValidationUtils.isValid(user) && ValidationUtils.isValid(user.getHashedpassword())) {
				try {
					if (passwordHasher.validatePassword(password, user.getHashedpassword())) {
						if (user.getNumberlogins() == null) {
							user.setNumberlogins(1);
						} else {
							user.setNumberlogins(user.getNumberlogins() + 1);
						}
						user.setLastlogin(DateTime.now().getMillis());
						// This should be faster, no waiting.
						saveObject(user, user.getId(), indexName, type, false);
					}
				} catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
					logger.error("Error logging in, could not validate password for " + id);
				}
			} else {
				logger.warn("Could not get user with id " + id);
			}
		} else {
			logger.error("User id and/or password were invalid.");
		}
		return user;
	}

	@Override
	public G_User save(final G_User g) {
		G_User returnVal = null;
		if (ValidationUtils.isValid(g)) {
			g.setModified(getModifiedTime());
			if (g.getId() == null) {
				g.setId(saveObject(g, g.getId(), indexName, type, false));
			}
			saveObject(g, g.getId(), indexName, type, true);
			returnVal = g;
		} else {
			logger.error("Attempted to save a null user object!");
		}
		return returnVal;
	}

	/**
	 * Given a new object, the object won't have an id because it is auto
	 * generated by the backend upon first save. But we wish to embed this id
	 * into the structure of the POJO.
	 * 
	 * So we save once without an id, and let the backend create a new UID based
	 * on it's rules. Then we take the result of the save and get the generated
	 * UID, put it into the POJO with a setID, then save again. The second save
	 * will use the id from the previous save, and thus perform an update.
	 */

	@Override
	public boolean updatePasswordHash(final String id, final String hash) {
		final G_User user = getById(id);
		if (user == null) {
			logger.error("Could not find user for id " + id);
			return false;
		} else {
			logger.debug("Updating password hash for " + user.getUsername());
			user.setHashedpassword(hash);
			final G_User s = save(user);
			if (s == null) {
				logger.error("Problem saving updated password hash");
				return false;
			} else {
				logger.debug("Password hash saved.");
				return true;
			}
		}
	}

}
