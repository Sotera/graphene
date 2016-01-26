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

package graphene.dao.titan;

import graphene.model.idl.G_PropertyKeyTypeAccess;
import graphene.util.validator.ValidationUtils;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;

import com.thinkaurelius.titan.core.attribute.Geoshape;
import com.tinkerpop.blueprints.Edge;
import com.tinkerpop.blueprints.Graph;
import com.tinkerpop.blueprints.Vertex;

public abstract class AbstractBlueprintImporter implements BlueprintImporter {

	@Inject
	protected G_PropertyKeyTypeAccess propertyKeyTypeAccess;

	protected Graph g;

	@Inject
	private Logger logger;

	public Vertex createOrUpdateNode(final String id, final String idType, final String nodeType,
			final Vertex attachTo, final String relationType, final String relationValue) {
		Vertex a = null;
		if (ValidationUtils.isValid(id)) {
			a = getBasicNode(g, id, nodeType);
			setSafeProperty("idtype", idType, a);
			// now we have a valid node. Attach it to the other node provided.
			if (ValidationUtils.isValid(attachTo)) {
				final Edge e = getSafeEdge(g, attachTo, relationType, a);
				setSafeProperty("value", relationValue, e);
			}
		}
		return a;
	}

	/**
	 * May return null. This is a newer more generic version
	 * 
	 * @param g
	 * @param id
	 * @param type
	 * 
	 * @return
	 */
	public Vertex getBasicNode(final Graph g, final String id, final String type) {
		Vertex v = null;
		if (ValidationUtils.isValid(id)) {
			v = g.getVertex(id);
			if (v == null) {
				v = g.addVertex(id);
				setSafeProperty(type, id, v);
			}
		}
		return v;
	}

	public Vertex getGeoNode(final Graph g, final String lat, final String lon, final String type) {
		Vertex v = null;
		if (ValidationUtils.isValid(lat, lon)) {
			try {
				final Geoshape geo = Geoshape.point(Double.parseDouble(lat), Double.parseDouble(lon));

				if (ValidationUtils.isValid(geo)) {
					v = g.getVertex(geo);
					if (v == null) {
						v = g.addVertex(geo);
						setSafeProperty(type, geo, v);
					}
				}
			} catch (final Exception e) {
				logger.error(e.getMessage());
			}
		}
		return v;
	}

	/**
	 * Creates the edge a - rel - b
	 * 
	 * @param a
	 * @param b
	 * @param g
	 * @param type
	 * @return
	 */
	public Edge getSafeEdge(final Graph g, final Vertex a, final String type, final Vertex b) {
		Edge e = null;
		if (ValidationUtils.isValid(a, b)) {

			final String id = a.getId().toString() + b.getId().toString() + type;
			e = g.addEdge(id, a, b, type);

		}
		return e;
	}

	public void setSafeProperty(final String key, final Object value, final Edge e) {
		if (ValidationUtils.isValid(value, e)) {
			if (e.getProperty(key) == null) {
				e.setProperty(key, value);
			}
		}
	}

	/**
	 * This will only set the value if it's safe
	 * 
	 * @param key
	 * @param value
	 * @param v
	 */
	public void setSafeProperty(final String key, final Object value, final Vertex v) {
		if (ValidationUtils.isValid(value, v)) {
			if (v.getProperty(key) == null) {
				v.setProperty(key, value);
			}
		}
	}
}
