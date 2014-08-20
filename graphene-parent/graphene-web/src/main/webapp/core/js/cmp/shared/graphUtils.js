
// TODO (NEAR TERM AND FUTURE)
// 1. All functions in this file should be within a namespace
// 2. The server/graphml service should generate json instead of XML thus avoiding the need to use xmlToGraph
// 3. The server should perform all of the force directed graph layouts and send back the final graph node positions, 
//    this would save even more wait time and offload the browser from performing the cpu intensive fd calculations

// Parse a graphml xml graph to a javascript object containing nodes and edges
// This will get turned into Infovis JSON or perhaps passed to d3 (when supported)
// note that xml.childNodes contains nodes and edges, but the order is not defined.
function xmlToGraph(xml)
{
    var j_nodes=[];
    var j_edges=[];
    var nodes, edges;
    var node = null;
    var data = null;
    var edge = null;
    var datum, key, val, target, source, directed, vweight;
    var jnode = {};
    var i, j;


    // MFM Notes: 
    // IE treats the XML dom differently than other browsers, so we have to parse the dom differently for IE.
    // When the server/graphml service generates json instead of XML this will avoiding the need to use xmlToGraph at all!
    
    if (window.navigator.appName.indexOf("Internet Explorer") >= 0 || window.navigator.appName.indexOf("Microsoft") >= 0) {
        nodes = xml.childNodes[1].childNodes;
        edges = xml.childNodes[0].childNodes;
           
	for (i = 0; i < nodes.length; i++) {
            node = nodes[i];
            data = node.childNodes; // xml childNodes of the node
            jnode = {
                id: node.attributes.getNamedItem("id").value,
                attrs:[]
            };
            
            for (j = 0; j < data.length; j++) {
                datum=data[j];
                key = datum.attributes[0].value;
                val = "unknown id";  // MFM firstChild may be null
                if (datum.firstChild) {
                    val = datum.firstChild.text;
                }

                if (key=='IdentifierType')
                        jnode.idType=val;
                if (key=='Identifier')
                        jnode.idVal=val;

                if (key=='label') {
                        jnode.label=val;
                        jnode.name=val;								
                }
                else if (key.indexOf ("Color") != -1) {
                    switch(val.length) {
                        case 6: 
                                jnode.color="#" + val;
                                break;
                        case 7:
                                jnode.color=val;
                                break;
                        case 9:
                                jnode.color="#" + val.substring(3);
                                break;
                        case 8:
                                jnode.color="#" + val.substring(2);
                                break;
                        default:
                                jnode.color='black';
                                break;
                    } // switch
                } // is color
                else {
                    jnode.attrs.push({key:key,val:val});
                }

            } // each data
            j_nodes.push(jnode);		
	} // each node

        
	for (j = 0; j < edges.length; j++) {
            edge = edges[j];
            //var target=edge.attributes.target.textContent;
            target=edge.attributes.getNamedItem("target").text;
            //var source=edge.attributes.source.textContent;
            source=edge.attributes.getNamedItem("source").text;
            //var directed=edge.attributes.directed.textContent;
            directed=edge.attributes.getNamedItem("directed").text;
            // MFM added weight
            //var vweight = edge.attributes.weight.value;
            vweight = edge.attributes.getNamedItem("weight").value; 

                // PWG added attributes 1/22/14. Need to test with IE
                var attrs = [];
                var data = edge.childNodes;
		for (var i = 0; i < data.length; i++) {
                    datum=data[i];
                    key = datum.attributes[0].value;
                    val = "unknown id";
                    if (datum.firstChild) {
                        val = datum.firstChild.textContent;
                    }
                    attrs.push({key:key,val:val});
               }



            j_edges.push({source:source, target:target, directed:directed, "weight":vweight, "attrs":attrs});
	} // each edge      
    }   // end if IE
    else {
       if ((xml.childNodes[0] === undefined) ||
	    (xml.childNodes[0].childNodes[0] === undefined) ||
	    (xml.childNodes[0].childNodes[0].attributes.id === undefined)) {
		nodes=xml.childNodes[1].childNodes;
		edges=xml.childNodes[0].childNodes;		
	}
	else {
		nodes=xml.childNodes[0].childNodes;
		edges=xml.childNodes[1].childNodes;		
	}

	for (i = 0; i < nodes.length; i++) {
		node = nodes[i];
		data=node.childNodes; // xml childNodes of the node
		jnode = {
			id:node.attributes.id.value,
			attrs:[]
		};
		for (j = 0; j < data.length; j++) {
                    datum=data[j];
                    key = datum.attributes[0].value;
                    val = "unknown id";  // MFM firstChild may be null
                    if (datum.firstChild) {
                        val = datum.firstChild.textContent;
                    }

                    if (key=='IdentifierType')
                            jnode.idType=val;
                    if (key=='Identifier')
                            jnode.idVal=val;

                    if (key=='label') {
                            jnode.label=val;
                            jnode.name=val;								
                    }
                    else if (key.indexOf ("Color") != -1) {
                        switch(val.length) {
                            case 6: 
                                    jnode.color="#" + val;
                                    break;
                            case 7:
                                    jnode.color=val;
                                    break;
                            case 9:
                                    jnode.color="#" + val.substring(3);
                                    break;
                            case 8:
                                    jnode.color="#" + val.substring(2);
                                    break;
                            default:
                                    jnode.color='black';
                                    break;
                        } // switch
                    } // is color
                    else {
                        jnode.attrs.push({key:key,val:val});
                    }

		} // each data
		j_nodes.push(jnode);		
	} // each node

	for (j = 0; j < edges.length; j++) {
		edge = edges[j];
		target=edge.attributes.target.textContent;
		source=edge.attributes.source.textContent;
		directed=edge.attributes.directed.textContent;
                // MFM added weight
                vweight = edge.attributes.weight.value; 
                
                // PWG added attributes 1/22/14
                var attrs = [];
                var data = edge.childNodes;
		for (var i = 0; i < data.length; i++) {
                    datum=data[i];
                    key = datum.attributes[0].value;
                    val = "unknown id";
                    if (datum.firstChild) {
                        val = datum.firstChild.textContent;
                    }
                    attrs.push({key:key,val:val});
               }
                
		j_edges.push({source:source, target:target, directed:directed, "weight":vweight, "attrs":attrs});
		
	} // each edge
    }
    return {nodes:j_nodes, edges:j_edges};
}

function uniqueNodeIds(data)
{
	if (data.length==0)
		return [];
            
	var ids=[];
        var d;
	for (var i = 0; i < data.length; i++) {
	    	d = data[i];
	    	if (!utils.arrayHas(ids, d.source))
	    		ids.push(d.source);
	    	if (!utils.arrayHas(ids, d.target))
	    		ids.push(d.target);
	}
	// ids now is a unique set of node identifiers
	
	return ids;
}

// Take the data that we received, find the most common maxrow interactions.
// data must have source and target values

function interactionPreprocess(searchTarget, data, maxrow)
{
	if (data.length==0)
		return [];
		
	var obj={};
        var d;
	for (var i = 0; i < data.length; i++) {
		d = data[i];

		if (d.source == "" || d.target=="")
			continue;

		// normalize so if our target didn't have area code we modify
		
		if (searchTarget.indexOf(",") == -1) {	// A single number
			if (d.source.indexOf(searchTarget) != -1 || searchTarget.indexOf(d.source) != -1)
				d.source = searchTarget;
			if (d.target.indexOf(searchTarget) != -1 || searchTarget.indexOf(d.target) != -1)
				d.target = searchTarget;
		}

                if (undefined == obj[d.source])
			    obj[d.source] = 0;
		    else
			    ++obj[d.source];
		    if (undefined == obj[d.target])
			    obj[d.target] = 0;
		    else
			    ++obj[d.target];
	}
	    // obj is now a deduplicated list of identifiers, with the count for each
	    // now we need to calculate the top maxrow identifiers.

	    var tosort=[];
	    for (i in obj) {
		tosort.push({value: i, count: obj[i]});
            }
	    tosort.sort(function(a,b) {return b.count-a.count});

	    if (maxrow > tosort.length)
	    	maxrow = tosort.length;
	    	
	    var targets=[];
	    for (i = 0; i < maxrow; i++) {
		targets.push(tosort[i].value);
            }

	    // Targets now has the most frequent (maxrow) identifiers

	    // Now we need to restrict the data to the most frequent (maxrow) and assign a row number to them

	    var reduced = [];

	    for (i = 0; i < data.length; i++) {
		d = data[i];
		var sourcerow = -1;
		var targetrow = -1;
		for (var j = 0; j < maxrow; j++) {
			if (targets[j] == d.source) {
				sourcerow = j;
			}
			if (targets[j] == d.target) {
				targetrow = j;
			}
		}
		if (sourcerow >= 0 && targetrow >= 0) {
			d.sourcerow = sourcerow;
			d.targetrow = targetrow;
			reduced.push(d);
		}
	    }

	return reduced;
}

function scatterplotPreprocess(data, maxrow)
{
	if (data.length==0)
		return [];

	var obj={};
	for (var i = 0; i < data.length; i++) {
            var d = data[i];

            if (d.source == "" || d.target=="")
                    continue;

	    if (undefined == obj[d.source])
			    obj[d.source] = 0;
		    else
			    ++obj[d.source];
	    if (undefined == obj[d.target])
			    obj[d.target] = 0;
		    else
			    ++obj[d.target];
	}
	   // obj is now a deduplicated list of identifiers, with the count for each
	    // now we need to calculate the top maxrow identifiers.

	var tosort=[];
	for (i in obj)
		tosort.push({value: i, count: obj[i]}); 
	tosort.sort(function(a,b) {return b.count-a.count});


	if (tosort.length < maxrow)
		maxrow = tosort.length;

	var targets=[];
	for (i = 0; i < maxrow; i++) {
		targets.push(tosort[i].value);
        }	

	// Targets now has the most frequent (maxrow) identifiers

	// Now we need to restrict the data to the most frequent (maxrow) and assign a row number to them

	var reduced = [];

	for (i = 0; i < data.length; i++) {
		d = data[i];
		var sourcerow = -1;
		var targetrow = -1;
		for (var j = 0; j < maxrow; j++) { // check against the maxrow highest ranked
			if (targets[j] == d.source) {
				sourcerow = j;
			}
			if (targets[j] == d.target) {
				targetrow = j;
			}
		}
		if (sourcerow >= 0 && targetrow >= 0) {
			d.sourcerow = sourcerow;
			d.targetrow = targetrow;
			reduced.push(d);
		}
	}

	return reduced;
}

// MFM added the below 12/04/13 - TODO all functions in this file should be within a namespace

function importGraph(context) {
        var self = context;
        var importWindow = Ext.create("DARPA.importDialog", {
                title: 'Import Window',
                border: true
        });

        importWindow.setInfoVis(self.GraphVis);   // reference to the graph (not the graph data)
        importWindow.show();
        importWindow.center();
}
        
function exportGraph(context, defaultFileName) {
    var self = context; 
	if (self.GraphVis) {
	
		var outj;
		try {
			outj = self.GraphVis.exportGraph();
		} catch (e) {
			console.error("ERROR GETTING GRAPH JSON.");
			outj = undefined;
		}
		
		var outPNG;
		try {
			outPNG = self.GraphVis.gv.png();
		} catch (e) {
			console.error("ERROR GETTING GRAPH PNG.");
			outPNG = undefined;
		}
		
		var exportWindow = Ext.create("DARPA.exportDialog", {
			title: 'Export Window',
			border: true
		});

		exportWindow.setGraphJSON(outj);
		exportWindow.setGraphPNG(outPNG);
		//TODO: work some magic with scope so you can build default file name with query parameters with this.getSearch().getName()
		exportWindow.setFileName(defaultFileName);
		exportWindow.show();
		exportWindow.center();
	}
	else {
		Ext.Msg.alert("No graph data is available for this Entity.");
	}
}
        
function saveGraph(context, userid) {
        var self = context; 
        if (self.GraphVis) {
            var outj = self.GraphVis.exportGraph();
            var uid = (userid != null && userid.length > 0)? userid : "unknown";

            UDSIF.init();
            var UDSAction = { type: "sg", data: { graphJSON: outj } };
            UDSIF.addToUDSession(UDSAction);
            UDSIF.saveUDSession(uid, null);
        }
        else {
            Ext.Msg.alert("No graph data is available for this Entity.");
        }
}
        
function restoreGraph(context, userid, butid) {
        var self = context;
        if (self.GraphVis) {
            var uid = (userid != null && userid.length > 0)? userid : "unknown";
            UDSIF.restoreUDSession(uid, self.GraphVis, butid);
        }
}

// check if the amount matches or is within the specified min and max range
// amount       - the input value to match against
// amountMatch  - can be a single number or a min and max amount separated with a comma
function amountInList(amount, amountMatch) {
        var amountMatchList = amountMatch.split(",");  // should only be 2 numbers, the min and max amounts
        var len = amountMatchList.length;
        var recAmount = parseInt(amount);
        if (len == 1) {
            if (parseInt(amountMatchList[0]) == recAmount)
                return true;
        }
        else if (len == 2) {
            if (recAmount >= parseInt(amountMatchList[0]) && recAmount <= parseInt(amountMatchList[1])) {
                return true;
            }
        }
        return false;
}
        
// check if the number (any string) is in the list
// number is the current node's id or number
// numbers is a string that may contain a partial number, one number, or a comma separated list of numbers or partial numbers
function numberInList(number, numbers) {
        // this allows several partial matches
        var numList = numbers.split(",");
        for (var i = 0; i < numList.length; i++) {
            if (number.indexOf(numList[i].trim()) >= 0)
                return true;
        }
        return false;
}

// MFM JIRA-29
// applyAdditionalFieldsFilter is meant to hide items in the graph canvas
// context          - The specific graph instance
// filterItems      - array of additional attributes (fields) to filter on
// Each filter data item has these elements:
//   dataSourceField    - data element name in a node or an edge
//   dataSourceType     - "nodes" | "edges"
//   dispFieldName      - field name (key) - used only when dataSourceField == "attrs"
//   value              - the user specified value to filter on. Empty values are skipped.
// compareType      -  "exact" | "fuzzy"
function applyAdditionalFieldsFilter(context, filterItems, compareType) {

    var self = context;
    var i, j;
    var gv;
    
    if (self.GraphVis) {
       gv = self.GraphVis.getGv();
    }
    else {
        Ext.Msg.alert("No graph data is available for this Entity.");
        return;
    }
    var filterItem, fvalue;
    var nodes2Hide = []; 
    var edges2Hide = [];
    var nonEmptyFilterItems = [];
    
    // Prune the filter items. Don't waste time repeatedly iterating over filter items with empty values
    for (i = 0; i < filterItems.length; i++) {
        filterItem = filterItems[i].data;
        fvalue = filterItem.value.trim();
        if (fvalue.length > 0) {
            filterItem.value = fvalue;
            nonEmptyFilterItems.push(filterItem);
        }
    }
    
    if (gv) {
        // iterate over the nodes and edges only once
        gv.nodes().each(function(indx, node) {
            if (node) {
                var allMatch = true;
                for (i = 0; i < nonEmptyFilterItems.length; i++) {
                    filterItem = nonEmptyFilterItems[i];
                    fvalue = filterItem.value;  // value has already been trimmed above
                    var fvalUpper, nvalUpper;
                    
                    if (filterItem.dataSourceType == "nodes") {
                        if (filterItem.dataSourceField == "attrs") {
                            var attrs = node.data().attrs;
                            var attrItem;
                                          
                            // more than one attribute could be specified
                            for (j = 0; j < attrs.length; j++) {
                                attrItem = attrs[j];
                                if (attrItem.key == filterItem.dispFieldName) {
                                    if (compareType == "exact") {       // and case sensitive
                                        if (attrItem.val != fvalue) { 
                                            allMatch = false;
                                            nodes2Hide.push(node);
                                            break;  // match failed, no need to check other attributes
                                        }
                                    }
                                    else {
                                         fvalUpper = fvalue.toUpperCase();
                                         nvalUpper = attrItem.val.toUpperCase();                                         
                                         if (nvalUpper.indexOf(fvalUpper) < 0) {  // case insensitive
                                            allMatch = false;
                                            nodes2Hide.push(node);
                                            break;  // match failed, no need to check other attributes
                                        }
                                    }
                                }
                            }
                            if (allMatch == false) {
                                break;
                            }
                        }
                        else {
                            var nodeValue = node.data()[filterItem.dataSourceField];
                            if (compareType == "exact") {
                                if (nodeValue != fvalue) {  // TODO case insensitive
                                    allMatch = false;
                                    nodes2Hide.push(node);
                                    break;  // match failed, no need to check other fields
                                }
                            }
                            else {  
                                fvalUpper = fvalue.toUpperCase();
                                nvalUpper = nodeValue.toUpperCase();
                                if (nvalUpper.indexOf(fvalUpper) < 0) {  // case insensitive
                                    allMatch = false;
                                    nodes2Hide.push(node);
                                    break;  // match failed, no need to check other fields
                                }
                            }
                        }
                    }
                }
                if (allMatch) {
                    self.GraphVis.showNode(node);
                }
            }
        });  // end for each node
        
        gv.edges().each(function(indx, edge) {
            if (edge) {
                var allMatch = true;
                for (i = 0; i < nonEmptyFilterItems.length; i++) {
                    filterItem = nonEmptyFilterItems[i];
                    fvalue = filterItem.value;  // value has already been trimmed above
                    var fvalUpper, evalUpper;
                    
                    if (filterItem.dataSourceType == "edges") {
                        if (filterItem.dataSourceField == "attrs") {
                            var attrs = edge.data().attrs;
                            var attrItem;
                            
                            // more than one attribute could be specified
                            for (j = 0; j < attrs.length; j++) {
                                attrItem = attrs[j];
                                if (attrItem.key == filterItem.dispFieldName) {
                                    if (compareType == "exact") {
                                        if (attrItem.val != fvalue) {  // and case sensitive
                                            allMatch = false;
                                            edges2Hide.push(edge);
                                            break;  // match failed, no need to check other attributes
                                        }
                                    }
                                    else {
                                       fvalUpper = fvalue.toUpperCase();
                                       evalUpper = attrItem.val.toUpperCase();
                                       if (evalUpper.indexOf(fvalUpper) < 0) {  // case insensitive
                                            allMatch = false;
                                            edges2Hide.push(edge);
                                            break;  // match failed, no need to check other attributes
                                       } 
                                    }
                                }
                            }
                            if (allMatch == false) {
                                break;
                            }
                        }
                        else {
                            var edgeValue = edge.data()[filterItem.dataSourceField];
                            if (compareType == "exact") {
                                if (edgeValue != fvalue) {  // and case sensitive
                                    allMatch = false;
                                    edges2Hide.push(edge);
                                    break;  // match failed, no need to check other fields
                                }
                            }
                            else {
                                fvalUpper = fvalue.toUpperCase();
                                evalUpper = edgeValue.toUpperCase();
                                if (evalUpper.indexOf(fvalue) < 0) {  // case insensitive
                                    allMatch = false;
                                    edges2Hide.push(edge);
                                    break;  // match failed, no need to check other fields
                                }
                            }
                        }
                    }
                }
                if (allMatch) {
                    self.GraphVis.showNode(edge);
                }
            }
        });  // end for each edge
        
        // now the hides (if any)
        for (var h = 0; h < nodes2Hide.length; h++) {
           self.GraphVis.hideNode(nodes2Hide[h]); 
        }
        for (h = 0; h < edges2Hide.length; h++) {
           self.GraphVis.hideEdge(edges2Hide[h]); 
        }
    }
}
        
// filter is meant to hide items in the graph canvas
// searchItems          - is a string that may contain a partial or full string (anything), or a comma separated list of strings
// fromDate and toDate  - are numeric (in millisecs)
function applyFilter(context, searchItems, amount, fromDate, toDate) {            
        // iterate over all the nodes in the graph
         // if the node attributes do NOT match the filter criteria, hide the node
    var self = context;
    
    if (self && self.GraphVis) {
         var gv = self.GraphVis.getGv();
         var checkNumbers = (searchItems && searchItems.length > 0);
         var checkAmount = (amount && amount.length > 0);
         var checkDates = (fromDate != null && fromDate > 0) || (toDate != null && toDate > 0);
         var nodes2Hide = []; 
         var edges2Hide = [];
         if (gv) {
             gv.nodes().each(function(indx, node) {
                if (node) {
                    var numMatch = false;
                    var dateMatch = false;

                    if (checkNumbers) {
                        if (numberInList(node.data().name, searchItems)) {
                            numMatch = true;
                        } 
                    }

                    // Amount is checked on the Edges and not the nodes
                    if (checkDates) {    
                        if (eventTimeInDateRange(node.data().name, fromDate, toDate)) {
                            dateMatch = true;
                        }
                    }

                    if ((checkNumbers && numMatch == false) || (checkDates && dateMatch == false)) {
                        // Do the all the shows first then all of the hides. 
                        // Can't mix both show and hide in this loop because of an issue with edge labels being redisplayed that should be hidden
                        //self.GraphVis.hideNode(node);
                        nodes2Hide.push(node);
                    }
                    else {
                        self.GraphVis.showNode(node);
                    }
                }
             }); // end each node

             // Only check edges if an amount is specified
             if (checkAmount) {
                 gv.edges().each(function(indx, edge) {
                    if (edge) {
                        var amountMatch = false;

                        if (amountInList(edge.data().amount, amount)) {
                             amountMatch = true;
                        } 

                        if (amountMatch == false) {
                            // Do the all the shows first then all of the hides. 
                            // Can't mix both show and hide in this loop because of an issue with edge labels being redisplayed that should be hidden
                            //self.GraphVis.hideNode(node);
                            edges2Hide.push(edge);
                        }
                        else {
                            self.GraphVis.showEdge(edge);
                        }
                    }
                 }); // end each edge
             }   // if checkAmount

             // now the hides (if any)
             for (var h = 0; h < nodes2Hide.length; h++) {
                self.GraphVis.hideNode(nodes2Hide[h]); 
             }
             for (h = 0; h < edges2Hide.length; h++) {
                self.GraphVis.hideEdge(edges2Hide[h]); 
             }
         }
     }
     else {
        Ext.Msg.alert("No graph data is available for this Entity.");
     }
}  
        
// clear filter is meant to unhide all items in the graph canvas
function clearFilter(context) {            
    // iterate over all the nodes in the graph and show them (unhide)
    var self = context;
    if (self && self.GraphVis) {
        self.GraphVis.showAll();
    }
}

function getNodeIdType(node)
{
	return node.data().idType;
/*	
	var idType = "";
	var attrs = node.data().attrs;
	for (var i = 0; i < attrs.length; ++i) {
		var a = attrs[i];
		if (a.key=='IdentifierType') {
			idType = a.val;
			break;
		}
	}
*/	
}
