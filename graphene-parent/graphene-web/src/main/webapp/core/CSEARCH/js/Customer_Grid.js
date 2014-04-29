
Ext.define('CustomerModel', {
    extend: 'Ext.data.Model',
    fields: 
    [
	    {name: 'customerNumber'}, 
	    {name: 'customerName'}, 
	    {name: 'address'},
	    {name: 'accountSet', type:'auto'}
	]
	
});


Ext.define("DARPA.CustomerGrid",  {

    extend:'Ext.grid.Panel',

    	flex:1,
    	title:'Records Matching Selected Name',
    	titleAlign:'center',
        verticalScroller: {
        	leadingBufferZone:500, 		// nbr of rows prior to the grid display to keep 
        	trailingBufferZone:1000		// nbr of rows after the grid display to keep
       	},
       
        verticalScrollerType:'paginggridscroller',

        viewConfig: {
        	invalidateScrollerOnRefresh:true,
        	loadMask:false,
                enableTextSelection: true   // MFM added so that can copy the values
       	},
       	store:makeCustomerStore(),
        
        align:'left',
	columns: [
		{header: 'Customer Nbr',        width:80,  dataIndex: 'customerNumber'}, 					// 0
		{header: 'Name', width:300, dataIndex: 'customerName'},		// 1
		{header: 'Address', flex:1,    dataIndex: 'address'},
		{header: 'Accounts', flex:1,   dataIndex: 'accountSet'},
		{
			header: 'Options', 
			width:200, 
			dataIndex:'accountSet',
			renderer:function(value,metadata,record) 
				{
					var self=this;
//					var ledgerLink = self.showLedger();
					return ("<button style='font-size:75%;' onclick=showLedger("+value+");>LEDGER</button>" +
					        "<button style='font-size:75%;' onclick=showCustomerGraph(" + record.raw.customerNumber + ");>GRAPH</button>");
				}
		}
	],
        loadMask: true,
        loadCustomer:function(customer) {
        	var self=this;
        	self.setTitle("Customer with the name " + customer);
		var cstore = self.getStore();
		cstore.getProxy().extraParams.name=customer;
		cstore.load(QSLoadComplete);
        }

/*
        listeners:{
       		select:function(rm, record, index, opts) {
        		// record.raw contains the backing data for the search.
	       		if (record.raw.account == Ext.getCmp("account").getValue()) // trying to pivot to same account
	       			return;
	       		Ext.getCmp("account").setValue(record.raw.account);        		
	       		Ext.getCmp("minAmount").setValue("");	        		
	       		Ext.getCmp("maxAmount").setValue("");
	       		Ext.getCmp("Ledgergrid").store.removeAll();
			doSearch();        		
                  } // select
        }
*/        
    });

// A store to back the customer details grid
function makeCustomerStore()
{
   var store = Ext.create('Ext.data.JsonStore', {
        autoDestroy: true,
        model: 'CustomerModel',
        buffered: true, // keep more data in the store than are currently being displayed in the grid
        leadingBufferZone: 300,
        remoteSort:true,
        pageSize: 2000, // number of rows constituting a page. So if we call loadPage(0) will get the first 2000 etc
        autoLoad: false, // don't call load method immediately after creation
	proxy: 
        {
            type: 'rest',
            url: Config.customerDetailsUrl, // changed later where needed    
                   
            timeout: 60000,
            reader: {
                type: 'json',
                root: 'results',
                totalProperty: 'resultCount'                
            }, // totalProperty is the total number of results matching the search, not the number last downloaded
            extraParams: {
            	name: ''
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
       		datachanged: function(a,b) {
//    			setStatus("OK");
	        	return true;
	        }
        } // listeners
     });
     
     return store;

}
