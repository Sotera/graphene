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

package graphene.dao.es.impl;

import graphene.dao.DocumentBuilder;
import graphene.dao.G_Parser;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntityQuery;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_SearchResult;
import graphene.model.idlhelper.PropertyHelper;
import graphene.util.validator.ValidationUtils;

import java.util.Collection;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.UsesConfiguration;
import org.slf4j.Logger;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;

@UsesConfiguration(G_Parser.class)
public class MultiDocumentBuilderESImpl implements DocumentBuilder {

	protected ObjectMapper mapper = new ObjectMapper();

	@Inject
	protected Logger logger;

	protected Collection<G_Parser> singletons;

	public MultiDocumentBuilderESImpl() {
	}

	public MultiDocumentBuilderESImpl(final Collection<G_Parser> singletons) {
		this.singletons = singletons;
	}

	@Override
	public G_SearchResult buildSearchResultFromDocument(final int index, final JsonNode hit, final G_EntityQuery sq) {
		G_SearchResult sr = null;
		try {
			final JsonNode source = hit.findValue("_source");
			if (ValidationUtils.isValid(source)) {
				final JsonNode typeNode = hit.get("_type");
				// logger.debug("typenode " + typeNode.textValue());
				if (typeNode != null) {
					final String type = typeNode.asText();
					final G_Parser delegate = getParserForObject(type);
					if (delegate != null) {
						final DoubleNode score = (DoubleNode) hit.findValue("_score");
						if (score == null) {
							logger.error("Could not find the score of result. There may be something wrong with your ElasticSearch instance");
						}

						synchronized (delegate) {
						    final G_Entity entity = delegate.buildEntityFromDocument(hit, sq);
    						if (entity != null) {
    
    							entity.getProperties().put(G_Parser.SCORE,
    									new PropertyHelper(G_Parser.SCORE, score.asDouble(0.0d), G_PropertyTag.STAT));
    							entity.getProperties()
    									.put(G_Parser.CARDINAL_ORDER,
    											new PropertyHelper(G_Parser.CARDINAL_ORDER, new Long(index + 1),
    													G_PropertyTag.STAT));
    
    							sr = new G_SearchResult(score.asDouble(0.0d), entity);
    						} else {
    							logger.error("Delegate was unable to build entity from document.");
    						}
						}

					} else {
						logger.error("Could not find parser for type " + type);
					}
				} else {
					logger.error("Could not find the type of result. There may be something wrong with your ElasticSearch instance");
				}
			} else {
				logger.error("Could not find the source of result. There may be something wrong with your ElasticSearch instance");
			}
		} catch (final Exception e) {
			e.printStackTrace();
			logger.error(e.getMessage());
		}
		return sr;
	}

	@Override
	public G_Parser getParserForObject(final String typeToParse) {
		G_Parser dgp = null;
		if (typeToParse == null) {
			logger.warn("Object was invalid");
		} else {
			if (ValidationUtils.isValid(singletons)) {
				for (final G_Parser s : singletons) {
					if (s.getSupportedObjects().contains(typeToParse)) {
						// logger.debug("Found DocumentGraphParser which supports "
						// + s.getSupportedObjects());
						dgp = s;
					}
				}
			} else {
				logger.error("You must contribute parsers to the DocumentBuilder implementation");
			}
		}
		if (dgp == null) {
			logger.error("No handler for class " + typeToParse
					+ ".  Check to make sure your parsers have the correct supported types (case sensitive)");
		}
		return dgp;
	}
}
