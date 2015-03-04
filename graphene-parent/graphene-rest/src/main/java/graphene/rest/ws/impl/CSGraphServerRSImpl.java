package graphene.rest.ws.impl;

import graphene.dao.FederatedEventGraphServer;
import graphene.dao.LoggingDAO;
import graphene.dao.WorkspaceDAO;
import graphene.model.graph.G_PersistedGraph;
import graphene.model.idl.G_GraphViewEvent;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_User;
import graphene.model.idl.G_UserDataAccess;
import graphene.rest.ws.CSGraphServerRS;
import graphene.services.EventGraphBuilder;
import graphene.services.HyperGraphBuilder;
import graphene.util.DataFormatConstants;
import graphene.util.FastNumberUtils;
import graphene.util.StringUtils;
import graphene.util.validator.ValidationUtils;

import java.util.Arrays;

import javax.ws.rs.core.Response;

import mil.darpa.vande.converters.cytoscapejs.V_CSGraph;
import mil.darpa.vande.generic.V_GenericGraph;
import mil.darpa.vande.generic.V_GraphQuery;
import mil.darpa.vande.interactions.TemporalGraphQuery;

import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.tynamo.security.services.SecurityService;

import com.fasterxml.jackson.databind.ObjectMapper;

public class CSGraphServerRSImpl implements CSGraphServerRS {

	@Inject
	private FederatedEventGraphServer feg;

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
	private SecurityService securityService;

	public CSGraphServerRSImpl() {

	}

	@Override
	public V_CSGraph getEvents(final String objectType, final String[] value, final String valueType,
			final String degree, final String maxNodes, final String maxEdgesPerNode, final boolean showIcons,
			final String minSecs, final String maxSecs, final String minimumWeight, final boolean useSaved) {
		logger.debug("-------");
		logger.debug("get Interaction Graph for type " + objectType);
		logger.debug("Value     " + Arrays.toString(value));
		logger.debug("valueType     " + valueType);
		logger.debug("Degrees   " + degree);
		logger.debug("Max Nodes " + maxNodes);
		logger.debug("Max Edges per node" + maxEdgesPerNode);
		logger.debug("showIcons " + showIcons);
		logger.debug("minSecs " + minSecs);
		logger.debug("maxSecs " + maxSecs);
		logger.debug("minimumWeight " + minimumWeight);
		logger.debug("useSaved " + useSaved);
		final int maxdegree = FastNumberUtils.parseIntWithCheck(degree, defaultMaxDegrees);
		final int maxnodes = FastNumberUtils.parseIntWithCheck(maxNodes, defaultMaxNodes);
		final int maxedges = FastNumberUtils.parseIntWithCheck(maxEdgesPerNode, defaultMaxEdgesPerNode);
		final int minWeight = FastNumberUtils.parseIntWithCheck(minimumWeight, 0);
		final long startDate = FastNumberUtils.parseLongWithCheck(minSecs, 0);
		final long endDate = FastNumberUtils.parseLongWithCheck(maxSecs, 0);

		final TemporalGraphQuery q = new TemporalGraphQuery();
		q.setStartTime(startDate);
		q.setEndTime(endDate);
		q.setType(valueType); // new, --djue
		q.setMinTransValue(minWeight); // new --djue
		q.setMaxNodes(maxnodes);
		q.setMaxEdgesPerNode(maxedges);
		q.setMaxHops(maxdegree);
		q.addSearchIds(value);

		V_CSGraph m = null;
		if (ValidationUtils.isValid(value)) {
			try {
				V_GenericGraph g = null;
				final EventGraphBuilder gb = feg.getGraphBuilderForDataSource(objectType);
				if (gb != null) {
					logger.debug("Found Graph Builder for " + objectType + ": " + gb.getClass().getName());
					// loggingDao.recordQuery(q);
					g = gb.makeGraphResponse(q);
				} else {
					logger.error("Unable to handle graph request for type " + objectType);
				}

				m = new V_CSGraph(g, true);
			} catch (final Exception e) {
				logger.error(e.getMessage());
			}
		} else {
			m = new V_CSGraph();
			m.setStrStatus("A query was sent without any ids");
			logger.error("A query was sent without any ids");
		}
		return m;

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
		if (requireAuthentication && !securityService.isAuthenticated()) {
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
			if (ValidationUtils.isValid(securityService.getSubject())) {
				try {
					username = (String) securityService.getSubject().getPrincipal();
					final G_User byUsername = userDataAccess.getByUsername(username);
					userId = byUsername.getId();
				} catch (final Exception e) {
					logger.error("Error getting user information during rest call: ", e);
					// e.printStackTrace();
				}
			} else {
				logger.debug("User was not authenticated");
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
						q.addSearchIds(value);
						q.setDirected(false);
						q.setMaxNodes(maxNodesInt);
						q.setMaxEdgesPerNode(maxEdgesPerNodeInt);
						q.setMaxHops(maxDegreeInt);
						q.setUserId(userId);
						q.setUsername(username);
						gve.setQueryObject(q);
						gve.setReportType("New");
						loggingDao.recordGraphViewEvent(gve);
						g = propertyGraphBuilder.makeGraphResponse(q);
						g.setUserId(userId);
						g.setUserName(username);
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

	@Override
	public V_CSGraph getTemporalEvents(final String objectType, final String[] ids, final String valueType,
			final String maxHops, final String maxNodes, final String maxEdgesPerNode, final boolean showIcons,
			final String minSecs, final String maxSecs, final String minLinksPairOverall,
			final String minValueAnyInteraction, final boolean daily, final boolean monthly, final boolean yearly,
			final boolean directed)

	{
		logger.debug("-------");
		logger.debug("get Interaction Graph for type " + objectType);
		logger.debug("IDs     " + Arrays.toString(ids));
		logger.debug("Max Hops   " + maxHops);
		logger.debug("Max Nodes " + maxNodes);
		logger.debug("Max Edges " + maxEdgesPerNode);
		logger.debug("min links " + minLinksPairOverall);
		logger.debug("min value " + minValueAnyInteraction);
		logger.debug("daily " + daily);
		logger.debug("monthly " + monthly);
		logger.debug("yearly " + yearly);
		logger.debug("directed " + directed);

		final TemporalGraphQuery q = new TemporalGraphQuery();

		q.setMaxHops(FastNumberUtils.parseIntWithCheck(maxHops, defaultMaxDegrees));
		q.setMaxNodes(FastNumberUtils.parseIntWithCheck(maxNodes, defaultMaxNodes));
		q.setMaxEdgesPerNode(FastNumberUtils.parseIntWithCheck(maxEdgesPerNode, defaultMaxEdgesPerNode));
		q.setMinLinks(FastNumberUtils.parseIntWithCheck(minLinksPairOverall, 2));
		q.setMinTransValue(FastNumberUtils.parseIntWithCheck(minValueAnyInteraction, 0));
		q.setMinEdgeValue(FastNumberUtils.parseIntWithCheck(minValueAnyInteraction, 0)); // new,
																							// djue
		q.setByMonth(monthly);
		q.setByDay(daily);
		q.setByYear(yearly);
		q.setDirected(directed);
		q.setStartTime(FastNumberUtils.parseLongWithCheck(minSecs, 0));
		q.setEndTime(FastNumberUtils.parseLongWithCheck(maxSecs, 0));

		q.addSearchIds(ids);

		logger.debug(q.toString());

		// egb.setOriginalQuery(gq);

		V_CSGraph m = new V_CSGraph();
		if (ValidationUtils.isValid(ids)) {
			try {
				// V_GenericGraph g = eventGraphBuilder.makeGraphResponse(gq);
				V_GenericGraph g = null;
				final EventGraphBuilder gb = feg.getGraphBuilderForDataSource(objectType);
				if (gb != null) {
					logger.debug("Found Graph Builder for " + objectType + ": " + gb.getClass().getName());
					g = gb.makeGraphResponse(q);
					if (ValidationUtils.isValid(g)) {
						m = new V_CSGraph(g, true);
						if (ValidationUtils.isValid(g.getNodes(), g.getEdges())) {
							logger.debug("Made graph with " + g.getNodes().size() + " Nodes and " + g.getEdges().size()
									+ " Edges");
						}
					} else {
						logger.error("Problem creating graph response.");
					}
				} else {
					logger.error("Unable to handle graph request for type " + objectType);
				}

			} catch (final Exception e) {
				logger.error("Error building graph: ", e);
				m.setStrStatus("An error occurred when creating the graph: " + e.getMessage());

			}
		} else {
			m = new V_CSGraph();
			m.setStrStatus("A query was sent without any ids");
			logger.error("A query was sent without any ids");
		}
		return m;
	}

	@Override
	public Response saveGraph(final String graphSeed, final String username, final String timeStamp, final String graph) {
		if (requireAuthentication && !securityService.isAuthenticated()) {
			// The user needs to be authenticated.
			logger.error("User must be logged in to save a graph.");
			return Response.status(200).entity("Unable to save, you must be logged in. ").build();
		} else {
			String authenticatedUsername = username;
			if (ValidationUtils.isValid(securityService.getSubject())) {
				try {
					authenticatedUsername = (String) securityService.getSubject().getPrincipal();
					// final G_User byUsername =
					// userDataAccess.getByUsername(authenticatedUsername);
					// byUsername.getId();

				} catch (final Exception e) {
					logger.error("Error getting user information during rest call: ", e);
					// e.printStackTrace();
				}
			} else {
				logger.debug("User was not authenticated");
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
