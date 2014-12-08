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
			width: "25%",
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
					graph.pivot(nodes[0]);
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
			width: "25%",
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
				};
			} // handler
		}); // hide

		var unpivot = Ext.create("Ext.Button", {
			text:'UNPIVOT',
			id: config.id + '-UNPIVOT',
			disabled:true,
			margin:4,   
			height:24,
			width: "25%",
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
					graph.unpivot();
					
					/* if there are no more prev ids to unpivot on (reached bottom of stack), 
						disable the unpivot button */
					if (but && graph.previousPivotIds.length == 0) {
						but.disable();
					}

				} // if node
			} // handler
		}); // unpivot

		var unhide = Ext.create("Ext.Button", {
			text:'UNHIDE', 
			disabled:true, 
			id: config.id + '-UNHIDE',
			margin:4,   
			height:24,
			width: "25%",
			handler:function(but)
			{
				var path = but.id.split('-');
				var graphId = path[0] + '-' + path[1];
				var graph = Ext.getCmp(graphId);
				if (graph) {
					graph.GraphVis.showAll();
					but.setDisabled(true);
				} // if graph
			} // handler
		}); // unhide

		var expand = Ext.create("Ext.Button", {
		    text:'EXPAND',
		    id: config.id+'-EXPAND',
		    //disabled:false,
		    margin:4,   
		    height:24,
			width: "25%",
		    handler: function(but) {
				var path = but.id.split('-');
				var graphId = path[0] + '-' + path[1];
				var graph = Ext.getCmp(graphId);
				if (graph) {
					var nodes = graph.GraphVis.gv.$("node:selected");
					if (nodes.length > 0) {
						but.disable();
						nodes.each(function(i, n) {
							graph.expand(n);
						});
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
				}
		    }
		}); // expand

		var unexpand = Ext.create("Ext.Button", {
		    text:'UNEXPAND',
		    id: config.id+'-UNEXPAND',
		    //disabled:false,
		    margin:4,   
		    height:24,
			width: "25%",
		    handler: function(but) {
				var path = but.id.split('-');
				var graphId = path[0] + '-' + path[1];
				var graph = Ext.getCmp(graphId);
				if (graph) {
					var nodes = graph.GraphVis.gv.$("node:selected");
					if (nodes.length > 0) {
						but.disable();
						nodes.each(function(i, n) {
							graph.unexpand(n);
						});
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
				}
		    }
		}); // unexpand
		
		var show = Ext.create("Ext.Button", {
			text:'SHOW',
			id:config.id + '-SHOW',
			disabled:true,
			margin:4,   
			height:24,
			width: "25%",
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
					Ext.Msg.alert("Warning", "You must first select one or more nodes before you can create its tab");
				}
		    }
		}); // show

		var stop = Ext.create("Ext.Button", {
			text: "HALT",
			id: config.id + '-STOP',
			disabled: true,
			margin: 4,
			height: 24,
			width: "25%",
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
		}); // stop
		
		var deleteBtn = Ext.create("Ext.Button", {
			text: "DELETE",
			id: config.id + "-DELETE",
			//disabled: true,
			margin: 4,
			height: 24,
			width: "25%",
			handler: function(btn) {
				var path = btn.id.split('-');
				var graphId = path[0] + '-' + path[1];
				var graph = Ext.getCmp(graphId);
				if (graph) {
					var nodes = graph.GraphVis.gv.$("node:selected");
					if (nodes.length > 0) {
						Ext.Msg.confirm("Warning", "Deleting selected nodes will remove them from the graph entirely.  Are you sure?", function(btn) {
							if (btn == "yes") {
								graph.GraphVis.deleteNodes(nodes);
							}
						});
					} else {
						Ext.Msg.alert("Warning", "You must first select one or more nodes before you can delete them.");
					}
				}
			}
		}); // delete
		
		var unmerge = Ext.create("Ext.Button", {
			text: "UNMERGE",
			id: config.id + "-UNMERGE",
			margin: 4,
			height: 24,
			width: "25%",
			handler: function(btn) {
				var path = btn.id.split('-');
				var graphId = path[0] + '-' + path[1];
				var graph = Ext.getCmp(graphId);
				if (graph) {
					var nodes = graph.GraphVis.gv.$("node:selected");
					//nodes.each(function(i, n) {
					//	graph.unmergeNode(n);
					//});
					graph.givePromptToUnmerge(nodes);
				}
			}
		}); // unmerge
		
		var help = Ext.create("Ext.Button", {
		    icon: Config.helpIcon,
		    maxHeight: 30,
		    scale: 'medium',
		    margin: 2,
		    style: {marginTop: '2px'},
		    handler: function() {
			    Ext.Msg.alert(
			       'Help',
				   "<h2 style = 'text-align:center; vertical-align:middle;'>Graph Help</h2>" +
				   "<p><b>Pan</b>: Click and hold left mouse button in graph's whitespace, then wait briefly for a gray circle to appear around the cursor. " + 
				   "While still holding the left mouse button, drag the cursor to pan the graph.</p>" +
				   "<p><b>Box-Select</b>: Click and hold the mouse button in graph's whitespace, then immediately drag the cursor to draw a box selector. " + 
				   "Upon letting go of the left mouse button, any nodes within the box will be selected.</p>" +
				   "<p><i>Note</i>: Selected nodes can be dragged and repositioned.</p>" +
				   "<p><b>Zoom In/Out</b>: Roll the mousewheel up or down to zoom in or out.</p>" + 
				   "<br>" +
				   "<h2 style = 'text-align:center; vertical-align:middle;'>Graph Actions</h2>" +
				   "<p><b>Pivot</b>: Designate the selected node as the new root and display its graph with PIVOT. UNPIVOT will reset this action.</p>" +
				   "<p><b>Hide</b>: Remove the selected nodes and their connected edges from view with HIDE. UNHIDE resets this action, making everything visible.</p>" +
				   "<p><b>Expanding</b>: Add the neighbors, if any, of the selected node(s) to the graph with EXPAND.  UNEXPAND deletes the newly discovered neighbors.</p>" +
				   "<p><b>Show</b>: Creates a new tab for any of the selected entities.</p>" +
				   "<p><b>Halt</b>: Stop a procedurally-generated layout in process.</p>" +
				   "<p><b>Delete</b>: Permanently removes selected nodes and their attached edges from the graph.  UNHIDE will <i>not</i> bring them back.</p>"
			    );
			}
		});
		
		var actionButtonsLine1 = Ext.create("Ext.form.FieldSet", { 
			border: 0,
			maxHeight: 40,
			padding: 0,
			margin: 0,
			items:[{
				xtype:'fieldcontainer',
				height:'auto',
				width: '100%',
				items:[pivot, unpivot, hide, unhide]
			}]
		});
		
		var actionButtonsLine2 = Ext.create("Ext.form.FieldSet", { 
			border: 0,
			maxHeight: 40,
			padding: 0,
			margin: 0,
			items:[{
				xtype:'fieldcontainer',
				height:'auto',
				width: '100%',
				items:[expand, unexpand, show, stop]
			}]
			
		});
		
		var actionButtonsLine3 = Ext.create("Ext.form.FieldSet", {
			border: 0,
			maxHeight: 40,
			padding: 0,
			margin: 0,
			items: [{
				xtype: 'fieldcontainer',
				height: 'auto',
				width: '100%',
				items: [deleteBtn, unmerge, help]
			}]
		});

		this.items = [
			actionButtonsLine1,
			actionButtonsLine2,
			actionButtonsLine3
		];

		this.getPivotButton = function() { return pivot; };
		this.getUnPivotButton = function() { return unpivot; };
		this.getHideButton = function() { return hide; };
		this.getUnHideButton = function() { return unhide; };
		this.getExpandButton = function() { return expand; };
		this.getUnExpandButton = function() { return unexpand; };
		this.getShowButton = function() { return show; };
		this.getStopButton = function() { return stop; };
		this.getDeleteButton = function() { return deleteBtn; };
		this.getUnmergeButton = function() { return unmerge; };
		this.getHelpButton = function() { return help; };
		
		this.callParent(arguments);
	} // constructor

});  

