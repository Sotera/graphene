package graphene.web.pages;

import graphene.model.idl.G_User;

import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;

public class SimpleBasePage {
	@SessionState(create = false)
	private G_User user;

	private boolean userExists;

	@Inject
	private Logger logger;
	
	@Inject
	private Messages messages;

	protected Messages getMessages() {
		return messages;
	}

	/**
	 * @return the userExists
	 */
	public final boolean isUserExists() {
		return userExists;
	}

	@Inject
	private Request request;

	/**
	 * @return the user
	 */
	public final G_User getUser() {
		return user;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public final void setUser(G_User user) {
		this.user = user;
	}

}
