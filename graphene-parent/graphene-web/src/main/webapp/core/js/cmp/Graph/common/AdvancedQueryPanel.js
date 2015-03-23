Ext.define("DARPA.AdvancedQueryPanel", {
	extend: "Ext.panel.Panel",
	height: 'auto',
	
	graphRef: null,
	
	constructor: function(config) {
		
		var cblov = Ext.create("Ext.form.ComboBox", {
			maxHeight: 150,
			width: "79%",
			multiSelect: true,
			emptyText: "Select Types",
			store: new Ext.data.ArrayStore({
				fields: ["value", "label"],
				data: [
					["1", 'MEDIA'], 
					["2", 'Comment'],
					["3", 'USERNAME']
				]
			}),
			valueField: "value",
			displayField: "label",
			forceSelection: true,
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
			handler: function(e) {
				
			}
		});
		
		var clearSelection = Ext.create("Ext.Button", {
			text: "None",
			width: "11%",
			height: "100%",
			handler: function(e) {
				
			}
		});
		
		var _nodeTypeLine = {
			xtype: 'panel',
			title: "Record Types",
			layout: "hbox",
			margin: "2 0 2 0",
			items: [cblov, selectAll, clearSelection]
		};
		
		var _dateLine = {
			xtype: 'panel',
			title: 'Dates',
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
			title: "Amounts",
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
		
		var _paramFields = {
			xtype: 'container',
			layout: 'vbox',
			padding: 2,
			items: [{
				xtype: 'textfield',
				fieldLabel: "Part A",
				width: '100%',
				labelWidth: 50
			}, {
				xtype: 'textfield',
				fieldLabel: "Part B",
				width: '100%',
				labelWidth: 50
			}, {
				xtype: 'textfield',
				fieldLabel: "Part C",
				width: '100%',
				labelWidth: 50
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
		
		var actionButtonsLine1 = Ext.create("Ext.form.FieldSet", {
			border: 0,
			maxHeight: 40,
			padding: 0,
			margin: 0,
			items: [{
				xtype: 'fieldcontainer',
				height: 'auto',
				width: '100%',
				items: [
					new Ext.Button({
						text: 'test btn 1',
						margin: 4,
						height: 24,
						width: '33%'
					}),
					new Ext.Button({
						text: 'test btn 2',
						margin: 4,
						height: 24,
						width: '33%'
					}),
					new Ext.Button({
						text: 'test btn 3',
						margin: 4,
						height: 24,
						width: '33%'
					})
				]
			}]
		});
		
		var helpBtn = Ext.create("Ext.Button", {
			icon: Config.helpIcon,
		    maxHeight: 30,
		    scale: 'medium',
		    margin: 2,
		    style: {marginTop: '2px'},
		    handler: function() {
			    Ext.Msg.alert(
			       'Help',
				   "A work in progress!"
			    );
			}
		});
		
		var actionButtonsLine2 = Ext.create("Ext.form.FieldSet", {
			border: 0,
			maxHeight: 40,
			padding: 0,
			margin: 0,
			items: [{
				xtype: 'fieldcontainer',
				height: 'auto',
				width: '100%',
				items: [
					new Ext.Button({
						text: 'test btn 4',
						margin: 4,
						height: 24,
						width: '25%'
					}),
					new Ext.Button({
						text: 'test btn 5',
						margin: 4,
						height: 24,
						width: '25%'
					}),
					helpBtn
				]
			}]
		})
		
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
				actionButtonsLine1,
				actionButtonsLine2
			]
		});
		
		this.items = [form, actionsPanel];
		
		this.callParent(arguments);
	}
});