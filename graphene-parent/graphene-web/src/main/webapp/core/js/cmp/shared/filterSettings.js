/** 
* Reusable Component to Filter and Animate the Graph
* History
* 09/30/13  M. Martinet
* 10/24/13  M. Martinet - Revised to use itemIds instead of ids and added the functions listed in USAGE below.
* 01/14/14  M. Martinet - Fixed bugs with related to disabling time filtering
* 02/14/14  M. Martinet - Additional data-driven filtering
* 04/02/15  A. Weller - Pruned the FilterSettings panel to its bare essentials (a string filter field and buttons); it contained a lot of deprecated and unnecessary components and features
* 
* USAGE: (deprecated)
* When creating this component you will need to call setGraph() and optionally call enableTimefilter() and setSearchFieldLabel()
* It is Not necessary to give the component an id.
* EXAMPLE below: (deprecated)
* 
*   var filterSettings = Ext.create("DARPA.FilterSettings", {id:'PBGraphFilter'});
*               
*   // Have to delay this call a bit since the Graph Frame (see PB_Frame) is not defined yet
*   var wtimeout2 = window.setTimeout(function() {
*       window.clearTimeout(wtimeout2);
*       filterSettings.setGraph(graph());         <--- This sets the Graph Frame context for the Filter
*       filterSettings.enableTimeFilter(false);             <--- This enables or disables Time filtering and Animation
*       filterSettings.setSearchFieldLabel("Identifier")    <--- This sets the field label name for the Number/Identifier search field
*   }, 4000);
**/

Ext.define("DARPA.FilterSettings", {
	extend: "Ext.Panel",
	graphRef: null,
	buttonAlign: "center",
	layout: {
		type: "vbox",
		align: "start"
	},
	border: false,
	
	constructor: function(config) {
		var _this = this;
	
		this.items = [{
			xtype: "textfield",
			itemId: "filterField",
			padding: "5 0 0 5",
			fieldLabel: "Identifiers(s)",
			value: "",
			width: 300
		}];
		
		this.buttons = [{
			xtype: "button",
			text: "CLEAR",
			height: 25,
			handler: function(btn) {
				_this.clear();
			}
		}, {
			xtype: "button",
			text: "APPLY TO GRAPH",
			height: 25,
			handler: function(btn) {
				_this.applyFilter( _this.getFilterField().getValue() );
			}
		}, {
			xtype: "button",
			icon: Config.helpIcon,
			maxHeight: 30,
			maxWidth: 30,
			scale: "medium",
			handler: function(but) {
				Ext.Msg.alert(
				   "Filter Help",
				   "<ul>" +
				   "<li><b>Identifier(s)</b> - This can be a single identifier or a comma separated list of identifiers.  Each identifier can be partial.  For example, 'abc, def' would match all Node Names containing *abc* OR *def*.  Each application of a filter applies to the last subset of the graph, allowing the user to filter against already-filtered nodes.</li>" +
				   "<li><b>CLEAR</b> - This will clear the filter settings and redisplay the unfiltered graph.</li>" +
				   "<li><b>APPLY TO GRAPH</b> - This will filter the graph based on the specified filter term(s)</li>" +
				   "<ul>"
				);
			}
		}];
		this.callParent(arguments);
	},
	
	clear: function() {
		this.getFilterField().setValue("");
				
		if (this.graphRef) {
			this.graphRef.clearFilter();
		} else {
			console.error("Graph can not be filtered as it is undefined.");
		}
	},
	
	applyFilter: function(filterString) {
		var string = filterString;
		var amount = undefined;		// legacy code; no longer required for filtering
		var fromDate = undefined;	// legacy code; no longer required for filtering
		var toDate = undefined;		// legacy code; no longer required for filtering
		
		if (this.graphRef) {
			this.graphRef.applyFilter(string, amount, fromDate, toDate);
		} else {
			console.error("Graph can not be filtered as it is undefined.");
		}
	},
	
	getFilterField: function() {
		return this.getComponent('filterField');
	}
});