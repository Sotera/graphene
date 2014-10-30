
// A custom panel containing The customer and accound details for the ledger header

// Usage: 	var header = Ext.create('Ext.custom.headerPanel');
//		header.setUrl(url); // where url is the REST service call to get the header from account details
// 
// This is encapsulated as a class but note the global call to an external function getAndShowTransactions which needs to be
// encapsulated in a transactions or grid object that has a reference here.


Ext.define('HeaderModel', {
    extend: 'Ext.data.Model',
    fields: 
       [
	    {
		name: 'customerName'
	    }, 
	    {
		name: 'address'
	    }, 
	    {
		name: 'customerNumber'
	    },
	    {
		name: 'accountNumber'
	    },
	    {
	    	name: 'email'
      	    },
	    {
	    	name: 'unit'
      	    },
	    {
	    	name: 'accountType'
      	    },
      	    {
      	    	name:'parentAccount'
	    }
      	    
	]
});
  

Ext.define("DARPA.customerDetails", {
	extend:'Ext.Container',
	border:true,
	height:130,
	width:325,
	
	constructor:function(config) {
		config.items =[
		   {
		      xtype:'displayfield', // customerName
		      fieldLabel:'Customer Name'
		   },
		   {
		      xtype:'displayfield', // customerAddress
		      fieldLabel:'Customer Address'
		   }
		];
       		this.callParent(arguments);
	},
	update:function(header) {
		this.items.items[0].setValue(header.customerName);
		this.items.items[1].setValue(header.address);        	
	}


}), // define
    
    
Ext.define("DARPA.accountDetails", {
	extend:'Ext.Container',
      
	border:5,
        width:325,
        height:130,
        align:'left',
        autoScroll:true,
        constructor:function(config) {
        	
		config.items = [

			{
				xtype:'displayfield',
				fieldLabel: 'Customer ID',
				width:200
			},
			{
				xtype:'displayfield',
				fieldLabel: 'Email Address',
				width:200
			},
			{
				xtype:'displayfield',
				fieldLabel: 'Account Type',
				width:200	      

			},
			{
				xtype:'displayfield',
				fieldLabel: 'Account Nbr',
				width:200

			}

		    ];
       		this.callParent(arguments);
            		
            
            },
            update:function(header, acno)
            {
            	this.items.items[0].setValue(header.customerNumber);
            	this.items.items[1].setValue(header.email);
            	this.items.items[2].setValue(header.accountType);
            	this.items.items[3].setValue(acno);
            
            }
    
});
    
Ext.define("DARPA.headerPanel",{

	extend:'Ext.Container',
    	hidden:true,
    	headerStore:Ext.create('Ext.data.Store', {
    		model:'HeaderModel',
    		proxy: {
			type: 'rest',
	//		url:  Config.headerUrl,		
			reader: {
	                	type: 'json'
	               	}
    		}
    	}),
    	
    	constructor:function(config) {
		config.items = 
		[
			{
				xtype: 'fieldset',
				layout:{
				   type:'hbox'
				},
				width:800,
				height:'auto',


				items:[
					Ext.create("DARPA.customerDetails", { }),
					Ext.create("Ext.panel.Panel", {border:false, flex: 1}), // spacer
					Ext.create("DARPA.accountDetails", { })
				]
			}		

		];
		this.callParent(arguments);
	},
	
    	setUrl: function(u)
    	{
    		this.url = u;
    	},
    	loadHeader:function(acno)
	{
		this.headerStore.getProxy().url=this.url + '/' + acno;
		this.headerStore.load( {
			callback: function(account, operation, success) 
			{
                            // MFM handle no results
                            // PWG this is not what this means. It means no *header* details not no *transactions*.TODO: fix this fix
                            if (account && account.length > 0) {
				var header = account[0].data;
				var fd = this.items.items[0];
				var cd = fd.items.items[0];
				var ac = fd.items.items[2];
				cd.update(header);
				ac.update(header, acno);

// Now get the transactions				
				var ledger = getTransactionFrame().getLedger(acno);
				if (header.parentAccount != null && header.parentAccount.length != 0)
					ledger.getAndShowTransactions(header.parentAccount);
				else
					ledger.getAndShowTransactions(acno);
                            }
                            else {
                                Ext.Msg.alert("Status", "No results were found for this search.");
                            }
                            
			}, // success
			scope:this
		}); // load
	  } // function

});  // define custom headerpanel
   
