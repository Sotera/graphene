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
	
        infoVis: null,    // reference to the InfoVis graph (not the graph data)
        fd: null,
	
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
                                    var start = importText.indexOf("[");
                                    if (start < 0 || start > 2 ) {
                                        alert("Invalid input. The imported graph must be valid JSON and must start with [");
                                        return;
                                    }
                                    try {
                                        graphJSON = Ext.decode(importText);
                                    }
                                    catch (dex) {
                                       alert("Invalid input. The imported graph must be valid JSON and must start with [");
                                       return;
                                    }
                                    
                                    // DEBUG
                                    console.log("Import button:");
                                    
                                    if (graphJSON) {
                                       var grInfoVisItems = thisWindow.getInfoVis();  // reference to the InfoVis graph
                                       if (grInfoVisItems && grInfoVisItems.length == 2) {
                                           var infoVis = grInfoVisItems[0];
                                           thisWindow.hide();
                                           infoVis.importGraph(graphJSON, grInfoVisItems[1]);
                                           thisWindow.close();
                                       }
                                       // else handle internal error case. grInfoVis should never be null 
                                    }
                                    else {
                                        alert("Invalid input. The imported graph must be valid JSON and must start with [");
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
        
        // set reference to the InfoVis graph
	setInfoVis: function(ivgr, fd) {
		this.infoVis = ivgr;
                this.fd = fd;
	},
        
        // get reference to the InfoVis graph
        // returns 2 items in an array
	getInfoVis: function() {
		return [this.infoVis, this.fd];
	}
});