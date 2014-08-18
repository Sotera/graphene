// UNUSED FOR NOW as the Ledger is added to the Entity tab
Ext.define("DARPA.TransactionFrame", 
{
	extend:'Ext.panel.Panel',
    	border: false,

    	layout:{
    		type:'vbox',
    		align:'stretch'
		},
    	items:[],
        constructor:function(config)
        {
        	this.items = [
	    		Ext.create("DARPA.TransactionSearch", {id:config.id+-'TransactionSearch', height:'auto'}),
			Ext.create("DARPA.Ledger", {id:config.id+'-TransactionLedger', flex:1})
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
		
    		if (fromGlobal) {
    			srch.clear();
    			self.getLedger().getGrid().clear();
			if (idType == 'account')
	    			srch.setAccount(idValue);
	    		else {
	       			utils.setBlink(self, false);
	       			utils.setHasData(self, false);
	    			return;
    			}
		}
		doTransactionSearch('display');
	}
        
        

});
    function doTransactionSearch(type)
    {
    	var frame=getTransactionFrame();
	var ac = frame.getSearch().getAccount();
	
	var ledger = frame.getLedger();
		
	setStatus("LOADING - PLEASE WAIT");

	if (type == 'csv') {
		var csvurl  = Config.csvUrl + '?accountList=' + ac + makeTransactionParams();
		window.open(csvurl);
	}
	else if (type == 'xls') {
		var xlsurl  = Config.xlsUrl + '?accountList=' + ac + makeTransactionParams();
		window.open(xlsurl);
	}
	
	else if (ac != "") { 					// they entered an account number
		ledger.getGrid().clear();
		ledger.getHeader().loadHeader(ac); 		// will show trans when header received
	}
	else { // They didn't specify an account
		ledger.getHeader().hide();
//		Ext.getCmp('customerData').collapse();		
		ledger.getAndShowTransactions(ac);
		ledger.getGrid().setTitle("Showing transactions from multiple accounts");
	}
	
	updateTransactionBalanceCols();
	   
    }
    
