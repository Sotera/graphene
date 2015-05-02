package graphene.web.components.security;

import graphene.model.idl.G_User;
import graphene.web.pages.Index;

import java.io.IOException;

import org.apache.shiro.SecurityUtils;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.RequestGlobals;

public class Logout {
	@SessionState(create = false)
	protected G_User user;
	@Inject
	RequestGlobals r;
	private boolean userExists;

	Object onActionFromLogout() {
		user = null;
		SecurityUtils.getSubject().logout();
		try {
			r.getRequest().getSession(false).invalidate();
			r.getHTTPServletResponse().setHeader("REMOTE_USER", "");
			r.getHTTPServletResponse().sendError(401);
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return Index.class;
	}
}
