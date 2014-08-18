Ext.define("DARPA.PBNodeDisplay", {
	extend : "Ext.Container",
	width : 'auto',
	height : 'auto',
	// height: 340,
	border : 0,
	margin : 0,
	layout : {
		type : 'vbox',
		align : 'stretch'
	},
	constructor : function(config) {

		var idPanel = Ext.create("Ext.form.FieldSet", {
			border : 0,
			padding : 2,
			margin : 2,
			flex : 1,
			layout : {
				type : 'vbox',
				align : 'stretch'
			},
			items : [ {
				xtype : 'textfield',
				fieldLabel : 'Identifier Type',
				labelLength : 80
			}, {
				xtype : 'textfield',
				fieldLabel : 'Identifier',
				labelLength : 80
			}, {
				xtype : 'panel',
				flex : 1,
				autoScroll : true
			} ]
		});

		this.items = [ idPanel,

		Ext.create("DARPA.Node_Actions", {
			id : config.id + '-Actions',
			height : 'auto'
		}), Ext.create("Ext.panel.Panel", {
			title : 'LEGEND',
			items : GLegend.getLegend()
		})

		]; // end

		this.callParent(arguments);

	}, // constructor

	getGraph : function() {
		var self = this;
		var path = self.id.split('-'); // this will be the entity ID
		var graphId = path[0] + '-' + path[1];
		var graph = Ext.getCmp(graphId);
		return graph;
	},

	getActions : function() {
		var self = this;
		var actions = Ext.getCmp(self.id + '-Actions');
		return actions;
	},

	// ------------
	setAttrs : function(data) {
		var attrs = data.attrs;
		var self = this;
		var html = "<table rules='rows'>";
		var detailsItems = self.items.items[0].items.items; // MFM
		detailsItems[0].setValue(data.idType);
		detailsItems[1].setValue(data.idVal);

		for (var i = 0; i < attrs.length; ++i) {
			var a = attrs[i];
			if (a.key.indexOf("node-prop") == -1) {
				html += "<tr><td>" + a.key + ":&nbsp </td><td>" + a.val
						+ "</td></tr>";
			}
		}
		html += "</table>";

		detailsItems[2].update(html);
	},

	// MFM
	getIdentifierType : function() {
		var detailsItems = this.items.items[0].items.items;
		return detailsItems[0].getValue();
	},

	// MFM
	getIdentifier : function() {
		var detailsItems = this.items.items[0].items.items;
		return detailsItems[1].getValue();
	},

	// MFM
	getIdentifierDetails : function() {
		var detailsItems = this.items.items[0].items.items;
		return detailsItems[2].getValue();
	},

	enablePivot : function(enable) {
		var self = this;
		var button = self.getGraph().getPivotButton();
		if (enable)
			button.enable();
		else
			button.disable();
	},
	enableHide : function(enable) {
		var self = this;
		var button = self.getGraph().getHideButton();

		if (enable)
			button.enable();
		else
			button.disable();
	},
	enableShow : function(enable) {
		var self = this;
		var button = self.getGraph().getShowButton();
		if (enable)
			button.enable();
		else
			button.disable();
	}
});
