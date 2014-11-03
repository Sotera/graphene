/**
 * The new Cytoscape graph wrapper class.
 * API for interfacing with cytoscape.js, graph layouts, and state management
 * 
 * Author: Andrew Weller
 * Date: 23/10/2014
 **/
function CytoGraphVis(inId) {

	this.id = inId;
	this.gv = null;
	this.initialized = false;
	this.currentLayout = null;
	this.owner = null;
	this.searchName = "";
	this.dispWidth = 1000;
	this.dispHeight = 800;
	
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
			//searchedNode: '#FF0000',
			defaultEdge:  '#23A4FF',
			selectedEdge: '#0B0B0B',
			expandedDefNode: '#F66CFB',
			dijkstraPath: "red",
			dijkstraSize: 5,
			//expandedDefEdge: '#BE26C4',
			
			fillColor: "rgba(0, 0, 200, 0.75)",
			activeFillColor: "rgba(92, 194, 237, 0.75)",
			
			defaultLayout: "breadthfirst",
			minLeafDistance: 60
		};
		return _legend[key];
	};
	
	var _layoutManager = new LayoutManager(this);
	var _stateManager = new StateManager(this);
	var _dijkstraManager = new DijkstraManager(this);
	
	this.getGv = function() { return this.gv; };
	this.getOwner = function() { return this.owner; };
	this.getCurrentLayout = function() { return this.currentLayout; };
	this.getSearchName = function() { return this.searchName; };
	
	this.getLayoutManager = function() { return _layoutManager; };
	this.getStateManager = function() { return _stateManager; };
	this.getDijkstraManager = function() { return _dijkstraManager; };
	
	// extend this' API with the layout manager's methods
	this.changeLayout = _layoutManager.changeLayout;
	this.registerLayout = _layoutManager.registerLayout;
	
	// extend this' API with the state manager's methods
	this.deleteNodes = _stateManager.deleteNodes;
	this.exportGraph = _stateManager.exportGraph;
	this.importGraph = _stateManager.importGraph;
	this.hideNode = _stateManager.hideNode;
	this.showNode = _stateManager.showNode;
	this.showEdge = _stateManager.showEdge;
	this.hideEdge = _stateManager.hideEdge;
};

/*
 *	Initialize this graph wrapper div with a cytoscape graph.
 *		config:Object - configuration parameters
 *		owner:Object - Reference to the parent display that houses this graph visualization (useful for scope)
 *		callbackFn:Function - (Optional) a callback function executed after cytoscape initializes the graph
 *		
 *		note: anything else passed to this function will be given to callbackFn as parameters
 */
CytoGraphVis.prototype.initGraph = function( /*config, owner[, callbackFn, ...args]*/ ) {
	var _this = this;
	
	var args = [].slice.apply(arguments);
	var config = args.shift();
	var owner = args.shift();
	var onLoadCallback = args.shift();
	// at this point, args is an array of whatever other arguments were passed to this function
	
	if (typeof config.width !== "undefined") _this.dispWidth = config.width;
	if (typeof config.height !== "undefined") _this.dispHeight = config.height;
	if (typeof config.rightBorder !== "undefined") _this.dispRightBorder = config.rightBorder;
	if (typeof config.leftBorder !== "undefined") _this.dispLeftBorder = config.leftBorder;
	if (typeof config.topBorder !== "undefined") _this.dispTopBorder = config.topBorder;
	if (typeof config.botBorder !== "undefined") _this.dispBotBorder = config.botBorder;
	
	_this.owner = owner;
	_this.expandedNodes = [];
	_this.onLoadCallback = onLoadCallback;
	_this.args = args;
	
	if (_this.gv !== null) return;
	
	cytoscape({
		container: document.getElementById(_this.id),
		ready: function() {
			overrideRegisterInstance();
			overrideCOSE();
			overrideARBOR();
			
			$("#" + _this.id).cytoscape({
				showOverlay: false,
				style: cytoscape.stylesheet()
					.selector("node").css({
						'content': 'data(name)',
						'text-valign': 'bottom',
						'color': _this.CONSTANTS("textColor"),
						'background-color': 'data(color)',
						'font-size': _this.CONSTANTS("fontSize"),
						'text-outline-width': 1,
						'text-outline-color': _this.CONSTANTS("textOutlineColor"),
						'width': _this.CONSTANTS("nodeSize"),
						'height': _this.CONSTANTS("nodeSize")
					})
					.selector("$node > node").css({
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
					})
					.selector("node:selected").css({
						'background-color': _this.CONSTANTS("selectedNode"),
						'line-color': 'black',
						'text-outline-color': _this.CONSTANTS("selectedTextColor"),
						'target-arrow-color': _this.CONSTANTS("selectedEdge"),
						'source-arrow-color': _this.CONSTANTS("selectedEdge"),
						'border-color': _this.CONSTANTS("lineColor"),
						//'shape': 'data(type)',  // added
						'font-size': _this.CONSTANTS("selectedFontSize"),
						'width': _this.CONSTANTS("selectedNodeSize"),  
						'height': _this.CONSTANTS("selectedNodeSize")
					})
					.selector("edge").css({
						'content':'data(label)',   
						'text-valign': 'center',
						'color': _this.CONSTANTS("textColor"),
						'text-outline-width': 1,
						'font-size': _this.CONSTANTS("fontSize"),
						'text-outline-color': _this.CONSTANTS("textOutlineColor"),
						'line-color': _this.CONSTANTS("defaultEdge"), 
						'target-arrow-color': _this.CONSTANTS("defaultEdge"),
						'target-arrow-shape': 'triangle',
						'width': 'data(count)'
					})
					.selector("edge:selected").css({
						'background-color': _this.CONSTANTS("selectedEdge"),
						'line-color': _this.CONSTANTS("lineColor"),
						'text-outline-color': _this.CONSTANTS("selectedTextColor"),
						'target-arrow-color': _this.CONSTANTS("lineColor"),
						'source-arrow-color': _this.CONSTANTS("lineColor"),
						'border-color': _this.CONSTANTS("lineColor"),
						'font-size': _this.CONSTANTS("fontSize"),
						//'width': 'data(lineWidth)'
					})
					.selector(".toggled-show").css({
						'content': "data(label)"
					})
					.selector(".toggled-hide").css({
						'content': " "
					})
					.selector(".on-path").css({
						'line-color': _this.CONSTANTS("dijkstraPath"),
						'target-arrow-color': _this.CONSTANTS("dijkstraPath"),
						'width': _this.CONSTANTS("dijkstraSize")
					})
			});
			
			// declare a reference to the completed cytoscape graph object as this.gv
			_this.gv = $("#" + _this.id).cytoscape("get");
			
			// if the plug-in is not included for any reason, do not try to initialize it
			try {
				if (typeof _this.gv.cxtmenu !== "function")
					throw "You must include 'cytoscape.js-cxtmenu.js' in the .html file";
				
				_this.gv.cxtmenu({
					menuRadius: 75, selector: 'node', activePadding: 0,
					fillColor: _this.CONSTANTS("fillColor"), 
					activeFillColor: _this.CONSTANTS("activeFillColor"), 
					commands: [{
						content: "Expand",
						select: function() {
							var node = this;
							if (_this.owner.expand) { _this.owner.expand(node); } 
							else { console.log("expand() is undefined"); }
						}
					}, {
						content: "Pivot",
						select: function() {
							var node = this;
							if (_this.owner.pivot) { _this.owner.pivot(node); }
							else { console.log("pivot() is undefined"); }
						}
					}, {
						content: "Hide",
						select: function() {
							var node = this;
							if (_this.owner.hideNode) { _this.owner.hideNode(node); } 
							else { console.log("hideNode() is undefined"); }
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
					}],
				});
			} catch(e) {
				console.log(e);
				console.log("Context Menu on cytoscape graph " + _this.id + " will be unavailable.");
			}
			
			// if a callback function was provided, pass it any remaining parameters and call it
			if (_this.onLoadCallback && typeof _this.onLoadCallback == "function") {
				_this.onLoadCallback.apply(_this.owner, _this.args);
			}
			
			_this.initialized = true;
			_this.setHandlers();
			_this.changeLayout(_this.CONSTANTS("defaultLayout"), {});
		}
	});
};

/*
 *	Define the event handlers for user-interaction with this graph.
 */
CytoGraphVis.prototype.setHandlers = function() {
	var _this = this;
	//var timoutFn = null;
	
	this.gv.on("click", "node", function(e) {
		var dm = _this.getDijkstraManager();
		if (dm.isWaiting === true) {
			dm.setWait(false);
			dm.setDest(e.cyTarget);
			var results = dm.run();
			
			results.paths.edges().addClass("on-path");
			
			if (_this.owner.getProgressBar) {
				var pb = _this.owner.getProgressBar();
				if (pb) pb.updateProgress(1, "Shortest Path calculated; Number of hops is " + results.distance + ".");
			}
		} else {
			e.cyTarget.select();
		}
	});
	
	this.gv.on("select", "node", function(e) {
		var node = e.cyTarget;
		if (typeof _this.owner.nodeClick !== "undefined") {
			_this.owner.nodeClick(node);
		}
	});
	
	this.gv.on("select", "edge", function(e) {
		var edge = e.cyTarget;
		if (typeof _this.owner.edgeClick !== "undefined") {
			_this.owner.edgeClick(edge);
		}
	});
	
	this.gv.on("cxttapend", "edge", function(e) {
		var edge = e.cyTarget;
		if (typeof _this.owner.edgeRightClick !== "undefined") {
			_this.owner.edgeRightClick(edge);
		} 
	});
	/*
	this.gv.on('mouseover', 'node', function(e) {
		var x = e.originalEvent.layerX;
		var y = e.originalEvent.layerY;
		var distFromTop = e.originalEvent.clientY - e.originalEvent.layerY;
		var node = e.cyTarget;   // the current selected node
		
		timeoutFn = setTimeout(function() {
			if (!(_this.owner.nodeMouseOver == undefined)) {
				_this.owner.nodeMouseOver(node);
				
				// if a popup window cannot be (re)created, _this.owner.mouseOverPopUp will be undefined
				if (!(_this.owner.mouseOverPopUp == undefined)) {
					
					// prevent y coordinate from being out-of-bounds
					y = y - _this.owner.mouseOverPopUp.height;
					if (y < 0) 
						y = 0;
					y += distFromTop;
					
					_this.owner.mouseOverPopUp.showAt(x, y);
				}
			}
		}, 1000); // hover for one second
		
	});

	this.gv.on('mouseout', 'node', function(e) {
		clearTimeout(timeoutFn);
		if (_this.owner.mouseOverPopUp)
			_this.owner.mouseOverPopUp.hide();
	});
	*/
};

CytoGraphVis.prototype.reset = function() {
	this.setHandlers();
	this.changeLayout(this.CONSTANTS("defaultLayout"), {});
};

/*
 *	Show all elements on this graph and toggle their class appropriately
 */
CytoGraphVis.prototype.showAll = function() {
	this.gv.elements().show();
	this.gv.elements().removeClass("toggled-hide");
	this.gv.elements().addClass("toggled-show");
};

/*
 *	Load the following json into cytoscape for processing.
 *		json:Object - json representation of the graph
 *		name:String - identifier of the root node/entity 
 */
CytoGraphVis.prototype.showGraph = function(json, name) {
	this.searchName = name;
	this.gv.load(json);
};

CytoGraphVis.prototype.resize = function(w, h) {
	if (this.initialized && typeof this.gv != "undefined") {
		this.gv.fit();
	}
	this.dispWidth = w;
	this.dispHeight = h;
};

CytoGraphVis.prototype.clear = function() {
	this.gv.elements().remove();
};

/*
 * 	Given a sub-graph and a central node, place the expanded nodes in an orbit
 *	around the "Origin Node".  Note: this method does not determine what the expanded,
 *	or 1-hop, graph will be.
 *		json:JsonObject - JSON graph representation of the nodes/edges found in 1-hop
 *		innode:Cytoscape Node - "Origin Node" to extend from the graph and show its 1-hop neighbors 
 */
CytoGraphVis.prototype.showGraph1Hop = function(json, innode) {
	var pos = innode.position();
	try {
		var connectedNodes = innode.connectedEdges().connectedNodes();
		var neighbor = null;
		connectedNodes.each(function(i, n) {
			if (n.data().id != innode.data().id) {
				neighbor = n;
				return;
			}
		});
		var n_pos = neighbor.position();
		var dx = pos.x - n_pos.x;
		var dy = pos.y - n_pos.y;
		var newX = pos.x + dx * 1.5;
		var newY = pos.y + dy * 1.5;
		innode.position({x: newX, y: newY});
		pos = innode.position();
	} catch(e) {
		console.log(e.message);
	}
	
	var nodes = json.nodes;
	var l = nodes.length;
	for (var i = 0; i < l; i++) {
		var node = nodes[i];
		var rad = 2 * Math.PI * i / l;
		var radius = this.CONSTANTS("minLeafDistance") + l + l;
		
		if (node.data.id != innode.data().id) {
			node.data.color = this.CONSTANTS("expandedDefNode");
			node.data.expanded = true;
			node.position = {
				x: pos.x + (radius * Math.cos(rad)),
				y: pos.y + (radius * Math.sin(rad))
			};
		}
	}
	
	// prevent any duplicate edges from being added to the graph
	var edges = json.edges;
	for (i = 0; i < edges.length; i++) {
		var edge = edges[i];
		
		var a = edge.data.amount;
		var l = edge.data.label;
		var s = edge.data.source;
		var t = edge.data.target;
		
		var amountCondition = (typeof a == "string" && a.length > 0) ? "[amount = '"+a+"']" : "";
		var labelCondition = (typeof l == "string" && l.lenght > 0) ? "[label = '"+l+"']" : "";
		
		// can't use IDs since they are not present in the JSON.  Only available after this.gv.add(json), which is too late.
		// (this.source == that.source && this.target == that.target && this.amount == that.amount && this.label == that.label) is 
		// the best match condition so far.
		var matchedEdges = this.gv.$("edge[source = '"+s+"'][target = '"+t+"']" + amountCondition + labelCondition);
		
		// if edge already exists on the graph, splice it out of the json before it's loaded
		if (matchedEdges.length > 0) {
			edges.splice(i, 1);
			i--;
			
			matchedEdges.each(function(index, e){
				console.log("Pruned edge with id='" + e.data("id") + "'");
			});
		}
	}
	
	this.gv.add(json);
	
	var retJson = {};
	retJson.nodes = this.gv.nodes().jsons();
	retJson.edges = this.gv.edges().jsons();
	return retJson;
};

/*
 * 	Delete all leaf nodes from the given node, essentially undoing a 1-hop expansion.
 *  Note: any edges that were created to existing nodes will remain
 *  	innode:Cytoscape Node - "Origin Node" from which to retract all leaf nodes
 */
CytoGraphVis.prototype.unexpand1Hop = function(innode) {
	var nodesToDelete = [];
	
	// TODO: make a public function somewhere
	var _isLeaf = function(node) {
		var possibleNeighbors = node.connectedEdges().connectedNodes();
		var confirmedNeighbors = [];
		possibleNeighbors.each(function(index, n) {
			if (n.data().id != node.data().id) {
				confirmedNeighbors.push(n.data().id);
			}
		});
		return confirmedNeighbors.length == 1;
	};
	
	var neighbors = innode.connectedEdges().connectedNodes();
	neighbors.each(function(i, n) {
		if (_isLeaf(n) && n.data().id !== innode.data().id) {
			nodesToDelete.push(n);
		}
	});
	if (nodesToDelete.length > 0) {
		this.deleteNodes(nodesToDelete);
	} else {
		alert("This node has no leaves.");
	}
};

// LayoutManager constructor
function LayoutManager(graphRef) {
	var _this = this;
	
	var _registeredLayouts = {};
	
	this.getRegisteredLayouts = function() {
		var arr = [];
		
		for (var key in _registeredLayouts) {
			if (!_registeredLayouts.hasOwnProperty(key)) continue;
			
			var layout = _registeredLayouts[key];
			var options = {};
			
			for (var prop in layout) {
				if (!layout.hasOwnProperty(prop)) continue;
				var val = layout[prop];
				if (typeof val !== "function") {
					options[prop] = val;
				}
			}
			
			arr.push({
				layoutName: key,
				config: options
			});
		}
		
		return arr;
	};
	
	/*
	 * private function to merge the properties of the two parameters, with optsToMerge having overwrite priority.
	 *		baseOpts:Object - default configuration object
	 *		optsToMerge:Object - overrides for the default configuration
	 * 		returns mergedOptions:Object - combination of baseOpts + optsToMerge
	 */
	var _mergeOptions = function(baseOpts, optsToMerge) {
		var mergedOptions = {};
		
		for (var key in baseOpts) {
			if (baseOpts.hasOwnProperty(key)) {
				var value = baseOpts[key];
				mergedOptions[key] = value;
			}
		}
		
		for (var key in optsToMerge) {
			if (optsToMerge.hasOwnProperty(key)) {
				var value = optsToMerge[key];
				mergedOptions[key] = value;
			}
		}
		return mergedOptions;
	};
	
	/*
	 *	Changes the current graph layout if layoutName matches a registered layout.
	 *		layoutName:String - key to access the registered layouts
	 *		config:Object - (optional) overrides for the layout's configuration
 	 */
	this.changeLayout = function(layoutName, config) {
		if (graphRef.initialized == false) return;
		
		if (_registeredLayouts.hasOwnProperty(layoutName)) {
			var options = _registeredLayouts[layoutName];
			options = _mergeOptions(options, config);
			graphRef.currentLayout = options.name;
			
			if (typeof options.beforeLayout == "function") {
				options.beforeLayout();
			}
			
			graphRef.gv.layout(options);
		}
	};
	
	/*
	 *	Add a new layout option to the LayoutManager.
	 *		layoutName:String - key to access the registered layouts
	 *		config:Object - configuration parameters for this new layout
	 *		stopFn:Function - (optional) callback on layoutstop event
	 *		progressFn:Function - (optional) callback for layout's procedural status, if applicable
	 *		startFn:Function - (optional) callback on layoutready event
	 */
	this.registerLayout = function(layoutName, scope, config, stopFn, progressFn, startFn) {
	
		if (typeof layoutName == "undefined" || typeof config == "undefined") return;
		
		_registeredLayouts[layoutName] = config;
		
		if (typeof stopFn == "function") {
			// if stopFn was provided, assign it to this layout options using the function's name as a key
			_registeredLayouts[layoutName][stopFn.name] = function() { stopFn.apply(scope, arguments); };
		}
		if (typeof progressFn == "function") {
			// if progressFn was provided, assign it to this layout options using the function's name as a key
			_registeredLayouts[layoutName][progressFn.name] = function() { progressFn.apply(scope, arguments); };
		}
		if (typeof startFn == "function") {
			// if startFn was provided, assign it to this layout options using "beforeLayout" as a key (so it can be referenced with a default)
			_registeredLayouts[layoutName]["beforeLayout"] = function() { startFn.apply(scope, arguments); };
		}
	};
	
	/*
	 *	For each Origin Node passed to this method, its leaf nodes will be repositioned to orbit around
	 *	the Origin Node.  Neighbors to the Origin Node who have neighbors of their own will be untouched.
	 *		nodelist:Object - Contains one or more "origin nodes" and an array of each one's leaves
	 *			nodelist resembles {
	 *				"origin_id_1" : {
	 *					node: Cytoscape Node Object of the origin node,
	 *					leaves: [ Cytoscape Node Objects of the leaves ]
	 *				},
	 *				...
	 *			}
	 */
	this.repositionClusters = function(nodelist) {
        var minDist = graphRef.CONSTANTS("minLeafDistance");
		for (var key in nodelist) {
			var coreNode = nodelist[key].node;
			var leaves = nodelist[key].leaves;
			
			var corePos = coreNode.position();
			var l = leaves.length;
			var rad = 0;
			var r = minDist + l + l;
			
			for (var i = 0; i < l; i++) {
				var leaf = leaves[i];
				rad = 2 * Math.PI * i / l;
				var newX = corePos.x + (r * Math.cos(rad));
				var newY = corePos.y + (r * Math.sin(rad));
				
				leaf.position({x: newX, y: newY});
			}
		}
    };
	
	// ========================================= REGISTER DEFAULT LAYOUTS ========================================= /
	
	this.registerLayout("grid", graphRef, { 
			name: "grid", fit: true, rows: undefined, column: undefined 
		},
		function stop() {
			if (graphRef.owner.getProgressBar) {
				var pb = graphRef.owner.getProgressBar();
				if (pb) pb.updateProgress(1, "100%");
			}
		}
	); // END GRID
	
	this.registerLayout("circle", graphRef, {
			name: "circle", fit: true, rStepSize: 10, padding: 30, counterClockwise: false
		},
		function stop() {
			if (graphRef.owner.getProgressBar) {
				var pb = graphRef.owner.getProgressBar();
				if (pb) pb.updateProgress(1, "100%");
			}
		}
	); // END CIRCLE
	
	this.registerLayout("breadthfirst", graphRef, {
			name: "breadthfirst", fit: true, directed: true, padding: 15, circle: false, roots: undefined
		},
		function stop() {
			if (graphRef.owner.getProgressBar) {
				var pb = graphRef.owner.getProgressBar();
				if (pb) pb.updateProgress(1, "100%");
			}
		}
	); // END BREADTHFIRST
	
	this.registerLayout("cose", graphRef, {
			name: "cose", refresh: 10, fit: true, randomize: true, debug: false, nodeRepulsion: 99999999,
			nodeOverlap: 5000, idealEdgeLength: 10, edgeElasticity: 1, nestingFactor: 5, gravity: 250,
			numIter: 5000, initialTemp: 500, coolingFactor: 0.95, minTemp: 8,
			feedbackRate: 5, intervalRate: 1
		},
		function stop() {
			if (graphRef.owner.getStopButton) {
				var stopBtn = graphRef.owner.getStopButton();
				stopBtn.setDisabled(true);
				stopBtn.setCurrentLayout(undefined);
			}

			if (graphRef.owner.getProgressBar) {
				var pb = graphRef.owner.getProgressBar();
				if (pb) pb.updateProgress(1, "100%");
			}
		},
		function onFeedback(opt) {
			if (graphRef.owner.getProgressBar) {
				var pb = graphRef.owner.getProgressBar();
				if (pb) {
					var progress = _this.minTemp / opt.temp;
					pb.updateProgress(progress, Math.floor(progress * 100) + "%");
				}
			}
		},
		function ready() {
			_this.minTemp = _registeredLayouts["cose"].minTemp;
			
			if (graphRef.owner.getStopButton) {
				var stopBtn = graphRef.owner.getStopButton();
				stopBtn.setDisabled(false);
				stopBtn.setCurrentLayout("COSE");
			}
		}
	); // END COSE
	
	this.registerLayout("arbor-snow", graphRef, {
			name: "arbor", liveUpdate: true, maxSimulationTime: 90000, fit: true, padding: [50, 10, 50, 10],
			ungrabifyWhileSimulating: false, repulsion: 8000, stiffness: 300, friction: undefined,
			gravity: true, fps: 240, precision: 0.2, nodeMass: undefined, edgeLength: 3, stepSize: 1
		},
		function stop() {
			_this.progress = 0;
		
			if (graphRef.owner.getStopButton) {
				var stopBtn = graphRef.owner.getStopButton();
				stopBtn.setDisabled(true);
				stopBtn.setCurrentLayout(undefined);
			}
			
			if (graphRef.owner.getProgressBar) {
				var pb = graphRef.owner.getProgressBar();
				if (pb) pb.updateProgress(1, "100%");
			}
		},
		function stableEnergy(en) {
			var max_progress = 0.3 / en.max;
			var mean_progress = 0.2 / en.mean;
			var avg_progress = (max_progress + mean_progress) / 2;
			
			if (avg_progress < 1 && avg_progress > _this.progress) {
				_this.progress = avg_progress;
			}
			
			if (graphRef.owner.getProgressBar) {
				var pb = graphRef.owner.getProgressBar();
				if (pb) pb.updateProgress(_this.progress, Math.floor(_this.progress * 100) + "%");
			}
			return (en.max <= 0.3) || (en.mean <= 0.2);
		},
		function ready() {
			_this.progress = 0;
			
			if (graphRef.owner.getStopButton) {
				var stopBtn = graphRef.owner.getStopButton();
				stopBtn.setDisabled(false);
				stopBtn.setCurrentLayout("ARBOR");
			}
		}
	); // END ARBOR-SNOW
	
	this.registerLayout("arbor-wheel", graphRef, {
			name: "arbor", liveUpdate: true, maxSimulationTime: 90000, fit: true, padding: [50, 10, 50, 10],
			ungrabifyWhileSimulating: false, repulsion: 8000, stiffness: 300, friction: undefined,
			gravity: true, fps: 240, precision: 0.2, nodeMass: undefined, edgeLength: 3, stepSize: 1
		},
		function stop() {
			_this.progress = 0;
		
			if (graphRef.owner.getStopButton) {
				var stopBtn = graphRef.owner.getStopButton();
				stopBtn.setDisabled(true);
				stopBtn.setCurrentLayout(undefined);
			}
			
			var anodes = graphRef.gv.nodes();
			var originNodes = {};

			// TODO: make a public function somewhere
			var _isLeaf = function(node) {
				var possibleNeighbors = node.connectedEdges().connectedNodes();
				var confirmedNeighbors = [];
				possibleNeighbors.each(function(index, n) {
					if (n.data().id != node.data().id) {
						confirmedNeighbors.push(n.data().id);
					}
				});
				return confirmedNeighbors.length == 1;
			};
			
			// iterate over all nodes.  if a leaf is found, designate its single neighbor to be
			// an "origin node".  Origin Nodes have an array of all their leaves. 
			anodes.each(function(indx, node) {
				if (_isLeaf(node)) {
					var neighbors = node.connectedEdges().connectedNodes();
					neighbors.each(function(index, n) {
						var neighborId = n.data().id;
						if (neighborId != node.data().id) {
							//node.data().color = "purple";
							if (originNodes.hasOwnProperty(neighborId)) {
								originNodes[neighborId].leaves.push(node);
							} else {
								originNodes[neighborId] = {
									node: n,
									leaves: [node]
								};
							}
						}
					});
				}
			});

			// For each origin node, reposition its leaves in its orbit
			_this.repositionClusters(originNodes);
			
			if (graphRef.owner.getProgressBar) {
				var pb = graphRef.owner.getProgressBar();
				if (pb) pb.updateProgress(1, "100%");
			}
		},
		function stableEnergy(en) {
			var max_progress = 0.3 / en.max;
			var mean_progress = 0.2 / en.mean;
			var avg_progress = (max_progress + mean_progress) / 2;
			
			if (avg_progress < 1 && avg_progress > _this.progress) {
				_this.progress = avg_progress;
			}
			
			if (graphRef.owner.getProgressBar) {
				var pb = graphRef.owner.getProgressBar();
				if (pb) pb.updateProgress(_this.progress, Math.floor(_this.progress * 100) + "%");
			}
			return (en.max <= 0.3) || (en.mean <= 0.2);
		},
		function ready() {
			_this.progress = 0;
			
			if (graphRef.owner.getStopButton) {
				var stopBtn = graphRef.owner.getStopButton();
				stopBtn.setDisabled(false);
				stopBtn.setCurrentLayout("ARBOR");
			}
		}
	); // END ARBOR-WHEEL
}

// StateManager constructor
function StateManager(graphRef) {
	var _this = this;
	
	/*
	 *	Gather the metadata and graph JSON for this cytoscape graph and
	 *	return it.
	 *		returns json:Object
	 */
	this.exportGraph = function() {
		var json = {};
		json.timestamp = new Date().getTime();
		json.searchName = graphRef.getSearchName();
		json.currentLayout = graphRef.getCurrentLayout();
		
		var outNodes = graphRef.gv.nodes().jsons();
		var outEdges = graphRef.gv.edges().jsons();
		
		json.graph = {};
		json.graph.nodes = outNodes;
		json.graph.edges = outEdges;
		
		return json;
	};
	
	/*
	 *	Loads a pre-defined cytoscape graph's JSON object, preserving the elements'
	 *	visibility and positions on the X/Y plane.
	 *		json:Object - graph JSON to load into cytoscape.
	 */
	this.importGraph = function(json) {
		if (json && json.graph && json.graph.nodes && json.graph.nodes.length > 0) {
			graphRef.clear();
			graphRef.searchName = json.searchName;
			graphRef.gv.add(json.graph);
			
			graphRef.gv.nodes().each(function(i, n) {
				if (n.data().visible == false) {
					n.hide();
				}
			});
			
			graphRef.gv.edges().each(function(i, e) {
				if (e.data().visible == false) {
					e.hide();
				}
			});
		} else {
			console.error("The graph json for import is empty or invalid");
		}
	};
	
	/*
	 *	Remove (not hide) nodes from the cytosape graph AND the json representation
	 *	stored by most parent containers of a graph.
	 *		nodes:Array - cytosape nodes to be deleted
	 */
	this.deleteNodes = function(nodes) {
		for (var i = 0; i < nodes.length; i++) {
			var id = nodes[i].data().id;
			
			// remove all edges whose source == id
			graphRef.gv.remove( graphRef.gv.elements("edge[source='" + id + "']") );
			
			// remove all edges whose target == id
			graphRef.gv.remove( graphRef.gv.elements("edge[target='" + id + "']") );
			
			// remove all nodes whose id == id
			graphRef.gv.remove("#" + id);

			// most graph implementations store a json representation of their cyto graphs;
			// remove traces of nodes and edges involved with selected node IDs
			if (typeof graphRef.owner.json !== "undefined") {
				var j;
				var json = graphRef.owner.json;
				
				for (j = 0; j < json.nodes.length; j++) {
					var jsonNode = graphRef.owner.json.nodes[j];
					if (jsonNode.data.id == id) {
						json.nodes.splice(j, 1);
						break;  // there should only be one node with this id; stop iterating
					}
				}
				
				for (j = 0; j < json.edges.length; j++) {
					var jsonEdge = graphRef.owner.json.edges[j];
					if (jsonEdge.data.source == id || jsonEdge.data.target == id) {
						json.edges.splice(j, 1);
						j--;
					}
				}
			}
		}
	};
	
	this.showNode = function(node) {
		if (node.hidden()) {
			node.show();
			node.data().visible = true;
			var edges = node.connectedEdges();
			if (edges) {
				edges.each(function(i, e) {
					_this.showEdge(e);
				});
			}
		}
	};
	
	this.hideNode = function(node) { 
		if (node.visible()) {
			var edges = node.connectedEdges();
			if (edges) {
				edges.each(function(i, e) {
					_this.hideEdge(e);
				});
			}
			node.hide();
			node.data().visible = false;
		}
	};
	
	this.showEdge = function(edge) {
		if (edge.hidden()) {
			edge.show();
			edge.data().visible = true;
			edge.removeClass("toggled-hide");
			edge.addClass("toggled-show");
		}
	};
	
	this.hideEdge = function(edge) {
		if (edge.visible()) {
			edge.hide();
			edge.data().visible = false;
			edge.removeClass("toggled-show");
			edge.addClass("toggled-hide");
		}
	};
}
 
function DijkstraManager(graphRef) {
	var _this = this;
	
	this.root = null;		// root node from which the paths will originate
	this.dest = null;		// the terminal or destination node
	this.isWaiting = false;	// whether this manager is waiting for destination node or not
	
	this.setRoot = function(node) { 
		if (typeof node.isNode == "function" && node.isNode() == true) {
			_this.root = node;
			_this.setWait(true);
			
			if (graphRef.owner.getProgressBar) {
				var pb = graphRef.owner.getProgressBar();
				if (pb) pb.updateProgress(0, "Root node " + /*(id='" + node.data("id") + "') */ "selected.  Left-click another node to show the shortest path between them.");
			}
		}
	};
	
	this.setDest = function(node) {
		if (typeof node.isNode == "function" && node.isNode() == true) {
			_this.dest = node;
			
			if (graphRef.owner.getProgressBar) {
				var pb = graphRef.owner.getProgressBar();
				if (pb) pb.updateProgress(1, "100%");
			}
		}
	};
	
	this.setWait = function(bool) {
		_this.isWaiting = (bool === true) ? true : false;
	};
	
	this.run = function(isDirected) {
		var eles = graphRef.gv.elements();
		
		var result = eles.dijkstra(
			_this.root, // root node
			// optional function that returns edge weight
			function(edge) { 
				var a = edge.data("weight"); 
				//console.log("Dijkstra: weight is " + a); 
				return a;
			},
			false		// only follow directed edges, yes/no
		);
		
		return {
			paths: result.pathTo(_this.dest),
			distance: result.distanceTo(_this.dest)
		};
	};
	
	this.clear = function() {
		_this.root = null;
		_this.dest = null;
		_this.isWaiting = false;
		graphRef.gv.elements().removeClass("on-path");
	};
}