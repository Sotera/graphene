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
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_Provenance;
import graphene.model.idl.G_Uncertainty;
import graphene.model.idlhelper.EntityHelper;
import graphene.model.idlhelper.PropertyHelper;
import graphene.util.validator.ValidationUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author djue
 * 
 */
public abstract class AbstractExtractor implements Extractor {

	protected Matcher m;
	protected Pattern p;
	protected int[] groups = { 1 };
	protected final Logger logger = LoggerFactory.getLogger(Extractor.class);

	@Override
	public Collection<String> divideAndExtract(final String source, final String divideRegex) {
		final String[] split = source.split(divideRegex);
		final Set<String> matchList = new HashSet<String>();
		for (String s : split) {
			s = s.trim();
			if (ValidationUtils.isValid(s)) {
				m = p.matcher(s);
				while (m.find()) {
					for (final int groupIndex : groups) {
						final String match = postProcessMatch(m.group(groupIndex));
						if (match != null) {
							matchList.add(match);
						}
					}
				}
			}
		}
		if (matchList.size() > 0) {
			logger.debug("Found " + matchList.size() + " matches");
		}
		return matchList;
	}

	@Override
	public Collection<String> extract(final String source) {
		final Set<String> matchList = new HashSet<String>();
		if (ValidationUtils.isValid(source)) {
			m = p.matcher(source);
			while (m.find()) {
				for (final int groupIndex : groups) {
					final String match = postProcessMatch(m.group(groupIndex));
					if (match != null) {
						matchList.add(match);
					}
				}
			}
		}
		if (matchList.size() > 0) {
			logger.debug("Found " + matchList.size() + " matches");
		}
		return matchList;
	}

	@Override
	public Collection<G_Entity> extractEntities(final String source) {
		final Set<String> matchList = new HashSet<String>();
		if (ValidationUtils.isValid(source)) {
			m = p.matcher(source);
			while (m.find()) {
				matchList.add(postProcessMatch(m.group(1)));
			}
		}
		final ArrayList<G_Entity> list = new ArrayList<G_Entity>();
		for (final String id : matchList) {

			final G_Provenance prov = new G_Provenance("Narrative");
			final G_Uncertainty uncertainty = new G_Uncertainty();
			uncertainty.setConfidence(1.0d);
			final G_Entity extractedIdentifierNode = new EntityHelper(id, getEntityTags(), prov, uncertainty,
					getProperties());
			list.add(postProcessEntity(extractedIdentifierNode));
		}
		return list;
	}

	@Override
	public List<G_EntityTag> getEntityTags() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * @return the groups
	 */
	public int[] getGroups() {
		return groups;
	}

	@Override
	public Map<String, G_Property> getProperties() {
		final Map<String, G_Property> tags = new HashMap<String, G_Property>();
		tags.put(G_PropertyTag.TYPE.name(), new PropertyHelper(G_PropertyTag.TYPE, getNodetype()));
		return tags;
	}

	/**
	 * Override in concrete classes to add additional properties to this entity.
	 * 
	 * @param extractedIdentifierNode
	 * @return
	 */
	public G_Entity postProcessEntity(final G_Entity extractedIdentifierNode) {
		return extractedIdentifierNode;
	}

	/**
	 * Adds the abilty to post process the match so that it more closely
	 * resembles structured entities. Override in your concrete class if needed.
	 * 
	 * @param group
	 * @return
	 */
	public String postProcessMatch(final String match) {
		return match.trim();
	}

	/**
	 * @param groups
	 *            the groups to set
	 */
	public void setGroups(final int[] groups) {
		this.groups = groups;
	}
}