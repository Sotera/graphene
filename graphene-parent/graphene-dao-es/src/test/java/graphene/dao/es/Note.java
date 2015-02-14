package graphene.dao.es;

import io.searchbox.annotations.JestId;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
	private static final long serialVersionUID = -3971912226293959387L;

	@JestId
	private String id;

	private String note;

	private Date createdOn;

	private String username;

	public Note(final String username, final String note) {
		this.username = username;
		this.note = note;
		this.createdOn = new Date();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNote() {
		return note;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public Date getCreatedOn() {
		return createdOn;
	}

	public void setCreatedOn(Date createdOn) {
		this.createdOn = createdOn;
	}

	public String getUserName() {
		return username;
	}

	public void setUserName(String username) {
		this.username = username;
	}

	@Override
	public String toString() {
		return "Note [id=" + id + ", note=" + note + ", createdOn=" + createdOn
				+ ", username=" + username + "]";
	}

}