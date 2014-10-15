Ext.define("DARPA.EntityTab", {

	extend:'Ext.tab.Panel',
//	extend:'Ext.Container',
	layout: {
		type:'vbox',
		align:'stretch'
	},
	items:[],
	entity:{},
	constructor:function(config) {
/*			
		var transactions = Ext.create("DARPA.Ledger", {
			title:'TRANSACTIONS',
			id:config.id + "-Ledger",
			institution: config.institution,
			resizable: false,
			disabled:true
			
		});
*/

		if (typeof config.componentsToInclude == "undefined") {
			// if componentsToInclude is undefined in constructor, add everything by default
			this.hasDetails = true;
			this.hasTransfers = true;
			this.hasTransactionGraph = true;
			this.hasEntityGraph = true;
		} else {
			// else add only the components specified in the contructor
			this.hasDetails = config.componentsToInclude.indexOf("details") > -1;
			this.hasTransfers = config.componentsToInclude.indexOf("transfers") > -1;
			this.hasTransactionGraph = config.componentsToInclude.indexOf("transactionGraph") > -1;
			this.hasEntityGraph = config.componentsToInclude.indexOf("entityGraph") > -1;
		}

		this.items = [];
		
		if (this.hasDetails) { 
			var details = Ext.create("DARPA.EntityDetailsFrame", {
				title:'ENTITY DETAILS',
				id:config.id + '-Details',			
				entityname:config.title, 
				institution: config.institution,
				resizable: false,
				entity:config.entity
			});
			this.items.push(details); 
		}
		
		if (this.hasTransfers) { 
			var transfers = Ext.create("DARPA.TransfersLedger", {
				title:'EMAILS',
				id:config.id + '-Transfers',
				institution: config.institution,
				resizable: false,
				disabled:true
			});
			this.items.push(transfers); 
		}
		
		if (this.hasTransactionGraph) { 
			var transactionsGraph = Ext.create("DARPA.TransactionGraph", {
				title:'EMAIL GRAPH', 
				id:config.id + '-TransGraph',
				entityId: config.id, 	
				institution: config.institution,
				resizable: false,
				disabled:true			
			});
			this.items.push(transactionsGraph); 
		}
		
		if (this.hasEntityGraph) { 
			var entityGraph = Ext.create("DARPA.PBGraph", {
				title:'ENTITY GRAPH', 
				entityId: config.id, 
				id:config.id + '-EntityGraph',
				institution: config.institution,
				resizable: false
			})
			this.items.push(entityGraph); 
		}
		
		this.callParent(arguments);
		
		if (this.hasEntityGraph) {
			entityGraph.load(config.id);
			if (entityGraph.prevLoadParams) {
				entityGraph.prevLoadParams.prevNumber = config.id;
			} else {
				entityGraph.prevLoadParams = {prevNumber: config.id};
			}
		}
	},
/*	
	loadLedger:function(account) { 
		var t = this.getTransactions();
		t.getAllTransactions(account);
		t.setDisabled(false);
		this.setActiveTab(t);
	},
*/	
	loadTransfers:function(account) {
		if (!this.hasTransfers) {
			console.log("Transfer tab not available");
			return;
		}
		var t = this.getTransfers();
		t.getAllTransactions(account);
		t.setDisabled(false);		
	},
	
	loadTransactionGraph:function(accountNumber, hasData) {
		if (!this.hasTransactionGraph) {
			console.log("Transaction graph not available");
			return;
		}
		var self=this;
		var t = self.getTransactionGraph();
		t.setDisabled(!hasData);		
		if (hasData)
			t.load([accountNumber]);
	},

	getDetailsTab:function() 
	{
		// if this.hasDetails == false, this will return undefined
		return Ext.getCmp(this.id+'-Details');
	},
/*	
	getTransactions:function() 
	{
		return this.items.items[1];
	},
*/	
	getTransfers:function() 
	{
		// if this.hasTransfers == false, this will return undefined
		return Ext.getCmp(this.id+'-Transfers');
	},
	getTransactionGraph:function()
	{
		// if this.hasTransactionGraph == false, this will return undefined
		return Ext.getCmp(this.id+'-TransGraph');
	},
	getEntityGraph:function()
	{
		// if this.hasEntityGraph == false, this will return undefined
		return Ext.getCmp(this.id+'-EntityGraph');
	}

});