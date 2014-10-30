Ext.define("DARPA.GraphManagerMenu", {
	extend: "Ext.menu.Menu",
	
	graphRef: undefined,
	
	constructor: function(config) {
		var scope = this;
		
		scope.items = [{
			text: "Export Graph",
			handler: function(item) {
				var gr;
				if (scope.graphRef) {
					gr = scope.graphRef;
				} else {
					var menu = item.parentMenu;
					var toolbar = menu.up().up();
					gr = toolbar.up();
				}
				
				gr.exportGraph();
								
				// DRAPER API - this one uses the workflow type of REPORT
				// Send a User Activity Message with optional metadata
				//activityLogger.logUserActivity('Export Graph', <Graph Name Tab>, activityLogger.WF_REPORT, 
				//    {'Tab':<Graph Name>, 'action': 'Export', 'selectedNode': 'NA' });
			}
		}, {
			text: "Import Graph",
			handler: function(item) {
				var gr;
				if (scope.graphRef) {
					gr = scope.graphRef;
				} else {
					var menu = item.parentMenu;
					var toolbar = menu.up().up();
					gr = toolbar.up();
				}
				
				Ext.Msg.confirm('Confirm', 
					'Importing another graph will delete the current graph. Are you sure you want to Import?', 
					function(ans) {
						if (ans == 'yes') {
							gr.importGraph();
						}
					}
				);
			}
		}, {
			text: "Save Graph",
			handler: function(item) {
				var gr;
				if (scope.graphRef) {
					gr = scope.graphRef;
				} else {
					var menu = item.parentMenu;
					var toolbar = menu.up().up();
					gr = toolbar.up();
				}
				
				gr.saveGraph();

				// DRAPER API - this one uses the workflow type of REPORT
				// Send a User Activity Message with optional metadata
				//activityLogger.logUserActivity('Save Graph', <Graph Name Tab>, activityLogger.WF_REPORT, 
				//    {'Tab':<Graph Name>, 'action': 'Save', 'selectedNode': 'NA' });
			}
		}, {
			text: "Restore Graph",
			handler: function(item) {
				var gr;
				if (scope.graphRef) {
					gr = scope.graphRef;
				} else {
					var menu = item.parentMenu;
					var toolbar = menu.up().up();
					gr = toolbar.up();
				}
				
				Ext.Msg.confirm('Confirm', 
					'Restoring a saved graph will delete the current graph. Are you sure you want to Restore?', 
					function(ans) {
						if (ans == 'yes') {
							gr.restoreGraph(null);
						}
					}
				);
			}
		}];
		
		scope.callParent(arguments);
	}
});