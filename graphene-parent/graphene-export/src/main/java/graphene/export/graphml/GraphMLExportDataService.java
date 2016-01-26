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

import graphene.export.ExportDataService;
import graphene.services.store.ConflictException;
import graphene.services.store.ContentService;
import graphene.services.store.ContentService.Document;
import graphene.services.store.ContentService.DocumentDescriptor;

import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map.Entry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class GraphMLExportDataService implements ExportDataService {

	/**
	 * Add definition keys to a given GraphML. These keys specify how the node
	 * date should look in the output graph, and hence are constant across all
	 * .xml files produced.
	 * 
	 * @param graphML
	 */
	private static void addDefinitionKeys(final GraphML graphML) {
		GraphKey gkey = new GraphKey();
		gkey.setid("column");
		gkey.setFor("node");
		gkey.setAttrName("fileColumn");
		gkey.setAttrType("int");
		graphML.getGraph().getkey().add(gkey);

		gkey = new GraphKey();
		gkey.setid("row");
		gkey.setFor("node");
		gkey.setAttrName("fileRow");
		gkey.setAttrType("int");
		graphML.getGraph().getkey().add(gkey);
	}

	private final ContentService service;

	// private final ClusterContextCache contextCache;

	private final JsonNodeFactory factory = new JsonNodeFactory(false);

	// The name of the CMS store we'll use for image captures
	private final static String DEFAULT_STORE = "aperture.render";

	@Inject
	public GraphMLExportDataService(final ContentService service) {
		this.service = service;
		// this.contextCache = contextCache;
	}

	private ByteArrayOutputStream convertJSONToGraphML(final ObjectNode jsonData, final String version)
			throws JsonProcessingException, JAXBException {

		ArrayNode columns, files, links;
		ObjectNode columnObject, file, link, miscData, columnData;
		GraphData data;
		GraphDataXML xmlData;

		final GraphML graphML = new GraphML(); // create Graph and set
												// attributes
		graphML.setversion(version);
		graphML.setGraph(new Graph());
		graphML.getGraph().setid(jsonData.get("xfId").toString());
		graphML.getGraph().setedgedefault("directed");

		graphML.getGraph().setkey(new ArrayList<GraphKey>()); // init Graph
																// collections
		graphML.getGraph().setdata(new ArrayList<GraphDataXML>());
		graphML.getGraph().setnode(new ArrayList<GraphNode>());
		graphML.getGraph().setedge(new ArrayList<GraphEdge>());

		addDefinitionKeys(graphML);

		miscData = new ObjectNode(factory); // add misc data
		miscData.putAll(jsonData);

		xmlData = new GraphDataXML();
		xmlData.setkey("miscData");
		xmlData.setvalue(miscData.toString());
		graphML.getGraph().getdata().add(xmlData);

		columns = (ArrayNode) jsonData.get("children"); // go through columns
														// and
		// add all files
		for (int i = 0; i < columns.size(); i++) {
			columnObject = (ObjectNode) columns.get(i);

			columnData = new ObjectNode(factory); // add column data
			final Iterator<Entry<String, JsonNode>> iter = columnObject.fields();
			while (iter.hasNext()) {
				final Entry<String, JsonNode> field = iter.next();
				if (!field.getKey().equals("children")) {
					columnData.put(field.getKey(), field.getValue());
				}
			}
			xmlData = new GraphDataXML();
			xmlData.setkey("column" + i);
			xmlData.setvalue(columnData.toString());
			graphML.getGraph().getdata().add(xmlData);

			files = (ArrayNode) columnObject.get("children"); // add child data
																// (only files)
			for (int j = 0; j < files.size(); j++) {
				if (files.get(j) instanceof JsonNode) {
					file = (ObjectNode) files.get(j);

					if (file.get("UIType").equals("xfFile")) {
						file.remove("matchUIObject");

						final GraphNode node = new GraphNode(); // create file
																// node
						node.setid((file.get("xfId").asText()));
						node.setdata(new ArrayList<GraphData>());

						data = new GraphData(); // set file column and row
						data.setkey("column");
						data.setvalue(String.valueOf(i));
						node.getdata().add(data);
						data = new GraphData();
						data.setkey("row");
						data.setvalue(String.valueOf(j));
						node.getdata().add(data);

						xmlData = new GraphDataXML(); // set raw json data
						xmlData.setkey("fileJSON");
						xmlData.setvalue(file.toString());
						node.setnodedata(new ArrayList<GraphDataXML>());
						node.getnodedata().add(xmlData);

						// final PermitSet permits = new PermitSet();
						final ArrayNode clusterObject = null;
						// try {
						// final ContextReadWrite contextRW =
						// contextCache.getReadWrite(file.get("xfId").asText(),
						// permits);
						// for (final G_Cluster cluster :
						// contextRW.getClusters()) {
						// for (final G_Property property :
						// cluster.getProperties()) {
						// if (!(property.getRange() instanceof
						// G_SingletonRange)) {
						// continue;
						// }
						// final G_SingletonRange range = (G_SingletonRange)
						// property.getRange();
						// if (!range.getType().equals(G_PropertyType.STRING)) {
						// continue;
						// }
						// property.setRange(new
						// SingletonRangeHelper(XML.escape((String)
						// range.getValue())));
						// }
						// }
						// clusterObject = new
						// ArrayNode(ClusterHelper.toJson(contextRW.getClusters()));
						// } catch (final IOException e) {
						// throw new
						// ResourceException(Status.CLIENT_ERROR_BAD_REQUEST,
						// "Exception during cluster cache processing.", e);
						// } finally {
						// permits.revoke();
						// }

						if (clusterObject != null) {
							xmlData = new GraphDataXML(); // set raw json data
							xmlData.setkey("clusterJSON");
							xmlData.setvalue(clusterObject.toString());
							node.getnodedata().add(xmlData);
						}

						graphML.getGraph().getnode().add(node);

						links = (ArrayNode) file.get("links");
						for (int k = 0; k < links.size(); k++) { // add file
																	// links
							if (links.get(k) instanceof JsonNode) {
								final GraphEdge linkItem = new GraphEdge();
								link = (ObjectNode) links.get(k);

								if (link.get("destination").asText().contains(file.get("xfId").asText())) {
									linkItem.setsource((link.get("source").asText()));
									linkItem.settarget((link.get("destination").asText()));
									graphML.getGraph().getedge().add(linkItem);
								}

								// TODO add edge width/type? (linkType =
								// link.getString("type"))
							}
						}
					}
				}
			}
		}

		final JAXBContext jc = JAXBContext.newInstance(GraphMLUtil.GRAPHML_CLASSES);
		final Marshaller marshaller = jc.createMarshaller();
		marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, GraphMLUtil.SCHEMA_LOCATION);
		marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

		final ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
		marshaller.marshal(graphML, baoStream);

		return baoStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see influent.server.spi.ExportDataService#toXMLDoc(org.json.JsonNode)
	 */
	@Override
	public String exportToXML(final ObjectNode jsonData) throws JsonProcessingException {
		try {
			final ByteArrayOutputStream graphML = convertJSONToGraphML(jsonData, "");
			return graphML.toString("UTF-8");

		} catch (final JAXBException e) {
			throw new JsonMappingException("Failure serializing graph data with JAXB");
			// throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
			// "Failure serializing graph data with JAXB", e);
		} catch (final UnsupportedEncodingException e) {
			throw new JsonMappingException("Failure converting graph data to UTF-8");
			// throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
			// "Failure converting graph data to UTF-8", e);
		}
	}

	@Override
	public DocumentDescriptor exportToXMLDoc(final ObjectNode JSONData, final String version) throws ConflictException,
			JsonProcessingException {

		byte[] xmlData;
		try {
			final ByteArrayOutputStream graphML = convertJSONToGraphML(JSONData, version);
			xmlData = graphML.toByteArray();
		} catch (final JAXBException e) {
			throw new JsonMappingException("Failure serializing graph data with JAXB");
			// throw new ResourceException(Status.SERVER_ERROR_INTERNAL,
			// "Failure serializing graph data with JAXB", e);
		}

		final String xmlType = "application/xml";

		// Store to the content service, return a URL to the image
		final Document doc = service.createDocument();
		doc.setContentType(xmlType);
		doc.setDocument(xmlData);

		// Store and let the content service pick the id
		return service.storeDocument(doc, DEFAULT_STORE, null, null);
	}
}
