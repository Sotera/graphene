Ext.define("DARPA.NodeMergeDialog", {
	extend: 'Ext.Window',
	title: 'Node Merge Confirmation',
	height: 200,
	width: 400,
	closeAction: 'destroy',
	plain: 'true',
	border: false,
	resizable: false,
	modal: true,
	layout: 'fit',
	buttonAlign: 'center',
	
	confirmFn: undefined,
	cancelFn: undefined,
	
	constructor: function(config) {
		var thisWindow = this;
		
		var textArea = Ext.create("Ext.form.FormPanel", {
			title: "Please give a brief explanation for this action",
			width: "100%",
			height: "100%",
			bodyPadding: 5,
			id: "reason_panel",
			layout: 'fit',
			items: [{
				xtype: "textareafield",
				id: "reason_text",
				allowBlank: false,
				grow: true,
				anchor: "100%"
			}]
		});
		
		this.items = [textArea];
		
		this.buttons = [{
			text: "Confirm",
			type: "Submit",
			handler: function(btn) {
				if (Ext.getCmp("reason_panel").getForm().isValid()) {
					if (typeof thisWindow.confirmFn == "function") {
						var reason = Ext.getCmp("reason_text").getValue();
						thisWindow.confirmFn(reason);
					} else {
						console.error("No confirmation callback handler was defined.");
					}
					thisWindow.close();
				}
			}
		}, {
			text: "Cancel",
			handler: function(btn) {
				if (typeof thisWindow.cancelFn == "function") {
					thisWindow.cancelFn();
				} else {
					console.error("No cancellation callback handler was defined.");
				}
				thisWindow.close();
			}
		}];
		
		this.callParent(arguments);
	}
});