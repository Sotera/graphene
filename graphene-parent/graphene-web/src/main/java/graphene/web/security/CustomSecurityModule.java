package graphene.web.security;

import graphene.model.idl.G_Workspace;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.services.ApplicationStateContribution;
import org.apache.tapestry5.services.ApplicationStateCreator;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.ComponentRequestFilter;
import org.apache.tapestry5.services.ComponentRequestHandler;
import org.apache.tapestry5.validator.ValidatorMacro;

public class CustomSecurityModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(AuthenticatorHelper.class, BasicAuthenticator.class);
	}

	@Contribute(ValidatorMacro.class)
	public static void combineValidators(
			MappedConfiguration<String, String> configuration) {
		configuration.add("username", "required, minlength=3, maxlength=15");
		configuration.add("password", "required, minlength=6, maxlength=12");
	}

	@Contribute(ComponentRequestHandler.class)
	public static void contributeComponentRequestHandler(
			OrderedConfiguration<ComponentRequestFilter> configuration) {
		configuration.addInstance("RequiresLogin", AuthenticationFilter.class);
	}

	
	/**
	 * Control how new workspaces are created.
	 * @param config
	 */
	@Contribute(ApplicationStateManager.class)
	public void provideStateCreators(
			MappedConfiguration<Class, ApplicationStateContribution> config) {
		ApplicationStateCreator<G_Workspace> creator = new ApplicationStateCreator<G_Workspace>() {
			public G_Workspace create() {
				return new G_Workspace();
			}
		};
		ApplicationStateContribution contribution = new ApplicationStateContribution(
				PersistenceConstants.SESSION, creator);
		config.add(G_Workspace.class, contribution);
	}
	

}
