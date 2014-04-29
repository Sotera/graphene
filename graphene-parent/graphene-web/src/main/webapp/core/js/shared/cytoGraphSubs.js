// cytoGraphSubs.js
// API for interfacing with the cytoscape.js node graph display library
//
// 11/04/13 M. Martinet, 
// Last updated 12/06/13 

var graphVisCommon = {
    // Node and edge colors - change these here as needed
    Colors: {
        defaultNode:  '#00ffff', // cyan
        selectedNode: 'DarkBlue',   // #0babfc',
        searchedNode: '#ff0000', // red
        defaultEdge:  '#23a4ff',
        selectedEdge: '#0b0b0b', // black (canvas background is light tan)
        expandedDefNode: '#f66cfb', // expanded node default
        expandedSelNode: '#b941bd', // expanded node selected
        expandedDefEdge: '#be26c4', // expanded edge default
        expandedSelEdge: '#4c144e' // expanded edge selected
    },
     
    widthsRendered: false,
    
    // Used to set the edge line width based on the input weight
    // defaultSize      - the default line width in pixels
    // weight           - input weight (aka amount)
    setEdgeLineWidth: function(defaultSize, weight) {
                            
        var lw = (defaultSize) ? defaultSize : 2;   // default line width
        if (weight && weight != 0) {
           // the line width cannot be directly (1:1) proportional to the weight, 
           // as this would result in huge (very fat) lines for some weights
           lw = ((Math.sqrt(weight) / 2.0) + (weight / 10.0)) * 1.5;
        }
        if (lw < 1.0)
            lw = 1.0;
        
        // DEBUG
        //console.log("lw = " + lw);
        
        return lw;
    }
};

Ext.define("DARPA.GraphVis", 
{
    // Cytoscape.js specific
    //======================

    gv: null,               // aka cy
    id: null,               // the id MUST be set by the caller when creating an instance of this 
    initialized: false,
    searchName: null,       // contains name, id, or number that was searched for. Passed down from the caller
    currentLayout: null,
    dispHeight: 800,        // graph display area height, updated by initGraph() and resize()
    dispWidth:  1000,       // graph display area width, updated by initGraph() and resize()
    dispRightBorder: 10,    // set later
    dispLeftBorder: 10,     // set later
    dispTopBorder: 10,
    dispBotBorder: 10,

    
    // expandedNodes holds a list of expanded node ids
    // Each element in the array consists of: { expandFrom: innumber, nodeIds: expNodeIdList}
    expandedNodes: [],
    
    minLeafDistance:    60,    // used for optimizing the layout of leaf nodes. min distance (radius) in pixel units 
    
    currentNumNodes:    0,      // holds the current number of nodes retrieved by the last call to graphToJSON
    
    constructor: function(config) {
        this.id = config.id;    // id must be set by the caller
        if (!(config.setBusy == undefined))
        	this.setBusy=config.setBusy;
    },
    
    // Returns the id of this object
    // if addHash is true then we return '#' + the id, else just the id
    getId: function(addHash) {
        var scope = this;
        if (addHash) {
            return "#" + scope.id;
        }
        else {
            return scope.id;
        }
    },
    
    // Turn nodes and edges lists into the "json" structure needed by cytoscape.js to show a graph.
    // We have already parsed the graph from the input json or xml and populated the node id,label and color   
    graphToJSON:function(graph) {
            var scope = this;
	    var nodes=graph.nodes;
	    var edges=graph.edges;
	    var json = [];
            var nodesout = [];
            var edgesout = [];
            
            // Convert json to format that cytoscape.js expects
            var node;
	    for (var n = 0; n < nodes.length; n++) {
		node = nodes[n];
		var nodeEntry = { 
                    "data": {
                        "id": node.id,
                        "name": node.label,
                        "color": node.color, 
                        "type": (node.type != null) ? node.type : "circle",
                        "label": node.label,
                        "attrs": node.attrs,
                        "idType": node.idType,
                        "idVal": node.idVal,
                        "visible": true
                    }
                };
		nodesout.push(nodeEntry);
	    } // n loop
            
            var edge;
            for (var e = 0; e < edges.length; e++) {
                edge = edges[e];
                var data;
                
                var edgeEntry = {
                    "data":{ 
                        "type": "line",
                        "source": edge.source,
                        "target": edge.target,
                        "amount": (edge.amount)? edge.amount : ((edge.weight)? edge.weight : 0), // placeholder for $ amount
                        "weight": (edge.weight)? edge.weight : 0,
                        "lineWidth": graphVisCommon.setEdgeLineWidth(2, (edge.weight)? edge.weight : 0),
                        "visible": true,
                        "attrs" : edge.attrs
                    }
                };
                if (edge.directed) {
                    var dir=[edge.source,edge.target];
                    edgeEntry.data.direction = dir;
                    edgeEntry.data.type = "arrow";
                }
                 
                if (edge.weight != null)
                    edgeEntry.data.weight = edge.weight;    // MFM added weight to represent the number of interactions

                edgesout.push(edgeEntry);
            }

            scope.currentNumNodes = nodes.length;
            json = { nodes: nodesout, edges: edgesout };            
            return json;
    },
    
    
    // Style the nodes based on certain node attributes if any
    // injson       - Input json containing nodes and edges
    // styleConfig: - Config object defining the matching attributes and styling rules
    //  Example: { keyMatch: "propertyName", 
    //             keyValueMatch: "node.data.name", 
    //             styles: [ { name: "type", value: "triangle" }, { name: "color", value: "red" } ]
    //           } 
    //            
    // TODO FUTURE : do this on the server before returning the graph
    styleNodes: function(injson, styleConfig) {
        if (styleConfig) {
            var node, i, a, s;
            var attrs, attr;
            var styles, style;
            var name;
            
            for (i = 0; i < injson.nodes.length; i++) {
                node = injson.nodes[i];
                attrs = node.data.attrs;
                name = node.data.name;
                
                for (a = 0; a < attrs.length; a++) {
                    attr = attrs[a];
                    if (attr.key.indexOf(styleConfig.keyMatch) >= 0) {  // attribute key name matches the specified keyMatch value
                        if (styleConfig.keyValueMatch == "node.data.name") {
                            if (name == attr.val) {                     // node name matches this attribute value
                                styles = styleConfig.styles;
                                if (styles && styles.length > 0) {
                                    for (s = 0; s < styles.length; s++) {
                                        style = styles[s];
                                        if (node.data.hasOwnProperty(style.name)) {
                                            node.data[style.name] = style.value;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }  
            }
        }
        return injson;
    },

    // return the node with matching id - may not need
    getNodeById: function(id) {
        // TODO
        return null;
    },

    clear:function()
    {
        // remove old elements
        var oldEles = this.gv.elements();
        if ( oldEles.length > 0 ){
                oldEles.remove();
        }  
    },
        
    loadJSON: function(json) {
        var scope = this;
        scope.gv.load(json);
    },
        
    showGraph: function(json, searchName)
    {
        var self = this;  
        self.searchName = searchName;
        self.loadJSON(json);    // load JSON data.
    },
        
    //------------------
    // For displaying an exported graph
    // The only difference between showGraph and this function is that this function does not
    // compute the positions of the nodes but uses their previously saved positions for the layout.
    // Also this function should preserve the node colors and node alpha values
    showExportedGraph:function(json)
    {
        var scope = this;
        
        scope.clear();
        scope.searchName = json.searchName;
        scope.gv.add(json.graph);  
        //currentLayout - don't need to change it
        
        // need to hide any elements that were hidden at the time of export        
        scope.gv.nodes().each(function(indx, cnode) {
            if (cnode.data().visible == false) {                
              scope.hideNode(cnode);  
            }
        });
        
        scope.gv.edges().each(function(indx, edge) {
            if (edge.data().visible == false) {                
              scope.hideEdge(edge);  
            }
        });

    }, // function showExportedGraph
        
    // Import
    // graph    - Is a json rep of the exported graph.  
    // This should contain an array of nodes with adjacencies plus some additional 
    // information about the view state of each node.
    importGraph: function(json) {

        var self = this;            
        if (json && json.graph.nodes.length > 0) {
            // Load Previously Exported JSON data.
            // Note this data has positions and colors of each node
            self.showExportedGraph(json);
        }
        else {
            alert("The exported graph is empty or invalid.");
        }
    },

    // Export
    // Returns the json rep of the current graph.  
    // This will be an object containing an array of nodes and an array of edges 
    // includes information about the position and view state of each node.
    exportGraph: function() { 
        var scope = this;
        var outj = {};

        outj.userid = getSessionUserid();   // TODO
        outj.timestamp = new Date().getTime();
        outj.searchName = scope.searchName; // number or id that was searched for - TODO - this should hold the current query params
        outj.currentLayout = scope.currentLayout;
        
        var outnodes = [];
        var outedges = [];
        
        scope.gv.nodes().each(function(indx, cnode) {
            outnodes.push(cnode.json());
        });
        
        scope.gv.edges().each(function(indx, edge) {
            outedges.push(edge.json());
        });
        
        outj.graph = {};
        outj.graph.nodes = outnodes;  // array of nodes
        outj.graph.edges = outedges;  // array of edges
        
        return outj;
    },

    // Expand out one hop from a selected node. This adds nodes to the graph around the specified node.
    // injson       - is an object that contains the following: { nodes: [array of nodes], edges: [array of edges] }
    // innode       - is the user selected node
    showGraph1Hop: function(injson, innode)
    {
        // Need to make all of the ids unique (except for the expanded node) so that they 
        // do not conflict with existing nodes that are already in the graph.
        // Child nodes of an expanded node will have a unique node id that are prefixed by the name/number
        // Example: 35152523411X3   Where 'X' is the delimiter
        var scope = this;
        if (injson.nodes.length == 0 || innode == null) {
            // TODO may want to alert this - unexpected error
            return;
        }

        var node;
        var innumber = innode.data().name;
        var oldSelectedNodeId = "-1";
        var innodePos = innode.position();
        
        // DEBUG
        //console.log("node orig position = " + innodePos.x + ", " + innodePos.y);
        
        // Move the selected node 'out' further away from its connected node to make some room for the expanded neighborhood
        var connectedEdge = innode._private.edges[0];
        var connectedNodes = connectedEdge.connectedNodes();
        var connectedNode = null;
        connectedNodes.each(function(indx, cnode) {
            if (cnode.data().id != innode.data().id) {
                connectedNode = cnode;  
                return;
            }
        });
        var conNodePos = connectedNode.position();
        var dx = innodePos.x - conNodePos.x;
        var dy = innodePos.y - conNodePos.y;
        var newx = innodePos.x + dx * 1.5;
        var newy = innodePos.y + dy * 1.5;
        innode.position({x: newx, y: newy});
        innodePos = innode.position();
        
        // DEBUG
        //console.log("node new position = " + innodePos.x + ", " + innodePos.y);
                
        // First change the node ids
        var n = injson.nodes.length;
        var twoPIion = 0;
        var radius;
        
        for (var i = 0; i < n; i++) {
            node = injson.nodes[i];
            twoPIion = 2 * Math.PI * i / n;
            radius = scope.minLeafDistance + (n*2);
            
            if (node.data.name == innumber) {   
                // this node is the selected node we are expanding
                // it already exists in the graph so we must use its existing id
                oldSelectedNodeId = node.data.id;    // remember for the next section
                node.data.id = innode.data().id;     // keep the id of the node we are expanding 
                node.position = innodePos;     // keep the nodes current position
            }
            else {
                node.data.id = innumber + 'X' + node.data.id;    // make ids unique
                node.data.color = graphVisCommon.Colors.expandedDefNode;   // Change the color of the expanded nodes
                node.data.expanded = true;
                // For the initial display, locate the nodes in a circle around the expanded node.
                // If the user changes the layout, this positioning will be lost - that is ok 
                newx = innodePos.x + (radius  * Math.cos(twoPIion));
                newy = innodePos.y + (radius * Math.sin(twoPIion));
                node.position = { x: newx, y: newy };
            }   
        }

        // Next change the references in the edges and directions
        // We can't do both the above and below in one loop. 
        // The node ids must all be changed first then the edges and directions.

        // If the node has adjacencies (edges), change the ids in the adajencies list to be unique
        // Note: This json node is not fully fleshed, it has no methods like: node.each(function(adj)
        for (var a = 0; a < injson.edges.length; a++) {
            var adj = injson.edges[a];  // aka: 'edges'
            if (adj.data.source && adj.data.target) {
                if (adj.data.source != oldSelectedNodeId) {
                    adj.data.source = innumber + 'X' + adj.data.source;
                }
                else {
                   adj.data.source = innode.data().id;
                }

                if (adj.data.target != oldSelectedNodeId) {
                    adj.data.target = innumber + 'X' + adj.data.target;
                }
                else {
                   adj.data.target = innode.data().id;
                }

                var dir = adj.data.direction;  // direction array conatins the node ids of the 2 connected nodes
                if (dir.length == 2) {
                    if (dir[0] != oldSelectedNodeId) {
                        dir[0] = innumber + 'X' + dir[0];
                    }
                    else {
                       dir[0] = innode.data().id; 
                    }
                    if (dir[1] != oldSelectedNodeId) {
                        dir[1] = innumber + 'X' + dir[1];
                    }
                    else {
                        dir[1] = innode.data().id;
                    }
                }
            }  
        }

        var scope = this;
        // Add JSON data to the graph. This displays the added nodes in a circle around the selected node
        scope.gv.add(injson);   
        
        // Add the expanded node ids to a list for later removal (unexpand)
        var expNodeIdList = [];
        for (i = 0; i < injson.nodes.length; i++) {
            node = injson.nodes[i];
            if (node.data.id.indexOf("X") > 0) {
                    expNodeIdList.push(node.data.id);
            }
        }
        var eNList = { expandFrom: innumber, nodeIds: expNodeIdList};
        scope.expandedNodes.push(eNList);
           
    }, // function showGraph1Hop
	
    // remove nodes and connected edges for the specified node ids
    // nodeIdList       - Array of node ids
    removeNodesEdges: function(nodeIdList) {
        var scope = this;
        var nodeId;
        for (var i = 0; i < nodeIdList.length; i++) {
            nodeId = nodeIdList[i];
            var node = scope.gv.filter("node[id = '" + nodeId + "']");
            if (node) {
                var edges = node.connectedEdges();
                if (edges) {
                    scope.gv.remove(edges);
                }
                scope.gv.remove(node);
            }
        }
    },
    
    // If the specified node has been expanded, this deletes the expanded nodes and edges (and their labels)
    unexpand1Hop: function (node) {  
        var scope = this;
        if (node) {
            var number = node.data().name; // number of the node that MAY have been expanded
            var ids2Remove = [];

            if (scope.expandedNodes && scope.expandedNodes.length > 0) {                 
                var eNodes, e;
                for (e = 0; e < scope.expandedNodes.length; e++) {
                   eNodes = scope.expandedNodes[e];
                   if (eNodes.expandFrom == number) {
                       ids2Remove = eNodes.nodeIds;
                       break;
                   }
                }

                if (ids2Remove.length > 0) {
                    // this removes the nodes and edged but not the labels
                    scope.removeNodesEdges(ids2Remove);
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
	
    showAll: function() {
        var scope = this; 
        scope.gv.nodes().show();
        scope.gv.edges().show();
        scope.gv.edges().removeClass('toggled-hide');
        scope.gv.edges().addClass('toggled-show'); // this shows the edge labels
    },
    
    // This also hides the edges connected to the node
    hideNode:function(node)
    {
        if (node) {
            // DEBUG
            //console.log("hide Node " + node.data().name);
            
            var edges = node.connectedEdges();
            if (edges) {
                edges.removeClass('toggled-show');
                edges.addClass('toggled-hide'); // this hides the edge labels
                edges.hide();
            }
            node.hide();
            node.data().visible = false;    // must set this state for graph export
        }
    },
    
    // This hides just the edge
    hideEdge:function(edge)
    {
        if (edge) {
            // DEBUG
            //console.log("hide edge " + edge.data().amount);
            
            //edge.removeClass('toggled-show');
            //edge.addClass('toggled-hide'); // this hides the edge labels
            edge.hide();
            edge.data().visible = false;    // must set this state for graph export
        }
    },
    
    // This shows just the edge
    showEdge: function(edge) {
        if (edge) {
            // DEBUG
            //console.log("show Edge " + edge.data().amount);
            
            edge.show();
            edge.data().visible = true;    // must set this state for graph export
        }
    },
    
    // This also shows the edges connected to the node
    showNode: function(node)
    {
        if (node) {
            // DEBUG
            //console.log("show Node " + node.data().name);
            
            node.show();
            node.data().visible = true;    // must set this state for graph export
            var edges = node.connectedEdges();
            if (edges) {
                edges.show();
                edges.removeClass('toggled-hide');
                edges.addClass('toggled-show'); // this shows the edge labels
            }
        }
    },

    // Initialize
    // config   - Object with the following attributes
    //          width   - width of the graph display area pixels
    //          height  - height of the graph display area pixels
    //          rightBorder - offset of right border in pixels
    //          leftBorder  - offset of left border in pixels
    //          topBorder   - offset of top border in pixels
    //          botBorder   - offset of bottom border in pixels
    //          other params - TBD
    //          
    // owner    - Pointer to the caller's context
    initGraph: function(config, owner) {
        var scope = this;        
        if (config) {
            if (config.width) {
                scope.dispWidth = config.width;
            }
            if (config.height) {
                scope.dispHeight = config.height;
            }
            if (config.rightBorder) {
                scope.dispRightBorder = config.rightBorder;
            }
            if (config.leftBorder) {
                scope.dispLeftBorder = config.leftBorder;
            }
            if (config.topBorder) {
                scope.dispTopBorder = config.topBorder;
            }
            if (config.botBorder) {
                scope.dispBotBorder = config.botBorder;
            }
        }
        scope.owner = owner;    // caller context
        scope.expandedNodes = [];
        
        // DEBUG
        //console.log("initGraph id = " + scope.id + ", width = " + scope.dispWidth + ", height = " + scope.dispHeight);
        
        // TODO - use the optional config param to set/override the styling
         
        if (scope.gv == null) {
            cytoscape({
                container: document.getElementById(scope.id),   // id must be set by the caller
                ready: function() {
        
                	overrideRegisterInstance(); // see shared/cytoOverrides.js
                	overrideCOSE();
                	overrideARBOR();
                	
                    $("#" + scope.id).cytoscape({
							showOverlay: false,
                            style: cytoscape.stylesheet()
                                .selector('node').css({
                                        'content': 'data(name)',
                                        'text-valign': 'bottom',
                                        'color': 'black',
                                        'background-color': 'data(color)',
                                        'font-size': 10,
                                        'text-outline-width': 1,
                                        'text-outline-color': 'white',
                                        'shape': 'data(type)',  // added
                                        'width': 16,  
                                        'height': 16   
                                }).selector('edge').css({
                                        'content':'data(amount)',   
                                        'text-valign': 'center',
                                        'color': 'black',   // color of the edge label
                                        'text-outline-width': 1,
                                        'font-size': 10,
                                        'text-outline-color': 'white',
                                        'line-color': graphVisCommon.Colors.defaultEdge, 
                                        'target-arrow-color': 'DarkBlue',
                                        'target-arrow-shape': 'triangle',
                                        'width': 'data(lineWidth)'   
                                }).selector('node:selected').css({
                                        'background-color': graphVisCommon.Colors.selectedNode,
                                        'line-color': 'black',
                                        'text-outline-color': 'yellow',
                                        'target-arrow-color': 'black',
                                        'source-arrow-color': 'black',
                                        'border-color': 'black',
                                        'shape': 'data(type)',  // added
                                        'font-size': 12
                                }).selector('edge:selected').css({
                                        'background-color': graphVisCommon.Colors.selectedEdge,
                                        'line-color': 'black',
                                        'text-outline-color': 'yellow',
                                        'target-arrow-color': 'black',
                                        'source-arrow-color': 'black',
                                        'border-color': 'black',
                                        'font-size': 12
                                        // 'width': 20,  
                                        //'height': 20 
                                }).selector('.toggled-show').css({  // Workaround for showing and hiding edge labels
                                        'content':'data(amount)'
                                }).selector('.toggled-hide').css({
                                        'content':' '
                                }).selector('.faded').css({
                                        'opacity': 0.25,
                                        'text-opacity': 0
                                })
                    });

                    scope.gv = $("#" + scope.id).cytoscape("get");
                    scope.initialized = true;
                    scope.reset();
                   
                }
            });
        } // END TEST
    },

    getGv: function() {
            return this.gv;
    },

    zoom: function(fzoomLevel) {
        if (this.initialized && this.gv) {
            this.gv.zoom(fzoomLevel);
        }
    },

    resize: function(width,height) {
        var scope = this;
        if (scope.initialized && scope.gv) {
            scope.gv.fit();
        }
        scope.dispWidth = width;
        scope.dispHeight = height;
        // DEBUG
        //console.log("resize: width = " + scope.dispWidth + ", height = " + scope.dispHeight);
    },
   
    // Reposition leaf nodes 'around' the origin position
    repositionNodes: function(originPos, leafList) {
        var scope = this;
        var n = leafList.length;
        var node;
        var twoPIion = 0;
        var radius = scope.minLeafDistance + (n*2);
       
       // DEBUG
       //console.log("repositionNodes num leaves = " + n);
       
       for (var i = 0; i < n; i++) {
            node = leafList[i];
            twoPIion = 2 * Math.PI * i / n;
            // Locate the nodes in a circle around the cluster origin.
            // If the user changes the layout, this positioning will be lost - that is ok 
            var newx = originPos.x + (radius  * Math.cos(twoPIion));
            var newy = originPos.y + (radius * Math.sin(twoPIion));
            node.position({ x: newx, y: newy });
        }
    },
   
    // For each node cluster with leafs, reposition the leafs 'around' the cluster origin
    // nodelist    - List of cluster origin nodes.
    repositionClusters: function(nodelist) {
        var scope = this;
        var originNode = {};
        var originItem = {};
        for (originItem in nodelist) {
            originNode = nodelist[originItem];

            // DEBUG
            //console.log("repositionClusters: cnode = " + originNode.data().name);

            var originPos = originNode.position();
            // DOES NOT WORK var edges = originNode.edgesWith();
            var edges = originNode._private.edges;
            var leafList = [];
            for (var e = 0; e < edges.length; e++) {
                var edge = edges[e];
                var connectedNodes = edge.connectedNodes();
                connectedNodes.each(function(indx, n) {
                    if (n.degree() == 1) { // This is a leaf node
                        leafList.push(n);
                    }
                });
            }

            // At this point we should have a cluster origin node and all its leaf nodes
            // Reposition the leafs
            scope.repositionNodes(originPos, leafList);
        }
    },
   
    // layoutName   - User selected layout name
    changeLayout: function(layoutName) {
        var scope = this;
        if (scope.initialized) {
            scope.currentLayout = layoutName;
            switch (layoutName) {
                case 'circle':
                    scope.gv.layout({
                        name: 'circle',
                        fit: true,          // fit the viewport to the graph
                        rStepSize: 10,      // the step size for increasing the radius if the nodes don't fit on screen
                        padding: 30,        // padding on fit
                        // startAngle:  3/2 * Math.PI,  // position of the first node
                        counterclockwise: false,    // whether the layout should go counterclockwise (true) or clockwise
                        ready: undefined,   // callback on layoutready
                        stop: function() {
							if (!(scope.setBusy == undefined)) {
								scope.setBusy(false);
							}
                        
                            // DEBUG
                            //console.log("circle layout done");
                            // TODO - needs more tuning
                            // for larger graphs, the circle is huge and does not fit within the viewport on initial display
							
							if (scope.owner.getProgressBar) {
								var pb = scope.owner.getProgressBar();
							
								if (pb) {
									pb.updateProgress(1, "100%");
								}
							}
                            // scope.gv.zoom(0.8);
                        }
                    });
                    break;

                 case 'grid':
                    scope.gv.layout({
                        name: 'grid',
                        fit: true,          // fit the viewport to the graph
                        rows: undefined,    // force number of rows in the grid
                        columns: undefined, // force number of cols in the grid
                        ready: undefined,   // callback on layoutready
                        stop: function() {
							if (!(scope.setBusy == undefined)) {
								scope.setBusy(false);
							}
                            // DEBUG
                            //console.log("grid layout done");
							
							if (scope.owner.getProgressBar) {
								var pb = scope.owner.getProgressBar();
							
								if (pb) {
									pb.updateProgress(1, "100%");
								}
							}
                            // scope.gv.zoom(0.9);
                        }
                    });
                    break;

                case 'breadthfirst':
                case 'tree':
                    
                    
                    scope.gv.layout({
                        name: 'breadthfirst',
                        fit: true,          // fit the viewport to the graph
                        directed: true,     // whether the tree is directed downwards
                        padding: 15,        // padding on fit
                        circle: false,      // put depths in concentric circles if true, put depths top down if false
                        roots: undefined,   // the root(s) of the tree(s), can be an array of node ids
                        ready: undefined,   // callback on layoutready
                        stop: function() {
							if (!(scope.setBusy == undefined)) {
								scope.setBusy(false);
							}
                            // DEBUG
                            //console.log("tree layout done");
							if (scope.owner.getProgressBar) {
								var pb = scope.owner.getProgressBar();
							
								if (pb) {
									pb.updateProgress(1, "100%");
								}
							}
                            // scope.gv.zoom(0.9);
                        }
                    });
                    break;

                    case 'cose':
                    	// enable the Halt Layout button if it exists
						if (scope.owner.getStopButton) {
							var stopBtn = scope.owner.getStopButton();
							stopBtn.setDisabled(false);
							stopBtn.setCurrentLayout("COSE");
						}
						
                        scope.gv.layout({
                                name: 'cose',
                                refresh		: 2500,			// Number of iterations between consecutive screen positions update (0 -> only updated on the end)
                                fit		: true, 
                                randomize	: false,
                                debug		: false,

                                nodeRepulsion	: 99999999,		// Node repulsion (non overlapping) multiplier
                                nodeOverlap	: 5000,			// Node repulsion (overlapping) multiplier
                                idealEdgeLength	: 10,			// Ideal edge (non nested) length
                                edgeElasticity	: 1,			// Divisor to compute edge forces
                                nestingFactor	: 5, 			// Nesting factor (multiplier) to compute ideal edge length for nested edges
                                gravity		: 250, 			// Gravity force (constant)

                                numIter		: 5000,			// Maximum number of iterations to perform
                                initialTemp	: 500,			// Initial temperature (maximum node displacement)
                                coolingFactor	: 0.95, 		// Cooling factor (how the temperature is reduced between consecutive iterations)
                                minTemp 	: 8,			// Lower temperature threshold (below this point the layout will end)

                                feedbackRate	: 10,			// get a status response from layout execution for every <feedbackRate> iterations

                                onFeedback: function(opt) {
                                	// feedback callback.  frequency of calls determined by options.feedbackRate
									var progress = this.minTemp / opt.temp;
									
									if (scope.owner.getProgressBar) {
										var pb = scope.owner.getProgressBar();
									
										if (pb) {
											pb.updateProgress(progress, Math.floor(progress * 100) + "%");
										}
									}
                                },

                                stop: function() {
									if (!(scope.setBusy == undefined)) {
										scope.setBusy(false);
									}
									
									if (scope.owner.getStopButton) {
										var stopBtn = scope.owner.getStopButton();
										stopBtn.setDisabled(true);
										stopBtn.setCurrentLayout(undefined);
									}
                                        // DEBUG
                                    //console.log("tree layout done");

									if (scope.owner.getProgressBar) {
										var pb = scope.owner.getProgressBar();
									
										if (pb) {
											pb.updateProgress(1, "100%");
										}
									}
                                    // scope.gv.zoom(0.9);
                                }
                            });
                            break;
					
                 case 'arbor':
                 case 'arbor-snow':     // force directed arbor - out of the box
                    scope.doForceDirectedLayout('arbor-snow');
                    break;
                    
                 case 'arbor-wheel':    // custom arbor
                    scope.doForceDirectedLayout('arbor-wheel');
                    break;

                default:
                    // do nothing, invalid layout
                    break;
            }
        }   // if initialized
    },

    // refresh the current layout
    refreshLayout: function() {
        var scope = this;
        if (scope.currentLayout) {
            scope.changeLayout(scope.currentLayout);
            //scope.gv.nodes.unlock();
        }
    },
    
    // Default layout is a tree to get the initial display to show up quickly.
    // The user can then change to use a different layout
    doDefaultLayout: function() {
        var scope = this;
        
        if (!(scope.setBusy == undefined)) {
		scope.setBusy(true);
        }
	/* MFM commented out 
        else {
		var mbox = Ext.Msg.show({
			title: 'Graph Display',
			msg: 'The graph is being prepared for display. Please wait...',
			buttons: Ext.Msg.OK
		});
        }
        */
        scope.currentLayout = 'breadthfirst';
        scope.gv.layout({
            name: 'breadthfirst',
            fit: true,          // fit the viewport to the graph
            directed: true,     // whether the tree is directed downwards
            padding: 15,        // padding on fit
            circle: false,      // put depths in concentric circles if true, put depths top down if false
            roots: undefined,   // the root(s) of the tree(s)
            ready: function() {  // callback on layoutready
            },
            stop: function() {
				if (scope.owner.getProgressBar) {
					var pb = scope.owner.getProgressBar();
				
					if (pb) {
						pb.updateProgress(1, "100%");
					}
				}
                // scope.gv.zoom(0.9);
	        	if (!(scope.setBusy == undefined)) {
	        		scope.setBusy(false);
                }
                /* MFM commented out
                if (mbox) {
                    var dt4 = window.setTimeout(function() {
                            window.clearTimeout(dt4);
                            mbox.close();
                    },5000);
                }
                */
            }
        });
    },
        
    // fdType   - 'arbor-wheel' or 'arbor-snow'
    doForceDirectedLayout: function(fdType) {
            var scope = this;
            
            // for feedback while the graph is in progress
	    if (!(scope.setBusy == undefined)) {
		scope.setBusy(true);
            }
            /* MFM commented out
            else {
		    var mbox = Ext.Msg.show({
			title: 'Graph Display',
			msg: 'The graph is being prepared for display. Please wait...',
			buttons: Ext.Msg.OK
		    });
	    }
            */
	    	// enable the Halt Layout button if it exists
		    if (scope.owner.getStopButton) {
				var stopBtn = scope.owner.getStopButton();
				stopBtn.setDisabled(false);
				stopBtn.setCurrentLayout("ARBOR");
			}
	    
	    	var progress = 0;
            scope.gv.layout({
                    name: 'arbor',

                    liveUpdate: true,       // whether to show the layout as it's running
                    maxSimulationTime: 90000, // max time in ms to run the layout
                    fit: true,              // fit to viewport
                    padding: [ 50,10,50,10 ], // top, right, bottom, left
                    ungrabifyWhileSimulating: false, // so you CAN drag nodes during layout

                    // forces used by arbor (use arbor default on undefined)
                    repulsion: 8000,    //15000,       // force repelling the nodes from each other
                    stiffness: 300,     //500, //undefined,    // rigity of the edges
                    friction: undefined,    // amount of damping in the system
                    gravity: true,          // additional force attracting nodes to the origin
                    fps: 240,               // frames per second
                    precision: 0.2, // 0.5, // 0 is fast but jittery, 1 is smooth but cpu intensive

                    // static numbers or functions that dynamically return what these
                    // values should be for each element
                    nodeMass: undefined, 
                    edgeLength: 3,  //undefined,
                    stepSize: 1,            // size of timestep in simulation

                    // function that returns true if the system is stable to indicate
                    // that the layout can be stopped
                    stableEnergy: function( energy ){
                    	var max_progress = 0.3 / energy.max;
						var mean_progress = 0.2 / energy.mean;
						var avg_progress = (max_progress + mean_progress) / 2;
						
						if (avg_progress > progress) {
							progress = avg_progress;
						}
						
						if (scope.owner.getProgressBar) {
							var pb = scope.owner.getProgressBar();
						
							if (pb) {
								pb.updateProgress(progress, Math.floor(progress * 100) + "%");
							}
						}
						return (energy.max <= 0.3) || (energy.mean <= 0.2);
                    },
                    ready: function() { // callback on layoutready
                        // DEBUG
                        //console.log("arbor ready");
                    },
                    stop: function() {
                        // DEBUG
                        //console.log("arbor done");

                    	if (scope.owner.getStopButton) {
							var stopBtn = scope.owner.getStopButton();
							stopBtn.setDisabled(true);
							stopBtn.setCurrentLayout(undefined);
						}
                    	
                        if (fdType == "arbor-wheel") {
                            // Optimize the leaf node positions and layout
                            // Find all (leaf) nodes having just one edge
 
                            // NOTE
                            // The stop function will be triggered before the graph layout optimization 
                            // is actually completed (stops moving), so we have to delay this a bit.
                            // Timeout will depend on the total number of nodes and Browser speed, and the FD algo
                            // This is a bit kludgey to workaround the problem of the stop function being called too early
                            var delayFactor = 20;
                            if (window.navigator.appVersion.indexOf("Chrome") < 0) {    // Chrome is a LOT faster than Firefox
                                delayFactor = 100;  
                            }
                            var delay = (scope.currentNumNodes > 0)? scope.currentNumNodes * delayFactor : 2000;
                            var dt2 = window.setTimeout(function() {
                                window.clearTimeout(dt2);
                                var anodes = scope.gv.nodes();
                                var originNodes = {};

                                // Find all of the cluster origin nodes having 1 or more leafs
                                anodes.each(function(indx, n) {
                                   var degree = n.degree();
                                   if (degree == 1) {   // This is a leaf
                                       var nodeName = n.data().name;

                                       // Should only be one edge
                                       //DOES NOT WORK: var connectedNodes = n._private.edges.connectedNodes();
                                       var connectedEdge = n._private.edges[0];
                                       var connectedNodes = connectedEdge.connectedNodes();
                                       connectedNodes.each(function(indx, cnode) {
                                           var cnodeName = cnode.data().name;
                                           var cnodeId = cnode.data().id;
                                           if (cnodeName != nodeName) { // This is the node the leaf is connected to
                                               if (originNodes[cnodeId] === undefined || originNodes[cnodeId] == null) {
                                                    originNodes[cnodeId] = cnode; // just push unique cnodes
                                               }
                                           }
                                       });
                                   }
                                }); 

                                // For each cluster with leafs, reposition the leafs 'around' the cluster origin
                                scope.repositionClusters(originNodes);
		                                
			        			if (!(scope.setBusy == undefined)) {
			        				scope.setBusy(false);
                                }
                            /* MFM commented out
                                if (mbox) {
                                    var mboxcloseDelay = 5000;
                                    if (window.navigator.appVersion.indexOf("Chrome") < 0) {    // Chrome is a LOT faster than Firefox and IE
                                        mboxcloseDelay = 20000;
                                    }
                                    
                                    var dt3 = window.setTimeout(function() {
                                        window.clearTimeout(dt3);
                                        mbox.close();
                                    }, mboxcloseDelay);
                                }
                                */
								if (scope.owner.getProgressBar) {
									var pb = scope.owner.getProgressBar();
								
									if (pb) {
										pb.updateProgress(1, "100%");
									}
								}
                            }, delay); 
                        }   
                        else {
							if (!(scope.setBusy == undefined)) {
								scope.setBusy(false);
							}
							
							if (scope.owner.getProgressBar) {
								var pb = scope.owner.getProgressBar();
							
								if (pb) {
									pb.updateProgress(1, "100%");
								}
							}
                            // scope.gv.zoom(0.9);
                            
                            /* MFM commented out
                            if (mbox) {
                                var mboxcloseDelay = 3000;
                                if (window.navigator.appVersion.indexOf("Chrome") < 0) {    // Chrome is a LOT faster than Firefox
                                    mboxcloseDelay = 12000;
                                }
                                    
                                var dt3 = window.setTimeout(function() {
                                        window.clearTimeout(dt3);
                                        mbox.close();
                                }, mboxcloseDelay);
                            }
                            */
                        }
                    }
            });
    },
	
    reset: function() {
            var scope = this;
            scope.setHandlers();	
            scope.doDefaultLayout();
            scope.gv.panningEnabled(true);
    },
	
    setHandlers: function() {
        var scope = this;

        // SELECT NODE - This function is called for every node selected
        scope.gv.on('select','node', function(e) {
            var node = e.cyTarget;   // the current selected node

            // node's data elements
            // attrs[], color, id, idVal, label, name, type

            // This will return all of the selected nodes
            //var selectedNodes = scope.gv.$('node:selected');

            // DEBUG
            //console.log("node selected = " + node.data().name);

            node._private.locked = false; // no documented API that unlocks a single node, so we must do it under the hood
            scope.owner.nodeClick(node);
        });

        scope.gv.on('cxttapend','node', function(e) {
            var node = e.cyTarget;   // the current selected node
            if (!(scope.owner.nodeRightClick==undefined))
	            scope.owner.nodeRightClick(node);
        
        });
        
        // SELECT EDGE - This function is called for every edge selected
        scope.gv.on('select','edge', function(e) {
            var data = e.cyTarget.data();   // the current selected edge

            // edge's data elements
            // amount, direction[], id, lineWidth, source, target, type, weight

            // This will return all of the selected edges
            //var selectedEdges = scope.gv.$('edge:selected');

	    var edge = e.cyTarget;
            scope.owner.edgeClick(edge);
            // DEBUG
            //console.log("edge selected id = " + data.id + ", weight = " + data.weight);
        });

        // DRAG NODE - This function is called for every node dragged
        // The border checks are to prevent the node from being dragged outside of the visible graph disp area
        scope.gv.on('drag','node', function(e) {
            var node = e.cyTarget;
            var data = node.data();   // the current selected edge
            var pos = node.renderedPosition();
            var xmax = scope.dispWidth - scope.dispRightBorder - scope.dispLeftBorder - 10;   // TODO calculate and store these once, not each time
            var ymax = scope.dispHeight - scope.dispBotBorder - scope.dispTopBorder - 10;

            // DEBUG
            //console.log("node dragged START = " + data.name + "dispWidth = " + scope.dispWidth + ", dispHeight = " + scope.dispHeight + 
            //    ", x = " + pos.x + ", y = " + pos.y + " isLocked = " + node._private.locked);

            // Constrain the position to the visible viewing area. cytoscape.js does not do this by default
            if (pos.x > scope.dispLeftBorder + 5 && pos.x < xmax &&
                pos.y > scope.dispTopBorder + 5 && pos.y < ymax) {
                node._private.locked = false;   // no documented API that locks a single node, so we must do it under the hood
            }
            else {
                node._private.locked = true;
            }

            // unlock and reposition the locked node after 1 sec
            var ult = null;
            if (node.locked()) {
                ult = window.setTimeout(function() {
                    window.clearTimeout(ult);
                    node._private.locked = false;   // no documented API that unlocks a single node, so we must do it under the hood
                    
                    if (pos.x <= scope.dispLeftBorder + 5) {
                       node.renderedPosition('x', scope.dispLeftBorder + 5);   // was + 5 
                    }
                    if (pos.x >= xmax) {
                       node.renderedPosition('x', xmax - 5);   // was - 5 
                    }
                    if (pos.y <= scope.dispTopBorder + 5) {
                       node.renderedPosition('y', scope.dispTopBorder + 5);    // was + 5
                    }
                    if (pos.y >= ymax) {
                       node.renderedPosition('y', ymax - 5);   // was - 5 
                    }
                    
                    scope.gv.fit(); // this works much better than zooming
                    
                }, 1000);
            }

        });

        // MOUSEUP This function is called when a node is selected and also for every node dragged when dragging is completed
        scope.gv.on('mouseup','node', function(e) {
            var data = e.cyTarget.data();   // the current selected edge
            e.cyTarget._private.locked = false;
            scope.gv.$("node[id = " + data.id + "]").trigger('select'); 

            //scope.owner.nodeClick(e.cyTarget);
        });

        var timeoutFn = null;
		
		scope.gv.on('mouseover', 'node', function(e) {
			var x = e.originalEvent.layerX;
			var y = e.originalEvent.layerY;
			var distFromTop = e.originalEvent.clientY - e.originalEvent.layerY;
			var node = e.cyTarget;   // the current selected node
			
			timeoutFn = setTimeout(function() {
				if (!(scope.owner.nodeMouseOver == undefined)) {
					scope.owner.nodeMouseOver(node);
					
					// if a popup window cannot be (re)created, scope.owner.mouseOverPopUp will be undefined
					if (!(scope.owner.mouseOverPopUp == undefined)) {
						
						// prevent y coordinate from being out-of-bounds
						y = y - scope.owner.mouseOverPopUp.height;
						if (y < 0) 
							y = 0;
						y += distFromTop;
						
						scope.owner.mouseOverPopUp.showAt(x, y);
					}
				}
			}, 1000); // hover for one second
			
		});

		scope.gv.on('mouseout', 'node', function(e) {
			clearTimeout(timeoutFn);
			if (scope.owner.mouseOverPopUp)
				scope.owner.mouseOverPopUp.hide();
		});         
    }
        
}); // define

