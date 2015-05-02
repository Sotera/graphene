package graphene.web.components.navigation;

import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_Workspace;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

public class ShortcutsMenu {
	@SessionState(create = false)
	@Property
	private G_User user;

	@Property
	@SessionState(create = false)
	private List<G_Workspace> workspaces;
	@Property
	private boolean workspacesExists;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.ENABLE_WORKSPACES)
	protected boolean workspacesEnabled;
	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_MANAGED_SECURITY)
	protected boolean applicationManagedSecurity;

	public boolean getShortcutsAvailable() {
		if (workspacesEnabled || applicationManagedSecurity) {
			return true;
		} else {
			return false;
		}
	}
}
