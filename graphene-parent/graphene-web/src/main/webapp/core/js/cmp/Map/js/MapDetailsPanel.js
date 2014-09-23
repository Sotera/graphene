Ext.define("DARPA.MapDetailsPanel", {
	extend: "Ext.panel.Panel",
	
	width: "auto",
	height: "auto",
	collapsible: true,
	collapseDirection: "left",
	region: "west",
	
	layout: {
		type: "vbox",
		align: "stretch"
	},
	
	mapRef: null, // reference to MapPanel parent
	dataStore: null, 
	
	constructor: function(config) {
		var scope = this;
		
		var modelName = "test_model";
		
		Ext.define("MapModel", {
			extend: Ext.data.Model,
			fields: [
				{name: "id", type: "string"},
				{name: "name", type: "string"},
				{name: "lat", type: "long"},
				{name: "lon", type: "long"}
			]
		});
		
		var store = new Ext.data.Store({
			model: "MapModel",
			proxy: "ajax",
			url: "/test.json",
			reader: {
				type: "json",
				root: "points"
			},
			autoLoad: false,
			listeners: {
				datachanged: function(store, b) {
					var grid = store.grid;
					//var frame = grid.up().up();
					//var frame = Ext.getCmp(config.id.split("-DetailsPanel")[0]);
					var frame = scope.up();
					frame.mapVis.removeAllMarkers();
					
					// FIXME: the map that these points are being drawn on does not match
					// the map represented by the minimap.  Minimap map pane is one pane 
					// left to the marked map pane
					var records = store.data.items;
					for (var i = 0; i < records.length; i++) {
						var rec = records[i];
						var name = rec.get('name');
						var lat = rec.get("lat");
						var lon = rec.get("lon");
						
						// temporary implementation
						// datachanged handler should not be responsible for creating points
						// on the map;  should be done on mapInit after a successful GET request
						// this MapDetailsPanel should have a reference to existing points or
						// know how to get them
						frame.mapVis.addMarker(lat, lon, undefined, name);
					}
				}
			}
		});
		
		var grid = Ext.create('Ext.grid.Panel', {
			title: 'Entities on Map',
			store: store,
			columns: [
				{text: 'ID', width: 50, dataIndex:'id'},
				{text: 'Name', flex: 1, dataIndex:'name'},
				{
					xtype: 'actioncolumn', 
					width: 20,
					items: [{
						icon: Config.imagesUrl + 'Play_Hover14.png',  // Use a URL in the icon config
						tooltip: 'View on Map',
						handler: function(grid, rowIndex, colIndex) {
							var rec = grid.getStore().getAt(rowIndex);
							var name = rec.get('name');
							var lat = rec.get("lat");
							var lon = rec.get("lon");
							var marker = scope.up().mapVis.panTo(lat, lon);
							if (typeof marker !== "undefined") {
								//marker.openPopup();
								scope.up().mapVis.L.popup()
									.setLatLng([lat, lon])
									.setContent(name)
									.openOn(scope.up().mapVis.map);
							}
						}
					}]
				}
			],
			width: "auto",
			//height: 350
			flex: 1
		});
		store.grid = grid;
		scope.items = [
			grid,
			{
				xtype: "button",
				text: "Get Bounds",
				handler: function(e) {
					var bounds = scope.up().mapVis.map.getBounds();
					var nw = bounds.getNorthWest();
					var ne = bounds.getNorthEast();
					var se = bounds.getSouthEast();
					alert("Bounds - NorthWest: " + nw + ", NorthEast: " + ne + ".");
				}
			},
			
			Ext.create("DARPA.MiniMap",{
				id: config.id + "-MiniMap",
				width: 320
			})
		];
		scope.callParent(arguments);
	},
	
	getDataStore: function() {
		return this.dataStore;
	},
	
	getGrid: function() {
		return this.items.items[0];
	},
	
	load: function(acNo) {
		var self = this;
		var grid = self.getGrid();
		var store = grid.getStore();
		var proxy = store.getProxy();
		
		// load hard-coded data for now.  hit service later
		
		store.loadData([
			{id: "AK", name: "Juneau", lat: 58.286, lon: 225.59},
			{id: "WA", name: "Seattle", lat: 47.59, lon: 237.69},
			{id: "OR", name: "Salem", lat: 44.91, lon: 237.97},
			{id: "CA", name: "Sacramento", lat: 38.56, lon: 238.55},
			{id: "ID", name: "Boise", lat: 43.58, lon: 243.82},
			{id: "NV", name: "Carson City", lat: 39.16, lon: 240.23},
			{id: "MO", name: "Helena", lat: 46.57, lon: 247.98},
			{id: "UT", name: "Salt Lake City", lat: 40.7, lon: 248.13},
			{id: "AZ", name: "Phoenix", lat: 33.37, lon: 247.96},
			{id: "WY", name: "Cheyenne", lat: 41.11, lon: 255.14},
			{id: "CO", name: "Denver", lat: 39.73, lon: 255.03},
			{id: "NM", name: "Santa Fe", lat: 35.67, lon: 254.11},
			{id: "ND", name: "Bismarck", lat: 46.76, lon: 259.27},
			{id: "SD", name: "Pierre", lat: 44.35, lon: 259.71},
			{id: "NE", name: "Omaha", lat: 41.21, lon: 264.04},
			{id: "KA", name: "Topeka", lat: 39.04, lon: 264.34},
			{id: "OK", name: "Oklahoma City", lat: 35.46, lon: 262.50},
			{id: "TX", name: "Austin", lat: 30.26, lon: 262.29},
			{id: "MN", name: "Minneapolis", lat: 44.93, lon: 266.79},
			{id: "IA", name: "Des Moines", lat: 41.55, lon: 266.46},
			{id: "MO", name: "Jefferson City", lat: 38.53, lon: 267.86},
			{id: "AS", name: "Little Rock", lat: 34.73, lon: 267.72},
			{id: "LA", name: "Baton Rouge", lat: 30.44, lon: 268.85},
			{id: "WI", name: "Madison", lat: 43.06, lon: 270.63},
			{id: "IL", name: "Springfield", lat: 39.78, lon: 270.37},
			{id: "KN", name: "Frankfort", lat: 38.19, lon: 275.13},
			{id: "TN", name: "Nashville", lat: 36.15, lon: 273.24},
			{id: "MS", name: "Jackson", lat: 32.27, lon: 269.82},
			{id: "MI", name: "Traverse City", lat: 44.75, lon: 274.38},
			{id: "IN", name: "Indianapolis", lat: 39.76, lon: 273.86},
			{id: "AL", name: "Montgomery", lat: 32.36, lon: 273.70},
			{id: "FL", name: "Tallahassee", lat: 30.42, lon: 275.73},
			{id: "OH", name: "Columbus", lat: 39.95, lon: 277.00},
			{id: "GA", name: "Atlanta", lat: 33.73, lon: 275.63},
			{id: "WV", name: "Charleston", lat: 38.32, lon: 278.38},
			{id: "NC", name: "Raleigh", lat: 35.77, lon: 281.39},
			{id: "SC", name: "Columbia", lat: 34.00, lon: 278.94},
			{id: "PA", name: "Harrisburg", lat: 40.25, lon: 283.12},
			{id: "MD", name: "Annapolis", lat: 38.97, lon: 283.50},
			{id: "VA", name: "Richmond", lat: 37.53, lon: 282.55},
			{id: "DE", name: "Dover", lat: 39.15, lon: 284.48},
			{id: "NJ", name: "Trenton", lat: 40.21, lon: 285.23},
			{id: "NY", name: "Albany", lat: 42.64, lon: 286.24},
			{id: "CN", name: "Hartford", lat: 41.76, lon: 287.32},
			{id: "RI", name: "Providence", lat: 41.82, lon: 288.58},
			{id: "MA", name: "Boston", lat: 42.35, lon: 288.93},
			{id: "VT", name: "Monpelier", lat: 44.25, lon: 287.41},
			{id: "NH", name: "Concord", lat: 43.18, lon: 288.48},
			{id: "ME", name: "Augusta", lat: 44.31, lon: 290.21},
			{id: "HI", name: "Honolulu", lat: 21.28, lon: 202.159}
		]);
	}
});

Ext.define("DARPA.MiniMap", {
	extend: "Ext.panel.Panel",
	
	constructor: function(config) {
		var scope = this;
		var mapId = config.id + "-MapObj"
		
		scope.mapVis = Ext.create("DARPA.MapVis", {
			id: mapId,
			mapChangeHandler: function(a, b, c) {
			},
			setBusy: function(isBusy) {
				//var tab = Ext.getCmp(config.entityId).getEntityGraph();
				//utils.setBlink(tab, busy);
			},
			zoomLevel: 0,
			zoomControl: false,
			attribution: "Minimap"
		});
		
		var mapContainer = Ext.create("Ext.container.Container", {
			//flex: 1,
			height: 150,
			width: 320,//"100%",
			id: mapId
		});
		
		scope.items = [
			mapContainer
		];
		
		scope.callParent(arguments);
	},
	
	afterRender: function() {
		var scope = this;
		
		scope.mapVis.initMap();
		//scope.mapVis.map.fitWorld();
		
		// Disable drag and zoom handlers.
		scope.mapVis.map.off("click");
		scope.mapVis.map.dragging.disable();
		scope.mapVis.map.touchZoom.disable();
		scope.mapVis.map.doubleClickZoom.disable();
		scope.mapVis.map.scrollWheelZoom.disable();
		var bounds = scope.mapVis.map.getBounds();

		// Disable tap handler, if present.
		if (scope.mapVis.map.tap) scope.mapVis.map.tap.disable();
		
		scope.callParent(arguments);
	},
	
	drawBoundingBox: function(topLeft, bottomRight) {
		this.mapVis.removeAllPolygons();
		var bounds = [
			[topLeft.lat, bottomRight.lng],
			[topLeft.lat, topLeft.lng],
			[bottomRight.lat, topLeft.lng],
			[bottomRight.lat, bottomRight.lng]
		];
		this.mapVis.addPolygon(bounds, {});
	}
});