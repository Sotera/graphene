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

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;

import org.apache.catalina.realm.GenericPrincipal;
import org.apache.shiro.subject.PrincipalCollection;
import org.apache.shiro.subject.SimplePrincipalCollection;
import org.apache.shiro.subject.Subject;
import org.apache.shiro.subject.SubjectContext;
import org.apache.shiro.web.mgt.DefaultWebSubjectFactory;
import org.apache.shiro.web.subject.WebSubjectContext;
import org.apache.shiro.web.subject.support.WebDelegatingSubject;
import org.slf4j.LoggerFactory;

public class PreAASubjectFactory extends DefaultWebSubjectFactory {
	org.slf4j.Logger logger = LoggerFactory.getLogger(PreAASubjectFactory.class);

	@Override
	public Subject createSubject(final SubjectContext context) {
		if (!(context instanceof WebSubjectContext)) {
			return super.createSubject(context);
		}
		final WebSubjectContext wsc = (WebSubjectContext) context;
		final org.apache.shiro.mgt.SecurityManager securityManager = wsc.resolveSecurityManager();
		final org.apache.shiro.session.Session session = wsc.resolveSession();
		final boolean sessionEnabled = wsc.isSessionCreationEnabled();
		PrincipalCollection principals = wsc.resolvePrincipals();
		final boolean authenticated = wsc.resolveAuthenticated();
		final String host = wsc.resolveHost();
		final ServletRequest request = wsc.resolveServletRequest();
		final ServletResponse response = wsc.resolveServletResponse();
		try {
			final HttpServletRequest httpRequest = (HttpServletRequest) request;
			final GenericPrincipal principal = (GenericPrincipal) httpRequest.getUserPrincipal();
			if (principal != null) {
				principals = new SimplePrincipalCollection(principal, PreAASecurityRealm.REALM_NAME);
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		final WebDelegatingSubject subject = new WebDelegatingSubject(principals, authenticated, host, session,
				sessionEnabled, request, response, securityManager);
		return subject;
	}
}
