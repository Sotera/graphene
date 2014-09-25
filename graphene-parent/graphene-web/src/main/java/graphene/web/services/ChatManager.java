package graphene.web.services;

import graphene.web.model.ChatMessage;

import java.util.Collection;
import java.util.List;

public interface ChatManager {
	Collection<String> getRooms();
	Collection<String> getRoomUsers(String room);
	void joinRoom(String room, String user);
	void leaveRoom(String room, String user);
	void sendRoomMessage(String room, String user, String message);
	void sendPrivateMessage(String fromUser, String toUser, String message);
	List<ChatMessage> getRecentMessages(String room);
	void leaveAllRooms(String chatUser);
}
