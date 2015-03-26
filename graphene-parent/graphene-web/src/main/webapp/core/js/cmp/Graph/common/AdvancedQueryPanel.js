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
			margin: 2,
			items: [nodeTypeCB, selectAll, clearSelection]
		};
		
		var _dateLine = {
			xtype: 'panel',
			id: config.id + "-DATE-PANEL",
			title: '*Dates',
			layout: 'hbox',
			margin: 2,
			items: [{
				xtype: 'datefield',
				fieldLabel: "From:",
				margin: 2,
				labelWidth: 30,
				//submitFormat: 'U000',
				emptyText: "MM/DD/YYYY",
				width: '50%'
			}, {
				xtype: 'datefield',
				fieldLabel: "To:",
				margin: 2,
				labelWidth: 30,
				//submitFormat: 'U000',
				emptyText: "MM/DD/YYYY",
				width: '50%'
			}]
		};
		
		var _amountLine = {
			xtype: 'panel',
			title: "*Amounts",
			id: config.id + "-AMOUNT-PANEL",
			layout: 'hbox',
			margin: 2,
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
			maxHeight: 400,
			autoScroll: true,
			collapsible: true,
			collapseDirection: "top",
			items: [
				_nodeTypeLine,
				_dateLine, 
				_amountLine
			]
		});
		
		var postBtn = Ext.create("Ext.Button", {
			text: "POST",
			margin: 4,
			height: 30,
		    margin: 2,
			columnWidth: .5,
			handler: function() {
				alert("Not yet implemented; coming soon");
			}
		});
		
		var clearBtn = Ext.create("Ext.Button", {
			text: "CLEAR ALL",
			margin: 4,
			height: 30,
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
			height: 30,
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
			collapseDirection: "top",
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

		// get the parent container of the node type combobox and disable it
		Ext.getCmp(_this.id + "-NODE-TYPE-CB").up().setDisabled(true);
		
		Ext.Ajax.request({
			url: "rest/meta/canonicaltypes",
			success: function(resp) {
				try {
					var array = Ext.decode(resp.responseText);
					
					// example data
					var data = {
						'1': 'USERNAME',
						'2': 'MEDIA',
						'3': 'Comment'
					};
					
					for (var i = 0; i < array.length; i++) {
						data[array[i].name] = array[i].friendlyName;
					}
					
					_this.loadRecordTypes(data, false);
					
					// -default select all?
					_this.selectAllRecordTypes();
				} catch (e) {
					console.error(e.message);
				}
				// get the parent container of the node type combobox and enable it
				Ext.getCmp(_this.id + "-NODE-TYPE-CB").up().setDisabled(false);
			},
			failure: function(resp) {
				console.error("could not load any record types");
				
				// load nothing
				_this.loadRecordTypes({}, true);

				// get the parent container of the node type combobox and enable it
				Ext.getCmp(_this.id + "-NODE-TYPE-CB").up().setDisabled(false);
			}
		});
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
		// FIXME depending on the service, we may want different default values if the field(s) is/are blank
		var fromDateMillis = 0;	// null?
		var toDateMillis = 0;	// null?
		
		try {
			fromDateMillis = items[0].getValue().getTime();
		} catch(e) {
			// from date field is blank
		}
		
		try {
			toDateMillis = items[1].getValue().getTime();
		} catch (e) {
			// to date field is blank
		}
		
		// TODO: date conversions/reformatting if necessary
		
		return {
			from : fromDateMillis,
			to : toDateMillis
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
		
		// TODO parseInt/reformatting if necessary
		
		return {
			from : fromAmount,
			to: toAmount
		};
	},
	
	selectAllRecordTypes: function() {
		var combo = Ext.getCmp(this.id + "-NODE-TYPE-CB");
		combo.select(combo.getStore().collect(combo.valueField));
	},
	
	/**
	 * 	Expects the data object to resemble JSON:
	 * 		{
	 * 			"type1Value" : "Type 1 Label",
	 * 			...,
	 * 			"typeNValue" : "Type n Label"
	 * 		}
	 */
	loadRecordTypes: function(obj, isAppend) {
		var combo = Ext.getCmp(this.id + "-NODE-TYPE-CB");
		var dataArray = [];
		
		// if isAppend is true, get the existing store and assign it to 'dataArray'
		// else 'dataArray' will be an empty array
		if (isAppend === true) {
			var items = combo.getStore().data.items;
			for (var i = 0; i < items.length; i++) {
				var item = items[i];
				dataArray.push([item.data.value, item.data.label]);
			}
		}
		
		// iterate over the JSON object and add each name/value pair to 'dataArray'
		// prevent duplicates
		for (var key in obj) {
			if (!obj.hasOwnProperty(key)) continue;
			var newVal = [key, obj[key]];
			var isDuplicate = false;
			for (var i = 0; i < dataArray.length; i++) {
				if (dataArray[i][0] == newVal[0] && dataArray[i][1] == newVal[1]) {
					isDuplicate = true;
					break;
				}
			}
			if (!isDuplicate) dataArray.push(newVal);
		}
		
		var array_store = new Ext.data.ArrayStore({
			autoDestroy: true,
			storeId: "node_type_store",
			fields: [
			    // if you change the field names, don't forget to change them in the combobox too
				{name: 'value', type: 'string'},
				{name: 'label', type: 'string'}
			],
			expandData: true, 
			data: dataArray
		});

		combo.bindStore(array_store);
	}
});