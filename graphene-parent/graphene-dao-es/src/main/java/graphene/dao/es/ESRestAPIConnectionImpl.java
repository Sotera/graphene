package graphene.dao.es;

import graphene.business.commons.exception.DataAccessException;
import graphene.model.idl.G_SearchTuple;
import graphene.model.idl.G_SearchType;
import graphene.model.query.EntityQuery;
import graphene.util.validator.ValidationUtils;
import io.searchbox.client.JestClient;
import io.searchbox.core.Count;
import io.searchbox.core.CountResult;
import io.searchbox.core.Search;
import io.searchbox.core.SearchResult;

import javax.annotation.Nullable;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.elasticsearch.index.query.CommonTermsQueryBuilder;
import org.elasticsearch.index.query.MatchQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.index.query.SimpleQueryStringBuilder;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.slf4j.Logger;

public class ESRestAPIConnectionImpl implements ESRestAPIConnection {
	@Inject
	private Logger logger;

	@Inject
	private JestClient client;

	private String createCleanUrl(@Nullable String basicAuth, String baseUrl) {
		if (basicAuth != null) {
			logger.debug("Auth information provided, using auth info.");
			String cleanAuth = basicAuth.replaceAll("@", "%40");
			String cleanURL = baseUrl.replace("http://", "http://" + cleanAuth
					+ "@");
			return cleanURL;
		}
		logger.debug("No auth information provided, using without auth info.");
		return baseUrl;
	}

	@Override
	public long performCount(@Nullable String basicAuth, String baseUrl,
			String index, String type, String fieldName, String term)
			throws DataAccessException {
		try {
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			QueryBuilder qb = QueryBuilders.matchQuery(fieldName, term);
			SearchSourceBuilder query = searchSourceBuilder.query(qb);
			Count action = new Count.Builder().addIndex(index)
					.query(query.toString()).build();
			CountResult result = client.execute(action);
			long longCount = result.getCount().longValue();
			logger.debug("Found a count of: " + longCount);
			return longCount;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataAccessException(
					"Could not connect to one of the external resources needed for your request: "
							+ e.getMessage());
		}
	}

	@Override
	public String performQuery(@Nullable String basicAuth, String baseurl,
			String index, String type, String fieldName, String term,
			long from, long size) throws DataAccessException {
		try {
			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			QueryBuilder qb = QueryBuilders.matchQuery(fieldName, term);
			searchSourceBuilder.query(qb);
			Search action = new Search.Builder(searchSourceBuilder.toString())
					.addIndex(index).setParameter("from", from)
					.setParameter("size", size).build();
			SearchResult result = client.execute(action);
			String resultString = result.getJsonString();
			// logger.debug(resultString);
			return resultString;
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataAccessException(
					"Could not connect to one of the external resources needed for your request: "
							+ e.getMessage());
		}
	}

	@Override
	public String performQuery(String basicAuth, String baseurl, String index,
			String type, EntityQuery q) throws DataAccessException {
		String retval = null;
		try {
			if (q.getAttributeList().get(0).getValue().isEmpty()) {
				retval = performIndexQuery(index, q);
			} else {
				retval = performMatchQuery(index, q);
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataAccessException(
					"Could not connect to one of the external resources needed for your request: "
							+ e.getMessage());
		}
		return retval;
	}

	private String performIndexQuery(String index, EntityQuery q)
			throws Exception {

		if (q.getMaxResult() == 0) {
			logger.warn("NO MAX RESULT SUPPLIED FOR EntityQuery!  Setting to one.");
			q.setMaxResult(1l);
		}
		Search action = new Search.Builder("").addIndex(index)
				.setParameter("from", q.getFirstResult())
				.setParameter("size", q.getMaxResult()).build();
		logger.debug("Action:\n" + action.toString());
		SearchResult result = client.execute(action);
		String resultString = result.getJsonString();
		return resultString;
	}

	/**
	 * @param index
	 * @param q
	 * @throws Exception
	 */
	private String performCommonTermsQuery(String index, EntityQuery q)
			throws Exception {
		StringBuffer terms = new StringBuffer();
		// Dead simple, just coalesces the values as one long phrase
		for (G_SearchTuple<String> qi : q.getAttributeList()) {
			terms.append(qi.getValue() + " ");
		}
		if (ValidationUtils.isValid(terms)) {
			logger.debug("Searching for terms: " + terms + " from query " + q);
			// Let's decide that at least half of the terms listed need to
			// appear.
			Integer halfTerms = q.getAttributeList().size() / 2;
			if (halfTerms <= 1) {
				halfTerms = 1;
			}
			CommonTermsQueryBuilder qbc = QueryBuilders
					.commonTerms("_all", terms.toString())
					.cutoffFrequency(0.1f)
					.lowFreqMinimumShouldMatch(halfTerms.toString());

			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(qbc);
			if (q.getMaxResult() == 0) {
				logger.warn("NO MAX RESULT SUPPLIED FOR EntityQuery!  Setting to one.");
				q.setMaxResult(1l);
			}
			logger.debug("SSB: \n" + searchSourceBuilder.toString());
			Search action = new Search.Builder(searchSourceBuilder.toString())
					.addIndex(index).setParameter("from", q.getFirstResult())
					.setParameter("size", q.getMaxResult()).build();
			logger.debug("Action:\n" + action.toString());
			SearchResult result = client.execute(action);
			String resultString = result.getJsonString();
			return resultString;
		}
		return null;
	}

	private String performMatchQuery(String index, EntityQuery q)
			throws Exception {
		StringBuffer terms = new StringBuffer();
		// Dead simple, just coalesces the values as one long phrase
		for (G_SearchTuple<String> qi : q.getAttributeList()) {
			terms.append(qi.getValue() + " ");
		}
		if (ValidationUtils.isValid(terms)) {
			logger.debug("Searching for terms: " + terms + " from query " + q);
			// Let's decide that at least half of the terms listed need to
			// appear.
			Integer halfTerms = q.getAttributeList().size() / 2;
			if (halfTerms <= 1) {
				halfTerms = 1;
			}
			MatchQueryBuilder qbc = QueryBuilders.matchPhraseQuery("_all",
					terms.toString());

			SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
			searchSourceBuilder.query(qbc);
			if (q.getMaxResult() == 0) {
				logger.warn("NO MAX RESULT SUPPLIED FOR EntityQuery!  Setting to one.");
				q.setMaxResult(10l);
			}
			logger.debug("SSB: \n" + searchSourceBuilder.toString());
			Search action = new Search.Builder(searchSourceBuilder.toString())
					.addIndex(index).setParameter("from", q.getFirstResult())
					.setParameter("size", q.getMaxResult()).build();
			logger.debug("Action:\n" + action.toString());
			SearchResult result = client.execute(action);
			String resultString = result.getJsonString();
			return resultString;
		}
		return null;
	}
}
