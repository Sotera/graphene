Ext.define("DARPA.GraphLayoutMenu", {
	extend: "Ext.menu.Menu",
	
	graphRef: undefined,
	
	constructor: function(config) {
		var scope = this;
		
		scope.items = [{
			text: "Circular Layouts",
			menu: [{
				text: "Default",
				handler: function(item) {
					var gr;
					if (scope.graphRef) {
						gr = scope.graphRef;
					} else {
						var menu = item.parentMenu.parentMenu;
						var toolbar = menu.up().up();
						gr = toolbar.up();
					}
					
					gr.GraphVis.changeLayout('circle', {
						// layout config options go here
					});
				} // END DEFAULT CIRCULAR LAYOUT
			}] // END CIRCULAR LAYOUT MENU
		}, {
			text: "Grid Layouts",
			menu: [{
				text: "Default",
				handler: function(item) {
					var gr;
					if (scope.graphRef) {
						gr = scope.graphRef;
					} else {
						var menu = item.parentMenu.parentMenu;
						var toolbar = menu.up().up();
						gr = toolbar.up();
					}
					
					gr.GraphVis.changeLayout('grid', {
						// layout config options go here
					});
				} // END DEFAULT GRID LAYOUT
			}] // END GRID LAYOUT MENUS
		}, {
			text: "Heirarchical Layouts",
			menu: [{
				text: "Breadth-First",
				handler: function(item) {
					var gr;
					if (scope.graphRef) {
						gr = scope.graphRef;
					} else {
						var menu = item.parentMenu.parentMenu;
						var toolbar = menu.up().up();
						gr = toolbar.up();
					}
					
					gr.GraphVis.changeLayout('breadthfirst', {
						// layout config options go here
					});
				} // END BREADTH-FIRST HEIRARCHY LAYOUT
			}, {
				text: "Circles",
				handler: function(item) {
					var gr;
					if (scope.graphRef) {
						gr = scope.graphRef;
					} else {
						var menu = item.parentMenu.parentMenu;
						var toolbar = menu.up().up();
						gr = toolbar.up();
					}
					
					gr.GraphVis.changeLayout('breadthfirst', {
						// layout config options go here
						circle: true
					});
				} // END CIRCULAR HEIRARCHY LAYOUT
			}] // END HEIRARCHICAL LAYOUT MENUS
		}, {
			text: "Force Directed Layouts",
			menu: [{
				text: "Arbor",
				menu: [{
					text: "Snowflake (anim.)",
					handler: function(item) {
						var gr;
						if (scope.graphRef) {
							gr = scope.graphRef;
						} else {
							var menu = item.parentMenu.parentMenu.parentMenu;
							var toolbar = menu.up().up();
							gr = toolbar.up();
						}
						
						gr.GraphVis.changeLayout('arbor-snow', {
							// layout config options go here
							liveUpdate: true
						});
					} // END ARBOR SNOWFLAKE ANIMATED
				}, {
					text: "Snowflake (unanim.)",
					handler: function(item) {
						var gr;
						if (scope.graphRef) {
							gr = scope.graphRef;
						} else {
							var menu = item.parentMenu.parentMenu.parentMenu;
							var toolbar = menu.up().up();
							gr = toolbar.up();
						}
						
						gr.GraphVis.changeLayout('arbor-snow', {
							// layout config options go here
							liveUpdate: false
						});
					} // END ARBOR SNOWFLAKE UNANIMATED
				}, {
					text: "Wheel (anim.)",
					handler: function(item) {
						var gr;
						if (scope.graphRef) {
							gr = scope.graphRef;
						} else {
							var menu = item.parentMenu.parentMenu.parentMenu;
							var toolbar = menu.up().up();
							gr = toolbar.up();
						}
						
						gr.GraphVis.changeLayout('arbor-wheel', {
							// layout config options go here
							liveUpdate: true
						});
					} // END ARBOR WHEEL ANIMATED
				}, {
					text: "Wheel (unanim.)",
					handler: function(item) {
						var gr;
						if (scope.graphRef) {
							gr = scope.graphRef;
						} else {
							var menu = item.parentMenu.parentMenu.parentMenu;
							var toolbar = menu.up().up();
							gr = toolbar.up();
						}
						
						gr.GraphVis.changeLayout('arbor-wheel', {
							// layout config options go here
							liveUpdate: false
						});
					} // END ARBOR WHEEL UNANIMATED
				}] // END ARBOR LAYOUT MENU
			}, {
				text: "COSE",
				menu: [{
					text: "Default (anim.)",
					handler: function(item) {
						var gr;
						if (scope.graphRef) {
							gr = scope.graphRef;
						} else {
							var menu = item.parentMenu.parentMenu.parentMenu;
							var toolbar = menu.up().up();
							gr = toolbar.up();
						}
						
						gr.GraphVis.changeLayout('cose', {
							// layout config options go here
						});
					} // END COSE DEFAULT UNANIMATED
				}, {
					text: "Default (unanim.)",
					handler: function(item) {
						var gr;
						if (scope.graphRef) {
							gr = scope.graphRef;
						} else {
							var menu = item.parentMenu.parentMenu.parentMenu;
							var toolbar = menu.up().up();
							gr = toolbar.up();
						}
						
						gr.GraphVis.changeLayout('cose', {
							// layout config options go here
							refresh: 0
						});
					} // END COSE DEFAULT UNANIMATED
				}] // END COSE LAYOUT MENU
			}] // END FORCE-DIRECTED LAYOUT MENUS
		}]; // END ITEMS
		
		scope.callParent(arguments);
	}
});