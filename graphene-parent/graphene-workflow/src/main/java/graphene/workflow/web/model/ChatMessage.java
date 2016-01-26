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

package graphene.workflow.web.model;

import java.util.Date;

public class ChatMessage {
	private String fromUser;
	private Date time;
	private String message;
	
	public ChatMessage(String fromUser, String message) {
		super();
		this.fromUser = fromUser;
		this.message = message;
		this.time = new Date();
	}
	
	public String getFromUser() {
		return fromUser;
	}
	
	public String getMessage() {
		return message;
	}

	public Date getTime() {
		return time;
	}
}
