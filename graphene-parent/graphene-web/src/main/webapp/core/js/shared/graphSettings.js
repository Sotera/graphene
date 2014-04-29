/*
* USAGE:
* When creating this component you will need to call setGraphFrame() 
* 
* EXAMPLE below:
* 
*   var graphSettings = Ext.create("DARPA.GraphSettings", {});
*               
*   // Have to delay this call a bit since the Graph Frame (see PB_Frame) is not defined yet
*   var wtimeout2 = window.setTimeout(function() {
*       window.clearTimeout(wtimeout2);
*       graphSettings.setGraphFrame(getPBFrame());      <--- This sets the Graph Frame context for the Filter
*       graphSettings.enableApply2Search()              <--- This enables the Apply To Search Button, by default this button is disabled
*   }, 4000);
*/

Ext.define("DARPA.GraphSettings",
{
	extend:"Ext.form.FieldSet",
	
	constructor:function(config) {

		var applyButton = Ext.create("Ext.Button", {
			text:'APPLY TO GRAPH', // Only apply the changes to the graph
			disabled:false, // MFM enabled
			itemId: 'btnApplyGraphSettings',
			id:config.id+'-APPLY',
			margin:4,   
			height:24,
			width:108,
			handler: function(but) {
				var path = but.id.split('-');
				var graph = Ext.getCmp(path[0] + '-' + path[1]);
				graph.applySettings();


			}
		});

		var helpButton = Ext.create("Ext.Button", {
		    icon: Config.helpIcon,
		    maxHeight: 30,
		    scale: 'medium',
		    margin: 4,
		    style: {marginTop: '2px'},
		    handler: function(but) {
			var owner = but.ownerCt;
			var helpText = "";
			if (owner.apply2SearchEnabled) {
			    helpText = 
				"<b>APPLY TO SEARCH</b>: This will perform a search using the communication id(s) that were used in the search with the new graph settings." +
				" This will display the Call Log with the search results. You can still click on the Call Graph to see the call graph.<br><br>";
			}
			helpText = helpText +
			    "<b>APPLY TO GRAPH</b>:&nbsp;&nbsp; This will display the graph using the new settings. " +
			    "If Maximum Hops > 2 this may return a very large graph that will take some time to display.";

			Ext.Msg.alert('Help', helpText);
		}
		});

		
		this.items = [
			{
				xtype:'textfield',
				fieldLabel:'Maximum Hops',
				labelLength:150,
				value:'2',
				width:200
			},
			{
				xtype:'textfield',		
				fieldLabel:'Maximum Nodes',
				labelLength:150,
				value:'500',
				width: 200
			},
			{
				xtype:'textfield',		
				fieldLabel:'Maximum Edges per Node',
				labelLength:150,
				value:'20',
				width: 200
			},
			{
				xtype:'textfield',		
				fieldLabel:'Minimum Edge Weight',
				labelLength:150,
				value:'2',
				width: 200,
				hidden:true

			},
			applyButton,
			helpButton
			// MFM added to update settings and redisplay
	/*		
			,
			{
				xtype:'textfield',		
				fieldLabel:'Maximum Search Results',
				labelLength:150,
				width: 200
			}
	*/		
			// plus: icons vs text when available
		];
		this.callParent(arguments);
	}, // constructor

        // Reference to the Caller's Graph
        graph: null,
        
        apply2SearchEnabled: false,
        
        // This sets the Graph Frame context for the Filter
        setGraph: function(gf) {            
            if (gf) {
                this.graph = gf;
            }
        },
        
        // This enables the Apply To Search Button, by default this button is disabled
        enableApply2Search: function() {
            this.apply2SearchEnabled = true;
            this.items.items[4].setDisabled(false);
        },             
        
	getMaxHops:function()
	{
		var self=this;
		return self.items.items[0].getValue();
	},
	getMaxNodes:function()
	{
		var self=this;
		return self.items.items[1].getValue();
	},
	getMaxEdgesPerNode:function()
	{
		var self=this;
		return self.items.items[2].getValue();
	},
	getMinWeight:function()
	{
		var self=this;
		return self.items.items[3].getValue();
	}
		
});

