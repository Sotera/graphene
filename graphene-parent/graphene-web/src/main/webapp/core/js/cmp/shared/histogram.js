// histogram.js
// PWG for DARPA
// requires d3

// This is a custom component - think of it as a class, not an object. To instantiate one, use Ext.create('Ext.DARPA.Histogram', {...
// It consists of an Ext.js component containing a d3 object. D3 adds the object to the DOM using a selector with the component's ID


Ext.define('DARPA.Histogram', {

	extend: 'Ext.Component',

	xLabels:[],
	logScale:false,
	textGap:15, // space above bars for text
	xGap: 5,    // gap between bars
	barWidth: 30,
	rescale:true,
	height:0,
	width:0, 
	xLabelStyle:"font-size: 10; font-family: Helvetica,sans-serif",
	mysvg:null,
	datacols:0,
	leftPadding:65,
	firstTime:true,
	layout:'fit',
	data:null,
	maxVal:0,

	afterRender:function()
	{
		// This is getting called the first time we setVisible(true) on the parent chart container
		// but only called once.
		// NB: you can't debug this because if it hits a breakpoint the item is no longer visible and size is wrong
		
		// If the component is not set to be visible, we just save the data when we plot.
		// Then when we make it visible afterRender() is called - we should know the sizes at this point
		
		var self = this;
/*		
		console.log ("Histogram after render");
		console.log("self.height is " + self.height);
		console.log("self.getHeight() is " + self.getHeight());
		console.log("Visible is " + self.isVisible());
		console.log("Id is " + self.id);
*/		
		if (self.getHeight() > 0)
			self.makesvg();
		
		if (self.data != null)
			self.plot(self.data, self.maxVal);
			
	},
	
	makesvg:function()
	{
		var self = this;
		self.mysvg=d3.select('#'+self.id);
		self.height = self.getHeight();
		self.width = self.getWidth();
		self.mysvg = self.mysvg
			.append("svg:svg")
			.attr("width", self.width)
			.attr("height", self.height);

		self.maxBarHeight = self.height - 20 - self.textGap;
		self.labelY =  self.height - 10;
	
	},
	
	makeSize:function(width, height)
	{
		// called from the parent (e.g. timeline) when the size of the containing object changes.
		var self = this;
		self.height = self.getHeight();
		self.width = self.getWidth();
		self.maxBarHeight = self.height - 20 - self.textGap;
		self.labelY =  self.height - 10;
		
		if (self.mysvg == null)
			self.makesvg();
		else {
			self.mysvg.attr("width", self.width);
			self.mysvg.attr("height", self.height);
		}
		if (self.data != null) {
			self.firstTime = true;
			self.plot(self.data, self.maxVal);
		}
		
	
	
	},
	
	reset: function() {
		this.firstTime = true;
		this.data=null;
	},

	setBarWidth: function(w)
	{
		this.barWidth = w;
	},

	plot:function(data, maxVal) {
	
		var self = this;
		self.data=data;
		
		if (undefined===self.el) {
			if (!(undefined==console))
				console.log("Cannot plot histogram as no el");
			return;
		
		};
		
//		if (self.getHeight() < 200)
//			return;

		if (self.mysvg == null) {
			self.maksvg();
		}
		
		self.maxVal = maxVal;

		if ((self.datacols != data.length) || self.rescale) {
			self.mysvg.selectAll("rect").remove();
			self.mysvg.selectAll("text").remove();		
			self.mysvg.selectAll("g").remove();				
			self.firstTime = true; // triggers a new append function
			self.datacols = data.length;
		}

		if (maxVal == -1) { // used to scale by the actual histogram
			maxVal = d3.max(data, function(d) {return d.val;})
			if (maxVal < 4)
				maxVal = 4; // So we don't see fractions
		}

		// Calculate the scales

// if (!(undefined==console))
// console.log("Recalculating x scale with nlabels: " + self.xLabels.length + " data length: " + data.length + " width: " + self.width);

		var xScale = d3.scale.linear()
			.domain([0,self.xLabels.length])
			.range([self.leftPadding + 20,self.width]);

		var yScale = self.logScale ? d3.scale.log() : d3.scale.linear()
			.domain([0, maxVal])
			.range([self.maxBarHeight, 0]);



		// Add the col dimensions to the data object. this is because the scales are not in scope in the callback.

		// The examples for d3 assume that the scales are in scope, and appear to be wrong in this scenario.

		var barsPerLabel = data.length/self.xLabels.length;
		var newBarWidth = self.barWidth/barsPerLabel;


			// col0: lab0 offset 0
			// col1: lab0 offset 1
			// col2: lab1 offset 0
			// col3: lab1 offset 1

		var lab = 0;
		var offset = 0;
		for (var column = 0; column < data.length; ++column) {
			var datum = data[column];
			if (barsPerLabel == 1)
				datum.x = xScale(column);
			else {
				datum.x = xScale(lab) + (offset * newBarWidth);
			}

			if (datum.val == 0) {
				datum.height = 0;
				datum.y = self.textGap + self.maxBarHeight; // added 2/13/2013
				
			}
			else {
				// Remember that y=0 is the TOP in svg terms. The bottom of the graph is at maxBarHeight + the top
				// margin we left for the labels above the bars (textGap).

				// with svg, x and y define the top left corner. 

				// the yScale gives an inverted result (0 for the biggest, maxBarHeight for the smallest).
				// This is because we reversed the order of the range parameters when generating it to make the 
				// d3.axis work (otherwise, bizarrely, it starts at the top).

				var absval = datum.val > 0 ? datum.val : -datum.val;
				datum.height = (absval == 0) ? 0 : self.maxBarHeight-yScale(absval);

				datum.y = this.textGap + (self.maxBarHeight - datum.height);


	/*			
				datum.y -= yScale(absval); // cannot use log if it's zero - log 0 is negative infinity
				datum.height = (absval == 0) ? 0 : yScale(absval); // same reason
	*/			
			}
			datum.barWidth = newBarWidth;
			++offset;
			if (offset == barsPerLabel) {
				offset = 0;
				++lab;
			}
		}

		self.showBars(self.mysvg, data, self.firstTime, self);
		if (self.toplabels)
			self.showBarText(self.mysvg, data, self.firstTime);	
		self.showXaxis(self.mysvg, self.firstTime, self);
		self.showYAxis(yScale, self.maxBarHeight);

		self.firstTime = false;
	},

	showBars:function(mysvg, data, firstTime)
	   {
	   var slf = this;

		if (firstTime) {
			mysvg.selectAll("rect").data(data)
			.enter().append("svg:rect")
			.attr("x",function(d) {
				return d.x;
			   }
			 )

			.attr("y",function(d) {
				return d.y;
			   }
			 )
			.attr("height", function(d) {
				return d.height;
			 }
			)
			.attr("width", function(d) {return d.barWidth;})
			.attr("fill", function(d) {
				return d.color;
			 })
			 .on("click", function(d,i) {
				slf.fireEvent("BarClick", i); // a custom event
			   }
			 )
			 ;

		}

		else { // not first time, so transition
			mysvg.selectAll("rect")
				.data(data).transition()
				.duration(0)
				.attr("fill", function(d) {
					return d.color;
				})
				.transition()
				.duration(1000)
				.attr("y",function(d) {	
					return d.y;
				})
				 .attr("x",function(d) {
					return d.x;})
				.attr("height", function(d) {
					return d.height;

				})
				.attr("width", function(d) {
					return d.barWidth;
				});			

		} 
	   }, // function show bars

	showBarText:function(mysvg, data, firstTime)
	{
		// Labels over bars

		var txt;
		if (firstTime)
			txt = mysvg.selectAll("text").data(data).enter().append("svg:text");
		else
			txt = mysvg.selectAll("text").data(data);

		txt
			.attr("x", function(d) {return d.x + (d.barWidth/2);})
			.attr("y", function(d) {return d.y;})
			.attr("text-anchor","middle")
			.attr("style","font-size: 12; font-family: Helvetica,sans-serif")
	//		.text(function(d) {return d.val != 0 ? d.val : "";})
			.text(function(d) {return d.valtext;})		
			.attr("fill", "black");		
	},

	setXlabels: function(labs)
	{
		this.xLabels = labs; // will be displayed the next time we call plot().
// TODO: broken. 
//		if (this.mysvg != null) // created
//			this.showXaxis(this.mysvg, true, this);
	},
	showXaxis: function(mysvg, firstTime, self)
	{
		var xScale = d3.scale.linear()
			.domain([0,self.xLabels.length])
			.range([self.leftPadding + 20,self.width]);

		if (firstTime) {
			mysvg.selectAll("text.xAxis")
			.data(self.xLabels)
			.enter()
			.append("svg:text")
			.attr("x", function(d, index) {
				function g(d,index) {
					return xScale(index) + (self.barWidth/2);
				}
				return g(d,index);
			   }
			 )
			.attr("y", self.labelY)
	//		.attr("dx", -self.barWidth/2)
			.attr("text-anchor","middle")
			.attr("style",self.xLabelStyle)
			.text(function(d, index) {
				function g(index) {
					return self.xLabels[index];
				}
				return g(index);

			})
	//		.attr("transform", "translate(0,18)")
	//		.attr("class","xAxis")
			;
		}		

	/* only needed if labels change

		else {

			mysvg.selectAll("text.xAxis").data(this.xLabels)
			.attr("x", function(d, index) {
				function g(d,index) {
					return xScale(index) + this.barWidth;
				}
				g(d,index);

			   }
			 )
			.attr("y", this.labelY)
			.attr("dx", -this.barWidth/2)
			.attr("text-anchor","middle")
			.attr("style", this.xLabelStyle)
			.text(function(d,index) {
				function g(index) {
					return this.xLabels[index];
				}
				return g(index);

			   }
			 )
	//		.attr("transform", "translate(0,18)")
	//		.attr("class","xAxis")
		;
		}
	*/
	},


	   showYAxis: function(yScale)
		{

		var yAxis = d3.svg.axis()
			.scale(yScale)
			.orient("left")
			.ticks(5)
		;

		this.mysvg
		.append("g")
		.attr("class", "axis")
		.call(yAxis)
		.attr("transform", "translate(" + this.leftPadding + "," + this.textGap + ")")
		;

	// translate is x,y. Without it, the scale was off the chart on the left.
	   } // function showYAxis
	}
); // define
