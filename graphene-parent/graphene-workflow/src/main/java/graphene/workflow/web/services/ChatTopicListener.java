package graphene.workflow.web.services;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.atmosphere.cpr.AtmosphereResource;
import org.lazan.t5.atmosphere.services.TopicListener;

public class ChatTopicListener implements TopicListener {
	private static final Pattern ROOM_PATTERN = Pattern.compile("rooms/(.*)/messages");
	private final ChatManager chatManager;
	
	public ChatTopicListener(ChatManager chatManager) {
		super();
		this.chatManager = chatManager;
	}
	
	@Override
	public void onConnect(AtmosphereResource resource, String topic) {
	}
	
	@Override
	public void onDisconnect(AtmosphereResource resource, String topic) {
		Matcher matcher = ROOM_PATTERN.matcher(topic);
		if (matcher.matches()) {
			HttpSession session = resource.getRequest().getSession(false);
			if (session != null) {
				String chatUser = (String) session.getAttribute(ChatConstants.CHAT_USER_SESSION_KEY);
				if (chatUser != null) {
					String room = matcher.group(1);
					chatManager.leaveRoom(room, chatUser);
				}
			}
		}
	}
}
