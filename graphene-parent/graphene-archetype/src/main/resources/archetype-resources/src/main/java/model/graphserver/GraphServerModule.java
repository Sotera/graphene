package ${package}.model.graphserver;

import graphene.dao.DocumentGraphParser;
import graphene.dao.FederatedPropertyGraphServer;
import ${package}.dao.GraphTraversalRuleService;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_Constraint;
import graphene.services.HyperGraphBuilder;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;

public class GraphServerModule {
	public static final String MEDIA = "Media";

	public static void bind(final ServiceBinder binder) {

		binder.bind(HyperGraphBuilder.class, PropertyHyperGraphBuilderInstagramImpl.class).withId("HyperProperty")
				.eagerLoad().scope(ScopeConstants.PERTHREAD);

		binder.bind(DocumentGraphParser.class, MediaGraphParser.class).withId(MEDIA);

	}

	@Contribute(FederatedPropertyGraphServer.class)
	public static void contributeApplication2(final Configuration<HyperGraphBuilder> singletons,
			@InjectService("HyperProperty") final HyperGraphBuilder egb) {
		singletons.add(egb);
	}

	/**
	 * Contribute to the list of available parsers.
	 * 
	 * 
	 * @param singletons
	 * @param media
	 */
	@Contribute(HyperGraphBuilder.class)
	public static void contributeParsers(final Configuration<DocumentGraphParser> singletons,
			@InjectService(MEDIA) final DocumentGraphParser media) {
		singletons.add(media);
	}

	@Contribute(GraphTraversalRuleService.class)
	public static void contributeTraversalRules(final MappedConfiguration<String, G_Constraint> rules) {
		rules.add("default", G_Constraint.COMPARE_CONTAINS);

		// Lose rules that will use common terms
		rules.add(G_CanonicalPropertyType.ACCOUNT.name(), G_Constraint.COMPARE_CONTAINS);

		// More strict rules that will use match
		rules.add(G_CanonicalPropertyType.ADDRESS.name(), G_Constraint.COMPARE_EQUALS);
		rules.add(G_CanonicalPropertyType.ADDRESS_STREET.name(), G_Constraint.COMPARE_EQUALS);
		rules.add(G_CanonicalPropertyType.NAME.name(), G_Constraint.COMPARE_EQUALS);
		rules.add(G_CanonicalPropertyType.MEDIA.name(), G_Constraint.COMPARE_EQUALS);

	}

}
