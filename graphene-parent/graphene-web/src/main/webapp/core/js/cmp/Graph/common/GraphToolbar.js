Ext.define("DARPA.GraphToolbar", {
	extend : "Ext.toolbar.Toolbar",
	height : 33,
	border : false,
	// margin: "0, 2, 0, 10",

	constructor : function(config) {
		var scope = this;
		
		var myWidth = 30;
		var myHeight = 30;
	
		var server_save_btn = {
			id: config.id + "-SERVER-SAVE",
			text: "To Server",
			height: myHeight,
			tooltip: "Persist any changes you have made so that any similar queries will have these changes by default",
			handler: function(item) {
				var gr = scope.up();

				// TODO move functionality to AbstractGraphPanel
				var json = gr.GraphVis.exportGraph().graph;
				json.legend = gr.legendJSON;
				
				Ext.Ajax.request({
					method : "POST",
					url : Config.entityGraphCSUrl + "save",
					headers: {
						"Content-Type" : "application/json"
					},
					params : {
						"seed" : gr.prevLoadParams.value,
						"username" : "unknownClient",
						"timestamp" : new Date().getTime()
					},
					jsonData: Ext.encode(json),
					scope : gr,
					success : function(resp) {
						console.log("Persist POST success. " + resp.responseText);
						
						var pb = scope.up().getProgressBar();
						if (pb) pb.updateProgress(1, "Graph state persisted");
					},
					failure : function(resp) {
						console.log("Persist POST failure.");
						
						var pb = scope.up().getProgressBar();
						if (pb) pb.updateProgress(0, "Error in persisting graph state");
					}
				});
			}
		};
		
		var local_save_btn = {
			id: config.id + "-LOCAL-SAVE",
			text: "To Browser Cookie",
			tooltip: "Save the current graph state to a browser cookie so it can be loaded on this machine later",
			height: myHeight,
			handler: function(item) {
				var gr = scope.up();
				gr.saveGraph();
			}
		};
		
		var server_load_changes_btn = {
			id: config.id + "-SERVER-NOCHANGES-LOAD",
			text: "From Server (with changes)",
			tooltip: "Re-loads graph with all of its persisted changes",
			height: myHeight,
			handler: function(item) {
				var gr = scope.up();
				var id = gr.prevLoadParams.searchValue;
				Ext.Msg.confirm(
					"Confirm",
					'Regenerating will discard any changes you have made to the graph. Are you sure you want to Regenerate?',
					function(ans) {
						if (ans == 'yes') {
							gr.load(id, true);
						}
					}
				);
			}
		};
		
		var server_load_no_changes_btn = {
			id: config.id + "-SERVER-LOAD",
			text: "From Server (without changes)",
			tooltip: "Re-loads graph without any persisted changes and returns what is in the data store",
			height: myHeight,
			handler: function(item) {
				var gr = scope.up();
				var id = gr.prevLoadParams.searchValue;
				Ext.Msg.confirm(
					'Confirm',
					'Regenerating will discard any changes you have made to the graph. Are you sure you want to Regenerate?',
					function(ans) {
						if (ans == 'yes') {
							gr.load(id, false);
						}
					}
				);
			}
		};
		
		var local_load_btn = {
			id: config.id + "-LOCAL-LOAD",
			text: "From Browser Cookies",
			tooltip: "Load any existing graph states saved to this browser",
			height: myHeight,
			handler: function(item) {
				var gr = scope.up();
				Ext.Msg.confirm(
					'Confirm',
					'Restoring a saved graph will delete the current graph. Are you sure you want to Restore?',
					function(ans) {
						if (ans == 'yes') {
							gr.restoreGraph(null);
						}
					}
				);
			}
		};
	
		var save_menu = {
			text: "Save",
			menu: {
				xtype: "menu",
				items: [server_save_btn, local_save_btn],
				listeners: {
					'mouseleave': function(menu, e, eOpts) {
						menu.hide();
					}
				}
			}
		};
		
		var load_menu = {
			text: "Load",
			menu: {
				xtype: "menu",
				items: [server_load_changes_btn, server_load_no_changes_btn, local_load_btn],
				listeners: {
					'mouseleave': function(menu, e, eOpts) {
						menu.hide();
					}
				}
			}
		};
	
		var import_graph = {
			xtype: "button",
			id: config.id + "-IMPORT",
			text: "Import",
			tooltip: "Import a previously exported graph .JSON file to be rendered",
			height: myHeight,
			handler: function(item) {
				var gr = scope.up();
				Ext.Msg.confirm(
					'Confirm',
					'Importing another graph will delete the current graph. Are you sure you want to Import?',
					function(ans) {
						if (ans == 'yes') {
							gr.importGraph();
						}
					}
				);
			}
		};
		var export_graph = {
			xtype: "button",
			id: config.id + "-EXPORT",
			text: "Export",
			tooltip: "Export the existing graph as a .JSON file or as a .PNG image",
			height: myHeight,
			handler: function(item) {
				var gr = scope.up();
				gr.exportGraph();
			}
		};
		
		var grid_default = {
			xtype: "button",
			id: config.id + "-GRID-DEFAULT",
			height: myHeight,
			width: myWidth,
			tooltip: "Grid Layout",
			toggleGroup : config.id + "-toolbarLayouts",
			cls: "grid_default_btn",
			handler: function(item) {
				var gr = scope.up();

				gr.GraphVis.changeLayout("grid", {
					// layout config options go here
				});
			}
		};
		
		var hierarch_default = {
			xtype: "button",
			id: config.id + "-HIERARCH-DEFAULT",
			height: myHeight,
			width: myWidth,
			tooltip: "Hierarchy layout - default",
			toggleGroup : config.id + "-toolbarLayouts",
			cls : "hierarch_default_btn",
			handler: function(item) {
				var gr = scope.up();

				gr.GraphVis.changeLayout("breadthfirst", {
					// layout config options go here
				});
			}
		};
		
		var hierarch_circle = {
			xtype: "button",
			id: config.id + "-HIERARCH-CIRCLE",
			height: myHeight,
			width: myWidth,
			tooltip : "Hierarchy layout - circle",
			toggleGroup : config.id + "-toolbarLayouts",
			cls : "hierarch_circle_btn",
			handler: function(item) {
				var gr = scope.up();

				gr.GraphVis.changeLayout("breadthfirst", {
					circle : true
				});
			}
		};
		
		var cose_anim = {
			xtype: "button",
			id: config.id + "-COSE-ANIM",
			height: myHeight,
			width : myWidth,
			tooltip: "COSE layout (animated)",
			toggleGroup : config.id + "-toolbarLayouts",
			cls: "cose_anim_btn",
			handler: function(item) {
				var gr = scope.up();

				gr.GraphVis.changeLayout("cose", {
					// layout config options go here
				});
			}
		};
		
		var cose_unanim = {
			xtype: "button",
			id: config.id + "-COSE-UNANIM",
			height: myHeight,
			width : myWidth,
			tooltip: "COSE layout (unanimated)",
			toggleGroup : config.id + "-toolbarLayouts",
			cls: "cose_unanim_btn",
			handler: function(item) {
				var gr = scope.up();

				gr.GraphVis.changeLayout("cose", {
					refresh : 0
				});
			}
		};
		
		var arbor_anim = {
			xtype: "button",
			id: config.id + "-ARBOR-ANIM",
			height: myHeight,
			width : myWidth,
			tooltip: "Arbor layout (animated)",
			toggleGroup : config.id + "-toolbarLayouts",
			cls: "arbor_anim_btn",
			handler: function(item) {
				var gr = scope.up();

				gr.GraphVis.changeLayout("arbor-snow", {
					liveUpdate : true
				});
			}
		};
		
		var arbor_unanim = {
			xtype: "button",
			id: config.id + "-ARBOR-UNANIM",
			height: myHeight,
			width : myWidth,
			tooltip: "Arbor layout (unanimated)",
			toggleGroup : config.id + "-toolbarLayouts",
			cls: "arbor_unanim_btn",
			handler: function(item) {
				var gr = scope.up();
				
				gr.GraphVis.changeLayout("arbor-snow", {
					liveUpdate : false
				});
			}
		};
		
		var arbor_wheel_anim = {
			xtype: "button",
			id: config.id + "-ARBOR-WHEEL-ANIM",
			height: myHeight,
			width : myWidth,
			tooltip: "Arbor Wheel layout (animated)",
			toggleGroup : config.id + "-toolbarLayouts",
			cls: "arbor_wheel_anim_btn",
			handler: function(item) {
				var gr = scope.up();
				
				gr.GraphVis.changeLayout("arbor-wheel", {
					liveUpdate : true
				});
			}
		};
		
		var arbor_wheel_unanim = {
			xtype: "button",
			id: config.id + "-ARBOR-WHEEL-UNANIM",
			height: myHeight,
			width : myWidth,
			tooltip: "Arbor Wheel layout (unanimated)",
			toggleGroup : config.id + "-toolbarLayouts",
			cls: "arbor_wheel_unanim_btn",
			handler: function(item) {
				var gr = scope.up();
				
				gr.GraphVis.changeLayout("arbor-wheel", {
					liveUpdate : false
				});
			}
		};
		
		var inst_label = {
			xtype: 'label',
			id: config.id + "-LABEL",
			text: "INSTITUTION: ",
			disabled: false,
			margin: "2 2 2 175",
			listeners: {
				beforerender: function(self, opts) {
					var toolbar = self.up();
					self.setText("INSTITUTION: " + toolbar.institution);
				}
			}
		};
		
		var snap_btn = {
			xtype: "button",
			id: config.id + "-SNAP",
			text: "Snap To Graph",
			tooltip: "Snap the graph back into view.  Layout is preserved.",
			height: myHeight,
			handler: function(item) {
				var gr = scope.up();
				gr.GraphVis.gv.fit();
			}
		};
		
		var help_btn = {
			xtype: "button",
			id: config.id + "-HELP",
			height: myHeight,
			width: myWidth,
			cls: "toolbar_help_btn",
			handler: function(item) {
				Ext.Msg.alert(
					"Toolbar Help",
					"<b>Save</b>" +
					"<ul>" + 
						"<li>" + "<i>To Server</i> - Persist any changes you have made so that any similar queries will have these changes by default." + "</li>" +
						"<li>" + "<i>To Browser Cookie</i> - Save the state of your session to be loaded exactly as-is in a future session." + "</li>" +
					"</ul>" +
					"<b>Load</b>" +
					"<ul>" + 
						"<li>" + "<i>From Server (with changes)</i> - Fetches the graph directly from the database and automatically applies any previous user adjustments." + "</li>" +
						"<li>" + "<i>From Server (without changes)</i> - Fetches the graph directly from the database without applying any existing user adjustments." + "</li>" +
						"<li>" + "<i>From Browser Cookies</i> - Load a saved session to replace the graph state of your current session." + "</li>" +
					"</ul>" +
					"<b>Import</b> - Import a JSONized graph to replace the graph currently displayed." + "<br>" + 
					"<b>Export</b> - Export your current graph as a .PNG image or a .JSON file for use elsewhere." + "<br>" + 
					"<b>Snap To Graph</b> - Automatically pans back to the graph, setting it to fit the display without adjusting the existing layout." + "<br>" +
					"<hr>" +
					/* Grid layout help */
					"<img src='" + Config.coreImagesUrl + "grid_icon.png'></img>" + "<b>Grid Layout</b> - Place the nodes in an evenly spaced grid to fill the display bounds." + "<br>" +
					/* Hierarchy layout help */
					"<img src='" + Config.coreImagesUrl + "hierarchy_icon.png'></img>" + 
					"<b>Hierarchy</b> - The default layout puts nodes in a hierarchy, based on a breadthfirst traversal of the graph. The circular hierarchy layout positions nodes in concentric circles, based on an internal metric to segregate the nodes into levels." + "<br>" +
					/* COSE layout help */
					"<img src='" + Config.coreImagesUrl + "cose_icon.png'></img>" + 
					"<b>COSE</b> - (Compound Spring Embedder) a force-directed simulation to lay out compound graphs." + "<br>" +
					/* Arbor (default) layout help */
					"<img src='" + Config.coreImagesUrl + "arbor_icon.png'></img>" + 
					"<b>Arbor (default)</b> - Uses a force-directed physics simulation to evenly space out nodes into clusters." + "<br>" +
					/* Arbor (wheel) layout help */
					"<img src='" + Config.coreImagesUrl + "arbor_wheel_icon.png'></img>" + 
					"<b>Arbor (wheel)</b> - Exactly the same as the default Arbor implementation, but evenly distributes leaf nodes around their parents in an even, circular fashion." + "<br>" +
					
					"Note: this background denotes that the layout is animated:&nbsp;&nbsp;" + "<img src='" + Config.coreImagesUrl + "animated_template.png'></img>"
				).doComponentLayout();
			}
		};
	
		this.items = [
			save_menu, load_menu, import_graph, export_graph, 
			"-", 
			grid_default,
			"-", 
			hierarch_default, hierarch_circle, 
			"-",
			cose_anim, cose_unanim, 
			"-", 
			arbor_anim, arbor_unanim, arbor_wheel_anim, arbor_wheel_unanim, 
			"-", 
			snap_btn, inst_label,
			"->", 
			help_btn
		];
		
		this.callParent(arguments);
	},
	
	getPersistBtn : function() {
		return Ext.getCmp(this.id + "-SERVER-SAVE");
	},

	getSaveBtn : function() {
		return Ext.getCmp(this.id + "-LOCAL-SAVE");
	},
	
	getRefreshBtn: function() {
		return Ext.getCmp(this.id + "-SERVER-CHANGES-LOAD");
	},

	getLoadBtn : function() {
		return Ext.getCmp(this.id + "-LOCAL-LOAD");
	},

	getImportBtn : function() {
		return Ext.getCmp(this.id + "-IMPORT");
	},

	getExportBtn : function() {
		return Ext.getCmp(this.id + "-EXPORT");
	},

	getGridBtn : function() {
		return Ext.getCmp(this.id + "-GRID-DEFAULT");
	},

	getHierarchyBtn : function() {
		return Ext.getCmp(this.id + "-HIERARCH-DEFAULT");
	},

	getHierarchyCircleBtn : function() {
		return Ext.getCmp(this.id + "-HIERARCH-CIRCLE");
	},

	getCoseAnimBtn : function() {
		return Ext.getCmp(this.id + "-COSE-ANIM");
	},

	getCoseBtn : function() {
		return Ext.getCmp(this.id + "-COSE-UNANIM");
	},

	getArborAnimBtn : function() {
		return Ext.getCmp(this.id + "-ARBOR-ANIM");
	},

	getArborBtn : function() {
		return Ext.getCmp(this.id + "-ARBOR-UNANIM");
	},

	getArborWheelAnimBtn : function() {
		return Ext.getCmp(this.id + "-ARBOR-WHEEL-ANIM");
	},

	getArborWheelBtn : function() {
		return Ext.getCmp(this.id + "-ARBOR-WHEEL-UNANIM");
	},

	getSnapBtn: function() {
		return Ext.getCmp(this.id + "-SNAP");
	},
	
	getLabel : function() {
		return Ext.getCmp(this.id + "-LABEL");
	},

	getHelpBtn : function() {
		return Ext.getCmp(this.id + "-HELP");
	},
	
	getEnabledLayoutBtn: function() {
		var btns = [
			this.getGridBtn(), this.getHierarchyBtn(), this.getHierarchyCircleBtn(), 
			this.getCoseAnimBtn(), this.getCoseBtn(), this.getArborAnimBtn(), 
			this.getArborBtn(), this.getArborWheelAnimBtn(), this.getArborWheelBtn()
		];
		
		for (var i = 0; i < btns.length; i++) {
			var btn = btns[i];
			if (btn.pressed) {
				return btn;
			}
		}
		return null;
	}
});