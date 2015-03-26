Ext.define("DARPA.EntityNodeDisplay", {
	extend: "DARPA.AbstractNodeDisplay",
	
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
				readOnly: true,
				labelLength: 80
			}, {
				xtype: 'textfield',						
				fieldLabel: 'Identifier',
				readOnly: true,
				labelLength: 80				
			}, {
				xtype: 'panel',
				flex: 1,
				autoScroll: true
			}]
		});
		    
		this.items = [
			idPanel,
			Ext.create("DARPA.Node_Actions", {
				id: config.id + '-Actions',
				height: 'auto',
				collapsible: true,
				collapseDirection: "bottom"
			}),
			Ext.create("Ext.panel.Panel", {
				title: 'LEGEND',
				id: config.id + '-Legend',
				maxHeight: 300,
				autoScroll: true,
				collapsible: true,
				collapseDirection: "bottom"
				//items: GLegend.getLegendByGroup(GLegend.getDefaultGroupName())
			})
		];
		
		this.callParent(arguments);
	},
	

    getIdentifierType: function() {
        var detailsItems = this.items.items[0].items.items;
        return detailsItems[0].getValue();
    },
    
    getIdentifier: function() {
        var detailsItems = this.items.items[0].items.items;
        return detailsItems[1].getValue();
    },
    
    getIdentifierDetails: function() {
        var detailsItems = this.items.items[0].items.items;
        return detailsItems[2].getValue();
    },
    
    getLegendPanel: function() {
    	return this.items.items[2]; //.items.items[0];
    },
	//Called when node(s) are selected, so that the attributes are shown in the panel.
    showNodeAttrs: function(node) {
		var self = this;
		var html = "<table rules='rows'>";
		var data = node.data();
		var attrs = data.attrs;
		var detailsItems = self.items.items[0].items.items;
		
		detailsItems[0].setValue(data.idType);
		detailsItems[1].setValue(data.idVal);
		
		var _showAttrs = function(attrs) {
			var runningHTML = "";
			for (var i = 0; i < attrs.length; ++i) {
				var a = attrs[i];
				runningHTML += "<tr><td>" + a.key + "</td> <td>&nbsp;:&nbsp;</td> <td>" + a.val + "</td></tr>";
			}
			return runningHTML;
		};
		
		var _recurse = function(node, index) {
			var isNodeJSON = typeof node.data != "function";
			var subNodes = (!isNodeJSON) ? node.data("subNodes") : node.data.subNodes;
			var attrs = (!isNodeJSON) ? node.data("attrs") : node.data.attrs;
			var name = (!isNodeJSON) ? node.data("name") : node.data.name;
			var reason = (!isNodeJSON) ? node.data("reason") : node.data.reason;
			var imageUrl = (!isNodeJSON) ? node.data("imageUrl") : node.data.imageUrl;
			
			var runningHTML = "<tr><th colspan='3'><b>" + index + "_" + name + "</b></th></tr>";
			
			if (imageUrl !== null && imageUrl !== undefined) {
				runningHTML += "<tr><th colspan='3'>" + "<img src='" + imageUrl + "'>" + "</th></tr>";
			}
			
			runningHTML += _showAttrs(attrs);
			
			if (reason !== null && reason !== undefined) {
				runningHTML += "<tr><td>Reason for Merge</td> <td>&nbsp;:&nbsp;</td> <td>" + reason + "</td></tr>";
			}
			
			// base case -- no subnodes
			if (subNodes === null || typeof subNodes == "undefined" || subNodes.length <= 0) {
				return runningHTML;
			} else {
				for (var i = 0; i < subNodes.length; ++i) {
					runningHTML += _recurse(subNodes[i], ++index);
				}
				return runningHTML;
			}
		};
		
		//if (typeof node.data().subNodes == "undefined") {
		//	html += _showAttrs(attrs);
		//} else {
			html += _recurse(node, 1);
		//}
		html += "</table>";
		detailsItems[2].update(html);
	},
	
	showEdgeAttrs: function(edge) {
		var self = this;
		var html = 	"<table rules='rows'>";
		var data = edge.data();
		var attrs = data.attrs;
		var detailsItems = self.items.items[0].items.items;
		
		detailsItems[0].setValue(data.idType);
		detailsItems[1].setValue(data.idVal);
		
		var _showAttrs = function(attrs) {
			var runningHTML = "";
			for (var i = 0; i < attrs.length; ++i) {
				var a = attrs[i];
				runningHTML += "<tr><td>" + a.key + "</td> <td>&nbsp;:&nbsp;</td> <td>" + a.val + "</td></tr>";
			}
			return runningHTML;
		};
		
		html += _showAttrs(attrs);
		html += "</table>";
		
		detailsItems[2].update(html);
	}
});