package graphene.web.security;

import graphene.model.idl.G_Workspace;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.IOCSymbols;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.ApplicationStateContribution;
import org.apache.tapestry5.services.ApplicationStateCreator;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.services.HttpServletRequestFilter;
import org.apache.tapestry5.validator.ValidatorMacro;
import org.tynamo.security.Security;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.services.SecurityFilterChainFactory;
import org.tynamo.security.services.SecurityModule;
import org.tynamo.security.services.impl.SecurityFilterChain;

@SubModule({ SecurityModule.class })
public class ShiroSecurityModule {
	public static void bind(final ServiceBinder binder) {

		binder.bind(AuthenticatorHelper.class, ShiroAuthenticatorHelper.class).eagerLoad();
		binder.bind(Realm.class, GrapheneSecurityRealm.class);
	}

	@Contribute(ValidatorMacro.class)
	public static void combineValidators(final MappedConfiguration<String, String> configuration) {
		configuration.add("username", "required, minlength=3, maxlength=50");
		configuration.add("password", "required, minlength=5, maxlength=50");
	}

	public static void contributeApplicationDefaults(final MappedConfiguration<String, String> configuration) {
		configuration.add(SecuritySymbols.LOGIN_URL, "/graphene/pub/login");

		configuration.add(SecuritySymbols.UNAUTHORIZED_URL, "/graphene/infrastructure/pagedenied");
		configuration.add(SecuritySymbols.SUCCESS_URL, "/graphene/index");
		configuration.add(SecuritySymbols.REDIRECT_TO_SAVED_URL, "true");

		/*
		 * This is a workaround for a problem with Tynamo Security using Subject
		 * Aware Parallel Executor that is unresolved. Version 0.6 should fix
		 * this.
		 */
		configuration.add(IOCSymbols.THREAD_POOL_CORE_SIZE, "1");
	}

	@Contribute(HttpServletRequestFilter.class)
	@Security
	public static void contributeFilter(final Configuration<SecurityFilterChain> configuration,
			final SecurityFilterChainFactory factory, final WebSecurityManager securityManager) {

		// Allow access to the login and registration pages
		configuration.add(factory.createChain("/graphene/pub/**").add(factory.anon()).build());
		// configuration.add(factory.createChain("/").add(factory.roles(),
		// "user").build());
		configuration.add(factory.createChain("/").add(factory.authc()).build());
		configuration.add(factory.createChain("/graphene/**").add(factory.authc()).build());
		configuration.add(factory.createChain("/assets/**").add(factory.anon()).build());
		configuration.add(factory.createChain("/core/**").add(factory.anon()).build());
		configuration.add(factory.createChain("/**").add(factory.authc()).build());

	}

	public static void contributeHttpServletRequestHandler(
			@InjectService("SecurityConfiguration") final HttpServletRequestFilter securityConfiguration,
			final OrderedConfiguration<HttpServletRequestFilter> filters) {
		filters.override("SecurityConfiguration", securityConfiguration,
				"before:ResteasyRequestFilter,after:StoreIntoGlobals");
	}

	@Contribute(WebSecurityManager.class)
	public static void contributeSecurity(final Configuration<Realm> configuration, final Realm grapheneSecurityRealm) {
		configuration.add(grapheneSecurityRealm);
	}

	/**
	 * Control how new workspaces are created.
	 * 
	 * @param config
	 */
	@Contribute(ApplicationStateManager.class)
	public void provideStateCreators(final MappedConfiguration<Class, ApplicationStateContribution> config) {
		final ApplicationStateCreator<G_Workspace> creator = new ApplicationStateCreator<G_Workspace>() {
			@Override
			public G_Workspace create() {
				return new G_Workspace();
			}
		};
		final ApplicationStateContribution contribution = new ApplicationStateContribution(
				PersistenceConstants.SESSION, creator);
		config.add(G_Workspace.class, contribution);
	}
}