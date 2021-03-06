Ext.define("DARPA.TransactionNodeDisplay", {
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
    
	showNodeAttrs: function(node) {
		var self = this;
		var html = "<table width='100%' cellspacing='0' cellpadding='0' border='0' rules='rows'>";
		var detailsItems = self.items.items[0].items.items;
		
		detailsItems[0].setValue(node.data("idType"));
		detailsItems[1].setValue(node.data("idVal"));
		
		var _showAttrs = function(attrs) {
			var runningHTML = "";
			for (var i = 0; i < attrs.length; ++i) {
				var a = attrs[i];
				runningHTML += "<tr><td>" + a.key + "</td> <td>&nbsp;:&nbsp;</td> <td>" + a.val + "</td></tr>";
			}
			return runningHTML;
		};
		
		var _showMedia = function(mediaArray) {
			if (mediaArray == null || mediaArray == undefined || mediaArray.length <= 0) return "";
			
			var runningHTML = "";
			var carouselDivStart = "<tr><th colspan='3'><div style='width:100%; overflow:hidden;'><figure style='margin:0;'>";
			var carouselDivEnd = "</figure></div></th></tr>";
			
			for (var i = 0; i < mediaArray.length; i++) {
				var a = mediaArray[i];
				switch (a.key) {
				case "IMAGE":
					carouselDivStart += "<img src='" + a.val + "'>";
				}
			}
			// return runningHTML;
			return carouselDivStart + carouselDivEnd;
		};
		
		var _recurse = function(node, index) {
			var isNodeJSON = typeof node.data != "function";
			var subNodes = (!isNodeJSON) ? node.data("subNodes") : node.data.subNodes;
			var attrs = (!isNodeJSON) ? node.data("attrs") : node.data.attrs;
			var name = (!isNodeJSON) ? node.data("name") : node.data.name;
			var reason = (!isNodeJSON) ? node.data("reason") : node.data.reason;
			var mediaArray = (!isNodeJSON) ? node.data("media") : node.data.media;
			
			var runningHTML = "<tr><th colspan='3'><b>" + index + "_" + name + "</b></th></tr>";
			
			runningHTML += _showMedia(mediaArray);
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
			
		html += _recurse(node, 1);
		html += "</table>";
		detailsItems[2].update(html);
	},
	
	showEdgeAttrs: function(edge) {
		var self = this;
		var html = 	"<span style='color:blue'>[Right-click the selected edge to view email content]</span>" +
					"<br><br>" + 
					"<table rules='rows'>";
		var data = edge.data();
		var attrs = data.attrs;
		var detailsItems = self.items.items[0].items.items;
		
		detailsItems[0].setValue(data.idType);
		detailsItems[1].setValue(data.idVal);
		
		var _showAttrs = function(attrs) {
			var runningHTML = "";
			for (var i = 0; i < attrs.length; ++i) {
				var a = attrs[i];
				if (a.key.indexOf("payload") == -1) {
					runningHTML += "<tr><td>" + a.key + "</td> <td>&nbsp;:&nbsp;</td> <td>" + a.val + "</td></tr>";
				}
				if (a.key == "subject") {
					detailsItems[1].setValue(a.val);
				}
			}
			return runningHTML;
		};
		
		html += _showAttrs(attrs);
		html += "</table>";
		
		detailsItems[2].update(html);
	}
});