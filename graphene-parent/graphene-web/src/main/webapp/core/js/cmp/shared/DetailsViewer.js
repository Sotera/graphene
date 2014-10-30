Ext.define("DARPA.DetailsViewer", {
	extend: 'Ext.Window',
	title: 'Email Viewer',
	height: 600,
	width: 500,
	minHeight: 300,
	minWidth: 300,
	closeAction: 'destroy',
	plain: 'true',
	border: false,
	resizable: true,
	modal: false,
	layout: {
		//type: 'vbox',
		//pack: 'left'
		type: 'vbox',
		pack: 'start',
		align: 'stretch'
	},
	
	timeStamp: null,
	
	constructor: function(config) {
		var thisWindow = this;
		
		var to_field = Ext.create("Ext.form.TextField", {
			value: "THIS IS TO",
			width: "100%",
			height: 20,
			fieldLabel: "To",
			readOnly: true
		});
		
		var from_field = Ext.create("Ext.form.TextField", {
			value: "THIS IS FROM",
			width: "100%",
			height: 20,
			fieldLabel: "From",
			readOnly: true
		});
		
		var subject_field = Ext.create("Ext.form.TextField", {
			value: "THIS IS SUBJECT",
			width: "100%",
			height: 20,
			fieldLabel: "Subject",
			readOnly: true
		});
		
		var email_body = Ext.create("Ext.form.TextArea", {
			value: "THIS IS BODY",
			width: "100%",
			height: '100%',
			readOnly: true,
			autoScroll: true
		});
		
		var time_stamp = Ext.create("Ext.form.Label", {
			text: config.timeStamp,
			height: 15
		});
		
		this.items = [
			to_field,		//[0]
			from_field,		//[1]
			subject_field,	//[2]
			//email_body		//[3]
			{
				xtype: 'container',
				title: null,
				flex: 1,
				items: email_body
			}, {
				xtype: 'container',
				title: null,
				layout: {
					type: 'hbox',
					pack: 'end'
				},
				items: time_stamp
			}
		];
		
		this.callParent(arguments);
	},
	
	setTo: function(str) 		{ this.items.items[0].setValue(str); },
	setFrom: function(str) 		{ this.items.items[1].setValue(str); },
	setSubject: function(str) 	{ this.items.items[2].setValue(str); },
	setBody: function(str) 		{ this.items.items[3].items.items[0].setValue(str); },
	setTimeStamp: function(str)	{ this.items.items[4].setValue(str); }
});