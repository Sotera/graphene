package graphene.web.pages.experimental;

import graphene.web.model.ChatMessage;
import graphene.web.services.ChatConstants;
import graphene.web.services.ChatManager;

import java.text.DateFormat;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.TimeZone;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.PageActivationContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionAttribute;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

public class ChatDemo {
	@Property
	@PageActivationContext
	private String room;

	@SessionAttribute(ChatConstants.CHAT_USER_SESSION_KEY)
	@Property
	private String user;
	
	@Property
	private String currentUser;
	
	@Property
	private String currentRoom;
	
	@Property
	private ChatMessage chatMessage;
	
	@Property
	private Set<String> updatedUsers;
	
	@Property
	private String outMessage;
	
	@Inject
	private ChatManager chatManager;

	@Inject
	private Block messageBlock;

	@Inject
	private Block usersBlock;
	
	@InjectComponent
	private Zone chatFormZone;
	
	Object onSuccessFromLoginForm() {
		return this;
	}
	
	Object onChangeRoom(String newRoom) {
		String oldRoom = this.room;
		chatManager.leaveRoom(oldRoom, user);
		this.room = newRoom;
		return this;
	}
	
	Object onSuccessFromChatForm() {
		chatManager.sendRoomMessage(room, user, outMessage);
		this.outMessage = null;
		return chatFormZone.getBody();
	}
	
	Object onLogout() {
		chatManager.leaveRoom(room, user);
		user = null;
		room = null;
		return this;
	}
	
	void setupRender() {
		if (room != null && user != null) {
			chatManager.joinRoom(room, user);
		}
	}
	
	public boolean isLoggedIn() {
		return user != null && room != null;
	}
	
	public Block onUsersChange(Set<String> users) {
		updatedUsers = users;
		return usersBlock;
	}
	
	public Block onChatMessage(ChatMessage chatMessage) {
		this.chatMessage = chatMessage;
		return messageBlock;
	}
	
	public Collection<String> getRooms() {
		return chatManager.getRooms();
	}
	
	public Collection<String> getRoomUsers() {
		return chatManager.getRoomUsers(room);
	}
	
	public boolean isOtherRoom() {
		return !currentRoom.equals(room);
	}
	
	public Collection<String> getMessageTopics() {
		return Arrays.asList(
			String.format("rooms/%s/messages", room),
			String.format("users/%s/messages", user)
		);
	}
	
	public List<ChatMessage> getRecentMessages() {
		return chatManager.getRecentMessages(room);
	}
	
	public Format getTimeFormat() {
		DateFormat format = new SimpleDateFormat("HH:mm");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		return format;
	}
	
	public String userColor(String user) {
		int remainder = Math.abs(user.hashCode()) % (256 * 256 * 256);
		String hex = Integer.toHexString(remainder);
		for (int i = hex.length(); i < 6; ++i) {
			hex = "0" + hex;
		}
		return "#" + hex;
	}
}
