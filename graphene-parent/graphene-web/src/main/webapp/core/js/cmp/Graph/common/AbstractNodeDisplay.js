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
    	var scope = this;
    	var legendPanel = scope.getLegendPanel();
		// assume jsonArray is array of objects, each containing "text" and "color" or "iconPath"
    	
    	if (typeof legendPanel !== "undefined") {
    		// create a legend item for each object present in jsonArray
    		for (var i = 0; i < jsonArray.length; i++) {
    			var li = jsonArray[i];
    			GLegend.addLegendItem(groupName, li.text, li.iconPath, li.color);
    		}
    		
    		// legend objects based on jsonArray
    		var newLegend = GLegend.getLegendByGroup(groupName);
    		
    		// default legend objects defined in .HTML
			var defaultLegend = GLegend.getLegendByGroup(GLegend.getDefaultGroupName());
			
			// mash the two legend arrays together to make one big legendary family
			/*var finalLegend = newLegend.concat(defaultLegend);*/
			var finalLegend = defaultLegend.concat(newLegend);
			
			// remove existing legend ( otherwise, the legend would grow with each call of this.updateLegend() )
			legendPanel.items.each(function(child) { this.remove(child); }, legendPanel);
			
			// replace with new legend
    		legendPanel.add(finalLegend);
			// legendPanel.doLayout();
    		
    		/* not an ideal solution, I know */
			setTimeout(function() {scope.getLegendPanel().doLayout();}, 500);
    	}
    },
    
    clear: function() {
		var idType, idVal, attrs;
		
		try {
			idType = this.items.items[0].items.items[0];
			idVal = this.items.items[0].items.items[1];
			attrs = this.items.items[0].items.items[2];
		} catch (e) {
			console.error("Could not clear the Node Details panel");
		}
		
		if (idType) idType.setValue("");
		if (idVal) idVal.setValue("");
		if (attrs) attrs.update("");
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