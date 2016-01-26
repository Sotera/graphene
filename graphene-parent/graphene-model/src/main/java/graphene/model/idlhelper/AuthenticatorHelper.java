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

package graphene.model.idlhelper;

import graphene.business.commons.exception.BusinessException;
import graphene.model.idl.AuthenticationException;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.services.RequestGlobals;
import org.apache.tapestry5.services.Response;
import org.tynamo.security.internal.services.LoginContextService;

/**
 * Basic security interface for synchonizing the login/logout functions with the
 * creation/destruction of related session state objects. Some implementations
 * may also provide authentication. Others will integrate with 3rd Party
 * authenticators, like Shiro Realms.
 * 
 * @author karesti, djue
 */
public interface AuthenticatorHelper {

	/**
	 * Gets the logged user
	 * 
	 * @return User, the logged User
	 */
	// G_User getLoggedUser();

	/**
	 * Gets username of current user
	 */
	String getUsername();
	
	/**
	 * Checks if the current user is logged in
	 * 
	 * @return true if the user is logged in
	 */
	boolean isUserObjectCreated();

	/**
	 * Logs the user in.
	 * 
	 * @param username
	 * @param password
	 * @throws AuthenticationException
	 *             throw if an error occurs
	 * @throws AvroRemoteException
	 * @throws BusinessException
	 */
	@Log
	void login(String username, String password) throws AuthenticationException, AvroRemoteException, BusinessException;

	Object loginAndRedirect(String grapheneLogin, String graphenePassword, boolean grapheneRememberMe,
			RequestGlobals requestGlobals, LoginContextService loginContextService, Response response,
			Messages messages, AlertManager alertManager);

	boolean loginAuthenticatedUser(String username);

	/**
	 * Logs out the user
	 */
	@Log
	Object logout();
}
