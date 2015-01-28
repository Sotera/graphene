package graphene.web.components.navigation;

import graphene.model.idl.G_User;
import graphene.model.idl.G_Workspace;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;

public class ShortcutsMenu {
	@SessionState(create = false)
	@Property
	private G_User user;

	@Property
	@SessionState(create = false)
	private List<G_Workspace> workspaces;
	@Property
	private boolean workspacesExists;
}
