package graphene.security.oam.preaa;

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
