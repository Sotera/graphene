package graphene.web.services;

import graphene.dao.DAOModule;
import graphene.model.idl.G_SymbolConstants;
import graphene.util.time.JodaTimeUtil;
import graphene.web.services.javascript.CytoscapeStack;
import graphene.web.services.javascript.NeoCytoscapeStack;

import java.io.IOException;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Local;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.services.BeanBlockContribution;
import org.apache.tapestry5.services.DisplayBlockContribution;
import org.apache.tapestry5.services.EditBlockContribution;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.JavaScriptStackSource;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.slf4j.Logger;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 * 
 * Note that additional modules you want to use should be included in the @SubModules
 * annotation.
 */
@SubModule({  DAOModule.class })
public class GrapheneModule {

	/**
	 * This is not working yet. We want to get the version number from a value
	 * the manifest, which is updated by the maven pom.
	 * 
	 */
	/*
	 * private static final String version = ModuleProperties
	 * .getVersion(AppModule.class);
	 */
	@Contribute(JavaScriptStackSource.class)
	public static void addGrapheneJSStacks(
			MappedConfiguration<String, JavaScriptStack> configuration) {
		configuration.addInstance("CytoscapeStack", CytoscapeStack.class);
		configuration.addInstance("NeoCytoscapeStack", NeoCytoscapeStack.class);
	}

	public static void contributeApplicationDefaults(
			MappedConfiguration<String, Object> configuration) {
		// The application version number is incorporated into URLs for some
		// assets. Web browsers will cache assets because of the far future
		// expires
		// header. If existing assets are changed, the version number should
		// also
		// change, to force the browser to download new versions. This overrides
		// Tapesty's default
		// (a random hexadecimal number), but may be further overridden by
		// DevelopmentModule or
		// QaModule.
		// configuration.add(G_SymbolConstants.APPLICATION_NAME, "Graphene");
		// configuration.override(SymbolConstants.APPLICATION_VERSION, "4.0.8");
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
		configuration.add(G_SymbolConstants.THEME_PATH, "core/");
		configuration.add(SymbolConstants.PRODUCTION_MODE, false);
		configuration.add(G_SymbolConstants.DATABASE_PROPERTIES_LOCATION, "");
		configuration.add(G_SymbolConstants.GRAPHENE_WEB_CORE_PREFIX,
				"graphene");
		configuration.add(SymbolConstants.HMAC_PASSPHRASE,
				"ad4c17c4ec6da4afe3aad15660abaf8e");

		// configuration.add(JQuerySymbolConstants.SUPPRESS_PROTOTYPE, true);
		// configuration.add(JQuerySymbolConstants.JQUERY_ALIAS, "$");
	}

	public static void contributeComponentClassResolver(
			Configuration<LibraryMapping> configuration,
			@Inject @Symbol(G_SymbolConstants.GRAPHENE_WEB_CORE_PREFIX) String pathPrefix) {
		configuration.add(new LibraryMapping(pathPrefix, "graphene.web"));
	}

	/**
	 * This is a service definition, the service will be named "TimingFilter".
	 * The interface, RequestFilter, is used within the RequestHandler service
	 * pipeline, which is built from the RequestHandler service configuration.
	 * Tapestry IoC is responsible for passing in an appropriate Logger
	 * instance. Requests for static resources are handled at a higher level, so
	 * this filter will only be invoked for Tapestry related requests.
	 * <p/>
	 * <p/>
	 * Service builder methods are useful when the implementation is inline as
	 * an inner class (as here) or require some other kind of special
	 * initialization. In most cases, use the static bind() method instead.
	 * <p/>
	 * <p/>
	 * If this method was named "build", then the service id would be taken from
	 * the service interface and would be "RequestFilter". Since Tapestry
	 * already defines a service named "RequestFilter" we use an explicit
	 * service id that we can reference inside the contribution method.
	 */
	public RequestFilter buildTimingFilter(final Logger log) {
		return new RequestFilter() {
			public boolean service(Request request, Response response,
					RequestHandler handler) throws IOException {
				long startTime = System.currentTimeMillis();

				try {
					// The responsibility of a filter is to invoke the
					// corresponding method
					// in the handler. When you chain multiple filters together,
					// each filter
					// received a handler that is a bridge to the next filter.

					return handler.service(request, response);
				} finally {
					long elapsed = System.currentTimeMillis() - startTime;

					log.info(String.format("Request time: %d ms", elapsed));
				}
			}
		};
	}

	/**
	 * This is a contribution to the RequestHandler service configuration. This
	 * is how we extend Tapestry using the timing filter. A common use for this
	 * kind of filter is transaction management or security. The @Local
	 * annotation selects the desired service by type, but only from the same
	 * module. Without @Local, there would be an error due to the other
	 * service(s) that implement RequestFilter (defined in other modules).
	 */
	public void contributeRequestHandler(
			OrderedConfiguration<RequestFilter> configuration,
			@Local RequestFilter filter) {
		// Each contribution to an ordered configuration has a name, When
		// necessary, you may
		// set constraints to precisely control the invocation order of the
		// contributed filter
		// within the pipeline.
		// configuration.add("Timing", filter);
	}



	/**
	 * Tell Tapestry how to coerce Joda Time types to and from Java Date types
	 * for the TypeCoercers example.
	 * 
	 * We do this by contributing configuration to Tapestry's TypeCoercer
	 * service.
	 * 
	 * Based on http://tapestry.apache.org/typecoercer-service.html
	 * 
	 * @param configuration
	 */

	@SuppressWarnings("rawtypes")
	public static void contributeTypeCoercer(
			Configuration<CoercionTuple> configuration) {
		// LocalDate ///////////////////////////
		// From java.util.Date to LocalDate

		Coercion<java.util.Date, LocalDate> toLocalDate = new Coercion<java.util.Date, LocalDate>() {
			public LocalDate coerce(java.util.Date input) {
				return JodaTimeUtil.toLocalDate(input);
			}
		};

		configuration.add(new CoercionTuple<>(java.util.Date.class,
				LocalDate.class, toLocalDate));

		// From LocalDate to java.util.Date

		Coercion<LocalDate, java.util.Date> fromLocalDate = new Coercion<LocalDate, java.util.Date>() {
			public java.util.Date coerce(LocalDate input) {
				return JodaTimeUtil.toJavaDate(input);
			}
		};

		configuration.add(new CoercionTuple<>(LocalDate.class,
				java.util.Date.class, fromLocalDate));
		// End LocalDate ///////////////////////////

		// DateTime ///////////////////////////
		// From java.util.Date to DateTime

		Coercion<java.util.Date, DateTime> toDateTime = new Coercion<java.util.Date, DateTime>() {
			public DateTime coerce(java.util.Date input) {
				return JodaTimeUtil.toDateTime(input);
			}
		};

		configuration.add(new CoercionTuple<>(java.util.Date.class,
				DateTime.class, toDateTime));

		// From DateTime to java.util.Date

		Coercion<DateTime, java.util.Date> fromDateTime = new Coercion<DateTime, java.util.Date>() {
			public java.util.Date coerce(DateTime input) {
				return JodaTimeUtil.toJavaDate(input);
			}
		};

		configuration.add(new CoercionTuple<>(DateTime.class,
				java.util.Date.class, fromDateTime));
		// End DateTime ///////////////////////////
	}

	/**
	 * Tell Tapestry how its BeanDisplay and BeanEditor can handle the JodaTime
	 * types.
	 * 
	 * We do this by contributing configuration to Tapestry's
	 * DefaultDataTypeAnalyzer and BeanBlockSource services.
	 * 
	 * Based on http://tapestry.apache.org/beaneditform-guide.html
	 * 
	 * @param configuration
	 */
	public static void contributeDefaultDataTypeAnalyzer(
			@SuppressWarnings("rawtypes") MappedConfiguration<Class, String> configuration) {
		configuration.add(DateTime.class, "dateTime");
		configuration.add(LocalDateTime.class, "localDateTime");
		configuration.add(LocalDate.class, "localDate");
		configuration.add(LocalTime.class, "localTime");
	}

	public static void contributeBeanBlockSource(
			Configuration<BeanBlockContribution> configuration) {

		// Display blocks
		configuration.add(new DisplayBlockContribution("dateTime",
				"infrastructure/AppPropertyDisplayBlocks", "dateTime"));
		configuration.add(new DisplayBlockContribution("localDateTime",
				"infrastructure/AppPropertyDisplayBlocks", "localDateTime"));
		configuration.add(new DisplayBlockContribution("localDate",
				"infrastructure/AppPropertyDisplayBlocks", "localDate"));
		configuration.add(new DisplayBlockContribution("localTime",
				"infrastructure/AppPropertyDisplayBlocks", "localTime"));

		// Edit blocks
		configuration.add(new EditBlockContribution("localDate",
				"infrastructure/AppPropertyEditBlocks", "localDate"));
		configuration.add(new EditBlockContribution("dateTime",
				"infrastructure/AppPropertyEditBlocks", "dateTime"));
	}
}
