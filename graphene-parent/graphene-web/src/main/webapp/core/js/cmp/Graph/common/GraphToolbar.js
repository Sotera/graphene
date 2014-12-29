Ext.define("DARPA.GraphToolbar", {
	extend : "Ext.toolbar.Toolbar",
	height : 33,
	border : false,
	// margin: "0, 2, 0, 10",

	constructor : function(config) {

		var scope = this;

		var myWidth = 30;
		var myHeight = 30;

		var saveToServer = {
			xtype : "button",
			text : "Store", // FIXME: might need a new name
			height : myHeight,
			handler : function(item) {
				var gr = item.up().up();

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
					},
					failure : function(resp) {
						console.log("Persist POST failure.");
					}
				});
			}
		};

		var save_graph = {
			xtype : 'button',
			text : "Save",
			height : myHeight,
			handler : (typeof config.saveHandler !== "undefined") ? 
				config.saveHandler : function(item) {
					var gr = item.up().up();
					gr.saveGraph();
				}
		};

		var load_graph = {
			xtype : 'button',
			text : "Load",
			height : myHeight,
			handler : (typeof config.loadHandler !== "undefined") ? 
				config.loadHandler : function(item) {
					var gr = item.up().up();

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

		var import_graph = {
			xtype : 'button',
			text : "Import",
			height : myHeight,
			handler : (typeof config.importHandler !== "undefined") ? 
				config.importHandler : function(item) {
					var gr = item.up().up();

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
			xtype : 'button',
			text : "Export",
			height : myHeight,
			handler : (typeof config.exportHandler !== "undefined") ? 
				config.exportHandler : function(item) {
					var gr = item.up().up();

					gr.exportGraph();
				}
		};

		var grid_default = {
			xytpe : 'button',
			height : myHeight,
			width : myWidth,
			tooltip : "Grid Layout",
			toggleGroup : config.id + "-toolbarLayouts",
			cls : "grid_default_btn",
			handler : (typeof config.layout_grid_default !== "undefined") ? 
				config.layout_grid_default : function(item) {
					var gr = item.up().up();

					gr.GraphVis.changeLayout("grid", {
						// layout config options go here
					});
				}
		};

		var hierarch_circle = {
			xytpe : 'button',
			height : myHeight,
			width : myWidth,
			tooltip : "Hierarchy layout - Circle",
			toggleGroup : config.id + "-toolbarLayouts",
			cls : "hierarch_circle_btn",
			handler : (typeof config.layout_heirarch_circle !== "undefined") ? 
				config.layout_heirarch_circle : function(item) {
					var gr = item.up().up();

					gr.GraphVis.changeLayout("breadthfirst", {
						circle : true
					});
				}
		};

		var hierarch_default = {
			xytpe : 'button',
			height : myHeight,
			width : myWidth,
			pressed : true, // <-- because Hierarchy is currently default layout
			tooltip : "Hierarchy layout - Default",
			toggleGroup : config.id + "-toolbarLayouts",
			cls : "hierarch_default_btn",
			handler : (typeof config.layout_hierarch_default !== "undefined") ? 
				config.layout_hierarch_default : function(item) {
					var gr = item.up().up();

					gr.GraphVis.changeLayout("breadthfirst", {
						// layout config options go here
					});
				}
		};

		var cose_anim = {
			xytpe : 'button',
			height : myHeight,
			width : myWidth,
			tooltip : "COSE layout (animated)",
			toggleGroup : config.id + "-toolbarLayouts",
			cls : "cose_anim_btn",
			handler : (typeof config.layout_cose_anim !== "undefined") ? 
				config.layout_cose_anim : function(item) {
					var gr = item.up().up();

					gr.GraphVis.changeLayout("cose", {
						// layout config options go here
					});
				}
		};

		var cose_unanim = {
			xytpe : 'button',
			height : myHeight,
			width : myWidth,
			tooltip : "COSE layout (unanimated)",
			toggleGroup : config.id + "-toolbarLayouts",
			cls : "cose_unanim_btn",
			handler : (typeof config.layout_cose_unanim !== "undefined") ? 
				config.layout_cose_unanim : function(item) {
					var gr = item.up().up();

					gr.GraphVis.changeLayout("cose", {
						refresh : 0
					});
				}
		};

		var arbor_anim = {
			xytpe : 'button',
			height : myHeight,
			width : myWidth,
			tooltip : "Arbor layout (animated)",
			toggleGroup : config.id + "-toolbarLayouts",
			cls : "arbor_anim_btn",
			handler : (typeof config.layout_arbor_anim !== "undefined") ? 
				config.layout_arbor_anim : function(item) {
					var gr = item.up().up();

					gr.GraphVis.changeLayout("arbor-snow", {
						liveUpdate : true
					});
				}
		};

		var arbor_unanim = {
			xytpe : 'button',
			height : myHeight,
			width : myWidth,
			tooltip : "Arbor layout (unanimated)",
			toggleGroup : config.id + "-toolbarLayouts",
			cls : "arbor_unanim_btn",
			handler : (typeof config.layout_arbor_unanim !== "undefined") ? 
				config.layout_arbor_unanim : function(item) {
					var gr = item.up().up();

					gr.GraphVis.changeLayout("arbor-snow", {
						liveUpdate : false
					});
				}
		};

		var arbor_wheel_anim = {
			xytpe : 'button',
			height : myHeight,
			width : myWidth,
			tooltip : "Arbor Wheel layout (animated)",
			toggleGroup : config.id + "-toolbarLayouts",
			cls : "arbor_wheel_anim_btn",
			handler : (typeof config.layout_arbor_wheel_anim !== "undefined") ? 
				config.layout_arbor_wheel_anim : function(item) {
					var gr = item.up().up();

					gr.GraphVis.changeLayout("arbor-wheel", {
						liveUpdate : true
					});
				}
		};

		var arbor_wheel_unanim = {
			xytpe : 'button',
			height : myHeight,
			width : myWidth,
			tooltip : "Arbor Wheel layout (unanimated)",
			toggleGroup : config.id + "-toolbarLayouts",
			cls : "arbor_wheel_unanim_btn",
			handler : (typeof config.layout_arbor_wheel_unanim !== "undefined") ? 
				config.layout_arbor_wheel_unanim : function(item) {
					var gr = item.up().up();

					gr.GraphVis.changeLayout("arbor-wheel", {
						liveUpdate : false
					});
				}
		};

		var inst_label = {
			xtype : 'label',
			text : "INSTITUTION: ",
			disabled : false,
			// margin: "2 2 2 475",
			margin : "2 2 2 175",
			listeners : {
				beforerender : function(self, opts) {
					var toolbar = self.up();
					self.setText("INSTITUTION: " + toolbar.institution);
				}
			}
		};

		var snap_btn = {
			xtype : 'button',
			height : myHeight,
			text : "Snap To Graph",
			tooltip : "Snap the graph back into view.  Layout is preserved.",
			handler : (typeof config.reset == "function") ? 
				config.reset : function(item) {
					var gr = item.up().up();
					gr.GraphVis.gv.fit();
				}
		};
		
		var regenerateFromServer = {
			xtype: "button",
			height: myHeight,
			text: "Regenerate Graph",
			tooltip: "Undoes any persisted changes to this graph and returns what is in the data store",
			handler: function(item) {
				var gr = item.up().up();
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

		var help_btn = {
			xtype : 'button',
			height : myHeight,
			width : myWidth,
			cls : "toolbar_help_btn",
			handler : (typeof config.help !== "undefined") ? 
				config.help : function(item) {
					Ext.Msg.alert(
						"Toolbar Help",
						"<b>Store</b> - Persist any changes you have made so that any future similar queries will have these changes by default." + "<br>" + 
						"<b>Save</b> - Save the state of your session <i>to a cookie</i> to be loaded exactly as-is in a future session." + "<br>" + 
						"<b>Load</b> - Load a saved session to replace all the queries and graph state of your current session." + "<br>" + 
						"<b>Import</b> - Import a JSONized graph to replace the graph currently displayed." + "<br>" + 
						"<b>Export</b> - Export your current graph as a .PNG image or a .JSON file for use elsewhere." + "<br>" + 
						"<b>Snap To Graph</b> - Automatically pans back to the graph, setting it to fit the display without adjusting the existing layout." + "<br>" +
						"<b>Regenerate Graph</b> - Fetches the graph directly from the database without applying any existing user adjustments." + "<br>" +
						"<hr>" +
						/* Grid layout help */
						"<img src='" + Config.coreImagesUrl + "grid_icon.png'></img>" + "<b>Grid Layout</b> - Place the nodes in an evenly spaced grid to fill the display bounds." + "<br>" +
						/* Hierarchy layout help */
						"<img src='" + Config.coreImagesUrl + "hierarchy_icon.png'></img><img src='" + Config.coreImagesUrl + "hierarchy_circle_icon.png'></img>" + 
						"<b>Hierarchy</b> - The default layout puts nodes in a hierarchy, based on a breadthfirst traversal of the graph. The circular hierarchy layout positions nodes in concentric circles, based on an internal metric to segregate the nodes into levels." + "<br>" +
						/* COSE layout help */
						"<img src='" + Config.coreImagesUrl + "cose_icon.png'></img><img src='" + Config.coreImagesUrl + "cose_animated_icon.png'></img>" + 
						"<b>COSE</b> - (Compound Spring Embedder) a force-directed simulation to lay out compound graphs." + "<br>" +
						/* Arbor (default) layout help */
						"<img src='" + Config.coreImagesUrl + "arbor_icon.png'></img><img src='" + Config.coreImagesUrl + "arbor_animated_icon.png'></img>" + 
						"<b>Arbor (default)</b> - Uses a force-directed physics simulation to evenly space out nodes into clusters." + "<br>" +
						/* Arbor (wheel) layout help */
						"<img src='" + Config.coreImagesUrl + "arbor_wheel_icon.png'></img><img src='" + Config.coreImagesUrl + "arbor_wheel_animated_icon.png'></img>" + 
						"<b>Arbor (wheel)</b> - Exactly the same as the default Arbor implementation, but evenly distributes leaf nodes around their parents in an even, circular fashion." + "<br>" +
						
						"Note: this background denotes that the layout is animated:&nbsp;&nbsp;" + "<img src='" + Config.coreImagesUrl + "animated_template.png'></img>"
					).doComponentLayout();
				}
		};

		this.items = [
			saveToServer, load_graph, import_graph, export_graph, 
			"-", 
			grid_default,
			"-", 
			hierarch_default, hierarch_circle, 
			"-",
			cose_anim, cose_unanim, 
			"-", 
			arbor_anim, arbor_unanim, arbor_wheel_anim, arbor_wheel_unanim, 
			"-", 
			snap_btn, regenerateFromServer, inst_label,
			"->", 
			help_btn 
		];

		this.callParent(arguments);
	},

	getPersistBtn : function() {
		return this.items.items[0];
	},

	getSaveBtn : function() {
		return this.items.items[1];
	},

	getLoadBtn : function() {
		return this.items.items[2];
	},

	getImportBtn : function() {
		return this.items.items[3];
	},

	getExportBtn : function() {
		return this.items.items[4];
	},

	getGridBtn : function() {
		return this.items.items[6];
	},

	getHierarchyBtn : function() {
		return this.items.items[8];
	},

	getHierarchyCircleBtn : function() {
		return this.items.items[9];
	},

	getCoseAnimBtn : function() {
		return this.items.items[11];
	},

	getCoseBtn : function() {
		return this.items.items[12];
	},

	getArborAnimBtn : function() {
		return this.items.items[14];
	},

	getArborBtn : function() {
		return this.items.items[15];
	},

	getArborWheelAnimBtn : function() {
		return this.items.items[16];
	},

	getArborWheelBtn : function() {
		return this.items.items[17];
	},

	getSnapBtn: function() {
		return this.items.items[19];
	},
	
	getRefreshBtn: function() {
		return this.items.items[20];
	},
	
	getLabel : function() {
		return this.items.items[21];
	},

	getHelpBtn : function() {
		return this.items.items[23];
	}
});