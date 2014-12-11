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
		return null;
	},
    
	showEdgeAttrs: function(edge) {
    	// TODO implement in extending class
		return null;
	},
	
    getIdentifierType: function() {
    	// TODO implement in extending class
		return null;
    },
    
    getIdentifier: function() {
    	// TODO implement in extending class
		return null;
    },
    
    getIdentifierDetails: function() {
    	// TODO implement in extending class
		return null;
    },
    
    getLegendPanel: function() {
    	// TODO implement in extending class
		return null;
    },
    
 	getGraph:function() {
    	var self = this;
    	var path = self.id.split('-'); // this will be the entity ID
		var graphId = path[0] + '-' + path[1];
		var graph = Ext.getCmp(graphId);
		return graph;
    },        
    
    updateLegend: function(jsonArray, groupName) {
    	var legendPanel = this.getLegendPanel();
		// assume jsonArray is array of objects, each containing "text" and "color" or "iconPath"
    	
    	if (typeof legendPanel !== "undefined") {
    		// create a legend item for each object present in jsonArray
    		while (jsonArray.length > 0) {
    			var i = jsonArray.shift();
    			GLegend.addLegendItem(groupName, i.text, i.iconPath, i.color);
    		}
    		
    		// legend objects based on jsonArray
    		var newLegend = GLegend.getLegendByGroup(groupName);
    		
    		// default legend objects defined in .HTML
			var defaultLegend = GLegend.getLegendByGroup(GLegend.getDefaultGroupName());
			
			// mash the two legend arrays together to make one big legendary family
			var finalLegend = newLegend.concat(defaultLegend);
			
			// remove existing legend ( otherwise, the legend would grow with each call of this.updateLegend() )
			legendPanel.items.each(function(child) { this.remove(child); }, legendPanel);
			
			// replace with new legend
    		legendPanel.add(finalLegend);
			legendPanel.doLayout();
    	}
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