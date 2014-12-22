package graphene.web.test.integration;

import graphene.web.data.IDataSource;
import graphene.web.data.MockDataSource;

import java.text.ParseException;

import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.services.ApplicationStateContribution;
import org.apache.tapestry5.services.ApplicationStateCreator;

public class GrapheneTestModule {
	public static void bind(ServiceBinder binder) {
		binder.bind(IDataSource.class, MockDataSource.class);
	}
	/**
	 * This is for trying to make T5 Jquery Ajax datatables work, where the data
	 * is very large and results need to be manipulated on the server before
	 * sending to the client.
	 * 
	 * @param configuration
	 */

	public void contributeApplicationStateManager(
			MappedConfiguration<Class, ApplicationStateContribution> configuration) {

		ApplicationStateCreator<IDataSource> creator = new ApplicationStateCreator<IDataSource>() {
			public IDataSource create() {
				try {
					return new MockDataSource();
				} catch (ParseException e) {
					e.printStackTrace();
					return null;
				}
			}
		};

		configuration.add(IDataSource.class, new ApplicationStateContribution(
				"session", creator));
	}
}
