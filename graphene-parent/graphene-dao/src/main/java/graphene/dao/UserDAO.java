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

package graphene.dao;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.AuthenticationException;
import graphene.model.idl.G_User;

import java.util.List;

public interface UserDAO {

	/**
	 * Count the number of users whose username matches partialName
	 * 
	 * @param partialName
	 * @return the number of usernames that match
	 */
	long countUsers(String partialName);

	/**
	 * Deletes the user found by username, and also deletes ALL relationships to
	 * the user.
	 * 
	 * @param username
	 */
	boolean delete(String id);

	/**
	 * Disable User by Username
	 * 
	 * @param id
	 * @return
	 */
	boolean disable(String id);

	/**
	 * Enable User by Username
	 * 
	 * @param id
	 * @return
	 */
	boolean enable(String id);

	/**
	 * Get All Users
	 * 
	 * @return
	 */
	List<G_User> getAll();

	/**
	 * Find and return a user, do not create
	 * 
	 * @param id
	 * @return
	 */
	G_User getById(String id);

	/**
	 * Find and return a user, do not create
	 * 
	 * @param username
	 * @return
	 */
	G_User getByUsername(String username);

	/**
	 * 
	 * @param username
	 * @param password
	 * @return Return the hash of the password, or null if there was an error.
	 */
	String getPasswordHash(String id, String password);

	void initialize() throws DataAccessException;

	/**
	 * We need this kind of method for times where we are checking if the
	 * username is taken.
	 * 
	 * @param username
	 * @return
	 */
	boolean isExistingUsername(String username);

	boolean isExistingId(String id);

	G_User loginAuthenticatedUser(String id);

	G_User loginUser(String id, String password) throws AuthenticationException;

	/**
	 * 
	 * @param person
	 * @return the user with any updates that the DAO might have done.
	 */
	G_User save(G_User user);

	boolean updatePasswordHash(String id, String passwordHash);

}
