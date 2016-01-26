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

import graphene.dao.G_Parser;
import graphene.dao.StopWordService;
import graphene.model.idl.G_Entity;
import graphene.model.idl.G_Property;
import graphene.model.idl.G_PropertyTag;
import graphene.model.idl.G_PropertyType;
import graphene.model.idl.G_SearchResult;
import graphene.model.idlhelper.PropertyHelper;
import graphene.util.StringUtils;
import graphene.util.validator.ValidationUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import mil.darpa.vande.generic.V_GenericNode;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.DoubleNode;

public abstract class BasicParserESImpl<T> implements G_Parser<T> {

	protected ObjectMapper mapper = new ObjectMapper();
	Logger logger = LoggerFactory.getLogger(BasicParserESImpl.class);
	protected List<String> supported;
	@Inject
	protected StopWordService stopwordService;

	protected boolean parenting = false;

	/**
	 * Safely add data to a node even if you supplied a null node var.
	 * 
	 * @param node
	 * @param key
	 * @param value
	 */
	public void addSafeData(final V_GenericNode node, final String key, final String value) {
		if (ValidationUtils.isValid(node)) {
			node.addData(key, value);
		}
	}

	protected void addSafeString(final Collection<String> datesFiled, final Object o) {
		if (ValidationUtils.isValid(datesFiled, o)) {
			datesFiled.add(o.toString());
		}
	}

	protected G_Property addSafeString(final String style, final G_PropertyType nodeType, final String... s) {
		final String coalesc = graphene.util.StringUtils.coalesc(" ", s);
		G_Property gp = null;
		if (ValidationUtils.isValid(coalesc)) {
			final List<G_PropertyTag> tags = new ArrayList<G_PropertyTag>();
			gp = new PropertyHelper(coalesc, coalesc, coalesc, nodeType, tags);
			gp.setStyleType(style);
		}
		return gp;
	}

	protected G_Property addSafeStringWithTitle(final String style, final G_PropertyType nodeType, final String title,
			final String... s) {
		final String searchString = graphene.util.StringUtils.coalesc(" ", s);
		G_Property gp = null;
		if (ValidationUtils.isValid(searchString)) {
			final List<G_PropertyTag> tags = new ArrayList<G_PropertyTag>();
			if (ValidationUtils.isValid(title)) {
				gp = new PropertyHelper(searchString, StringUtils.coalesc(" ", title + ":", searchString),
						searchString, nodeType, tags);
			} else {
				gp = new PropertyHelper(searchString, searchString, searchString, nodeType, tags);
			}

			gp.setStyleType(style);
		} else {
			// logger.error("Invalid property " + title + " nodetype " +
			// nodeType + " s=" + s);
		}
		return gp;
	}

	protected <T> T getClassFromJSON(final JsonNode json, final Class<T> clazz) {
		T t = null;
		try {
			t = mapper.readValue(json.findValue("_source").toString(), clazz);
		} catch (final IOException e) {
			e.printStackTrace();
		}
		return t;
	}

	@Override
	public <T> T getDTO(final G_SearchResult sr, final Class<T> clazz) {
		if (ValidationUtils.isValid(sr)) {
			final G_Entity e = (G_Entity) sr.getResult();
			final Object object = PropertyHelper.getSingletonValue(e.getProperties().get(G_Parser.DTO));
			return (T) object;
		} else {
			return null;
		}
	}

	public String getRawJsonFromSource(final JsonNode json) {
		return json.findValue("_source").toString();
	}

	public double getScore(final JsonNode n) {
		return ((DoubleNode) n.findValue("_score")).asDouble(0.0d);
	}

	/**
	 * @return the supported
	 */
	public final List<String> getSupported() {
		return supported;
	}

	@Override
	public List<String> getSupportedObjects() {
		return supported;
	}

	/**
	 * @return the parenting
	 */
	public boolean isParenting() {
		return parenting;
	}

	public void setParent(final V_GenericNode parent, final V_GenericNode child) {
		if (parenting) {
			addSafeData(child, "parent", parent.getId());
		}
	}

	/**
	 * @param parenting
	 *            the parenting to set
	 */
	public void setParenting(final boolean parenting) {
		this.parenting = parenting;
	}
}
