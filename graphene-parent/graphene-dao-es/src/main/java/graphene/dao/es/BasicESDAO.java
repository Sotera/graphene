package graphene.dao.es;

import graphene.dao.DataSourceListDAO;
import graphene.dao.DocumentBuilder;
import graphene.model.idl.G_BoundedRange;
import graphene.model.idl.G_CallBack;
import graphene.model.idl.G_Constraint;
import graphene.model.idl.G_DataAccess;
import graphene.model.idl.G_DateRange;
import graphene.model.idl.G_DirectionFilter;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_LevelOfDetail;
import graphene.model.idl.G_Link;
import graphene.model.idl.G_LinkEntityTypeFilter;
import graphene.model.idl.G_LinkTag;
import graphene.model.idl.G_PropertyDescriptors;
import graphene.model.idl.G_PropertyMatchDescriptor;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SearchResult;
import graphene.model.idl.G_SearchResults;
import graphene.model.idl.G_SymbolConstants;
import graphene.model.idl.G_TransactionResults;
import graphene.model.idlhelper.PropertyMatchDescriptorHelper;
import graphene.model.idlhelper.QueryHelper;
import graphene.model.idlhelper.SingletonRangeHelper;
import graphene.util.StringUtils;
import graphene.util.stats.TimeReporter;
import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestResult;
import io.searchbox.core.Count;
import io.searchbox.core.CountResult;
import io.searchbox.core.Delete;
import io.searchbox.core.Index;
import io.searchbox.core.Search;
import io.searchbox.core.Search.Builder;
import io.searchbox.core.SearchScroll;
import io.searchbox.indices.CreateIndex;
import io.searchbox.indices.IndicesExists;
import io.searchbox.params.Parameters;
import io.searchbox.params.SearchType;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

import org.apache.avro.AvroRemoteException;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.FilterBuilders;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.joda.time.DateTime;
import org.slf4j.Logger;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

/**
 * This is a basic class that you can use to communicate with ElasticSearch
 * through Jest, which does the talking over REST.
 * 
 * The benefit of using Jest over the ElasticSearchAPI directly, is that you are
 * not bound to a specific version of the ElasticSearch API. (i.e. Changes at
 * the REST level happen much less frequently, and you have greater flexibility
 * with using multiple ElasticSearch versions at customer sites)
 * 
 * @author djue
 * 
 */
public class BasicESDAO implements G_DataAccess {

	/**
	 * Use this flag if you want to supply an es string query that will go
	 * directly into a SearchSourceBuilder.
	 */
	public static final String ESQUERY = "_esquery";
	public static final String ESFIELDS = "_esfields";
	public static final String ESTYPE = "_estype";
	private static final int MAX_TO_GET_AT_ONCE = 1000000;
	private static final int PAGESIZE = 200;
	public static final String ESID = "_esid";
	public static final String ESBOOST = "_esboost";
	/*
	 * can reuse, share globally;
	 */
	protected static ObjectMapper mapper = new ObjectMapper();

	public static ArrayNode getListOfHitsFromResult(final JestResult jestResult) throws JsonParseException,
			JsonMappingException, IOException {
		final JsonNode rootNode = mapper.readValue(jestResult.getJsonString(), JsonNode.class);
		final List<JsonNode> hits = rootNode.get("hits").findValues("hits");

		final ArrayNode actualListOfHits = (ArrayNode) hits.get(0);
		return actualListOfHits;
	}

	private String index;
	protected Logger logger;
	protected ESRestAPIConnection c;
	protected String auth = null;
	protected String type = "_all";
	@Inject
	@Symbol(JestModule.ES_READ_DELAY_MS)
	protected long ES_READ_DELAY_MS;
	@Inject
	@Symbol(JestModule.ES_DEFAULT_TIMEOUT)
	protected String defaultESTimeout;
	@Inject
	@Symbol(G_SymbolConstants.DEFAULT_MAX_SEARCH_RESULTS)
	protected long defaultMaxSearchResults;
	@Inject
	@Symbol(JestModule.ES_SERVER)
	private String host;
	@Inject
	private DataSourceListDAO dao;
	@Inject
	private DocumentBuilder db;
	private final String minimumShouldMatchRule = "1<30%";

	static final String DATE_FORMAT = "yyyy-MM-dd";

	protected BoolQueryBuilder buildBooleanConstraints(final PropertyMatchDescriptorHelper pmdh, BoolQueryBuilder bool) {
		final String key = pmdh.getKey();
		final G_Constraint constraint = pmdh.getConstraint();

		boolean constraintUsed = false;
		boolean createdNew = false;

		if (bool == null) {
			bool = QueryBuilders.boolQuery();
			createdNew = true;
		}
		String[] fieldArray = new String[1];
		fieldArray[0] = key;
		final ArrayList<String> specificFields = dao.getFieldMappings().get(pmdh.getKey());
		if (specificFields != null) {
			fieldArray = specificFields.toArray(new String[specificFields.size()]);
		} else {
			logger.warn("Could not find specific fields for the key " + pmdh.getKey());
		}
		if (ValidationUtils.isValid(pmdh.getSingletonRange())) {
			final String text = (String) pmdh.getSingletonRange().getValue();

			switch (constraint) {
			case EQUALS:
				bool = bool.must(QueryBuilders.multiMatchQuery(text, fieldArray));
				constraintUsed = true;
				break;
			case CONTAINS:
				for (final String sf : fieldArray) {
					bool = bool.should(QueryBuilders.matchPhraseQuery(sf, text));
					constraintUsed = true;
				}
				break;
			case STARTS_WITH:
				for (final String sf : fieldArray) {
					bool = bool.should(QueryBuilders.prefixQuery(sf, text));
					constraintUsed = true;
				}
				break;
			case NOT:
				for (final String sf : fieldArray) {
					bool = bool.mustNot(QueryBuilders.matchPhraseQuery(sf, text));
					constraintUsed = true;
				}
				break;
			case FUZZY_REQUIRED:
				bool = bool.must(QueryBuilders.fuzzyLikeThisQuery(fieldArray).likeText(text));
				constraintUsed = true;
				break;
			default:
				break;
			}
		} else if (ValidationUtils.isValid(pmdh.getListRange())) {
			for (final Object text : pmdh.getListRange().getValues()) {
				switch (constraint) {
				case EQUALS:
					bool = bool.must(QueryBuilders.matchPhraseQuery(key, text));
					constraintUsed = true;
					break;
				case CONTAINS:
					bool = bool.should(QueryBuilders.matchPhraseQuery(key, text));
					constraintUsed = true;
					break;
				case STARTS_WITH:
					bool = bool.must(QueryBuilders.matchPhrasePrefixQuery(key, text));
					constraintUsed = true;
					break;
				case NOT:
					bool = bool.mustNot(QueryBuilders.matchPhraseQuery(key, text));
					constraintUsed = true;
					break;
				default:
					break;
				}
			}
		} else if (ValidationUtils.isValid(pmdh.getBoundedRange())) {
			final G_BoundedRange br = pmdh.getBoundedRange();
			// Enumerate any specific fields that this range can apply to.
			String[] rangeFieldArray = new String[1];
			rangeFieldArray[0] = key;
			final ArrayList<String> specificRangeFields = dao.getRangeMappings().get(pmdh.getKey());
			if (specificRangeFields != null) {
				rangeFieldArray = specificRangeFields.toArray(new String[specificRangeFields.size()]);
			} else {
				logger.warn("Could not find specific fields for the key " + pmdh.getKey());
			}
			String start = null;
			String end = null;
			if (br.getStart() != null) {
				switch (pmdh.getType()) {
				case STRING:
					start = br.getStart().toString();
					break;
				case DATE:
					final DateTime dt = (DateTime) br.getStart();
					start = dt.toString(DATE_FORMAT);
					break;
				default:
					break;
				}
			}
			if (br.getEnd() != null) {
				switch (pmdh.getType()) {
				case STRING:
					end = br.getEnd().toString();
					break;
				case DATE:
					final DateTime dt = (DateTime) br.getEnd();
					end = dt.toString(DATE_FORMAT);
					break;
				default:
					break;
				}
			}
			for (final String sf : rangeFieldArray) {
				if (ValidationUtils.isValid(start, end)) {
					bool = bool.should(QueryBuilders.rangeQuery(sf).from(start).to(end));
					constraintUsed = true;
				} else if (ValidationUtils.isValid(start)) {
					bool = bool.should(QueryBuilders.rangeQuery(sf).gt(start));
					constraintUsed = true;
				} else if (ValidationUtils.isValid(end)) {
					bool = bool.should(QueryBuilders.rangeQuery(sf).lt(end));
					constraintUsed = true;
				}
			}
		} else {
			logger.error("Unknown range type for " + pmdh);
		}
		if ((constraintUsed == false) && (createdNew == true)) {
			// boolean was created but not used.
			bool = null;
		}
		return bool;
	}

	protected io.searchbox.core.Search.Builder buildSearchAction(final G_EntityQuery pq) {
		final Set<String> esTypes = new HashSet<String>();

		final Set<String> esFields = new HashSet<String>();
		final Set<String> esNotFields = new HashSet<String>();
		final Set<String> esIds = new HashSet<String>();
		SearchSourceBuilder ssb = new SearchSourceBuilder();
		if (ValidationUtils.isValid(pq) && ValidationUtils.isValid(pq.getPropertyMatchDescriptors())) {

			BoolQueryBuilder bool = null;

			String queryString = null;

			for (final G_PropertyMatchDescriptor d : pq.getPropertyMatchDescriptors()) {
				final PropertyMatchDescriptorHelper pmdh = PropertyMatchDescriptorHelper.from(d);

				if (ValidationUtils.isValid(pmdh)) {
					final String key = pmdh.getKey();
					if (key.equals(ESTYPE)) {
						if (ValidationUtils.isValid(pmdh.getSingletonRange())) {
							if (ValidationUtils.isValid(pmdh.getSingletonRange().getValue())
									&& pmdh.getSingletonRange().getType().equals(G_PropertyType.STRING)) {
								esTypes.add((String) pmdh.getSingletonRange().getValue());
							}
						} else if (ValidationUtils.isValid(pmdh.getListRange())
								&& ValidationUtils.isValid(pmdh.getListRange().getValues())
								&& pmdh.getListRange().getType().equals(G_PropertyType.STRING)) {
							for (final Object t : pmdh.getListRange().getValues()) {

								esTypes.add((String) t);
							}
						}
						logger.debug("Types are now " + StringUtils.coalesc(", ", esTypes.toArray()));
					} else if (key.equals(ESID)) {
						if (ValidationUtils.isValid(pmdh.getSingletonRange())) {
							if (ValidationUtils.isValid(pmdh.getSingletonRange().getValue())
									&& pmdh.getSingletonRange().getType().equals(G_PropertyType.STRING)) {

								esIds.add((String) pmdh.getSingletonRange().getValue());
							}
						} else if (ValidationUtils.isValid(pmdh.getListRange())
								&& ValidationUtils.isValid(pmdh.getListRange().getValues())
								&& pmdh.getListRange().getType().equals(G_PropertyType.STRING)) {
							for (final Object t : pmdh.getListRange().getValues()) {

								esIds.add((String) t);
							}
						}
						logger.debug("Adding id, ids are now " + StringUtils.coalesc("", esIds.toArray()));
					} else if (key.equals(ESFIELDS)) {
						// TODO: Finish up the logic here, because ES has a
						// different meaning of searching by field, versus
						// excluding it in the return value
						if (ValidationUtils.isValid(pmdh.getSingletonRange())) {
							if (ValidationUtils.isValid(pmdh.getSingletonRange().getValue())
									&& pmdh.getSingletonRange().getType().equals(G_PropertyType.STRING)) {

								if (d.getInclude()) {
									esFields.add((String) pmdh.getSingletonRange().getValue());
								} else {
									esNotFields.add((String) pmdh.getSingletonRange().getValue());
								}
							}
						} else if (ValidationUtils.isValid(pmdh.getListRange())
								&& ValidationUtils.isValid(pmdh.getListRange().getValues())
								&& pmdh.getListRange().getType().equals(G_PropertyType.STRING)) {
							for (final Object t : pmdh.getListRange().getValues()) {
								if (d.getInclude()) {
									logger.debug("Adding field " + t);
									esFields.add((String) t);
								} else {
									logger.debug("Do not include field " + t);
									esNotFields.add((String) t);
								}
							}
						}
					} else if (key.equals(ESQUERY)) {
						if (ValidationUtils.isValid(pmdh.getSingletonRange())
								&& ValidationUtils.isValid(pmdh.getSingletonRange().getValue())
								&& pmdh.getSingletonRange().getType().equals(G_PropertyType.STRING)) {
							logger.debug("Setting custom query " + pmdh.getSingletonRange().getValue());
							queryString = (String) pmdh.getSingletonRange().getValue();

						}
					} else if (key.equals(ESBOOST)) {
						if (ValidationUtils.isValid(pmdh.getSingletonRange())
								&& ValidationUtils.isValid(pmdh.getSingletonRange().getValue())
								&& pmdh.getSingletonRange().getType().equals(G_PropertyType.STRING)) {
							logger.debug("WIP: Setting boost custom query " + pmdh.getSingletonRange().getValue());
							// not working yet
						}
					} else {
						bool = buildBooleanConstraints(pmdh, bool);
					}
				} else {
					logger.error("Key or Value was not valid for " + pmdh.toString());
				}
			}// end looping over pmdhs
			if (queryString != null) {
				logger.debug("We encountered a custom query so we will use that");
			} else if (bool != null) {

				logger.debug("Using a boolean query with rule " + minimumShouldMatchRule);
				bool.minimumShouldMatch(minimumShouldMatchRule);
				queryString = bool.toString();
			} else if (esIds.size() > 0) {
				logger.debug("Using an id filter query instead");
				queryString = QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
						FilterBuilders.termsFilter("_id", esIds)).toString();
			} else {
				logger.debug("Using an matchAllQuery instead");
				queryString = QueryBuilders.matchAllQuery().toString();
			}
			ssb = ssb.query(queryString);
			/*
			 * This is available as long as we don't do scan.
			 */
			// ssb = ssb.sort("_id");
			logger.debug(ssb.toString());
		}

		final io.searchbox.core.Search.Builder action = new Search.Builder(ssb.toString()).setParameter("timeout",
				defaultESTimeout);
		if (ValidationUtils.isValid(pq.getTargetSchema())) {
			action.addIndex(pq.getTargetSchema());
			logger.debug("adding specified index: " + pq.getTargetSchema());
		} else if (ValidationUtils.isValid(c.getIndexName())) {
			action.addIndex(c.getIndexName());
			logger.debug("adding default index: " + c.getIndexName());
		} else {
			logger.error("No index to set in query, check your es.properties to set a search index.");
		}

		if (ValidationUtils.isValid(esTypes)) {
			action.addType(esTypes);
			logger.debug("adding types: " + esTypes.toArray());
		}

		action.setParameter(Parameters.SIZE, pq.getMaxResult());

		logger.debug("We built query " + action.build().toString());
		return action;
	}

	public long count() {
		final G_PropertyMatchDescriptor pmdh = G_PropertyMatchDescriptor.newBuilder()
				.setConstraint(G_Constraint.CONTAINS).setKey(ESTYPE)
				.setSingletonRange(new SingletonRangeHelper(type, G_PropertyType.STRING)).build();
		final G_EntityQuery q = new QueryHelper(pmdh);
		return count(q);
	}

	@Override
	public long count(final G_EntityQuery pq) {
		long longCount = 0l;
		try {
			final io.searchbox.core.Count.Builder action = new Count.Builder()
					.setParameter("timeout", defaultESTimeout);
			if (ValidationUtils.isValid(pq) && ValidationUtils.isValid(pq.getPropertyMatchDescriptors())) {

				pq.getPropertyMatchDescriptors().get(0);

				BoolQueryBuilder bool = null;
				for (final G_PropertyMatchDescriptor d : pq.getPropertyMatchDescriptors()) {
					if (ValidationUtils.isValid(d)) {
						final PropertyMatchDescriptorHelper pmdh = PropertyMatchDescriptorHelper.from(d);
						final String key = pmdh.getKey();

						if (key.equals(ESTYPE)) {

							if (ValidationUtils.isValid(pmdh.getSingletonRange())) {
								final String type = (String) pmdh.getSingletonRange().getValue();
								action.addType(type);
								logger.debug("added type: " + type);

							} else if (ValidationUtils.isValid(pmdh.getListRange())) {
								action.addType((Collection<? extends String>) pmdh.getListRange());
							}

						} else {
							bool = buildBooleanConstraints(pmdh, bool);
						}
					}
				}

				SearchSourceBuilder ssb = new SearchSourceBuilder();
				if (bool != null) {
					ssb = ssb.query(bool);
				} else {
					ssb = ssb.query(QueryBuilders.matchAllQuery());
				}
				logger.debug(ssb.toString());
				action.query(ssb.toString());
				if (ValidationUtils.isValid(pq.getTargetSchema())) {
					action.addIndex(pq.getTargetSchema());
					logger.debug("adding specified index: " + pq.getTargetSchema());
				} else if (ValidationUtils.isValid(c.getIndexName())) {
					action.addIndex(c.getIndexName());
					logger.debug("adding default index: " + c.getIndexName());
				} else {
					logger.error("No index to set in query, check your es.properties to set a search index.");
				}

			}
			final CountResult result = c.getClient().execute(action.build());
			longCount = result.getCount().longValue();
			return longCount;
		} catch (final Exception e) {
			logger.error("performCount Could not connect to one of the external resources needed for your request: "
					+ e.getMessage());
		}
		logger.debug("Found a count of: " + longCount);
		return longCount;
	}

	public void createIndex(final String indexName) throws Exception {
		logger.debug("Creating index " + indexName + " with client " + c.getClient().toString());
		// create new index (if u have this in elasticsearch.yml and prefer
		// those defaults, then leave this out
		final ImmutableSettings.Builder settings = ImmutableSettings.settingsBuilder();
		settings.put("number_of_shards", 3);
		settings.put("number_of_replicas", 0);
		final JestResult execute = c.getClient().execute(
				new CreateIndex.Builder(indexName).settings(settings.build().getAsMap())
						.setParameter("timeout", defaultESTimeout).build());
		logger.debug(execute.toString());
	}

	public boolean delete(final String id) {
		boolean success = false;
		try {
			final JestResult result = c.getClient().execute(
					(new Delete.Builder(id)).index(index).type(type).setParameter("timeout", defaultESTimeout).build());
			success = result.isSucceeded();
			if (success) {
				logger.debug("Successfully deleted id " + id);
			} else {
				logger.error("Could not delete id '" + id + "' : " + result.getJsonString());
			}
		} catch (final Exception e) {
			logger.error("delete " + e.getMessage());
		}

		return success;
	}

	protected void deleteIndex(final String indexName) throws Exception {
		c.deleteIndex(indexName);
	}

	public boolean exists(final String id) {
		final String query = new SearchSourceBuilder().query(
				QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(), FilterBuilders.termFilter("_id", id)))
				.toString();
		try {
			final CountResult result = c.getClient().execute(
					new Count.Builder().query(query).addIndex(index).addType(type)
							.setParameter("timeout", defaultESTimeout).build());
			logger.debug("Exists found count " + result.getCount());
			return (result.getCount() > 0);
		} catch (final Exception e) {
			logger.error("Error determining existence " + e.getMessage());
		}
		logger.error("Error determining existence ");
		return false;
	}

	@Override
	public Map<String, List<G_Entity>> getAccounts(final List<String> entities) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public G_SearchResults getAll(final long offset, final long maxResults) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public JestResult getAllResults() {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.matchAllQuery()).size(MAX_TO_GET_AT_ONCE);
		final Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(index).addType(type)
				.setParameter("timeout", defaultESTimeout).build();
		logger.debug(searchSourceBuilder.toString());
		JestResult result = new JestResult(null);
		try {
			result = c.getClient().execute(search);
		} catch (final Exception e) {
			logger.error("Get all: " + e.getMessage());
		}
		return result;
	}

	@Override
	public G_TransactionResults getAllTransactions(final G_EntityQuery q) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public JestResult getByField(final String field, final String value) {
		JestResult result = new JestResult(null);
		if (ValidationUtils.isValid(field, value)) {
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			// Use the match phrase query so it doesn't tokenize the value.
			searchSourceBuilder.query(QueryBuilders.matchPhraseQuery(field, value));
			final Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(index).addType(type)
					.setParameter("timeout", defaultESTimeout).build();
			logger.debug(searchSourceBuilder.toString());

			try {
				result = c.getClient().execute(search);
			} catch (final Exception e) {
				logger.error("Problem getting by field: " + field + " value: " + value, e.getMessage());
			}
		} else {
			logger.error("A null field or value was provided.");
		}
		return result;
	}

	public JestResult getByJoinFields(final String field1, final String value1, final String field2, final String value2) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
		searchSourceBuilder.query(QueryBuilders.boolQuery().must(QueryBuilders.matchQuery(field1, value1))
				.must(QueryBuilders.matchQuery(field2, value2)));
		final Search search = new Search.Builder(searchSourceBuilder.toString()).addIndex(index).addType(type)
				.setParameter("timeout", defaultESTimeout).build();
		JestResult result = new JestResult(null);
		try {
			result = c.getClient().execute(search);
		} catch (final Exception e) {
			logger.error("User Roles for user: " + e.getMessage());
		}
		return result;
	}

	@Override
	public G_PropertyDescriptors getDescriptors() throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<G_Entity> getEntities(final List<String> entities, final G_LevelOfDetail levelOfDetail)
			throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<String, List<G_Link>> getFlowAggregation(final List<String> entities, final List<String> focusEntities,
			final G_DirectionFilter direction, final G_LinkEntityTypeFilter entityType, final G_LinkTag tag,
			final G_DateRange date) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getIdByFirstHit(final JestResult jr) {
		String id = null;
		try {
			id = getListOfHitsFromResult(jr).get(0).get("_id").asText();
		} catch (final Exception e) {
			logger.error("Problem getting id from first hit: ", e);
		}

		return id;
	}

	public String getIndex() {
		return index;
	}

	public long getModifiedTime() {
		return DateTime.now().getMillis();
	}

	@Override
	public double getReadiness() throws AvroRemoteException {
		// TODO Auto-generated method stub
		return 0;
	}

	/**
	 * When given just the id. Uses default index and no types for this dao.
	 * 
	 * @param id
	 * @return
	 */
	protected JestResult getResultsById(final String id) {
		// DONT use _all for type when searching by id.
		return getResultsById(id, index, null);
	}

	/**
	 * When given the id and a type (that is not _all). Uses default index for
	 * this dao.
	 * 
	 * @param id
	 * @param type
	 * @return
	 */
	protected JestResult getResultsById(final String id, final String type) {
		return getResultsById(id, index, type);
	}

	/**
	 * When given an id a type and an index.
	 * 
	 * @param id
	 * @param customIndex
	 * @param customType
	 * @return
	 */
	protected JestResult getResultsById(final String id, final String customIndex, final String customType) {
		final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

		searchSourceBuilder.query(QueryBuilders.filteredQuery(QueryBuilders.matchAllQuery(),
				FilterBuilders.termFilter("_id", id)));
		final Builder sb = new Search.Builder(searchSourceBuilder.toString()).setParameter("timeout", defaultESTimeout);
		logger.debug(searchSourceBuilder.toString());
		if (ValidationUtils.isValid(customIndex)) {
			sb.addIndex(customIndex);
		} else {
			// sb.addIndex("_all");
		}
		if (ValidationUtils.isValid(customType)) {
			sb.addType(customType);
		}

		JestResult result = new JestResult(null);

		try {
			final Search search = sb.build();
			logger.debug("Using schema " + customIndex);
			logger.debug("Using type " + customType);
			result = c.getClient().execute(search);
			logger.debug(result.getJsonString());
		} catch (final Exception e) {
			logger.error("getResultsById: " + e.getMessage());
		}
		return result;
	}

	public JestResult getSpecifiedFields(final String... field) {
		JestResult result = new JestResult(null);
		final SearchSourceBuilder ssb = new SearchSourceBuilder().query(QueryBuilders.matchAllQuery())
				.size(MAX_TO_GET_AT_ONCE).sort("_id");
		if (ValidationUtils.isValid(field)) {
			ssb.fields(field);
		}
		final Search search = new Search.Builder(ssb.toString()).addIndex(index).addType(type)
				.setParameter("timeout", defaultESTimeout).build();
		logger.debug(ssb.toString());

		try {

			result = c.getClient().execute(search);
		} catch (final Exception e) {
			logger.error("Problem getting by ids with field: " + field, e.getMessage());
		}
		return result;
	}

	@Override
	public Map<String, List<G_Link>> getTimeSeriesAggregation(final List<String> entities,
			final List<String> focusEntities, final G_LinkTag tag, final G_DateRange date) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public String getType() {
		return type;
	}

	public boolean indexExists() {
		boolean success = false;
		if (!ValidationUtils.isValid(index)) {
			logger.warn("Index variable was not initialized! Cannot check for existence.");
		} else {
			try {
				final JestResult result = c.getClient().execute(new IndicesExists.Builder(index).build());
				success = result.isSucceeded();
			} catch (final Exception e) {
				logger.error("indexExists " + e.getMessage());
			}
		}
		return success;

	}

	public void initialize() {
		logger.debug("Initializing index " + index + " and type " + type);

		if (ValidationUtils.isValid(index)) {
			if (indexExists()) {
				logger.debug("Index " + index + " already exists.  This is fine.");
			} else {
				try {
					createIndex(index);
				} catch (final Exception e) {
					logger.error("Problem initializing index: " + index, e);
				}
			}
		} else {
			logger.error("Could not check for existance of index because index variable was not defined.");
		}
	}

	@Override
	public boolean isReady() throws AvroRemoteException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean performCallback(final long offset, final long maxResults, final G_CallBack cb, final G_EntityQuery pq) {
		JestResult jestResult = new JestResult(null);

		try {

			final boolean scrolling = false;
			if (scrolling) {
				io.searchbox.core.Search.Builder action = buildSearchAction(pq);
				final Search build = action.build();
				action.addIndex(pq.getTargetSchema());
				action.setParameter(Parameters.SEARCH_TYPE, SearchType.SCAN);
				action = action.setParameter(Parameters.SIZE, maxResults);
				action = action.setParameter(Parameters.SCROLL, "1m");

				/**
				 * Make a search object first.
				 */
				jestResult = c.getClient().execute(action.build());

				// The first query will not have any results, just the scroll id

				if (jestResult.isSucceeded()) {
					logger.debug("execution completed (expected no results, just setting up scroll)");
					// logger.debug(jestResult.getJsonString());
					int currentResultSize = 0;
					int pageNumber = 1;
					do {
						final String scrollId = jestResult.getJsonObject().get("_scroll_id").getAsString();
						logger.debug("Next scroll id is " + scrollId);
						final SearchScroll scroll = new SearchScroll.Builder(scrollId, "1m").build();
						final TimeReporter tr = new TimeReporter("Executing scroll " + pageNumber, logger);
						jestResult = c.getClient().execute(scroll);
						tr.logAsCompleted();
						// Get the next scroll id
						final ArrayNode actualListOfHits = getListOfHitsFromResult(jestResult);
						currentResultSize = actualListOfHits.size();
						for (int i = 0; i < actualListOfHits.size(); i++) {
							final JsonNode currentHit = actualListOfHits.get(i);
							if (ValidationUtils.isValid(currentHit)) {
								final G_SearchResult sr = db.buildSearchResultFromDocument(i, currentHit, pq);
								cb.execute(sr, pq);
							}
						}
						logger.debug("finished scrolling page # " + pageNumber++ + " which had " + currentResultSize
								+ " hits.");

					} while (currentResultSize > 0);
				} else {
					logger.error("Scroll failed with " + jestResult.getErrorMessage());
				}
			} else {
				// Not scrolling
				io.searchbox.core.Search.Builder action = buildSearchAction(pq);
				// action.setParameter(Parameters.SEARCH_TYPE, SearchType.SCAN)
				action.addIndex(pq.getTargetSchema());
				action = action.setParameter(Parameters.SIZE, maxResults);

				// The first query will not have any results, just the scroll id

				int currentResultSize = 0;
				// int pageNumber = 0;
				// do {
				jestResult = c.getClient().execute(action.build());
				// logger.debug(jestResult.getJsonString());
				final JsonNode rootNode = mapper.readValue(jestResult.getJsonString(), JsonNode.class);
				final JsonNode outerHits = rootNode.get("hits");
				if (outerHits == null) {
					currentResultSize = 0;
					logger.warn("Found 0 results");
				} else {
					final List<JsonNode> hits = outerHits.findValues("hits");

					final ArrayNode actualListOfHits = (ArrayNode) hits.get(0);
					currentResultSize = actualListOfHits.size();
					logger.debug("Found " + currentResultSize + " results");
					for (int i = 0; i < actualListOfHits.size(); i++) {
						final JsonNode currentHit = actualListOfHits.get(i);
						if (ValidationUtils.isValid(currentHit)) {
							/**
							 * TODO: To improve speed, let's make a second
							 * callback option that takes an Object. From there
							 * the documentBuilder will have access to the raw
							 * object for a short while, and it can pull out the
							 * id of the document so see if we've already run
							 * through it, BEFORE doing the expensive casting to
							 * DTO object.
							 */
							final G_SearchResult sr = db.buildSearchResultFromDocument(i, currentHit, pq);
							// logger.debug("Executing callback with sr = " +
							// sr);
							cb.execute(sr, pq);
						}
					}
					logger.debug("finished executing on " + currentResultSize + " hits.");
				}
			}
		} catch (final Exception e) {
			e.printStackTrace();
		}
		return true;
	}

	public void recreateIndex() {
		try {
			deleteIndex(index);
		} catch (final Exception e) {
			logger.error("recreateIndex " + e.getMessage());
		}
		try {
			createIndex(index);
		} catch (final Exception e) {
			logger.error("recreateIndex " + e.getMessage());
		}
	}

	/**
	 * 
	 * @param g
	 * @param id
	 * @param indexName
	 * @param type
	 * @param useDelay
	 * @return the id used by the saved object.
	 */
	@Override
	public String saveObject(final Object g, final String id, final String indexName, final String type,
			final boolean useDelay) {
		Index saveAction;
		if (!ValidationUtils.isValid(id)) {
			// saving without an id.
			saveAction = new Index.Builder(g).index(indexName).type(type).setParameter("timeout", defaultESTimeout)
					.build();
		} else {
			// saving with a given id.
			saveAction = new Index.Builder(g).index(indexName).id(id).type(type)
					.setParameter("timeout", defaultESTimeout).build();
		}
		String generatedId = null;
		try {

			final JestResult result = c.getClient().execute(saveAction);
			final Object oid = result.getValue("_id");
			if (ValidationUtils.isValid(oid)) {
				generatedId = oid.toString();
				if (useDelay && (ES_READ_DELAY_MS > 0)) {
					Thread.sleep(ES_READ_DELAY_MS);
				}
			} else {
				logger.error("Error getting saved object's id: " + result.getJsonString());
				generatedId = null;
			}
		} catch (ExecutionException | InterruptedException | IOException e) {
			logger.error("saveObject " + e.getMessage());
		} catch (final Exception e) {
			logger.error("saveObject " + e.getMessage());
		}
		return generatedId;
	}

	@Override
	public G_SearchResults search(final G_EntityQuery pq) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setIndex(final String index) {
		this.index = index;
	}

	@Override
	public Void setReady(final boolean b) throws AvroRemoteException {
		// TODO Auto-generated method stub
		return null;
	}

	public void setType(final String type) {
		this.type = type;
	}
}
