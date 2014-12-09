Ext.define("DARPA.NodeEditor", {
	extend: "Ext.Window",
	title: 'Edit Node Values',
	height: 400,
	width: 400,
	closeAction: 'destroy',
	plain: 'true',
	border: false,
	resizable: false,
	modal: true,
	layout: 'fit',

	onComplete: function() { console.error("You forgot to implement the onComplete() fn."); },
	nodeRef: null,
	
	constructor: function(config) {
		var _this = this;
		
		_this.nodeRef = config.nodeRef;
		
		var dataRows = new Ext.Container({
			id: "attr_rows",
			width: "100%",
			items: []
		});
		
		dataRows.getRows = function() {
			return this.items.items;
		};
		
		var northPanel = new Ext.Panel({
			region: "north",
			title: "Node Values",
			id: "node_values",
			padding: '1 1 1 1',
			layout: {
				type: 'vbox',
				pack: 'start',
				align: 'center'
			},
			items: [{
				xtype: 'panel',
				width: '100%',
				padding: '1 1 1 1',
				border: false,
				layout: {
					type: "hbox",
					pack: "start",
					align: "center"
				},
				items: [{
					xtype: 'textfield',
					fieldLabel: "Name",
					labelWidth: 60,
					width: '100%',
					name: "node_name",
					value: config.nodeRef.data("name"),
					allowBlank: false
				}]
			}, {
				xtype: 'panel',
				width: '100%',
				border: false,
				padding: '1 1 1 1',
				layout: {
					type: "hbox",
					pack: "start",
					align: "center"
				},
				items: [{
					xtype: 'textfield',
					fieldLabel: "ID Type",
					labelWidth: 60,
					width: '50%',
					name: "node_type",
					value: config.nodeRef.data("idType"),
					allowBlank: false
				}, {
					xtype: 'textfield',
					fieldLabel: "Color",
					labelWidth: 60,
					width: '50%',
					name: "node_color",
					value: config.nodeRef.data("color"),
					allowBlank: false
				}]
			}]
		});
		
		northPanel.getName = function() {
			return this.items.items[0].items.items[0].value;
		};
		northPanel.getIdType = function() {
			return this.items.items[1].items.items[0].value;
		};
		northPanel.getColor = function() {
			return this.items.items[1].items.items[1].value;
		};
		
		var dataForm = new Ext.Panel({
			layout: "border",
			title: "Node Attributes",
			border: false,
			frame: false,
			region: "center",
			items: [{
				region: "center",
				autoScroll: true,
				border: true,
				style: "border:1px solid; border-color: #416AA3",
				items: [dataRows]
			}]
		});
		
		var panel = new Ext.FormPanel({
			frame: true,
			width: "100%",
			height: "100",
			region: "center",
			layout: "border",
			items: [northPanel, dataForm]
		});
		
		_this.items = [panel];
		
		_this.buttons = [{
			text: "OK",
			handler: function(btn) {
				if (_this.items.items[0].getForm().isValid()) {
					_this.onComplete();
					_this.close();
				}
			}
		}, {
			text: "Cancel",
			handler: function(btn) {
				_this.close();
			}
		}];
		
		_this.callParent(arguments);
		_this.autoPopulate(config.nodeRef);
	},
	
	getName: function() {
		var panel = Ext.getCmp("node_values");
		return panel.getName();
	},
	
	getIdType: function() {
		var panel = Ext.getCmp("node_values");
		return panel.getIdType();
	},
	
	getColor: function() {
		var panel = Ext.getCmp("node_values");
		return panel.getColor();
	},
	
	getAttrs: function() {
		var retVal = [];
		var attrRows = Ext.getCmp("attr_rows");
		if (attrRows) {
			var attrs = attrRows.getRows();
			for (var i = 0; i < attrs.length; i++) {
				var row = attrs[i];
				var nvp = row.getNameValuePair();
				
				// TODO: use regex and match for whitespace
				if (typeof nvp.name !== "undefined" && typeof nvp.value !== "undefined" &&
					nvp.name !== "" && nvp.value !== "") {
					retVal.push(nvp);
				}
			}
		}
		return retVal;
	},
	
	makeNewDataRow: function(inName, inValue) {
		var _this = this;
		
		var nameField = {
			xtype: "textfield",
			labelWidth: 60,
			fieldLabel: "Attr_Name",
			name: "name",
			value: inName,
			layout: {
				type: "column",
				pack: "center"
			},
			columnWidth: .5,
			allowBlank: true
		};
		
		var valField = {
			xtype: "textfield",
			labelWidth: 60,
			fieldLabel: "Attr_Value",
			name: "value",
			value: inValue,
			layout: {
				type: "column",
				pack: "center"
			},
			columnWidth: .5,
			allowBlank: true
		};
		
		var addBtn = {
			xtype: "button",
			text: "+",
			tooltip: "Add a new row",
			height: 20, width: 20,
			handler: function(btn) {
				var attrRows = btn.up().up();
				attrRows.add(_this.makeNewDataRow());
			}
		};
		
		var delBtn = {
			xtype: "button",
			text: "-",
			tooltip: "Remove this row",
			height: 20, width: 20,
			handler: function(btn) {
				var row = btn.up();
				var attrRows = btn.up().up();
				if (attrRows.getRows().length > 1) {
					attrRows.remove(row);
				}
			}
		};
		
		var dataRow = new Ext.Panel({
			layout: 'column',
			width: '100%',
			height: 30,
			padding: '1 1 1 1',
			style: "border: 1px  none; border-bottom-style: solid; border-color: #416AA3;",
			items: [nameField, valField, addBtn, delBtn]
		});
		
		dataRow.getNameValuePair = function() {
			var nameField = this.items.items[0].value;
			var valueField = this.items.items[1].value;
			
			return {name: nameField, value: valueField};
		};
		
		return dataRow;
	},
	
	autoPopulate: function(node) {
		var attrRows = Ext.getCmp("attr_rows");
		var nodeAttrs = node.data().attrs;
		
		for (var i = 0; i < nodeAttrs.length; i++) {
			var attr = nodeAttrs[i];
			attrRows.add(this.makeNewDataRow(attr.key, attr.val));
		}
		
		attrRows.add(this.makeNewDataRow());
	}
});