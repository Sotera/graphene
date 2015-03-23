function DijkstraManager(graphRef) {
	var _this = this;
	
	this.root = null;		// root node from which the paths will originate
	this.dest = null;		// the terminal or destination node
	this.isWaiting = false;	// whether this manager is waiting for destination node or not
	
	this.setRoot = function(node) { 
		if (typeof node.isNode == "function" && node.isNode() == true) {
			_this.root = node;
			_this.setWait(true);
			graphRef.utils.updateProgress("Root node selected.  Left-click another node to show the shortest path between them.", 0);
		}
	};
	
	this.setDest = function(node) {
		if (typeof node.isNode == "function" && node.isNode() == true) {
			_this.dest = node;
			graphRef.utils.updateProgress("100%", 1);
		}
	};
	
	this.setWait = function(bool) {
		_this.isWaiting = (bool === true) ? true : false;
	};
	
	this.run = function(followDirection, onlyTraverseVisibleElements) {
		var selector = (onlyTraverseVisibleElements === true) ? "[?visible]" : "";
		var directed = (followDirection === true) ? true : false;
		
		var eles = graphRef.gv.elements(selector);
		
		// function that returns edge weight where this == current edge
		var getEdgeWeight = function() {
			var a = this.data("weight"); 
			if (typeof a == "undefined" || parseInt(a) <= 0) {
				console.log("Could not get valid weight from edge id='" + this.data().id + "'");
				a = "1";
			}
			//console.log("Dijkstra: weight is " + a); 
			return parseInt(a);
		};
		
		// the last boolean parameter of dijkstra() is a flag to only follow edge directions, yes/no
		var result = eles.dijkstra(_this.root, getEdgeWeight, directed);
		return {
			paths: result.pathTo(_this.dest),
			distance: result.distanceTo(_this.dest)
		};
	};
	
	this.clear = function() {
		_this.root = null;
		_this.dest = null;
		_this.isWaiting = false;
		graphRef.gv.elements().removeClass("on-path");
	};
};