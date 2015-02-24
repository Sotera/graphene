// importDialog.js
// Used for Importing a previously exported graph (in json format only for now)

Ext.define("DARPA.importDialog", {
	extend: 'Ext.Window',
	title: 'Import',
	height: 600,
	width: 800,
	closeAction: 'destroy',
	resizable: true,
	modal: true,
	//layout: "fit",
	layout: {
		type: 'vbox',
		align: 'stretch',
		pack: 'start'
	},
	buttonAlign: 'center',
	graphVis: null,
	
	CONSTANTS: function(key) {
		var _legend = {
			empty_title: "Empty",
			background_idle: "#FFFFFF",
			background_fail: "#EE5555",
			background_success: "#00FF99",
			message_idle: "<p style='color:black; text-align:center;'><i>Drag the graph JSON file into this area and click Import</i></p>",
			message_fail: "<p style='color:white; text-align:center;'><b>Please drop a valid file before attempting to import</b></p>",
			message_success: ""
		};
		
		return _legend[key];
	},
	
	constructor: function() {
		var thisWindow = this;
			
		var div = document.createElement('div');
		div.id = "droppable_div";
		
		var panel = new Ext.Panel({
			title: thisWindow.CONSTANTS("empty_title"),
			border: false,
			height: "100%",
			flex: 1,
			width: '100%',
			layout: 'fit',
			id: "form_panel",
			//autoScroll: true,
			html: div.outerHTML
		});
		
		thisWindow.getPanel = function() {
			return panel;
		};
		
		thisWindow.buttons = [{
			text: "Import",
			handler: function() {
				var area = thisWindow.getDropArea();
				var jsonText = area.innerText;
				var gv = thisWindow.getGraphVis();
				
				try {
					if (gv == null) throw "Reference to graph is null";
					
					var jsonObj = Ext.decode(jsonText);
					
					thisWindow.hide();
					gv.importGraph(jsonObj);
					thisWindow.close();
				} catch (e) {
					thisWindow.showErrorState(e);
				}
			}
		}, {
			text: "Clear",
			handler: function() {
				thisWindow.showIdleState();
			}
		}, {
			text: "Cancel",
			handler: function() {
				thisWindow.close();
			}
		}];
		
		thisWindow.items = [panel];
		thisWindow.doLayout();
		thisWindow.callParent(arguments);
	},
	
	afterRender: function() {
		var thisWindow = this;
		
		var div = thisWindow.getDropArea();
		div.style.transition = "background-color 1s linear";
		div.style.height = "100%";
		div.style.width = "100%";
		
		thisWindow.showIdleState();
		
		var fileDragHover = function(e) {
			e.stopPropagation();
			e.preventDefault();
			e.target.className = (e.type == "dragover" ? "hover" : "");
		};
		
		var fileSelectHandler = function(e) {
			fileDragHover(e);
			
			var files = e.target.files || e.dataTransfer.files;
			var reader = new FileReader();
			
			reader.onload = function(e) {
				div.innerText = e.target.result;
				div.style.backgroundColor = thisWindow.CONSTANTS("background_success");
			};
			
			reader.onprogress = function(e) {
				if (e.lengthComputable) {
					var percentage = Math.round(e.loaded / e.total);
					// TODO: add a progress bar to the bottom and update it?
				}
			};
			
			// should only be one, but loop anyway. break out after first file is read
			for (var i = 0; i < files.length; i++) {
				var f = files[i];
				// split on ".json" and make sure that it is the file's extension
				// i.e. the last set of characters
				var arr = f.name.toLowerCase().split(".json");
				if (arr[arr.length - 1].length == 0) {
					thisWindow.setImportTitle(f.name);
					reader.readAsText(f);
					break;
				} else {
					thisWindow.showErrorState("File is not .json type");
					break;
				}
			}
		};
		
		div.addEventListener("dragover", fileDragHover, false);
		div.addEventListener("dragleave", fileDragHover, false);
		div.addEventListener("drop", fileSelectHandler, false);
		
		this.callParent(arguments);
	},
	
	getDropArea: function() {
		return document.getElementById("droppable_div");
	},
	 
	setGraphVis: function(ivgr) {
		this.graphVis = ivgr;
	},
	
	getGraphVis: function() {
		return this.graphVis;
	},
	
	setImportTitle: function(title) {
		this.items.items[0].setTitle(title);
	},
	
	showIdleState: function(e) {
		this.setImportTitle(this.CONSTANTS("empty_title"));
		
		var div = this.getDropArea();
		div.innerHTML = this.CONSTANTS("message_idle");
		div.style.backgroundColor = this.CONSTANTS("background_idle");
	},
	
	showErrorState: function(e) {
		this.setImportTitle(this.CONSTANTS("empty_title"));
		
		var div = this.getDropArea();
		div.innerHTML = this.CONSTANTS("message_fail");
		div.style.backgroundColor = this.CONSTANTS("background_fail");
		
		console.error("Error while importing: " + e);
	}
});