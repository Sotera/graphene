package graphene.workflow.web.services;

import javax.servlet.http.HttpSession;

import org.atmosphere.cpr.AtmosphereResource;
import org.lazan.t5.atmosphere.services.TopicAuthorizer;

public class ChatTopicAuthorizer implements TopicAuthorizer {
	public boolean isAuthorized(AtmosphereResource resource, String topic) {
		HttpSession session = resource.getRequest().getSession(false);
		if (session != null) {
			String chatUser = (String) session.getAttribute(ChatConstants.CHAT_USER_SESSION_KEY);
			return chatUser != null;
		}
		return false;
	}
}
