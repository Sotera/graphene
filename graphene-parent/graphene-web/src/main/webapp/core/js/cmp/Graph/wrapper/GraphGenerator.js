function GraphGenerator(graphRef) {
	var _this = this;
	var _currentId = 0;
	var _parentNode = null;
	
	var _stateEnum = {
		ADD:		{value: false, name: "ADD"},
		CONNECT:	{value: false, name: "CONNECT"}
	};
	
	var _getCurrentState = function() {
		for (var state in _stateEnum) {
			if (_stateEnum.hasOwnProperty(state)) {
				if (_stateEnum[state].value === true)
					return _stateEnum[state].name;
			}
		}
		return null;
	};
	
	this.setParent = function(node) {
		if (typeof node.isNode == "function" && node.isNode() == true) {
			_parentNode = node;
		}
	};
	
	this.addNode = function(x, y) {
		// create node and set node's position to (x, y)
		var nodeJSON = {
			data: {
				id: "generatedNode_" + _currentId,
				idVal: "GeneratedNode_" + _currentId,
				idType: "GENERATED",
				name: "New Node",
				size: graphRef.CONSTANTS("nodeSize"),
				label: "New Node*",
				color: "gray",
				generated: true,
				attrs: [/* Populated via NodeEditor */]
			},
			group: "nodes",
			position: {x: x, y: y}
		};
		
		// create edge between new node and _parentNode
		var edgeJSON = {
			data: {
				id: "generatedEdge_" + _currentId,
				idVal: "GeneratedEdge_" + _currentId,
				idType: "GENERATED",
				color: graphRef.CONSTANTS("defaultEdge"),
				source: _parentNode.data("id"),
				target: "generatedNode_" + _currentId,
				lineStyle: "dashed",
				generated: true,
				count: 1,
				label: "",
				attrs: [/* Populated via NodeEditor */]
			},
			group: "edges"
		};
		
		// add node and edge json graphRef.gv
		graphRef.gv.add([nodeJSON, edgeJSON]);
		
		// increment the running id so we can have unique nodes/edges
		_currentId++;
	};
	
	this.connectNodes = function(targetNode) {
		if (_this.stateMatches("CONNECT") && typeof _parentNode !== "undefined") {
			// create edge between targetNode and _parentNode
			var edgeJSON = {
				data: {
					id: "generatedEdge_" + _currentId,
					idVal: "GeneratedEdge_" + _currentId,
					idType: "GENERATED",
					color: graphRef.CONSTANTS("defaultEdge"),
					source: _parentNode.data("id"),
					target: targetNode.data("id"),
					lineStyle: "dashed",
					generated: true,
					count: 1,
					label: "",
					attrs: [/* Populated via NodeEditor */]
				},
				group: "edges"
			};
			// add edge json to graphRef.gv
			graphRef.gv.add(edgeJSON);
			
			// increment the running id so we can have unique nodes/edges
			_currentId++;
		}
	};
	
	this.changeState = function(newState) {
		if (typeof newState !== "string") return;
		
		switch (newState.toUpperCase()) {
			case "ADD":
				_stateEnum.ADD.value = true;
				_stateEnum.CONNECT.value = false;
				break;
			case "CONNECT":
				_stateEnum.ADD.value = false;
				_stateEnum.CONNECT.value = true;
				break;
			default:
				break;
		}
	};
	
	this.stateMatches = function(inState) {
		if (typeof inState !== "string") return false;
		return inState.toUpperCase() == _getCurrentState();
	};
	
	this.givePrompt = function() {
		switch (_getCurrentState()) {
			case "ADD":
				graphRef.utils.updateProgress("Click on graph whitespace to place new node there.", 0);
				break;
			case "CONNECT":
				graphRef.utils.updateProgress("Click on another node to draw an edge.", 0);
				break;
			default:
				graphRef.utils.updateProgress("100%", 1);
				break;
		}
	};
	
	this.clear = function() {
		_root = null;
		_stateEnum.ADD.value = false;
		_stateEnum.CONNECT.value = false;
	};
};