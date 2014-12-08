Ext.define("DARPA.AbstractGraphPanel", {
	extend: "Ext.panel.Panel",
	border: 1,
	title: "Graph",
	titleAlign: "center",
	layout: {
		type: "hbox",
		align: "stretch"
	},
	resizable: true,
	collapsible: false,
	width: "auto",
	
	graphType: "default",
	GraphVis: null,
	graphStore: null,
	institution: "",
	
	json: null,
	json1Hop: null,
	
	serviceURL: "",
	previousPivotIds: [],
	
	prevLoadParams: {
		searchValue: null,
		value: null,
		prevValue: null
	},
	
	listeners: {
		resize: function(tab, width, height, oldWidth, oldHeight) {
			if (typeof tab.GraphVis !== "undefined") {
				tab.GraphVis.resize(width, height);
			}
		}
	},
	
	constructor: function(config) {
		var self = this;
		
		this.institution = config.institution;
		
		//this.GraphVis = null; // TODO: implement in extending class
		Ext.define("DARPA.JsonStoreDataModel", {
			extend: "Ext.data.Model"
		});
		
		this.graphStore = Ext.create("Ext.data.JsonStore", {
			proxy: {
				type: 'ajax',
				model: "DARPA.JsonStoreDataModel",
				timeout: 120000,
				url: "",
				reader: {
					type: 'json'
				}
			}
		});
		
		config.tbar = Ext.create("DARPA.GraphToolbar", {
			id: config.id + "-GraphToolbar",
			institution: config.institution
		});
		
		config.bbar = Ext.create("Ext.ProgressBar", {
			text: "Ready",
			id: config.id + "-ProgressBar",
			height: 20,
			width: 200
		});
		
		this.callParent(arguments);
	}, 
	
	getNodeDisplay : function() {
		return undefined;
	},
	
	getSettings : function() {
		return undefined;
	},
	
	getProgressBar: function() {
		return Ext.getCmp(this.id + "-ProgressBar");
	},
	
	getPivotButton : function() {
		return this.getNodeDisplay().items.items[1].getPivotButton();
	},

	getUnPivotButton : function() {
		return this.getNodeDisplay().items.items[1].getUnPivotButton();
	},

	getHideButton : function() {
		return this.getNodeDisplay().items.items[1].getHideButton();
	},

	getUnHideButton : function() {
		return this.getNodeDisplay().items.items[1].getUnHideButton();
	},
	getExpandButton : function() {
		return this.getNodeDisplay().items.items[1].getExpandButton();
	},
	getUnExpandButton : function() {
		return this.getNodeDisplay().items.items[1].getUnExpandButton();
	},
	getShowButton : function() {
		return this.getNodeDisplay().items.items[1].getShowButton();
	},
	getStopButton : function() {
		return this.getNodeDisplay().items.items[1].getStopButton();
	},

	setStatus: function(msg, progress) {
		var prog = (typeof progress == "undefined") ? 1 : progress;
		this.getProgressBar().updateProgress(prog, msg);
	},
	
	appendTabTitle: function(text) {
		if (typeof text == "string" && text.length > 0) {
			if (this.originalTitle == undefined || this.originalTitle == null) {
				this.originalTitle = this.title;
			}

			var title = (this.originalTitle) ? this.originalTitle : this.title;
			this.setTitle(title + " " + text);
		}
	},
	
	load: function(custno) {
		// TODO: implement in extended class
	},
	
	loadOneHop: function(node) {
		// TODO: implement in extended class
	},
	
	showjson: function(searchValue) {
		if (this.json != null && this.GraphVis.getGv() != null) {
			this.GraphVis.showGraph(this.json, searchValue);
		}
	},
	
	// why is this even here?
	applySettings: function() {
		this.load(this.prevLoadPrams.value); 
	},
	
	afterLayout: function() {
		var self = this;
		if (self.GraphVis.getGv() == null) {
			var config = {
				//width: self.getWidth(),
				//height: self.getHeight(),
				rightBorder: 320,
				leftBorder: 5,
				topBorder: 5,
				botBorder: 80
			};
			self.GraphVis.initGraph(config, self, function() {
				self.showjson(self.prevLoadParams.value);
			});
		}
		this.callParent(arguments); // ?
	},
	
	afterRender: function() {
		this.showjson();
		this.callParent(arguments); // ?
	},
	
	importGraph: function() {
		var importWindow = Ext.create("DARPA.importDialog", {
			title: "Import Window",
			border: true
		});
		
		importWindow.setGraphVis(this.GraphVis);
		importWindow.show();
		importWindow.center();
	},
	
	exportGraph: function() {
		var self = this;
		var outJSON;
		try {
			outJSON = this.GraphVis.exportGraph();
		} catch (e) {
			console.error("ERROR GETTING GRAPH JSON");
			outJSON = undefined;
		}
		
		var outPNG;
		try {
			outPNG = self.GraphVis.gv.png();
		} catch (e) {
			console.error("ERROR GETTING GRAPH PNG");
			outPNG = undefined;
		}
		
		var exportWindow = Ext.create("DARPA.exportDialog", {
			title: "Export Window",
			border: true
		});
		
		exportWindow.setGraphJSON(outJSON);
		exportWindow.setGraphPNG(outPNG);
		//TODO: work some magic with scope so you can build default file name with query parameters with this.getSearch().getName()
		exportWindow.setFileName(self.graphType + "-" + self.prevLoadParams.searchValue);
		exportWindow.show();
		exportWindow.center();
	},
	
	saveGraph: function(userId) {
		var outJSON = this.GraphVis.exportGraph();
		var uid = (typeof userId == "undefined") ? "unknown" : userId; // FIXME: get userID somehow
		UDSIF.init();
		var UDSAction = {type: "sg", data: {graphJSON: outJSON}};
		UDSIF.addToUDSession(UDSAction);
		UDSIF.saveUDSession(uid, null);
	},
	
	restoreGraph: function(butId) {
		var uid = "unknown";  // FIXME: get userID somehow
		UDSIF.restoreUDSession(uid, this.GraphVis, butId);
	},
	
	nodeClick: function(node) {
		// TODO: implement in extended class
		var nodeDisp = this.getNodeDisplay();
		nodeDisp.showNodeAttrs(node);
		nodeDisp.enablePivot(true);
        nodeDisp.enableHide(true);
	},
	
	edgeClick: function(edge) {
		// TODO: implement in extended class
		var nodeDisp = this.getNodeDisplay();
		var d = edge.data();
		if (d && d.attrs) {
			nodeDisp.showEdgeAttrs(edge);
		}
	},
	
	edgeRightClick: function(edge) {
		// TODO: override in customization
	},
	
	editNode: function(node) {
		var self = this;

		if (!node || !node.isNode || !node.isNode()) {
			console.error("Variable 'node' is not a cytoscape node object.");
			return;
		}

		var x_pos = node.position().x;
		var y_pos = node.position().y;

		var window = Ext.create("DARPA.NodeEditor", {
			nodeRef: node,
			onComplete: function() {
				var fields = window.getAttrs();
				var newAttrs = [];
				for (var i = 0; i < fields.length; i++) {
					newAttrs.push({
						key: fields[i].name, 
						val: fields[i].value
					});
				}
				node.data().attrs = newAttrs;
				
				node.data({
					"name": window.getName(),
					"idType": window.getIdType(),
					"color": window.getColor(),
					"EDITED_BY_USER": true
				});
			}
		});

		// window.showAt(x_pos, y_pos - window.height)?
		window.show();
	},
	
	givePromptToMerge: function(superNode, selectedNodes) {
		var scope = this;
		var window = Ext.create("DARPA.NodeMergeDialog", {
			confirmFn: function(reason) {
				// first, do the client-side merge of all the nodes and get subNodeIds
				var subNodeIds = scope.mergeNodes(superNode, selectedNodes); 

				var graph = {
					nodes: scope.GraphVis.gv.nodes().jsons(),
					edges: scope.GraphVis.gv.edges().jsons()
				};
				
				// ...then persist those unmerges to the back-end service via REST
				/*
				Ext.Ajax.request({
					method: "POST",
					url: "TODO",
					//headers: {"Content-Type": "application/json"},
					params: {
						isMerge: true,
						superNodeIds: [superNode.data("id")],
						subNodeIds: subNodeIds,
						userComment: reason,
						graph: graph
						//TODO: get user ID
					},
					scope: this,
					success: function(resp) {
						console.log("Persist POST success.");
					},
					failure: function(resp) {
						console.error("Persist POST failed.");
					}
				});
				*/
			},
			cancelFn: function() {
				// don't merge
			}
		});
		window.show();
	},
	
	mergeNodes: function(superNode, selectedNodes) {
		var doContinue = true;
		try {
			if (typeof superNode == "undefined") throw "Variable 'superNode' is undefined.";
			if (typeof superNode.isNode == "undefined" || typeof superNode.isNode() == false) throw "Right-clicked element is not a node.";
			if (typeof selectedNodes == "string" || selectedNodes.length < 1) throw "No other nodes were selected.";
		} catch (e) {
			doContinue = false;
			Ext.Msg.alert("Failed to Merge", "Unable merge nodes.  Reason: " + e);
		}

		var _this = this;
		var typesMatch = true;
		for (var i = 0; i < selectedNodes.length; i++) {
			if (superNode.data("idType") !== selectedNodes[i].data("idType")) {
				typesMatch = false;
				break;
			}
		}
		
		if (!typesMatch) {
			doContinue = false;
			Ext.Msg.alert("Failed to Merge", "You can only merge nodes of the same type.");
		}
		
		if (!doContinue) return;
		
		var subNodeIds = [];
		
		selectedNodes.each(function(i, n) {
			if (n.data("id") != superNode.data("id")) {
			
				subNodeIds.push(n.data("id"));
				
				n.connectedEdges().each(function(j, e) {
					var edge_clone = e.json();
					if (e.data("target") == n.data("id")) {
						// selectedNode n is target node
						if (typeof edge_clone.data.old_targets == "undefined") {
							edge_clone.data.old_targets = [];
						}
						edge_clone.data.old_targets.push(n.data("id"));
						edge_clone.data.target = superNode.data("id");
					} else if (e.data("source") == n.data("id")) {
						// selectedNode n is source node
						if (typeof edge_clone.data.old_sources == "undefined") {
							edge_clone.data.old_sources = [];
						}
						edge_clone.data.old_sources.push(n.data("id"));
						edge_clone.data.source = superNode.data("id");
					}
					_this.GraphVis.gv.remove(e);
					_this.GraphVis.gv.add(edge_clone);
				});
				
				if (typeof superNode.data().subNodes == "undefined") {
					superNode.data().subNodes = [];
				}
				
				superNode.data().subNodes.push(n.json());
				_this.GraphVis.deleteNodes([n]);
			}
		});
		
		return subNodeIds;
	},
	
	givePromptToUnmerge: function(superNodes) {
		var scope = this;
		var window = Ext.create("DARPA.NodeMergeDialog", {
			confirmFn: function(reason) {
				// first, do the client-side merge of all the nodes
				var superNodeIds = [];
				superNodes.each(function(i, n) {
					superNodeIds.push(n.data("id"));
					scope.unmergeNode(n);
				});

				var graph = {
					nodes: scope.GraphVis.gv.nodes().jsons(),
					edges: scope.GraphVis.gv.edges().jsons()
				};
				
				// ...then persist those unmerges to the back-end service via REST
				/*
				Ext.Ajax.request({
					method: "POST",
					url: "TODO",
					//headers: {"Content-Type": "application/json"},
					params: {
						isMerge: false,
						superNodeIds: superNodeIds
						subNodeIds: [],
						userComment: reason,
						graph: graph
						//TODO: get user ID
					},
					scope: this,
					success: function(resp) {
						console.log("Persist POST success.");
					},
					failure: function(resp) {
						console.error("Persist POST failed.");
					}
				});
				*/
			},
			cancelFn: function() {
				// don't merge
			}
		});
		window.show();
	},
	
	unmergeNode: function(superNode) {
		if (typeof superNode == "undefined" || typeof superNode.isNode == "undefined" ||
			superNode.isNode == false || typeof superNode.data().subNodes == "undefined") {
			// fail quietly
			console.log("variable superNode is not valid input");
			return;
		}
		
		var _this = this;
		
		var subNodes = superNode.data("subNodes");
		while (subNodes.length > 0) {
			var subNodeJSON = subNodes.pop();
			_this.GraphVis.gv.add(subNodeJSON);
			_this.json.nodes.push(subNodeJSON);
			
			_this.GraphVis.gv.edges().each(function(i, e) {
				var edge_clone;
				
				if (typeof e.data().old_sources !== "undefined" && e.data().old_sources.length > 0) {
					var old_source = e.data().old_sources.pop();
					if (old_source == subNodeJSON.data.id) {
						edge_clone = e.json();
						edge_clone.data.source = subNodeJSON.data.id;
						_this.GraphVis.gv.remove(e);
						_this.GraphVis.gv.add(edge_clone);
						_this.json.edges.push(edge_clone);
					} else {
						// didn't match, so put it back
						e.data().old_sources.push(old_source);
					}
				}
				
				if (typeof e.data().old_targets !== "undefined" && e.data().old_targets.length > 0) {
					var old_target = e.data().old_targets.pop();
					if (old_target == subNodeJSON.data.id) {
						edge_clone = e.json();
						edge_clone.data.target = subNodeJSON.data.id;
						_this.GraphVis.gv.remove(e);
						_this.GraphVis.gv.add(edge_clone);
						_this.json.edges.push(edge_clone);
					} else {
						// didn't match, so put it back
						e.data().old_targets.push(old_target);
					}
				}
			});
		}
		// might not be necessary, since subNodes would be [] at this point
		delete superNode.data().subNodes;
	},
	
	pivot: function(node) {
		var self = this;
		var id = node.data("idVal");
		self.previousPivotIds.push(self.prevLoadParams.prevValue);
		self.load(id);
	},
	
	unpivot: function() {
		var self = this;
		var prevId = self.previousPivotIds.pop();
		if (typeof prevId !== "undefined") {
			this.load(prevId);
		}
	},
	
	hideNode: function(node) {
		this.GraphVis.hideNode(node);
		var unhideBtn = this.getUnHideButton();
		if (unhideBtn) {
			unhideBtn.setDisabled(false);
		}
	},
	
	unhide: function() {
		//var self = this;
		//if (self, prevNode) {
		//	self.GraphVis.showNode(prevNode);
		//	
		//	var unhide = self.getUnHideButton();
		//	if (unhide) {
		//		unhide.setDisabled(true);
		//	}
		//}
	},
	
	expand: function(node) {
		this.loadOneHop(node);
	},
	
	unexpand: function(node) {
		this.GraphVis.unexpand1Hop(node);
	},
	
	showDetail: function(nodeData) {
		showEntityDetails(nodeData.idVal);
	},
	
	applyFilter: function(searchItems, amount, fromDate, toDate) {
		// applyFilter(this, searchItems, amount, fromDate, toDate);
		var self = this;
		// applyFilter(self, searchItems, amount, null, null);
		// MFM from and todates set to null until entity graph has temporal data
		applyFilter(self, searchItems, amount, fromDate, toDate);
	},
	
	applyAdditionalFieldsFilter: function(filterItems, compareType) {
		var self = this;
		applyAdditionalFieldsFilter(self, filterItems, compareType);
	},

	clearFilter : function() {
		clearFilter(this);
	},

	clear : function() {
		var self = this;
		if (self.GraphVis != null) {
			self.GraphVis.clear();
		}
	},
});