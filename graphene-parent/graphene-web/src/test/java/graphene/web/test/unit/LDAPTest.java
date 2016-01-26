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

package graphene.web.test.unit;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.directory.Attributes;
import javax.naming.directory.SearchControls;
import javax.naming.directory.SearchResult;
import javax.naming.ldap.InitialLdapContext;
import javax.naming.ldap.LdapContext;

import org.apache.shiro.SecurityUtils;
import org.apache.shiro.config.IniSecurityManagerFactory;
import org.apache.shiro.util.Factory;
import org.testng.annotations.Test;

public class LDAPTest {

	private SearchControls getSimpleSearchControls() {
		final SearchControls searchControls = new SearchControls();
		searchControls.setSearchScope(SearchControls.SUBTREE_SCOPE);
		searchControls.setTimeLimit(30000);
		// String[] attrIDs = {"objectGUID"};
		// searchControls.setReturningAttributes(attrIDs);
		return searchControls;
	}

	@Test
	public void sample1() {
		final String ldapURL = "ldap://localhost:10389/dc=example,dc=com";
		final String username = "dn: uid=djue,ou=Users,dc=example,dc=com";
		final String password = "password";
		try {
			final Hashtable env = new Hashtable(11);
			env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
			env.put(Context.PROVIDER_URL, ldapURL);
			env.put(Context.SECURITY_AUTHENTICATION, "simple");
			env.put(Context.SECURITY_PRINCIPAL, username);
			env.put(Context.SECURITY_CREDENTIALS, password);
			env.put(Context.REFERRAL, "follow");

			// Bind anonymously
			final LdapContext ctx = new InitialLdapContext(env, null);
			final SearchControls controls = new SearchControls();
			controls.setSearchScope(SearchControls.SUBTREE_SCOPE);
			controls.setReturningAttributes(new String[0]);
			controls.setReturningObjFlag(true);
			final String base = "o=sevenSeas";
			final String filter = "(&(objectClass=inetOrgPerson)(uid={0}))";
			final String uid = username;
			ctx.search(base, filter, new String[] { uid }, controls);

			ctx.setRequestControls(null);
			final NamingEnumeration<?> namingEnum = ctx.search("ou=Users,dc=example,dc=com", "(objectclass=person)",
					getSimpleSearchControls());
			while (namingEnum.hasMore()) {
				final SearchResult result = (SearchResult) namingEnum.next();
				final Attributes attrs = result.getAttributes();
				System.out.println(attrs.get("cn"));

			}
			namingEnum.close();
		} catch (final Exception e) {
			e.printStackTrace();
		}

	}

	// @Test
	public void testLogin() {
		// 1.
		final Factory<org.apache.shiro.mgt.SecurityManager> factory = new IniSecurityManagerFactory(
				"classpath:shiro.ini");

		// 2.
		final org.apache.shiro.mgt.SecurityManager securityManager = factory.getInstance();

		// 3.
		SecurityUtils.setSecurityManager(securityManager);
	}
}
