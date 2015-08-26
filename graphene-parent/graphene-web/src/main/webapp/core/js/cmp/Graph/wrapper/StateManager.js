function StateManager(graphRef) {
	var _this = this;
	
	/**
	 *	Gather the metadata and graph JSON for this cytoscape graph and
	 *	return it.
	 *		@returns json:Object
	 */
	this.importGraphJSON = function(json) {
		if (json && json.graph && json.graph.nodes && json.graph.nodes.length > 0) {
			graphRef.clear();
			graphRef.searchName = json.searchName;
			
			_this.add(json.graph);
			
			graphRef.gv.elements().each(function(i, ele) {
				if (ele.data("visible") == false) {
					ele.hide();
				}
			});
			
			graphRef.gv.fit();
		} else {
			console.error("The graph json for import is empty or invalid");
		}
	};
	
	/**
	 *	Loads a pre-defined cytoscape graph's JSON object, preserving the elements'
	 *	visibility and positions on the X/Y plane.
	 *		@param json:Object - graph JSON to load into cytoscape.
	 */
	this.exportGraphJSON = function() {
		var json = {};
		json.timestamp = new Date().getTime();
		json.searchName = graphRef.getSearchName();
		json.currentLayout = graphRef.getCurrentLayout();
		
		var nodeJSON = graphRef.gv.nodes().jsons();
		var edgeJSON = graphRef.gv.edges().jsons();
		
		json.graph = {};
		json.graph.nodes = nodeJSON;
		json.graph.edges = edgeJSON;
		
		return json;
	};
	
	/**
	 *	Remove (not hide) nodes from the cytosape graph AND the json representation
	 *	stored by most parent containers of a graph.  Connected edges are removed as well.
	 *		@param nodes:Array - cytosape nodes to be deleted
	 *		@param bypass:boolean - force deletion
	 */
	this.deleteNodes = function(nodes, bypass) {
		var errorMsg = null;
		var edgesToDelete = [];
		console.log("deleting nodes: " + nodes);
        console.log("bypass: " + bypass);
		for (var i = 0; i < nodes.length; i++) {
			var id = nodes[i].data("id");
			
			// if node is not user-generated && bypass is not true, continue;
			if (nodes[i].data("generated") !== true && bypass !== true) {
				errorMsg = "You can only delete user-generated nodes.";
				continue;
			}
			
			edgesToDelete = edgesToDelete.concat(nodes[i].connectedEdges());
			
			// remove all nodes whose id == id
			graphRef.gv.remove("node[id='" + id + "']");
			
			// most graph implementations store a json representation of their cyto graphs;
			// remove traces of nodes involved with selected node IDs
			if (typeof graphRef.owner.json !== "undefined") {
				var json = graphRef.owner.json;
				for (var j = 0; j < json.nodes.length; j++) {
					var nodeJSON = json.nodes[j];
					if (nodeJSON.data.id == id) {
						json.nodes.splice(j, 1);
						break;  // there should only be one node with this id; stop iterating
					}
				}
			}
		}
		
		_this.deleteEdges(edgesToDelete, bypass);
		
		if (errorMsg != null) graphRef.utils.updateProgress(errorMsg, 1);
	};
	
	/**
	 *	Remove (not hide) edges from the cytosape graph AND the json representation
	 *	stored by most parent containers of a graph.
	 *		@param edges:Array - cytosape edges to be deleted
	 *		@param bypass:boolean - force deletion
	 */
	this.deleteEdges = function(edges, bypass) {
		var errorMsg = null;
		
		for (var i = 0; i < edges.length; i++) {
			var id = edges[i].data("id");
			
			// if edge is not user-generated && bypass is not true, continue;
			if (edges[i].data("generated") !== true && bypass !== true) {
				errorMsg = "You can only delete user-generated edges.";
				continue;
			}
			
			// remove all edges with matching id
			graphRef.gv.remove("edge[id= '" + id + "']");
			
			// most graph implementations store a json representation of their cyto graphs;
			// remove traces of edges involved with selected id
			if (typeof graphRef.owner.json !== "undefined") {
				var json = graphRef.owner.json;
				for (var j = 0; j < json.edges.length; j++) {
					var edgeJSON = graphRef.owner.json.edges[j];
					if (edgeJSON.data.id == id) {
						json.edges.splice(j, 1);
						break; // there should only be one edge with this id;  stop iterating
					}
				}
			}
		}
		
		if (errorMsg != null) graphRef.utils.updateProgress(errorMsg, 1);
	};
	
	this.add = function(json) {
		graphRef.gv.add(json);
	};
	
	this.remove = function() {
		//TODO
	};
	
	/**
	 *	Hides or shows the given node based on the isVisible boolean.  IsFilter is
	 *	used to categorize this action as a "hide" event or a "filter" event.
	 *	Connected edges to this node are hidden/filtered as well.
	 *		@param node:Cytoscape Node - node to hide or show
	 *		@param isVisible:boolean - true to show, false to hide
	 *		@param isFilter:boolean - true is filter, false is hide
	 */
	this.setNodeVisibility = function(node, isVisible, isFilter) {
		var edges = node.connectedEdges();
		var cls = isFilter ? "toggled-filter" : "toggled-hide";
		
		if (isVisible == true) {
			node.show();
			node.data().visible = true;
			node.removeClass(cls);
		}
		else if (isVisible == false) {
			node.hide();
			node.data().visible = false;
			node.addClass(cls);
		}
		
		// edges are hidden/shown by cytoscape automatically based on whether their to- and from- nodes are visible
		/*
		edges.each(function(i, e) {
			_this.setEdgeVisibility(e, isVisible, isFilter);
		});
		*/
	};
	
	/**
	 *	Hides or shows the given edge based on the isVisible boolean.  IsFilter is
	 *	used to categorize this action as a "hide" event or a "filter" event
	 *		@param edge:Cytoscape Edge - edge to hide or show
	 *		@param isVisible:boolean - true to show, false to hide
	 *		@param isFilter:boolean - true is filter, false is hide
	 */
	this.setEdgeVisibility = function(edge, isVisible, isFilter) {
		var cls = isFilter ? "toggled-filter" : "toggled-hide";
		
		if (isVisible == true) {
			edge.show();
			edge.data().visible = true;
			edge.removeClass(cls);
		}
		else if (isVisible == false) {
			edge.hide();
			edge.data().visible = false;
			edge.addClass(cls);
		}
	};
	
	/**
	 * 	A separate filtering action from Hiding/Showing.  Nodes are filtered based on whether their 
	 * 	"name" field contains any of the search terms provided.
	 * 		@param str:String - A term or comma-separated terms. Nodes that contain a term are shown, the rest are hidden
	 * 		@param nodeProperty:String - (optional) The data field of the node to filter against. Defaults to "name"
	 */
	this.applyFilter = function(str, nodeProperty) {
		var nodes2Hide = []; 
		var nodes2Show = [];
		var isFilter = true;
		var terms = str.toUpperCase().split(",");
		var dataField = (typeof nodeProperty == "string") ? nodeProperty : "name";
		
		var _nodeNameContainsTerm = function(name, terms) {
	        for (var i = 0; i < terms.length; i++) {
	            if (name.indexOf(terms[i].trim()) >= 0) {
	                return true;
	            }
	        }
	        return false;
		};
		
		// for each node (that does NOT have the toggled-hide class), determine if its
		// name contains any of the search terms
		graphRef.gv.nodes().not(".toggled-hide").each(function(i, node) {
			var property = node.data(dataField);
			if (typeof property != "undefined"){
				if ( !node.hasClass("toggled-show") && _nodeNameContainsTerm(property.toUpperCase(), terms) ) {
					nodes2Show.push(node);
				} else {
					nodes2Hide.push(node);
				}
			}
		});

		for (var i = 0; i < nodes2Hide.length; i++) {
			this.setNodeVisibility(nodes2Hide[i], false, isFilter);
		}
		//for (i = 0; i < nodes2Show.length; i++) {
		//	this.setNodeVisibility(nodes2Show[i], true, isFilter);
		//}
		
		graphRef.gv.fit(); // ugh...
	};
	
	/**
	 *	Sets all elements' visibility to true and removes the .toggled-filter class if
	 *	isFilter is true, or removes the .toggled-hide class if isFilter is false
	 *		@param isFilter:boolean - true undoes filter, false undoes hide
	 */
	this.showAll = function(isFilter) {
		var selector = "";
		if (typeof isFilter !== "undefined") {
			selector = (isFilter == true) ? ".toggled-filter" : ".toggled-hide";
		}
		
		graphRef.utils.$(selector, function(i, ele) {
			if (ele.isNode()) {
				_this.setNodeVisibility(ele, true, isFilter);
			} else if (ele.isEdge()) {
				_this.setEdgeVisibility(ele, true, isFilter);
			}
		});
	};
	
	/**
	 *	Returns a collection of the selected nodes on this graph
	 */
	this.getSelectedNodes = function() {
		return graphRef.utils.$("node:selected");
	};
	
	/**
	 *	Returns a collection of the selected edges on this graph
	 */
	this.getSelectedEdges = function() {
		return graphRef.utils.$("edge:selected");
	};
};