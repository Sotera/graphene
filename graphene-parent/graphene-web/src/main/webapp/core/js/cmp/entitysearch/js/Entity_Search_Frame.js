   
   Ext.define("DARPA.ESFrame",  {
    	extend:'Ext.panel.Panel',
    	border: false,
    	margin:20,
    	xtype: 'fieldSet',
    	layout:{
    		type:'vbox',
    		align:'stretch'
//    		,
//    		pack:'center'
	},
	items:[],
	constructor:function(config) {
		this.items = [
			Ext.create("DARPA.EntitySearch", {height:'auto', width:800}),
			Ext.create("DARPA.EntityGrid", {flex:1, width:800,margin:'10 0 0 0'})
		];
		this.callParent(arguments);
		
	},
        getSearch: function()
        {
        	var self=this;
        	return self.items.items[0];
        },
        getEntityGrid: function() 
        {
        	var self=this;
        	return self.items.items[1];
        }
        
   });
    
    

 