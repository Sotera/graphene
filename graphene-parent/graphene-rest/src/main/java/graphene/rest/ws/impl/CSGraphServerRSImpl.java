package graphene.rest.ws.impl;

import graphene.dao.HyperGraphBuilder;
import graphene.dao.LoggingDAO;
import graphene.dao.WorkspaceDAO;
import graphene.model.graph.G_PersistedGraph;
import graphene.model.idl.G_GraphViewEvent;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.rest.ws.CSGraphServerRS;
import graphene.util.DataFormatConstants;
import graphene.util.FastNumberUtils;
import graphene.util.StringUtils;
import graphene.util.validator.ValidationUtils;

import javax.ws.rs.core.Response;

import mil.darpa.vande.converters.cytoscapejs.V_CSGraph;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GraphQuery;

import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.PostInjection;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.RequestGlobals;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CSGraphServerRSImpl implements CSGraphServerRS {

	@Inject
	private Logger logger;

	@InjectService("HyperProperty")
	private HyperGraphBuilder propertyGraphBuilder;

	@SessionState(create = false)
	protected G_User user;

	@Inject
	@Symbol(G_SymbolConstants.REQUIRE_AUTHENTICATION)
	private boolean requireAuthentication;

	protected boolean userExists;
	@Inject
	protected LoggingDAO loggingDao;
	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_GRAPH_TRAVERSAL_DEGREE)
	private int defaultMaxDegrees;

	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_MAX_GRAPH_NODES)
	private int defaultMaxNodes;
	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_MAX_GRAPH_EDGES_PER_NODE)
	private int defaultMaxEdgesPerNode;
	@Inject
	private WorkspaceDAO wdao;

	@Inject
	private G_UserDataAccess userDataAccess;

	@Inject
	private RequestGlobals rq;

	// @Inject
	// private SecurityService securityService;

	public CSGraphServerRSImpl() {
	}

	@Override
	public V_CSGraph getProperties(final String type, final String[] value, final String maxDegree,
			final String maxNodes, final String maxEdgesPerNode, final boolean bipartite, final boolean leafNodes,
			final boolean showNameNodes, final boolean showIcons, final boolean useSaved) {
		logger.debug("-------");
		logger.debug("get property graph for type " + type);
		logger.debug("Value     " + StringUtils.toString(value));
		logger.debug("Degrees   " + maxDegree);
		logger.debug("LeafNodes " + leafNodes);
		logger.debug("Max Nodes " + maxNodes);
		logger.debug("Max Edges " + maxEdgesPerNode);
		logger.debug("LeafNodes " + leafNodes);
		logger.debug("Bipartite " + bipartite);
		logger.debug("showNameNodes " + showNameNodes);
		logger.debug("useSaved " + useSaved);

		final int maxDegreeInt = FastNumberUtils.parseIntWithCheck(maxDegree, 6);
		final int maxNodesInt = FastNumberUtils.parseIntWithCheck(maxNodes, 1000);
		final int maxEdgesPerNodeInt = FastNumberUtils.parseIntWithCheck(maxEdgesPerNode, 100);

		V_CSGraph m = null;
		if (requireAuthentication && (rq.getHTTPServletRequest().getRemoteUser() == null)) {
			// The user needs to be authenticated.
			m = new V_CSGraph();
			m.setIntStatus(1);
			final String errorMessage = "You must be authenticated in order to get the graph.";
			m.setStrStatus(errorMessage);
			m.setUserId("Unknown");
			m.setUserName("Unknown");
		} else {
			// Either we don't need authentication or the user is
			// authenticated.

			String userId = null;
			String username = null;
			if (requireAuthentication) {
				if (ValidationUtils.isValid(rq.getHTTPServletRequest().getRemoteUser())) {
					try {
						username = rq.getHTTPServletRequest().getRemoteUser();
						final G_User byUsername = userDataAccess.getByUsername(username);
						userId = byUsername.getId();
					} catch (final Exception e) {
						logger.error("Error getting user information during rest call: ", e);
						// e.printStackTrace();
					}
				} else {
					logger.debug("User was not authenticated");
				}
			}
			if (ValidationUtils.isValid(value) && !"null".equals(value[0])) {
				final String firstValue = value[0];
				final G_GraphViewEvent gve = new G_GraphViewEvent();
				gve.setReportId(firstValue);
				gve.setUserId(userId);
				gve.setUserName(username);
				gve.setTimeInitiated(DateTime.now().getMillis());
				if (useSaved) {
					try {
						// TODO: fix which key is going to be used as the seed
						final G_PersistedGraph existingGraph = wdao.getExistingGraph(firstValue, userId, username);
						final ObjectMapper mapper = new ObjectMapper();
						if (existingGraph != null) {
							m = mapper.readValue(existingGraph.getGraphJSONdata(), V_CSGraph.class);
							if (m == null) {
								logger.error("Could not parse existing graph from a previous save, will regenerate.");
							} else {
								gve.setReportType("Existing");
								loggingDao.recordGraphViewEvent(gve);
								m.setStrStatus("This graph was previously saved on "
										+ DataFormatConstants.formatDate(existingGraph.getModified()));
								m.createPositionMapping(); // necessary for
															// preset layout in
															// Cytoscape 3.2.9
							}
						} else {
							logger.info("Could not find previously saved graph, will regenerate");
						}

					} catch (final Exception e) {
						logger.error("Error building graph at rest service: ", e);
					}
				}
				// If pulling from cache didn't work, get a new graph
				if (m == null) {
					V_GenericGraph g = null;
					try {
						final V_GraphQuery q = new V_GraphQuery();
						for (final String v : value) {
							q.addSearchIds(StringUtils.split(v, ','));
						}
						q.setDirected(false);
						q.setMaxNodes(maxNodesInt);
						q.setMaxEdgesPerNode(maxEdgesPerNodeInt);
						q.setMaxHops(maxDegreeInt);
						q.setUserId(userId);
						q.setUsername(username);
						gve.setQueryObject(q);
						gve.setReportType("New");
						loggingDao.recordGraphViewEvent(gve);
						g = propertyGraphBuilder.buildFromSubGraphs(q);
						// g = propertyGraphBuilder.makeGraphResponse(q);
						g.setUserId(userId);
						g.setUsername(username);
						m = new V_CSGraph(g, true);

					} catch (final Exception e) {
						logger.error("Error building graph at rest service: ", e);
					}
				}

			} else {
				m = new V_CSGraph();
				String errorMessage;
				m.setUserId(userId);
				m.setUserName(username);
				if (useSaved) {
					errorMessage = "Tried to recover an existing graph, but no valid id was provided to the server: ["
							+ StringUtils.toString(value) + "]";
				} else {
					errorMessage = "Tried to generate a new graph, but no valid id was provided to the server: ["
							+ StringUtils.toString(value) + "]";
				}
				logger.error(errorMessage);
				m.setIntStatus(1);
				m.setStrStatus(errorMessage);
			}
		}
		return m;

	}

	@PostInjection
	public void initialize() {
		logger.debug("CS Graph Server now available");
	}

	@Override
	public Response saveGraph(final String graphSeed, final String username, final String timeStamp, final String graph) {
		if (requireAuthentication && (rq.getHTTPServletRequest().getRemoteUser() == null)) {
			// The user needs to be authenticated.
			logger.error("User must be logged in to save a graph.");
			return Response.status(200).entity("Unable to save, you must be logged in. ").build();
		} else {
			String authenticatedUsername = username;
			try {
				if (ValidationUtils.isValid(rq.getHTTPServletRequest().getRemoteUser())) {
					authenticatedUsername = rq.getHTTPServletRequest().getRemoteUser();
					// final G_User byUsername =
					// userDataAccess.getByUsername(authenticatedUsername);
					// byUsername.getId();

				} else {
					logger.debug("User was not authenticated");
				}
			} catch (final Exception e) {
				logger.error("Error getting user information during rest call: ", e);
				// e.printStackTrace();
			}

			final G_PersistedGraph pg = new G_PersistedGraph();
			pg.setCreated(DateTime.now().getMillis());
			pg.setModified(DateTime.now().getMillis());
			pg.setGraphSeed(graphSeed);
			pg.setUserName(authenticatedUsername);
			pg.setGraphJSONdata(graph);
			logger.debug("seed: " + graphSeed);
			logger.debug("json: " + graph);
			final G_PersistedGraph saveGraph = wdao.saveGraph(pg);
			if (saveGraph != null) {
				return Response.status(200).entity("Saved " + graphSeed + " as " + saveGraph.getId()).build();
			} else {
				return Response.status(200).entity("Unable to save " + graphSeed).build();
			}
		}
	}

}
