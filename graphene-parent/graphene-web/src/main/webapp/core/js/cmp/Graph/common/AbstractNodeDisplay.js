Ext.define("DARPA.AbstractNodeDisplay", {
	extend: "Ext.Container",   
	width: 'auto',
	height: 'auto',
	border: 0,
	margin: 0,
	layout: {
		type:'vbox',
		align:'stretch'
	},
	
	constructor: function(config) {
		this.callParent(arguments);
	}, // constructor
	
	showNodeAttrs: function(node) {
    	// TODO implement in extending class
	},
    
	showEdgeAttrs: function(edge) {
    	// TODO implement in extending class
	},
	
    getIdentifierType: function() {
    	// TODO implement in extending class
    },
    
    getIdentifier: function() {
    	// TODO implement in extending class
    },
    
    getIdentifierDetails: function() {
    	// TODO implement in extending class
    },
    
 	getGraph:function() {
    	var self = this;
    	var path = self.id.split('-'); // this will be the entity ID
		var graphId = path[0] + '-' + path[1];
		var graph = Ext.getCmp(graphId);
		return graph;
    },        
    
	getPivotButton: function() 		{ return Ext.getCmp(this.id + "-Actions").getPivotButton(); },
	getUnPivotButton: function() 	{ return Ext.getCmp(this.id + "-Actions").getUnPivotButton(); },
	getHideButton: function() 		{ return Ext.getCmp(this.id + "-Actions").getHideButton(); },
	getUnHideButton: function() 	{ return Ext.getCmp(this.id + "-Actions").getUnHideButton(); },
	getExpandButton: function() 	{ return Ext.getCmp(this.id + "-Actions").getExpandButton(); },
	getUnExpandButton: function() 	{ return Ext.getCmp(this.id + "-Actions").getUnExpandButton(); },
	getShowButton: function() 		{ return Ext.getCmp(this.id + "-Actions").getShowButton(); },
	getStopButton: function() 		{ return Ext.getCmp(this.id + "-Actions").getStopButton(); },
    
	enablePivot:function(enable) {
		var self = this;
		var button = self.getPivotButton();
		
		if (enable) {
			button.enable();
		} else {
			button.disable();
		}
	},
	
	enableHide:function(enable) {
		var self = this;
		var button = self.getHideButton();
		
		if (enable) {
			button.enable();
		} else {
			button.disable();
		}
	},
	
	enableShow:function(enable) {
		var self = this;
		var button = self.getShowButton();
		if (enable) {
			button.enable();
		} else {
			button.disable();
		}
	}
});