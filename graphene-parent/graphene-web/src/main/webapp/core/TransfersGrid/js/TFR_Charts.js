
// Graph, and Interaction
/*
function makeTransfersGraph(data, maxrow, GraphVis)
{
    // Data contains an array of [{source:s,target:t}]

	if (data.length==0)
		return [];

	    // Now compute the graph
	var nodeIds=uniqueNodeIds(data);
	var edges = [];
	for (var i = 0; i < data.length; i++) {
            // MFM TODO amount should be populated with the txn amount if this is available
            edges.push({source:data[i].source, target:data[i].target, amount: 0});
        }
		
	edges = utils.dedupeArray(edges, function(a,b) {
            return (
                (a.source==b.source && a.target==b.target) || 
                (b.source==a.target && b.target==a.source)
            );
	});
	
	var nodes=[];
	for (var node = 0; node < nodeIds.length; node++) {
            nodes.push( {
                    color:'#D3D3D3',
                    name: nodeIds[node],
                    label:nodeIds[node],
                    attrs:null,
                    idVal: nodeIds[node],
                    id:nodeIds[node]}
            );
	}

	return GraphVis.graphToJSON({nodes:nodes, edges:edges});	
}


Ext.define("DARPA.TransfersGraph", { // graph
	extend:"Ext.panel.Panel",
//	extend:"Ext.Container",	
	border:'1',
	title:'Connections',
	hidden:false,
	titleAlign:'center',
	width:240,
	height:240,
        GraphVis: null, // reference to "DARPA.GraphVis" defined in cytoGraphSubs.js
	graphId:'',
	gdata:null,
	
	items: [],
	setGraphData: function(data, target) {
		if (data.length == 0)
			return;
                    
		var self = this;
		self.gdata = makeTransfersGraph(data, 10, self.GraphVis);
		if (self.gdata.length > 0) {
			self.GraphVis.showGraph(self.gdata, target);
                }
	},
	constructor:function(config)
	{
		this.GraphVis = Ext.create("DARPA.GraphVis", { id: "cygraph" });
		//this.GraphVis.params.width =240;
		//this.GraphVis.params.height=240;
		this.items=Ext.create("Ext.Component", {
			height:240,width:240, id: "cygraph"
		});	
	
		this.callParent(arguments);
		
	},
	getGraphContainer:function()
	{
		var self=this;
		return self.items.items[0];
	},
	afterLayout:function()
	{
		var self=this;
		var graph=this.getGraphContainer();
		//if (self.fd==null) {
		//	self.GraphVis.params.width  = graph.getWidth();
		//	self.GraphVis.params.height = graph.getHeight();
		//}
		if (self.gdata != null) {
			self.GraphVis.showGraph(self.gdata, null);
                }
			
		this.callParent(arguments);
	},
	
	clear:function()
	{
		var self=this;
                self.GraphVis.clear();
	}

});
*/

Ext.define("DARPA.TransfersCharts", // D3 charts
{
	extend:"Ext.panel.Panel",
	height:'auto', 
	width:'auto',
	layout:{
		type:'hbox', // note that we never show them both at once.
		align:'stretch'
	},
	border:1,
	flex:1,
	title:'Transfers Timeline',
	titleAlign:'center',   
	afterLayout:function() { // We don't know our width until after layout. We need to tell the children
		var self=this;
		var newWidth=this.getWidth();
		self.getInteractionChart().changeWidth(newWidth);				
	},
	items:[],
	
	constructor:function(config) 
	{
		this.items=
		[
			Ext.create("DARPA.Interactiond3", {			
//				hidden:true,
				width:1200, // MFM
				height:300  // MFM
			})

		];
		
	
		this.callParent(arguments);
	},
	setChartData:function(data, maintarget) {
		var self = this;
		var title = "Transfers between " + maintarget;
		if (maintarget.indexOf(",") == -1)
			title += " and the most common accounts";
		self.setTitle(title);
		data = interactionPreprocess(maintarget, data, 10);
                var ichart = self.getInteractionChart();
		ichart.reset();		
		ichart.plot(data, maintarget);
		ichart.setVisible(true);
		
		return data; // we use the reduced data set for the graph

	},
        
        
	getInteractionChart:function()
	{
		var self = this;
		return self.items.items[0];
	},
	clear:function()
	{
		var self=this;
		self.setTitle("Charts");
		self.setChartData([],'');
		self.getInteractionChart().setVisible(false);		
	
	}

});

// Contains the graph on the left and two overlapping charts on the right: 
// 	scatterplot (used when only one number)
//	interaction (used when several numbers selected)
// Now removed the graph on the left as we have one in a separate panel

   Ext.define("DARPA.TransfersChartFrame", {
	extend:"Ext.Container",
	height:'auto',
	width:'auto',
   	layout:{
   		type:'hbox',
   		align:'stretch'
	},
	items:[],
	constructor:function(config)
	{
		this.items= [
/*
			Ext.create("DARPA.TransfersGraph", // The force-directed graph on the left
			{
				border:'1',
				title:'Connections',
				hidden:true,    // MFM TEST
				titleAlign:'center',
				width:'auto',
				height:'auto',
				graphId:config.graphId  // this is why we have this custom constructor. So we can set graph Id dynamically
							// which allows us to have more than one graph
			}),
*/			
			Ext.create("DARPA.TransfersCharts", // container for interaction chart on the right
			{
				flex:1
			})
		];
		
		this.callParent(arguments);
	},
/*	
	getGraph:function()
	{
		var self=this;	
		return self.items.items[0];
	},
*/	
	getCharts:function()
	{
		var self=this;	
		return self.items.items[0];
	
	},
	getScatterPlot:function()
	{
		var self = this;
		return self.getCharts().getScatterPlot();
	},
	getInteractionChart:function()
	{
		var self = this;
		return self.getCharts().getInteractionChart();
	},
	
	setData:function(data, maintarget)
	{
		var self=this;
		data=self.getCharts().setChartData(data, maintarget); // returned a reduced set for the graph
//		self.getGraph().setGraphData(data, maintarget);
	}

	
   }); // create
 





