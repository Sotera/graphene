Ext.define("DARPA.ProgressBar", {
	extend: "Ext.ProgressBar",
	
	min: 0.33,
	ave: 0.66,
	max: 0.99,
	
	min_color: "#FF4444",
	ave_color: "#FFFF44",
	max_color: "#009900",
	
	constructor: function(config) {
		this.callParent(arguments);
	},
	
	changeStyle: function(cssObj) {
		var bar = this.getEl().child(".x-progress-bar", true);
		for (var prop in cssObj) {
			if (cssObj.hasOwnProperty(prop)) {
				bar.style[prop] = cssObj[prop];
			}
		}
	},
	
	listeners: {
		/*
		"update": function(obj, val) {
			var color;
			
			if (val <= this.min) {
				color = this.min_color;
			} else if (val <= this.ave) {
				color = this.ave_color;
			} else {
				color = this.max_color;
			}
			
			this.changeStyle({
				"border-color": color,
				"background-image": "-webkit-linear-gradient(top, " + color + ", #999999 50%, #999999 51%, " + color + ")"
			});
		}
		*/
	}
});