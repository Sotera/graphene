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

package graphene.workflow.web.services;

import graphene.workflow.web.model.ChatMessage;

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
	static final class ChatRoom {
		Set<String> users = Collections.newSetFromMap(new ConcurrentHashMap<String, Boolean>());
		List<ChatMessage> recentMessages = Collections.synchronizedList(new LinkedList<ChatMessage>());
	}

	private final String ADMINISTRATOR = "admin";
	private static final List<String> ROOMS = Collections.unmodifiableList(Arrays.asList("cars", "cats", "dogs",
			"java", "tapestry"));

	private static final int RECENT_MESSAGE_COUNT = 15;
	private final ConcurrentMap<String, ChatRoom> chatRooms;

	private final AtmosphereBroadcaster broadcaster;

	public ChatManagerImpl(final AtmosphereBroadcaster broadcaster) {
		super();
		this.broadcaster = broadcaster;
		chatRooms = new ConcurrentHashMap<String, ChatRoom>();
		for (final String room : ROOMS) {
			chatRooms.put(room, new ChatRoom());
		}
	}

	@Override
	public List<ChatMessage> getRecentMessages(final String room) {
		return Collections.unmodifiableList(chatRooms.get(room).recentMessages);
	}

	@Override
	public Collection<String> getRooms() {
		return ROOMS;
	}

	@Override
	public Collection<String> getRoomUsers(final String room) {
		return sort(chatRooms.get(room).users);
	}

	@Override
	public void joinRoom(final String room, final String user) {
		final Set<String> roomUsers = chatRooms.get(room).users;
		final boolean added = roomUsers.add(user);
		if (added) {
			// broadcast an update for the room users
			final String topic = String.format("rooms/%s/users", room);
			broadcaster.broadcast(topic, sort(roomUsers));

			// send a message for user joining
			sendRoomMessage(room, ADMINISTRATOR, user + " joined the chat room");
		}
	}

	@Override
	public void leaveAllRooms(final String user) {
		for (final String room : getRooms()) {
			leaveRoom(room, user);
		}
	}

	@Override
	public void leaveRoom(final String room, final String user) {
		final Set<String> roomUsers = chatRooms.get(room).users;
		final boolean removed = roomUsers.remove(user);

		if (removed) {
			// broadcast an update for the room users
			final String topic = String.format("rooms/%s/users", room);
			broadcaster.broadcast(topic, getRoomUsers(room));

			// send a message for user joining
			sendRoomMessage(room, ADMINISTRATOR, user + " left the chat room");
		}
	}

	@Override
	public void sendPrivateMessage(final String fromUser, final String toUser, final String message) {
		final String topic = String.format("users/%s/messages", toUser);
		final ChatMessage chatMessage = new ChatMessage(fromUser, message);
		broadcaster.broadcast(topic, chatMessage);
	}

	@Override
	public void sendRoomMessage(final String room, final String user, final String message) {
		final String topic = String.format("rooms/%s/messages", room);
		final ChatMessage chatMessage = new ChatMessage(user, message);
		broadcaster.broadcast(topic, chatMessage);

		final List<ChatMessage> recentMessages = chatRooms.get(room).recentMessages;
		recentMessages.add(chatMessage);
		while (recentMessages.size() > RECENT_MESSAGE_COUNT) {
			recentMessages.remove(0);
		}
	}

	private Set<String> sort(final Set<String> set) {
		final Set<String> sorted = new TreeSet<String>(String.CASE_INSENSITIVE_ORDER);
		sorted.addAll(set);
		return sorted;
	}
}
