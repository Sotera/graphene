Ext.define("DARPA.EntityGraphPanel", {
	extend: "DARPA.AbstractGraphPanel",
	
	constructor: function(config) {
		var self = this;
		
		this.GraphVis = new CytoGraphVis(config.id + "-ENTcygraph");
		
		var graphSettings = Ext.create("DARPA.GraphSettings", {
			id: config.id + "-Settings"
		});
		
		graphSettings.setGraph(self);
		
		var filterSettings = Ext.create("DARPA.FilterSettings", {
			id: config.id + "-Filter"
		});
		
		filterSettings.setAdditionalFields([{
			dispFieldName: "Node Color", 		dispFieldType: "dropdown", 	dispFieldWidth: 100,
			dispFieldChoices: "getfromnode",	dataSourceType: "nodes", 	dataSourceField: "color"
		}, {
			dispFieldName: "Node Name", 		dispFieldType: "dropdown", 	dispFieldWidth: 100,
			dispFieldChoices: "getfromnode", 	dataSourceType: "nodes", 	dataSourceField: "name"
		}, {
			dispFieldName: "Identifier Type", 	dispFieldType: "dropdown", 	dispFieldWidth: 100,
			dispFieldChoices: "getfromnode", 	dataSourceType: "nodes", 	dataSourceField: "idType"
		}, {
			dispFieldName: "getfromkey", 	dispFieldType: "text", 		dispFieldWidth: 100,
			dispFieldChoices: "", 			dataSourceType: "nodes", 	dataSourceField: "attrs"
		}, {
			dispFieldName: "Amount", 	dispFieldType: "text", 		dispFieldWidth: 100,
			dispFieldChoices: "", 		dataSourceType: "edges", 	dataSourceField: "amount"
		}, {
			dispFieldName: "getfromkey", dispFieldType: "text", dispFieldWidth: 100,
			dispFieldChoices: "", dataSourceType: "edges", dataSourceField: "attrs"
		}]);
		
		filterSettings.setGraph(self);
		filterSettings.enableTimeFilter(false); // TODO renable based on graph type or if data is temporally-based
		filterSettings.setSearchFieldLabel("Identifiers(s)");
		
		var graphContainer = Ext.create("Ext.Container", {
			width: 'auto',
			height: 'auto',
			id: config.id + "-ENTcygraph",
			flex: 1
		});
		
		var settingsPanel = Ext.create("Ext.tab.Panel", {
			width: 320,
			height: 'auto',
			collapsible: true,
			collapseDirection: 'right',
			items: [
				Ext.create("Ext.panel.Panel", {
					title: "DETAILS/ACTIONS",
					layout: 'fit',
					items: [
						Ext.create("DARPA.EntityNodeDisplay", {
							id: config.id + "-NodeDisplay",
							height: "auto"
						})
					]
				}),
				Ext.create("Ext.panel.Panel", {
					title: "SETTINGS/FILTER",
					items: [
						graphSettings,
						Ext.create("Ext.panel.Panel", {
							title: "FILTER",
							collapsible: false,
							items: filterSettings
						})
					] // END settings/filter items
				})
			] // END settings panel items
		});
		
		this.items = [graphContainer, settingsPanel];
		this.callParent(arguments);
	},

	getNodeDisplay : function() {
		var tabPanel = this.items.items[1];
		var tabScreen = tabPanel.items.items[0];
		return tabScreen.items.items[0];
	},
	
	getSettings : function() {
		var tabPanel = this.items.items[1];
		var settingsPanel = tabPanel.items.items[1];
		return settingsPanel.items.items[0];
	},
	
	load: function(custno) {
		var self = this;
		
		var graphStore = self.graphStore;
		var hops = self.getSettings().getMaxHops();
		var degree = parseInt(hops);// + 1; //djue

		self.json = null;
		self.prevLoadParams.searchValue = self.prevLoadParams.value = self.prevLoadParams.prevValue = custno;
		
		self.getProgressBar().wait({
        	interval: 1000,
        	duration: 90000,
        	increment: 10,
        	text: "Searching..."
        });

		graphStore.proxy.extraParams.degree = degree;
		graphStore.proxy.url = Config.entityGraphCSUrl + 'customer/' + custno;
		
		graphStore.load({
			scope: this, // ?
			callback: function(records, operation, success) {

				self.getProgressBar().reset();

				if (success == false || records == null || records.length == 0) {
					if (success == false) self.setStatus("SERVER ERROR REQUESTING GRAPH");
					else if (records == null) self.setStatus("SERVER RETURNED NULL GRAPH");
					else if (records.length == 0) self.setStatus("SERVER RETURNED EMPTY GRAPH");
					self.clear();
					return;
				}

				self.setStatus("LOADED DATA", 1);
				self.json = records[0].raw;
				
				if (self.json && self.json.nodes.length == 0) {
					self.setStatus("NO DATA FOUND TO PLOT");
					// self.clear(); // don't clear what is already shown
				} else {
					if (self.GraphVis.getGv() != null) {
						self.clear();
						self.showjson(self.prevLoadParams.value);
					}
				}

				var nodeCount = self.json.nodes.length;
				self.appendTabTitle("(" + nodeCount.toString() + ")");
			}
		});
	},
	
	loadOneHop: function(node) {
		var self = this;
		var graphStore = self.graphStore;
		var s = self.getSettings();
		var maxNewCallsAlertThresh = 30; // Adjust as needed
		
		graphStore.proxy.extraParams.degree = 1; // labelled hops. only 1 hop out from this node
		graphStore.proxy.extraParams.maxEdgesPerNode = s.getMaxEdgesPerNode();
		graphStore.proxy.extraParams.maxNodes = s.getMaxNodes();
		if (graphStore.proxy.extraParams.maxNodes > 200) {
			graphStore.proxy.extraParams.maxNodes = 200; // hard limit for this case
		}
		if (maxNewCallsAlertThresh > graphStore.proxy.extraParams.maxEdgesPerNode) {
			maxNewCallsAlertThresh = graphStore.proxy.extraParams.maxEdgesPerNode;
		}
		graphStore.proxy.extraParams.minWeight = s.getMinWeight();

		if (intype == null || intype.length == 0) {
			intype = "customer";
		}
		graphStore.proxy.extraParams.Type = intype;

		// Type must be set in the URL
		graphStore.proxy.url = Config.entityGraphUrl + intype + '/' + node.data().name;

		self.json1Hop = null; // prevents us from trying to display the previous graph if we switch to this tab before we have fully loaded the new graph
		self.json1HopNode = node;

		self.getProgressBar().wait({
        	interval: 1000,
        	duration: 90000,
        	increment: 10,
        	text: "Expanding..."
        });
		
		graphStore.load({
			scope : this, // ?
			callback : function(records, operation, success) {

				self.getProgressBar().reset();
				
				if (success == false || records == null || records.length == 0) {
					if (success == false) self.setStatus("SERVER ERROR REQUESTING GRAPH");
					else if (records == null) self.setStatus("SERVER RETURNED NULL GRAPH");
					else if (records.length == 0) self.setStatus("SERVER RETURNED EMPTY GRAPH");
					self.clear();
					return;
				}

				self.setStatus("LOADED DATA", 1);
				self.json = records[0].raw;;

				// results could be empty, check for this here
				if (self.json && self.json.nodes.length <= 2) { 
					self.setStatus("No additional items were found for this id.");
					self.json1HopNode = null;
					// don't alter the existing graph
				} else {
					// should be self.json.nodes.length
					if (self.json.length > maxNewCallsAlertThresh) {
						Ext.Msg.confirm(
							'Confirm',
							'This value has more than ' + maxNewCallsAlertThresh + ' items and may clutter the display. Do you want to continue displaying it?',
							function(ans) {
								if (ans == 'yes') {
									self.GraphVis.showGraph1Hop(self.json, node);
								}
							}
						);
					} else {
						self.GraphVis.showGraph1Hop(self.json, node);
					}
				}

				var nodeCount = self.json.nodes.length;
				self.appendTabTitle("(" + nodeCount.toString() + ")");
				
				// Update title to display the communicationId value and value of nodes found
				// self.updateTitle(graph.nodes.length, self.prevLoadParams.value );
			}
		});
	},
	
	nodeClick: function(node) {
		var self = this;
		self.currentNode = node;
		var nodeDisp = self.getNodeDisplay();
		nodeDisp.showNodeAttrs(node);
		var type = node.data().idType;
		var isEntity = false;
		if (typeof isPivotableType == "function") {
			// isPivotableType is a global function found in .html
			isEntity = isPivotableType(type);
		} else {
			isEntity = (type == 'customer' || type == 'LENDER' || type == 'BORROWER');
		}
		nodeDisp.enablePivot(isEntity);
		nodeDisp.enableShow(type == 'account' || isEntity);
		nodeDisp.enableHide(true);
	},
	
	edgeClick: function(edge) {
		var nodeDisp = this.getNodeDisplay();
		var d = edge.data();
		if (d && d.attrs) {
			nodeDisp.showEdgeAttrs(edge);
		}
	},
	
	edgeRightClick: function(edge) {
		// do nothing
	}
});