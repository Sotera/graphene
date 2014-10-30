Ext.define("DARPA.MapPanel", {
	extend: "Ext.panel.Panel",
	border: "1",
	title: "Map Viewer",
	//layout: {
	//	align: 'stretch',
	//	type: 'hbox'
	//},
	layout: "border",
	resizable: false,
	titleAlign: "center",
	width: "100%",
	tbar: null,
	
	listeners: {
		resize: function(tab, width, height, a, b, c) {
			//tab.getMapPanel().setHeight(tab.height);
			//tab.getMapPanel().setWidth(tab.width);
			tab.mapVis.map.getContainer().style.height = height;
			tab.mapVis.map.getContainer().style.width = width - tab.getSettingsPanel.width;
		}
	},
	
	mapVis: null,
	
	constructor: function(config) {
		var scope = this;
		var mapId = config.id + "-MapVis";
		
		scope.tbar = Ext.create("DARPA.MapToolbar", {
			institution: config.institution
		});
		
		scope.mapVis = Ext.create("DARPA.MapVis", {
			id: mapId,
			mapChangeHandler: function(a, b, c) {
				// this refers to DARPA.MapVis object
				var bounds = this.map.getBounds();
				var nw = bounds.getNorthWest();
				var se = bounds.getSouthEast();
				
				var minimap = Ext.getCmp(config.id + "-DetailsPanel-MiniMap");
				if (minimap && minimap.isVisible()) {
					minimap.drawBoundingBox(nw, se);
				}
				//alert("Map state has been changed");
			},
			setBusy: function(isBusy) {
				//var tab = Ext.getCmp(config.entityId).getEntityGraph();
				//utils.setBlink(tab, busy);
			}
		});
		
		var mapContainer = Ext.create("Ext.container.Container", {
			//flex: 1,
			height: 926,
			width: "100%",
			id: mapId,
			region: "center"
		});
		
		var settings = Ext.create("DARPA.MapDetailsPanel", {
			id: config.id + "-DetailsPanel",
			width: 320,
			height: "auto",
			collapsible: true,
			collapseDirection: "left",
			region: "west",
			mapRef: scope
		});
		
		scope.items = [settings, mapContainer];
		scope.callParent(arguments);
	},
	
	afterRender: function() {
		var scope = this;
		
		scope.getMapPanel().setHeight(scope.height);
		scope.getMapPanel().setWidth(scope.width);
		scope.mapVis.initMap(scope, scope.load, scope.entityId, "c", "d");
		scope.callParent(arguments);
	},
	
	afterLayout: function() {
		//this.mapVis.initMap();
		//this.callParent(arguments);
	},
	
	load: function(acNo) {
		// callback function of map init.
		// load map with whatever data points we can get using the entity id
		// constrain with current map bounds
		
		this.getSettingsPanel().load(acNo);
	},
	
	getMapPanel: function() {
		return this.items.items[1];
	},
	getSettingsPanel: function() {
		return this.items.items[0];
	}
});