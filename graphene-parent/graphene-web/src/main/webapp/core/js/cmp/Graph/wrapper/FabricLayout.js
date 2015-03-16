;(function FabricLayout($$){
	
	var defaults = {
		ready: function(){},
		stop: function(){}
	};
	
	function FabricLayout(options) {
		this.options = $$.util.extend(true, {}, defaults, options);
	}
	
	FabricLayout.prototype.run = function() {
		var options = this.options;
		var eles = options.eles;
		var layout = this;
		
		var cy = options.cy;
		
		var x_margin = 50;
		var y_margin = 50;
		
		var node_pos = {};
		
		layout.trigger("layoutstart");
		
		eles.nodes().each(function(i, node) {
			var num_edges = node.connectedEdges().length;
			if (!node_pos.hasOwnProperty(num_edges)) {
				node_pos[num_edges] = [];
			}
			node_pos[num_edges].push(node);
		});
		
		var column = 1;
		var rank = 1;
		for (var key in node_pos) {
			if (!node_pos.hasOwnProperty(key)) continue;
			var nodes_arr = node_pos[key];
			for (var i = 0; i < nodes_arr.length; i++) {
				var node = nodes_arr[i];
				node.position({
					x: x_margin + column,
					y: y_margin + rank
				});
				column += 30;
			}
			column = 1;
			rank += 50;
		}
		
		eles.edges().each(function(i, edge) {
			edge.addClass("toggled-hide");
			edge.hide();
		});
		
		// trigger layoutready when each node has had its position set at least once
		layout.one('layoutready', options.ready);
		layout.trigger('layoutready');

		// trigger layoutstop when the layout stops (e.g. finishes)
		layout.one('layoutstop', options.stop);
		layout.trigger('layoutstop');

		return this; // chaining
	};
	
	FabricLayout.prototype.stop = function() {
		
		return this; // chaining
	};
	
	// register the layout
	$$('layout', 'fabric', FabricLayout);
})(cytoscape);