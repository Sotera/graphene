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
