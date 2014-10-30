package graphene.web.services;

import graphene.web.model.ChatMessage;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.lazan.t5.atmosphere.services.AtmosphereBroadcaster;

public class ChatManagerImpl implements ChatManager {
	private final String ADMINISTRATOR = "admin";
	private static final List<String> ROOMS = Collections.unmodifiableList(Arrays.asList("cars", "cats", "dogs", "java", "tapestry"));
	private static final int RECENT_MESSAGE_COUNT = 15;
	
	private final ConcurrentMap<String, ChatRoom> chatRooms;
	private final AtmosphereBroadcaster broadcaster;
	
	public ChatManagerImpl(AtmosphereBroadcaster broadcaster) {
		super();
		this.broadcaster = broadcaster;
		this.chatRooms = new ConcurrentHashMap<String, ChatRoom>();
		for (String room : ROOMS) {
			chatRooms.put(room, new ChatRoom());
		}
	}

	@Override
	public Collection<String> getRooms() {
		return ROOMS;
	}

	@Override
	public Collection<String> getRoomUsers(String room) {
		return sort(chatRooms.get(room).users);
	}
	
	private Set<String> sort(Set<String> set) {
		Set<String> sorted = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		sorted.addAll(set);
		return sorted;
	}

	@Override
	public void joinRoom(String room, String user) {
		Set<String> roomUsers = chatRooms.get(room).users;
		boolean added = roomUsers.add(user);
		if (added) {
			// broadcast an update for the room users
			String topic = String.format("rooms/%s/users", room);
			broadcaster.broadcast(topic, sort(roomUsers));
			
			// send a message for user joining
			sendRoomMessage(room, ADMINISTRATOR, user + " joined the chat room");
		}
	}

	@Override
	public void leaveRoom(String room, String user) {
		Set<String> roomUsers = chatRooms.get(room).users;
		boolean removed = roomUsers.remove(user);
		
		if (removed) {
			// broadcast an update for the room users
			String topic = String.format("rooms/%s/users", room);
			broadcaster.broadcast(topic, getRoomUsers(room));
			
			// send a message for user joining
			sendRoomMessage(room, ADMINISTRATOR, user + " left the chat room");
		}
	}
	
	@Override
	public void leaveAllRooms(String user) {
		for (String room : getRooms()) {
			leaveRoom(room, user);
		}
	}

	@Override
	public void sendRoomMessage(String room, String user, String message) {
		String topic = String.format("rooms/%s/messages", room);
		ChatMessage chatMessage = new ChatMessage(user, message);
		broadcaster.broadcast(topic, chatMessage);
		
		List<ChatMessage> recentMessages = chatRooms.get(room).recentMessages;
		recentMessages.add(chatMessage);
		while (recentMessages.size() > RECENT_MESSAGE_COUNT) {
			recentMessages.remove(0);
		}
	}

	@Override
	public void sendPrivateMessage(String fromUser, String toUser, String message) {
		String topic = String.format("users/%s/messages", toUser);
		ChatMessage chatMessage = new ChatMessage(fromUser, message);
		broadcaster.broadcast(topic, chatMessage);
	}
	
	@Override
	public List<ChatMessage> getRecentMessages(String room) {
		return Collections.unmodifiableList(chatRooms.get(room).recentMessages);
	}
	
	static final class ChatRoom {
		Set<String> users = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
		List<ChatMessage> recentMessages = Collections.synchronizedList(new LinkedList<ChatMessage>());
	}
}
