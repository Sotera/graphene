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
		var button = this.getPivotButton();
		(enable === true) ? button.enable() : button.disable();
	},
	
	enableUnPivot: function(enable) {
		var button = this.getUnPivotButton();
		(enable === true) ? button.enable() : button.disable();
	},
	
	enableHide:function(enable) {
		var button = this.getHideButton();
		(enable === true) ? button.enable() : button.disable();
	},
	
	enableUnHide: function(enable) {
		var button = this.getUnHideButton();
		(enable === true) ? button.enable() : button.disable();
	},
	
	enableExpand: function(enable) {
		var button = this.getExpandButton();
		(enable === true) ? button.enable() : button.disable();
	},
	
	enableUnExpand: function(enable) {
		var button = this.getShowButton();
		(enable === true) ? button.enable() : button.disable();
	},
	
	enableShow:function(enable) {
		var button = this.getHideButton();
		(enable === true) ? button.enable() : button.disable();
	},
	
	enableStop: function(enable) {
		var button = this.getStopButton();
		(enable === true) ? button.enable() : button.disable();
	}
});