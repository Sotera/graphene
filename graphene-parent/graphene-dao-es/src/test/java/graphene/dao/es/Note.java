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

	private String userName;

	public Note(final String userName, final String note) {
		this.userName = userName;
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
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	@Override
	public String toString() {
		return "Note [id=" + id + ", note=" + note + ", createdOn=" + createdOn
				+ ", userName=" + userName + "]";
	}

}