
/** 
* Reusable Component to Filter and Animate the Graph
* History
* 09/30/13  M. Martinet
* 10/24/13  M. Martinet - Revised to use itemIds instead of ids and added the functions listed in USAGE below.
* 
* USAGE:
* When creating this component you will need to call setGraphFrame() and optionally call enableTimefilter() and setSearchFieldLabel()
* It is Not necessary to give the component an id.
* EXAMPLE below:
* 
*   var filterSettings = Ext.create("DARPA.FilterSettings", {id:'PBGraphFilter'});
*               
*   // Have to delay this call a bit since the Graph Frame (see PB_Frame) is not defined yet
*   var wtimeout2 = window.setTimeout(function() {
*       window.clearTimeout(wtimeout2);
*       filterSettings.setGraphFrame(getPBFrame());         <--- This sets the Graph Frame context for the Filter
*       filterSettings.enableTimeFilter(false);             <--- This enables or disables Time filtering and Animation
*       filterSettings.setSearchFieldLabel("Identifier")    <--- This sets the field label name for the Number/Identifier search field
*   }, 4000);
**/

// Added for annimation. TODO: should be in a namespace
var gfilter2Animate = {
    animateTimeout: null,
    animateDirection: "",
    
    // owner  - Owning Component (context)
    stopAnimation: function(owner) {
        var self = this;
        if (self.animateTimeout) {
            window.clearTimeout(self.animateTimeout);
            self.animateTimeout = null;
            self.animateDirection = "";
        
            if (owner) {
                // The Animation buttous are within a FieldContainer
                var brev = owner.items.items[9].getComponent('gfilter2AnimReverse');
                var bfwd = owner.items.items[9].getComponent('gfilter2AnimPlay');  
                brev.setLoading(false);
                bfwd.setLoading(false);
            }        
        }
    }
};

// Second Version
Ext.define("DARPA.FilterSettings2",
{
	extend:"Ext.form.FieldSet",
        // id:    CAN be set in the calling code but NOT here as this is a reusable component.
	items:[
            Ext.create('DARPA.dategroup', {itemId: 'graphfilter2FromDate', margins: '2,20,0,1', label:'From:', align:'left'}), // bottom left right top, from date
            Ext.create('DARPA.dategroup', {itemId: 'graphfilter2ToDate', margins: '2,0,20,1', label:'To:' , align:'left'}),   // to date

            // Slider with multiple controls for changing the start and end dates
            Ext.create('Ext.slider.Multi', {    // items[2]
                itemId: 'graphfilter2DateSlider',
                width: 280,
                height: 34,
                hideLabel: false,
                label: 'Time Filter',
                values: [20, 600],    // Values will be set later
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
                         var fdate = owner.getComponent('graphfilter2FromDate');
                         var tdate = owner.getComponent('graphfilter2ToDate');

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
            {    // NOTE: This field is for searching any text string and is not limited to Numbers. items[3]
                 // Also the fieldLabel can be changed by the caller.
                    xtype:'textfield',		
                    fieldLabel:'Number(s)', 
                    itemId: 'graphfilter2Numbers',
                    labelLength:70,
                    value:'',
                    width: 280

            },
            // Added 11/07/13
            {    // This field is for filtering by the link amount or number of calls. items[4]
                    xtype:'textfield',		
                    fieldLabel:'Amount (Min,Max)', 
                    itemId: 'graphfilter2Amount',
                    labelLength:70,
                    value:'',
                    width: 280

            },
            // added to update settings and redisplay. items[5]
            {
                    xtype:'button',
                    text:'CLEAR',
                    disabled:false, // MFM enabled
                    itemId: 'btnClearFilter2Settings',
                    margin:4,   
                    height:24,
                    width:108,
                    listeners: {
                        click: function(but)
                        {   
                            var owner = but.ownerCt;
                            var fdate = owner.getComponent('graphfilter2FromDate');
                            var tdate = owner.getComponent('graphfilter2ToDate');
                            var numbers = owner.getComponent('graphfilter2Numbers');
                            var amount = owner.getComponent('graphfilter2Amount');
                            
                            gfilter2Animate.stopAnimation(owner);
                                                        
                            var fromDate = 0;
                            var toDate = 0;
                            
                            // TODO should be owner.getSearchFrame and should have a set method for the caller
                            //var srch = getSearchFrame().getSearch();  
                            //if (srch) {
                            //    fromDate = srch.getFromDate();
                            //   toDate   = srch.getToDate();
                            //}
                                
                            if (fromDate == 0) {
                                fromDate = (new Date(2008,0,1,0,0,0)).getTime();    // default 2008
                            }
                            if (toDate == 0) {
                                toDate = (new Date()).getTime();    // Today
                            }
                            // update the to and from date fields in the Call Graph Filter panel.
                            owner.updateGraphFilterDates(owner, fromDate, toDate);

                            numbers.setValue("");
                            amount.setValue("");

                            // should have a set graphFrame method for the caller
                            var gf = owner.graphFrame;
                            if (gf) {
                               var gr = gf.getGraph();
                                gr.clearFilter();
                            }
                        }
                    } // listeners
                },
                {   // items[6]
                    xtype:'button',
                    text:'APPLY TO GRAPH', // Only apply the changes to the graph
                    disabled:false, // enabled
                    itemId: 'btnApplyFilter2Settings',
                    margin:4,   
                    height:24,
                    width:108,
                    listeners: {
                        click: function(but)
                        {
                            var owner = but.ownerCt;
                            
                            var gf = owner.graphFrame;
                            var gr = gf.getGraph();
                            var fromDate = owner.getComponent('graphfilter2FromDate').getDt();
                            var toDate = owner.getComponent('graphfilter2ToDate').getDt();
                            var numbers = owner.getComponent('graphfilter2Numbers').getValue(); // Can be any string or set of comma delimited strings
                            var amount = owner.getComponent('graphfilter2Amount').getValue();

                            if (toDate != 0 && toDate < fromDate) { // to and from can be the same
                                alert("The To Date cannot be less than the From Date.");
                                return;
                            }
                            // DEBUG
                            //console.log("Apply: from =>" + fromDate + "<=, to =>" + toDate + "<=, numbers =>" + numbers + "<=");

                            // some minimal validation
                            if (amount && amount.length > 0) {
                                // DEBUG
                                //console.log("amount unparsed = " + duration);
                                
                                if (amount.indexOf(",") > 0) {
                                    var testdur = amount.split(",");
                                    var mind = 0;
                                    var maxd = 500;
                                    for (var d = 0; d < 2; d++) {
                                       if (isNaN(parseInt(testdur[d]))) {
                                            alert("Amount must be a number or 2 numbers separated by a comma.");
                                            return; 
                                       }
                                    }
                                    mind = parseInt(testdur[0]);
                                    maxd = parseInt(testdur[1]);
                                    // DEBUG
                                    //console.log("amount min = " + mind + ", max = " + maxd);
                                    
                                    if (mind < 0 || maxd < 0) {
                                        alert("Amount cannot be negative. Enter a positive number.");
                                        return;
                                    }
                                    if (mind > maxd) {
                                        alert("The maximum Amount cannot be less than the minimum Amount.");
                                        return;
                                    }
                                }
                                else if (isNaN(parseInt(amount))) {
                                    alert("Amount must be a number or 2 numbers separated by a comma.");
                                    return;
                                }
                                else if (parseInt(amount) < 0) {
                                    alert("Amount cannot be negative. Enter a positive number.");
                                    return;
                                }
                            }
                            
                            gfilter2Animate.stopAnimation(owner);
                            
                            if (gr) {
                                if (gr.prevLoadParams && gr.prevLoadParams.searchNumber) {
                                    if (gr.prevLoadParams.searchNumber == null || gr.prevLoadParams.searchNumber.length == 0) {
                                        alert("Enter at least one number of at least 10 digits in the Search Tab.");
                                        return;
                                    }
                                }
                                gr.applyFilter(numbers, amount, fromDate, toDate);  
                            }
                        }
                    } // listeners
                },
                {   // help button items[7]
                        xtype:'button',
                        icon: Config.helpIcon,
                        maxHeight: 30,
                        scale: 'medium',
                        margin: 4,
                        style: {marginTop: '2px'},
                        listeners: {
                            click: function(but) {
                                var owner = but.ownerCt;
                                var srchFieldLabel = owner.searchFieldLabelText;
                                if (srchFieldLabel == null || srchFieldLabel.length == 0) {
                                    srchFieldLabel = "Identifier(s)";
                                }
                                var ix = srchFieldLabel.indexOf("(");
                                var singularLabel = srchFieldLabel.substr(0, (ix > 0) ? ix : srchFieldLabel.length);
                                
                                Ext.Msg.alert(
                                   'Help',
                                   '<b>From and To</b>:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; From and To Dates can be manually entered or you can use the date picker on the right to select the dates. ' +
                                       'Once the dates have been selected, you can adjust them using the sliders below the date fields.<br><br>' +
                                   '<b>' + srchFieldLabel + '</b>:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This can be a single ' + singularLabel + ' or a comma separated list of ' + srchFieldLabel + '.<br>' +
                                       'Each ' + singularLabel + ' can be a partial ' + singularLabel + '. For example: 863091,950. This would match all ' + srchFieldLabel + ' containing *863091* or *950*.<br><br>' +
                                   '<b>Amount</b>:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This can be a single number or a Minimum and Maximum amount range. For example: 10,200. This would match all transactions or connections with amounts between 10 and 200 inclusive.<br><br>' +
                                   '<b>CLEAR</b>:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This will clear the filter settings and redisplay the graph. ' +
                                   'This will also redisplay any nodes that have been manually hidden.<br><br>' +
                                   '<b>APPLY TO GRAPH</b>: This will display the graph with the filter settings.<br><br>' +
                                   '<b>Animate / Step</b>: This will increment or decrement the specified From and To dates and will auto update the graph display. ' +
                                   'Move the slider to change the speed of the animation. If the slider is all the way to the left (at 0) this will stop animation.<br><br>'  
                                );
                            }
                        } // listeners
                },
                {   // items[8]
                    xtype: 'label',
                    margin: 0,
                    html: "<hr>",
                    width: 240
                },
            // Animate Temporal controls
            Ext.create('Ext.form.FieldContainer', { // items[9]
                width: 290,
                maxHeight: 28,
                margin: 1,
                layout: 'hbox',
                fieldLabel: 'Animate / Step',
                labelWidth: 100,
                items: [  
                    Ext.create('Ext.Button', {  //---- PLAY REVERSE  items[9].items[0]
                        //icon: '/images/PlayReverse_Hover14.png',
                        icon: Config.PlayReverse,
                        iconAlign: 'left',
                        itemId: 'gfilter2AnimReverse',
                        margin: "2 8 1 2",
                        maxHeight: 22,
                        maxWidth: 22,
                        listeners: {
                            click: function(me) {
                                // DEBUG
                                //console.log("<-");
                                
                                var owner = me.ownerCt.ownerCt;
                                var fdate = owner.getComponent('graphfilter2FromDate');
                                var tdate = owner.getComponent('graphfilter2ToDate');
                                var applyBut = owner.getComponent('btnApplyFilter2Settings');

                                if (gfilter2Animate.animateTimeout && gfilter2Animate.animateDirection != "B") {
                                    window.clearTimeout(gfilter2Animate.animateTimeout);
                                    gfilter2Animate.animateDirection = "B";  // set direction
                                }
                                owner.stepAnimate(fdate, tdate, applyBut, "B");    // step back/reverse
                                
                                // if not animating, start it
                                // for some reason the timeout does not persist during the next cycle, have to reset it each time.
                                if (owner.animateDelay > 0) {
                                    me.setLoading("<<");
                                    gfilter2Animate.animateTimeout = window.setTimeout(function() {
                                        me.fireEvent('click',me);
                                    }, owner.animateDelay * 800);                                    
                                } 
                                else {  // no longer animating
                                    gfilter2Animate.stopAnimation(owner);
                                }
                            }
                        }
                    }),
                    Ext.create('Ext.Button', {  //---- STOP/PAUSE items[9].items[1]
                        enableToggle: false,
                        //icon: '/images/Pause_Hover14.png',
                        icon: Config.Pause,
                        iconAlign: 'left',
                        itemId: 'gfilter2AnimPause',
                        margin: 2,
                        maxHeight: 22,
                        maxWidth: 22,
                        listeners: {
                            click: function(me) {
                                var owner = me.ownerCt.ownerCt;
 
                                // stop animation
                                // DEBUG
                                //console.log("p");

                                gfilter2Animate.stopAnimation(owner);
                            }
                        }
                    }),
                    Ext.create('Ext.Button', { //---- PLAY FORWARD items[9].items[2]
                        //icon: '/images/Play_Hover14.png',
                        icon: Config.Play,
                        iconAlign: 'right',
                        itemId: 'gfilter2AnimPlay',
                        margin: "2 10 1 8",
                        maxHeight: 22,
                        maxWidth: 22,
                        listeners: {
                            click: function(me) {
                                // DEBUG
                                //console.log("->");
                                
                                var owner = me.ownerCt.ownerCt;                                
                                var fdate = owner.getComponent('graphfilter2FromDate');
                                var tdate = owner.getComponent('graphfilter2ToDate');
                                var applyBut = owner.getComponent('btnApplyFilter2Settings');

                                if (gfilter2Animate.animateTimeout && gfilter2Animate.animateDirection != "F") {
                                    window.clearTimeout(gfilter2Animate.animateTimeout);
                                    gfilter2Animate.animateDirection = "F";  // set direction
                                }
                                
                                owner.stepAnimate(fdate, tdate, applyBut, "F");    // step forward   
                                
                                // if not animating, start it
                                // for some reason the timeout does not persist during the next cycle, have to reset it.
                                if (owner.animateDelay > 0) {
                                    me.setLoading(">>");
                                    gfilter2Animate.animateTimeout = window.setTimeout(function() {
                                        me.fireEvent('click',me);
                                    }, owner.animateDelay * 800);                                    
                                } 
                                else {  // no longer animating
                                    gfilter2Animate.stopAnimation(owner); 
                                }
                            }
                        }
                    }),
                    Ext.create('Ext.slider.Single', {   // items[9].items[3]
                        itemId: 'gfilter2AnimSpeed',
                        width: 76,
                        height: 24,
                        hideLabel: false,
                        label: 'Speed',
                        value: 0,    
                        increment: 1,
                        minValue: 0,
                        maxValue: 10,
                        listeners: {
                            changecomplete: function(slider, newValue, thumb) {  
                                var owner = slider.ownerCt.ownerCt;
                                owner.animateDelay = slider.maxValue - newValue + 1;
                                 
                                 // DEBUG
                                 //console.log("Speed change: newVal =" + newValue + ", animatespeed = " + slider.filterCmp.animateDelay);
                                 
                                // if speed is 0 this will also stop the animation
                                if (newValue == 0) {
                                    owner.animateDelay = 0;
                                    gfilter2Animate.stopAnimation(owner);
                                }
                             }
                        }
                    })
                ]
            })
	],
        
        // Animation
        animateDelay: 0,    // current animation speed
        
        // Reference to the Caller's Graph Frame
        graphFrame: null,
        
        graph:null,
        
        setGraph: function(gf) {            
	       if (gf) {
	             this.graph = gf;
	        }
	},

        searchFieldLabelText: "Identifiers(s)",  // can be changed using setSearchFieldLabel()
        
        
        // This sets the Graph Frame context for the Filter
        setGraphFrame: function(gf) {            
            if (gf) {
                this.graphFrame = gf;
            }
        },
        
        // This sets the Label (and Help) text for the Number or Ientifier search field
        setSearchFieldLabel: function(labelText) {
            var self = this;
            var fLabel = self.getComponent('graphfilter2Numbers');
            if (fLabel) {
                fLabel.setFieldLabel(labelText + ':');
                self.searchFieldLabelText = labelText;
            }
        },
        
        // Enables or disables the Time Filtering and Animation controls, this also enables/disables the From and To Date fields and sliders
        // mode - true or false.
        enableTimeFilter: function(mode) {
            var self = this;
            
            // DEBUG
            //console.log("enableTimeFilter mode = " + mode);            
            
            var fdate = self.getComponent('graphfilter2FromDate');
            var tdate = self.getComponent('graphfilter2ToDate');
            var dateSlider = self.getComponent('graphfilter2DateSlider');
            
            var ctlsList = [];
            
            // These controls are within a FieldContainer            
            if (self.items.items[9] !== undefined && self.items.items[9] != null) {
                var reverseBut = self.items.items[9].getComponent('gfilter2AnimReverse');
                var pauseBut = self.items.items[9].getComponent('gfilter2AnimPause');
                var playBut = self.items.items[9].getComponent('gfilter2AnimPlay');
                var speedCtl = self.items.items[9].getComponent('gfilter2AnimSpeed');
                ctlsList = [fdate,tdate,dateSlider,reverseBut,pauseBut,playBut,speedCtl];
            }
            else {
                ctlsList = [fdate,tdate,dateSlider];
            }
            var i, cmp;
            
            for (i = 0; i < ctlsList.length; i++) {
                cmp = ctlsList[i];
                if (cmp) {
                    if (mode == false) {
                        cmp.setDisabled(true);
                    }
                    else {
                        cmp.setDisabled(false);
                    }
                }
            }
        },
        
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
	getEventIds:function()
	{
		var self=this;
		return self.items.items[3].getValue();
	},   

        stepAnimate: function(fdate, tdate, applyBut, direction) {
            var self = this;
            var curFromDate = new Date(fdate.getDt());
            var fromDatems = curFromDate.getTime();
            //var deltams = 86459178; 
            var deltams = 86400000;
            var newFromDatems;
            if (direction == "F" || direction == "->") {
                direction = "F";
                newFromDatems = fromDatems + deltams + 1000;
            } 
            else { 
                direction = "B";
                newFromDatems = fromDatems - deltams;
            }
            
            var newFromDate = new Date(newFromDatems);  // some dates get 'stuck' and don't roll over to the next day
            if (newFromDate.getHours() > 1) {
                // DEBUG
                //console.log("from date stuck on " + newFromDate);
                if (direction == "F")
                    newFromDatems += deltams/2;
                else
                    newFromDatems -= deltams/2;
                
                newFromDate = new Date(newFromDatems);
            }

            var curToDate = new Date(tdate.getDt());
            var toDatems = curToDate.getTime();
            var newToDatems;
            if (direction == "F")
                newToDatems = toDatems + deltams + 1000;
            else 
                newToDatems = toDatems - deltams;
            
            var newToDate = new Date(newToDatems);
            // DEBUG
            //console.log("newToDate.getHours() = " + newToDate.getHours() + ", newToDate.getTime() = " + newToDate.getTime() + ", toDatems = " + toDatems);

            if (newToDate.getHours() > 1) {
                // DEBUG 
                //console.log("to date stuck on " + newToDate);
                if (direction == "F")
                    newToDatems += deltams/2;
                else
                    newToDatems -= deltams/2;
                
                newToDate = new Date(newToDatems);
            }
            var endTodayms = (new Date().getTime()) + 86399999;
            var earliestDatems = (new Date(2008,0,1,0,0,0)).getTime();    // default 2008 - TODO: SHOULD BE DEFINED IN ONE PLACE as a param
            if ((direction == "F" && newToDatems < endTodayms) || (direction == "B" && newFromDatems > earliestDatems)) {
                // DO NOT update the sliders as this will change the dates

                fdate.items.items[2].setValue(utils.formatDate(newFromDate));
                tdate.items.items[2].setValue(utils.formatDate(newToDate));

                // DEBUG
                //console.log("Play click newFrom = " + newFromDate + ", newTo = " + newToDate);
                
               var gf = self.graphFrame;
               var gr = gf.getGraph();

               gfilter2Animate.stopAnimation(self);  
               var fromDate = self.getComponent('graphfilter2FromDate').getDt();
               var toDate = self.getComponent('graphfilter2ToDate').getDt();
               var numbers = self.getComponent('graphfilter2Numbers').getValue(); 
               var amount = self.getComponent('graphfilter2Amount').getValue();
               if (gr) {
                    if (gr.prevLoadParams.searchNumber == null || gr.prevLoadParams.searchNumber.length == 0) {
                        alert("Enter at least one communication id of at least 10 digits in the Search Tab.");
                        return;
                    }
                    gr.applyFilter(numbers, amount, fromDate, toDate);  
               }
            }
            else {
                alert("Hit end of date range. Change the From and/or To Dates if needed.");
                gfilter2Animate.stopAnimation(self);
            }
        },
        
        // MFM 10/23/13 Added owner context
        updateGraphFilterDates: function(owner, fromDate, toDate) {
            var fdate = owner.getComponent('graphfilter2FromDate');
            var tdate = owner.getComponent('graphfilter2ToDate');
            var self = this;
            if (fdate) {
                var fromDateDate = new Date(fromDate);
                fdate.items.items[2].setValue(utils.formatDate(fromDateDate));
            }
            if (tdate) {
                var toDateDate = new Date(toDate);
                tdate.items.items[2].setValue(utils.formatDate(toDateDate));
            }

            self.setDateSliders(fromDate,toDate);
        },
    
        // These functions set the date filter slider controls
        // from and to dates are in ms
        // TODO - this needs more tuning
        setDateSliders: function(fromDate,toDate) {
            var self = this;
            var today = new Date().getTime(); 
            var fsmax = toDate + (15778800 * 1000);    // + 6 months
            if (fsmax > today) {
                fsmax = today;
            }
            
            var fsmin = fromDate - (15778800 * 1000);
            var fsfrom;
            var fsto;
            
            fsfrom = fromDate;
            fsto = toDate;
            
            //OLD var slider = Ext.getCmp('graphfilter2DateSlider');
            var slider = self.getComponent('graphfilter2DateSlider');
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