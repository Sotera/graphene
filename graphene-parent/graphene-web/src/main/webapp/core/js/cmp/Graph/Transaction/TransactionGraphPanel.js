Ext.define("DARPA.TransactionGraphPanel", {
	extend: "DARPA.AbstractGraphPanel",
	
	constructor: function(config) {
		var self = this;
		
		this.GraphVis = new CytoGraphVis(config.id + "-TXNcygraph");
		
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
			id: config.id + "-TXNcygraph",
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
						Ext.create("DARPA.TransactionNodeDisplay", {
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
			}, false);
		}
		this.callParent(arguments); // ?
	},
	
	load: function(custno, useSaved) {
		var self = this;
		
		// TODO write catch to make sure variable useSaved is a boolean
		
		self.setStatus("LOADING DATA FOR GRAPH");
		self.json = null;
		
		var graphStore = self.graphStore;
		var hops = self.getSettings().getMaxHops();
		var degree = parseInt(hops) + 1;
		
        self.prevLoadParams.searchNumber = custno;
        self.prevLoadParams.prevValue = custno;
        self.prevLoadParams.number = custno;
        
        this.getProgressBar().wait({
        	interval: 1000,
        	duration: 90000,
        	increment: 10,
        	text: "Searching..."
        });
		
		graphStore.proxy.extraParams.degree = degree;
		graphStore.proxy.extraParams.useSaved = useSaved;
		graphStore.proxy.url = Config.transferGraphCSUrl + custno;
		graphStore.proxy.extraParams.Type = 'account';
        
		graphStore.load({
			scope: this, //?
			callback: function(records,operation,success) {
				self.getProgressBar().reset();
				
				if (success == false || records == null || records.length == 0) {
					if (success == false) self.setStatus("SERVER ERROR REQUESTING GRAPH");
					else if (records == null) self.setStatus("SERVER RETURNED NULL GRAPH");
					else if (records.length == 0) self.setStatus("SERVER RETURNED EMPTY GRAPH");
					self.clear();
					return;
				}
				
				self.setStatus("LOADED DATA", 1);
				self.json=records[0].raw;
				
				self.legendJSON = records[0].raw.legend;
				if (typeof self.legendJSON == "string") {
					self.legendJSON = Ext.decode(self.legendJSON);
				} // else assume JSON
				
				if (self.json && self.json.nodes.length == 0) {
					self.setStatus("NO DATA FOUND TO PLOT");
				} else { 
					// hack to determine if graph comes back with saved positions
					if (self.json.nodes[0].position == null) useSaved = false;
					if (self.GraphVis.getGv() != null) {
						self.clear();
						self.showjson(self.prevLoadParams.number, useSaved);
					}
				}
				
				var nodeCount = self.json.nodes.length;
				self.appendTabTitle("(" + nodeCount.toString() + ")");
				self.getNodeDisplay().updateLegend(self.legendJSON, "TransactionGraph");
			}
		});
	},
	
	loadOneHop: function(node) {
		var self=this;
		var graphStore = this.graphStore; 
		var s = self.getSettings(); 
		var maxNewCallsAlertThresh = 30;
		
		self.json1Hop = null; // prevents us from trying to display the previous graph if we switch to this tab before we have fully loaded the new graph
		
		graphStore.proxy.extraParams.degree = 1; // labelled hops. only 1 hop out from this node
		graphStore.proxy.extraParams.maxEdgesPerNode = s.getMaxEdgesPerNode();
		graphStore.proxy.extraParams.maxNodes = s.getMaxNodes();
		graphStore.proxy.extraParams.minWeight  = s.getMinWeight();
		graphStore.proxy.extraParams.Type	= "account";	
		
		graphStore.proxy.url = Config.transferGraphCSUrl + node.data().id;
		
		if (graphStore.proxy.extraParams.maxNodes > 200) {
			graphStore.proxy.extraParams.maxNodes = 200;    // hard limit for this case
		}
		
		if (maxNewCallsAlertThresh > graphStore.proxy.extraParams.maxEdgesPerNode) {
			maxNewCallsAlertThresh = graphStore.proxy.extraParams.maxEdgesPerNode;
		}
		
		self.getProgressBar().wait({
        	interval: 1000,
        	duration: 90000,
        	increment: 10,
        	text: "Expanding..."
        });
		
		graphStore.load({
			scope: self, //?
			callback: function(records, operation, success) {

				self.getProgressBar().reset();
				
				if (success == false || records == null || records.length == 0) {
					if (success == false) self.setStatus("SERVER ERROR REQUESTING GRAPH");
					else if (records == null) self.setStatus("SERVER RETURNED NULL GRAPH");
					else if (records.length == 0) self.setStatus("SERVER RETURNED EMPTY GRAPH");
					self.clear();
					return;
				}
				
				self.json = records[0].raw;
				self.setStatus("LOADED DATA", 1);

				self.legendJSON = records[0].raw.legend;
				if (typeof self.legendJSON == "string") {
					self.legendJSON = Ext.decode(self.legendJSON);
				} // else assume JSON
				
				// results could be empty, check for this here
				if (self.json && self.json.nodes.length <= 2) { 
					//  don't include this node already connected to another node in the graph
					self.setStatus("No additional items were found for this id.");
					self.json1HopNode = null;
					// don't alter the existing graph
				} else {
					// should be self.json.nodes.length
					if (self.json.length > maxNewCallsAlertThresh) {
						Ext.Msg.confirm('Confirm', 
							'This number has more than ' + maxNewCallsAlertThresh + ' items and may clutter the display.\n' + 
							'Do you want to continue displaying it?', 
							function(ans) {
								if (ans == 'yes') {
									self.GraphVis.showGraph1Hop(self.json, node);
									self.getNodeDisplay().updateLegend(self.legendJSON, "TransactionGraph");
								}
							}
						);
					} else {
						self.GraphVis.showGraph1Hop(self.json, node);
						self.getNodeDisplay().updateLegend(self.legendJSON, "TransactionGraph");
					}
				}
				// Update title to display the communication id and number of nodes found 
				//self.updateTitle(graph.nodes.length, self.prevLoadParams.number );
			}
		});
	},
	
	nodeClick: function(node) {
		var nodeDisp = this.getNodeDisplay();
		nodeDisp.showNodeAttrs(node);
		nodeDisp.enablePivot(true);
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
		var data = edge.data();
		var attrs = data.attrs;
		
		var to, from, subject, body, date;
		
		// use this edge's TO and FROM ids to find the nodes with those ids, then get their names
		to = this.GraphVis.gv.$("node[id = '" + data.target +"']").data().name;
		from = this.GraphVis.gv.$("node[id = '" + data.source +"']").data().name;
		
		for (var i = 0; i < attrs.length; i++) {
			var key = attrs[i].key;
			
			switch (key) {
				case "date":
					try {
						date = new Date(parseInt(attrs[i].val));
						date = date.toTimeString();
					} catch (e) {
						date = null;
					}
					break;
				case "payload":
					body = attrs[i].val;
					break;
				case "subject":
					subject = attrs[i].val;
					break;
				default: break;
			}
		}
		var window = Ext.getCmp("darpa-email-viewer");
		if (typeof window == "undefined") {
			var window = Ext.create("DARPA.DetailsViewer", {
				timeStamp: date
			});
		}
		window.show();
		
		window.setTo(to);
		window.setFrom(from);
		window.setSubject(subject);
		window.setBody(body);
	}
});