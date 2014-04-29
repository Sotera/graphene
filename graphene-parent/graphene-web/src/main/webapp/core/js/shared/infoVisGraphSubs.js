// infovisGraphSubs.js

var labelType, useGradients, nativeTextSupport, animate;

(function() {
	var ua = navigator.userAgent,
 	    iStuff = ua.match(/iPhone/i) || ua.match(/iPad/i),
	    typeOfCanvas = typeof HTMLCanvasElement,
	    nativeCanvasSupport = (typeOfCanvas == 'object' || typeOfCanvas == 'function'),
            textSupport = nativeCanvasSupport 
                 && (typeof document.createElement('canvas').getContext('2d').fillText == 'function');
                 
  //I'm setting this based on the fact that ExCanvas provides text support for IE
  //and that as of today iPhone/iPad current text support is lame
  
        labelType = (!nativeCanvasSupport || (textSupport && !iStuff))? 'Native' : 'HTML';
  	nativeTextSupport = labelType == 'Native';
  	useGradients = nativeCanvasSupport;
  	animate = !(iStuff || !nativeCanvasSupport);
})();

// MFM
var infoVisCommon = {
    // Node and edge colors - change these here as needed
    Colors: {
        defaultNode:  '#00ffff', // cyan
        selectedNode: '#0babfc',
        searchedNode: '#ff0000', // red
        defaultEdge:  '#23a4ff',
        selectedEdge: '#0b0b0b', // black (canvas background is light tan)
        expandedDefNode: '#f66cfb', // expanded node default
        expandedSelNode: '#b941bd', // expanded node selected
        expandedDefEdge: '#be26c4', // expanded edge default
        expandedSelEdge: '#4c144e' // expanded edge selected
    },
     
    widthsRendered: false,
    
    setivisEdgeLineWidth: function(adj, defaultSize) {
                            
        var lw = defaultSize;   // default line width
        if (adj.data.$weight) {
            var weight = adj.data.$weight;
            if (weight && weight != 0) {
                lw = (Math.sqrt(weight) / 2.0) + (weight / 10.0);
            }
        }
        // hack fix for when importing a graph, the $weight is undefined
        // we attempt to recreate the weight vlue from thr lineWidth - this is an approximation
        else if (adj.data.$weight === undefined){  
            if (adj.data.$lineWidth) {
                lw = adj.data.$lineWidth;
                adj.data.$weight = (Math.sqrt(lw)) * 2 * lw;   // approx
            }   
        }
        return lw;
    }
};

Ext.define("DARPA.InfoVis", 
{
// turn nodes and edges lists into the "json" structure needed by infovis to show a graph
// We have already parsed the graph from the input json or xml and populated the node id,label and color

	graphToJSON:function(graph) {

	    var nodes=graph.nodes;
	    var edges=graph.edges;
	    var json=[];
	    for (var n = 0; n < nodes.length; ++n) {
		var node = nodes[n];
		var adj = [];
		for (var e = 0; e < edges.length; ++e) {
			var edge = edges[e];
			var data;
			if (edge.source==node.id) {
				if (edge.directed) {
					var direction=[edge.source,edge.target];
					data = {"$direction":direction, "$type":"arrow"};
				}
				else {
					data = {};
                                }    
                                if (edge.weight != null)
                                	data.$weight = edge.weight;
                                	
				adj.push({
					"nodeTo": edge.target, 
					"nodeFrom": edge.source, 
                                        "weight": edge.weight,  // MFM added weight to represent the number of calls
					"data":data
				});
			}
		}
		var nodeEntry = 
			{
				"adjacencies":adj, 
				"data":{
					"$color": node.color, 
					"$type": "circle",
					"$label":node.label,
					"attrs": node.attrs,
					"idType":node.idType,
					"idVal":node.idVal
				}, 
				"id":node.id,
				"name":node.label
			};

		json.push(nodeEntry);

	    } // n loop

            return json;
	},

	clickNode:function(node)
	{
		var n = node;
            // DEBUG
            //alert("clicked node " + n);

	},

	clear:function(fd)
	{
            if (fd) {
		fd.canvas.clear();              
		fd.labels.clearLabels(true);
            }
	
	},
	showGraph:function(json, fd)
	{
            // save fd in each node so we can handle mouse events. InvoVis had fd as a global, which
            // prevented us from having more than one graph instance

            // DEBUG TIMING - *** REMOVE THIS LATER ***
            /*
                fd.TESTGRAPH_timingstart = (new Date()).getTime(); 
            */
            // END DEBUG TIMING - *** REMOVE THIS LATER ***
            
            for (var i = 0; i < json.length; ++i)
                    json[i].data.fd=fd;

            // load JSON data.
          
            if (json.length == 0) { // MFM
                return;
            }
            fd.loadJSON(json);
       
	  // compute positions incrementally and animate.
            fd.computeIncremental(
		{
			iter: 20,   // MFM was 40
			property: 'end',
			onStep: function(perc){
	//		      Log.write(perc + '% loaded...');
			},
			onComplete: function(){
                                var x = fd;                                
				fd.animate({
                                    modes: ['linear'],
                                    transition: $jit.Trans.Elastic.easeOut,
                                    duration: 2500,
                                    onComplete: function() {   // MFM. This is called after the graph is first displayed
                                        // This will render the edges with the proper widths
                                        x.graph.eachNode(function(node) {
                                            if (node) {
                                                node.eachAdjacency(function(adj) {
                                                      var lw = infoVisCommon.setivisEdgeLineWidth(adj, 0.4);
                                                      adj.setDataset('end', {
                                                          lineWidth: lw,
                                                          color: infoVisCommon.Colors.defaultEdge  
                                                      }
                                                      );
                                                });
                                            }
                                        }); // end x.graph.eachNode

                                        x.fx.animate({
                                            modes: ['node-property:dim:color',
                                                    'edge-property:lineWidth:color'
                                                   ],
                                            duration: 500
                                        });
                                     }
                                }); // animate
                                
                        // DEBUG TIMING - *** REMOVE THIS LATER ***
                        /*
                        fd.TESTGRAPH_timingstop = (new Date()).getTime(); 
                        var delta = fd.TESTGRAPH_timingstop - fd.TESTGRAPH_timingstart;
                        console.log("TIMING STOP - Elapsed time: " + delta / 1000 + " seconds");
                        */
                         // DEBUG TIMING - *** REMOVE THIS LATER ***
                                 
			} // onComplete
		} // parms to fd compute
            ); // fd.compute
	}, // function showGraph
        
        //------------------
        // MFM
        // For displaying an exported graph
        // The only difference between showGraph and this function is that this function does not
        // compute the positions of the nodes but uses their previously saved positions for the layout.
        // Also this function should preserve he node colors and node alpha values
        showExportedGraph:function(json, fd)
	{
            
            if (fd) {
		fd.canvas.clear();              
		fd.labels.clearLabels(true);
            }
            
            // save fd in each node so we can handle mouse events. InvoVis had fd as a global, which
            // prevented us from having more than one graph instance
            for (var i = 0; i < json.length; ++i)
                    json[i].data.fd=fd;

            if (json.length == 0) { // MFM
                return;
            }
            fd.loadJSON(json);
       
          
          // TODO: This works pretty fast but the original exported positions are not preserved after display
          fd.compute('current');
          fd.plot();
         
	}, // function showExportedGraph
        
        // MFM Import
        // graph    - Is a json rep of the exported graph.  
        // This should contain an array of nodes with adjacencies plus some additional 
        // information about the view state of each node.
        importGraph: function(json, fd) {
            
            var self = this;            
            if (json && json.length > 2) {
                // Load Previously Exported JSON data.
                // Note this data has positions and colors of each node
                self.showExportedGraph(json,fd);
            }
            else {
                alert("The exported graph is empty or invalid.");
            }
        },
        
        // MFM Export
        // Returns the json rep of the current graph.  
        // This will be an array of nodes with adjacencies plus some additional 
        // information about the view state of each node.
        // See NOTE below.
        exportGraph: function(fd) {              
            var graphj = fd.toJSON("graph");
            
            // NOTE:
            // Before we can return the json, we need to post-process it to extract some 
            // display attributes like current position, selected, and alpha for each node. 
            // The fd object that is included with each node in the graph's json must Not be included.
            var outj = [];
            var i, nodeid, node, fdGraphNode;
            
            for (i = 0; i < graphj.length; i++) {
                var xpos, ypos;
                var alpha, selected; 
                
                node = graphj[i];
                nodeid = node.id;   // this is a string
                fdGraphNode = node.data.fd.graph.nodes[nodeid]; // node id is NOT the same as i

                xpos = fdGraphNode.pos.x;
                ypos = fdGraphNode.pos.y;
                
                alpha = 1;
                if (fdGraphNode.data.$alpha !== undefined) {
                    alpha = fdGraphNode.data.$alpha;   // 1 if shown, 0 if hidden
                }

                selected = false;
                if (fdGraphNode.selected !== undefined) {
                    selected = fdGraphNode.selected;
                }
        
                var nodeClone = {};
                nodeClone.adjacencies = node.adjacencies;
                nodeClone.data = {};
                nodeClone.data.pos = { x: xpos, y: ypos };
                nodeClone.data.selected = selected;
                nodeClone.data.$alpha = alpha;
                nodeClone.data.$color = node.data.$color;
                nodeClone.data.$dim = node.data.$dim;
                nodeClone.data.$label = node.data.$label;
                nodeClone.data.$type = node.data.$type;
                nodeClone.data.attrs = node.data.attrs;
                //nodeClone.dataC.idType = node.data.idType;    // omit, this is alway undefined
                nodeClone.data.idVal = node.data.idVal;
                // Important! omit the node.data.fd
                nodeClone.id = node.id;
                nodeClone.name = node.name;
                outj.push(nodeClone);
            }
            return outj;
        },
        
        
        // MFM
        // Expand out one hop from a selected node. This adds nodes to the graph around the specified node
        showGraph1Hop: function(injson, innode, fd)
	{
            // Save fd in each node so we can handle mouse events. InvoVis had fd as a global, which
            // prevented us from having more than one graph instance.
            // Also need to make all of the ids unique (except for the expanded node) so that they 
            // do not conflict with existing nodes that are already in the graph
            // Child nodes of an expanded node will have a unique node id that is prefixed by the communication id
            // Example: 35152523411-3
            
            if (injson.length == 0 || innode == null) {
                // TODO may want to alert this - unexpected error
                return;
            }

            var node;
            var innumber = innode.name;
            var oldSelectedNodeId = "-1";
            
            // First change the node ids
            for (var i = 0; i < injson.length; i++) {
                node = injson[i];
                node.data.fd=fd;
                
                if (node.name == innumber) {   
                    // this node is the selected node we are expanding
                    // it already exists in the graph so we must use its existing id
                    oldSelectedNodeId = node.id;    // remember for the next section
                    node.id = innode.id;            // keep the id of the node we are expanding 
                }
                else {
                    node.id = innumber + '-' + node.id;    // make ids unique
                    node.data.$color = infoVisCommon.Colors.expandedDefNode;   // Change the color of the expanded nodes
                    node.data.expanded = true;
                }   
            }

            // Next change the references in the adjacencies and directions
            // We can't do both the above and below in one loop. 
            // The node ids must all be changed first then the adjacencies and directions.
            for (i = 0; i < injson.length; i++) {
                node = injson[i];
                
                // If the node has adjacencies change the ids in the adajencies list to be unique
                // Note: This node is not fully fleshed, it has no methods like: node.eachAdjacency(function(adj)
                for (var a = 0; a < node.adjacencies.length; a++) {
                    var adj = node.adjacencies[a];
                    if (adj.nodeFrom && adj.nodeTo) {
                        //OLD if (adj.nodeFrom != innode.id) {
                        if (adj.nodeFrom != oldSelectedNodeId) {
                            adj.nodeFrom = innumber + '-' + adj.nodeFrom;
                        }
                        else {
                           adj.nodeFrom = innode.id;
                        }
                        if (adj.nodeTo != oldSelectedNodeId) {
                            adj.nodeTo = innumber + '-' + adj.nodeTo;
                        }
                        else {
                           adj.nodeTo = innode.id;
                        }

                        var dir = adj.data.$direction;  // direction array conatins the node ids of the 2 connected nodes
                        if (dir.length == 2) {
                            if (dir[0] != oldSelectedNodeId) {
                                dir[0] = innumber + '-' + dir[0];
                            }
                            else {
                               dir[0] = innode.id; 
                            }
                            if (dir[1] != oldSelectedNodeId) {
                                dir[1] = innumber + '-' + dir[1];
                            }
                            else {
                                dir[1] = innode.id;
                            }
                        }
                    }  
                }
            }

            // add JSON data to the graph.

            // Extend the jit library here to include an addJSON function and an fd.expandedNodes array if not already present
            if (fd.addJSON == undefined || typeof fd.addJSON != "function") {
                
                // MFM 10/28/13 This holds a list of expanded node ids
                // Each element in the array consists of: { expandFrom: innumber, nodeIds: expNodeIdList}
                fd.expandedNodes = []; 
                    
                // Add JSON nodes to the existing graph
                fd.addJSON = function(json) {                              
                    fd.op.sum(json, {
                        //type: 'fade:seq',
                        type: 'nothing',
                        duration: 1000,
                        hideLabels: false,
                        transition: $jit.Trans.Quart.easeOut
                    });
               }; 
            }
            fd.addJSON(injson);
                    
            // MFM 10/28/13 Add the expanded node ids to a list for later removal
            var expNodeIdList = [];
            for (i = 0; i < injson.length; i++) {
                node = injson[i];
                if (node.id.indexOf("-") > 0) {
                    expNodeIdList.push(node.id);
                }
            }
            var eNList = { expandFrom: innumber, nodeIds: expNodeIdList};
            fd.expandedNodes.push(eNList);

            // re compute positions incrementally and animate.
            fd.computeIncremental(
            {
                iter: 20,   // MFM was 40
                property: 'end',
                onStep: function(perc){
//		      Log.write(perc + '% loaded...');
                },
                onComplete: function(){
                    var x = fd;
                    fd.animate({
                        modes: ['linear'],
                        transition: $jit.Trans.Elastic.easeOut,
                        duration: 2000,
                        onComplete: function() {   // MFM. This is called after the graph is first displayed
                            // This will render the edges with the proper widths
                            x.graph.eachNode(function(node) {
                                if (node) {
                                    node.eachAdjacency(function(adj) {
                                        var lw = infoVisCommon.setivisEdgeLineWidth(adj, 0.4);
                                        if (node.data.expanded && node.data.expanded == true) {
                                           adj.setDataset('end', {
                                               lineWidth: lw,
                                               color: infoVisCommon.Colors.expandedDefEdge
                                           }); 
                                        }
                                        else {
                                           adj.setDataset('end', {
                                               lineWidth: lw,
                                               color: infoVisCommon.Colors.defaultEdge
                                           });
                                        }
                                    });
                                }
                            }); // end x.graph.eachNode

                            x.fx.animate({
                                modes: ['node-property:dim:color',
                                        'edge-property:lineWidth:color'
                                       ],
                                duration: 500
                            });
                         }
                    }); // animate            
                } // onComplete
            } // parms to fd compute
            ); // fd.compute
	}, // function showGraph1Hop
	
        // If the specified node has been expanded, this deletes the expanded nodes and links (and labels)
        unexpand1Hop: function (node, fd) {          
            if (node) {
                var number = node.name; // number of the node that MAY have been expanded
                var ids2Remove = [];
             
                // MFM 10/28/13
                if (fd.expandedNodes && fd.expandedNodes.length > 0) {                 
                    var eNodes, e;
                    for (e = 0; e < fd.expandedNodes.length; e++) {
                       eNodes = fd.expandedNodes[e];
                       if (eNodes.expandFrom == number) {
                           ids2Remove = eNodes.nodeIds;
                           break;
                       }
                    }
                    
                    if (ids2Remove.length > 0) {
                        // this removes the nodes and links but not the labels
                        fd.op.removeNode(ids2Remove, { 'type': 'nothing' });

                        // Extend the jit library here to include a function to delete labels by ids, if not already present
                        if (fd.labels.clearLabelsByIds == undefined || typeof fd.labels.clearLabelsByIds != "function") {
                            // Add JSON nodes to the existing graph
                            fd.labels.clearLabelsByIds = function(ids) {
                              for(var l = 0; l < ids.length; l++) {
                                  var lid = ids[l];
                                  fd.labels.disposeLabel(lid);
                                  delete fd.labels.labels[lid];
                              }  
                            }
                        }
                        fd.labels.clearLabelsByIds(ids2Remove); // this removes the labels
                    }
                    else {
                        alert("This number was not manually expanded.");
                    }
                }
                else {
                    alert("This number was not manually expanded.");
                }
            }
        },
        
	saveGraph: function(fd)
	{
		fd.canvas.getCtx().save();	
	},
	restoreGraph: function(fd)
	{
		fd.canvas.canvases[0].plot();
//		var ctx=fd.canvas.getCtx();
//		ctx.restore();	
	},
	
	params: 
	{
	//Enable zooming and panning
	//with scrolling and DnD
		Navigation: {
			enable: true,
			type: 'Native',
			//Enable panning events only if we're dragging the empty
			//canvas (and not a node).
			panning: 'avoid nodes',
			zooming: 10 //zoom speed. higher is more sensible
		},

	// Change node and edge styles such as color and width.
	// These properties are also set per node
	// with dollar prefixed data-properties in the
	// JSON structure.
		Node: {
			overridable: true,
			dim: 7
		},
		Edge: {
			overridable: true,
			color: infoVisCommon.Colors.defaultEdge,
			lineWidth: 0.4
		},

	// Add node events
		Events: {
			enable: true,
			type: 'Native',
                
			//Change cursor style when hovering a node
			onMouseEnter: function() {
				if (arguments[1].node != false) {
					var fd = arguments[1].node.data.fd;
					if (fd != null) {
						fd.canvas.getElement().style.cursor = 'move';
						var callback = arguments[1].node.data.fd.owner.nodeEnter;
						if (callback !== undefined)
							callback(arguments[1].node);
					}
				}
			},
			onMouseLeave: function() {
				if (arguments[1].node != false) {
					var fd = arguments[1].node.data.fd;
					if (fd != null) 
						fd.canvas.getElement().style.cursor = '';
				}
			},
                        
                        // MFM Select and highlight the node and edges when dragging is done
			onDragEnd: function(node, eventInfo, e) { 
                            var fd = arguments[0].data.fd;
                            if (fd != null) {                                 
                                node.data.fd.owner.nodeClick(node); 

                                var pos = eventInfo.getPos();
                                node.pos.setc(pos.x, pos.y);
                                fd.plot();

                                // MFM added to show this node is selected
                                node.data.fd.graph.eachNode(function(n) {
                                    if(n.id != node.id) 
                                            delete n.selected;

                                    n.setData('dim', 7, 'end');
                                    //var curColor = n.getData('color');
                                    
                                    n.eachAdjacency(function(adj) {                                                
                                        var lw = infoVisCommon.setivisEdgeLineWidth(adj, 0.4); 
                                        if (n.data.expanded && n.data.expanded == true) {
                                           adj.setDataset('end', {
                                                lineWidth: lw,
                                                color: infoVisCommon.Colors.expandedDefEdge
                                            }); 
                                        }
                                        else {
                                            adj.setDataset('end', {
                                                lineWidth: lw,
                                                color: infoVisCommon.Colors.defaultEdge
                                            });
                                        }
                                    });
                                });
                                    
                                node.selected = true;
                                node.setData('dim', 10, 'end');
                                //var curColor = node.getData('color');
                               
                                node.eachAdjacency(function(adj) {
                                    var lw = infoVisCommon.setivisEdgeLineWidth(adj, 0.4) * 1.5;
                                    if (node.data.expanded && node.data.expanded == true) {
                                       adj.setDataset('end', {
                                            lineWidth: lw,
                                            color: infoVisCommon.Colors.expandedSelEdge
                                        }); 
                                    }
                                    else {
                                        adj.setDataset('end', {
                                            lineWidth: lw,
                                            color: infoVisCommon.Colors.selectedEdge
                                        });
                                    }
                                });

                    //trigger animation to final styles
                                node.data.fd.fx.animate({
                                        modes: ['node-property:dim:color',
                                                'edge-property:lineWidth:color'
                                               ],
                                        duration: 500
                                });
                            }
			},
                        //---------------
			//Update node positions when dragged
			onDragMove: function(node, eventInfo, e) { 
				var fd = arguments[0].data.fd;
				if (fd != null) {
                                    
                                    // This will be called possibly hundreds! of times while the mouse is being moved
                                    // so don't select the node until the Drag is done (one time)
                                    //node.data.fd.owner.nodeClick(node); 
                                    
                                    var pos = eventInfo.getPos();
                                    node.pos.setc(pos.x, pos.y);
                                    fd.plot();
                                   
				}
			},
			//Implement the same handler for touchscreens
			onTouchMove: function(node, eventInfo, e) {
				$jit.util.event.stop(e); //stop default touchmove event
				this.onDragMove(node, eventInfo, e);
			}
		},

	//Number of iterations for the FD algorithm
		iterations: 200,

	//Edge length
		levelDistance: 130,

	// This method is only triggered
	// on label creation and only for DOM labels (not native canvas ones).

	//[params.]
		onCreateLabel: function(domElement, node)
		{
		// Create a 'name' and 'close' buttons and add them
		// to the main node label
                    var     nameContainer = document.createElement('span'),
                            closeButton = document.createElement('span'),
                            style = nameContainer.style;
                    nameContainer.className = 'name';
                    nameContainer.innerHTML = node.name;
                    closeButton.className = 'close';
                    closeButton.innerHTML = 'x';
                    domElement.appendChild(nameContainer);
    //		domElement.appendChild(closeButton);
                    style.fontSize = "0.9em";
                    //      		style.color = "#ddd";
                    style.color = "black";     		

                    //Fade the node and its connections when
                    //clicking the close button
                    closeButton.onclick = function() {
                            node.setData('alpha', 0, 'end');
                            node.eachAdjacency(function(adj) {
                                    adj.setData('alpha', 0, 'end');
                            }
                            ); // each

                            node.data.fd.fx.animate(
                            {
                                    modes: ['node-property:alpha',
                                            'edge-property:alpha'
                                           ],
                                    duration: 500
                            }); // animate
                    };

	//Toggle a node selection when clicking
	//its name. This is done by animating some
	//node styles like its dimension and the color
	//and lineWidth of its adjacencies

                nameContainer.onclick = function() {
                    node.data.fd.owner.nodeClick(node);

                    //set final styles
                    node.data.fd.graph.eachNode(function(n) {
                        if(n.id != node.id) 
                                delete n.selected;
                            
                        n.setData('dim', 7, 'end');
                        //var curColor = n.getData('color');
                        
                        n.eachAdjacency(function(adj) {
                            var lw = infoVisCommon.setivisEdgeLineWidth(adj, 0.4);
                            if (n.data.expanded && n.data.expanded == true) {
                               adj.setDataset('end', {
                                     lineWidth: lw,
                                     color: infoVisCommon.Colors.expandedDefEdge
                                 }); 
                            }
                            else {
                                adj.setDataset('end', {
                                    lineWidth: lw,
                                    color: infoVisCommon.Colors.defaultEdge  
                                });
                            }
                        });
                    });

                    if(!node.selected) {
                            node.selected = true;
                            node.setData('dim', 10, 'end');
                            //var curColor = node.getData('color');
 
                            node.eachAdjacency(function(adj) {
                                var lw = infoVisCommon.setivisEdgeLineWidth(adj, 0.4) * 1.5;  // line width when selected 
                                if (node.data.expanded && node.data.expanded == true) {
                                    adj.setDataset('end', {
                                         lineWidth: lw,
                                         color: infoVisCommon.Colors.expandedSelEdge
                                     }); 
                                }
                                else {
                                    adj.setDataset('end', {
                                        lineWidth: lw,
                                        color: infoVisCommon.Colors.selectedEdge
                                    });
                                }
                            });
                    } 
                    else {
                            delete node.selected;
                    }
            //trigger animation to final styles
                    node.data.fd.fx.animate({
                            modes: ['node-property:dim:color',
                                    'edge-property:lineWidth:color'
                                   ],
                            duration: 500
                    });

	/*
	PWG Removed
	// Build the right column relations list.
	// This is done by traversing the clicked node connections.

	var html = "<h4>" + node.name + "</h4><b> connections:</b><ul><li>",
	list = [];
	node.eachAdjacency(function(adj){
	if(adj.getData('alpha')) list.push(adj.nodeTo.name);
	});
	//append connections information
	$jit.id('inner-details').innerHTML = html + list.join("</li><li>") + "</li></ul>";
	*/
	};

	},
// Change node styles when DOM labels are placed
// or moved.

//[params.]
	onPlaceLabel: function(domElement, node){
		var style = domElement.style;
		var left = parseInt(style.left);
		var top = parseInt(style.top);
		var w = domElement.offsetWidth;
		style.left = (left - w / 2) + 'px';
		style.top = (top + 10) + 'px';
		style.display = '';         
	}
	} // params
        ,

	hideNode:function(node)
	{
            node.setData('alpha', 0, 'end');
            node.eachAdjacency(function(adj) {
                    adj.setData('alpha', 0, 'end');
            }); // each

            node.data.fd.fx.animate(
            {
                    modes: ['node-property:alpha',
                            'edge-property:alpha'
                           ],
                    duration: 50    // was 500
            }); // animate
	},
        // MFM
        showNode:function(node)
	{
            if (node) {
                node.setData('alpha', 1, 'end');
                node.eachAdjacency(function(adj) {
                        adj.setData('alpha', 1, 'end');
                }); // each

                node.data.fd.fx.animate(
                {
                        modes: ['node-property:alpha',
                                'edge-property:alpha'
                               ],
                        duration: 50
                }); // animate
            }
	},

	makeFd:function(container, owner)
	{
	  // init ForceDirected

		var self=this;
		self.params.injectInto=container;
		this.fd = new $jit.ForceDirected(self.params);
		this.fd.owner=owner;
		return this.fd;

	}
}); // define

