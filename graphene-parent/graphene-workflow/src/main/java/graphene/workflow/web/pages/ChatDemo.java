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

package graphene.workflow.web.pages;

import graphene.workflow.web.model.ChatMessage;
import graphene.workflow.web.services.ChatConstants;
import graphene.workflow.web.services.ChatManager;

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

	public Collection<String> getMessageTopics() {
		return Arrays.asList(String.format("rooms/%s/messages", room), String.format("users/%s/messages", user));
	}

	public List<ChatMessage> getRecentMessages() {
		return chatManager.getRecentMessages(room);
	}

	public Collection<String> getRooms() {
		return chatManager.getRooms();
	}

	public Collection<String> getRoomUsers() {
		return chatManager.getRoomUsers(room);
	}

	public Format getTimeFormat() {
		final DateFormat format = new SimpleDateFormat("HH:mm");
		format.setTimeZone(TimeZone.getTimeZone("UTC"));
		return format;
	}

	public boolean isLoggedIn() {
		return (user != null) && (room != null);
	}

	public boolean isOtherRoom() {
		return !currentRoom.equals(room);
	}

	Object onChangeRoom(final String newRoom) {
		final String oldRoom = room;
		chatManager.leaveRoom(oldRoom, user);
		room = newRoom;
		return this;
	}

	public Block onChatMessage(final ChatMessage chatMessage) {
		this.chatMessage = chatMessage;
		return messageBlock;
	}

	Object onLogout() {
		chatManager.leaveRoom(room, user);
		user = null;
		room = null;
		return this;
	}

	Object onSuccessFromChatForm() {
		chatManager.sendRoomMessage(room, user, outMessage);
		outMessage = null;
		return chatFormZone.getBody();
	}

	Object onSuccessFromLoginForm() {
		return this;
	}

	public Block onUsersChange(final Set<String> users) {
		updatedUsers = users;
		return usersBlock;
	}

	void setupRender() {
		if ((room != null) && (user != null)) {
			chatManager.joinRoom(room, user);
		}
	}

	public String userColor(final String user) {
		final int remainder = Math.abs(user.hashCode()) % (256 * 256 * 256);
		String hex = Integer.toHexString(remainder);
		for (int i = hex.length(); i < 6; ++i) {
			hex = "0" + hex;
		}
		return "#" + hex;
	}
}
