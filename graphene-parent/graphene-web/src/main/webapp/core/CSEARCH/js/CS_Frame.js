   
   Ext.define("DARPA.CSFrame",  {
    	extend:'Ext.panel.Panel',
    	border: false,
    	xtype: 'container',
    	layout:{
    		type:'vbox',
    		align:'center',
    		pack:'center'
	},
	items:[
		Ext.create("DARPA.CSSearch", {width:'auto'}),
        ],
        getSearch: function()
        {
        	var self=this;
        	return self.items.items[0];
        }
   });
    
    

 