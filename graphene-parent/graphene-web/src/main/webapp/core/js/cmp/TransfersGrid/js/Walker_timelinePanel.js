// a custom component containing three histograms and a set of buttons.

//  TimelinePanel has instances of timeline.js.  Timeline.js has instances of histogram.js (in shared/common/js)


Ext.define("DARPA.TimelinePanel",  {

	extend:'Ext.container.Container', 

	layout:{
		type: 'vbox', 
		align:'stretch'
	},
	
	title:false,
	
	statAccount:"",
	currentMonthstats:{stats:[]},
	currentYearStats:{stats:[]},
	activityData:[],
	activityMaxVal:0,
	rescale:true, // scale each graph by the data for the selected year, rather than the overall data
	currentLabs: '',
	currentYear:0,
	currentMonth: -1,
	currentView: '',
	items:[],
	
	constructor:function(config)
	{
		
		var act = Ext.create("DARPA.Timeline",
				{
					flex:1,
					id:config.id + "-Activity",
					title:'Number of Transactions',
					tlPanel:this,
					histoid:'Activity'
				}
		);
		var buttons =  Ext.create('DARPA.timelineSelectors',
				{
					height:50,
					tlPanel:this,
					id:config.id + "-Buttons"					
				});
		
		this.items = [
			act,
			buttons
			];
			
		this.callParent(arguments);
	
	
	},
/*	
	afterRender:function()
	{
		var self = this;
		console.log("Timeline panel after render with height: " + self.getHeight() + " visible: " + self.isVisible());
		
		// PWG TODO: we are getting this even with the panel invisible. 
		// If it is invisible we get height 0 if a container or height 2 if a panel
	
	},
*/	
	getTimeline:function(name) {
		var self = this;
		var i = self.getId() + "-" + name;
		var cmp = Ext.getCmp(i);
		if (cmp == undefined)
			console.log("Could not find timeline with ID " + i);
		return cmp;
	},
	
	listeners: {
		resize:function(me, width, height)
		{
			console.log("timelinepanel sized to height: " + height + " width: " + width);
			
		}
	},

	getHistogram:function(name) {
		var self=this;
		return self.getTimeline(name).histogram;
	},
	
	getTimelineButtons:function()
	{	
		var self = this;
		return self.getTimeline("Buttons");
	},

		
	timelinecontrolsChanged: function(view, newval)
	{
		with (this) {

			if (view == 'month' && newval == 12) 
			{
				view = 'year';
				newval = this.currentYear;
			}

			if (currentView != view) {
				currentView = view;
				setLabels();
			}


			if (view == 'year') { // showing a new year
				this.currentYear = newval;
				this.currentMonth = -1;
				getTimelineButtons().setMonth('All'); // all
				getTimelineButtons().enableAllButton(false); // all
				this.showGraph(); // no need to load data because we retain all the month totals
			}

			else if (view == 'month') { // showing a new month, need to call the server for the daily totals
				this.currentMonth = newval;			
				loadDaily(this.statAccount, this.currentYear, this.currentMonth); // callback will plot
				getTimelineButtons().enableAllButton(true); // all
			}
		}	

	},
	
	setLabels:function()
	{
		if (this.currentView == 'year')
			this.setMonthLabels();
		else
			this.setDayLabels();
	
	
	},
	
	histogramBarClicked: function(histogram, index)
	{
		var self = this;
		if (self.currentView != 'month') { // no drill-down on the days
			self.timelinecontrolsChanged('month', index);
			self.getTimelineButtons().setMonth(index); 
		}
		
	},
	
	getSelectedYear:  function()
	{
		var self = this;
		return self.getTimelineButtons().getSelectedYear();
	},
/*	
	getSelectedType: function()
	{
		var tButtons = this.getTimelineButtons().getSelectedType();
		var status = tButtons.getChecked();
		if (status.length == 0)
			return ""; 
		return status[0].boxLabel;
	},			
*/
	setView:function(view) {
		if (this.currentView != view) {
			this.currentView = view;
			this.setLabels();
		}
	
	},
	
	
	updateAccount:function(account)
	{
	// We call this when the grid has been reloaded. Only then do we know that the server has the new statistics
	
		if (account != this.statAccount) 
		{
			this.loadMonthly(account);
	
		}
	
	},
	
				
	loadMonthly: function(ac)
	{
	
		if (this.statAccount == ac)
			return;
	
		this.statAccount = ac;
		Ext.Ajax.request( {
			method:'GET',
			url:Config.monthlyStatisticsUrl,
			params: {accountNumber: ac},
			scope:this,
			success:function(result, request) {
				this.currentYearStats = Ext.JSON.decode(result.responseText);
				this.setStatistics(this.currentYearStats);
			} // success
		
		} // request params
		); // request
		
	},
	
	loadDaily: function(ac, year, month)
	{
	
		Ext.Ajax.request( {
			method:'GET',
			url:Config.dailyStatisticsUrl,
			params: {accountNumber: ac, month:month, year:year},
			scope:this,
			success:function(result, request) {
				this.currentMonthStats = Ext.JSON.decode(result.responseText);			
				this.setStatistics(this.currentMonthStats);
			} // success
		
		} // request params
		); // request
	
	
	},
	
	
	
	resetHistograms: function()
	{
		this.getHistogram('Activity').reset();
	
	},

	setBarWidths: function(w)
	{
		this.getHistogram('Activity').setBarWidth(w);
	},
	
	setMonthLabels: function()
	{
		this.setBarWidths(24);
			
		this.getTimeline('Activity').showMonthLabels();
	
	},
	
	setDayLabels: function()
	{
		this.setBarWidths(12);
			
		this.getTimeline('Activity').showDayLabels();
	
	},

	setStatistics: function(statistics)
	{
		var self = this;
		if (self.currentView == 'year') {
			var years = self.getYears(statistics);
			self.getTimelineButtons().enableYears(years);
		}
		var activitydata = self.updateData(self.getSelectedYear(),'Activity');
		
		this.resetHistograms();

		this.calcMaxVals(statistics.stats); // based on whole set, not just on the current year or month
		
		this.plot(); // based on the current selection
	
		// maxVal is the largest value in the whole data set, not the subset contained in timeData.
		// This tells the histogram not to re-scale the y axis based on every new data slice.
	},
	
	calcMaxVals: function(stats)
	{
		this.activityMaxVal;	
		for (var i = 0; i < stats.length; ++i) {
			var m = stats[i];
			if (m.nbrTransactions > this.activityMaxVal)
				this.activityMaxVal = m.nbrTransactions;
		}
	
	},
	
	plot: function()
	{
		this.currentYear=this.getSelectedYear();
		this.showGraph();
	},
	
	
	showGraph: function()
	{
		var data = this.updateData(this.currentYear, 'Activity');
		this.getHistogram('Activity').plot(
				data, 
				this.rescale?-1:this.activityMaxVal, 
				function(i) {
					this.histogramBarClicked('activity', i);
				}
			);
		
	},
	
	updateData: function(year, type)
	{
	var i;
	var val;
	
		var self = this;

		var stats = (self.currentView == 'year') ? self.currentYearStats : self.currentMonthStats;
		var tdata=[];
		
		var doubleCols = (type == 'Cash Flow');
		
		var nbr = (self.currentView=='year') ? 12 : 31;
		if (doubleCols)
			nbr *= 2;
		
		for (var i = 0; i < nbr; ++ i) {
			var entry = {
				val:0,
				valstr:'',
				color:'#2d578b'
			 };
			 tdata.push(entry);
		}
		
		for (i = 0; i < stats.stats.length; ++i) {

			var m = stats.stats[i];	
			if (m.year != year)
				continue;
	
			val = m.nbrTransactions;
			var col = (this.currentView=="year") ? m.month : m.day;
			
if (col > tdata.length)
console.log("Error: invalid col " + col + " For view " + self.currentView);

			tdata[col] = self.makeBar(val);
		}
		return tdata;
	},

	makeBar:function(val)
	{
		if (val == null || val == undefined)
			val = 0;
		var color = val > 0 ? "#2d578b" : "red";
		
		if (val > 1000000 || val < -1000000)
			valstr = (val/1000000).toFixed(1) + " M";
		else if (val > 100000 || val < -100000)
			valstr = (val/100000).toFixed(1) + " K";
			
		else
			valstr = "" + val;
		var entry = {
			val: val,
			color: color,
			valtext: valstr
		};
		
		if (val < 0)
			val = -val; // to calc the abs max
		if (val > this.maxVal)
			this.maxVal = val;
		
		return entry;
	},
	
	getYears: function(statistics)
	{
		var yearFound = [];
		
		for (var i = 0; i < statistics.stats.length; ++i) {
			var m = statistics.stats[i];
			var yr = m.year;
			var exists = false; {
			for (var j = 0; j < yearFound.length; ++j)
				if (yearFound[j] == yr) 
					exists = true;
			}
			if (!exists)
				yearFound.push(yr);
		}
		
		return yearFound;
		
	}, // getYears
	
	getDefaultYear: function(statistics)
	{
		var firstYear = 3000;
		
		for (var i = 0; i < statistics.stats.length; ++i) {
			var m = statistics.stats[i];
			var yr = m.year;
			if (firstYear > yr)
			    firstYear = yr;
		}
		
		return firstYear;
		
	} // getDefaultYear

} // define param 2	
); // define