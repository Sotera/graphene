#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.model.graphserver;

import graphene.dao.DocumentBuilder;
import graphene.dao.G_Parser;
import graphene.dao.GraphTraversalRuleService;
import graphene.dao.HyperGraphBuilder;
import graphene.model.idl.G_CanonicalPropertyType;
import graphene.model.idl.G_Constraint;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ScopeConstants;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.InjectService;

public class GraphServerModule {
	public static final String MEDIA = "Media";

	public static void bind(final ServiceBinder binder) {

		binder.bind(HyperGraphBuilder.class, PropertyHyperGraphBuilder${artifactId}Impl.class).withId("HyperProperty")
				.eagerLoad().scope(ScopeConstants.PERTHREAD);

		binder.bind(G_Parser.class, MediaGraphParser.class).withId(MEDIA);

	}

	/**
	 * Contribute to the list of available parsers.
	 * 
	 * 
	 * @param singletons
	 * @param media
	 */
	@Contribute(DocumentBuilder.class)
	public static void contributeParsers(final Configuration<G_Parser> singletons,
			@InjectService(MEDIA) final G_Parser media) {
		singletons.add(media);
	}

	@Contribute(GraphTraversalRuleService.class)
	public static void contributeTraversalRules(final MappedConfiguration<String, G_Constraint> rules) {
		rules.add("default", G_Constraint.CONTAINS);

		// Lose rules that will use common terms
		rules.add(G_CanonicalPropertyType.ACCOUNT.name(), G_Constraint.CONTAINS);

		// More strict rules that will use match
		rules.add(G_CanonicalPropertyType.ADDRESS.name(), G_Constraint.EQUALS);
		rules.add(G_CanonicalPropertyType.ADDRESS_STREET.name(), G_Constraint.EQUALS);
		rules.add(G_CanonicalPropertyType.NAME.name(), G_Constraint.EQUALS);
		rules.add(G_CanonicalPropertyType.MEDIA.name(), G_Constraint.EQUALS);

	}

}
