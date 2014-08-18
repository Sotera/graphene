Ext.define('CustomerNameModel', {
    extend: 'Ext.data.Model',
    fields: 
    [   'name',
    	{name: 'score',type:'int',
    	 name: 'index', type:'int'
    	 }
        
    ]
	
});

   var customerNameStore = Ext.create("Ext.data.JsonStore", {
       		model:'CustomerNameModel',
       		proxy: {
   			type: 'rest',
   			url:  Config.customerNamesUrl,	
   			reader: {
   	                	type: 'json',
   	               		root:'results'   	                	
   	               	},

   	               	extraParams: {
	       			searchType:'simple',
	       			caseSensitive:false,
	       			idType:'name',
	       			limit:2000
   	               	}
       		},
	        listeners: {
	       		datachanged: function(a,b) {
	       			Ext.getCmp('customerNameLB').expand();
	    			return true;
		        }
        } // listeners
   });
   
   var customerNamePanel = {
	title:'Customers',
	flex:1,
	items:[
		{
			xtype:'combo',
			id:'customerNameLB',
			displayField: 'name',
			store:customerNameStore,
			listeners: {
				'select': function(a) {
					Ext.getCmp("customerGrid").loadCustomer(a.getValue());
				}
			}
      				
		}
       			]
   };
    		