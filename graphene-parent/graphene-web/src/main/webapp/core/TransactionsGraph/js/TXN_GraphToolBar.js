Ext.define("DARPA.TXNGToolbar", {  // allows the user to change the graph layout
    extend: "Ext.toolbar.Toolbar",
    height: 24,
    border: false,
    margin: '0,2,0,10',
    
    constructor:function(config) {
	    this.items = [  
		'-',
		{
		    text: 'File',
		    menu: Ext.create("Ext.menu.Menu", {
			//style: { backgroundColor: "FloralWhite", fontSize: "medium" }, 
			items: [
			    {
				text: "EXPORT GRAPH",
				handler: function() {
				    var gf = getTransactionGraphFrame();
				    var gr = gf.getGraph();                             
				    if (gr) {
					gr.exportGraph();

					// DRAPER API - this one uses the workflow type of REPORT
					// Send a User Activity Message with optional metadata
					//activityLogger.logUserActivity('Export Graph', '<graph tab name>', activityLogger.WF_REPORT, 
					//    {'Tab':'<graph tab>', 'action': 'Export', 'selectedNode': 'NA' });
				    }
				}
			    }, 
			    {
				text: "IMPORT GRAPH",
				handler: function(item) {
				    var menu = item.parentMenu;
				    var toolbar = menu.up().up();
				    var gr = toolbar.up();
				    gr.exportGraph();
				    Ext.Msg.confirm('Confirm', 
					 'Importing another graph will delete the current graph. Are you sure you want to Import?', 
					  function(ans) {
					      if (ans == 'yes') {
						  self.importGraph();
					      }
					    }
					);
				}
			    }, 
			    {
				text: "SAVE GRAPH",
				handler: function(item) {
				    var menu = item.parentMenu;
                                    var toolbar = menu.up().up();
                                    var gr = toolbar.up();
                                    gr.saveGraph();

					// DRAPER API - this one uses the workflow type of REPORT
					// Send a User Activity Message with optional metadata
					//activityLogger.logUserActivity('Save Graph', '<graph tab name>', activityLogger.WF_REPORT, 
					//    {'Tab':'Transaction Graph', 'action': 'Save', 'selectedNode': 'NA' });
				    }
			    }, 
			    {
				text: "RESTORE GRAPH",
				handler: function(item) {
				    var menu = item.parentMenu;
                                    var toolbar = menu.up().up();
                                    var gr = toolbar.up();
                                    Ext.Msg.confirm('Confirm', 
                                        'Restoring a saved graph will delete the current graph. Are you sure you want to Restore?', 
                                        function(ans) {
                                            if (ans == 'yes') {
                                                gr.restoreGraph(null);
                                            }
                                        }
                                    ); // msg
				}
			    },

			] // items
		    } // menu items
		    ) // create menu
		},
		'-',
		{
		    text: 'Change View',
		    menu: Ext.create("Ext.menu.Menu", {
			style: { backgroundColor: "FloralWhite", fontSize: "medium" }, 
			items: [
			    {
				text: "CIRCULAR",
				handler: function(item) {
				    var menu = item.parentMenu;
				    var toolbar = menu.up().up();
				    var gr = toolbar.up();
				    gr.GraphVis.changeLayout('circle');
				}
			    }, 
			    {
				text: "GRID",
				handler: function(item) {
				    var menu = item.parentMenu;
				    var toolbar = menu.up().up();
				    var gr = toolbar.up();
				    gr.GraphVis.changeLayout('grid');
				}
			    }, 
			    {
				text: "HIERARCHICAL",
				handler: function(item) {
				    var menu = item.parentMenu;
				    var toolbar = menu.up().up();
				    var gr = toolbar.up();
				    gr.GraphVis.changeLayout('breadthfirst');
				}
			    }, 
			     {
				text: "FORCE DIRECTED - Wheel",
				handler: function(item) {
				    var menu = item.parentMenu;
				    var toolbar = menu.up().up();
				    var gr = toolbar.up();
				    gr.GraphVis.changeLayout('arbor-wheel');
				}
			    },
			    {
				text: "FORCE DIRECTED - Snowflake",
				handler: function(item) {
				    var menu = item.parentMenu;
				    var toolbar = menu.up().up();
				    var gr = toolbar.up();
				    gr.GraphVis.changeLayout('arbor-snow');
				}
			    },
			    {
				text: "FORCE DIRECTED - COSE",
				handler: function(item) {
				    var menu = item.parentMenu;
				    var toolbar = menu.up().up();
				    var gr = toolbar.up();
				    gr.GraphVis.changeLayout('cose');
				}
			    }
			    // 'random' layout is also available but pretty useless so this one is omitted
			] // items
		    } // menu items
		    ) // create menu
		},
		'-',    // separator
		// button to refresh the display
		 {
		    xtype:'button',
		    text:'Refresh', 
		    disabled:false, 
		    margin:0,   
		    width:70,
		    height: 24,
		    maxHeight: 24,
		    style: { backgroundColor: "FloralWhite", fontSize: "medium" }, 
		    listeners: {
			click: function(self)
			{
				var toolbar = self.up();
				var gr = toolbar.up();
				gr.GraphVis.refreshLayout();
			}
		    } // listeners
		},
		'-',
                // display the institution
                {
                    xtype:'label',
                    text:'INSTITUTION: ', 
                    disabled:false, 
                    margin: '2 2 2 300',   
                    width:170,
                    listeners: {
                        beforerender: function(self, opts)
                        {
                            var toolbar = self.up();                        
                            self.setText("INSTITUTION: " + toolbar.institution);
                        }
                    } // listeners
                }
	    ]; // toolbar items	
	    this.callParent(arguments);
	    
    } // constructor
 }); // define
 
