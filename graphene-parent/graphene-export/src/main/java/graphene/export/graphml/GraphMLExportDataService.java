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

import influent.idl.FL_Cluster;
import influent.idl.FL_Property;
import influent.idl.FL_PropertyType;
import influent.idl.FL_SingletonRange;
import influent.idlhelper.ClusterHelper;
import influent.idlhelper.SingletonRangeHelper;
import influent.server.clustering.utils.ClusterContextCache;
import influent.server.clustering.utils.ClusterContextCache.PermitSet;
import influent.server.clustering.utils.ContextReadWrite;
import influent.server.spi.ExportDataService;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import oculus.aperture.spi.store.ConflictException;
import oculus.aperture.spi.store.ContentService;
import oculus.aperture.spi.store.ContentService.Document;
import oculus.aperture.spi.store.ContentService.DocumentDescriptor;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;

import com.google.inject.Inject;

public class GraphMLExportDataService implements ExportDataService {

	private final ContentService service;
	private final ClusterContextCache contextCache;
	
	// The name of the CMS store we'll use for image captures
	private final static String DEFAULT_STORE = "aperture.render";
	
	@Inject
	public GraphMLExportDataService(ContentService service, ClusterContextCache contextCache) {
		this.service = service;
		this.contextCache = contextCache;
	}
	
	@Override
	public DocumentDescriptor exportToXMLDoc(JSONObject JSONData, String version) throws ConflictException, JSONException {
		
		byte[] xmlData;
		try {
			final ByteArrayOutputStream graphML = convertJSONToGraphML(JSONData, version);
			xmlData = graphML.toByteArray();
		} catch (JAXBException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Failure serializing graph data with JAXB", e);
		}
		
		final String xmlType = "application/xml";
		
		// Store to the content service, return a URL to the image
		Document doc = service.createDocument();
		doc.setContentType(xmlType);
		doc.setDocument(xmlData);
		
		// Store and let the content service pick the id
		return service.storeDocument(
			doc, 
			DEFAULT_STORE, 
			null, 
			null
		);
	}
	
	/* (non-Javadoc)
	 * @see influent.server.spi.ExportDataService#toXMLDoc(org.json.JSONObject)
	 */
	@Override
	public String exportToXML(JSONObject jsonData) throws JSONException {
		try {
			final ByteArrayOutputStream graphML = convertJSONToGraphML(jsonData, "");
			return graphML.toString("UTF-8");
					
		} catch (JAXBException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Failure serializing graph data with JAXB", e);
		} catch (UnsupportedEncodingException e) {
			throw new ResourceException(Status.SERVER_ERROR_INTERNAL, "Failure converting graph data to UTF-8", e);
		}
	}
	
	private ByteArrayOutputStream convertJSONToGraphML(JSONObject jsonData, String version) throws JSONException, JAXBException {

		JSONArray columns, files, links;
		JSONObject columnObject, file, link, miscData, columnData;
		GraphData data;
		GraphDataXML xmlData;
		
		GraphML graphML = new GraphML();									// create Graph and set attributes
		graphML.setversion(version);
		graphML.setGraph(new Graph());
		graphML.getGraph().setid(jsonData.get("xfId").toString());
		graphML.getGraph().setedgedefault("directed");
		
		graphML.getGraph().setkey(new ArrayList<GraphKey>());				// init Graph collections
		graphML.getGraph().setdata(new ArrayList<GraphDataXML>());
		graphML.getGraph().setnode(new ArrayList<GraphNode>());
		graphML.getGraph().setedge(new ArrayList<GraphEdge>());
		
		addDefinitionKeys(graphML);
		
		miscData = new JSONObject();									// add misc data
		for(String dataKey : JSONObject.getNames(jsonData)) {
			if(!dataKey.equals("children")) {
				miscData.put(dataKey, jsonData.get(dataKey));
			}
		}
		xmlData = new GraphDataXML();
		xmlData.setkey("miscData");
		xmlData.setvalue(XML.toString(miscData));
		graphML.getGraph().getdata().add(xmlData);
		
    	columns = jsonData.getJSONArray("children");			// go through columns and add all files
    	for (int i = 0; i < columns.length(); i++) {
    		columnObject = columns.getJSONObject(i);
    		
    		columnData = new JSONObject();									// add column data
    		for(String dataKey : JSONObject.getNames(columnObject)) {
    			if(!dataKey.equals("children")) {
    				columnData.put(dataKey, columnObject.get(dataKey));
    			}
    		}
    		xmlData = new GraphDataXML();
    		xmlData.setkey("column"+i);
    		xmlData.setvalue(XML.toString(columnData));
    		graphML.getGraph().getdata().add(xmlData);
    		
            files = columnObject.getJSONArray("children");					// add child data (only files)
        	for (int j = 0; j < files.length(); j++) {
        		if (files.get(j) instanceof JSONObject) {
                	file = files.getJSONObject(j);
                	
                	if(file.get("UIType").equals("xfFile")) {
                		file.remove("matchUIObject");
                		
                       	GraphNode node = new GraphNode();					// create file node
                    	node.setid((file.getString("xfId")));
                    	node.setdata(new ArrayList<GraphData>());
                    	
                    	data = new GraphData();								// set file column and row
                		data.setkey("column");
                		data.setvalue(String.valueOf(i));
                		node.getdata().add(data);
                    	data = new GraphData();
                		data.setkey("row");
                		data.setvalue(String.valueOf(j));
                		node.getdata().add(data);
                    	
                		xmlData = new GraphDataXML();						// set raw json data
                		xmlData.setkey("fileJSON");
                		xmlData.setvalue(XML.toString(file));
                    	node.setnodedata(new ArrayList<GraphDataXML>());
                		node.getnodedata().add(xmlData);
                    	
                		PermitSet permits = new PermitSet();
                		JSONArray clusterObject = null;
                		try {
               				final ContextReadWrite contextRW = contextCache.getReadWrite(file.getString("xfId"), permits);
               				for(FL_Cluster cluster : contextRW.getClusters()) {
               					for(FL_Property property : cluster.getProperties()) {
               						if(!(property.getRange() instanceof FL_SingletonRange)) continue;
               						FL_SingletonRange range = (FL_SingletonRange) property.getRange();
               						if(!range.getType().equals(FL_PropertyType.STRING)) continue;
               						property.setRange(new SingletonRangeHelper(XML.escape((String)range.getValue())));
               					}
               				}
               				clusterObject = new JSONArray(ClusterHelper.toJson(contextRW.getClusters()));
                		} catch (IOException e) {
        					throw new ResourceException(
        							Status.CLIENT_ERROR_BAD_REQUEST,
        							"Exception during cluster cache processing.",
        							e
        						);
						} finally {
                			permits.revoke();
                		}

                		if(clusterObject != null) {
                    		xmlData = new GraphDataXML();						// set raw json data
                    		xmlData.setkey("clusterJSON");
                    		xmlData.setvalue(clusterObject.toString());
                    		node.getnodedata().add(xmlData);
                		}

                    	graphML.getGraph().getnode().add(node);
                    	
                        links = file.getJSONArray("links");
                        for (int k = 0; k < links.length(); k++) {							// add file links
                        	if (links.get(k) instanceof JSONObject) {
                        		GraphEdge linkItem = new GraphEdge();
                        		link = links.getJSONObject(k);
                        		
                        		if(link.getString("destination").contains(file.getString("xfId"))) {
                            		linkItem.setsource((link.getString("source")));
                            		linkItem.settarget((link.getString("destination")));
                            		graphML.getGraph().getedge().add(linkItem);
                        		}

                        		// TODO add edge width/type?  (linkType = link.getString("type"))
                        	}
                        }
                	}
        		}
        	}
    	}
        
    	JAXBContext jc = JAXBContext.newInstance(GraphMLUtil.GRAPHML_CLASSES);
        Marshaller marshaller = jc.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_SCHEMA_LOCATION, GraphMLUtil.SCHEMA_LOCATION);
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        
        ByteArrayOutputStream baoStream = new ByteArrayOutputStream();
        marshaller.marshal(graphML, baoStream);
        
    	return baoStream;
	}
	
	/**
	 * Add definition keys to a given GraphML. These keys specify how the node date should look
	 * in the output graph, and hence are constant across all .xml files produced.
	 * 
	 * @param graphML
	 */
	private static void addDefinitionKeys(GraphML graphML) {
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
}
