package ${package}.dao.impl;

import ${package}.dao.GraphTraversalRuleService;
import graphene.model.idl.G_SearchType;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class GraphTraversalRuleServiceImpl implements GraphTraversalRuleService {
	Map<String, G_SearchType> typeQueryMap = new HashMap<String, G_SearchType>();

	@Inject
	Logger logger;

	public GraphTraversalRuleServiceImpl(final Map<String, G_SearchType> externalConfig) {
		typeQueryMap = externalConfig;
	}

	@Override
	public void addRule(final String nodeType, final G_SearchType queryType) {
		typeQueryMap.put(nodeType, queryType);
	}

	@Override
	public G_SearchType getRule(final String nodeType) {
		G_SearchType rule = typeQueryMap.get(nodeType);
		if (null == rule) {
			// default search type
			rule = G_SearchType.COMPARE_EQUALS;
			logger.warn("Could not find a search type for node type " + nodeType + ", using " + rule);
		}
		logger.debug("Using rule " + rule + " for type " + nodeType);
		return rule;
	}

	/**
	 * @return the typeQueryMap
	 */
	@Override
	public Map<String, G_SearchType> getTypeQueryMap() {
		return typeQueryMap;
	}

	/**
	 * @param typeQueryMap
	 *            the typeQueryMap to set
	 */
	@Override
	public void setTypeQueryMap(final Map<String, G_SearchType> typeQueryMap) {
		this.typeQueryMap = typeQueryMap;
	}
}
