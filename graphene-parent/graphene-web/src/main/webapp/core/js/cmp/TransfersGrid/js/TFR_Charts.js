
// Graph, and Interaction

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
		var title = "Emails with the most common correspondents";
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
 





