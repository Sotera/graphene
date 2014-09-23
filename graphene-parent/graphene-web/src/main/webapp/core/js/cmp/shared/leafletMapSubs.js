Ext.define("DARPA.MapVis", {
	id: null,
	map: null, // reference to map obj created in div with this.id
	L: null, // reference to Leaflet.js API
	
	defaultLat: 0,
	defaultLon: 0,
	zoomLevel: 0,
	
	marker_collection: undefined,
	circle_collection: undefined,
	polygon_collection: undefined,
	
	mapChangeHandler: function() {},
	
	constructor: function(config) {
		this.id = config.id;
		this.L = L; // should be global
		
		this.attribution = config.attribution || "Graphene &copy; Sotera";
		
		this.marker_collection = [];
		this.circle_collection = [];
		this.polygon_collection = [];
		
		this.mapChangeHandler = (typeof config.mapChangeHandler !== "undefined") ? config.mapChangeHandler : function() {};
		this.hasZoomControl = (config.zoomControl === false) ? false : true; 
		// unless specified, the default start of the map should be Washington, DC
		this.defaultLat = (typeof config.defaultLat !== "undefined") ? config.defaultLat : 38.906;
		this.defaultLon = (typeof config.defaultLon !== "undefined") ? config.defaultLon : -77.04;
		this.zoomLevel = (typeof config.zoomLevel !== "undefined") ? config.zoomLevel : 9;
	},
	
	initMap: function(/* callbackScope, callbackFn, ...args */) {
		if (this.id == null) {
			// TODO: handle error
			return;
		}
		
		// create a Leaflet map in div whose id is this.id
		this.map = this.L.map(this.id, {
			zoomControl: this.hasZoomControl
		});
		
		// set default latitude, longitude, and zoom level
		this.map.setView([this.defaultLat, this.defaultLon], this.zoomLevel);
		
		// set map tile layer using url defined in Config.mapTileUrl
		this.L.tileLayer(Config.mapTileUrl, {
			attribution: this.attribution,
			maxZoom: 18
		}).addTo(this.map);
		
		// set event handlers for this map
		this.setHandlers();
		
		// do callback if one was passed
		var args = [].slice.apply(arguments); // turn the native arguments object into an array
		var scope = args.shift(); // pop the first parameter from the arguments array. should be the function scope
		var callbackFn = args.shift(); // pop the second parameter from the arguments array.  should be a function
		if (typeof callbackFn == "function") {
			callbackFn.apply(scope, args); // pass additional parameters to callback function and execute
		}
	},
	
	getL: function() {
		return this.L;
	},
	
	addMarker: function(lat, lon, options, popupText) {
		
		if (typeof options == "undefined") {
			/* default options as defined in Leaflet.js */
			options = {
				//icon: undefined,	// Icon class to use for rendering the marker. See Icon documentation for details on how to customize the marker icon. Set to new L.Icon.Default() by default.
				clickable: true,	// If false, the marker will not emit mouse events and will act as a part of the underlying map.
				draggable: false,	// Whether the marker is draggable with mouse/touch or not.
				keyboard: true,		// Whether the marker can be tabbed to with a keyboard and clicked by pressing enter.
				title: '',			// Text for the browser tooltip that appear on marker hover (no tooltip by default).
				alt: '',			// Text for the alt attribute of the icon image (useful for accessibility).
				zIndexOffset: 0,	// By default, marker images zIndex is set automatically based on its latitude. Use this option if you want to put the marker on top of all others (or below), specifying a high value like 1000 (or high negative value, respectively).
				opacity: 1.0,		// The opacity of the marker.
				riseOnHover: false,	// If true, the marker will get on top of others when you hover the mouse over it.
				riseOffset:	250,	// The z-index offset used for the riseOnHover feature.
			};
		}
	
		var marker = this.L.marker([lat, lon], options).addTo(this.map);
		
		if (typeof popupText != "undefined") {
			marker.bindPopup(popupText);
		}
		
		this.marker_collection.push(marker);
		return marker;
	},
	
	removeAllMarkers: function() {
		for (var i = 0; i < this.marker_collection.length; i++) {
			this.map.removeLayer(this.marker_collection[i]);
		}
	},
	
	addCircle: function(lat, lon, radius, options, popupText) {
	
		if (typeof options == "undefined") {
			/* default options as defined in Leaflet.js */
			options = {
				stroke:	true,		// Whether to draw stroke along the path. Set it to false to disable borders on polygons or circles.
				color: '#03f',		// Stroke color.
				weight: 5,			// Stroke width in pixels.
				opacity: 0.5,		// Stroke opacity.
				fill: true,			// Whether to fill the path with color. Set it to false to disable filling on polygons or circles.
				fillColor: '#03f',	// Fill color.
				fillOpacity: 0.2,	// Fill opacity.
				dashArray: null,	// A string that defines the stroke dash pattern. Doesn't work on canvas-powered layers (e.g. Android 2).
				lineCap: null,		// A string that defines shape to be used at the end of the stroke.
				lineJoin: null,		// A string that defines shape to be used at the corners of the stroke.
				clickable: true,	// If false, the vector will not emit mouse events and will act as a part of the underlying map.
				pointerEvents: null,// Sets the pointer-events attribute on the path if SVG backend is used.
				className: ''		// Custom class name set on an element.
			};
		}
		
		var circle = this.L.circle([lat, lon], radius, options).addTo(this.map);
		
		if (typeof popupText != "undefined") {
			circle.bindPopup(popupText);
		}
		
		this.circle_collection.push(circle);
		return circle;
	},
	
	addPolygon: function(vertexArray, options, popupText) {
	
		if (typeof options == "undefined") {
			/* default options as defined in Leaflet.js */
			options = {
				stroke:	true,		// Whether to draw stroke along the path. Set it to false to disable borders on polygons or circles.
				color: '#03f',		// Stroke color.
				weight: 5,			// Stroke width in pixels.
				opacity: 0.5,		// Stroke opacity.
				fill: true,			// Whether to fill the path with color. Set it to false to disable filling on polygons or circles.
				fillColor: '#03f',	// Fill color.
				fillOpacity: 0.2,	// Fill opacity.
				dashArray: null,	// A string that defines the stroke dash pattern. Doesn't work on canvas-powered layers (e.g. Android 2).
				lineCap: null,		// A string that defines shape to be used at the end of the stroke.
				lineJoin: null,		// A string that defines shape to be used at the corners of the stroke.
				clickable: true,	// If false, the vector will not emit mouse events and will act as a part of the underlying map.
				pointerEvents: null,// Sets the pointer-events attribute on the path if SVG backend is used.
				className: '',		// Custom class name set on an element.
				smoothFactor: 1.0,	// How much to simplify the polyline on each zoom level. More means better performance and smoother look, and less means more accurate representation.
				noClip: false		// Disabled polyline clipping.
			};
		}
		
		var polygon = this.L.polygon(vertexArray, options).addTo(this.map);
		
		if (typeof popupText != "undefined") {
			polygon.bindPopup(popupText);
		}
		
		this.polygon_collection.push(polygon);
		return polygon;
	},
	
	removeAllPolygons: function() {
		for (var i = 0; i < this.polygon_collection.length; i++) {
			this.map.removeLayer(this.polygon_collection[i]);
		}
	},
	
	panTo: function(lat, lon) {
		
		this.map.panTo([lat, lon]);
		
		// if an element is at this location, return it to the
		// method caller so it can show() or otherwise act upon
		// it
		return this.getMarkerAt(lat, lon);
	},
	
	setHandlers: function() {
		var scope = this;
		scope.map.on('click', function(e) {
			// TODO: implement PROPER onClick handler
			scope.L.popup()
				.setLatLng(e.latlng)
				.setContent("Clicked at " + e.latlng.toString())
				.openOn(scope.map);
		});
		
		scope.map.on('contextmenu', function(e) {
			alert(e.latlng.toString());
		});
		
		scope.map.on('viewreset dragend', function(e) {
			scope.mapChangeHandler(e);
		});
	},
	
	getPolygonAt: function(lat, lon) { /* TODO: implement */ },
	getCircleAt: function(lat, lon) { /* TODO: implement */ },
	getMarkerAt: function(lat, lon) {
	
		for (var i = 0; i < this.marker_collection.length; i++) {
			var marker = this.marker_collection[i];
			var latlng = marker.getLatLng();
			
			if (latlng.lat == lat && latlng.lng == lon) {
				return marker;
			}
		}
		
		return null;
	},
	
	resize: function(w, h) {
		
	}
});