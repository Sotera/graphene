// importDialog.js
// Used for Importing a previously exported graph (in json format only for now)

Ext.define("DARPA.importDialog", {
	extend: 'Ext.Window',
	title: 'Import',
	height: 600,
	width: 800,
	closeAction: 'destroy',
	plain: 'true',
	border: false,
	resizable: true,
	modal: true,
	layout: 'form',
	buttonAlign: 'center',
	
        graphVis: null,    // reference to the graph api (not the graph data)
	
	constructor: function() {
		var thisWindow = this;
		
		this.items = new Ext.FormPanel({
			layout: 'form',
			title: 'Import Graph',
			width: '100%',
			border: false,
			items: [{
				xtype: 'container',
				title: null,
				height: '100%',
				width: '100%',
				layout: {
					type: 'hbox',
					pack: 'center'
				},
				items: [
				    {
                                        xtype: 'textareafield',
                                        id: 'grImportTextArea',
                                        grow: true,
                                        margin: 2,
                                        allowBlank: false,
                                        autoScroll: true,
                                        //height: yy,
                                        width: 780,
                                        //resizable: true,
                                        size: 3000,  // default size of the text input
                                        value: 'Paste a copy of your exported graph into this area and hit Import.'
                                        
                                    }
				]
			}]
		});
		
		this.buttons = [{
			text: 'Import',
			handler: function(btn, e) {
                            var importTextCmp = Ext.getCmp('grImportTextArea');
                            if (importTextCmp) {
                                var graphJSON = null;
                                var importText = importTextCmp.getRawValue();
                                if (importText && importText.length > 2) {
                                    
                                    // DEBUG
                                    console.log("import button hit");
                                    
                                    // minimal validation
                                    var start = importText.indexOf('"nodes":');
                                    if (start < 0 ) {
                                        alert("Invalid input. The imported graph must be valid JSON that has been exported from a previous graph.");
                                        return;
                                    }
                                    try {
                                        graphJSON = Ext.decode(importText);
                                    }
                                    catch (dex) {
                                       alert("Invalid input. The imported graph must be valid JSON that has been exported from a previous graph.");
                                       return;
                                    }
                                    
                                    if (graphJSON) {
                                       var grGraphVis = thisWindow.getGraphVis();  // reference to the GraphVis api
                                       if (grGraphVis) {
                                           thisWindow.hide();
                                           grGraphVis.importGraph(graphJSON);
                                           thisWindow.close();
                                       }
                                       // else handle internal error case. grGraphVis should never be null 
                                    }
                                    else {
                                        alert("Invalid input. The imported graph must be valid JSON that has been exported from a previous graph.");
                                    }
                                }
                            }
                            else {
                                // handle error
                            }
			}
		}, 
                {
			text: 'Clear',
			handler: function(btn, e) {
                            var importTextCmp = Ext.getCmp('grImportTextArea');
                            if (importTextCmp) {
                                importTextCmp.setRawValue("");
                            }
			}
		},
                {
			text: 'Cancel',
			handler: function(btn, e) {
				thisWindow.close();
			}
		}];
		
		this.callParent(arguments);
	},
        
        // set reference to the graph api
	setGraphVis: function(ivgr) {
		this.graphVis = ivgr;
	},
        
        // get reference to the graph api
	getGraphVis: function() {
		return this.graphVis;
	}
});