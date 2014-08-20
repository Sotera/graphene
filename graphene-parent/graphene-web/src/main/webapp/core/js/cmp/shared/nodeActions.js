//nodeActions.js

// A panel in the graph layout that shows the buttons available whan a node is selected
// This might be replaced by context menu options on the nodes

Ext.define("DARPA.Node_Actions", {

	extend:"Ext.Panel",
	title:'ACTIONS',
	height:'auto',
	width:'auto',
	layout:{
		type:'vbox',
		align:'stretch'
	},
	
	constructor:function(config) {

		var pivot = Ext.create("Ext.Button", {
			text:'PIVOT',
			id: config.id + '-PIVOT',
			disabled:true,
			margin:4,   
			height:24,
			width:58,
			handler:function(but)
			{
				var path = but.id.split('-');
				var graphId = path[0] + '-' + path[1];
				var graph = Ext.getCmp(graphId);
				var nodes = graph.GraphVis.gv.$("node:selected");
				if (nodes.length == 1) {
					var unpivot = graph.getUnPivotButton();
					if (unpivot) {
						unpivot.enable();
					};
					if (but) {
						but.disable();
					};
					graph.pivot(nodes[0]);

					// DRAPER API
					// Send a User Activity Message with optional metadata
					//activityLogger.logUserActivity('Pivot on Selected Node', 'PB Graph Tab', activityLogger.WF_OTHER, 
					//    {'Tab':'PB Graph', 'action': 'Pivot', 'selectedNode': node.data().name });

				} else {
					var msg = (nodes.length == 0) ?
						"You must first select a node before you can attempt to pivot the graph." :
						"Please select only one node before attempting to pivot the graph.";
					Ext.Msg.alert("Warning", msg);
				} // if node
			} // handler
		}); // pivot

		var hide = Ext.create("Ext.Button", {
			text:'HIDE',
			id: config.id + '-HIDE',
			disabled:true,
			margin:4,   
			height:24,
			width:58,
			handler:function(but) {
				var path = but.id.split('-');
				var graphId = path[0] + '-' + path[1];
				var graph = Ext.getCmp(graphId);
				var nodes = graph.GraphVis.gv.$("node:selected");
				if (nodes.length > 0) {
					if (!(graph.hideNode == undefined)) {
						for (var i = 0; i < nodes.length; i++) {
							graph.hideNode(nodes[i]);
						}
					} else {
					
						for (var i = 0; i < nodes.length; i++) {
							graph.GraphVis.hideNode(nodes[i]);
						}
						
						var unhide = graph.getUnHideButton();
						if (unhide) {
							unhide.setDisabled(false);
						};
					}

					// DRAPER API
					// Send a User Activity Message with optional metadata
					//activityLogger.logUserActivity('Hide Selected Node', 'PB Graph Tab', activityLogger.WF_OTHER, 
					//    {'Tab':'PB Graph', 'action': 'Hide', 'selectedNode': node.data().name });
				};
			} // handler
		}); // hide

		var unpivot = Ext.create("Ext.Button", {
			text:'UNPIVOT',
			id: config.id + '-UNPIVOT',
			disabled:true,
			margin:4,   
			height:24,
			width:58,
			handler:function(but)
			{
				var path = but.id.split('-');
				var graphId = path[0] + '-' + path[1];
				var graph = Ext.getCmp(graphId);
				if (graph) {
					var pivot = graph.getPivotButton();
					if (pivot) {
						pivot.enable();
					}
					if (but) {
						but.disable();
					}
					graph.unpivot('customer');

					// DRAPER API
					// Send a User Activity Message with optional metadata
					//activityLogger.logUserActivity('Pivot on Selected Node', 'PB Graph Tab', activityLogger.WF_OTHER, 
					//    {'Tab':'PB Graph', 'action': 'Pivot', 'selectedNode': node.data().name });

				}; // if node
			} // handler
		}); // unpivot

		var unhide = Ext.create("Ext.Button", {
			text:'UNHIDE', 
			disabled:true, 
			id: config.id + '-UNHIDE',
			margin:4,   
			height:24,
			width:60,
			handler:function(but)
			{
				var path = but.id.split('-');
				var graphId = path[0] + '-' + path[1];
				var graph = Ext.getCmp(graphId);
				if (graph) {
					graph.GraphVis.showAll();
					but.setDisabled(true);
					
					// DRAPER API
					// Send a User Activity Message with optional metadata
					//activityLogger.logUserActivity('UNHide Selected Node', 'PB Graph Tab', activityLogger.WF_OTHER, 
					//    {'Tab':'PB Graph', 'action': 'UNHide', 'selectedNode': gf.prevNode.name });
				}; // if graph
			} // handler
		} // extend config
		); // extend

		var expand = Ext.create("Ext.Button", {
		    text:'EXPAND',
		    id: config.id+'-EXPAND',
		    disabled:false,
		    margin:4,   
		    height:24,
		    width:58,
		    handler: function(but) {
				var path = but.id.split('-');
				var graphId = path[0] + '-' + path[1];
				var graph = Ext.getCmp(graphId);
				if (graph) {
					var node = graph.currentNode;
					if (node) {
						but.disable();
						graph.expand(node); // expand out 1 hop from this node
						but.enable();
					}

					/* uncomment once the selection manager is implemented *//*
						var nodes = graph.GraphVis.gv.$("node:selected");
						but.disable();
						for (var i = 0; i < nodes.length; i++) {
							graph.expand(nodes[i]);
							graph.SM.add(nodes[i]);
						}
						but.enable();
					*/
					
					// DRAPER API
					// Send a User Activity Message with optional metadata
					//activityLogger.logUserActivity('Expand Selected Node', 'PB Graph Tab', activityLogger.WF_SEARCH, 
					//    {'Tab':'PB Graph', 'action': 'Expand', 'selectedNode': node.data().name });
				}
		    }
		});

		var unexpand = Ext.create("Ext.Button", {
		    text:'UNEXPAND',
		    id: config.id+'-UNEXPAND',
		    disabled:false,
		    margin:4,   
		    height:24,
		    width:58,
		    handler: function(but) {
				var path = but.id.split('-');
				var graphId = path[0] + '-' + path[1];
				var graph = Ext.getCmp(graphId);
				if (graph) {
					var node = graph.currentNode;
					if (node) {
						but.disable();
						graph.unexpand(node);
						but.enable();
					}

					/* uncomment once the selection manager is implemented *//*
						var nodes = graph.SM.getAll();
						but.disable();
						
						for (var i = 0; i < nodes.length; i++) {
							graph.unexpand(nodes[i]);
						}
						
						but.enable();
						graph.SM.clear();
					*/
					
					// DRAPER API
					// Send a User Activity Message with optional metadata
					//activityLogger.logUserActivity('Expand Selected Node', 'PB Graph Tab', activityLogger.WF_SEARCH, 
					//    {'Tab':'PB Graph', 'action': 'Expand', 'selectedNode': node.data().name });
				}
		    }
		});
		
		var show = Ext.create("Ext.Button", {
			text:'SHOW',
			id:config.id + '-SHOW',
			disabled:true,
			margin:4,   
			height:24,
			width:58,
			handler: function(but) {
				var path = but.id.split('-');
				var graphId = path[0] + '-' + path[1];
				var graph = Ext.getCmp(graphId);
				var nodes = graph.GraphVis.gv.$("node:selected");
				
				if (nodes.length > 0) {
					for (var i = 0; i < nodes.length; i++) {
						graph.showDetail(nodes[i].data());
					}
				} else {
					Ext.Msg.alert("Warning", "You must first select one or more nodes before you can create its tab")
				}
		    }
		});

		var stop = Ext.create("Ext.Button", {
			text: "HALT LAYOUT",
			id: config.id + '-STOP',
			disabled: true,
			margin: 4,
			height: 24,
			width: 58,
			last_layout: undefined,
			setCurrentLayout: function(layoutStr) {
				this.last_layout = layoutStr;
			},
			handler: function(btn) {
				var currentLayout = btn.last_layout;
				
				switch (currentLayout) {
					case "COSE" :
						cytoscape.extensions.layout.cose.prototype.stop();
						break;
					case "ARBOR":
						cytoscape.extensions.layout.arbor.prototype.stop();
						break;
					default:
						break;
				}
			}
		});
		
		var help = Ext.create("Ext.Button", {
		    icon: Config.helpIcon,
		    maxHeight: 30,
		    scale: 'medium',
		    margin: 2,
		    style: {marginTop: '2px'},
		    handler: function() {
			    Ext.Msg.alert(
			       'Help',
			       'First select a node then click one of the Action buttons.<br>' +
			       'To select or deselect a node, click on its id or drag it.<br><br>' +
			       '<b>PIVOT</b>:&nbsp;&nbsp;&nbsp;&nbsp; This will perform a search using the selected item and will re-center the graph around this item.<br><br>' +
			       '<b>UNPIVOT</b>:&nbsp;This will perform a search using the previously selected item and will re-center the graph around this item.<br><br>' +
			       '<b>HIDE</b>:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This will hide the selected item from the graph.<br><br>' +
			       '<b>EXPAND</b>:&nbsp;&nbsp;This will expand out one hop from a selected item. You can only expand an item that has 1 connection.<br><br>' +
			       '<b>UNEXPAND</b>: This will unexpand a previously expanded item.<br><br>' +
			       '<b>Show</b>:&nbsp;&nbsp;&nbsp;&nbsp; This will display a Show for the selected item.<br><br>' +
			       //'<b>TIMELINE+</b>:&nbsp;&nbsp;&nbsp;This will show(+) or hide(-) an interaction timeline for the selected item.<br><br>' +
				'<b>EXPORT GRAPH</b>:&nbsp;&nbsp;&nbsp;&nbsp;This exports the currently displayed graph to a file of your choice.<br><br>' +
				'<b>IMPORT GRAPH</b>:&nbsp;&nbsp;&nbsp;&nbsp;This import a previously exported graph.<br><br>'
			    );
		
			    // DRAPER API
				// Send a User Activity Message with optional metadata
			       // activityLogger.logUserActivity('View ACTIONS Help', 'PB Graph Tab', activityLogger.WF_OTHER, 
			       //     {'Tab':'PB Graph', 'action': 'ACTIONS Help', 'selectedNode': 'NA' });
			}
		});

		
		var actionButtonsLine1 = Ext.create("Ext.form.FieldSet", { 
			border: 0,
			maxHeight: 40,
			padding: 0,
			margin: 0,
			items:[
			       {
			    	   xtype:'fieldcontainer',
			    	   height:'auto',
			    	   items:[pivot, unpivot, hide, unhide]
			       }
	       		]
		});
		var actionButtonsLine2 = Ext.create("Ext.form.FieldSet", { 
			border: 0,
			maxHeight: 40,
			padding: 0,
			margin: 0,
			items:[
			       {
			    	   xtype:'fieldcontainer',
			    	   height:'auto',
			    	   items:[expand, unexpand, show, stop, help]
			       }
	       		]
			
		});

		this.items = [
		              actionButtonsLine1,
		              actionButtonsLine2
		              ];

	this.callParent(arguments);
	} // constructor

});  

