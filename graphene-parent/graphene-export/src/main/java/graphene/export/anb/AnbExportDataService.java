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
package graphene.export.anb;

import graphene.export.ExportDataService;
import graphene.export.graphml.GraphMLUtil;
import graphene.services.store.ConflictException;
import graphene.services.store.ContentService;
import graphene.services.store.ContentService.Document;
import graphene.services.store.ContentService.DocumentDescriptor;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

public class AnbExportDataService implements ExportDataService {

	private final ContentService service;

	// The name of the CMS store we'll use for image captures
	private final static String DEFAULT_STORE = "aperture.render";

	@Inject
	public AnbExportDataService(final ContentService service) {
		this.service = service;
	}

	private ByteArrayOutputStream convertJSONToGraphML(final ObjectNode jsonData) throws JsonProcessingException,
			JAXBException {

		final Chart chart = new Chart();
		chart.setChartItemCollection(new ChartItemCollection());
		chart.getChartItemCollection().setChartItems(new ArrayList<ChartItem>());

		final ArrayNode columns = (ArrayNode) jsonData.get("columns");
		for (int i = 0; i < columns.size(); i++) {
			final ArrayNode files = (ArrayNode) columns.get(i).get("files");
			for (int j = 0; j < files.size(); j++) {
				if (files.get(j) instanceof JsonNode) {

					final JsonNode file = files.get(j);

					final ChartItem nodeItem = new ChartItem();
					chart.getChartItemCollection().getChartItems().add(nodeItem);

					nodeItem.setAttrLabel(file.get("title").asText());

					nodeItem.setCIStyle(new CIStyle());
					nodeItem.getCIStyle().setFont(new Font());
					nodeItem.getCIStyle().getFont().setAttrPointSize(1);

					nodeItem.setEnd(new End());
					nodeItem.getEnd().setEntity(new Entity());
					nodeItem.getEnd().getEntity().setAttrEntityId(file.get("xfId").asText());
					nodeItem.getEnd().getEntity().setIcon(new Icon());
					nodeItem.getEnd().getEntity().getIcon().setIconStyle(new IconStyle());

					nodeItem.setAttributeCollection(new AttributeCollection());
					nodeItem.getAttributeCollection().setAttributes(new ArrayList<Attribute>());

					final ArrayNode links = (ArrayNode) file.get("links");
					for (int k = 0; k < links.size(); k++) {
						if (links.get(k) instanceof JsonNode) {
							final JsonNode link = links.get(k);

							final ChartItem linkItem = new ChartItem();
							chart.getChartItemCollection().getChartItems().add(linkItem);

							linkItem.setLink(new Link());
							linkItem.getLink().setAttrEnd1Id(file.get("xfId").asText());
							linkItem.getLink().setAttrEnd2Id(link.get("destination").asText());

							linkItem.getLink().setLinkStyle(new LinkStyle());
							linkItem.getLink().getLinkStyle().setAttrType(link.get("type").asText());
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
		marshaller.marshal(chart, baoStream);

		return baoStream;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see influent.server.spi.ExportDataService#toXMLDoc(org.json.JSONObject)
	 */
	@Override
	public String exportToXML(final ObjectNode jsonData) throws JsonProcessingException {
		String retval = null;
		try {
			final ByteArrayOutputStream baoStream = convertJSONToGraphML(jsonData);
			retval = baoStream.toString("UTF-8");
			baoStream.close();
		} catch (final JAXBException e) {
			throw new JsonMappingException("Failure serializing graph data with JAXB");
			// throw new
			// JsonParseException("Failure serializing graph data with JAXB");
		} catch (final UnsupportedEncodingException e) {
			throw new JsonMappingException("Failure converting graph data to UTF-8");
			// throw new
			// JsonParseException("Failure converting graph data to UTF-8");
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return retval;
	}

	@Override
	public DocumentDescriptor exportToXMLDoc(final ObjectNode JSONData, final String version) throws ConflictException,
			JsonProcessingException {

		byte[] XMLdata = null;
		try {
			final ByteArrayOutputStream baoStream = convertJSONToGraphML(JSONData);
			XMLdata = baoStream.toByteArray();
			baoStream.close();
		} catch (final JAXBException e) {
			// throw new JsonParseException(Status.SERVER_ERROR_INTERNAL,
			// "Failure serializing graph data with JAXB", e);
			throw new JsonMappingException("Failure serializing graph data with JAXB");
		} catch (final IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		final String xmlType = "application/xml";

		// Store to the content service, return a URL to the image
		final Document doc = service.createDocument();
		doc.setContentType(xmlType);
		doc.setDocument(XMLdata);

		// Store and let the content service pick the id
		return service.storeDocument(doc, DEFAULT_STORE, null, null);
	}
}
