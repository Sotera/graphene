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

package graphene.security.tomcat.preaa;

import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;

import java.util.Set;

import org.apache.avro.AvroRemoteException;
import org.apache.catalina.realm.GenericPrincipal;
import org.apache.shiro.authc.AuthenticationException;
import org.apache.shiro.authc.AuthenticationInfo;
import org.apache.shiro.authc.AuthenticationToken;
import org.apache.shiro.authc.ExpiredCredentialsException;
import org.apache.shiro.authc.LockedAccountException;
import org.apache.shiro.authc.SimpleAccount;
import org.apache.shiro.authc.UsernamePasswordToken;
import org.apache.shiro.authc.credential.CredentialsMatcher;
import org.apache.shiro.authz.AuthorizationException;
import org.apache.shiro.authz.AuthorizationInfo;
import org.apache.shiro.realm.AuthorizingRealm;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.util.CollectionUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;
import org.slf4j.Logger;
import org.tynamo.security.services.SecurityService;

/**
 * A Realm is Shiro terminology for a specialized DAO type object that knows how
 * to deal with Users, Roles, and Permissions. In online examples this is
 * sometimes done by called a different DAO for each of those object types.
 * 
 * With Graphene, we already had a similar object called G_UserDataAccess, which
 * talks to the appropriate DAOs. (G_UserDataAccess also does much more, and
 * deals with workspaces and user groups, etc)
 * 
 * @author djue
 * 
 */
public class PreAASecurityRealm extends AuthorizingRealm {
	@Inject
	private G_UserDataAccess userDataAccess;

	private Logger logger;

	static final String REALM_NAME = "preAARealm";
	private RequestGlobals rq;

	@Inject
	private SecurityService ss;

	public PreAASecurityRealm(final Logger logger, final RequestGlobals rq) {
		this.rq = rq;
		this.logger = logger;

		logger.debug("Created " + REALM_NAME);
		setName(REALM_NAME);
		setCredentialsMatcher(new CredentialsMatcher() {
			@Override
			public boolean doCredentialsMatch(final AuthenticationToken token, final AuthenticationInfo info) {

				logger.debug("doCredentialsMatch " + rq.getHTTPServletRequest().getRemoteUser());

				return true;
			}
		});
	}

	@Override
	public void checkRoles(final PrincipalCollection principal, final String... roles) throws AuthorizationException {

		ss.hasRole("admin");
		super.checkRoles(principal, roles);
	}

	@Override
	protected AuthenticationInfo doGetAuthenticationInfo(final AuthenticationToken authToken)
			throws AuthenticationException {

		logger.debug("doGetAuthenticationInfo " + authToken.getPrincipal());
		// return null;
		final UsernamePasswordToken upToken = (UsernamePasswordToken) authToken;
		G_User g_User = null;
		SimpleAccount account = null;
		try {
			g_User = userDataAccess.getByUsername(upToken.getUsername());
			final Set<String> roleNames = CollectionUtils.asSet((String[]) null);
			account = new SimpleAccount(g_User.getUsername(), "password", getName(), roleNames, null);
		} catch (final AvroRemoteException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (account != null) {

			if (account.isLocked()) {
				throw new LockedAccountException("Account [" + account + "] is locked.");
			}
			if (account.isCredentialsExpired()) {
				final String msg = "The credentials for account [" + account + "] are expired";
				throw new ExpiredCredentialsException(msg);
			}

		} else {
			logger.error("user was null");
		}

		return account;
	}

	@Override
	protected AuthorizationInfo doGetAuthorizationInfo(final PrincipalCollection principals) {

		logger.debug("doGetAuthorizationInfo " + principals.asList());
		// return null;
		final Set<String> roleNames = CollectionUtils.asSet((String[]) null);
		final SimpleAccount simpleAccount = new SimpleAccount(getUsername(principals), "password", getName(),
				roleNames, null);
		return simpleAccount;
	}

	protected String getUsername(final PrincipalCollection principals) {
		return getAvailablePrincipal(principals).toString();
	}

	@Override
	public boolean hasRole(final PrincipalCollection principal, final String roleIdentifier) {
		// TODO Auto-generated method stub
		// return super.hasRole(principal, roleIdentifier);
		for (final Object p : principal.fromRealm(REALM_NAME)) {
			if (p instanceof GenericPrincipal) {
				final GenericPrincipal gp = (GenericPrincipal) p;
				for (final String r : gp.getRoles()) {
					if (r.equals(roleIdentifier)) {
						return true;
					}
				}
			}
		}
		return false;
	}
}
