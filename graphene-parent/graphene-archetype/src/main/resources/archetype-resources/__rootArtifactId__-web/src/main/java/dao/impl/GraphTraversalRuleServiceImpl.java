/*
 *
 *  Licensed to the Apache Software Foundation (ASF) under one
 *  or more contributor license agreements.  See the NOTICE file
 *  distributed with this work for additional information
 *  regarding copyright ownership.  The ASF licenses this file
 *  to you under the Apache License, Version 2.0 (the
 *  "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 *
 */

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
