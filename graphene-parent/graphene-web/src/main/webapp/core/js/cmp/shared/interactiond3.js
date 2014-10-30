// histogram.js
// PWG for DARPA
// requires d3

// This is a custom component - think of it as a class, not an object. To instantiate one, use Ext.create('Ext.custom.Histogram', {...

Ext.define('DARPA.Interactiond3', {

	extend: 'Ext.Component',

	xLabels:[],
	logScale:false,
	rescale:true,
	height:0,
	width:0, 
	xLabelStyle:"font-size: 12; font-family: Helvetica,sans-serif",
	mysvg:null,
        gridId: null,   // MFM ref to the corresponding data grid
	leftPadding:100, // width allowed for the y axis
	//OLD bottomPadding:55, // height allowed for the x axis
        bottomPadding:60, // fix for x axis labels at the bottom partially hidden
	firstTime:true,
        
	changeWidth:function(width) {
		//console.log("interaction got changeWidth to " + width);
		this.width=width;
		if (this.mysvg != null) {
			//console.log("interactiond3 will transition");
			this.mysvg.transition().attr("width", width);
			this.reset();
			if (this.data != null) {
				this.plot(this.data, this.searchTarget);
			}
		}
	},
        // MFM
        changeHeight:function(height) {                
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
	arrowName:'',

	afterRender:function() {this.makeSvg()},
	
        // MFM This version allows override of the background,stroke, and fill color
        makeCustomSvg: function(bkdColor,strokeColor, fillColor)
	{
            // DEBUG
            //console.log("interactiond3 makeCustomSvg with " + bkdColor + ", " + strokeColor + ", " + fillColor);

            this.arrowName = "arrow" + this.id;
            this.arrowUrl = "url(#" + this.arrowName + ")"
            this.mysvg=d3.select('#'+this.id).style("background-color",(bkdColor && bkdColor.length > 2)? bkdColor : "lightyellow");
            var height = this.height;
            this.mysvg = this.mysvg
                    .append("svg:svg")
                    .attr("width", this.width)
                    .attr("height", height);

            this.mysvg.append("svg:defs").selectAll("marker")
            .data([this.arrowName])
            .enter().append("svg:marker")
            .attr("id", String)
            .attr("viewBox", "0 0 20 20")
            .attr("refX", 10)
            .attr("refY", 10)
            .attr("markerWidth", 8)
            .attr("markerHeight", 6)
            .attr("orient", "auto")
            .attr("stroke", (strokeColor && strokeColor.length > 2)? strokeColor : "red")
            .attr("fill", (fillColor && fillColor.length > 2)? fillColor : "red")
            .append("svg:path")
            .attr("d", "M 0 0 L 20 10 L 0 20 z"); // Move to 0,0; Line to 20,10; Line to 0,20; Line to start position

// although I have not seen this explained anywhere, it seems you define the graphic for a right-facing horizontal line initially.
// auto means it will be rotated according to the angle of the drawn line from the horizontal.

	},
        
	makeSvg: function()
	{
            //console.log("interactiond3 makeSvg with width " + this.width);

            this.arrowName = "arrow" + this.id;
            this.arrowUrl = "url(#" + this.arrowName + ")"
            this.mysvg=d3.select('#'+this.id).style("background-color","lightyellow");
            var height = this.height;
            this.mysvg = this.mysvg
                    .append("svg:svg")
                    .attr("width", this.width)
                    .attr("height", height);

            this.mysvg.append("svg:defs").selectAll("marker")
            .data([this.arrowName])
            .enter().append("svg:marker")
            .attr("id", String)
            .attr("viewBox", "0 0 20 20")
            .attr("refX", 10)
            .attr("refY", 10)
            .attr("markerWidth", 8)
            .attr("markerHeight", 6)
            .attr("orient", "auto")
            .attr("stroke", "red")
            .attr("fill", "red")
            .append("svg:path")
            .attr("d", "M 0 0 L 20 10 L 0 20 z"); // Move to 0,0; Line to 20,10; Line to 0,20; Line to start position

// although I have not seen this explained anywhere, it seems you define the graphic for a right-facing horizontal line initially.
// auto means it will be rotated according to the angle of the drawn line from the horizontal.
         
	},
        
        // MFM added
        getData: function() {
            return this.data;
        },
        
        // MFM Change the default background color
        // newColor can be a valid HTML color name or a rgb hex value
        setBkdColor: function(newColor) {
            if (newColor && newColor.length > 2) {                
                this.mysvg=d3.select('#'+this.id).style("background-color",newColor); 
            }
        },
        
	reset: function() {
		if (this.mysvg != null) {
			this.mysvg.selectAll("rect").remove();
			this.mysvg.selectAll("text").remove();
                        //this.mysvg.selectAll("marker").remove(); // Adding this fixed the problem of not deleting old items when resize
			this.mysvg.selectAll("g").remove();				
			this.mysvg.selectAll("line").remove();
		}
			
		this.firstTime = true;
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

    // DEBUG
    //console.log("Interactiond3 plot with width: " + this.width + " height: " + this.height + " firstTime: " + this.firstTime);
	
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
		for (var i = 0; i < data.length; ++i) {
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

		if (searchTarget.indexOf(",") == -1) {	// A single number
			for (var i in labobj) {
				if (i != searchTarget)
					yLabels.push(i);
			}
			for (var i in labobj) {
				if (i == searchTarget)
					yLabels.push(i);
			}
		}
		else {
			for (var i in labobj) 
				yLabels.push(i);
		}
					
//		console.log("first: " + first + " last: " + last);
		
		// the domain is the input, the range is the output. Remember that 0,0 is TOP left in SVG
		
		if (last == first) {
			var mintime = (1000*60*60);
			last += mintime; // so we can show axes if only one entry
			first -= mintime;
		}
		
		var xScale = d3.time.scale().domain([first,last]).range([this.leftPadding+10, this.width-20]);	
		var yScale = d3.scale.ordinal().domain(yLabels).rangePoints([0 ,plotAreaHeight], 1);
				
		if ((this.datacols != data.length) || this.rescale) {
			this.mysvg.selectAll("rect").remove();
			this.mysvg.selectAll("text").remove();		
			this.mysvg.selectAll("g").remove();				
			this.firstTime = true; // triggers a new append function
			this.datacols = data.length;
		}

		// Add the col dimensions to the data object. this is because the scales are not in scope in the callback.
		// The examples for d3 assume that the scales are in scope, and appear to be wrong in this scenario.

		var lab = 0;
		var offset = 0;
		for (var column = 0; column < data.length; ++column) {
			var datum 	= data[column];
			datum.x 	= xScale(datum.time);
			datum.y_start 	= yScale(datum.source);
			datum.y_end   	= yScale(datum.target);
			datum.color 	= 'red';
		}

                // MFM this fixes bug for the first line disappearing after highlighting
                var fakeLine = { color: "lightyellow", source: "na", sourcerow: 0, target: "na", targetrow: 0, time: 0, x: 100, y_end: -2, y_start: -5 };
                var plotData = [];
                plotData.push(fakeLine);
                for (i = 0; i < data.length; ++i) {
                    plotData.push(data[i]);
                }
                
		this.showBars(this.mysvg, plotData, this.firstTime, this);
		if (this.toplabels)
			this.showBarText(this.mysvg, data, this.firstTime);	
                                
		this.showXaxis(this.mysvg, this.firstTime, xScale);
		this.showYAxis(yScale);

		this.firstTime = false;
	},


        // MFM 
        // This "highlights" a plotted line in the chart for the selected record
        // sourceType       Application source type
        // recordData       is expected to be an object of type: Ext.grid.record.data
        // highlightColor   is an optional color to use for highlighting
        highlightMatchingPlot: function(sourceType, recordData, highlightColor) {   
            var data2Highlight = this.getMatchingData(sourceType, recordData);            
            var self = this;
            
            if (data2Highlight) {
                var hColor = (highlightColor && highlightColor.length > 0)? highlightColor: "cyan";
                if (data2Highlight) {
                    self.highlight([data2Highlight], hColor);
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
        getMatchingData: function(sourceType,grec) { 
            var datum = null;

            for (var col = 0; col < this.data.length; col++) {
                datum = this.data[col];
                
                if (sourceType == "Walker") {
                	
                    // Need to match on date - DAY OF THE MONTH and not the exact time
                    var dDate = new Date(datum.time);
                    var dDateStr = dDate.toISOString().substr(0,10);    // Omit the time portion
                    
                    // DEBUG
                    //console.log("dDateStr = " + dDateStr + ", grec.date = " + grec.date);

                    if (grec.date == dDateStr && grec.receiverId == datum.target && grec.senderId == datum.source ) {
                        return datum;
                    }
                }
                // add other sourceTypes here
            }
            return null;    // no match
        },
       
        // MFM
        // This 'highlights' plotted items using a 'move to stroke' transition
        // dataArr      An array containing one or more data items
        // color        Line color
        highlight:function(dataArr, color) {

            // expects {time,source,target, sourcerow, targetrow} in an array
            // we must already have set up the rows as ordinals from zero	
            if (dataArr.length == 0) {
                    return;
            }
            this.showBarsHighlighted(this.mysvg, dataArr, color); 
	},  // end highlight
        
	showBars:function(mysvg, data, firstTime) {
            var slf = this;            
            if (firstTime) {
                    mysvg.selectAll("line").data(data)
                    .enter().append("svg:line")
                    .attr("x1",function(d) {
                            return d.x;
                       }
                     )
                    .attr("x2",function(d) {
                            return d.x;
                       }
                     )

                    .attr("y1",function(d) {
                            return d.y_start;
                       }
                     )
                    .attr("y2",function(d) {
                            return d.y_end;
                       }
                     )
                    .attr("stroke", function(d) {
                            return d.color;
                       }
                     )
//			 .attr("class", "link arrow")
                     .attr("marker-end", slf.arrowUrl)

                     .on("click", function(d,i) {
                            slf.fireEvent("BarClick", i); // a custom event
                       }
                     )
                     ;
            }

            else { // not first time, so transition
                    mysvg.selectAll("line")
                    .data(data).transition()
                    .duration(0)
                    .attr("stroke", function(d) {
                            return d.color;
                    })
                    .transition()
                    .duration(1000)
                    .attr("x1",function(d) {
                            return d.x;
                       }
                     )
                    .attr("x2",function(d) {
                            return d.x;
                       }
                     )

                    .attr("y1",function(d) {
                            return d.y_start;
                       }
                     )
                    .attr("y2",function(d) {
                            return d.y_end;
                       }
                     );

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
           
        // MFM
        showBarsHighlighted:function(mysvg, data, incolor) {
            
            // additional check on incolor
            var hColor = (incolor && incolor.length > 0)? incolor: "cyan";

            mysvg.selectAll("line")               
            .data(data).transition()
            .attr("stroke", function(d) {
                    return hColor;
            })
            .attr("stroke-width", function(d) { // Make it obvious which line
                    return "4";
            })
            .transition()
            .duration(1000)
            .attr("x1",function(d) {
                    return d.x;  
               }
             )
            .attr("x2",function(d) {
                    return d.x;
               }
             )
            .attr("y1",function(d) {
                    return d.y_start;
               }
             )
            .attr("y2",function(d) {
                    return d.y_end;
               }
             ) 
             ; 

        }, // function show bars Highlighted

	showXaxis: function(mysvg, firstTime, xScale)
	{
            var self = this;
            
            // DEBUG
            //console.log("showXaxis: firsttime = " + firstTime + ", xScale =" + xScale);
            //console.log("self.height-self.bottomPadding = " + self.height-self.bottomPadding);
            
            var xAxis = d3.svg.axis()
                .scale(xScale)
//			.orient("bottom")
                .tickFormat(d3.time.format("%m/%d/%Y"))
            ; // note that "bottom" or "top" mean relative to the axis, not to the screen.
              // If we don't transform, it, the axis will be y=0, which is the top of the display area
              // So if we use "top" and don't transform, the labels will be invisible (above the display area)
              // If we use "bottom" and don't transform, the labels will be at the top of the display area

            // Move the axis down to its correct position. This looks odd but is the right way to do it.
            var padstr = "translate(0," +(self.height-self.bottomPadding) + ")";
            this.mysvg
            .append("g")
            .attr("class", "axis")
            .call(xAxis)
            .attr("transform", padstr)
            .selectAll("text")
            .style("text-anchor", "end")
            .attr("dx", "-.8em")
            .attr("dy", ".3em")
            //.attr("transform", function(d) {return "rotate(-65)"})
            .attr("transform", function(d) {return "rotate(-50)"})  // fix for x axis labels at the bottom partially hidden
            ;
	},

        showYAxis: function(yScale)
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

				var numTicks = yScale.range().length;
				var overallWidth = this.width - this.leftPadding - 20;
				
				var yAxisRuler = d3.svg.axis()
					.scale(yScale)
					.ticks(numTicks)
					.tickSize(overallWidth, 0)
					.tickFormat("")
					.orient("right");
					
				this.mysvg.append("g")
					.attr("class", "grid")
					.call(yAxisRuler)
					.attr("transform", padstr);
				
            // translate is x,y. Without it, the scale was off the chart on the left.
            } // function showYAxis
	}
); // define
