
Ext.define('GTransaction', {
    extend: 'Ext.data.Model',
    fields: 
    [
	    {name: 'date'}, 
	    {name: 'comments'}, 

	    {name: 'debit', type:'string'},
	    {name: 'credit'},
    	{name: 'balance'},
	    
	    {name: 'localDebit', type:'string'},
	    {name: 'localCredit'},
    	{name: 'localBalance'},
    	    
    	{name: 'account'},
	    {name: 'unit'},
	    {name: 'id'}
	]
	
});


Ext.define("DARPA.TransactionGrid",  {

    extend:'Ext.grid.Panel',
    
    	constructor:function(config) {
    		this.store=makeStore(this); // done in constructor otherwise we get the same store in all the ledgers                
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
		{header: 'Date',        width:80,  dataIndex: 'date', sortable:true}, 					   // 0
		{header: 'Account Nbr', width:110, dataIndex: 'account',    id:'actCol', tdCls:'account', sortable:true,
                // Custom sort for this number column. 
                  doSort: function(state) {
                      var ds = this.up('grid').getStore();
                      var field = this.getSortParam();
                      ds.sort( {
                          property: field,
                          direction: state,
                          sorterFn: function(v1, v2) {
                              var id1 = v1.get(field);
                              id1 = id1.replace(",","");
                              id1 = id1.replace("-","");
                              if (id1 == null || id1 == "")
                                  id1 = "0";
                              
                              var nid1 = parseInt(id1);
                              
                              var id2 = v2.get(field);
                              id2 = id2.replace(",","");
                              id2 = id2.replace("-","");
                              if (id2 == null || id2 == "")
                                  id2 = "0";
                              
                              var nid2 = parseInt(id2);
                                
                              return (nid1 > nid2) ? 1 : (nid1 < nid2 ? -1 : 0);
                          }
                      });
                  }
                }, // 1
		{header: 'Comments', flex:1,    dataIndex: 'comments', sortable:true},			  // 2
		{header: 'Unit',       width:50,  dataIndex: 'unit',   align:'left', sortable:true},		  // 3
		{header: 'Debit ',   	width:100, dataIndex: 'debit',      align:'right', sortable:true,
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
                },		  // 4
		{header: 'Credit',  	width:100, dataIndex: 'credit',     align:'right', sortable:true,
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
                },		  // 5
		{header: 'Balance', 	width:100, dataIndex: 'balance',    align:'right', sortable:true,
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
                },		  // 6		
		
		{header: 'Loc Debit',   width:100, dataIndex: 'localDebit', align:'right', hidden:true},  // 7
		{header: 'Loc Credit',  width:100, dataIndex: 'localCredit',align:'right', hidden:true},  // 8
		{header: 'Loc Balance', width:100, dataIndex: 'localBalance',align:'right', hidden:true}, // 9		
		{header: 'Transaction', width:80,  dataIndex: 'id',         align:'left',  hidden:true} // will use for match search
	],
        loadMask: true,

        // MFM
        appendTabTitle: function(appendText) {
            var p = this;
            if (appendText && appendText.length > 0) {
                var title = p.up('panel').originalTitle;
                p.up('panel').setTitle(title + " " + appendText);
            }
        },
        
        listeners:{
/*        
TODO
       		select:function(rm, record, index, opts) {
       		
        		// record.raw contains the backing data for the search.
	       		if (record.raw.account == Ext.getCmp("account").getValue()) // trying to pivot to same account
	       			return;
	       		getTransactionFrame().getSearch().setAccount(record.raw.account);        		
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
        },
        clear:function()
        {
        	var self=this;
        	self.getStore().removeAll();
        }
        
        
//        Ext.selection.RowModel this, Ext.data.Model record, Number index, Object eOpts )
    });

function makeStore(grid)
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
        
	proxy: 
        {
            type: 'rest',
            url: Config.detailUrl, // changed later where needed    
                   
            timeout: 60000,
            reader: {
                type: 'json',
                root: Config.CXF ? 'singleSidedEvents.rows' : 'rows',
                totalProperty: Config.CXF ? 'singleSidedEvents.resultCount' :'resultCount'                
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
	      	if (sortstr=='date')
	      		sortstr="trn_dt"; // because date is a date string, not sortable
	       	if (sorters[0].direction == 'DESC')
	    		sortstr = sortstr + '$';
               	return sortstr;
            }

        }, // proxy
       
        listeners: {
       		datachanged: function(str,b) {
       			var grid = str.grid;
                        var self = this; // the store.
       			var frame = grid.up();
       			utils.setBlink(frame, false);
       			var hasData = str.data.length > 0;
       			utils.setHasData(frame, hasData);
    			var acno=str.proxy.extraParams.accountNumber;    			    			
//    			var ledger = frame.getLedger(acno); 
    			var ledger = frame;     			
			ledger.getTimeline().updateAccount(acno); // todo: make this relative
    			var s = str.proxy.reader.jsonData;
    			if (s.multiUnit) {
    				grid.columns[6].hide(); // hide the unit balance column
                        }
                        
                        // MFM update tab title with the total record count  
                        var totalRecs = (s.resultCount) ? s.resultCount : self.proxy.reader.rawData.rows.length;
                        grid.appendTabTitle("(" + totalRecs.toString() + ")");

    			ledger.setStatus(hasData ? "OK" : "No transactions found");
                        
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
