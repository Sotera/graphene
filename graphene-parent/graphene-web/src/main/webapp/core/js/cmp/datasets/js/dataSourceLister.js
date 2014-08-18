Ext.define("DARPA.Field", {
	extend:"Ext.data.Model",
	fields: [
		{name:'name', type: 'string'},
		{name:'friendlyName', type:'string'},
		{name:'type', type:'string'},
		{name:'sortable',type:'boolean'},
		{name:'searchFields',type:'auto'}
		
	]
});

Ext.define("DARPA.DataSource", {
	extend:"Ext.data.Model",
	fields:[
		{name:'id', type: 'string'},
		{name:'name', type: 'string'},		
		{name:'friendlyName', type:'string'},
		{name:'dataSets', type:'auto'}
	],
	idProperty:'id'

});

var global_current_datasource = {};
var global_all_datasources = {};

var entityDataSourceStore = Ext.create("Ext.data.Store", {
       		model:'DARPA.DataSource',
       		autoLoad:false
});

var transactionDataSourceStore = Ext.create("Ext.data.Store", {
       		model:'DARPA.DataSource',
       		autoLoad:false
});


var globalSourceStore = Ext.create("Ext.data.Store", {
       		model:'DARPA.DataSource',
       		proxy: {
       			type:'ajax',
   			url:  Config.dataSourceUrl,	
   			reader: {
   	                	type: 'json',
   	               		root:'dataSources'   	                	
   	               	}
       		}
   });
   
globalSourceStore.load( {
	scope:this,
	callback:function(records, operations, success) {
		var entityRecs=[];
		var transRecs = [];
		for (var i = 0; i < records.length; ++i) {
			var entity = false;
			var transaction = false;
			var source = records[i];
			for (var j = 0; j <source.data.dataSets.length; ++j) {
				if (source.data.dataSets[j].isEntity)
					entity = true;
				if (source.data.dataSets[j].isTransaction)
					transaction = true;
			}
			if (entity)
				entityRecs.push(source);
			if (transaction)
				transRecs.push(source);
		}
		entityDataSourceStore.loadRecords(entityRecs);
		transactionDataSourceStore.loadRecords(transRecs);

	}
   });


Ext.define("DARPA.DataSourcePicker", {
      	extend: 'Ext.Container',
    	layout:{
    		type:'hbox',
    		align:'stretch'
    	},
    	border:true,
    	items:[],
    	queryMode:'local',
    	isValid:function() {
    		var self=this;
    		return self.items.items[1].isValid()
    	},
    	
    	constructor:function()
    	{
    		var label = Ext.create("Ext.form.Display", {value:'Data Source:', padding:'0 10 0 0'});
    		var cb = Ext.create('Ext.form.field.ComboBox', {
				displayField:'friendlyName',
				valueField:'id',
//				fieldLabel:'Data Source',
				emptyText:'Choose a Source',
				allowBlank:false,
				allowOnlyBlankSpace:false,
				store:entityDataSourceStore,
				queryMode:'local',
				listeners:{
					'select':function(a){
						var selValue = a.getValue();
						console.log("Selected " + selValue);
						global_current_datasource=globalSourceStore.getById(selValue);
						console.log("Current Data Source " + global_current_datasource.data.name);
						getESFrame().getSearch().configureByDataSource(global_current_datasource);
					}
				}
			});
    		
    		// display first item in the store
			global_current_datasource = cb.getStore().getAt(0); 
			cb.select(global_current_datasource);
    		
			label.setVisible(false);
			cb.setVisible(false);
			
    		this.items=[label, cb]; // items
    		this.callParent(arguments);
    	}
	}
);



		
	
		