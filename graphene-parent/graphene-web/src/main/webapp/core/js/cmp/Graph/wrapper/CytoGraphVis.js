function CytoscapeGraphVis(div_id) {
	
	this.id = div_id;
	this.gv = null;
	this.initialized = false;
	this.currentLayout = null;
	this.owner = null;
	this.searchName = "";
	
	this.userName = "unknown";
	
	this.CONSTANTS = function(key) {
		// protected and immutable constants
		var _legend = {
			textColor: "black",
			textOutlineColor: "white",
			fontSize: 10,
			selectedFontSize: 12,
			nodeSize: 16,
			selectedNodeSize: 24,
			selectedTextColor: "yellow",
			lineColor: "black",
			defaultNode:  '#00FFFF',
			selectedNode: 'DarkBlue',
			defaultEdge:  '#23A4FF',
			selectedEdge: '#0B0B0B',
			expandedDefNode: '#F66CFB',
			dijkstraPath: "red",
			dijkstraSize: 5,
			outgoingHoverTextColor: "#2D55BE",
			outgoingHoverColor: "#111111",
			outgoingHoverSize: 2,
			incomingHoverTextColor: "#FF8040",
			incomingHoverColor: "#111111",
			incomingHoverSize: 2,

			borderWidth: 3,
			borderColor: "black",
			borderStyle: "solid", // or "dotted" or "dashed"
			
			fillColor: "rgba(0, 0, 200, 0.75)",
			activeFillColor: "rgba(92, 194, 237, 0.75)",
			
			defaultLayout: "breadthfirst",
			minLeafDistance: 60
		};
		
		if (_legend.hasOwnProperty(key)) {
			return _legend[key];
		} else {
			return null;
		}
	};
	
	this.utils = new Utils(this);
	
	var _layoutManager = new LayoutManager(this);
	var _stateManager = new StateManager(this);
	var _dijkstraManager = new DijkstraManager(this);
	var _generator = new GraphGenerator(this);
	
	this.getGv = function() { return this.gv; };
	this.getOwner = function() { return this.owner; };
	this.getCurrentLayout = function() { return this.currentLayout; };
	this.getSearchName = function() { return this.searchName; };
	
	this.getLayoutManager = function() { return _layoutManager; };
	this.getStateManager = function() { return _stateManager; };
	this.getDijkstraManager = function() { return _dijkstraManager; };
	this.getGenerator = function() {return _generator; };
	
	this.getUserName = function() { return this.userName; };
	this.setUserName = function(name) { this.userName = name; };
	
	// extend this' API with the layout manager's methods
	this.changeLayout = _layoutManager.changeLayout;
	this.registerLayout = _layoutManager.registerLayout;
	
	// extend this' API with the state manager's methods
	this.deleteNodes = _stateManager.deleteNodes;
	this.deleteEdges = _stateManager.deleteEdges;
	this.exportGraph = _stateManager.exportGraphJSON;
	this.importGraph = _stateManager.importGraphJSON;
	this.setNodeVisibility = _stateManager.setNodeVisibility;
	this.setEdgeVisibility = _stateManager.setEdgeVisibility;
	this.showAll = _stateManager.showAll;
	this.add = _stateManager.add;
	this.remove = _stateManager.remove;
	this.getSelectedNodes = _stateManager.getSelectedNodes;
	this.getSelectedEdges = _stateManager.getSelectedEdges;
};

/**
 *	Initialize this graph wrapper div with a cytoscape graph.
 *		@param config:Object - configuration parameters
 *		@param owner:Object - Reference to the parent display that houses this graph visualization (useful for scope)
 *		@param callbackFn:Function - (Optional) a callback function executed after cytoscape initializes the graph
 *		@param isUndirected:Boolean - (Optional) a flag to change the styling of the graph to prevent implication of directed edges		
 *
 *		note: anything else passed to this function will be given to callbackFn as parameters
 */
CytoscapeGraphVis.prototype.init = function(/*config, owner[, callbackFn, isUndirected, ...args]*/) {
	var _this = this;
	
	var args = [].slice.apply(arguments);
	var config = args.shift();
	var owner = args.shift();
	var onLoadCallback = args.shift();
	var isUndirected = args.shift();
	// at this point, args is an array of whatever other arguments were passed to this function
	
	if (_this.gv !== null) {
		console.log("Graph Visualization already exists for id = " + _this.id);
		return;
	}
	
	_this.owner = owner;
	
	cytoscape({
		container: document.getElementById(_this.id),
		ready: function() {
			overrideRegisterInstance();
			overrideCOSE();
			overrideARBOR();
			
			var style = cytoscape.stylesheet();
			style.selector("node").css({
				'content': 'data(label)',
				'text-valign': 'bottom',
				'color': _this.CONSTANTS("textColor"),
				'background-color': 'data(color)',
				'font-size': _this.CONSTANTS("fontSize"),
				'text-outline-width': 1,
				'text-outline-color': _this.CONSTANTS("textOutlineColor"),
				'width': 'data(size)',
				'height': 'data(size)'
			});
			style.selector("$node > node").css({
				'padding-top': '10px',
				'padding-left': '10px',
				'padding-bottom': '10px',
				'padding-right': '10px',
				'text-valign': 'top',
				'text-halign': 'center',
				'color': _this.CONSTANTS("textColor"),
				'background-color': 'data(color)',
				'font-size': _this.CONSTANTS("fontSize"),
				'text-outline-width': 1,
				'text-outline-color': _this.CONSTANTS("textOutlineColor")   
			});
			style.selector("node:selected").css({
				'background-color': _this.CONSTANTS("selectedNode"),
				'line-color': 'black',
				'text-outline-color': _this.CONSTANTS("selectedTextColor"),
				'target-arrow-color': _this.CONSTANTS("selectedEdge"),
				'source-arrow-color': _this.CONSTANTS("selectedEdge"),
				'border-color': _this.CONSTANTS("lineColor"),
				'font-size': _this.CONSTANTS("selectedFontSize"),
				'width': 'data(size)' + 5,//_this.CONSTANTS("selectedNodeSize"),  
				'height': 'data(size)' + 5//_this.CONSTANTS("selectedNodeSize")
			});
			if (isUndirected) {
				style.selector("edge").css({
					'curve-style': 'haystack',	// !
					'haystack-radius': 0,		// !
					'content':'data(label)',   
					'text-valign': 'center',
					'color': _this.CONSTANTS("textColor"),
					'text-outline-width': 1,
					'line-style': 'data(lineStyle)',
					'font-size': _this.CONSTANTS("fontSize"),
					'text-outline-color': _this.CONSTANTS("textOutlineColor"),
					'line-color': 'data(color)',
					'width': 'data(count)'
				});
			} else {
				style.selector("edge").css({
					'content':'data(label)',   
					'text-valign': 'center',
					'color': _this.CONSTANTS("textColor"),
					'text-outline-width': 1,
					'line-style': 'data(lineStyle)',
					'font-size': _this.CONSTANTS("fontSize"),
					'text-outline-color': _this.CONSTANTS("textOutlineColor"),
					'line-color': 'data(color)',
					'target-arrow-color': 'data(color)',
					'target-arrow-shape': 'triangle',
					'width': 'data(count)'
				});
			}
			style.selector("edge:selected").css({
				'background-color': _this.CONSTANTS("selectedEdge"),
				'line-color': _this.CONSTANTS("lineColor"),
				'text-outline-color': _this.CONSTANTS("selectedTextColor"),
				'target-arrow-color': _this.CONSTANTS("lineColor"),
				'source-arrow-color': _this.CONSTANTS("lineColor"),
				'border-color': _this.CONSTANTS("lineColor"),
				'font-size': _this.CONSTANTS("fontSize")
			});
			style.selector(".on-path").css({
				'line-color': _this.CONSTANTS("dijkstraPath"),
				'target-arrow-color': _this.CONSTANTS("dijkstraPath"),
				'width': _this.CONSTANTS("dijkstraSize")
			});
			style.selector(".parent-of").css({
				'color': _this.CONSTANTS("outgoingHoverTextColor"),
				'line-color': _this.CONSTANTS("outgoingHoverColor"),
				'target-arrow-color': _this.CONSTANTS("outgoingHoverColor"),
				'text-outline-color': _this.CONSTANTS("outgoingHoverColor")
			});
			style.selector(".child-of").css({
				'color': _this.CONSTANTS("incomingHoverTextColor"),
				'line-color': _this.CONSTANTS("incomingHoverColor"),
				'target-arrow-color': _this.CONSTANTS("incomingHoverColor"),
				'text-outline-color': _this.CONSTANTS("incomingHoverColor")
			});
			style.selector("node.super-node").css({
				'border-width': _this.CONSTANTS("borderWidth"),
				'border-style': _this.CONSTANTS("borderStyle"),
				'border-color': _this.CONSTANTS("borderColor")
			});
			
			$("#" + _this.id).cytoscape({
				zoomingEnabled: true,
				userZoomingEnabled: true,
				panningEnabled: true,
				userPanningEnabled: true,
				boxSelectionEnabled: true,
				touchTapThreshold: 8,
				desktopTapThreshold: 4,
				autolock: false,
				autoungrabify: false,
				autounselectify: false,
				showOverlay: false,
				motionBlur: false,
				layout: _this.getLayoutManager().getRegisteredLayouts(_this.CONSTANTS("defaultLayout"))[0].config,
				style: style
			});
			
			// declare a reference to the completed cytoscape graph object as this.gv
			_this.gv = $("#" + _this.id).cytoscape("get");
			
			// if the plug-in is not included for any reason, do not try to initialize it
			try {
				if (typeof _this.gv.cxtmenu !== "function")
					throw "You must include 'cytoscape.js-cxtmenu.js' in the .html file";
				
				// radial context menu for nodes
				_this.gv.cxtmenu({
					menuRadius: 75, selector: 'node', activePadding: 0,
					fillColor: _this.CONSTANTS("fillColor"), 
					activeFillColor: _this.CONSTANTS("activeFillColor"), 
					commands: [{
						content: "Unmerge",
						select: function() {
							var node = this;
							if (_this.owner.givePromptToUnmerge) { _this.owner.givePromptToUnmerge([node]); } 
							else if (_this.owner.unmergeNode) { _this.owner.unmergeNode(node); } 
							else { console.log("Capability to unmerge nodes is undefined"); }
						}
					}, {
						content: "Connect Node",
						select: function() {
							var node = this;
							var gg = _this.getGenerator();
							if (typeof gg !== "undefined") {
								gg.clear();
								gg.setParent(node);
								gg.changeState("CONNECT");
								gg.givePrompt();
							}
						}
					}, {
						content: "Add Node",
						select: function() {
							var node = this;
							var gg = _this.getGenerator();
							if (typeof gg !== "undefined") {
								gg.clear();
								gg.setParent(node);
								gg.changeState("ADD");
								gg.givePrompt();
							}
						}
					}, {
						content: "Merge Selected Nodes",
						select: function() {
							var node = this;
							var selectedNodes = _this.getSelectedNodes();
							if (_this.owner.givePromptToMerge) { _this.owner.givePromptToMerge(node, selectedNodes); } 
							else if (_this.owner.mergeNodes) { _this.owner.mergeNodes(node, selectedNodes); } 
							else { console.log("Capability to merge nodes is undefined"); }
						}
					}, {
						content: "Expand",
						select: function() {
							var node = this;
							if (_this.owner.expand) { _this.owner.expand(node); } 
							else { console.log("expand() is undefined"); }
						}
					}, {
						content: "Shortest Path",
						select: function() {
							var node = this;
							var dm = _this.getDijkstraManager();
							if (typeof dm !== "undefined") {
								dm.clear();
								dm.setRoot(node);
								dm.setWait(true);
								console.log("Set node id='" + node.data("id") + "' as root.");
							} else { console.log("dijkstra pathfinding is unavailable."); }
						}
					}, {
						content: "Edit",
						select: function() {
							var node = this;
							if (_this.owner.editElement) { _this.owner.editElement(node); } 
							else { console.log("editElement() is undefined"); }
						}
					}, {
						content: "Unexpand",
						select: function() {
							var node = this;
							if (_this.owner.unexpand) { _this.owner.unexpand(node); }
							else { console.log("unexpand() is undefined"); }
						}
					}]
				}); // END NODE CONTEXT MENU
				
				// radial context menu for edges
				_this.gv.cxtmenu({
					menuRadius: 75, selector: 'edge', activePadding: 0,
					fillColor: _this.CONSTANTS("fillColor"), 
					activeFillColor: _this.CONSTANTS("activeFillColor"), 
					commands: [{
						content: "Delete Edge",
						select: function() {
							var edge = this;
							_this.deleteEdges([edge]);
						}
					}, {
						content: "Edit Edge",
						select: function() {
							var edge = this;
							if (_this.owner.editElement) { _this.owner.editElement(edge); } 
							else { console.log("editElement() is undefined"); }
						}
					}, {
						content: "Hide Edge",
						select: function() {
							var edge = this;
							_this.setEdgeVisibility(edge, false, false);
						}
					}]
				}); // END EDGE CONTEXT MENU
				
			} catch(e) {
				console.log(e);
				console.log("Context Menu on cytoscape graph " + _this.id + " will be unavailable.");
			}
			
			if (typeof onLoadCallback == "function") {
				onLoadCallback.apply(_this.owner, args);
			}
			
			_this.initialized = true;
			_this.setHandlers();
		}
	});
};

CytoscapeGraphVis.prototype.reset = function() {
	// this.clear();
	this.setHandlers();
	this.changeLayout(this.CONSTANTS("defaultLayout"), {});
};

/**
 *	Load the given json into cytoscape for rendering and interaction.
 *		@param json:Object - json representation of the graph
 *		@param name:String - identifier of the root node/entity 
 *		@param useSaved:boolean - flag stating whether graph json was persisted or not
 */
CytoscapeGraphVis.prototype.load = function(json, name, useSaved) {
	var _this = this;
	
	_this.searchName = name;
	
	_this.gv.load(json, null, function() {
		var layoutName = (useSaved === true) ? "preset" : _this.CONSTANTS("defaultLayout");
		var layoutConfig = (useSaved === true) ? {positions: json.positionMapping} : {};
		
		_this.changeLayout(layoutName, layoutConfig);
		
		try {
			if (useSaved === true) {
				_this.owner.getToolbar().getEnabledLayoutBtn().toggle(false);
			} else {
				_this.owner.getToolbar().getDefaultLayoutBtn().toggle(true);
			}
		} catch(e) {
			console.log("Unable to toggle the default layout button during load.  This is okay, though.");
		}
		
		// for some reason, elements marked as hidden to not remain hidden when loaded into cytoscape.
		// investigate that later.  for now, just re-hide the hidden elements
		_this.utils.$("[!visible]", function(i, ele) {
			var isFilter = ele.hasClass("toggled-filter");
			if (ele.isNode()) { _this.setNodeVisibility(ele, false, isFilter); }
			else if (ele.isEdge()) { _this.setEdgeVisibility(ele, false, isFilter); }
		});
	});
};

/**
 * 	Given a sub-graph and a central node, place the expanded nodes in an orbit
 *	around the "Origin Node".  Note: this method does not determine what the expanded,
 *	or 1-hop, graph will be.
 *		@param json:JsonObject - JSON graph representation of the nodes/edges found in 1-hop
 *		@param innode:Cytoscape Node - "Origin Node" to extend from the graph and show its 1-hop neighbors 
 *		@returns JSONObject - json representation of the newly expanded graph
 */
CytoscapeGraphVis.prototype.expand = function(json, rootNode) {
	var pos = rootNode.position();
	var removedNodeIDs = [];
	var superNodes = this.utils.$("node.super-node");
	var nodes = json.nodes;
	var edges = json.edges;
	
	// breadthfirst recursion
	var _recurse = function(superNode, id) {
		// BASE CASE: if this node matches id, return false
		var thisId = (typeof superNode.data == "function") ? superNode.data("id") : superNode.data.id;
		if (thisId == id) { return false; }
		
		// BASE CASE: if there are no subnodes in this node, return false
		var subNodes = (typeof superNode.data == "function") ? superNode.data("subNodes") : superNode.data.subNodes;
		if (subNodes == null || typeof subNodes == "undefined" || subNodes.length == 0) { return false; }
		
		// BASE CASE: check all subnodes of this node
		for (var i = 0; i < subNodes.length; i++) {
			if (subNodes[i].data.id == id) { return true; }
		}
		
		// RECURSIVE CASE: none of the subnodes at this level match; check one level down for each subnode
		for (i = 0; i < subNodes.length; i++) {
			if ( _recurse(subNodes[i], id) ) { return true; }
		}
		
		// we checked, re-checked, and double-checked;  the id just isn't here
		return false;
	};
	
	for (var i = 0; i < nodes.length; i++) {
		var node = nodes[i];
		var l = nodes.length;
		var rad = 2 * Math.PI * i / l;
		var radius = this.CONSTANTS("minLeafDistance") + l + l;
		
		superNodes.each(function(index, n) {});
		
		if (node.data.id != rootNode.data("id")) {
			node.data.expanded = true;
			node.position = {
				x: pos.x + (radius * Math.cos(rad)),
				y: pos.y + (radius * Math.sin(rad))
			};
		}
	}
	
	// prevent any duplicate edges from being added to the graph
	for (i = 0; i < edges.length; i++) {
		var edge = edges[i];
		
		var a = edge.data.amount;
		var l = edge.data.label;
		var s = edge.data.source;
		var t = edge.data.target;
		
		edge.data.expanded = true;
		
		// if this edge was attached to a node that was removed, splice it out of the json and continue to the next edge
		if (removedNodeIDs.indexOf(t) != -1 || removedNodeIDs.indexOf(s) != -1) {
			edges.splice(i--, 1);
			continue;
		}
		
		var amountCondition = (typeof a == "string" && a.length > 0) ? "[amount = '"+a+"']" : "";
		var labelCondition = (typeof l == "string" && l.lenght > 0) ? "[label = '"+l+"']" : "";
		
		// can't use IDs since they are not present in the JSON.  Only available after this.gv.add(json), which is too late.
		// (this.source == that.source && this.target == that.target && this.amount == that.amount && this.label == that.label) is 
		// the best match condition so far.
		var matchedEdges = this.utils.$("edge[source = '"+s+"'][target = '"+t+"']" + amountCondition + labelCondition);
		
		// if edge already exists on the graph, splice it out of the json before it's loaded
		if (matchedEdges.length > 0) {
			edges.splice(i--, 1);
			
			matchedEdges.each(function(index, e) {
				console.log("Pruned edge with id='" + e.data("id") + "'");
			});
		}
	}
	
	this.add(json);
	
	var retJSON = {};
	retJSON.nodes = this.gv.nodes().jsons();
	retJSON.edges = this.gv.edges().jsons();
	return retJSON;
};

/**
 * 	Delete all leaf nodes from the given node, essentially undoing a 1-hop expansion.
 *  Note: any edges that were created to existing nodes will remain
 *  	@param rootNode:Cytoscape Node - "Origin Node" from which to retract all leaf nodes
 */
CytoscapeGraphVis.prototype.unexpand = function(rootNode) {
	var _this = this;
	var nodesToDelete = [];
	var edgesToDelete = [];
	var nonLeafNodeIDs = [];
	
	// "[?expanded]" filters for all elements which element.data("expanded") == true
	rootNode.connectedEdges("[?expanded]").connectedNodes().each(function (i, n) {
		if (_this.utils.isLeaf(n)) {
			if (n.data("id") !== rootNode.data("id")) {
				nodesToDelete.push(n);
			}
		} else {
			// TODO: handle non-leaf expanded nodes differently, if at all
			nonLeafNodeIDs.push(n.data("id"));
		}
	});
	
	rootNode.connectedEdges("[?expanded]").each(function(i, edge) {
		var s = edge.data("source");
		var t = edge.data("target");
		if (s !== t /*&& nonLeafNodeIDs.indexOf(t) == -1 && nonLeafNodeIDs.indexOf(s) == -1*/) {
			edgesToDelete.push(edge);
		}
	});
	
	if (nodesToDelete.length > 0 || edgesToDelete.length > 0) {
		_this.deleteNodes(nodesToDelete, true);
		_this.deleteEdges(edgesToDelete, true);
		
		if (rootNode.connectedEdges().length <= 0) {
			_this.deleteNodes([rootNode], true);
		}
	} else {
		_this.utils.updateProgress("This node has no leaves (that can be deleted).", 1);
	}
};

/**
 *	Removes all graph elements from the display, but leaves the graph instance intact.
 *	Essentially wipes the slate.
 */
CytoscapeGraphVis.prototype.clear = function() {
	this.gv.elements().remove();
};

/**
 *	Called when the hosting div is resized.  Notifies cytoscape to recalculate its bounds
 *		@param w:int - (optional) the div's new width in pixels
 *		@param h:int - (optional) the div's new height in pixels
 */
CytoscapeGraphVis.prototype.resize = function(w, h) {
	if (this.initialized == true && typeof this.gv != "undefined") {
		this.gv.resize();
		this.gv.fit();
	}
};

/**
 *	Define the event handlers for user-interaction with this graph.
 */
CytoscapeGraphVis.prototype.setHandlers = function() {
	var _this = this;
	
	var _highlightElements = function(ele, dir, addCls) {
		var accessProp = (dir == "incoming") ? "source" : "target";
		var cssClass = (dir == "incoming") ? "child-of" : "parent-of";
		
		var node = _this.gv.nodes("node[id = '" + ele.data(accessProp) + "']")[0];
		if (!ele.selected()) {
			(addCls == true) ? ele.addClass(cssClass) : ele.removeClass(cssClass);
		}
		if (!node.selected()) {
			(addCls == true) ?  node.addClass(cssClass) : node.removeClass(cssClass);
		}
	};
	
	_this.gv.on("click", function(e) {
		var gg = _this.getGenerator();
		
		if (e.cyTarget === _this.gv) {

			_this.utils.updateProgress(" ", 1);
			
			var pos = e.cyPosition;
			// if you're trying to add a node and click white-space, add a node
			if (gg.stateMatches("ADD")) {
				gg.addNode(pos.x, pos.y);
				gg.clear();
				gg.givePrompt();
			}
			// if you're trying to connect a node but click white-space, reset the manager
			// to prevent unwanted interaction later on
			if (gg.stateMatches("CONNECT")) {
				gg.clear();
				gg.givePrompt();
			}
		}
	});
	
	_this.gv.on("click", "node", function(e) {
		var dm = _this.getDijkstraManager();
		var gg = _this.getGenerator();
		if (dm.isWaiting === true) {
			dm.setWait(false);
			dm.setDest(e.cyTarget);
			
			var results = dm.run(false);
			results.paths.edges().addClass("on-path");
			
			_this.utils.updateProgress("Shortest Path calculated; Number of hops is " + results.distance + ".", 1);
		} else if (gg.stateMatches("CONNECT")) {
			gg.connectNodes(e.cyTarget);
			gg.clear();
			gg.givePrompt();
		} else if (gg.stateMatches("ADD")) {
			// if you're trying to add a new node but click an existing one, reset the manager
			// to prevent unwanted interaction later on
			gg.clear();
			gg.givePrompt();
		} else {
			var node = e.cyTarget;
			var isShift = e.originalEvent.shiftKey;
			var isAlt = e.originalEvent.altKey;
			var isCtrl = e.originalEvent.ctrlKey;
			
			// if ctrl + shift, select all nodes of the clicked node's type
			if (isShift && isCtrl) {
				_this.gv.elements().unselect();
				_this.gv.nodes("[idType = '" + node.data("idType") + "']").select();
				_this.gv.nodes("[id = '" + node.data("id") + "']").unselect();
			}
			
			// if alt + shift, select all neighbors of the clicked node
			else if (isShift && isAlt) {
				_this.gv.elements().unselect();
				node.connectedEdges().connectedNodes().select();
				_this.gv.nodes("[id = '" + node.data("id") + "']").unselect();
			}
			
			// if shift key is NOT held down, deselect everything else before selecting the clicked node
			else if (!isShift) {
				_this.gv.elements().unselect();
				_this.gv.nodes("[id = '" + node.data("id") + "']").unselect();
			}
		}
	});
	
	_this.gv.on("select", "node", function(e) {
		var node = e.cyTarget;
		
		// cytoscape is funky in that it will still select hidden elements
		if (node.hidden()) {
			node.unselect();
			return;
		}
		
		if (typeof _this.owner.nodeClick == "function") {
			_this.owner.nodeClick(node);
		}
	});
	
	_this.gv.on("mouseover", "node", function(e) {
		var node = e.cyTarget;
		var id = node.data("id");
		
		_this.utils.$("edge[source = '" + id + "']", function(i, ele) {
			_highlightElements(ele, "outgoing", true);
		});
		
		_this.utils.$("edge[target = '" + id + "']", function(i, ele) {
			_highlightElements(ele, "incoming", true);
		});
	});
	
	_this.gv.on("mouseout", "node", function(e) {
		var node = e.cyTarget;
		var id = node.data("id");
		
		_this.utils.$("edge[source = '" + id + "']", function(i, ele) {
			_highlightElements(ele, "outgoing", false);
		});
		
		_this.utils.$("edge[target = '" + id + "']", function(i, ele) {
			_highlightElements(ele, "incoming", false);
		});
	});
	
	_this.gv.on("select", "edge", function(e) {
		var edge = e.cyTarget;
		
		// cytoscape is funky in that it will still select hidden elements
		if (edge.hidden()) {
			edge.unselect();
			return;
		}
		
		if (typeof _this.owner.edgeClick !== "undefined") {
			_this.owner.edgeClick(edge);
		}
	});
	
	//_this.gv.on("cxttapend", "edge", function(e) {
	//	var edge = e.cyTarget;
	//	if (typeof _this.owner.edgeRightClick !== "undefined") {
	//		_this.owner.edgeRightClick(edge);
	//	} 
	// });
	
};