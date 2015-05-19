function LayoutManager(graphRef) {
	var _this = this;
	
	var _registeredLayouts = {};
	
	/**
	 *	Returns an array of the currently registered layouts whose names match any of the
	 *	key strings passed as this function's array parameter.
	 *		@param keysArr:Array - (optional) array containing layout identifier strings.  Only layouts
	 *						whose keys match any of these identifiers will be returned.
	 *		@returns Array - array of objects, where each object has a layoutName property and
	 *						config options for that layout.
	 */
	this.getRegisteredLayouts = function(keysArr) {
		
		var arr = [];
		var returnAll = true;
		if (typeof keysArr == "undefined") { keysArr = []; }
		if (typeof keysArr == "string") { keysArr = [keysArr]; }
		if (typeof keysArr.length !== "undefined") { returnAll = keysArr.length <= 0; }
		
		for (var key in _registeredLayouts) {
			if (!_registeredLayouts.hasOwnProperty(key)) continue;
			if (!returnAll && keysArr.indexOf(key) == -1) continue;
			
			var layout = _registeredLayouts[key];
			var options = {};
			
			for (var prop in layout) {
				if (!layout.hasOwnProperty(prop)) continue;
				var val = layout[prop];
				options[prop] = val;
			}
			
			arr.push({
				layoutName: key,
				config: options
			});
		}
		
		return arr;
	};
	
	/**
	 * private function to merge the properties of the two parameters, with optsToMerge having overwrite priority.
	 *		@param baseOpts:Object - default configuration object
	 *		@param optsToMerge:Object - overrides for the default configuration
	 *		@returns mergedOptions:Object - combination of baseOpts + optsToMerge
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
	
	/**
	 *	Changes the current graph layout if layoutName matches a registered layout.
	 *		@param layoutName:String - key to access the registered layouts
	 *		@param config:Object - (optional) overrides for the layout's configuration
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
	
	/**
	 *	Add a new layout option to the LayoutManager.
	 *		@param layoutName:String - key to access the registered layouts
	 *		@param config:Object - configuration parameters for this new layout
	 *		@param stopFn:Function - (optional) callback on layoutstop event
	 *		@param progressFn:Function - (optional) callback for layout's procedural status, if applicable
	 *		@param startFn:Function - (optional) callback on layoutready event
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
	
	/**
	 *	Deletes the registered layout whose key matches layoutName.
	 *		@param layoutName:String - identifier key for the registered layout to be deleted.
	 */
	this.unregisterLayout = function(layoutName) {
		if (typeof layoutName == "undefined" || typeof layoutName !== "string") return;
		if (_registeredLayouts.hasOwnProperty(layoutName)) {
			delete _registeredLayouts[layoutName];
		}
	};
	
	/**
	 *	For each Origin Node passed to this method, its leaf nodes will be repositioned to orbit around
	 *	the Origin Node.  Neighbors to the Origin Node who have neighbors of their own will be untouched.
	 *		@param nodelist:Object - Contains one or more "origin nodes" and an array of each one's leaves
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
	
	this.registerLayout("preset", graphRef, {
			name: "preset", fit: true, animate: false
		},
		function stop() {
			// graphRef.utils.updateProgress("100%", 1);
		}
	);
	
	this.registerLayout("grid", graphRef, { 
			name: "grid", fit: true, rows: undefined, column: undefined 
		},
		function stop() {
			// graphRef.utils.updateProgress("100%", 1);
		}
	); // END GRID
	
	this.registerLayout("circle", graphRef, {
			name: "circle", fit: true, rStepSize: 10, padding: 30, counterClockwise: false
		},
		function stop() {
			// graphRef.utils.updateProgress("100%", 1);
		}
	); // END CIRCLE
	
	this.registerLayout("breadthfirst", graphRef, {
			name: "breadthfirst", fit: true, directed: true, padding: 15, circle: false, roots: undefined,
			maximalAdjustments: 20
		},
		function stop() {
			// graphRef.utils.updateProgress("100%", 1);
		}
	); // END BREADTHFIRST
	
	this.registerLayout("cose", graphRef, {
			name: "cose", refresh: 10, fit: true, randomize: true, debug: false, nodeRepulsion: 999999,
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

			graphRef.utils.updateProgress("100%", 1);
		},
		function onFeedback(opt) {
			var progress = _this.minTemp / opt.temp;
			graphRef.utils.updateProgress(Math.floor(progress * 100) + "%", progress);
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
			ungrabifyWhileSimulating: true, repulsion: 8000, stiffness: 300, friction: undefined,
			gravity: true, fps: 240, precision: 0.2, nodeMass: undefined, edgeLength: 3, stepSize: 1
		},
		function stop() {
			_this.progress = 0;
		
			if (graphRef.owner.getStopButton) {
				var stopBtn = graphRef.owner.getStopButton();
				stopBtn.setDisabled(true);
				stopBtn.setCurrentLayout(undefined);
			}
			graphRef.utils.updateProgress("100%", 1);
		},
		function stableEnergy(en) {
			var max_progress = 0.3 / en.max;
			var mean_progress = 0.2 / en.mean;
			var avg_progress = (max_progress + mean_progress) / 2;
			
			if (avg_progress < 1 && avg_progress > _this.progress) {
				_this.progress = avg_progress;
			}
			
			graphRef.utils.updateProgress(Math.floor(_this.progress * 100) + "%", _this.progress);
			
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
			ungrabifyWhileSimulating: true, repulsion: 8000, stiffness: 300, friction: undefined,
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
			
			// iterate over all nodes.  if a leaf is found, designate its single neighbor to be
			// an "origin node".  Origin Nodes have an array of all their leaves. 
			anodes.each(function(indx, node) {
				if ( graphRef.utils.isLeaf(node) ) {
					var neighbors = node.connectedEdges().connectedNodes();
					neighbors.each(function(index, n) {
						var neighborId = n.data().id;
						if (neighborId != node.data().id) {
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
			
			graphRef.utils.updateProgress("100%", 1);
		},
		function stableEnergy(en) {
			var max_progress = 0.3 / en.max;
			var mean_progress = 0.2 / en.mean;
			var avg_progress = (max_progress + mean_progress) / 2;
			
			if (avg_progress < 1 && avg_progress > _this.progress) {
				_this.progress = avg_progress;
			}
			
			graphRef.utils.updateProgress(Math.floor(_this.progress * 100) + "%", _this.progress);
			
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
	
	this.registerLayout("fabric", graphRef, {
			name: 'fabric'
		},
		function stop() {
			// graphRef.utils.updateProgress("100%", 1);
		}
	);
}