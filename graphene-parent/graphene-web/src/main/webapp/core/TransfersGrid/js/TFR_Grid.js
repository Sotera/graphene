
Ext.define('GTransaction', {
    extend: 'Ext.data.Model',
    fields: 
    [
	    {name: 'date'}, 
	    {name: 'comments'}, 
	    {name: 'debit'},
	    {name:'amount', convert:function(value,record)
	    {
	    	// The table is messed up. Where the transactions are arranged with sender and receiver, the 
	    	// concepts of debit and credit are ambiguous.
	    	return record.get('debit') + record.get('credit');
	    
	    }
	    },

	    {name: 'credit'},
	    {name: 'senderId'},
	    {name: 'receiverId'},	    
	    {name: 'unit'},
	    {name: 'id'},
    	    {name: 'localBalance'},

    	    {
    	    	name: 'amount',
    	     	convert:function(value,record) { return record.get('debit') == 0 ? record.get('credit') : record.get('debit'); }
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
		{header: 'Date',        width:80,  dataIndex: 'date', sortable:true}, 					// 0
//		{header: 'Sender Account', width:140, dataIndex: 'senderId', tdCls:'account', sortable:true},// 1	
		{header: 'Loan ID', width:140, dataIndex: 'senderId', tdCls:'account', sortable:true},// 1			
//		{header: 'Receiver Account', width:140, dataIndex: 'receiverId', 	tdCls:'account', sortable:true},// 1				
		{header: 'Comments', flex:1,    dataIndex: 'comments', sortable:true},				// 2
//		{header: 'Unit',       width:50,  dataIndex: 'unit',   align:'left', sortable:true},		// 3
//		{header: 'Sent ',   	width:100, dataIndex: 'debit',      align:'right', sortable:true},		// 4
//		{header: 'Received',  	width:100, dataIndex: 'credit',     align:'right', sortable:true},		// 5
		{header: 'Amount ',   	width:100, dataIndex: 'amount',      align:'right', sortable:true,
                  // Custom sort for this amount column. 
                  // The raw values have the format: "ddd,ddd.00" with no leading unit indicator
                  doSort: function(state) {
                      var ds = this.up('grid').getStore();
                      var field = this.getSortParam();
                      ds.sort( {
                          property: field,
                          direction: state,
                          sorterFn: function(v1, v2) {
                              var amt1 = v1.get(field);
                              amt1 = amt1.replace(",","");
                              if (amt1 == null || amt1 == "")
                                  amt1 = "0.0";
                              
                              var famt1 = parseFloat(amt1);
                              
                              var amt2 = v2.get(field);
                              amt2 = amt2.replace(",","");
                              if (amt2 == null || amt2 == "")
                                  amt2 = "0.0";
                              
                              var famt2 = parseFloat(amt2);
                                
                              return (famt1 > famt2) ? 1 : (famt1 < famt2 ? -1 : 0);
                          }
                      });
                  }
                },		// 4		
		{header: 'Transaction', width:80,  dataIndex: 'id',         align:'left',  hidden:true} // will use for match search
	],
        
        loadMask: true,
        
        clear:function()
        {
        	var self=this;
        	self.getStore().removeAll();
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
                        ichart.highlightMatchingPlot("Default", record.data, "cyan"); 
                    } 
                }
            }, // select
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
                                            source:""+x.senderId,
                                            target:""+x.receiverId,
                                            time:x.dateMilliSeconds
                                    }
                            );
			}
			
			frame.getCharts().setData(d, acno);
                        // MFM
                        frame.getCharts().getInteractionChart().setGridRef(grid.getId());
                        
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
