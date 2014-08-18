
Ext.define('GTransaction', {
    extend: 'Ext.data.Model',
    fields: 
    [
	    {name: 'date'}, 
	    {name: 'comments'}, 
	    {name: 'debit'},
	    {name:'amount', convert:function(value,record) {
		    	// The table is messed up. Where the transactions are arranged with sender and receiver, the 
		    	// concepts of debit and credit are ambiguous.
		    	return record.get('debit') + record.get('credit');
		    }
	    },
   
	    {name: 'credit'},
	    {name: 'senderId'},
	    {name: 'receiverId'},	    
	    {name: "senderValue", mapping: "data.senderValue"},
	    {name: 'receiverValue', mapping: "data.receiverValue"},	 
	    {name: 'subject', mapping: "data.subject"},
	    {name: 'comments', mapping: 'comments'},
   	    {name: 'amount', convert:function(value,record) { 
   	    		return record.get('debit') == 0 ? record.get('credit') : record.get('debit'); 
   	    	}
   	    }
	]
	
});


Ext.define("DARPA.TransfersGrid",  {

    extend:'Ext.grid.Panel',
    
    	title:'Transfers',
    
    	constructor:function(config) {
    		this.store=makeTransferStore(this); // done in constructor otherwise we get the same store in all the ledgers
		this.callParent(arguments);    		
    		return this;
    	},
    
    	store:null,

        verticalScroller: {
        	leadingBufferZone:500, 		// nbr of rows prior to the grid display to keep 
        	trailingBufferZone:1000		// nbr of rows after the grid display to keep
       	},
       
        verticalScrollerType:'paginggridscroller', // new

        viewConfig: {
        	invalidateScrollerOnRefresh:true,
        	loadMask:false,
                enableTextSelection: true   // MFM added so that can copy the values
       	},
        
        align:'left',
	columns: [
		{header: 'Date',		width: '5%',	dataIndex: 'date',			sortable:true}, 					// 0
		{header: 'Size ',		width: '4%',	dataIndex: 'amount',		sortable:true /*align:'right'*/},
		{header: 'From',		width: '15%',	dataIndex: 'senderValue',	sortable:true},// 1	
		{header: 'To',			width: '15%',	dataIndex: 'receiverValue',	sortable:true},// 1				
		{header: 'Subject', 	width: '15%', 	dataIndex: 'subject', 		sortable: true},
		//{header: 'Email Body', 	width:' 45%',	dataIndex: 'comments',		sortable: true}
		{header: 'Email Body',	width: '45%', renderer: function() {
			return "<span style='color:blue'><b>[Right-click to view email content]</b></span>";
		}}
	],
        
        // MFM JIRA-68 local filtering of search results
        bbar: [     // internally this is a docked toolbar
            {
                xtype: 'label',     // 0
                text: 'Filter',
                margin: '1 4 1 4',
                style: { fontSize: "small" }
            }, 
            {
                xtype: 'textfield',      // 1
                hideLabel: true,
                caseSensitive: false,    // can be changed by the checkbox after this
                width: 120,
                listeners: {
                    change: function(self, newVal, oldVal, opts) {
                        var bbar = self.ownerCt;
                        var grid = bbar.ownerCt;                     
                        var filterVal = newVal.trim();  // ignore any spaces   
         
                        grid.store.clearFilter();
                        
                        // always clear the second field when this one changes
                        var searchFilter2 = bbar.items.items[3];
                        searchFilter2.setValue("");
                        
                        if (filterVal.length > 0) {
                            var filters = [];
                            filters = new Ext.util.Filter({
                                filterFn: function (item) {
                                    // Multi column search with OR logic
                                    return grid.gridFilter(item, filterVal, self.caseSensitive);
                                }
                            });
                            grid.store.filter(filters);
                        }
                    }
                }
            },
            {
                xtype: 'label',      // 2
                text: 'AND',
                margin: '1 5 1 2',
                style: { fontSize: "small" }
            }, 
            {
                xtype: 'textfield',      // 3
                hideLabel: true,
                caseSensitive: false,    // can be changed by the checkbox after this
                width: 120,
                listeners: {
                    change: function(self, newVal, oldVal, opts) {
                        var bbar = self.ownerCt;
                        var grid = bbar.ownerCt;
                        
                        var filterVal2 = newVal.trim();  // ignore any spaces   
                        
                        grid.store.clearFilter();
                        var searchFilter1 = bbar.items.items[1];
                        var filterVal1 = searchFilter1.getValue().trim();

                        var filters = [];
                        filters = new Ext.util.Filter({
                            filterFn: function (item) {
                                // Multi column search with OR logic
                                return grid.gridFilterAND(item, filterVal1, filterVal2, self.caseSensitive);
                            }
                        });
                        grid.store.filter(filters);
                    }
                }
            },
            {
                xtype: 'checkbox',      // 4
                boxLabel: 'Case Sensitive',
                margin: '1 8 1 8',  // t,r,b,l
                checked: false,
                handler: function(cb, checked) {
                    var bbar = cb.ownerCt;
                    var grid = bbar.ownerCt;
                    var searchFilter1 = bbar.items.items[1];
                    var searchFilter2 = bbar.items.items[3];
                    
                    if (searchFilter1 && searchFilter2) {
                        var prevCheckVal = searchFilter1.caseSensitive; 
                        searchFilter1.caseSensitive = checked;
                        searchFilter2.caseSensitive = checked;
                        if (prevCheckVal != checked) {
                            // force the filters to run with the new setting
                            var searchVal1 = searchFilter1.getValue();                            
                            searchFilter1.fireEvent('change', searchFilter1, searchVal1, "junk", null);
                            var searchVal2 = searchFilter2.getValue();                            
                            searchFilter2.fireEvent('change', searchFilter2, searchVal2, "junk", null);
                        }
                    }
                }
            },
            {
                xtype: 'button',
                text: 'Clear',
                width: 60,
                style: { backgroundColor: "FloralWhite", fontSize: "medium" },
                margin: '1 8 1 4',
                handler: function(but) {
                    var bbar = but.ownerCt;
                    var grid = bbar.ownerCt;
                    
                    var searchFilter1 = bbar.items.items[1];
                    searchFilter1.setValue("");

                    var searchFilter2 = bbar.items.items[3];
                    searchFilter2.setValue("");
                    
                    grid.store.clearFilter();
                }
            }        
        ],
        
        loadMask: true,
        
        clear:function()
        {
        	var self=this;
        	self.getStore().removeAll();
        },
        
        // MFM JIRA-68 Local Filtering
        // showState    - true or false to show or hide the local filter controls
        showFilter: function(showState) {
            var self=this;    
            var bbar = self.dockedItems.items[2];   // bbar in the gridPanel will always be null
                                                    // this is actually a docked toolbar
            if (bbar) {
                if (showState) {
                    bbar.show();
                }
                else {
                    bbar.hide();
                }
            }
        },
        
        // Custom filter for the search results grid
        // Mult-column multi-attribute search
        //
        // item         - a grid.store.data.item
        // filterVal    - the user-entered search string
        // csFlag       - true = case sensitive, else case INsensitive match
        gridFilter: function (item, filterVal, csFlag) {                        
            var sdate = item.get('date').toString();
            var sfrom = item.get('senderValue');
            var sto = item.get('receiverValue');
            var samt = item.get('amount').toString(); 
            var dateMatch, fromMatch, toMatch, amtMatch;
            
            if (csFlag == null || csFlag == false) {   // Case INsensitive
                 // Simple solution for now, may not work with all unicode characters (foreign names)
                 // Fix later to do a case insensitive regex
                 var upSearchVal = filterVal.toUpperCase();
                 
                 dateMatch = sdate.indexOf(upSearchVal) > -1;
                 fromMatch = sfrom.toUpperCase().indexOf(upSearchVal) > -1;
                 toMatch = sto.toUpperCase().indexOf(upSearchVal) > -1;
                 amtMatch = samt.indexOf(upSearchVal) > -1;
                 if (dateMatch || fromMatch || toMatch || amtMatch) { // quick bypass
                        return true;
                 }
                 return (false);
            }
            else {  // Case sensitive                 
                 dateMatch = sdate.indexOf(filterVal) > -1;
                 fromMatch = sfrom.indexOf(filterVal) > -1;
                 toMatch = sto.indexOf(filterVal) > -1;
                 amtMatch = samt.indexOf(filterVal) > -1;
                 if (dateMatch || fromMatch || toMatch || amtMatch) { // quick bypass
                        return true;
                 }
                return (false);
            }                        
        },
        
        // Custom filter for the search results grid
        // Logical AND of two filter fields
        //
        // item         - a grid.store.data.item
        // filterVal1    - the user-entered search string
        // filterVal2    - the user-entered search string
        // csFlag       - true = case sensitive, else case INsensitive match
        gridFilterAND: function (item, filterVal1, filterVal2, csFlag) {
            var grid = this;            
            var test1 = grid.gridFilter(item, filterVal1, csFlag);
            var test2 = grid.gridFilter(item, filterVal2, csFlag);
            return (test1 && test2);
                 
        },
        
        // returns true if the test and data positions are 'close to' one another - 1 dimensional
        positionCloseTo: function(testpos, datapos, slop) {
            var band = (slop && slop > 0) ? slop : 4;
            var dmore = datapos + band;
            var dless = datapos - band;
            if (dless <= testpos && testpos <= dmore) {
                return true;
            }
            return false;
        },
        
        highlightMatchingRow: function(selDataItem) {
            // match on source, target, and time
            var self = this;
            
            //DEBUG
            //console.log('highlightMatchingRow');
            
            //var recs = self.getView().getRecords(); //self.getSelectionModel().getRowsVisible();
            var sm = self.getSelectionModel();
            var ds = self.getStore();
            var recs = ds.data.items;
            var numRecs = recs.length;
            var rowData = null;
            var rowTime = 0;
            var selTime = 0;
            
            for (var i = 0; i < numRecs; ++i) {
                rowData = recs[i].data;
                rowTime = recs[i].raw.dateMilliSeconds / 1000; //(new Date(rowData.date)).getTime() / 1000;
                selTime = (selDataItem.time / 1000); // - 14400;
                if (rowData.senderId.toString() == selDataItem.source &&
                    rowData.receiverId.toString() == selDataItem.target && 
                    rowTime == selTime ) {
                    
                        // DEBUG
                        //console.log("rowTime = " + rowTime + ", selTime = " + selTime);
                    
                        sm.select(i);   // this selects the matching row in the grid
                        // and as a nice side effect also highlights the arrow in the chart
                }
            }
        },
        
        // MFM
        // This function is called from the interaction chart svg document
        // when the user clicks on the interaction chart.
        // gridId   - Id of the Transfers Grid instance containing data for the chart.
        // x,y      - Mouse on click position within the chart
        highlightRowForChartPos: function(gridId, x, y) {
            // DEBUG
            //console.log("Grid: highlightRowForChartPos gridid =" + gridId + ", x =" + x + ", y=" + y);

            var self = this;
            var transfersLedger = self.ownerCt;
            var ichart = transfersLedger.getCharts().getInteractionChart();
            
            var chartData = ichart.getData();
            if (chartData) {
                var datalen = chartData.length;
                var dataItem;
                var selectedItems = [];
                
                for (var i = 0; i < datalen; ++i) {
                    dataItem = chartData[i];
                    
                    // we just need to check the x (time) position
                    if (self.positionCloseTo(x, dataItem.x, 4)) {
                        selectedItems.push(dataItem);
                    }
                }
                if (selectedItems.length > 0) {
                    
                    // DEBUG
                    //console.log("Grid: highlightRowForChartPos selected items count =" + selectedItems.length);
            
                    // TODO highlight the corresponding row(s) in the grid
                    for (i = 0; i < selectedItems.length; ++i) {
                        dataItem = selectedItems[i];
                        self.highlightMatchingRow(dataItem);
                    }
                    
                }
            }
        },
                
        // MFM
        appendTabTitle: function(appendText) {
            var p = this;
            if (appendText && appendText.length > 0) {
                var title = p.up('panel').originalTitle;
                p.up('panel').setTitle(title + " " + appendText);
            }
        },
        
        listeners: {
            
            select:function(rm, record, index, opts) {  // MFM
                // MFM Highlight the corresponding item in the interaction chart 
                
                // DEBUG
                //    console.log("Grid: select ");
                    
                    
                var self = this;
                var transfersLedger = self.ownerCt;
                if (transfersLedger) {
                    var ichart = transfersLedger.getCharts().getInteractionChart();
                    //OLD var ichart = getTransfersFrame().getCharts().getInteractionChart();
                    if (ichart) {
                        ichart.highlightMatchingPlot("Walker", record.data, "cyan"); 
                    } 
                }
            }, // select
            // row right-click handler
            itemcontextmenu: function(view, record, item, index, e, eOpts) {
    			
				var window = Ext.create("DARPA.DetailsViewer", {
					timeStamp: record.data.date
				});
				
				window.show();
				window.setTo(record.data.receiverValue);
				window.setFrom(record.data.senderValue);
				window.setSubject(record.data.subject);
				window.setBody(record.data.comments);
			},
/*        
OLD TODO
       		select:function(rm, record, index, opts) {
       		
        		// record.raw contains the backing data for the search.
	       		if (record.raw.account == Ext.getCmp("account").getValue()) // trying to pivot to same account
	       			return;
	       		getTransfersFrame().getSearch().setAccount(record.raw.account);        		
	       		Ext.getCmp("minAmount").setValue("");	        		
	       		Ext.getCmp("maxAmount").setValue("");
	       		getCurrentLedger().getGrid().store.removeAll();
			doSearch();        		
                  } // select
*/      
            // If results not very large use local sorting else use server-side sorting.
            // When the store's remoteSort == false (local sorting) the column header sorting menu items will be disabled.
            // We force them to always be enabled for both remote and local sorting.
            viewready: function(grid, opts) {
                var gridcol;
                // Enable the sorting menu items in the column headers. simple hack
                for (var i = 0; i < grid.columns.length; i++) {
                    gridcol = grid.columns[i];
                    if (gridcol.sortable == false) {
                        gridcol.sortable = true;
                    }
                }
            }
        }
        
        
//        Ext.selection.RowModel this, Ext.data.Model record, Number index, Object eOpts )
    });

function makeTransferStore(grid)
{
   var store = Ext.create('Ext.data.JsonStore', {
        autoDestroy: true,
        model: 'GTransaction',
        buffered: true, // keep more data in the store than are currently being displayed in the grid
        leadingBufferZone: 300,
        //OLD remoteSort:true,
        remoteSort: false,  // If results not very large use local sorting else use server-side sorting
                            // The default is local sorting but this may change based on the resultCount of the retreived data.
        pageSize: 2000, // number of rows constituting a page. So if we call loadPage(0) will get the first 2000 etc
        autoLoad: false, // don't call load method immediately after creation
        prevTotalRecs: 0,   // for preventing transfers graph recreate each time datachanged
	proxy: 
        {
            type: 'rest',
            url: Config.detailUrl, // changed later where needed    
                   
            timeout: 60000,
            reader: {
                type: 'json',
                root: 'rows',
                totalProperty: 'resultCount'                
            }, // totalProperty is the total number of results matching the search, not the number last downloaded
            extraParams: {
            	fromdt: '',
            	todt:   '',
            	accountNumber: '',
            	minAmount:'',
            	maxAmount:'',
            	comments:'',
            	unit:''
            },
            sortParam:'sortColumn',
	    encodeSorters: function(sorters) {
	      	var sortstr=sorters[0].property;
	       	if (sorters[0].direction == 'DESC')
	    		sortstr = sortstr + '$';
               	return sortstr;
            }


        }, // proxy
        
        listeners: {
       		datachanged: function(str,b) {
       			var grid = str.grid;
       			var frame = grid.up();
       			utils.setBlink(frame, false);
       			var hasData = str.data.length > 0;
       			utils.setHasData(frame, hasData);
    			var self = this; // the store. We need to find the grid.
    			var acno=self.proxy.extraParams.accountNumber;		
    			var ledger = frame; 
    			var d = [];
// This is called as you scroll through the grid. We need to load the charts with all the data read though, so we go to the 
// reader, not the self.data.
// Note that if there were more than 10K records though we still don't have them all.
// Also note that we will be refreshing the charts with the same data as they scroll. Need to find a way to prevent this.

    			var records=self.proxy.reader.rawData.rows;    			
    			for (var i = 0;i < records.length; ++i) {    			
                            var x = records[i];
                            d.push(
                                    {
                                            source:""+x.data.senderValue,
                                            target:""+x.data.receiverValue,
                                            subject:"" + x.subject,
                                            time:x.dateMilliSeconds
                                    }
                            );
			}
			
			frame.getCharts().setData(d, acno);
                        // MFM
                        frame.getCharts().getInteractionChart().setGridRef(grid.getId());
                        
//                        frame.getTimeline().updateAccount(acno);
                        
                        
                        // MFM update tab title with total record count  
                        var totalRecs = (self.proxy.reader.jsonData.resultCount) ? self.proxy.reader.jsonData.resultCount : records.length;
                        grid.appendTabTitle("(" + totalRecs.toString() + ")");
                        
                        // for preventing transfers graph recreate each time datachanged
                        if (self.prevTotalRecs != totalRecs) {
                            frame.up().loadTransactionGraph(acno, hasData); // The separate graph tab
                            self.prevTotalRecs = totalRecs;
                        }
                        
                        /* MFM Experimantal - this does not work yet
                        var curtitle = grid.title;
                        var view = grid.getView();
                      
                        // DEBUG
                        console.log("grid view");
                        //view.panel.header.setTitle(curtitle + " - " + records.length.toString() + " records");
                        view.panel.header.title = curtitle + " - " + records.length.toString() + " records";
                        grid.doComponentLayout();
                        **/
                        
    			ledger.setStatus(hasData ? "OK" : "No transfers found");
                        
                        // If results not very large use local sorting else use server-side sorting
                        // The default is local sorting but this may change based on the resultCount of the retreived data.
                        if (totalRecs > self.pageSize) {
                            self.remoteSort = true;
                        }
                        else {
                            self.remoteSort = false;
                        }
                        
    			return true;
	        }
        } // listeners
     });
     
     store.grid = grid; // TODO: kind of ugly to add custom settings - really should extend.

     return store;

}
