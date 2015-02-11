package graphene.web.test.integration;

import graphene.web.data.IDataSource;
import graphene.web.data.MockDataSource;

import java.text.ParseException;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.services.ApplicationStateContribution;
import org.apache.tapestry5.services.ApplicationStateCreator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GrapheneTestModule {
	public static void bind(final ServiceBinder binder) {
		binder.bind(IDataSource.class, MockDataSource.class);
	}

	/**
	 * This is for trying to make T5 Jquery Ajax datatables work, where the data
	 * is very large and results need to be manipulated on the server before
	 * sending to the client.
	 * 
	 * @param configuration
	 */
	private final Logger logger = LoggerFactory.getLogger(GrapheneTestModule.class);

	public void contributeApplicationStateManager(
			final MappedConfiguration<Class, ApplicationStateContribution> configuration) {

		final ApplicationStateCreator<IDataSource> creator = new ApplicationStateCreator<IDataSource>() {
			@Override
			public IDataSource create() {
				try {
					return new MockDataSource();
				} catch (final ParseException e) {
					logger.error(e.getMessage());
					return null;
				}
			}
		};

		configuration.add(IDataSource.class, new ApplicationStateContribution("session", creator));
	}
}
