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
import influent.idl.FL_DataAccess;
import influent.idl.FL_LevelOfDetail;
import influent.idl.FL_Property;
import influent.idl.FL_PropertyType;
import influent.idl.FL_SingletonRange;
import influent.idlhelper.ClusterHelper;
import influent.idlhelper.SingletonRangeHelper;
import influent.server.clustering.utils.ClusterContextCache;
import influent.server.clustering.utils.ClusterContextCache.PermitSet;
import influent.server.clustering.utils.ContextReadWrite;
import influent.server.spi.ImportDataService;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import oculus.aperture.spi.store.ConflictException;

import org.apache.commons.lang.StringEscapeUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.XML;
import org.restlet.data.Status;
import org.restlet.resource.ResourceException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Inject;

public class GraphMLImportDataService implements ImportDataService {
	private static final Logger s_logger = LoggerFactory.getLogger(GraphMLImportDataService.class);
	
	private final FL_DataAccess entityAccess;
	private final ClusterContextCache contextCache;
	
	@Inject
	public GraphMLImportDataService(FL_DataAccess entityAccess, ClusterContextCache contextCache) {
		this.entityAccess = entityAccess;
		this.contextCache = contextCache;
	}
	
	@Override
	public JSONObject importFromXML(String xmlData) throws ConflictException, JSONException, JAXBException {
		return convertXMLToJSON(xmlData);
	}

	private JSONObject convertXMLToJSON(String xmlData) throws JSONException, JAXBException {
		// Unmarshall the xml into a GraphML
		ByteArrayInputStream baiStream = new ByteArrayInputStream(xmlData.getBytes());
    	JAXBContext jc = JAXBContext.newInstance(GraphMLUtil.GRAPHML_CLASSES);
        Unmarshaller unmarshaller = jc.createUnmarshaller();
        GraphML graphML = (GraphML) unmarshaller.unmarshal(baiStream);

        // eventually check version for compatibility
	    s_logger.info("Importing chart version: {}", graphML.getversion());

        Graph graph = graphML.getGraph();

        JSONObject toReturn = new JSONObject(), miscData = null, columnJSON, nodeJSON = null, clusterJSON;
        String cachedClusterJSON = null;
        int columnIdx = -1, rowIdx = -1;
        List<List<JSONObject>> allColumns = new ArrayList<List<JSONObject>>();
        List<JSONObject> currColumn, outColumns = new ArrayList<JSONObject>();
        
        // first, set up the column and misc JSON data contained in the graph
        for(GraphDataXML data : graph.getdata()) {
        	if(data.getkey().startsWith("column")) {
        		columnJSON = XML.toJSONObject(data.getvalue());
        		columnIdx = Integer.parseInt(data.getkey().substring(6));
               	while(columnIdx >= outColumns.size()) { outColumns.add(new JSONObject()); }
        		
        		outColumns.set(columnIdx, columnJSON);
        		allColumns.add(new ArrayList<JSONObject>());
        	}
        	else if(data.getkey().equals("miscData")) {
        		miscData = XML.toJSONObject(data.getvalue());
        	}
        }

        // next, iterate through the graph nodes and place the JSONObject for each into it's proper row/column
        for(GraphNode node : graph.getnode()) {
        	for(GraphData data : node.getdata()) {	// parse the goodies from each file node
        		if(data.getkey().equals("column")) {
        			columnIdx = Integer.parseInt(data.getvalue());
        		}
        		else if(data.getkey().equals("row")) {
        			rowIdx = Integer.parseInt(data.getvalue());
        		}
        	}
        	
        	for(GraphDataXML data : node.getnodedata()) {	// parse the goodies from each data node
        		if(data.getkey().equals("fileJSON")) {
                	nodeJSON = XML.toJSONObject(data.getvalue());
        		} else if(data.getkey().equals("clusterJSON")) {
        			cachedClusterJSON = data.getvalue();
        		}
        	}

        	if(nodeJSON != null && nodeJSON.has("clusterUIObject") && !nodeJSON.get("clusterUIObject").toString().equals("null")) {
            	clusterJSON = nodeJSON.getJSONObject("clusterUIObject");		// do annoying cleanup
            	insertJSONArray(clusterJSON, "children");
            	insertJSONArray(clusterJSON.getJSONObject("spec"), "members");
        	}
        	
			if(cachedClusterJSON != null) {
				PermitSet permits = new PermitSet();

   				try {
   					List<String> entityIds = new LinkedList<String>();
   					List<FL_Cluster> allClusters = ClusterHelper.listFromJson(cachedClusterJSON);
;
   	   				for(FL_Cluster cluster : allClusters) {
   	   					entityIds.addAll(cluster.getMembers());
   	   					for(FL_Property property : cluster.getProperties()) {
   	   						if(!(property.getRange() instanceof FL_SingletonRange)) continue;
   	   						FL_SingletonRange range = (FL_SingletonRange) property.getRange();
   	   						if(!range.getType().equals(FL_PropertyType.STRING)) continue;
   	   						property.setRange(new SingletonRangeHelper(StringEscapeUtils.unescapeXml((String)range.getValue())));
   	   					}
   	   				}

					final ContextReadWrite contextRW = contextCache.getReadWrite(nodeJSON.getString("xfId"), permits);
					contextRW.getContext().addClusters(allClusters);
					contextRW.getContext().addEntities(entityAccess.getEntities(entityIds, FL_LevelOfDetail.SUMMARY));
					contextRW.setSimplifiedContext(allClusters);
				} catch (IOException e) {
					throw new ResourceException(
							Status.CLIENT_ERROR_BAD_REQUEST,
							"Exception during cluster cache processing.",
							e
						);
				} finally {
					permits.revoke();
				}
        	}

        	currColumn = allColumns.get(columnIdx);
        	while(rowIdx >= currColumn.size()) { currColumn.add(new JSONObject()); }
        	currColumn.set(rowIdx, nodeJSON);
        }
        
        // place the files as children in the outgoing columns array
        for(int i = 0; i < allColumns.size(); i++) {
        	columnJSON = outColumns.get(i);
        	columnJSON.put("children", new JSONArray(allColumns.get(i))); 
        }
        
        // finally, place the child columns and misc data in the resulting JSON object
        toReturn.put("children", new JSONArray(outColumns));
		for(String dataKey : JSONObject.getNames(miscData)) {
			toReturn.put(dataKey, miscData.get(dataKey));
		}
		
		return toReturn;
	}
	
	/**
	 * Since we're using a REALLY old version of json.jar, org.json.XML does not distinguish between
	 * JSONObject members and JSONArray members of length 1, and omits empty JSONArrays entirely. The client
	 * needs the types/contents to be consistent, so we need to replace certain JSONObjects with JSONArrays
	 * after unmarshalling. All this function does is place/replace the fieldKey member of rootObj with a JSONArray.
	 * 
	 * @param rootObj
	 * @param fieldKey
	 * @throws JSONException
	 */
	private static void insertJSONArray(JSONObject rootObj, String fieldKey) throws JSONException {
    	if(!rootObj.has(fieldKey)) {
    		rootObj.put(fieldKey, new JSONArray());
    	}
    	else {
    		Object existingObj = rootObj.get(fieldKey);
        	if(existingObj instanceof JSONObject) {
        		JSONArray realChildren = new JSONArray();
        		realChildren.put((JSONObject) existingObj);
        		rootObj.put(fieldKey, realChildren);
        	}
    	}
	}
}
