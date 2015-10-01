#set( $symbol_pound = '#' )
#set( $symbol_dollar = '$' )
#set( $symbol_escape = '\' )
package ${package}.dao.impl;

import graphene.dao.GraphTraversalRuleService;
import graphene.model.idl.G_Constraint;

import java.util.HashMap;
import java.util.Map;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

public class GraphTraversalRuleServiceImpl implements GraphTraversalRuleService {
	Map<String, G_Constraint> typeQueryMap = new HashMap<String, G_Constraint>();

	@Inject
	Logger logger;

	public GraphTraversalRuleServiceImpl(final Map<String, G_Constraint> externalConfig) {
		typeQueryMap = externalConfig;
	}

	@Override
	public void addRule(final String nodeType, final G_Constraint queryType) {
		typeQueryMap.put(nodeType, queryType);
	}

	@Override
	public G_Constraint getRule(final String nodeType) {
		G_Constraint rule = typeQueryMap.get(nodeType);
		if (null == rule) {
			// default search type
			rule = G_Constraint.EQUALS;
			logger.warn("Could not find a search type for node type " + nodeType + ", using " + rule);
		}
		logger.debug("Using rule " + rule + " for type " + nodeType);
		return rule;
	}

	/**
	 * @return the typeQueryMap
	 */
	@Override
	public Map<String, G_Constraint> getTypeQueryMap() {
		return typeQueryMap;
	}

	/**
	 * @param typeQueryMap
	 *            the typeQueryMap to set
	 */
	@Override
	public void setTypeQueryMap(final Map<String, G_Constraint> typeQueryMap) {
		this.typeQueryMap = typeQueryMap;
	}
}
