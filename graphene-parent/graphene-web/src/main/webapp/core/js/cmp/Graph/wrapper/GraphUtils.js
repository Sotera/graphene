function Utils(graphRef) {
	var _this = this;
	
	/**
	 * 	returns whether the passed Cytoscape node is a leaf or not; i.e. one neighbor only
	 * 		@param node:CSNode - the node to test for 'leafiness'
	 * 		@returns boolean - whether given node is a leaf or not  
	 */
	_this.isLeaf = function(node) {
		var possibleNeighbors = node.connectedEdges().connectedNodes();
		var confirmedNeighbors = [];
		
		possibleNeighbors.each(function(index, n) {
			if (n.data("id") != node.data("id")) {
				confirmedNeighbors.push(n.data("id"));
			}
			
			// if confirmed neighbors already > 1, break
			if (confirmedNeighbors.length > 1) {
				return false;
			}
		});
		
		return confirmedNeighbors.length == 1;
	};
	
	/**
	 * 	Export the current graph as a PNG image in Base64 representation
	 * 		@param options:Object - default parameters for PNG conversion
	 * 		@returns String - Base64 representation of PNG image of the graph
	 */
	_this.toPng = function(options) {
		if (typeof options == "undefined") {
			options = {};
		}
		
		var defaults = {
			bg: (typeof options.bg != "undefined") ? options.bg : undefined,
			full: (typeof options.full != "undefined") ? options.full : false,
			scale: (typeof options.scale != "undefined") ? options.scale : 1
		};
		
		return graphRef.gv.png(defaults);
	};
	
	/**
	 *
	 */
	_this.updateProgress = function(message, progressAmount) {
		if (progressAmount == undefined || (progressAmount > 1 || progressAmount < 0)) {
			progressAmount = 1;
		}
		
		try {
			graphRef.getOwner().getProgressBar().updateProgress(progressAmount, message);
		} catch(e) {
			alert(message);
		}
	};
	
	/**
	 *
	 */
	_this.$ = function(selector, callback) {
		var collection = graphRef.gv.elements(selector);
		if (typeof callback == "function") {
			collection.each(callback);
		} else {
			return collection;
		}
	};
};