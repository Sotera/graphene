// exportDialog.js
// Used for Exporting the graph in either xml or json format

// This creates a hidden iframe that can be used to download files that have been exported
// url      - This is the url of a service that returns the file contents to download
// fileURL  - this is the path to a file to be downloaded that has previously been exported
// example params:  "/rest/FIXME/graph/" + downloadURL, fileURI
var doExportDownload = function(url, fileURI) {
    var hiddenIFrameID = 'exportDownloader';
    var iframe = document.getElementById(hiddenIFrameID);
    // create the iframe only once
    if (iframe === null) {
        iframe = document.createElement('iframe');
        iframe.id = hiddenIFrameID;
        iframe.style.display = 'none';
        document.body.appendChild(iframe);
    }
    
    var durl = url + "?filePath=" + fileURI;
    // DEBUG
    //console.log("doExportDownload: url = " + durl);
    
    iframe.src = durl;   // url of a service that returns the file contents to download
};

Ext.define("DARPA.exportDialog", {
	extend: 'Ext.Window',
	title: 'Export',
	height: 200,
	width: 400,
	closeAction: 'destroy',
	plain: 'true',
	border: false,
	resizable: false,
	modal: true,
	layout: 'form',
	buttonAlign: 'center',
	
	graphJSON: null, // reference to the graph json data that this dialog will export
	graphPNG: undefined, // reference to png data from the graph that this dialog will export
	
	constructor: function() {
		var thisWindow = this;

		var extensionStore = ['.png', '.json' /*, '.xml'*/]; // expand when additional file extensions are implemented
	
		var fileExtension = new Ext.form.ComboBox({
			width: '25%',
			padding: '5 5 0 0',
			triggerAction: 'all',
			autoDestroy: true,
			editable: false,
			mode: 'local',
			store: extensionStore,
			selectOnFocus: true,
			allowBlank: false,
			forceSelection: true
		});
	
		fileExtension.setValue(extensionStore[0]);
	
		var fileNameField = new Ext.form.TextField({
			width: '75%',
			padding: '5 0 5 5',
			id: 'filename',
			allowBlank: false,
			fieldLabel: "File Name"
		});
	
		this.items = new Ext.FormPanel({
			headerCfg: {'content-type':'application/json'},
			layout: 'form',
			title: 'File name and extension',
			width: '100%',
			border: false,
			items: [{
				xtype: 'container',
				title: null,
				height: '100%',
				width: '100%',
				layout: {
					type: 'hbox',
					pack: 'center'
				},
				items: [
					fileNameField,
					fileExtension
				]
			}]
		});
	
		this.buttons = [{
			text: 'Export',
			type: 'Submit', // Important!
			handler: function(btn, e) {
				var graphJSON = thisWindow.getGraphJSON();
		
				if (typeof graphJSON == "undefined" || graphJSON.graph.nodes.length <= 0) return;
				
				var fileExt = fileExtension.getValue();
				var fileName = fileNameField.getValue();
				var a = null;
					
				switch(fileExt) {
					// -------------------------------------- PNG ---------------------------------------------
					case ".png":
						var pngData = thisWindow.getGraphPNG();
						if (typeof pngData == "undefined") {
							Ext.Msg.alert(
								"Invalid PNG Data Error",
								"The PNG data you are trying to export is either incorrect or undefined."
							);
							return;
						}
						a = document.createElement('a');
						a.href = pngData;
						break;
					// -------------------------------------- XML ---------------------------------------------
					case ".xml":
						// Currently unsupported by both the UI and any service
						var header = "application/xml";
						break;
					// -------------------------------------- JSON --------------------------------------------
					case ".json":
					default:
						try {
							// Blob and URL work in both Firefox and Chrome.  Have not tested IE yet.
							var jsonData = JSON.stringify(graphJSON);
							var blob = new Blob([jsonData], {type: "application/json"});
							var url = URL.createObjectURL(blob);
							a = document.createElement('a');
							a.href = url;
						} catch (e) {
							// Blob or URL might not be available for this browser.  Hmm...
							console.error("Blob() or URL is not working or not defined.");
							a = null;
						}
						break;
					// --------------------------------------------------------------------------------------
				}
				
				if (a == null) Ext.Msg.alert("Export Error", "Unable to perform export capability with current browser");
				else {
					// NOTE:  we will eventually HAVE to do an Ajax POST.  Export of .png or .json is currently client-side
					a.download = fileName + fileExt;
					(function (obj, evt) {
						if (document.createEvent) {
							var evObj = document.createEvent("MouseEvents");
							evObj.initEvent(evt, true, false);
							obj.dispatchEvent(evObj);
						} else if (document.createEventObject) {
							var evObj = document.createEventObject();
							obj.fireEvent("on" + evt, evObj);
						}
					})(a, "click");
				}
				
				thisWindow.close();
			}
		}, {
			text: 'Cancel',
			handler: function(btn, e) {
				thisWindow.close();
			}
		}];
		
		this.callParent(arguments);
	},
	
	// set reference to the graph that this dialog will export
	setGraphJSON: function(json) {
		this.graphJSON = json;
	},
	
	getGraphJSON: function() {
		return this.graphJSON;
	},
	
	setGraphPNG: function(inPNG) {
		this.graphPNG = inPNG;
	},
	
	getGraphPNG: function() {
		return this.graphPNG;
	},
	
	setFileName: function(name) {
		var fileNameField = Ext.getCmp("filename");
		
		if (typeof fileNameField !== "undefined") {
			fileNameField.setValue(name);
		}
	}
});