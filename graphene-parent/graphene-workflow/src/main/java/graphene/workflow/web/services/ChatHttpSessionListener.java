package graphene.workflow.web.services;

import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import org.apache.tapestry5.TapestryFilter;
import org.apache.tapestry5.ioc.Registry;

/**
 * Cleanup chat sessions when HTTPSession is destroyed 
 */
public class ChatHttpSessionListener implements HttpSessionListener {
	@Override
	public void sessionCreated(HttpSessionEvent se) {}

	@Override
	public void sessionDestroyed(HttpSessionEvent se) {
		HttpSession session = se.getSession();
		String chatUser = (String) session.getAttribute(ChatConstants.CHAT_USER_SESSION_KEY);
		if (chatUser != null) {
			ServletContext context = session.getServletContext();
			Registry registry = (Registry) context.getAttribute(TapestryFilter.REGISTRY_CONTEXT_NAME);
			ChatManager chatManager = registry.getService(ChatManager.class);
			
			chatManager.leaveAllRooms(chatUser);
		}
	}
}
