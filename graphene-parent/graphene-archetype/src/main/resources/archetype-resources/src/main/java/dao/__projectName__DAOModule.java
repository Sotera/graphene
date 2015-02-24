package ${package}.dao;

import graphene.augment.snlp.services.SentimentAnalyzer;
import graphene.augment.snlp.services.SentimentAnalyzerImpl;
import graphene.business.commons.exception.DataAccessException;
import graphene.dao.CombinedDAO;
import graphene.dao.DAOModule;
import graphene.dao.DataSourceListDAO;
import graphene.dao.EntityDAO;
import graphene.dao.GroupDAO;
import graphene.dao.IconService;
import graphene.dao.LoggingDAO;
import graphene.dao.StyleService;
import graphene.dao.TransactionDAO;
import graphene.dao.UserDAO;
import graphene.dao.UserGroupDAO;
import graphene.dao.UserWorkspaceDAO;
import graphene.dao.WorkspaceDAO;
import graphene.dao.annotations.EntityLightFunnelMarker;
import graphene.dao.es.DefaultESUserSpaceModule;
import graphene.dao.es.ESRestAPIConnection;
import graphene.dao.es.ESRestAPIConnectionImpl;
import graphene.dao.es.JestModule;
import graphene.dao.es.impl.CombinedDAOESImpl;
import graphene.dao.es.impl.LoggingDAODefaultESImpl;
import graphene.hts.entityextraction.Extractor;
import graphene.hts.keywords.KeywordExtractorImpl;
import graphene.hts.sentences.SentenceExtractorImpl;
import ${package}.dao.impl.GraphTraversalRuleServiceImpl;
import ${package}.dao.impl.IconServiceImpl;
import ${package}.dao.impl.InstagramDataAccess;
import ${package}.dao.impl.es.DataSourceListDAOESImpl;
import ${package}.dao.impl.es.EntityDAOESImpl;
import ${package}.dao.impl.es.TransactionDAOESImpl;
import ${package}.model.funnels.InstagramEntityLightFunnel;
import graphene.model.funnels.Funnel;
import graphene.model.idl.G_DataAccess;
import graphene.services.StopWordService;
import graphene.services.StopWordServiceImpl;
import graphene.services.StyleServiceImpl;
import graphene.util.PropertiesFileSymbolProvider;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.Invokable;
import org.apache.tapestry5.ioc.MappedConfiguration;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.ParallelExecutor;
import org.graphene.augment.mitie.MITIEModule;
import org.graphene.augment.mitie.dao.MitieDAO;
import org.graphene.augment.mitie.dao.MitieDAOImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Map the interfaces to the implementations you want to use. By default these
 * are singletons.
 * 
 * @author djue
 * 
 */
@SubModule({ JestModule.class, DefaultESUserSpaceModule.class, DAOModule.class, MITIEModule.class })
public class InstagramDAOModule {
	private static Logger logger = LoggerFactory.getLogger(InstagramDAOModule.class);

	public static void bind(final ServiceBinder binder) {
		binder.bind(GraphTraversalRuleService.class, GraphTraversalRuleServiceImpl.class);
		binder.bind(EntityDAO.class, EntityDAOESImpl.class).eagerLoad();
		binder.bind(G_DataAccess.class, InstagramDataAccess.class);

		// Graphene-web needs this for the coercion model
		binder.bind(TransactionDAO.class, TransactionDAOESImpl.class).withId("Primary");

		// TODO: Make this into a service in the core we can contribute to (for
		// distributed configuration!)
		binder.bind(DataSourceListDAO.class, DataSourceListDAOESImpl.class).eagerLoad();
		binder.bind(StyleService.class, StyleServiceImpl.class);

		binder.bind(CombinedDAO.class, CombinedDAOESImpl.class);
		binder.bind(ESRestAPIConnection.class, ESRestAPIConnectionImpl.class).eagerLoad();

		binder.bind(Extractor.class, KeywordExtractorImpl.class).withId("keyword");
		binder.bind(Extractor.class, SentenceExtractorImpl.class).withId("sentence");
		binder.bind(Funnel.class, InstagramEntityLightFunnel.class).withMarker(EntityLightFunnelMarker.class);

		binder.bind(MitieDAO.class, MitieDAOImpl.class);
		binder.bind(IconService.class, IconServiceImpl.class);
		binder.bind(StopWordService.class, StopWordServiceImpl.class);

		binder.bind(SentimentAnalyzer.class, SentimentAnalyzerImpl.class);

		binder.bind(LoggingDAO.class, LoggingDAODefaultESImpl.class).eagerLoad();
	}

	@Contribute(StopWordService.class)
	public static void contributeStopWords(final Configuration<String> stopwords) {
		stopwords.add("Test");
	}

	@Startup
	public static void scheduleJobs(final ParallelExecutor executor, @Inject final UserDAO userDAO,
			@Inject final GroupDAO groupDAO, @Inject final WorkspaceDAO workspaceDAO, @Inject final UserGroupDAO ugDAO,
			@Inject final UserWorkspaceDAO uwDAO, @Inject final LoggingDAO lDAO) {

		executor.invoke(UserDAO.class, new Invokable<UserDAO>() {
			@Override
			public UserDAO invoke() {
				try {
					userDAO.initialize();
				} catch (final DataAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return userDAO;
			}
		});

		executor.invoke(GroupDAO.class, new Invokable<GroupDAO>() {
			@Override
			public GroupDAO invoke() {
				try {
					groupDAO.initialize();
				} catch (final DataAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return groupDAO;
			}
		});

		executor.invoke(WorkspaceDAO.class, new Invokable<WorkspaceDAO>() {
			@Override
			public WorkspaceDAO invoke() {
				try {
					workspaceDAO.initialize();
				} catch (final DataAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return workspaceDAO;
			}
		});

		executor.invoke(UserGroupDAO.class, new Invokable<UserGroupDAO>() {
			@Override
			public UserGroupDAO invoke() {
				try {
					ugDAO.initialize();
				} catch (final DataAccessException e) {
					e.printStackTrace();
				}
				return ugDAO;
			}
		});

		executor.invoke(UserWorkspaceDAO.class, new Invokable<UserWorkspaceDAO>() {
			@Override
			public UserWorkspaceDAO invoke() {
				try {
					uwDAO.initialize();
				} catch (final DataAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				return uwDAO;
			}
		});
	}

	public PropertiesFileSymbolProvider buildTableNameSymbolProvider(final Logger logger) {
		return new PropertiesFileSymbolProvider(logger, "tablenames.properties", true);
	}

	public void contributeApplicationDefaults(final MappedConfiguration<String, String> configuration) {
		configuration.add(MITIEModule.ENABLED, "true");
		configuration.add(JestModule.ES_DEFAULT_TIMEOUT, "30s");

	}

}
