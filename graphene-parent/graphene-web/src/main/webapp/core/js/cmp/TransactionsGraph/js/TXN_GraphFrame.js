
Ext.define("DARPA.TransactionGraphFrame",  {
	extend:'Ext.panel.Panel',
	border: false,
	width:'auto',
	xtype: 'container',
	title:'TRANSACTION GRAPH',
	layout:{
		type:'hbox',
		align:'stretch'
	},
	items:[],
	
	constructor:function(config)
	{
	
		this.items = 
			[
				Ext.create("DARPA.TransactionGraph",
				{
					flex:1,
					id: config.id + '-Graph', 
					hidden:false
				})
		];

		this.callParent(arguments);
	},
	
	getGraph:function()
	{
		var self=this;
		return self.items.items[0];
	},
	setHasData:function(has)
	{
		var self=this;
		utils.setHasData(self, has);
	},
        
	// following exists for each frame and is called by central search (fromGlobal means from csearch)
	doSearch:function(accountList) 
	{
		getGraph().load(accountList);            
	},
	
	clear:function()
	{
		var self=this;
		self.getGraph().clear();
		utils.setHasData(self, false);
	
	}

});
