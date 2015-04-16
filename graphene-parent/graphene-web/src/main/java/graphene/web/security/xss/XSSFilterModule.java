package graphene.web.security.xss;

import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;

/**
 * 
 * @author Martin Papy
 * 
 */
public class XSSFilterModule {
	public static void bind(final ServiceBinder binder) {
		binder.bind(RequestFilter.class, XSSRequestFilterImpl.class).withId("XSSRequestFilter");
	}

	/*
	 * XSS Filtering
	 */
	@Contribute(RequestHandler.class)
	public static void requestHandler(final OrderedConfiguration<RequestFilter> configuration,
			@Local final RequestFilter xssFilter) {
		configuration.add("XSSRequestFilter", xssFilter, "after:StaticFiles", "before:StoreIntoGlobals");
	}
}