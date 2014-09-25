Ext.define("DARPA.MapToolbar", {
	extend: "Ext.toolbar.Toolbar",
	height: 24,
	border: false,
	//margin: "0, 2, 0, 10",
	
	institution: "",
	
	constructor: function(config) {
		var self = this;
		
		self.institution = config.institution || "";
	
		var label = {
			xtype: "label",
			text: "",
			disabled: false,
			margin: "2 2 2 300",
			width: 170,
			listeners: {
				beforerender: function(self, opts) {
					var toolbar = self.up();
					self.setText("INSTITUTION: " + toolbar.institution);
				}
			}
		};
		
		self.items = [label];
		self.callParent(arguments);
	}
});