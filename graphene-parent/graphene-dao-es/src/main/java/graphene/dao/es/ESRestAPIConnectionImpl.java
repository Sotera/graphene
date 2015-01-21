package graphene.dao.es;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.G_SearchTuple;
import graphene.model.query.EntityQuery;
import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestClient;
import io.searchbox.core.Count;
import io.searchbox.core.CountResult;
import io.searchbox.core.Search;
import io.searchbox.core.Search.Builder;
import io.searchbox.core.SearchResult;
import io.searchbox.indices.CreateIndex;

import javax.annotation.Nullable;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.index.query.CommonTermsQueryBuilder;
import org.elasticsearch.index.query.CommonTermsQueryBuilder.Operator;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.highlight.HighlightBuilder;
import org.elasticsearch.search.sort.SortBuilders;
import org.slf4j.Logger;

public class ESRestAPIConnectionImpl implements ESRestAPIConnection {
	public static final float DEFAULT_MIN_SCORE = 0.5f;

	@Inject
	private Logger logger;

	@Inject
	private JestClient client;

	@Inject
	@Symbol(JestModule.ES_SEARCH_INDEX)
	private String indexName;
	@Inject
	@Symbol(JestModule.ES_DEFAULT_TIMEOUT)
	private String defaultESTimeout;

	private String createCleanUrl(@Nullable final String basicAuth,
			final String baseUrl) {
		if (basicAuth != null) {
			logger.debug("Auth information provided, using auth info.");
			final String cleanAuth = basicAuth.replaceAll("@", "%40");
			final String cleanURL = baseUrl.replace("http://", "http://"
					+ cleanAuth + "@");
			return cleanURL;
		}
		logger.debug("No auth information provided, using without auth info.");
		return baseUrl;
	}

	@Override
	public void createIndex(final String indexName, final int shards,
			final int replicas) {
		try {
			if ((shards > 0) && (replicas > 0)) {
				logger.debug("Creating index " + indexName + " with " + shards
						+ "shard(s) and " + replicas + " replica(s).");
				final ImmutableSettings.Builder sb = ImmutableSettings
						.settingsBuilder();
				sb.put("number_of_shards", shards);
				sb.put("number_of_replicas", replicas);
				client.execute(new CreateIndex.Builder(indexName).settings(
						sb.build().getAsMap()).build());
			} else {
				logger.debug("Creating index " + indexName
						+ " with default settings");
				client.execute(new CreateIndex.Builder(indexName).build());
			}
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	@Override
	public void createIndex(final String indexName, final String settings) {
		try {
			if (settings != null) {
				logger.debug("Creating index " + indexName + " with settings "
						+ settings);
				client.execute(new CreateIndex.Builder(indexName).settings(
						ImmutableSettings.builder().loadFromSource(settings))
						.build());
			} else {
				logger.debug("Creating index " + indexName
						+ " with default settings");
				client.execute(new CreateIndex.Builder(indexName).build());
			}
		} catch (final Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * @return the client
	 */
	@Override
	public final JestClient getClient() {
		return client;
	}

	@Override
	public String getIndexName() {
		return indexName;
	}

	/**
	 * @param index
	 * @param q
	 * @throws Exception
	 */
	private String performCommonTermsQuery(final EntityQuery q)
			throws Exception {
		final StringBuffer terms = new StringBuffer();
		// Dead simple, just coalesces the values as one long phrase
		for (final G_SearchTuple<String> qi : q.getAttributeList()) {
			terms.append(qi.getValue() + " ");
		}
		final String queryTerms = terms.toString().trim();
		if (ValidationUtils.isValid(queryTerms)) {
			logger.debug("Searching for terms: " + queryTerms + " from query "
					+ q);
			final CommonTermsQueryBuilder qbc = QueryBuilders.commonTerms(
					"_all", queryTerms).lowFreqOperator(Operator.AND);

			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();

			final HighlightBuilder h = new HighlightBuilder().field("NARR");
			searchSourceBuilder.query(qbc).highlight(h)
					.minScore((float) q.getMinimumScore())
					.sort(SortBuilders.scoreSort());
			if (q.getMaxResult() == 0) {
				logger.warn("NO MAX RESULT SUPPLIED FOR EntityQuery!  Setting to 200.");
				q.setMaxResult(200l);
			}
			logger.debug("SSB: \n" + searchSourceBuilder.toString());
			String schema = q.getSchema();
			if (!ValidationUtils.isValid(schema)) {
				schema = indexName;
			}
			final Builder sb = new Search.Builder(
					searchSourceBuilder.toString()).addIndex(schema)
					.setParameter("from", q.getFirstResult())
					.setParameter("size", q.getMaxResult())
					.setParameter("timeout", defaultESTimeout);
			if (ValidationUtils.isValid(q.getFilters())) {
				sb.addType(q.getFilters());
			}
			final Search action = sb.build();

			logger.debug("Action:\n" + action.toString());

			final SearchResult result = client.execute(action);
			final String resultString = result.getJsonString();
			return resultString;
		}
		return null;
	}

	@Override
	public long performCount(@Nullable final String basicAuth,
			final String baseUrl, final String index, final String type,
			final String fieldName, final String term)
			throws DataAccessException {
		try {
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			final QueryBuilder qb = QueryBuilders.matchQuery(fieldName, term);
			final SearchSourceBuilder query = searchSourceBuilder.query(qb);
			final Count action = new Count.Builder().addIndex(index)
					.query(query.toString()).build();
			final CountResult result = client.execute(action);
			final long longCount = result.getCount().longValue();
			logger.debug("Found a count of: " + longCount);
			return longCount;
		} catch (final Exception e) {
			e.printStackTrace();
			throw new DataAccessException(
					"Could not connect to one of the external resources needed for your request: "
							+ e.getMessage());
		}
	}

	private String performIndexQuery(final EntityQuery q) throws Exception {

		if (q.getMaxResult() == 0) {
			logger.warn("NO MAX RESULT SUPPLIED FOR EntityQuery!  Setting to one.");
			q.setMaxResult(1l);
		}
		String schema = q.getSchema();
		if (!ValidationUtils.isValid(schema)) {
			schema = indexName;
		}
		final Builder sb = new Search.Builder("").addIndex(schema)
				.setParameter("from", q.getFirstResult())
				.setParameter("size", q.getMaxResult());
		if (ValidationUtils.isValid(q.getFilters())) {
			sb.addType(q.getFilters());
		}
		final Search action = sb.build();
		logger.debug("Action:\n" + action.toString());

		final SearchResult result = client.execute(action);
		final String resultString = result.getJsonString();
		return resultString;
	}

	private String performMatchQuery(final String index, final EntityQuery q)
			throws Exception {
		final StringBuffer sb = new StringBuffer();
		// Dead simple, just coalesces the values as one long phrase
		for (final G_SearchTuple<String> qi : q.getAttributeList()) {
			sb.append(qi.getValue() + " ");
		}
		final String terms = sb.toString().trim();
		if (ValidationUtils.isValid(terms)) {
			logger.debug("Searching for terms: " + terms + " from query " + q);
			// Let's decide that at least half of the terms listed need to
			// appear.
			Integer halfTerms = q.getAttributeList().size() / 2;
			if (halfTerms <= 1) {
				halfTerms = 1;
			}
			final MatchQueryBuilder qbc = QueryBuilders.matchPhraseQuery(
					"_all", terms.toString());

			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(qbc);
			if (q.getMaxResult() == 0) {
				logger.warn("NO MAX RESULT SUPPLIED FOR EntityQuery!  Setting to one.");
				q.setMaxResult(10l);
			}
			logger.debug("SSB: \n" + searchSourceBuilder.toString());
			final Search action = new Search.Builder(
					searchSourceBuilder.toString()).addIndex(index)
					.setParameter("from", q.getFirstResult())
					.setParameter("size", q.getMaxResult()).build();
			logger.debug("Action:\n" + action.toString());
			final SearchResult result = client.execute(action);
			final String resultString = result.getJsonString();
			return resultString;
		}
		return null;
	}

	@Override
	public String performQuery(final String basicAuth, final String baseurl,
			final EntityQuery q) throws DataAccessException {

		String retval = null;
		try {
			if (q.getAttributeList().get(0).getValue().isEmpty()) {
				retval = performIndexQuery(q);
			} else {
				retval = performCommonTermsQuery(q);
			}
		} catch (final Exception e) {
			e.printStackTrace();
			throw new DataAccessException(
					"Could not connect to one of the external resources needed for your request: "
							+ e.getMessage());
		}
		return retval;
	}

	@Override
	public String performQuery(final String basicAuth, final String baseurl,
			final String index, final String type, final Search action)
			throws DataAccessException {
		String retval = null;
		try {
			final SearchResult result = client.execute(action);
			retval = result.getJsonString();
		} catch (final Exception e) {
			e.printStackTrace();
			throw new DataAccessException(
					"Could not connect to one of the external resources needed for your request: "
							+ e.getMessage());
		}
		return retval;
	}

	@Override
	public String performQuery(@Nullable final String basicAuth,
			final String baseurl, final String index, final String type,
			final String fieldName, final String term, final long from,
			final long size) throws DataAccessException {
		try {
			final SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			final QueryBuilder qb = QueryBuilders.matchQuery(fieldName, term);
			searchSourceBuilder.query(qb);
			final Search action = new Search.Builder(
					searchSourceBuilder.toString()).addIndex(index)
					.setParameter("from", from).setParameter("size", size)
					.build();
			final SearchResult result = client.execute(action);
			final String resultString = result.getJsonString();
			// logger.debug(resultString);
			return resultString;
		} catch (final Exception e) {
			e.printStackTrace();
			throw new DataAccessException(
					"Could not connect to one of the external resources needed for your request: "
							+ e.getMessage());
		}
	}

	/**
	 * @param client
	 *            the client to set
	 */
	public final void setClient(final JestClient client) {
		this.client = client;
	}

	@Override
	public void setIndexName(final String indexName) {
		this.indexName = indexName;
	}
}
