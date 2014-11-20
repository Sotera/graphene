package graphene.web.pages;

import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_UserDataAccess;
import graphene.model.idl.G_VisualType;
import graphene.model.idl.G_Workspace;
import graphene.web.annotations.PluginPage;

import java.util.Date;
import java.util.List;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.alerts.AlertManager;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

/**
 * Start page of application.
 */

@PluginPage(visualType = G_VisualType.TOP, menuName = "Dashboard", icon = "fa fa-lg fa-fw fa-code-home")
public class Index extends SimpleBasePage {
	@Inject
	private AlertManager alertManager;

	@Property
	private G_Workspace currentWorkspace;

	@Property
	@Inject
	@Symbol(SymbolConstants.TAPESTRY_VERSION)
	private String tapestryVersion;

	@Inject
	private G_UserDataAccess service;

	@Property
	private List<G_Workspace> workspaces;

	public Date getCurrentTime() {
		return new Date();
	}

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_NAME)
	private String appName;

	@Property
	@Inject
	@Symbol(G_SymbolConstants.APPLICATION_VERSION)
	private String appVersion;

	@SetupRender
	public void setupRender() {
		if (isUserExists()) {
			try {
				workspaces = service.getWorkspacesOrCreateNewForUser(getUser()
						.getId());
			} catch (AvroRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
	}

}
