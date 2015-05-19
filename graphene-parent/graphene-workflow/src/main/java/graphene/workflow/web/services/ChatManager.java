package graphene.workflow.web.services;

import graphene.workflow.web.model.ChatMessage;

import java.util.Collection;
import java.util.List;

public interface ChatManager {
	List<ChatMessage> getRecentMessages(String room);

	Collection<String> getRooms();

	Collection<String> getRoomUsers(String room);

	void joinRoom(String room, String user);

	void leaveAllRooms(String chatUser);

	void leaveRoom(String room, String user);

	void sendPrivateMessage(String fromUser, String toUser, String message);

	void sendRoomMessage(String room, String user, String message);
}
