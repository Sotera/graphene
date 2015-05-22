package graphene.web.services;

import graphene.dao.DAOModule;
import graphene.dao.StartupProcedures;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_SymbolConstants;
import graphene.util.PropertiesFileSymbolProvider;
import graphene.util.time.JodaTimeUtil;
import graphene.web.services.javascript.CytoscapeStack;
import graphene.web.validators.Password;

import java.io.IOException;

import org.apache.tapestry5.SymbolConstants;
import org.apache.tapestry5.Validator;
import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.OrderedConfiguration;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.ioc.internal.services.ClasspathResourceSymbolProvider;
import org.apache.tapestry5.ioc.services.Coercion;
import org.apache.tapestry5.ioc.services.CoercionTuple;
import org.apache.tapestry5.ioc.services.SymbolProvider;
import org.apache.tapestry5.ioc.services.SymbolSource;
import org.apache.tapestry5.services.BeanBlockContribution;
import org.apache.tapestry5.services.DisplayBlockContribution;
import org.apache.tapestry5.services.EditBlockContribution;
import org.apache.tapestry5.services.LibraryMapping;
import org.apache.tapestry5.services.MarkupRenderer;
import org.apache.tapestry5.services.MarkupRendererFilter;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.RequestFilter;
import org.apache.tapestry5.services.RequestHandler;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.services.javascript.JavaScriptStack;
import org.apache.tapestry5.services.javascript.JavaScriptStackSource;
import org.got5.tapestry5.jquery.JQuerySymbolConstants;
import org.joda.time.DateTime;
import org.joda.time.LocalDate;
import org.joda.time.LocalDateTime;
import org.joda.time.LocalTime;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * This module is automatically included as part of the Tapestry IoC Registry,
 * it's a good place to configure and extend Tapestry, or to place your own
 * service definitions.
 * 
 * Note that additional modules you want to use should be included in the @SubModules
 * annotation.
 */
@SubModule({ DAOModule.class })
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
	public static void addGrapheneJSStacks(final MappedConfiguration<String, JavaScriptStack> configuration) {
		configuration.addInstance("CytoscapeStack", CytoscapeStack.class);
		// configuration.addInstance("NeoCytoscapeStack",
		// NeoCytoscapeStack.class);
		// configuration.addInstance("GlobeStack", GlobeStack.class);
	}

	public static void contributeApplicationDefaults(final MappedConfiguration<String, Object> configuration) {
		/*
		 * The application version number is incorporated into URLs for some
		 * assets. Web browsers will cache assets because of the far future
		 * expires header. If existing assets are changed, the version number
		 * should also change, to force the browser to download new versions.
		 * This overrides Tapesty's default (a random hexadecimal number), but
		 * may be further overridden by DevelopmentModule or QaModule.
		 * 
		 * 
		 * Note: These are the defaults. To override this in a customer
		 * implementation, repeat this method (and it's annotations) in a
		 * customer module, but use configuration.override() instead of
		 * configuration.add() to override this default and use the values you
		 * want.
		 */

		configuration.add(G_SymbolConstants.APPLICATION_NAME, "Graphene");
		configuration.add(SymbolConstants.APPLICATION_VERSION, "${graphene.application-version}");
		configuration.add(G_SymbolConstants.APPLICATION_CONTACT, "Sotera Defense Solutions, DFA");
		configuration.add(SymbolConstants.SUPPORTED_LOCALES, "en");
		configuration.add(G_SymbolConstants.THEME_PATH, "core/");
		configuration.add(SymbolConstants.PRODUCTION_MODE, false);
		configuration.add(G_SymbolConstants.DATABASE_PROPERTIES_LOCATION, "");
		configuration.add(G_SymbolConstants.GRAPHENE_WEB_CORE_PREFIX, "graphene");
		configuration.add(SymbolConstants.HMAC_PASSPHRASE, "ad4c17c4ec6da4afe3aad15660abaf8e");
		configuration.add(G_SymbolConstants.REQUIRE_AUTHENTICATION, false);
		configuration.add(JQuerySymbolConstants.SUPPRESS_PROTOTYPE, true);
		configuration.add(JQuerySymbolConstants.JQUERY_ALIAS, "$");
		configuration.add(JQuerySymbolConstants.USE_MINIFIED_JS, false);
		configuration.add(G_SymbolConstants.ENABLE_EXPERIMENTAL, false);
		configuration.add(G_SymbolConstants.ENABLE_MISC, false);
		configuration.add(G_SymbolConstants.ENABLE_SETTINGS, true);
		configuration.add(G_SymbolConstants.ENABLE_ADMIN, true);
		configuration.add(G_SymbolConstants.ENABLE_TAG_CLOUDS, false);
		configuration.add(G_SymbolConstants.ENABLE_FEDERATED_LOGIN, false);
		configuration.add(G_SymbolConstants.ENABLE_LOGGING, true);

		configuration.add(G_SymbolConstants.DEFAULT_MAX_SEARCH_RESULTS, 200);
		configuration.add(G_SymbolConstants.DEFAULT_GRAPH_TRAVERSAL_DEGREE, 1);
		configuration.add(G_SymbolConstants.DEFAULT_MAX_GRAPH_NODES, 1000);
		configuration.add(G_SymbolConstants.DEFAULT_MAX_GRAPH_EDGES_PER_NODE, 100);
		configuration.add(G_SymbolConstants.DEFAULT_MIN_SCORE, 0.75d);
		configuration.add(G_SymbolConstants.ENABLE_FREE_TEXT_EXTRACTION, true);
		configuration.add(G_SymbolConstants.ENABLE_GRAPH_QUERY_PATH, true);

		configuration.add(G_SymbolConstants.WORKSPACE_NAME_VALIDATION,
				"^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

	}

	public static void contributeBeanBlockSource(final Configuration<BeanBlockContribution> configuration) {

		// Display blocks
		configuration.add(new DisplayBlockContribution("dateTime", "infrastructure/AppPropertyDisplayBlocks",
				"dateTime"));
		configuration.add(new DisplayBlockContribution("localDateTime", "infrastructure/AppPropertyDisplayBlocks",
				"localDateTime"));
		configuration.add(new DisplayBlockContribution("localDate", "infrastructure/AppPropertyDisplayBlocks",
				"localDate"));
		configuration.add(new DisplayBlockContribution("localTime", "infrastructure/AppPropertyDisplayBlocks",
				"localTime"));

		// Edit blocks
		configuration.add(new EditBlockContribution("localDate", "infrastructure/AppPropertyEditBlocks", "localDate"));
		configuration.add(new EditBlockContribution("dateTime", "infrastructure/AppPropertyEditBlocks", "dateTime"));
	}

	public static void contributeComponentClassResolver(final Configuration<LibraryMapping> configuration,
			@Inject @Symbol(G_SymbolConstants.GRAPHENE_WEB_CORE_PREFIX) final String pathPrefix) {
		configuration.add(new LibraryMapping(pathPrefix, "graphene.web"));
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
			@SuppressWarnings("rawtypes") final MappedConfiguration<Class, String> configuration) {
		configuration.add(DateTime.class, "dateTime");
		configuration.add(LocalDateTime.class, "localDateTime");
		configuration.add(LocalDate.class, "localDate");
		configuration.add(LocalTime.class, "localTime");
	}

	/**
	 * Contribute custom validators for user input fields.
	 * 
	 * @param configuration
	 */
	@SuppressWarnings("rawtypes")
	public static void contributeFieldValidatorSource(final MappedConfiguration<String, Validator> configuration,
			@Inject @Symbol(G_SymbolConstants.USER_PASSWORD_VALIDATION) final String validation,
			@Inject @Symbol(G_SymbolConstants.USER_PASSWORD_VALIDATION_MESSAGE) final String validationMessage) {
		configuration.add("password", new Password(validation, validationMessage));
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
	public static void contributeTypeCoercer(final Configuration<CoercionTuple> configuration) {
		// LocalDate ///////////////////////////
		// From java.util.Date to LocalDate

		final Coercion<java.util.Date, LocalDate> toLocalDate = new Coercion<java.util.Date, LocalDate>() {
			@Override
			public LocalDate coerce(final java.util.Date input) {
				return JodaTimeUtil.toLocalDate(input);
			}
		};

		configuration.add(new CoercionTuple<>(java.util.Date.class, LocalDate.class, toLocalDate));

		// From LocalDate to java.util.Date

		final Coercion<LocalDate, java.util.Date> fromLocalDate = new Coercion<LocalDate, java.util.Date>() {
			@Override
			public java.util.Date coerce(final LocalDate input) {
				return JodaTimeUtil.toJavaDate(input);
			}
		};

		configuration.add(new CoercionTuple<>(LocalDate.class, java.util.Date.class, fromLocalDate));
		// End LocalDate ///////////////////////////

		// /////////////////////////////////////
		// DateTime ///////////////////////////
		// From java.util.Date to DateTime

		final Coercion<java.util.Date, DateTime> toDateTime = new Coercion<java.util.Date, DateTime>() {
			@Override
			public DateTime coerce(final java.util.Date input) {
				return JodaTimeUtil.toDateTime(input);
			}
		};

		configuration.add(new CoercionTuple<>(java.util.Date.class, DateTime.class, toDateTime));

		// From DateTime to java.util.Date

		final Coercion<DateTime, java.util.Date> fromDateTime = new Coercion<DateTime, java.util.Date>() {
			@Override
			public java.util.Date coerce(final DateTime input) {
				return JodaTimeUtil.toJavaDate(input);
			}
		};

		configuration.add(new CoercionTuple<>(DateTime.class, java.util.Date.class, fromDateTime));
		// End DateTime ///////////////////////////

		// /////////////////////////////////////
		// DateTime ///////////////////////////
		// From java.lang.Long to DateTime

		final Coercion<java.lang.Long, DateTime> longToDateTime = new Coercion<java.lang.Long, DateTime>() {
			@Override
			public DateTime coerce(final java.lang.Long input) {
				return JodaTimeUtil.toDateTime(input);
			}
		};

		configuration.add(new CoercionTuple<>(java.lang.Long.class, DateTime.class, longToDateTime));

		// From DateTime to java.util.Date

		final Coercion<DateTime, java.lang.Long> fromDateTimetoLong = new Coercion<DateTime, java.lang.Long>() {
			@Override
			public java.lang.Long coerce(final DateTime input) {
				return JodaTimeUtil.toJavaDate(input).getTime();
			}
		};

		configuration.add(new CoercionTuple<>(DateTime.class, java.lang.Long.class, fromDateTimetoLong));
		// End DateTime ///////////////////////////

		// From String to G_EntityQuery

		final Coercion<String, G_EntityQuery> fromStringtoGEntityQuery = new Coercion<String, G_EntityQuery>() {
			private final ObjectMapper mapper = new ObjectMapper();

			@Override
			public G_EntityQuery coerce(final String input) {
				new ObjectMapper();
				G_EntityQuery value = null;
				try {
					// value = SerializationHelper.fromJson(input,
					// G_EntityQuery.getClassSchema());
					value = mapper.readValue(input, G_EntityQuery.class);
					// for (final G_PropertyMatchDescriptor f :
					// value.getPropertyMatchDescriptors()) {
					// // f.getRange()
					// final LinkedHashMap<String, Object> lhm =
					// (LinkedHashMap<String, Object>) f.getRange();
					// ArrayList<Object> propValues = null;
					// Object propValue = null;
					// G_PropertyType propType = null;
					// for (final String k : lhm.keySet()) {
					// if (k.equals("values")) {
					// propValues = (ArrayList<Object>) lhm.get(k);
					// } else if (k.equals("value")) {
					// propValue = lhm.get(k);
					// } else if (k.equals("type")) {
					// propType = G_PropertyType.valueOf((String) lhm.get(k));
					//
					// }
					//
					// System.out.println("k =" + k + " v=" + lhm.get(k));
					// }
					// if (propValues != null) {
					// final G_ListRange l =
					// G_ListRange.newBuilder().setType(propType).setValues(propValues)
					// .build();
					// f.setListRange(l);
					// } else if (propValue != null) {
					// final G_SingletonRange l =
					// G_SingletonRange.newBuilder().setType(propType)
					// .setValue(propValue).build();
					// f.setSingletonRange(l);
					// }
					// }
				} catch (final JsonParseException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (final JsonMappingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (final IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return value;
			}
		};

		configuration.add(new CoercionTuple<>(String.class, G_EntityQuery.class, fromStringtoGEntityQuery));
		// End G_EntityQuery ///////////////////////////
	}

	@Contribute(SymbolSource.class)
	public static void contributeUserConfiguration(final OrderedConfiguration<SymbolProvider> configuration) {
		configuration.add("UserConfiguration", new ClasspathResourceSymbolProvider("users.properties"),
				"after:SystemProperties", "before:ApplicationDefaults");
	}

	@Contribute(SymbolSource.class)
	public static void contributeVersionSymbolProvider(final OrderedConfiguration<SymbolProvider> configuration,
			@InjectService("VersionSymbolProvider") final SymbolProvider c) {
		configuration.add("VersionPropertiesFile", c, "after:SystemProperties", "before:ApplicationDefaults");
	}

	@Contribute(MarkupRenderer.class)
	public static void deactivateDefaultCSS(final OrderedConfiguration<MarkupRendererFilter> config) {
		config.override("InjectDefaultStylesheet", null);
	}

	@Startup
	public static void performStartupProcedures(@Inject final StartupProcedures sp) {
		sp.initialize();
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
			@Override
			public boolean service(final Request request, final Response response, final RequestHandler handler)
					throws IOException {
				final long startTime = System.currentTimeMillis();

				try {
					// The responsibility of a filter is to invoke the
					// corresponding method
					// in the handler. When you chain multiple filters together,
					// each filter
					// received a handler that is a bridge to the next filter.

					return handler.service(request, response);
				} finally {
					final long elapsed = System.currentTimeMillis() - startTime;

					log.info(String.format("Request time: %d ms", elapsed));
				}
			}
		};
	}

	public PropertiesFileSymbolProvider buildVersionSymbolProvider(final Logger logger) {
		return new PropertiesFileSymbolProvider(logger, "version.prop", true);
	}
}
