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

/**
 * Copyright (c) 2013-2014 Oculus Info Inc.
 * http://www.oculusinfo.com/
 *
 * Released under the MIT License.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of
 * this software and associated documentation files (the "Software"), to deal in
 * the Software without restriction, including without limitation the rights to
 * use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies
 * of the Software, and to permit persons to whom the Software is furnished to do
 * so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package graphene.export.graphml;

import graphene.export.ImportDataService;
import graphene.model.idl.G_DataAccess;
import graphene.services.store.ConflictException;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GraphMLImportDataService implements ImportDataService {
	private static final Logger s_logger = LoggerFactory.getLogger(GraphMLImportDataService.class);

	/**
	 * Since we're using a REALLY old version of json.jar, org.json.XML does not
	 * distinguish between JsonNode members and ArrayNode members of length 1,
	 * and omits empty ArrayNodes entirely. The client needs the types/contents
	 * to be consistent, so we need to replace certain JsonNodes with ArrayNodes
	 * after unmarshalling. All this function does is place/replace the fieldKey
	 * member of rootObj with a ArrayNode.
	 * 
	 * @param rootObj
	 * @param fieldKey
	 * @throws JsonProcessingException
	 */
	// private static void insertArrayNode(final JsonNode rootObj, final String
	// fieldKey) throws JsonProcessingException {
	// if (!rootObj.has(fieldKey)) {
	// rootObj.put(fieldKey, new ArrayNode(factory));
	// } else {
	// final Object existingObj = rootObj.get(fieldKey);
	// if (existingObj instanceof JsonNode) {
	// final ArrayNode realChildren = new ArrayNode();
	// realChildren.put((JsonNode) existingObj);
	// rootObj.put(fieldKey, realChildren);
	// }
	// }
	// }

	private final G_DataAccess entityAccess;

	// private final ClusterContextCache contextCache;

	private final static JsonNodeFactory factory = new JsonNodeFactory(false);

	@Inject
	public GraphMLImportDataService(final G_DataAccess entityAccess) {
		this.entityAccess = entityAccess;
		// this.contextCache = contextCache;
	}

	private JsonNode convertXMLToJSON(final String xmlData) throws JsonProcessingException, JAXBException {
		// Unmarshall the xml into a GraphML
		final ByteArrayInputStream baiStream = new ByteArrayInputStream(xmlData.getBytes());
		final JAXBContext jc = JAXBContext.newInstance(GraphMLUtil.GRAPHML_CLASSES);
		final Unmarshaller unmarshaller = jc.createUnmarshaller();
		final GraphML graphML = (GraphML) unmarshaller.unmarshal(baiStream);

		// eventually check version for compatibility
		s_logger.info("Importing chart version: {}", graphML.getversion());

		final Graph graph = graphML.getGraph();

		final ObjectNode toReturn = new ObjectNode(factory);
		ObjectNode miscData = null, columnJSON, nodeJSON = null;
		String cachedClusterJSON = null;
		int columnIdx = -1, rowIdx = -1;
		final List<List<JsonNode>> allColumns = new ArrayList<List<JsonNode>>();
		List<JsonNode> currColumn;
		final List<JsonNode> outColumns = new ArrayList<JsonNode>();

		// first, set up the column and misc JSON data contained in the graph
		for (final GraphDataXML data : graph.getdata()) {
			if (data.getkey().startsWith("column")) {
				columnJSON = new ObjectNode(factory);
				columnJSON.put(data.getkey(), data.getvalue());
				// columnJSON = org.json.XML.toJsonNode(data.getvalue());
				columnIdx = Integer.parseInt(data.getkey().substring(6));
				while (columnIdx >= outColumns.size()) {
					outColumns.add(new ObjectNode(factory));
				}

				outColumns.set(columnIdx, columnJSON);
				allColumns.add(new ArrayList<JsonNode>());
			} else if (data.getkey().equals("miscData")) {
				// miscData = XML.toJsonNode(data.getvalue());
				miscData = new ObjectNode(factory);
				miscData.put(data.getkey(), data.getvalue());
			}
		}

		// next, iterate through the graph nodes and place the JsonNode for each
		// into it's proper row/column
		for (final GraphNode node : graph.getnode()) {
			for (final GraphData data : node.getdata()) { // parse the goodies
															// from each file
															// node
				if (data.getkey().equals("column")) {
					columnIdx = Integer.parseInt(data.getvalue());
				} else if (data.getkey().equals("row")) {
					rowIdx = Integer.parseInt(data.getvalue());
				}
			}

			for (final GraphDataXML data : node.getnodedata()) { // parse the
																	// goodies
																	// from each
																	// data node
				if (data.getkey().equals("fileJSON")) {
					// nodeJSON = XML.toJsonNode(data.getvalue());
					nodeJSON = new ObjectNode(factory);
					nodeJSON.put(data.getkey(), data.getvalue());
				} else if (data.getkey().equals("clusterJSON")) {
					cachedClusterJSON = data.getvalue();
				}
			}

			// if ((nodeJSON != null) && nodeJSON.has("clusterUIObject")
			// && !nodeJSON.get("clusterUIObject").toString().equals("null")) {
			// clusterJSON = nodeJSON.get("clusterUIObject"); // do
			// // annoying
			// // cleanup
			// insertArrayNode(clusterJSON, "children");
			// insertArrayNode(clusterJSON.get("spec"), "members");
			// }

			if (cachedClusterJSON != null) {
				// final PermitSet permits = new PermitSet();
				//
				// try {
				// final List<String> entityIds = new LinkedList<String>();
				// final List<G_Cluster> allClusters =
				// ClusterHelper.listFromJson(cachedClusterJSON);
				// ;
				// for (final G_Cluster cluster : allClusters) {
				// entityIds.addAll(cluster.getMembers());
				// for (final G_Property property : cluster.getProperties()) {
				// if (!(property.getRange() instanceof G_SingletonRange)) {
				// continue;
				// }
				// final G_SingletonRange range = (G_SingletonRange)
				// property.getRange();
				// if (!range.getType().equals(G_PropertyType.STRING)) {
				// continue;
				// }
				// property.setRange(new
				// SingletonRangeHelper(StringEscapeUtils.unescapeXml((String)
				// range
				// .getValue())));
				// }
				// }
				//
				// final ContextReadWrite contextRW = contextCache
				// .getReadWrite(nodeJSON.get("xfId").asText(), permits);
				// contextRW.getContext().addClusters(allClusters);
				// contextRW.getContext().addEntities(entityAccess.getEntities(entityIds,
				// G_LevelOfDetail.SUMMARY));
				// contextRW.setSimplifiedContext(allClusters);
				// } catch (final IOException e) {
				// throw new ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
				// "Exception during cluster cache processing.", e);
				// } finally {
				// permits.revoke();
				// }
			}

			currColumn = allColumns.get(columnIdx);
			while (rowIdx >= currColumn.size()) {
				currColumn.add(new ObjectNode(factory));
			}
			currColumn.set(rowIdx, nodeJSON);
		}

		// place the files as children in the outgoing columns array
		for (int i = 0; i < allColumns.size(); i++) {
			columnJSON = (ObjectNode) outColumns.get(i);
			columnJSON.put("children", new ArrayNode(factory).addAll(allColumns.get(i)));
		}

		// finally, place the child columns and misc data in the resulting JSON
		// object
		toReturn.put("children", new ArrayNode(factory).addAll(outColumns));
		toReturn.putAll(miscData);

		return toReturn;
	}

	@Override
	public JsonNode importFromXML(final String xmlData) throws ConflictException, JsonProcessingException,
			JAXBException {
		return convertXMLToJSON(xmlData);
	}
}
