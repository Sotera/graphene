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

package graphene.hts.entityextraction;

import graphene.model.idl.G_Entity;
import graphene.model.idl.G_EntityTag;
import graphene.model.idl.G_Property;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 
 * @author djue
 * 
 */
public interface Extractor {
	/**
	 * Allow large texts to be split into multiple pieces, to improve
	 * performance. For instance, split by newline, tabs and/or periods.
	 * 
	 * @param source
	 * @param divideRegex
	 * @return
	 */
	public abstract Collection<String> divideAndExtract(String body, String regex);

	/**
	 * 
	 * @param source
	 * @return a collection of strings which were found using source
	 */
	public abstract Collection<String> extract(String source);

	/**
	 * 
	 * @param source
	 * @return a collection of G_Entity which were found using source
	 */
	public abstract Collection<G_Entity> extractEntities(String source);

	/**
	 * 
	 * @return a list of entity tags
	 */
	public List<G_EntityTag> getEntityTags();

	/**
	 * 
	 * @return the id type
	 */
	public abstract String getIdType();

	/**
	 * 
	 * @return the node type
	 */
	public abstract String getNodetype();

	/**
	 * 
	 * @return a list of properties
	 */
	public Map<String, G_Property> getProperties();

	/**
	 * 
	 * @return the relation type
	 */
	public abstract String getRelationType();

	/**
	 * 
	 * @return the relation value
	 */
	public abstract String getRelationValue();

}