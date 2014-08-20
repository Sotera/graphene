
Ext.define("DARPA.TXNGNodeDisplay",
{
	extend: "Ext.Container",   
	width: 'auto',
	height: 'auto',
	//height: 120,
	border: 0,
	margin: 0,
	//maxHeight: 120,
	layout: {
		type:'vbox',
		align:'stretch'
	},
	
	constructor: function(config) {
	
		var idPanel = Ext.create("Ext.form.FieldSet",  { 
			border: 0,
			padding: 2,
			margin: 2,
			flex: 1,
			layout: {
				type: 'vbox',
				align: 'stretch'
			},
			items:[{
				xtype: 'textfield',			
				fieldLabel: 'Identifier Type',
				labelLength: 80
			}, {
				xtype: 'textfield',						
				fieldLabel: 'Identifier',
				labelLength: 80				
			}, {
				xtype: 'panel',
				flex: 1,
				autoScroll: true
			}] // items
		});
		    
		this.items = [
			idPanel,
			Ext.create("DARPA.Node_Actions", {
				id: config.id + '-Actions',
				height: 'auto'
			}),
			Ext.create("Ext.panel.Panel", {
				title: 'LEGEND',
				items: GLegend.getLegend()
			})
		];
		
		this.callParent(arguments);
	}, // constructor
	
	//------------
    setAttrs: function(data) {
		var self = this;
		var html = "<table rules='rows'>";
		var attrs = data.attrs;
		var detailsItems = self.items.items[0].items.items; // MFM
		var elementIsEdge = false;
		
		for (var prop in data) {
			if (data.hasOwnProperty(prop)) {
				if (prop == "idType") {
					detailsItems[0].setValue(data[prop]);
				}
				else if (prop == "idVal") {
					detailsItems[1].setValue(data[prop]);
				}
			}
		}
		
		for (var i = 0; i < attrs.length; ++i) {
			var a = attrs[i];
			if (a.key.indexOf("payload") == -1) {
				html += "<tr><td>" + a.key + "</td> <td>&nbsp;:&nbsp;</td> <td>" + a.val + "</td></tr>";
			} else {
				elementIsEdge = true;
			}
			if (a.key == "subject") {
				detailsItems[1].setValue(a.val);
			}
		}
		html += "</table>";
		if (elementIsEdge) {
			html = "<span style='color:blue'>[Right-click the selected edge to view email content]</span><br><br>" + html; 
		}
		detailsItems[2].update(html);
	},
    
    // MFM
    getIdentifierType: function() {
        var detailsItems = this.items.items[0].items.items;
        return detailsItems[0].getValue();
    },
    
    // MFM
    getIdentifier: function() {
        var detailsItems = this.items.items[0].items.items;
        return detailsItems[1].getValue();
    },
    
     // MFM
    getIdentifierDetails: function() {
        var detailsItems = this.items.items[0].items.items;
        return detailsItems[2].getValue();
    },
    
 	getGraph:function()
    {
    	var self = this;
    	var path = self.id.split('-'); // this will be the entity ID
		var graphId = path[0] + '-' + path[1];
		var graph = Ext.getCmp(graphId);
		return graph;
    },        
    
	enablePivot:function(enable)
	{
		var self = this;
		var button = self.getGraph().getPivotButton();
		
		if (enable) {
			button.enable();
		} else {
			button.disable();
		}
	},
	
	enableHide:function(enable)
	{
		var self = this;
		var button = self.getGraph().getHideButton();
		
		if (enable) {
			button.enable();
		} else {
			button.disable();
		}
	},
	
	enableShow:function(enable)
	{
		var self = this;
		var button = self.getGraph().getShowButton();
		if (enable) {
			button.enable();
		} else {
			button.disable();
		}
	}
});

