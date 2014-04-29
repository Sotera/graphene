// contains two overlapping charts, the scatterplot used when 1 search target and the interaction when there are more
// Both start out hidden.
Ext.define("DARPA.TLCharts", 
{
	extend:"Ext.panel.Panel",
	height:'auto', 
	width:'auto',
        collapsible: true,  // MFM
        resizable: true,    // MFM
        resizeHandles: 'n', // MFM north
	layout:{
		type:'vbox', // note that we never show them both at once.
		align:'stretch'
	},
	border:1,
	title:'Call Timeline',
	titleAlign:'center',
        firstTime: true,
	afterLayout:function() { // We don't know our width until after layout. We need to tell the children
		var self=this;
                //if (self.firstTime) {
                // TEST always resize
                    //self.firstTime = false;
                    var newWidth=self.getWidth();
                    // DEBUG
                    //console.log("Width of charts  " + self.getWidth());
                    //console.log("Height of charts " + self.getHeight());
                    self.getScatterPlot().changeWidth(newWidth);		
                    self.getInteractionChart().changeWidth(newWidth);
                    
                    // Bug fix sometimes the height after initial render is too high and the arrow tips and dates on the x axis don't render
                    if (self.getHeight() > 500) {
                        self.getScatterPlot().changeHeight(500);
                        self.getInteractionChart().changeHeight(500);
                    }
                    this.callParent(arguments);		
                //}
	},
	items:[],
	listeners: {
             resize: function(context, newWidth, newHeight, oldWidth, oldHeight, opts) {
                 var self = context;
                 // DEBUG
                 //console.log("TLCharts resize: w = " + newWidth + ", h = " + newHeight + ", oldw = " + oldWidth + ", oldh = " + oldHeight);
                 if (newHeight < 50) {
                     return;    // don't bother
                 }
                 if (newWidth != oldWidth) {
                    self.getScatterPlot().changeWidth(newWidth);		
                    self.getInteractionChart().changeWidth(newWidth);
                 }
                 if (newHeight != oldHeight) {
                    self.getScatterPlot().changeHeight(newHeight - 27);     // was 27	
                    self.getInteractionChart().changeHeight(newHeight - 27);
                 }
             }
        },
        
	constructor:function(config) 
	{
		this.items=
		[
			Ext.create("DARPA.Interactiond3", {			
				hidden:true,
				width:760, 
				height:360
			}),

			Ext.create("DARPA.Scatterplot", {
				hidden:true,
				width:760, 
				//height:260
                                height:360
			})
		];
		
	
		this.callParent(arguments);
	},
	setChartData:function(data, maintarget) {
		var self = this;

		if (maintarget.indexOf(",") == -1) {
			self.setTitle(data.length + " Most common connections for " + maintarget);
			data = scatterplotPreprocess(data, 10);
                        var splot = self.getScatterPlot();
                        splot.reset();
			splot.plot(data, maintarget);
			splot.setVisible(true);					
			self.getInteractionChart().setVisible(false);							
		}
		else {
			self.setTitle(data.length + " Interactions between " + maintarget);
			data = interactionPreprocess(maintarget, data, 10);
                        var ichart = self.getInteractionChart();
                        ichart.reset();
			ichart.plot(data, maintarget);
			ichart.setVisible(true);		
			self.getScatterPlot().setVisible(false);										
		}
		return data; // we use the reduced data set for the graph

	},
	getInteractionChart:function()
	{
		var self = this;
		return self.items.items[0];
	},
	getScatterPlot:function()
	{
		var self = this;
		return self.items.items[1];
	},
	clear:function()
	{
		var self=this;
		self.setTitle("Charts");
		self.setChartData([],'');
		self.getInteractionChart().setVisible(false);		
		self.getScatterPlot().setVisible(false);										
	
	}

});

// Contains two overlapping charts on the right: 
// 	scatterplot (used when only one number)
//	interaction (used when several numbers selected)

   Ext.define("DARPA.TLChartFrame", {
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
			Ext.create("DARPA.TLCharts", 
			{
				flex:1
			})
		];
		
		this.callParent(arguments);
	},
	//getGraph:function()
	//{
	//	var self=this;	
	//	return self.items.items[0];
	//},
	getCharts:function()
	{
		var self=this;	
		//OLD return self.items.items[1];
                return self.items.items[0]; // MFM
	
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
		//self.getGraph().setGraphData(data, maintarget); // MFM 11/27/13 removed 
	}
	
   }); // create