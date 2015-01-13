package graphene.web.pages;

import graphene.dao.LoggingDAO;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_Workspace;

import java.util.List;

import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Request;
import org.slf4j.Logger;

public class SimpleBasePage {

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_VERSION)
	private String appVersion;
	@Property
	@SessionState(create = false)
	private List<G_Workspace> workspaces;
	@Inject
	protected LoggingDAO loggingDao;

	@Inject
	protected AlertManager alertManager;

	@SessionState(create = false)
	private G_User user;

	private boolean userExists;

	@Inject
	private Logger logger;

	@Inject
	private Messages messages;

	@Inject
	private Request request;

	protected Messages getMessages() {
		return messages;
	}

	/**
	 * @return the user
	 */
	public final G_User getUser() {
		return user;
	}

	/**
	 * @return the userExists
	 */
	public final boolean isUserExists() {
		return userExists;
	}

	/**
	 * @param user
	 *            the user to set
	 */
	public final void setUser(final G_User user) {
		this.user = user;
	}

}
