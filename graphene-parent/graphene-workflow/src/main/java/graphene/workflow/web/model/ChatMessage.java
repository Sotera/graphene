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
