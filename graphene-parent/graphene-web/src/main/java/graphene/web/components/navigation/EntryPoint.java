package graphene.web.components.navigation;

import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_Workspace;

import java.util.List;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;

public class EntryPoint {
	@Property
	private G_Workspace currentWorkspace;

	@Inject
	private G_UserDataAccess userDataAccess;
	@Property
	@SessionState(create = false)
	protected List<G_Workspace> workspaces;
	@Property
	protected boolean workspacesExists;
}
