

// ************* Unused now ***************



Ext.define("DARPA.TransfersFrame", 
{
	extend:'Ext.panel.Panel',
    	border: false,
    	layout:{
    		type:'vbox',
    		align:'stretch'
		},
		
    	items:[],
        constructor: function(config) {
        
		this.items = [
			Ext.create("DARPA.TransfersSearch",     {id:'TransfersSearch', height:'auto', hidden:false}),
			Ext.create("DARPA.TransfersLedger",     {id:'TransfersLedger', flex:1, hidden:false}),
			Ext.create("DARPA.TransfersChartFrame", {graphId:'TransfersChart', height:'auto', hidden:false, resizable: true})
		];
        	this.callParent(arguments);

        },
        getSearch:function()
        {
        	var self=this;
        	return self.items.items[0];
        
        },
        getLedger:function()
        {
        	var self=this;
        	return self.items.items[1];
        },
        getCharts:function()
        {
        	var self=this;
        	return self.items.items[2];
        },
        loadLedger:function(ac)
        {
        	var self=this;
        	self.getSearch().setAccount(ac);
        	self.getLedger().getAndShowTransactions(ac);
        },
	doSearch:function(idValue, idType, searchType, caseSensitive, fromGlobal)
	{
		var self=this;
		var srch = self.getSearch();
		var grid = self.getLedger().getGrid();
		grid.clear();
		
    		if (fromGlobal) {
			if (idType != 'account') {
				utils.setBlink(self, false);		
				return;
			}
    			srch.clear();
    			srch.setAccount(idValue);
		}
		doTransfersSearch('display');
	},
	haveData:function(data)
	{
		var self=this;
		var ac=self.getSearch().getAccount();
		self.getCharts().setData(data, ac);
	}
        
        

});

    function doTransfersSearch(type)
    {
    	var frame=getTransfersFrame();
	var ac = frame.getSearch().getAccount();
	
	var ledger = frame.getLedger();
		
	setStatus("LOADING TRANSACTIONS - PLEASE WAIT");

	if (type == 'csv') {
		var csvurl  = Config.csvUrl + '?accountList=' + ac + makeTransfersParams();
		window.open(csvurl);
	}
	else if (type == 'xls') {
		var xlsurl  = Config.xlsUrl + '?accountList=' + ac + makeTransfersParams();
		window.open(xlsurl);
	}
	
	else if (ac != "") { 					// they entered an account number
		ledger.getAndShowTransactions(ac);

//		ledger.getHeader().loadHeader(ac); 		// will show trans when header received
	}
	else { // They didn't specify an account
		ledger.getHeader().hide();
//		Ext.getCmp('customerData').collapse();		
		ledger.getAndShowTransactions(ac);
		ledger.getGrid().setTitle("Showing transactions from multiple accounts");
	}
	
    }
    
