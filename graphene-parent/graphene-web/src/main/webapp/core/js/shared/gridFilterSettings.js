// This Reusable Filter can be used in various panels where a grid of call records is displayed.
// TODO - should rename it to CallGridFilterSettings

Ext.define("DARPA.GridFilterSettings",
{
	extend:"Ext.form.FieldSet",
        //                  Do NOT set it here as this is a reusable component.
        // resultsFrameId:  MUST be set in the calling code - this is the id of the Frame containing data grid in the caller. Do NOT set this id here
	items: [ 
            Ext.create('DARPA.dategroup', { //id: 'gridfilterFromDate', 
                itemId: 'gridfilterFromDate', 
                margins: '2,20,0,1', label:'From:', align:'left'}), // bottom left right top   // from date
            
            Ext.create('DARPA.dategroup', { //id: 'gridfilterToDate',
                itemId: 'gridfilterToDate',
                margins: '2,0,20,1', label:'To:' , align:'left'}),   // to date

            // Slider with multiple controls for changing the start and end dates
            Ext.create('Ext.slider.Multi', {
                //id: 'gridfilterDateSlider',
                itemId: 'gridfilterDateSlider',
                width: 240,
                height: 34,
                hideLabel: false,
                label: 'Time Filter',
                values: [50, 600],    // Values will be set later
                increment: 1,
                minValue: 1,
                maxValue: 730,     // make this 365 * 2 days
                prevFromSlide: 0,   // Values will be set later
                prevToSlide: 0,
                listeners: {
                    changecomplete: function(slider, newValue, thumb) {
                         // DEBUG
                         //console.log("slide change: newVal =" + newValue + ", thumb index =" + thumb.index);
                         
                         var owner = slider.ownerCt;
                         var fdate = owner.getComponent('gridfilterFromDate');
                         var tdate = owner.getComponent('gridfilterToDate');

                         //fdate.items.items[2].setValue(utils.formatDate(date));
                         if (thumb.index == 0) {    // changing from date
                             var fromDatems = new Date(fdate.getDt()).getTime();
                             var delta = newValue - slider.prevFromSlide;   // number of days
                             var deltams = delta * 86400*1000;
                             var newFromDatems = fromDatems + deltams;
                             var newFromDate = new Date(newFromDatems);
                             fdate.items.items[2].setValue(utils.formatDate(newFromDate));
                             slider.prevFromSlide = newValue;
                             // DEBUG
                             //console.log("from delta = " + delta + ", newFromDate = " + newFromDate);
                         }
                         if (thumb.index == 1) {    // changing to date
                             var toDatems = new Date(tdate.getDt()).getTime();
                             var delta = newValue - slider.prevToSlide;   // number of days
                             var deltams = delta * 86400*1000;
                             var newToDatems = toDatems + deltams;
                             var newToDate = new Date(newToDatems);
                             tdate.items.items[2].setValue(utils.formatDate(newToDate));
                             slider.prevToSlide = newValue;
                             // DEBUG
                             //console.log("to delta = " + delta + ", newToDate = " + newToDate);
                         }
                     }
                }
            }),      
            {
                  xtype:'textfield',		
                  fieldLabel:'Duration (Min,Max)',
                  //id: 'gridfilterDuration',
                  itemId: 'gridfilterDuration',
                  labelLength:80,
                  value: '',    // can be a single value or a range (min,max)
                  minWidth:100,
                  width: 180
            }, 
            {
                    xtype:'textfield',		
                    fieldLabel:'Communication Id(s)',
                    //id: 'gridfilterNumbers',
                    itemId: 'gridfilterNumbers',
                    labelLength:80,
                    value:'',
                    width: 240

            },
            // added to update settings and redisplay
            {
                    xtype:'button',
                    text:'CLEAR',
                    disabled:false, 
                    //id: 'btngridClearFilterSettings',
                    itemId: 'btngridClearFilterSettings',
                    margin:4,   
                    height:24,
                    width:80,
                    listeners: {
                        click: function(but)
                        {   
                            var owner = but.ownerCt;
                            var fdate = owner.getComponent('gridfilterFromDate');
                            var tdate = owner.getComponent('gridfilterToDate');
                            var numbers = owner.getComponent('gridfilterNumbers');
                            var duration = owner.getComponent('gridfilterDuration')
                            //var srch = getSearchFrame().getSearch();
                            //var fromDate = srch.getFromDate();    // not yet implemented
                            //var toDate   = srch.getToDate();      // not yet implemented
                            var fromDate = 0;
                            var toDate = 0;
                            if (fromDate == 0) {
                                fromDate = (new Date(2008,0,1,0,0,0)).getTime();    // default 2008
                            }
                            if (toDate == 0) {
                                toDate = (new Date()).getTime();    // Today
                            }
                            // update the to and from date fields in the Filter panel.
                            //OLD updateGridFilterDates(fromDate, toDate);
                            if (fdate) {
                                var fromDateDate = new Date(fromDate);
                                fdate.items.items[2].setValue(utils.formatDate(fromDateDate));
                            }
                            if (tdate) {
                                var toDateDate = new Date(toDate);
                                tdate.items.items[2].setValue(utils.formatDate(toDateDate));
                            }

                            owner.setDateSliders(fromDate,toDate);

                            numbers.setValue("");
                            duration.setValue("");

                            // The id of the Frame containing the data grid will be different in different tabs, need to pass in via resultsFrameId
                            //OLD var grid = Ext.getCmp('results').getGrid();
                            var frmId = owner.resultsFrameId;
                            var grid = null;
                            if (frmId) {
                                grid = Ext.getCmp(frmId).getGrid();
                                if (grid) {
                                    grid.clearFilter(); 
                                }
                            }
                        }
                    } // listeners
                },
                {
                    xtype:'button',
                    text:'APPLY', // Only apply the changes to the grid
                    disabled:false, 
                    //id: 'btngridApplyFilterSettings',
                    itemId: 'btngridApplyFilterSettings',
                    margin:4,   
                    height:24,
                    width:80,
                    listeners: {
                        click: function(but)
                        {   
                            var owner = but.ownerCt;
                            var fromDate = owner.getComponent('gridfilterFromDate').getDt();
                            var toDate = owner.getComponent('gridfilterToDate').getDt();
                            var numbers = owner.getComponent('gridfilterNumbers').getValue(); 
                            var duration = owner.getComponent('gridfilterDuration').getValue();
                            
                            // some minimal validation
                            if (duration && duration.length > 0) {
                                // DEBUG
                                //console.log("duration unparsed = " + duration);
                                
                                if (duration.indexOf(",") > 0) {
                                    var testdur = duration.split(",");
                                    var mind = 0;
                                    var maxd = 500;
                                    for (var d = 0; d < 2; d++) {
                                       if (isNaN(parseInt(testdur[d]))) {
                                            alert("Duration must be a number or 2 numbers separated by a comma.");
                                            return; 
                                       }
                                    }
                                    mind = parseInt(testdur[0]);
                                    maxd = parseInt(testdur[1]);
                                    // DEBUG
                                    //console.log("duration min = " + mind + ", max = " + maxd);
                                    
                                    if (mind < 0 || maxd < 0) {
                                        alert("Duration cannot be negative. Enter a positive number.");
                                        return;
                                    }
                                    if (mind > maxd) {
                                        alert("The maximum Duration cannot be less than the minimum Duration.");
                                        return;
                                    }
                                }
                                else if (isNaN(parseInt(duration))) {
                                    alert("Duration must be a number or 2 numbers separated by a comma.");
                                    return;
                                }
                                else if (parseInt(duration) < 0) {
                                    alert("Duration cannot be negative. Enter a positive number.");
                                    return;
                                }
                            }

                            if (toDate != 0 && toDate < fromDate) { // to and from can be the same
                                alert("The To Date cannot be less than the From Date.");
                                return;
                            }
                            // DEBUG
                            //console.log("Apply: from =>" + fromDate + "<=, to =>" + toDate + "<=, numbers =>" + numbers + "<=");
                            
                            // The id of the Frame containing the data grid will be different in different tabs, need to pass in via resultsFrameId
                            //OLD var grid = Ext.getCmp('results').getGrid();
                            //if (grid) {
                            //     grid.applyFilter(numbers, duration, fromDate, toDate); 
                            //}
                            var frmId = owner.resultsFrameId;
                            var grid = null;
                            if (frmId) {
                                grid = Ext.getCmp(frmId).getGrid();
                                if (grid) {
                                    grid.applyFilter(numbers, duration, fromDate, toDate); 
                                }
                            }
 
                        }
                    } // listeners
                },
                {   // MFM help button
                        xtype:'button',
                        //icon: '/resources/themes/images/default/shared/icon-question.gif', // <- TOO BIG!
                        icon: Config.helpIcon,
                        maxHeight: 30,
                        scale: 'medium',
                        margin: 4,
                        style: {marginTop: '2px'},
                        listeners: {
                                click: function() {
                                    Ext.Msg.alert(
                                       'Help',
                                       '<b>From and To</b>:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; From and To Dates can be manually entered or you can use the date picker on the right to select the dates. ' +
                                           'Once the dates have been selected, you can adjust them using the sliders below the date fields.<br><br>' +
                                       '<b>Communication Id(s)</b>: This can be a single number or a comma separated list of numbers.<br>' +
                                           'Each number can be a partial number. For example: 863091,950. This would match all numbers containing *863091* or *950*.<br><br>' +
                                       '<b>Duration</b>:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This can be a single number or a Minimum and Maximum duration range. For example: 5,20. This would match all calls with durations between 5 and 20 minutes inclusive.<br><br>' +
                                       '<b>CLEAR</b>:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This will clear the filter settings and redisplay the call log.<br><br>' +
                                       '<b>APPLY</b>:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This will display the table with the filter settings and will not update the Call Graph.'   
                                    );
                                }
                        } // listeners
                }
	],
        
	getFromDate:function()
	{
		var self=this;
		return self.items.items[0].getDt();
	},
	getToDate:function()
	{
		var self=this;
		return self.items.items[1].getDt();
	},
        getDuration:function()
	{
		var self=this;
		return self.items.items[3].getValue();
	}, 
	getEventIds:function()
	{
		var self=this;
		return self.items.items[4].getValue();
	},      
        
        // These functions set the date filter slider controls
        // from and to dates are in ms
        // TODO - this needs more tuning
        setDateSliders: function(fromDate,toDate) {
            var today = new Date().getTime(); 
            var fsmax = toDate + (15778800 * 1000);    // + 6 months
            if (fsmax > today) {
                fsmax = today;
            }
            //var fsmin = fsmax - (31557600 * 2000);   // default is less 2 years in the past
            //if (fromDate < fsmin) {
            //    fsmin = fromDate;
            //}
            
            var fsmin = fromDate - (15778800 * 1000);
            var fsfrom;
            var fsto;
            
            fsfrom = fromDate;
            fsto = toDate;
            
            //var slider = Ext.getCmp('gridfilterDateSlider');
            var slider = this.items.items[2];
            if (slider) {
                var fsrange = fsmax - fsmin;
                var sliderRange = slider.maxValue;
                var fromPos = Math.round(((fsfrom - fsmin) / fsrange) * sliderRange);
                var toPos = Math.round(((fsto - fsmin) / fsrange) * sliderRange);
                
                // DEBUG
                //console.log("fromPos = " + fromPos + ", toPos = " + toPos);
                slider.setValue(0,fromPos);
                slider.setValue(1,toPos);
                slider.prevFromSlide = fromPos;
                slider.prevToSlide = toPos;
            }
        } 
});