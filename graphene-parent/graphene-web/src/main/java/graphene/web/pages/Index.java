package graphene.web.pages;

import graphene.model.idl.G_User;
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
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

/**
 * Start page of application.
 */
@PluginPage(visualType = G_VisualType.META)
public class Index {
	@Inject
	private AlertManager alertManager;

	@Persist
	@Property
	private int clickCount;

	@Property
	private G_Workspace currentWorkspace;

	@Property
	@Inject
	@Symbol(SymbolConstants.TAPESTRY_VERSION)
	private String tapestryVersion;

	@SessionState(create=false)
	private G_User user;

	private boolean userExists;

	@Inject
	private G_UserDataAccess service;

	@Property
	private List<G_Workspace> workspaces;

	@InjectComponent
	private Zone zone;

	public Date getCurrentTime() {
		return new Date();
	}

	void onActionFromIncrement() {
		clickCount++;
		alertManager.info("Increment clicked");

	}

	Object onActionFromIncrementAjax() {
		clickCount++;
		alertManager.info("Increment (via Ajax) clicked");
		return zone;
	}

	@SetupRender
	public void setupRender() {
		if (userExists) {
			String username = user.getUsername();
			try {
				workspaces = service.getWorkspacesOrCreateNewForUser(username);
			} catch (AvroRemoteException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}
	}

}
