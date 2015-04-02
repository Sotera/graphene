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
			width: "65%",
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
		
		var invertSelection = Ext.create("Ext.Button", {
			text: "Invert",
			width: "13%",
			height: "100%",
			handler: function() {
				_this.invertSelectedRecordTypes();
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
			items: [nodeTypeCB, selectAll, invertSelection, clearSelection]
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
			maxHeight: 600,
			autoScroll: true,
			collapsible: true,
			collapseDirection: "top",
			items: [
				_nodeTypeLine,
				_dateLine, 
				_amountLine
			]
		});
		
		var applyBtn = Ext.create("Ext.Button", {
			text: "APPLY TO GRAPH",
			margin: 4,
			height: 25,
		    margin: 2,
			handler: function() {
				alert("Not yet implemented; coming soon");
			}
		});
		
		var clearBtn = Ext.create("Ext.Button", {
			text: "CLEAR",
			margin: 4,
			height: 25,
		    margin: 2,
			handler: function() {
				_this.clearRecordTypes();
				_this.clearDates();
				_this.clearAmounts();
			}
		});
		
		var helpBtn = Ext.create("Ext.Button", {
			icon: Config.helpIcon,
			maxWidth: 30,
		    scale: 'medium',
			height: 30,
		    margin: 2,
		    handler: function() {
				// TODO: write actual help documentation
				// right now, this is just for testing's sake; it just lists the current data constraints in the form
				
				var HTML = "<ul><li><b>SelectedRecordTypes:</b> <ul><li>";
				var selectedTypes = _this.getRecordTypes();
				var dates = _this.getDates();
				var amounts = _this.getAmounts();
				
				for (var i = 0; i < selectedTypes.length; i++) {
					HTML += selectedTypes[i];
					
					// if we're not at the last entry, add a comma and a space
					if (i != selectedTypes.length - 1) {
						HTML += ", ";
					}
				}
				HTML += "</li></ul></li>";
				HTML += "<li><b>Date Range:</b> <ul><li><i>From: </i>" + new Date(dates.from).toString() + "</li><li><i>To: </i>" + new Date(dates.to).toString() + "</li></ul></li>";
				HTML += "<li><b>Amount Range:</b> <ul><li><i>From: </i>" + amounts.from + "</li><li><i>To: </i>" + amounts.to + "</li></ul></li>";
				HTML += "</ul>";
				
			    Ext.Msg.alert(
			       'Advanced Query Help',
				   HTML
			    );
			}
		});
		
		var actionsPanel = Ext.create("Ext.Panel", {
			title: 'ACTIONS',
			height: 'auto',
			width: 'auto',
			buttonAlign: "center",
			collapsible: true,
			collapseDirection: "top",
			border: true,
			layout: {
				type: 'vbox',
				align: 'stretch'
			},
			buttons: [ clearBtn, applyBtn, helpBtn ]
		});
		
		this.items = [form, actionsPanel];
		this.callParent(arguments);
	},
	
	afterRender: function(ct, position) {
		DARPA.AdvancedQueryPanel.superclass.afterRender.call(this, ct, position);
		var _this = this;
		
		// TODO:
		// -set min and max dates based on the date range of the data

		// get the parent container of the node type combobox and disable it
		Ext.getCmp(_this.id + "-NODE-TYPE-CB").up().setDisabled(true);
		
		Ext.Ajax.request({
			url: "rest/meta/canonicaltypes",
			success: function(resp) {
				try {
					var array = Ext.decode(resp.responseText);
					var data = {};
					
					for (var i = 0; i < array.length; i++) {
						data[array[i]] = array[i];
					}
					
					_this.loadRecordTypes(data, false);
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
	
	getRecordTypes: function() {
		var combo = Ext.getCmp(this.id + "-NODE-TYPE-CB");
		return combo.getValue();
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
	
	invertSelectedRecordTypes: function() {
		var currentlySelectedItems = this.getRecordTypes();
		var combo = Ext.getCmp(this.id + "-NODE-TYPE-CB");
		var indicesToInvert = [];
		var indicesToSelect = [];
		
		// gather the indices of the selected items; these will be used to select everything else
		while (currentlySelectedItems.length > 0) {
			var item = currentlySelectedItems.shift();
			var record = combo.findRecord(combo.valueField, item);
			indicesToInvert.push( combo.store.indexOf(record) );
		}
		
		// at this point, the store has been completely emptied by the shift() calls
		
		var store = combo.getStore().collect(combo.valueField);
		
		for (var i = 0; i < store.length; i++) {
			if (indicesToInvert.indexOf(i) == -1) {
				indicesToSelect.push( store[i] );
			}
		}
		
		combo.select( indicesToSelect );
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