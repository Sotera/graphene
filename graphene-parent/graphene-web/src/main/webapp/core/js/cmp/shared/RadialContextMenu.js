/*
 * Constructor for generic radial context menu options for the Cytoscape plugin.
 * Assumes Cytoscape.js is not initialized upon this creation, so it will be passed
 * as the parameter to create the plugin elsewhere.
 */
function RadialContextMenu(config) {
	var _rm = this;

	var _fnScope = undefined;
	
	/* context menu configuration properties; assigned a default if not represented in config parameter */
	this.menuRadius =			(typeof config.menuRadius !== "undefined")		? config.menuRadius : 75;
	this.selector =				(typeof config.selector !== "undefined")		? config.selector : "node";
	this.fillColor =			(typeof config.fillColor !== "undefined")		? config.fillColor : "rgba(0, 0, 200, 0.75)";
	this.activeFillColor =		(typeof config.activeFillColor !== "undefined")	? config.activeFillColor : "rgba(92, 194, 237, 0.75)";
	this.activePadding =		(typeof config.activePadding !== "undefined")	? config.activePadding : 0;
	this.indicatorSize =		(typeof config.indicatorSize !== "undefined")	? config.indicatorSize : 24;
	this.separatorWidth =		(typeof config.separatorWidth !== "undefined")	? config.separatorWidth : 3;
	this.itemColor =			(typeof config.itemColor !== "undefined")		? config.itemColor : 'white';
	this.zIndex =				(typeof config.zIndex !== "undefined")			? config.zIndex : 9999;
	this.itemTextShadowColor =	(typeof config.itemTextShadowColor !== "undefined")	? config.itemTextShadowColor : 'black';
	this.spotlightPadding =		(typeof config.spotlightPadding !== "undefined")	? config.spotlightPadding : 4; 
	this.minSpotlightRadius =	(typeof config.minSpotlightRadius !== "undefined")	? config.minSpotlightRadius : 24;
	this.maxSpotlightRadius =	(typeof config.maxSpotlightRadius !== "undefined")	? config.maxSpotlightRadius : 38;
	
	/* default commands any Cytoscape radial context menu will have */
	this.commands = [{
		"content": "Expand",
		"select": function() {
			var element = this;
			if (_rm.getScope().owner.expand) {
				_rm.getScope().owner.expand(element);
			} else {
				console.error("expand() is undefined for current scope.");
			}
		}
	}, {
		"content": "Pivot",
		"select": function() {
			var element = this;
			if (_rm.getScope().owner.pivot) {
				_rm.getScope().owner.pivot(element);
			} else {
				console.error("pivot() is undefined for current scope.");
			}
		}
	}, {
		"content": "Hide",
		"select": function() {
			var element = this;
			if (_rm.getScope().owner.hideNode) {
				_rm.getScope().owner.hideNode(element);
			} else {
				console.error("hideNode() is undefined for current scope.");
			}
		}
	}, {
		"content": "Show Details",
		"select": function() {
			var element = this;
			var disp = _rm.getScope().owner.getNodeDisplay();
			if (disp && disp.setAttrs && element.data) {
				disp.setAttrs(element.data());
			} else {
				console.error("getNodeDisplay() is undefined for current scope.");
			}
		}
	}];
	
	this.setScope = function(scope) {
		_fnScope = scope;

		return this; // for method chaining
	};
	
	this.getScope = function() {
		return _fnScope;
	};
	
	/*
	this.getConfJson = function() {
		return {
			menuRadius : this.menuRadius,
			selector: this.selector,
			fillColor: this.fillColor,
			activeFillColor : this.activeFillColor,
			activePadding: this.activePadding,
			commands: this.commands
		};
	};
	*/
	
	this.addCommands = function(commands) {
		for (var i = 0, l = commands.length; i < l; i++) {
			this.addCommand(commands[i]);
		}
		return this; // for method chaining
	}; 
	
	this.addCommand = function( /*text, func[, params...]*/ ) {
		var args = [].slice.apply(arguments);
		var text = args.shift();	// pop() the first argument in args[]
		var func = args.shift();	// pop() the second argument in args[]
	
		// args[] is now the remaining parameters you passed, if any
		
		this.commands.push({
			"content" : text,
			"select" : function() {
				var element = this;
				var argClone = args.slice();
				argClone.unshift(element);
				func.apply(_rm.getScope(), argClone);
			}
		});
		
		return this; // for method chaining
	};
}