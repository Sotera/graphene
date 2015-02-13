package graphene.web.security;

import graphene.model.idl.G_Workspace;

import org.apache.shiro.realm.Realm;
import org.apache.shiro.web.mgt.WebSecurityManager;
import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.IOCSymbols;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.services.ApplicationStateContribution;
import org.apache.tapestry5.services.ApplicationStateCreator;
import org.apache.tapestry5.services.ApplicationStateManager;
import org.apache.tapestry5.validator.ValidatorMacro;
import org.tynamo.security.SecuritySymbols;
import org.tynamo.security.services.SecurityFilterChainFactory;
import org.tynamo.security.services.SecurityModule;
import org.tynamo.security.services.impl.SecurityFilterChain;

@SubModule({ SecurityModule.class })
public class NoSecurityModule {
	public static void bind(final ServiceBinder binder) {
		binder.bind(AuthenticatorHelper.class, ShiroAuthenticatorHelper.class).eagerLoad();
		binder.bind(Realm.class, NoSecurityRealm.class).eagerLoad();
	}

	@Contribute(ValidatorMacro.class)
	public static void combineValidators(final MappedConfiguration<String, String> configuration) {
		configuration.add("username", "required, minlength=3, maxlength=15");
		configuration.add("password", "required, minlength=6, maxlength=12");
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

	// @Contribute(HttpServletRequestFilter.class)
	// @Marker(Security.class)
	// public static void setupSecurity(
	// Configuration<SecurityFilterChain> configuration,
	// SecurityFilterChainFactory factory,
	// WebSecurityManager securityManager) {
	// // Set everything to anonymous access
	// configuration.add(factory.createChain("/**").add(factory.anon()).build());
	// }
	public static void contributeSecurityConfiguration(final Configuration<SecurityFilterChain> configuration,
			final SecurityFilterChainFactory factory) {
		// /authc/** rule covers /authc , /authc?q=name /authc#anchor urls as
		// well
		configuration.add(factory.createChain("/**").add(factory.anon()).build());
	}

	@Contribute(WebSecurityManager.class)
	public static void contributeWebSecurityManager(final Configuration<Realm> configuration,
			final Realm grapheneSecurityRealm) {
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