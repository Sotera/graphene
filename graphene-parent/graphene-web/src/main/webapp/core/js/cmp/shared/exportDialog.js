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

		var extensionStore = ['.png', '.json', '.xml']; // expand when additional file extensions are implemented
	
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
		
				if (graphJSON !== "undefined" && graphJSON.graph.nodes.length > 0) {	
					var header;
					var serviceURL;
					var skipREST = false;
					var downloadURL = "getExportedGraph";
					var fileExt = fileExtension.getValue();
	                    
					switch(fileExt) {
						case ".png":
							skipREST = true;
							break;
						case ".xml":
							header = "application/xml";
							serviceURL = "exportGraphAsXML";
							break;
						                                
						case ".json":
						default:
							header = "application/json";
							serviceURL = "exportGraphAsJSON";
							break;
					}
	
					if (skipREST == true) {
						var pngData = thisWindow.getGraphPNG();
						
						if (typeof pngData == "undefined") {
							Ext.Msg.alert(
								"Invalid PNG Data Error",
								"The PNG data you are trying to export is either incorrect or undefined."
							);
							return;
						}
						
						var download = document.createElement('a');
						download.href = pngData;
						download.download = fileNameField.getValue() + fileExt;
						
						(function (obj, evt) {
							if (document.createEvent) {
								var evObj = document.createEvent("MouseEvents");
								evObj.initEvent(evt, true, false);
								obj.dispatchEvent(evObj);
							} else if (document.createEventObject) {
								var evObj = document.createEventObject();
								obj.fireEvent("on" + evt, evObj);
							}
						})(download, "click");
						
						thisWindow.close();
					} else {
					
						// We do Not want the response to be rendered in the Browser.
						// Instead we want the browser to popup a dialog to download the exported file
						Ext.Ajax.request({
							url: 'rest/graph/' + serviceURL, 
							method: 'POST',
							headers: {
								'Content-Type': header
							},
							params: {
								// TODO - add username as a param
								userName: "TODO",
								timeStamp: ((new Date()).getTime()).toString(), // unique timestamp
								fileName: fileNameField.getValue(),
								fileExt: fileExt
							},
							jsonData: Ext.encode(graphJSON), 
												
							success: function(resp) {
								thisWindow.close();
								// DEBUG
								//console.log("resp = " + resp.responseText);
						
								// The response will be the Server location of the File containing the exported graph
								// pass this file location to doExportDownload
								var fileURI = resp.responseText;
								doExportDownload("rest/graph/" + downloadURL, fileURI);
							},
							failure: function(resp) {
								thisWindow.close();
								var err = new Ext.Window({
									title: 'Export Failure',
									height: 300,
									width: 600,
									html: resp.responseText
								});

								err.show();
							}
						}); 
					}                    
				} else {
					// TODO: error handling
				}
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