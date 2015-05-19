package graphene.workflow.web.services;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.atmosphere.cpr.ApplicationConfig;
import org.lazan.t5.atmosphere.services.AtmosphereModule;
import org.lazan.t5.atmosphere.services.TopicAuthorizer;
import org.lazan.t5.atmosphere.services.TopicListener;
import org.lazan.t5.atmosphere.services.internal.AtmosphereHttpServletRequestFilter;

@SubModule({ AtmosphereModule.class })
public class WorkflowModule {

	public static void bind(final ServiceBinder binder) {
		binder.bind(ChatManager.class, ChatManagerImpl.class);
	}

	@Contribute(AtmosphereHttpServletRequestFilter.class)
	public static void contributeAtmosphere(final MappedConfiguration<String, String> config) {
		config.add(ApplicationConfig.PROPERTY_NATIVE_COMETSUPPORT, "true");
	}

	public static void contributeTopicAuthorizer(final OrderedConfiguration<TopicAuthorizer> config) {
		config.addInstance("chat", ChatTopicAuthorizer.class);
	}

	public static void contributeTopicListener(final OrderedConfiguration<TopicListener> config) {
		config.addInstance("chat", ChatTopicListener.class);
	}

}
