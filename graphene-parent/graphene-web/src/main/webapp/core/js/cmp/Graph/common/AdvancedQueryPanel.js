Ext.define("DARPA.AdvancedQueryPanel", {
	extend: "Ext.panel.Panel",
	height: 'auto',
	
	graphRef: null,
	
	constructor: function(config) {
		var _this = this;
		
		var array_store = new Ext.data.ArrayStore({
			autoDestroy: true,
			storeId: "node_type_store",
			fields: [
				{name: 'value', type: 'string'},
				{name: 'label', type: 'string'}
			],
			expandData: true, 
			data: []
		});
		
		var nodeTypeCB = Ext.create("Ext.form.ComboBox", {
			id: config.id + "-NODE-TYPE-CB",
			maxHeight: 150,
			width: "78%",
			multiSelect: true,
			emptyText: "Select Types",
			store: array_store,
			valueField: "value",
			displayField: "label",
			forceSelection: false,
			editable: false,
			mode: 'local',
			triggerAction: 'all',
			allowBlank: true,
			listConfig : {          
				getInnerTpl : function() {
					return '<div class="x-combo-list-item"><img src="' + Ext.BLANK_IMAGE_URL + '" style="width:16px; height:16px;" class="chkCombo-default-icon chkCombo" /> {label} </div>';
				}
			}
		});
		
		var selectAll = Ext.create("Ext.Button", {
			text: "All",
			width: "9%",
			height: "100%",
			handler: function() {
				_this.selectAllRecordTypes();
			}
		});
		
		var clearSelection = Ext.create("Ext.Button", {
			text: "None",
			width: "12%",
			height: "100%",
			handler: function() {
				_this.clearRecordTypes();
			}
		});
		
		var _nodeTypeLine = {
			xtype: 'panel',
			title: "*Record Types",
			layout: "hbox",
			margin: "2 0 2 0",
			items: [nodeTypeCB, selectAll, clearSelection]
		};
		
		var _dateLine = {
			xtype: 'panel',
			id: config.id + "-DATE-PANEL",
			title: '*Dates',
			layout: 'hbox',
			margin: '2 0 2 0',
			items: [{
				xtype: 'datefield',
				fieldLabel: "From:",
				margin: 2,
				labelWidth: 30,
				emptyText: "MM/DD/YYYY",
				width: '50%'
			}, {
				xtype: 'datefield',
				fieldLabel: "To:",
				margin: 2,
				labelWidth: 30,
				emptyText: "MM/DD/YYYY",
				width: '50%'
			}]
		};
		
		var _amountLine = {
			xtype: 'panel',
			title: "*Amounts",
			id: config.id + "-AMOUNT-PANEL",
			layout: 'hbox',
			margin: '2 0 2 0',
			items: [{
				xtype: 'textfield',
				fieldLabel: "From:",
				margin: 2,
				labelWidth: 30,
				width: '50%'
			}, {
				xtype: 'textfield',
				fieldLabel: "To:",
				margin: 2,
				labelWidth: 30,
				width: '50%'
			}]
		};
		
		var form = Ext.create("Ext.form.Panel", {
			title: 'ADVANCED QUERY FORM',
			border: 0,
			flex: 1,
			margin: '0 2 0 2',
			items: [
				_nodeTypeLine,
				_dateLine, 
				_amountLine
			]
		});
		
		var postBtn = Ext.create("Ext.Button", {
			text: "POST",
			margin: 4,
			height: 25,
		    margin: 2,
			columnWidth: .5,
			handler: function() {
				
			}
		});
		
		var clearBtn = Ext.create("Ext.Button", {
			text: "CLEAR ALL",
			margin: 4,
			height: 25,
		    margin: 2,
			columnWidth: .5,
			handler: function() {
				_this.clearRecordTypes();
				_this.clearDates();
				_this.clearAmounts();
			}
		});
		
		var helpBtn = Ext.create("Ext.Button", {
			icon: Config.helpIcon,
			width: 30,
		    scale: 'medium',
		    margin: 2,
		    handler: function() {
			    Ext.Msg.alert(
			       'Help',
				   _this.getDates().toString()
			    );
			}
		});
		
		var actionButtonsLine1 = Ext.create("Ext.form.FieldSet", {
			border: 0,
			maxHeight: 40,
			padding: 0,
			margin: 0,
			items: [{
				xtype: 'fieldcontainer',
				height: 'auto',
				width: '100%',
				layout: "column",
				items: [ postBtn, clearBtn, helpBtn ]
			}]
		});
		
		var actionsPanel = Ext.create("Ext.panel.Panel", {
			title: 'ACTIONS',
			height: 'auto',
			width: 'auto',
			collapsible: true,
			collapseDirection: "bottom",
			layout: {
				type: 'vbox',
				align: 'stretch'
			},
			items: [
				actionButtonsLine1
				// ,actionButtonsLine2
			]
		});
		
		this.items = [form, actionsPanel];
		this.callParent(arguments);
	},
	
	afterRender: function(ct, position) {
		DARPA.AdvancedQueryPanel.superclass.afterRender.call(this, ct, position);
		var _this = this;
		
		// TODO:
		// -call REST service and get all node types for this data set
		// -call loadRecordTypes() with those node types
		// -default select all?
		// -set min and max dates based on the date range of the data
		
		// example data
		var data = {
		// 	{"value" : "label"}
			'1': 'USERNAME',
			'2': 'MEDIA',
			'3': 'Comment'
		};
		
		_this.loadRecordTypes(data, false);
		
		// -default select all?
		_this.selectAllRecordTypes();
	},
	
	clearRecordTypes: function() {
		var combo = Ext.getCmp(this.id + "-NODE-TYPE-CB");
		combo.select();
	},
	
	clearDates: function() {
		var datePanel = Ext.getCmp(this.id + "-DATE-PANEL");
		var items = datePanel.items.items;
		for (var i = 0; i < items.length; i++) {
			items[i].reset();
		}
	},
	
	getDates: function() {
		var datePanel = Ext.getCmp(this.id + "-DATE-PANEL");
		var items = datePanel.items.items;
		var fromDate = items[0].getValue();
		var toDate = items[1].getValue();
		
		// TODO: date conversions/reformatting if necessary
		
		return {
			from : fromDate,
			to : toDate
		};
	},
	
	clearAmounts: function() {
		var amountPanel = Ext.getCmp(this.id + "-AMOUNT-PANEL");
		var items = amountPanel.items.items;
		for (var i = 0; i < items.length; i++) {
			items[i].reset();
		}
	},
	
	getAmounts: function() {
		var amountPanel = Ext.getCmp(this.id + "-AMOUNT-PANEL");
		var items = amountPanel.items.items;
		var fromAmount = items[0].getValue();
		var toAmount = items[1].getValue();
		
		// TODO parseInt/reformatting if necesary
		
		return {
			from : fromAmount,
			to: toAmount
		};
	},
	
	selectAllRecordTypes: function() {
		var combo = Ext.getCmp(this.id + "-NODE-TYPE-CB");
		combo.select(combo.getStore().collect(combo.valueField));
	},
	
	loadRecordTypes: function(obj, isAppend) {
		var combo = Ext.getCmp(this.id + "-NODE-TYPE-CB");
		
		var array = [];
		for (var key in obj) {
			if (!obj.hasOwnProperty(key)) continue;
			array.push([key, obj[key]]);
		}
		
		var array_store = new Ext.data.ArrayStore({
			autoDestroy: true,
			storeId: "node_type_store",
			fields: [
			    // if you change the field names, don't forget to change them
			    // in the combobox too
				{name: 'value', type: 'string'},
				{name: 'label', type: 'string'}
			],
			expandData: true, 
			data: array
		});

		combo.bindStore(array_store);
	}
});