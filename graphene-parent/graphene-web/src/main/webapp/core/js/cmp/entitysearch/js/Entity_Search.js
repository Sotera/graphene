// Entity_Search.js
// Advanced Search form
// PWG

// following fixes an Extjs bug where a combobox in a grid cell shows the value instead of the displayfield
Ext.util.Format.comboRenderer=function(combo) {
	return function(value) {
            var record = combo.findRecordByValue(value);
            if (record) {   // MFM bug fix
		return record.data.friendlyName;
            }
            return "";
	}
};

var fieldNameStore = Ext.create("Ext.data.ArrayStore", 
{
	// data is hard-coded to represent fields in by the dataset until
	// store can be properly populated after the fact
	fields:['name','friendlyName'],
	data: [
		['name', 'Name'],
		['email', 'Email Address']
	]
});

var operatorStore = Ext.create("Ext.data.ArrayStore", 
{
	fields:['name','friendlyName'],
	data: [
		['include', 'Contains'],
		['equals', 'Equals'],
		['exclude', 'Does not contain'],
// TODO: less than or greater than when we include dates and amounts		
		['soundslike', 'Sounds Like'],
		['regex', 'Regular Expression']
	]
});


Ext.define("DARPA.EntityQueryRow", {

	extend:'Ext.data.Model',
	fields: [
		{name:'fieldName', type:'string'},
		{name:'operator', type:'string'},
		{name:'value', type:'string'},
		{name:'caseSensitive', type:'bool'}
	]
});

Ext.define("DARPA.EntitySearchStore", {

	extend:'Ext.data.ArrayStore',
	model:'DARPA.EntityQueryRow'
});


Ext.define("DARPA.EntitySearch",

{
   	extend: "Ext.form.Panel",   
    	align:'center',
    	title: 'ENTITY SEARCH',
    	titleAlign:'center',
    	labelAlign:'left',
    	bodyStyle:'background-color:lightyellow',
    	border:1,
        layout:{
    	 	type:'vbox',
    	 	align:'stretch'
        },
        items:[],
  	constructor:function() 
	{
		var loadbutton = Ext.create("Ext.Container", {
			layout:	{
				type:'hbox'
//				align:'center',
			},
			margin:10,
			items:[
				{xtype:'component',flex:1},
				{
					xtype:'button',
					text:'FIND ENTITIES',
					height:20,
					width:90,
					listeners: {
						click: function(a,b)
						{
							getESFrame().getEntityGrid().getStore().removeAll();
                            getESFrame().getSearch().run = true;
							getESFrame().getSearch().doSearch();
						}
					} // listeners
				},
				{xtype:'component',flex:1}
			]

		});// load button
		var datasetbox = Ext.create('DARPA.DataSourcePicker',{
				height:40, 
				maxWidth:300,
				margin:2
				
		});
		var gridAndLoad = Ext.create("Ext.Container", {
		
			layout:	{
				type:'hbox',
				align:'stretch'
			},
			items:
			[
				makeSearchGrid(),
				loadbutton
			]
		});
		this.items = [
			datasetbox,
			gridAndLoad

		 ]; // items for main search box
		 
		 this.callParent(arguments);
                 
                 // JIRA-17 when user hits Enter to search
                 this.run = false;
	 },
         getDataSetControl:function()
         {
		 var self=this;
		 return self.items.items[0];
         },
         getSearchGrid:function()
         {
		 var self=this;
		 return self.items.items[1].items.items[0];
         },
         configureByDataSource:function(ds)
         {
         	var self=this;
         	var grid = self.getSearchGrid();
         	var s;
         	var data = ds.data;
         	var newdata = new Array();
         	
         	for (var i = 0; i < data.dataSets.length;++i) {
         		s = data.dataSets[i];
         		if (s.isEntity) {
         			var sf = s.fields;
         			for (var f = 0; f < sf.length;++f) {
	         			var entry= [sf[f].name, 
	         				    sf[f].friendlyName
	         			]
	         			newdata.push(entry);
         			}
         		}
       		}
       		fieldNameStore.loadData(newdata);
//		grid.columns[0].fieldName.getStore().loadData(newdata);
//		grid.columns[0].fieldName.store=Ext.create('Ext.data.ArrayStore', {
//			fields: ['name', 'friendlyName'], 
//			data:newdata
//		});
         
         },
         
         doSearch:function()  
         {
         	// DEBUG
                //console.log("doSearch");
                
                var self = this;
         	var ds = self.getDataSetControl();
                self.run = false;  // JIRA-17 when user hits Enter to search
/*                
         	if (!ds.isValid()) {
         		Ext.MessageBox.alert("Please select a data source");
         		return;
       		}
*/       		
         	var grid=self.getSearchGrid();
         	
       		var items = grid.getStore().data.items;
       		var filters=[];
       		for (var i = 0; i < items.length; ++i) {
       			var d = items[i].data;
       			d.value = d.value.trim();
       			if (d.value.length>0)
       				filters.push(d);
		}

		global_current_datasource=globalSourceStore.getAt(0);       			
       		var source = global_current_datasource.data.id;
       		var dataset = 'Entities';
       		
       		getESFrame().getEntityGrid().getMatches(dataset, source, filters);
		
         }
         
});

         function makeSearchGrid()
         {
		var myStore = Ext.create('DARPA.EntitySearchStore', {});
		myStore.loadData([
			['name','include',' ', false],
			['name','include',' ', false],
			['name','include',' ', false],
			['name','include',' ', false],			
			['name','include',' ', false]
		]);
		
//		var cellEditing = Ext.create('Ext.grid.plugin.CellEditing', {clicksToEdit:1});

		var fieldCombo = new Ext.form.ComboBox( {
			typeAhead:true,
			triggerAction:'all',
			selectOnTab:false,
			displayField:'friendlyName',
			valueField:'name',
			store:fieldNameStore,
			lazyRender:true,
			listClass:'x-combo-list-small'
		
		});
		
		var opCombo = new Ext.form.ComboBox( {
			typeAhead:true,
			triggerAction:'all',
			selectOnTab:false,
			displayField:'friendlyName',
			valueField:'name',
			store:operatorStore,
			lazyRender:true,
			listClass:'x-combo-list-small'
		
		});
		
		var grid = Ext.create('Ext.grid.Panel', {
			store:myStore,
			height:'auto',
			flex:1,
			selType:'cellmodel',
			plugins: [
                            Ext.create('Ext.grid.plugin.CellEditing', 
                                { clicksToEdit:1,
                                  listeners: {  // JIRA-17 to handle the case when user hit Enter to initiate the search   
                                   edit: function(editor, e) {                                    
                                        //var selRecData = e.record.data;
                                        
                                        // DEBUG
                                        //console.log("cell edit");
                                    
                                        var search = getESFrame().getSearch();
                                        if (search.run) {  
                                            search.doSearch();
                                        }
                                   }
                                  }    
                                }
                            )
			],
			columns:[
			{
				header:'Field',
				width:100,
				sortable:false,
				dataIndex:'fieldName',
				menuDisabled:true,				
				field: fieldCombo,
				renderer:Ext.util.Format.comboRenderer(fieldCombo)
			},
			{
				header:'Operator',
				sortable:false,
				width:120,
				dataIndex:'operator',
				menuDisabled:true,				
				field: opCombo,
				renderer:Ext.util.Format.comboRenderer(opCombo)	
			},
			{
				header:'Value',
				sortable:false,
				flex:1,
				menuDisabled:true,				
				dataIndex:'value',
				field: {
					xtype:'textfield',  // TODO may want to use "DARPA.TextField" with definition of enterCallback
					typeAhead:true,
					triggerAction:'all',
					selectOnTab:false,
                                        // JIRA-17 to handle the case when user hit Enter to initiate the search
                                        listeners: {
                                            specialkey: function(f,e) {
                                                if (e.getKey() == e.ENTER) {
                                                    var store = getESFrame().getEntityGrid().getStore();
                                                    store.removeAll();
                                                    // f is the user selected field in the grid.
                                                    // f.value contains the field's current value
                                                    
                                                    // IDEALLY There should be a way to sync the grid view/latest cell edits with the store, but attempts to do this failed
                                                    // Case 1. User hits enter key in the row that they just entered or changed
                                                    // Case 2. User enters data in a row that is different than the row they hit Enter in
                                                    // Case 3. User changes multiple rows and then hits enter
                                                    // 
                                                    // Cannot do the search at this point because the most recent cell entry/editing is not avail here
                                                    // but it will be in Ext.grid.plugin.CellEditing edit event (see above)
                                                    getESFrame().getSearch().run = true;        
                                                }
                                            }  
                                        }
                                }
			},
			{
				sortable:false,
				header:'Case Sensitive',
				xtype:'checkcolumn',
				dataIndex:'caseSensitive',
				width:120
			}
			]  // end columns
                        
//			selModel:{selType:'cellmodel'},
//			plugins:[cellEditing]
			
		});
		
		return grid;
         }

  
   
    

