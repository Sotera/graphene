
// An extension for the more generic histogram function

Ext.define("DARPA.Timeline", {
	extend:'Ext.panel.Panel', 
	monthNames: ['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec'],
	dayNames: [' 1', ' 2', ' 3', ' 4', ' 5', ' 6', ' 7', ' 8', ' 9', '10', 
		'11', '12', '13', '14', '15', '16', '17', '18', '19', '20',
		'21', '22', '23', '24', '25', '26', '27', '28', '29', '30', '31'],
		
	items:[],
	layout:'fit',
	
	getHistogram: function()
	{
		var self = this;
		return self.histogram;
	
	},
	
	constructor:function(config)
	{
	
		this.histogram = Ext.create("DARPA.Histogram",
		{
//			height:config.height,
//			width:config.width, 		
			xLabels:[],
			logScale: false,
			textGap:  15, // space above bars for text
			xGap:      5,    // gap between bars
			barWidth: 30,
			rescale:  true,
			histoid:  config.histoid,
			callback:config.callback,
			listeners: {
				BarClick:function(i) 
				{
					Ext.getCmp(config.id).barClick(i);
				}
			},			
			
		}); // create config
		
		this.items=[this.histogram];

		this.callParent(arguments);
	}, // constructor
	
	barClick:function(barno)
	{
		var self = this;
		self.tlPanel.histogramBarClicked(this.histoid, barno);	
	},
/*	
	afterRender:function()
	{
		// this is called when program loads. But we only get it once even though there are 3 of them

	
	},
*/	
	listeners: {
	
		resize:function(me, width, height)
		
			{
				// we get this for each of the 3.
				me.histogram.makeSize(width, height);		
			}
	},
	
	xLabelStyle:"font-size: 12; font-family: Helvetica,sans-serif",

	showMonthLabels:function() {
		this.histogram.setXlabels(this.monthNames);
	},
	
	showDayLabels:function() {
		this.histogram.setXlabels(this.dayNames);
	}
}); // define


