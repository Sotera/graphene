Ext.define("DARPA.AccountList",
		{
			extend : 'Ext.grid.Panel',
			title : 'Accounts',

			viewConfig : {
				invalidateScrollerOnRefresh : true,
				loadMask : false,
				enableTextSelection : true
			// MFM added so that can copy the values
			},

			columns : [
					{
						header : 'Account',
						width : 200,
						dataIndex : 'account'
					},
					{
						header : 'Options',
						width : 120,
						renderer : function(value, metadata, record) {
							var accno = "" + record.raw[0];
							// I messed this up. This is how it used to look,
							// but accno is no longer an integer, it is a string
							// value (email), which causes errors later on.
							// tablink = "showLedger('" + accno + "','"
							// + this.entityId + "');";
							// This version uses entity id for both parameters:
							tablink = "showLedger('" + this.entityId + "','"
									+ this.entityId + "');";
							return ("<button style='font-size:75%;' onclick="
									+ tablink + ">SHOW EMAIL</button>");
						}
					} ],
			store : null,
			constructor : function(config) {
				var dat = [];
				var accs = config.entity.accountList;
				for (var i = 0; i < accs.length; ++i) {
					dat.push([ accs[i] ]); // note the extra square brackets to
					// make it an array of 1-element
					// arrays
				}

				this.store = Ext.create("Ext.data.ArrayStore", {
					fields : [ 'account' ],
					data : dat
				// data:config.entity.accountList
				});
				this.entityId = config.entity.id;
				this.institution = config.institution;
				this.callParent(arguments);
				return this;
			}

		});

Ext.define("DARPA.EntityPropertiesGrid", {
	extend : 'Ext.grid.Panel',
	title : 'Details',
	features : [ {
		ftype : 'grouping',
		groupHeaderTpl : '{name}'
	} ],

	viewConfig : {
		invalidateScrollerOnRefresh : true,
		loadMask : false,
		enableTextSelection : true
	// MFM added so that can copy the values
	},

	columns : [ {
		header : ' ',
		width : 100
	}, // spacer
	{
		header : 'Type',
		width : 200,
		dataIndex : 'name'
	}, {
		header : 'Value',
		flex : 1,
		dataIndex : 'value'
	} ],
	store : null,
	constructor : function(config) {
		this.store = Ext.create("Ext.data.Store", {
			fields : [ 'family', 'name', 'value' ],
			data : config.entity.attributes,
			groupField : 'family'
		});
		this.callParent(arguments);
		return this;
	}

});

function loadFamily(family, property, values, target) {
	for (var i = 0; i < values.length; ++i) {
		var val = "";
		val = eval('values[i].' + property);
		target.push({
			family : family,
			idName : '',
			idValue : val
		})
	}
}

Ext.define("DARPA.EntityDetailsPanel", {

	extend : "Ext.Panel",
	layout : {
		type : 'hbox',
		align : 'stretch'
	},
	items : [],
	// We have to do this in a constructor or we will get the same items in
	// every instance
	constructor : function(config) {
		this.items = [ Ext.create("DARPA.AccountList", {
			entity : config.entity,
			institution : config.institution
		}), Ext.create("DARPA.EntityPropertiesGrid", {
			entity : config.entity,
			institution : config.institution,
			flex : 1
		}) ];
		this.callParent(arguments);
		return this;
	}

});

Ext.define("DARPA.EntityDetailsFrame", {
	extend : 'Ext.panel.Panel',
	border : false,
	margin : 20,
	xtype : 'fieldSet',
	entity : {},
	layout : {
		type : 'vbox',
		align : 'stretch'
	},
	items : [],

	constructor : function(config) {
		var titlepanel = Ext.create("Ext.Container", {
			layout : {
				type : 'vbox',
				align : 'center'
			},
			flex : 1,
			border : 1,
			bodyStyle : 'background-color:lightyellow',
			items : [ {
				xtype : 'displayfield',
				// value:config.entityname
				value : "INSTITUTION: " + config.institution + " | Entity: "
						+ config.entityname
			} ]
		});

		var showgraphbutton = Ext.create("Ext.Container", {

			hidden : true, // going to try preloading the graph so not need
			// this button
			flex : 1,
			height : 24,
			title : false,
			layout : {
				type : 'vbox',
				align : 'center'
			},
			items : [ {
				xtype : 'button',
				width : 120,
				height : 40,
				text : 'SHOW ENTITY GRAPH',
				handler : function() {
					showEntityGraph(config.entity);
				}
			} ]
		});

		var topPanel = Ext.create("Ext.Container", {
			xtype : 'container',
			title : false,
			height : 80,

			layout : {
				type : 'hbox',
				align : 'stretch'
			},
			items : [ titlepanel, showgraphbutton ]
		});

		this.items = [ topPanel, Ext.create("DARPA.EntityDetailsPanel", {
			flex : 1,
			width : 800,
			margin : '10 0 0 0',
			entity : config.entity,
			institution : config.institution
		}) ]; // items

		this.callParent(arguments);

	}, // constructor

	getEntityGrid : function() {
		var self = this;
		return self.items.items[1];
	},

	loadLedger : function(account) {
		Ext.Msg.alert(account);
	}

});
