// scatterplot.js
// PWG for DARPA
// requires d3

// This is a custom component - think of it as a class, not an object. To instantiate one, use Ext.create('DARPA.Scatterplot', {...


Ext.define('DARPA.Scatterplot', {

	extend: 'Ext.Component',

	xLabels:[],
	logScale:false,
	rescale:true,
	height:0,
	width:0, 
	radius:2,   
	rotateText:true,
	xLabelStyle:"font-size: 12; font-family: Helvetica,sans-serif",
	mysvg:null,
	leftPadding:100, // width allowed for the y axis
        gridId: null,   // MFM ref to the corresponding data grid
//      bottomPadding:36, // height allowed for the x axis with horizontal labels	
        bottomPadding:60, // fix for x axis labels at the bottom partially hidden, height allowed for the x axis with slanted vertical labels	
	firstTime:true,
	changeWidth:function(width) {
		//console.log("scatterplot got changeWidth to " + width);
		this.width=width;
		if (this.mysvg != null) {
			//console.log("scatterplot will transition");
			this.mysvg.transition().attr("width", width);
			this.reset();
			if (this.data != null) {
				this.plot(this.data, this.searchTarget);
			}
		}
	},
        // MFM
        changeHeight:function(height) {
		//console.log("scatterplot got changeWidth to " + width);
		this.height=height;
		if (this.mysvg != null) {
			//console.log("scatterplot will transition");
			this.mysvg.transition().attr("height", height);
			this.reset();
			if (this.data != null) {
				this.plot(this.data, this.searchTarget);
			}
		}
	},
	afterRender:function()
	{
		this.mysvg=d3.select('#'+this.id).style("background-color","lightyellow");
		var height = this.height;
		this.mysvg = this.mysvg
			.append("svg:svg")
			.attr("width", this.width)
			.attr("height", height);
	},
	
	reset: function() {
		if (this.mysvg != null) {
			this.mysvg.selectAll("rect").remove();
                        this.mysvg.selectAll("circle").remove(); // Adding this fixed the problem of not deleting old circle items when resize 
			this.mysvg.selectAll("text").remove();		
			this.mysvg.selectAll("g").remove();				
			this.mysvg.selectAll("line").remove();
		}
	
		this.firstTime = true;
	},

        // MFM added
        getData: function() {
            return this.data;
        },
        
        // MFM 
        // Place a reference to the parent data grid for this chart into
        // this context so that we can later click on the svg display area
        // to get or highlight the corresponding grid data item
        setGridRef: function(gridId) {            
            this.gridId = gridId;      
        },
        
	plot:function(data, searchTarget) {

	// expects {time,source,target, sourcerow, targetrow} in an array
	// we must already have set up the rows as ordinals from zero
		
//		alert("Data made it to transactions with length " + data.length);
        
		this.data = data;
		this.searchTarget = searchTarget;

		if (this.mysvg == null) {
			return;
		}
		
		if (data.length == 0)
			return;
			
		var plotAreaHeight = this.height - this.bottomPadding;
		var plotAreaWidth  = this.width  - this.leftPadding;

		var first = data[0].time;
		var last  = data[0].time;
		var maxrow = 0;
		
		var labobj={};
		for (var i = 1; i < data.length; ++i) {
			if (data[i].time < first)
				first = data[i].time;
			if (data[i].time > last)
				last = data[i].time;
			if (data[i].sourcerow > maxrow)
				maxrow = data[i].sourcerow;
			if (data[i].targetrow > maxrow)
				maxrow = data[i].targetrow;
			labobj[data[i].source]=0;
			labobj[data[i].target]=0;
		}
		var yLabels=[];
		// make the search target the first in the list
		for (var i in labobj) {
			if (i != searchTarget)
				yLabels.push(i);
		}
			
		// the domain is the input, the range is the output. Remember that 0,0 is TOP left in SVG
		var xScale = d3.time.scale().domain([first,last]).range([this.leftPadding, this.width - 20]).nice(d3.time.day);
		var yScale = d3.scale.ordinal().domain(yLabels).rangePoints([0 ,plotAreaHeight], 1);
		
		// Add the col dimensions to the data object. this is because the scales are not in scope in the callback.
		// The examples for d3 assume that the scales are in scope, and appear to be wrong in this scenario.

		var lab = 0;
		var offset = 0;
		for (var column = 0; column < data.length; column++) {  // MFM using column++ instead of ++column
			var datum 	= data[column];
			datum.x 	= xScale(datum.time);
			datum.y 	= yScale(datum.source == searchTarget ? datum.target:datum.source);
			datum.color 	= 'red';
		}

                // MFM this fixes bug for the first line disappearing after highlighting                
                var fakeLine = { color: "lightyellow", source: "na", sourcerow: 0, target: "na", targetrow: 0, time: 0, x: 100, y: -2 };
                var plotData = [];
                plotData.push(fakeLine);
                for (i = 0; i < data.length; ++i) {
                    plotData.push(data[i]);
                }
                
		this.showBars(this.mysvg, plotData, this.firstTime, this.radius);
		if (!this.firstTime) {
			this.mysvg.selectAll("g.axis").remove();	
		}
		this.showXaxis(this.mysvg, this.firstTime, xScale);
		this.showYAxis(yScale, this.firstTime);

		this.firstTime = false;
	},
        
        // MFM 
        // This "highlights" a plotted point in the timeline for the selected record
        // sourceType       Application source type
        // recordData       is expected to be an object of type: Ext.grid.record.data
        // highlightColor   is an optional color to use for highlighting
        highlightMatchingPlot: function(sourceType, recordData, highlightColor) {   
            var data2Highlight = this.getMatchingData(sourceType, recordData);
            if (data2Highlight) {
                var hColor = (highlightColor && highlightColor.length > 0)? highlightColor: "cyan";
                if (data2Highlight) {
                    data2Highlight.color = hColor; 
                    this.highlight([data2Highlight]);
                }
            }
        },
        
    // TODO getMatchingData is duplicated in interactiond3 and scatterplot - place it in common utils.
        // MFM
        // This returns a matching plot data item for the specified record data,
        // the fields matched are: timestamp, caller (source), and called (targer) communication ids 
        // Returns null if no match.
        // sourceType       Application source type
        // grec         the selected record.data item from the Ext Grid
        getMatchingData: function(sourceType, grec) { 
            var datum = null;
            
            for (var col = 0; col < this.data.length; col++) {
                datum = this.data[col];
                
                if (sourceType == "Walker") {

                    // Need to match on date - DAY OF THE MONTH and not the exct time
                    var dDate = new Date(datum.time);
                    var dDateStr = dDate.toISOString().substr(0,10);    // Omit the time portion

                    if (grec.date == dDateStr && grec.receiverId == datum.target && grec.senderId == datum.source ) {
                        return datum;
                    }
                }
                
            }
            return null;    // no match
        },
       
        // MFM
        // This 'highlights' plotted items using a 'move to stroke' transition
        // dataArr      An array containing one or more data items
        highlight:function(dataArr) {

            // expects {time,source,target, sourcerow, targetrow} in an array
            // we must already have set up the rows as ordinals from zero
		
            if (dataArr.length == 0) {
                    return;
            }

            // The 3rd param must be false, this will modify the existing plot data using a 'slide in stroke' transition
            this.showBars(this.mysvg, dataArr, false, this.radius * 3); 
	},  // end highlight


	showBars:function(mysvg, data, firstTime, radius)
	   {
	   var slf = this;
		if (firstTime) {
			mysvg.selectAll("line").data(data)
			.enter().append("svg:circle")
			.attr("cx",function(d) {
				return d.x;
			   }
			 )

			.attr("cy",function(d) {
				return d.y;
			   }
			 )
			.attr("r",function(d) {
				return radius;
			   }
			 )
			 
			.attr("fill", function(d) {
				return d.color;
			 })
			 ;

		} // first time

		else { // not first time, so transition
			mysvg.selectAll("circle")
			.data(data).transition()
			.duration(0)
			.attr("stroke", function(d) {
				return d.color;
			})
			.transition()
			.duration(1000)
			.attr("cx",function(d) {
				return d.x;
			   }
			 )

			.attr("cy",function(d) {
				return d.y;
			   }
			 )
			.attr("r",function(d) {
				return radius;
			   }
			 )
			.attr("fill", function(d) {
				return d.color;
			 })
			 ;

		} 
                
            // Place a reference to the parent data grid for this chart into
            // the svg document so that we can later click on the svg display area
            // to get or highlight the corresponding grid data item
            if (slf.gridId) {

                   // MFM
                   // If user clicks on or near one of the lines, highlight the corresponding data 
                   // row in the parent grid or elsewhere., BarClick does not work
                   slf.mysvg.on('click', function () {
                       var self2 = this;    // == svg
                       var gridId = slf.gridId; // capture the gridId
                       var parentw = window.parent;
                       var coords = [0,0];

                       coords = d3.mouse(self2);
                       var x = coords[0];
                       var y = coords[1];

                       // get the previously stored ref to the data grid for this chart
                       //var getGrid = d3.select('#myGrid').datum();

                       // DEBUG
                       //console.log("onclick: gridid = " + gridId);

                       // call a function in the parent window to highlight the 
                       // corresponding data row in the parent grid 
                       if (gridId && parentw.highlightRowForChartPos) {
                           //parentw.highlightRowForChartPos(getGrid.gridId, x,y);
                           parentw.highlightRowForChartPos(gridId, x,y);
                       }
                   }, slf.gridId);	
               }
	   }, // function show bars

	showXaxis: function(mysvg, firstTime, xScale)
	{
            var self = this;
		var xAxis = d3.svg.axis()
			.scale(xScale)
			.tickFormat(d3.time.format("%m/%d/%Y"))			
//			.orient("bottom")
		; // note that "bottom" or "top" mean relative to the axis, not to the screen.
		  // If we don't transform, it, the axis will be y=0, which is the top of the display area
		  // So if we use "top" and don't transform, the labels will be invisible (above the display area)
		  // If we use "bottom" and don't transform, the labels will be at the top of the display area

            var padHeight = self.height-self.bottomPadding;
            
		// Move the axis down to its correct position. This looks odd but is the right way to do it.
		var padstr = "translate(0," + padHeight + ")";
		
		this.mysvg
		.append("g")
		.attr("class", "axis")
		.call(xAxis)
		.attr("transform", padstr)
		// following rotates the text
		.selectAll("text")
		.style("text-anchor", "end")
		.attr("dx", "-.8em")
                //.attr("dx", "-1.0em")   // TEST
		.attr("dy", ".3em")
		//OLD .attr("transform", function(d) {return "rotate(-65)"})
                .attr("transform", function(d) {return "rotate(-50)"})  // fix for x axis labels at the bottom partially hidden
		;

	},

        showYAxis: function(yScale, firstTime)
             {

             var yAxis = d3.svg.axis()
                     .scale(yScale)
                     .orient("left")
                     .ticks(1)
             ;

             var padstr = "translate(" + this.leftPadding + ",0)";

             this.mysvg
             .append("g")
             .attr("class", "axis")
             .call(yAxis)
             .attr("transform", padstr)
             ;

     // translate is x,y. Without it, the scale was off the chart on the left.
        } // function showYAxis
    }
); // define
