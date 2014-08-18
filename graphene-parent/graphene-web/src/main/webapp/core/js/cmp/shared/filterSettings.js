
/** 
* Reusable Component to Filter and Animate the Graph
* History
* 09/30/13  M. Martinet
* 10/24/13  M. Martinet - Revised to use itemIds instead of ids and added the functions listed in USAGE below.
* 01/14/14  M. Martinet - Fixed bugs with related to disabling time filtering
* 02/14/14  M. Martinet - Additional data-driven filtering
* 
* USAGE:
* When creating this component you will need to call setGraph() and optionally call enableTimefilter() and setSearchFieldLabel()
* It is Not necessary to give the component an id.
* EXAMPLE below:
* 
*   var filterSettings = Ext.create("DARPA.FilterSettings", {id:'PBGraphFilter'});
*               
*   // Have to delay this call a bit since the Graph Frame (see PB_Frame) is not defined yet
*   var wtimeout2 = window.setTimeout(function() {
*       window.clearTimeout(wtimeout2);
*       filterSettings.setGraph(graph());         <--- This sets the Graph Frame context for the Filter
*       filterSettings.enableTimeFilter(false);             <--- This enables or disables Time filtering and Animation
*       filterSettings.setSearchFieldLabel("Identifier")    <--- This sets the field label name for the Number/Identifier search field
*   }, 4000);
**/

// Added for annimation. TODO: should be in a namespace
var gfilterAnimate = {
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
                // The Animation buttons are within a FieldContainer
                var brev = owner.items.items[10].getComponent('gfilterAnimReverse');
                var bfwd = owner.items.items[10].getComponent('gfilterAnimPlay');  
                brev.setLoading(false);
                bfwd.setLoading(false);
            }        
        }
    }
};


Ext.define("DARPA.FilterSettings",
{
	extend:"Ext.form.FieldSet",
        // id:    CAN be set in the calling code but NOT here as this is a reusable component.
	items:[
            Ext.create('DARPA.dategroup', {itemId: 'graphfilterFromDate', margins: '2,20,0,1', label:'From:', align:'left'}), // bottom left right top, from date
            Ext.create('DARPA.dategroup', {itemId: 'graphfilterToDate', margins: '2,0,20,1', label:'To:' , align:'left'}),   // to date

            // Slider with multiple controls for changing the start and end dates
            Ext.create('Ext.slider.Multi', {    // items[2]
                itemId: 'graphfilterDateSlider',
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
                         var fdate = owner.getComponent('graphfilterFromDate');
                         var tdate = owner.getComponent('graphfilterToDate');

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
                    itemId: 'graphfilterNumbers',
                    labelLength:70,
                    value:'',
                    width: 280

            },
            // Added 11/07/13
            {    // This field is for filtering by the link amount or number of calls. items[4]
                    xtype:'textfield',		
                    fieldLabel:'Amount (Min,Max)', 
                    itemId: 'graphfilterAmount',
                    labelLength:70,
                    value:'',
                    width: 280

            },
            // MFM JIRA
            // Button to display additional fields panel items[5]
            {
                    xtype:'button',
                    text:'ADDITIONAL ATTRIBUTES',
                    disabled:false,
                    itemId: 'btnAdditionalFilterSettings',
                    margin:4,   
                    height:24,
                    width:224,
                    listeners: {
                        click: function(but)
                        {                               
                            var owner = but.ownerCt;
                            var gr = owner.graph;
                            var addFieldsGrid;
                     
                            if (gr && gr.GraphVis) {    // The graph must be created and not empty
                               addFieldsGrid = owner.getAdditionalFieldsGrid(gr);
                               //OLD owner.loadAdditionalFieldsGrid(gr); 
                               var bpos = but.getPosition();
                               bpos[0] = bpos[0] - 200;    // offset the panel's x position
                               
                               // display this in a separate window
                               var addFieldsWindow = Ext.getCmp(but.id + 'addFieldsWin');
                               if (addFieldsWindow === undefined || addFieldsWindow == null) {
                               
                                  addFieldsWindow = Ext.create('Ext.window.Window', {
                                    title: 'Filter Additional Attributes',
                                    id: but.id + 'addFieldsWin',
                                    height: 360,    // Adjust as needed
                                    width: 450,
                                    minHeight:240,
                                    maxHeight:600,
                                    minWidth: 400,
                                    layout: 'fit',
                                    minimizable: false,
                                    maximizable: false,
                                    resizable: true,
                                    modal: true,
                                    x: bpos[0],  // initial position
                                    //y: 650,
                                    items: addFieldsGrid,
                                    listeners: {
                                        beforeclose: function(panel) {    // User is closing this window                
                                            panel.hide(panel);   // don't  close, just hide
                                            return false;
                                        },
                                        hide: function(panel) {    // Hiding this window
                                            // Hook may not use
                                        }
                                    }
                                  }
                                  );
                               }
                               addFieldsWindow.show();
                            }
                        }
                    }
            },
            
            // added to update settings and redisplay. items[6]
            {
                    xtype:'button',
                    text:'CLEAR',
                    disabled:false, // MFM enabled
                    itemId: 'btnClearFilterSettings',
                    margin:4,   
                    height:24,
                    width:108,
                    listeners: {
                        click: function(but)
                        {   
                            var owner = but.ownerCt;
                            var numbers = owner.getComponent('graphfilterNumbers');
                            var amount = owner.getComponent('graphfilterAmount');
                            
                            gfilterAnimate.stopAnimation(owner);
                            
                            var fromDate = 0;
                            var toDate = 0;
                            
                            // TODO should be owner.getSearchFrame and should have a set method for the caller
                            //var srch = getSearchFrame().getSearch();  
                            //if (srch) {
                            //    fromDate = srch.getFromDate();
                            //    toDate   = srch.getToDate();
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

                            // should have a set graph method for the caller
                            var gr = owner.graph;
                            if (gr) {
                                gr.clearFilter();
                            }
                        }
                    } // listeners
                },
                {   // items[7]
                    xtype:'button',
                    text:'APPLY TO GRAPH', // Only apply the changes to the graph
                    disabled:false, // enabled
                    itemId: 'btnApplyFilterSettings',
                    margin:4,   
                    height:24,
                    width:108,
                    listeners: {
                        click: function(but)
                        {
                            var owner = but.ownerCt;
                            var gr = owner.graph;
                            
                            var fromDate = null;
                            var toDate = null;
                            var fdate = owner.getComponent('graphfilterFromDate');
                            if (fdate && fdate.isDisabled() == false) {
                                fromDate = fdate.getDt();
                            }
                            var tdate = toDate = owner.getComponent('graphfilterToDate');
                            if (tdate && tdate.isDisabled() == false) {
                                toDate = tdate.getDt();
                            }
                            var numbers = owner.getComponent('graphfilterNumbers').getValue(); // Can be any string or set of comma delimited strings
                            var amount = owner.getComponent('graphfilterAmount').getValue();

                            if (toDate != null && toDate != 0 && fromDate != null && toDate < fromDate) { // to and from can be the same
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
                            
                            gfilterAnimate.stopAnimation(owner);
                            
                            if (gr) {
                                if (gr.prevLoadParams && gr.prevLoadParams.searchNumber) {
                                    if (gr.prevLoadParams.searchNumber == null || gr.prevLoadParams.searchNumber.length == 0) {
                                        alert("Enter at least one number or id in the Search Tab.");
                                        return;
                                    }
                                }
                                // NOTE: from and to dates will be null when time filtering is disabled
                                gr.applyFilter(numbers, amount, fromDate, toDate);  
                            }
                        }
                    } // listeners
                },
                {   // help button items[8]
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
                {   // items[9]
                    xtype: 'label',
                    margin: 0,
                    html: "<hr>",
                    width: 240
                },
            // Animate Temporal controls
            Ext.create('Ext.form.FieldContainer', { // items[10]
                width: 290,
                maxHeight: 28,
                margin: 1,
                layout: 'hbox',
                fieldLabel: 'Animate / Step',
                labelWidth: 100,
                items: [  
                    Ext.create('Ext.Button', {  //---- PLAY REVERSE  items[10].items[0]
                        //icon: '/images/PlayReverse_Hover14.png',
                        icon: Config.PlayReverse,
                        iconAlign: 'left',
                        itemId: 'gfilterAnimReverse',
                        margin: "2 8 1 2",
                        maxHeight: 22,
                        maxWidth: 22,
                        listeners: {
                            click: function(me) {
                                // DEBUG
                                //console.log("<-");
                                
                                var owner = me.ownerCt.ownerCt;
                                var fdate = owner.getComponent('graphfilterFromDate');
                                var tdate = owner.getComponent('graphfilterToDate');
                                var applyBut = owner.getComponent('btnApplyFilterSettings');

                                if (gfilterAnimate.animateTimeout && gfilterAnimate.animateDirection != "B") {
                                    window.clearTimeout(gfilterAnimate.animateTimeout);
                                    gfilterAnimate.animateDirection = "B";  // set direction
                                }
                                owner.stepAnimate(fdate, tdate, applyBut, "B");    // step back/reverse
                                
                                // if not animating, start it
                                // for some reason the timeout does not persist during the next cycle, have to reset it each time.
                                if (owner.animateDelay > 0) {
                                    me.setLoading("<<");
                                    gfilterAnimate.animateTimeout = window.setTimeout(function() {
                                        me.fireEvent('click',me);
                                    }, owner.animateDelay * 800);                                    
                                } 
                                else {  // no longer animating
                                    gfilterAnimate.stopAnimation(owner);
                                }
                            }
                        }
                    }),
                    Ext.create('Ext.Button', {  //---- STOP/PAUSE items[10].items[1]
                        enableToggle: false,
                        //icon: '/images/Pause_Hover14.png',
                        icon: Config.Pause,
                        iconAlign: 'left',
                        itemId: 'gfilterAnimPause',
                        margin: 2,
                        maxHeight: 22,
                        maxWidth: 22,
                        listeners: {
                            click: function(me) {
                                var owner = me.ownerCt.ownerCt;
                                gfilterAnimate.stopAnimation(owner);
                            }
                        }
                    }),
                    Ext.create('Ext.Button', { //---- PLAY FORWARD items[10].items[2]
                        //icon: '/images/Play_Hover14.png',
                        icon: Config.Play,
                        iconAlign: 'right',
                        itemId: 'gfilterAnimPlay',
                        margin: "2 10 1 8",
                        maxHeight: 22,
                        maxWidth: 22,
                        listeners: {
                            click: function(me) {
                                // DEBUG
                                //console.log("->");
                                
                                var owner = me.ownerCt.ownerCt;                                
                                var fdate = owner.getComponent('graphfilterFromDate');
                                var tdate = owner.getComponent('graphfilterToDate');
                                var applyBut = owner.getComponent('btnApplyFilterSettings');

                                if (gfilterAnimate.animateTimeout && gfilterAnimate.animateDirection != "F") {
                                    window.clearTimeout(gfilterAnimate.animateTimeout);
                                    gfilterAnimate.animateDirection = "F";  // set direction
                                }
                                
                                owner.stepAnimate(fdate, tdate, applyBut, "F");    // step forward   
                                
                                // if not animating, start it
                                // for some reason the timeout does not persist during the next cycle, have to reset it.
                                if (owner.animateDelay > 0) {
                                    me.setLoading(">>");
                                    gfilterAnimate.animateTimeout = window.setTimeout(function() {
                                        me.fireEvent('click',me);
                                    }, owner.animateDelay * 800);                                    
                                } 
                                else {  // no longer animating
                                    gfilterAnimate.stopAnimation(owner); 
                                }
                            }
                        }
                    }),
                    Ext.create('Ext.slider.Single', {   // items[10].items[3]
                        itemId: 'gfilterAnimSpeed',
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
                                    gfilterAnimate.stopAnimation(owner);
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
        graph: null,
        
        additionalFields: [],   // This is a config attribute to the filter settings object. 
                                // It defines the additional attributes in the graph that can be filtered
        
        searchFieldLabelText: "Identifiers(s)",  // can be changed using setSearchFieldLabel()
        
        // This sets the Graph Frame context for the Filter
        setGraph: function(gf) {            
            if (gf) {
                this.graph = gf;
            }
        },
        
        // This sets the Label (and Help) text for the Number or Ientifier search field
        setSearchFieldLabel: function(labelText) {
            var self = this;
            var fLabel = self.getComponent('graphfilterNumbers');
            if (fLabel) {
                fLabel.setFieldLabel(labelText + ':');
                self.searchFieldLabelText = labelText;
            }
        },
        
        // Defines the additional attributes in the graph that can be filtered.
        // The attributes can be specific ones and/or can be based on the attribute data in the graph's nodes and edges
        // addFields    - Array of objects specifying additional filter attributes
        // RETURNS:     - Status string.  "OK" or "Error: with details of the error"
        setAdditionalFields: function(addFields) {
            var addField, i;
            var statusmsg = "";
            
            // Basic validation. Each item in the array should have the following fields:
            /**
            {
               dispFieldName:	string | "getfromkey"	// name to display in the grid
                                                        // getfromkey: get the display text from the attrs[j].key  value
               dispFieldType:	"text" | "dropdown" | "other"
               dispFieldWidth:	width of the input field (not yet used)
               dispFieldChoices:    string | "getfromnode",  // comma separated choices, or get the choices from the node's field
               dataSourceType:	"nodes" | "edges" | "other TBD"
               dataSourceField:	"color" | "idType" | "name" | "amount" | "weight" | "attrs"
                                    // Special case: when "attrs" - this iterates over all of the attrs and builds the
                                    // fields in the grid from each attribute (key, value) pair
            }
            **/
           
           if (addFields.length == 0) {
               return "Error: specified additional fields array is empty";
           }
           for (i = 0; i < addFields.length; i++) {
               addField = addFields[i];
               if ( typeof (addField.dispFieldName) == "undefined") {
                   statusmsg = statusmsg + "dispFieldName property is missing from item " + i.toString()+ "\n";
               }
               if ( typeof (addField.dispFieldType) == "undefined") {
                   statusmsg = statusmsg + "dispFieldType property is missing from item " + i.toString()+ "\n";
               }
               if ( typeof (addField.dispFieldWidth) == "undefined") {
                   statusmsg = statusmsg + "dispFieldWidth property is missing from item " + i.toString()+ "\n";
               }
               // dispFieldChoices - skip, this is optional
               if ( typeof (addField.dataSourceType) == "undefined") {
                   statusmsg = statusmsg + "dataSourceType property is missing from item " + i.toString()+ "\n";
               }
               if ( typeof (addField.dataSourceField) == "undefined") {
                   statusmsg = statusmsg + "dataSourceField property is missing from item " + i.toString()+ "\n";
               }
               if (statusmsg.length > 0) {  // no need to continue generating additional errors for each item
                   return "Error: " + statusmsg;
               }
           }
           this.additionalFields = addFields;
           return "OK"; 
        },
        
        // RETURNS:     - Array of objects specifying additional filter attributes
        //              - See setAdditionalFields() for a definition of the object fields
        getAdditionalFields: function() {
            return this.additionalFields;
        },
        
        // RETURNS:     - An ArrayStore for the additional fields grid 
        //              - See setAdditionalFields() for a definition of the object fields
        makeAdditionalFieldsStore: function() {
            
            var afStore = new Ext.data.ArrayStore({
                fields: [
                    { name: 'dispFieldName', type: 'string' }, 
                    { name: 'dispFieldType', type: 'string' },
                    { name: 'dispFieldChoices', type: 'string' }, // an array or comma separated strings
                    { name: 'dataSourceType', type: 'string' },
                    { name: 'dataSourceField', type: 'string' },
                    { name: 'value', type: 'string'}
                ],
                data: [],
                sorters: [ 'dispFieldName'],  
                proxy: new Ext.data.MemoryProxy()
            });
            
            return afStore;
        },
        
        // RETURNS:     - A GridPanel for displaying the additional fields
        getAdditionalFieldsGrid: function(graph) {
            var self = this;
            var addFieldsGridPanel = Ext.getCmp(self.id + 'addFieldsGrid');
            if (addFieldsGridPanel === undefined || addFieldsGridPanel == null) {
                
                addFieldsGridPanel = new Ext.grid.GridPanel({
                    height: 350,
                    width: 440,
                    id: self.id + "addFieldsGrid",
                    collapsible: false,
                    titleCollapse: false,
                    autoShow: true,
                    forceLayout: true,
                    // Row Selection Model by default
                    stripeRows: true,
                    autoWidth: true,
                    autoScroll: true,  
                    border: 2,
                    closeable: true,
                    closeAction: 'destroy',
                    frame: true,
                    resizeable: true,
                    //floating: true,
                    enableColumnMove: false,
                    selType:'cellmodel',
                    graph: graph,           // ref to the Graph
                    doFuzzySearch: true,    // initial setting is fuzzy search, changed using a checkbox
                    plugins: [
                        Ext.create('Ext.grid.plugin.CellEditing', 
                            { clicksToEdit:1,
                              listeners: {  // JIRA-17 to handle the case when user hit Enter to initiate the search   
                               edit: function(editor, e) {                                    
                                    var selRecData = e.record.data; 

                                    // DEBUG
                                    //console.log("filterSettings: cell edit");
                               }
                              }    
                            }
                        )
                    ],
                        
                    store: self.makeAdditionalFieldsStore(),

                    columns: [
                        {
                            header: 'Field',
                            //id: 'name',
                            width: 120,
                            sortable: true,
                            editable: false,
                            dataIndex: 'dispFieldName'
                        },
                        {
                            header: 'Filter Choices',
                            width: 140,
                            sortable: false,
                            dataIndex: 'dispFieldChoices',
                            editor: {
                                xtype: 'textfield',
                                grow: true,
                                readOnly: true
                                //,
                                //listeners: {
                                //    focus: function(choices, e, opts) {
                                //    }
                                //}
                            },
                            // This displays a tooltip with the choices if multiple items
			    renderer: function(value, metaData, record, rowIndx, colIndx, store) {
                                if (value.indexOf(",") > 0) {
                                    var i;
                                    var fieldName = (record.data.dispFieldName) ? record.data.dispFieldName : "Field";
                                    var out = "<b>" + fieldName + " Choices:</b><br>";                                    
                                    var choices = value.split(",");
                                    for (i = 0; i < choices.length; i++) {
                                        out = out + choices[i] + "<br>";
                                    }
                                    metaData.tdAttr = 'data-qtip="' + out + '"';
                                }
                                return value;
                            }
                        },
                        {
                            header:'Filter',
                            sortable:false,
                            flex:1,
                            itemId: 'addFilterInputField',
                            editable: true,
                            menuDisabled:true,				
                            dataIndex:'value',
                            field: {
                                xtype:'textfield',  // TODO may want to use "DARPA.TextField" with definition of enterCallback
                                triggerAction:'all',
                                selectOnTab:false
                            }
			},
                     ],

                    buttons: [
                        '->',
                        {
                            xtype: 'label', 
                            text: 'Fuzzy Search:',
                            style: { "margin-right": "1px" }
                        },
                        {
                            xtype: 'checkbox',
                            margin: '1 8 1 2',
                            checked: true,  // by default
                            listeners: {
                                change: function(cmp, newVal, oldVal, opts) {
                                    
                                    var gp = cmp.ownerCt.ownerCt;
                                    // clicked 
                                    if (newVal === true) {
                                        gp.doFuzzySearch = true;
                                    }
                                    else {
                                        gp.doFuzzySearch = false;
                                    }
                                }
                            }
                        },
                        { xtype: 'tbseparator' },
                        {
                            text: '&nbsp;&nbsp;CLEAR', // needs 2 spaces at the front
                            //icon: $images.choose,
                            disabled: false,
                            cls: 'x-toolbar-standardbutton',
                            listeners: {
                                click: function(but, e) {
                                    var rsgrid = but.ownerCt.ownerCt;
                                    if (rsgrid) {                                         
                                        rsgrid.clearFilterValues();
                                        
                                        // call the apply to restore the graph
                                        var bup = but.ownerCt;
                                        var apply = bup.getComponent('addFieldsFilterApply');
                                        if (apply) {
                                            apply.fireEvent('click', apply, e);
                                        }  
                                    }
                                }
                            }
                        },
                        { xtype: 'tbseparator' },
                        {
                            text: '&nbsp;&nbsp;APPLY', // needs 2 spaces at the front
                            disabled: false,
                            cls: 'x-toolbar-standardbutton',
                            itemId: 'addFieldsFilterApply',
                            listeners: {
                                click: function(but, e) {
                                   var rsgrid = but.ownerCt.ownerCt;
                                    if (rsgrid) {                                        
                                        rsgrid.applyFilterValues();
                                    }
                                }
                            }
                        },
                        // NO NEED FOR CANCEL - user can just close the window
                        { xtype: 'tbseparator' },
                        {
                            icon: Config.helpIcon,  // HELP
                            disabled: false,
                            maxHeight: 30,
                            maxWidth: 30,
                            scale: 'medium',
                            margin: 2,
                            style: {marginTop: '2px'},
                            listeners: {
                                click: function(but, e) {
                                   Ext.Msg.alert(
                                   'Help',
                                   '<b>Filter Choices</b>:&nbsp;&nbsp;Shows a range of filter choices if known in a tooptip.<br><br>' +
                                   '<b>Filter</b>:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;Enter your filter string here. This can be a number or partial string.<br><br>' +
                                   '<b>Fuzzy Search</b>:&nbsp; Check this box to perform fuzzy case-insensitive searches, uncheck this box to perform exact case-sensitive searches.<br><br>' +
                                   '<b>CLEAR</b>:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This will clear the filter settings and redisplay the graph. ' +
                                   'This will also redisplay any nodes that have been manually hidden.<br><br>' +
                                   '<b>APPLY</b>:&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; This will display the graph with the filter settings.<br><br>'
                                   
                                    );
                                }
                            }
                        }
                    ],
                                   
                    // Clear all of the filter values and update the graph
                    clearFilterValues: function() {
                        var self = this;
                        var gs = self.getStore();
                        var gData = gs.data;
                        var dataItem;
                        for (var i = 0; i < gData.items.length; i++) {
                            dataItem = gData.items[i];
                            dataItem.data.value = ""; 
                        }
                        gs.commitChanges(); // may not need
                        
                        // TODO call the parent graph filter (apply) function
                        // self.gr.xxx
                    },
                    
                    // Apply the filter values to the graph
                    applyFilterValues: function() {
                        var self = this;
                        var gs = self.getStore();
                        var gData = gs.data;                        
                        var gr = self.graph;
                        // apply these filters to the graph
                        var compareType = (self.doFuzzySearch) ? "fuzzy" : "exact";
                        gr.applyAdditionalFieldsFilter(gData.items, compareType);
                    },
                    
                    // This parses the additionalFields (specification) that was set with setAdditionalFields()
                    // and loads the additional fields grid with the actual fields the user can filter on.
                    // Some of the fields are obtained from the attribute data in the graph's nodes and edges.
                    // The graph must have been previously loaded.
                    // gr       - Graph
                    loadAdditionalFieldsGrid: function(gr) {
                        var addFieldsGrid = this;
                        var nodeAttrKeys = {}; // a list of attribute (key) names
                        var edgeAttrKeys = {}; // a list of attribute (key) names
                        var nodeColors = [];   // a partial list of unique node colors
                        var nodeNames = [];    // a partial list of unique node names
                        var nodeIdTypes = [];  // a partial list of unique node idTypes
                        var gridStoreData = [];
                        var i, j, aField;
                        var neCount, found;
                        var gv;
                        // DEBUG
                        //console.log("loadAdditionalFieldsGrid");

                        if (gr.GraphVis) {
                            gv = gr.GraphVis.getGv();

                            if (gv) {
                               // Get the distinct dynamic attributes for a node
                               // TODO optimize this: modify the service to return this metadata info
                               neCount = 0;
                               gv.nodes().each(function(indx, node) { // sample just the first 100
                                    if (node) {
                                       var nattrs = node.data().attrs;
                                       var nodeColor = node.data().color;
                                       var nodeName = node.data().name;
                                       var nodeIdType = node.data().idType;

                                       for (i = 0; i < nattrs.length; i++) {    // uses a hash table
                                           // add only distinct (new) items
                                           var nk = nodeAttrKeys[nattrs[i].key];
                                           if (nk === undefined || nk == null) {
                                                if (nattrs[i].key.indexOf("node-prop") < 0) // omit node-prop-displayValue from the list
                                                    nodeAttrKeys[nattrs[i].key] = nattrs[i].key;
                                           }
                                       }

                                       // colors
                                        found = false;                          
                                        for (j = 0; j < nodeColors.length; j++) {   // should use a hash table
                                            if (nodeColor == nodeColors[j]) {
                                                found = true;
                                            }
                                        }
                                        if (!found) {
                                            nodeColors.push(nodeColor);
                                        }
                                        
                                        // names - a partial list
                                        found = false;                          
                                        for (j = 0; j < nodeNames.length; j++) {    // should use a hash table
                                            if (nodeName == nodeNames[j]) {
                                                found = true;
                                            }
                                        }
                                        if (!found) {
                                            nodeNames.push(nodeName);
                                        }
                                        
                                        // idTypes - a partial list
                                        found = false;                          
                                        for (j = 0; j < nodeIdTypes.length; j++) {    // should use a hash table
                                            if (nodeIdType == nodeIdTypes[j]) {
                                                found = true;
                                            }
                                        }
                                        if (!found) {
                                            nodeIdTypes.push(nodeIdType);
                                        }
                                    }
                                    neCount++;
                                    if (neCount > 100) {    // limit to scan of first 100 nodes
                                       return (false);    // break out of each    
                                    }
                                });
        
                                // Get the distinct dynamic attributes for an edge - sample just the first 100
                                neCount = 0;
                                gv.edges().each(function(indx, edge) { 
                                    if (edge) {
                                       var eattrs = edge.data().attrs;
                                       for (i = 0; i < eattrs.length; i++) {  // uses a hash table
                                           // add only distinct (new) items
                                           var ek = edgeAttrKeys[eattrs[i].key];
                                           if (ek === undefined || ek == null) {
                                                edgeAttrKeys[eattrs[i].key] = eattrs[i].key;
                                           }
                                       }
                                    }
                                    neCount++;
                                    if (neCount > 100) {    // limit to scan of first 100 edges
                                       return (false);    // break out of each    
                                    }
                                });
                            }

                            for (i = 0; i < self.additionalFields.length; i++) {
                                aField = self.additionalFields[i];
                                if (aField.dispFieldName == "getfromkey") { // get the field names from the node or edge attrs keys
                                    if (aField.dataSourceType == "nodes") {
                                        //for (j = 0; j < nodeAttrKeys.length; j++) {
                                        var name;
                                        for (name in nodeAttrKeys) {
                                            var gridStoreItem = {};
                                            //gridStoreItem.dispFieldName = nodeAttrKeys[j];
                                            gridStoreItem.dispFieldName = name;
                                            gridStoreItem.dispFieldType = aField.dispFieldType;
                                            gridStoreItem.dispFieldChoices = " ";
                                            gridStoreItem.dataSourceType = aField.dataSourceType;
                                            gridStoreItem.dataSourceField = aField.dataSourceField;
                                            gridStoreData.push(gridStoreItem);
                                        }
                                    }
                                    else if (aField.dataSourceType == "edges") {
                                        //for (j = 0; j < edgeAttrKeys.length; j++) {
                                        for (name in edgeAttrKeys) {
                                            var gridStoreItem2 = {};
                                            //gridStoreItem2.dispFieldName = edgeAttrKeys[j];
                                            gridStoreItem2.dispFieldName = name;
                                            gridStoreItem2.dispFieldType = aField.dispFieldType;
                                            gridStoreItem2.dispFieldChoices = " ";
                                            gridStoreItem2.dataSourceType = aField.dataSourceType;
                                            gridStoreItem2.dataSourceField = aField.dataSourceField;
                                            gridStoreData.push(gridStoreItem2);
                                        }
                                    }
                                }
                                else {
                                    var gridStoreItem3 = {};
                                    gridStoreItem3.dispFieldName = aField.dispFieldName;
                                    gridStoreItem3.dispFieldType = aField.dispFieldType;
                                    var choices = "";
                                    var c;
                                    if (aField.dispFieldChoices == "getfromnode") {
                                        // node Colors
                                        if (aField.dataSourceField == "color") {
                                            for (c = 0; c < nodeColors.length; c++) {
                                                choices = choices + nodeColors[c];
                                                if (c < nodeColors.length - 1)
                                                    choices = choices + ",";
                                            }
                                        }
                                        // node Names
                                        else if (aField.dataSourceField == "name") {
                                            for (c = 0; c < nodeNames.length; c++) {
                                                choices = choices + nodeNames[c];
                                                if (c < nodeNames.length - 1)
                                                    choices = choices + ",";
                                            }
                                        }
                                        // node idTypes
                                        else if (aField.dataSourceField == "idType") {
                                            for (c = 0; c < nodeIdTypes.length; c++) {
                                                choices = choices + nodeIdTypes[c];
                                                if (c < nodeIdTypes.length - 1)
                                                    choices = choices + ",";
                                            }
                                        }
                                    }
                                    else {
                                        for (c = 0; c < aField.dispFieldChoices.length; c++) {
                                            choices = choices + aField.dispFieldChoices[c];
                                            if (c < aField.dispFieldChoices.length - 1)
                                                choices = choices + ",";
                                        }
                                    }
                                    gridStoreItem3.dispFieldChoices = choices;
                                    gridStoreItem3.dataSourceType = aField.dataSourceType;
                                    gridStoreItem3.dataSourceField = aField.dataSourceField;
                                    gridStoreData.push(gridStoreItem3);
                                }
                            }
                            addFieldsGrid.store.loadData(gridStoreData);
                        }
                        else {
                            Ext.Msg.alert("No graph data is available for this Entity.");
                        }
                    }
                });
             
                addFieldsGridPanel.graph = graph;
                addFieldsGridPanel.store.addListener('load', function(store, records, options) {
                    store.sort('dispFieldName', 'DESC'); 
                });
            }
            else {
                // DEBUG
                //console.log("getAdditionalFieldsGrid getting EXISTING");
            }
            
            addFieldsGridPanel.loadAdditionalFieldsGrid(graph); // always load/reload since the graph data may have changed 
            return addFieldsGridPanel;
        },
        
        // Enables or disables the Time Filtering and Animation controls, this also enables/disables the From and To Date fields and sliders
        // mode - true or false.
        enableTimeFilter: function(mode) {
            var self = this;
            
            // DEBUG
            //console.log("enableTimeFilter mode = " + mode);            
            
            var fdate = self.getComponent('graphfilterFromDate');
            var tdate = self.getComponent('graphfilterToDate');
            var dateSlider = self.getComponent('graphfilterDateSlider');
            
            var ctlsList = [];
            
            // These controls are within a FieldContainer
            var itemIndex = 10;
            if (self.items.items[itemIndex] !== undefined && self.items.items[itemIndex] != null) {
                var reverseBut = self.items.items[itemIndex].getComponent('gfilterAnimReverse');
                var pauseBut = self.items.items[itemIndex].getComponent('gfilterAnimPause');
                var playBut = self.items.items[itemIndex].getComponent('gfilterAnimPlay');
                var speedCtl = self.items.items[itemIndex].getComponent('gfilterAnimSpeed');
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
                
               var gr = self.graph;

               gfilterAnimate.stopAnimation(self);  
               var fromDate = self.getComponent('graphfilterFromDate').getDt();
               var toDate = self.getComponent('graphfilterToDate').getDt();
               var numbers = self.getComponent('graphfilterNumbers').getValue(); 
               var amount = owner.getComponent('graphfilterAmount').getValue();
               if (gr) {
                    if (gr.prevLoadParams.searchNumber == null || gr.prevLoadParams.searchNumber.length == 0) {
                        alert("Enter at least one number or id in the Search Tab.");
                        return;
                    }
                    gr.applyFilter(numbers, amount, fromDate, toDate);  
               }
            }
            else {
                alert("Hit end of date range. Change the From and/or To Dates if needed.");
                gfilterAnimate.stopAnimation(self);
            }
        },
        
        // MFM 10/23/13 Added owner context
        updateGraphFilterDates: function(owner, fromDate, toDate) {
            var fdate = owner.getComponent('graphfilterFromDate');
            var tdate = owner.getComponent('graphfilterToDate');
            if (fdate.isDisabled() == false && tdate.isDisabled() == false) {   // MFM bug fix
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
            }
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
            
            var slider = self.getComponent('graphfilterDateSlider');
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