#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dao;

import graphene.augment.mitie.dao.MitieDAO;
import graphene.augment.mitie.dao.MitieDAODefaultImpl;
import graphene.augment.mitie.web.services.MITIEMod;
import graphene.augment.snlp.services.SentimentAnalyzer;
import graphene.augment.snlp.services.SentimentAnalyzerImpl;
import graphene.business.commons.exception.DataAccessException;
import graphene.dao.DAOModule;
import graphene.dao.DataSourceListDAO;
import graphene.dao.DocumentBuilder;
import graphene.dao.EventServer;
import graphene.dao.GraphTraversalRuleService;
import graphene.dao.GroupDAO;
import graphene.dao.IconService;
import graphene.dao.LoggingDAO;
import graphene.dao.StartupProcedures;
import graphene.dao.StopWordService;
import graphene.dao.StyleService;
import graphene.dao.UserDAO;
import graphene.dao.UserGroupDAO;
import graphene.dao.UserWorkspaceDAO;
import graphene.dao.WorkspaceDAO;
import graphene.dao.es.DefaultESUserSpaceModule;
import graphene.dao.es.ESRestAPIConnection;
import graphene.dao.es.ESRestAPIConnectionImpl;
import graphene.dao.es.JestModule;
import graphene.dao.es.impl.CombinedDAOESImpl;
import graphene.dao.es.impl.LoggingDAODefaultESImpl;
import graphene.dao.es.impl.MultiDocumentBuilderESImpl;
import graphene.hts.entityextraction.Extractor;
import graphene.hts.keywords.KeywordExtractorImpl;
import graphene.hts.sentences.SentenceExtractorImpl;
import ${package}.dao.impl.GraphTraversalRuleServiceImpl;
import ${package}.dao.impl.IconServiceImpl;
import ${package}.dao.impl.es.DataSourceListDAOESImpl;
import ${package}.web.services.${projectName}StartupProceduresImpl;
import graphene.model.idl.G_DataAccess;
import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.model.idl.G_UserDataAccess;
import graphene.services.EventServerImpl;
import graphene.services.G_PropertyKeyTypeAccessImpl;
import graphene.services.StopWordServiceDefaultImpl;
import graphene.services.StyleServiceImpl;
import graphene.services.UserServiceImpl;

import org.apache.tapestry5.ioc.Configuration;
import org.apache.tapestry5.ioc.Invokable;
import org.apache.tapestry5.ioc.ServiceBinder;
import org.apache.tapestry5.ioc.annotations.Contribute;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Startup;
import org.apache.tapestry5.ioc.annotations.SubModule;
import org.apache.tapestry5.ioc.services.ParallelExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Map the interfaces to the implementations you want to use. By default these
 * are singletons.
 * 
 * @author djue
 * 
 */
@SubModule({ JestModule.class, DefaultESUserSpaceModule.class, DAOModule.class, MITIEMod.class })
public class ${projectName}DAOModule {
	private static Logger logger = LoggerFactory.getLogger(${projectName}DAOModule.class);

	public static void bind(final ServiceBinder binder) {
		binder.bind(G_UserDataAccess.class, UserServiceImpl.class);
		binder.bind(EventServer.class, EventServerImpl.class);
		binder.bind(G_PropertyKeyTypeAccess.class, G_PropertyKeyTypeAccessImpl.class);

		binder.bind(GraphTraversalRuleService.class, GraphTraversalRuleServiceImpl.class);

		// binder.bind(G_DataAccess.class, ${projectName}DataAccess.class);
		binder.bind(StyleService.class, StyleServiceImpl.class);

		binder.bind(DataSourceListDAO.class, DataSourceListDAOESImpl.class).eagerLoad();
		binder.bind(DocumentBuilder.class, MultiDocumentBuilderESImpl.class);

		binder.bind(G_DataAccess.class, CombinedDAOESImpl.class);
		binder.bind(ESRestAPIConnection.class, ESRestAPIConnectionImpl.class).eagerLoad();

		binder.bind(Extractor.class, KeywordExtractorImpl.class).withId("keyword");
		binder.bind(Extractor.class, SentenceExtractorImpl.class).withId("sentence");

		binder.bind(MitieDAO.class, MitieDAODefaultImpl.class);
		binder.bind(IconService.class, IconServiceImpl.class);
		binder.bind(StopWordService.class, StopWordServiceDefaultImpl.class);

		binder.bind(StartupProcedures.class, ${projectName}StartupProceduresImpl.class);
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

}
