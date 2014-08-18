
Ext.define('DARPA.EntityModel', {
    extend: 'Ext.data.Model',
    fields: 
    [
    	{name: 'status', type: 'string'}, // unused
    	{name: 'id', type: 'string'},
    	{name: 'accountList', type:'auto'},
    	{name: 'attributes', type:'auto'},
    	{name: 'effectiveName', type:'string'} // an entity may have several. This field contains the one to use in titles etc
   ]
	
});

Ext.define("DARPA.EntityGrid",  {

    extend:'Ext.grid.Panel',

        // local filtering of search results
        tbar: [
            {
                xtype: 'label',
                text: 'Filter',
                margin: '1 4 1 4',
                style: { fontSize: "small" }
            }, 
            {
                xtype: 'textfield',
                id: 'elistSearchFilter1',
                hideLabel: true,
                caseSensitive: false,    // can be changed by the checkbox after this
                width: 120,
                listeners: {
                    change: function(self, newVal, oldVal, opts) {
                        var grid = self.ownerCt.ownerCt;
                        var filterVal = newVal.trim();  // ignore any spaces   
         
                        grid.store.clearFilter();
                        
                        // always clear the second field when this one changes
                        var searchFilter2 = Ext.getCmp('elistSearchFilter2');
                        searchFilter2.setValue("");
                        
                        if (filterVal.length > 0) {
                            var filters = [];
                            filters = new Ext.util.Filter({
                                filterFn: function (item) {
                                    // Multi column search with OR logic
                                    return grid.gridFilter(item, filterVal, self.caseSensitive);
                                }
                            });
                            grid.store.filter(filters);
                        }
                    }
                }
            },
            {
                xtype: 'label',
                text: 'AND',
                margin: '1 5 1 2',
                style: { fontSize: "small" }
            }, 
            {
                xtype: 'textfield',
                id: 'elistSearchFilter2',
                hideLabel: true,
                caseSensitive: false,    // can be changed by the checkbox after this
                width: 120,
                listeners: {
                    change: function(self, newVal, oldVal, opts) {
                        var grid = self.ownerCt.ownerCt;
                        var filterVal2 = newVal.trim();  // ignore any spaces   
                        
                        //if (filterVal2.length > 0) {
                            grid.store.clearFilter();
                            var searchFilter1 = Ext.getCmp('elistSearchFilter1');
                            var filterVal1 = searchFilter1.getValue().trim();
                            
                            var filters = [];
                            filters = new Ext.util.Filter({
                                filterFn: function (item) {
                                    // Multi column search with OR logic
                                    return grid.gridFilterAND(item, filterVal1, filterVal2, self.caseSensitive);
                                }
                            });
                            grid.store.filter(filters);
                        //}
                    }
                }
            },
            {
                xtype: 'checkbox',
                boxLabel: 'Case Sensitive',
                margin: '1 8 1 8',  // t,r,b,l
                checked: false,
                handler: function(cb, checked) {
                    var searchFilter1 = Ext.getCmp('elistSearchFilter1');
                    var searchFilter2 = Ext.getCmp('elistSearchFilter2');
                    if (searchFilter1 && searchFilter2) {
                        var prevCheckVal = searchFilter1.caseSensitive; 
                        searchFilter1.caseSensitive = checked;
                        searchFilter2.caseSensitive = checked;
                        if (prevCheckVal != checked) {
                            // force the filters to run with the new setting
                            var searchVal1 = searchFilter1.getValue();                            
                            searchFilter1.fireEvent('change', searchFilter1, searchVal1, "junk", null);
                            var searchVal2 = searchFilter2.getValue();                            
                            searchFilter2.fireEvent('change', searchFilter2, searchVal2, "junk", null);
                        }
                    }
                }
            },
            {
                xtype: 'button',
                text: 'Clear',
                width: 60,
                style: { backgroundColor: "FloralWhite", fontSize: "medium" },
                margin: '1 8 1 4',
                handler: function(but) {
                    var grid = but.ownerCt.ownerCt;
                    
                    var searchFilter1 = Ext.getCmp('elistSearchFilter1');
                    searchFilter1.setValue("");
                    
                    var searchFilter2 = Ext.getCmp('elistSearchFilter2');
                    searchFilter2.setValue("");
                    
                    grid.store.clearFilter();
                }
            }          
        ],
        
        originalTitle:'Entities Matching Search',   
    	titleAlign:'center',
	features:[{ftype:'grouping', groupHeaderTpl:'{name}' }],
    	bbar:Ext.create("DARPA.Statusbar", {id:'entityListStatus'}),
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
       	constructor:function(config) {
                // MFM added this as a param
    		this.store=makeEntityStore(this); // done in constructor otherwise we get the same store in all the ledgers
		this.callParent(arguments);    		
    		return this;
    	},

       	store:null,
       	
        align:'left',
	columns: [
		{header: 'Customer Nbr',        width:90,  dataIndex: 'id', sortable: true,
                  // Custom sort for this number column. 
                  doSort: function(state) {
                      var ds = this.up('grid').getStore();
                      var field = this.getSortParam();
                      ds.sort( {
                          property: field,
                          direction: state,
                          sorterFn: function(v1, v2) {
                              var id1 = v1.get(field);
                              id1 = id1.replace(",","");
                              id1 = id1.replace("-","");
                              if (id1 == null || id1 == "")
                                  id1 = "0";
                              
                              var nid1 = parseInt(id1);
                              
                              var id2 = v2.get(field);
                              id2 = id2.replace(",","");
                              id2 = id2.replace("-","");
                              if (id2 == null || id2 == "")
                                  id2 = "0";
                              
                              var nid2 = parseInt(id2);
                                
                              return (nid1 > nid2) ? 1 : (nid1 < nid2 ? -1 : 0);
                          }
                      });
                  }
                },	// 0

		{header: 'Name', width:200, 	
		dataIndex: 'effectiveName',
		hidden:true
/*		
		 dataIndex: 'attributes',
		 renderer:function(value,metadata,record) 
			{
				var str = "";
				for (var i = 0; i < value.length; ++i) {
					if (value[i].family == 'Name') {
						str += value[i].value;
						if (i < value.length - 1)
							str += ":";
					}
				}
				return str;
			}
*/			
		 },
		{header: 'Email Address', 
		 flex:1, 	
		 dataIndex: 'attributes',
		 renderer:function(value,metadata,record) 
                    {
                        var str = "";
                        for (var i = 0; i < value.length; ++i) {
                            if (value[i].family == 'Email') {
                                    str += value[i].value;
                                    if (i < value.length - 1)
                                            str += ":";
                            }
                        }
                        return str;
                    },
                 sortable: false   // MFM Address field is **WAY** too long to sort, would be very CPU intensive
                 
                },

		 
		 
		{
			header: 'Options', 
			width:100, 
			dataIndex:'attributes',

			renderer:function(value,metadata,record) 
				{
					var self=this;
					var custno = ""+record.raw.id;
					var tablink="showEntityDetails('" + record.raw.id + "');";
					return ("<button style='font-size:75%;' onclick=" + tablink + ">SHOW IN TAB</button>");
				}

		}
		
	],
        listeners:{
            // If results not very large use local sorting else use server-side sorting.
            // When the store's remoteSort == false (local sorting) the column header sorting menu items will be disabled.
            // We force them to always be enabled for both remote and local sorting.
            viewready: function(grid, opts) {
                var gridcol;
                // Enable the sorting menu items in the column headers. simple hack
                for (var i = 0; i < grid.columns.length; i++) {
                    gridcol = grid.columns[i];
                    if (gridcol.sortable == false) {
                        gridcol.sortable = true;
                    }
                }
            }
        },
        
        loadMask: true,
        
        // Custom filter for the search results grid
        // Mult-column multi-attribute search with OR logic
        //
        // item         - a grid.store.data.item
        // filterVal    - the user-entered search string
        // csFlag       - true = case sensitive, else case INsensitive match
        gridFilter: function (item, filterVal, csFlag) {
            //var grid = this;
                        
            var sid = item.get('id').toString();
            var senm = item.get('effectiveName');
            var attrs = item.get('attributes'); // this will be an array
            var idMatch;
            var nameMatch
            var attrValue, j;

            if (csFlag == null || csFlag == false) {   // Case INsensitive
                 // Simple solution for now, may not work with all unicode characters (foreign names)
                 // Fix later to do a case insensitive regex
                 var upSearchVal = filterVal.toUpperCase();
                 sid = item.get('id').toString();
                 senm = item.get('effectiveName');
                 idMatch = sid.toUpperCase().indexOf(upSearchVal) > -1;
                 nameMatch = senm.toUpperCase().indexOf(upSearchVal) > -1;
                 if (idMatch || nameMatch) { // quick bypass
                        return true;
                 }
                 // check all the attribute values for a match
                 for (j = 0; j < attrs.length; j++) {
                      attrValue = attrs[j].value;
                      if (attrValue.toUpperCase().indexOf(upSearchVal) > -1)
                          return true;
                 }
                 return (false);
            }
            else {  // Case sensitive
                idMatch = sid.indexOf(filterVal) > -1;
                nameMatch = senm.indexOf(filterVal) > -1;
                if (idMatch || nameMatch) { // quick bypass
                    return true;
                }
                // check all the attribute values for a match
                for (j = 0; j < attrs.length; j++) {
                    attrValue = attrs[j].value;
                    if (attrValue.indexOf(filterVal) > -1)
                        return true;
                }
                return (false);
            }                        
        },
              
        // Custom filter for the search results grid
        // Logical AND of two filter fields
        //
        // item         - a grid.store.data.item
        // filterVal1    - the user-entered search string
        // filterVal2    - the user-entered search string
        // csFlag       - true = case sensitive, else case INsensitive match
        gridFilterAND: function (item, filterVal1, filterVal2, csFlag) {
            var grid = this;
            
            // DEBUG
            //console.log("gridFilter debug filterVal1 = " + filterVal1 + ", filterVal2 = " + filterVal2);
            
            var test1 = grid.gridFilter(item, filterVal1, csFlag);
            var test2 = grid.gridFilter(item, filterVal2, csFlag);
            return (test1 && test2);
                 
        },
        
        loadCustomersForName:function(name) {
        	var self=this;
        	self.setTitle("Customers with the name " + name);
		var cstore = self.getStore();
		cstore.getProxy().url=Config.customerDetailsUrl;
		cstore.getProxy().extraParams.name=name;
		cstore.load(QSLoadComplete);
        },
	getMatches:function(dataSet, source, filters)
	{

		var s = this.getStore();
		s.removeAll();
		s.totalCount=undefined; // kludge for ExtJS bug in guaranteeRange that doesn't realize that this
					// is a new query.
		var p = s.getProxy();
		p.url=Config.entityAdvancedSearch;
		p.extraParams.jsonSearch=Ext.encode( {
			dataSet:dataSet,
			source: source,
			filters: filters
		});
		
//		s.load();
		Ext.getCmp('entityListStatus').setStatus("SEARCHING");
		s.guaranteeRange(0,999); // will load the store with this range which must be pagesize or lower		
	},
	
        // MFM
        appendTitle: function(appendText) {
            var p = this;
            if (appendText && appendText.length > 0) {
                var title = (p.originalTitle) ? p.originalTitle : p.title;
                p.setTitle(title + " " + appendText);
            }
        },
        
	setStatus:function(msg)
	{
		Ext.getCmp('entityListStatus').setStatus(msg);
	},
	
        clear:function()
        {
        	var self=this;
        	self.getStore().removeAll();
        }

    });

// A store to back the customer details grid
function makeEntityStore(grid)  // MFM added grid
{
   var store = Ext.create('Ext.data.JsonStore', {
        autoDestroy: true,
        model: 'DARPA.EntityModel',
	groupField:'effectiveName',        
        buffered: true, // keep more data in the store than are currently being displayed in the grid
        leadingBufferZone: 300,
        //OLD remoteSort:true,
        remoteSort: false,  // If results not very large use local sorting else use server-side sorting
                            // The default is local sorting but this may change based on the resultCount of the retreived data.
        pageSize: 2000, // number of rows constituting a page. So if we call loadPage(0) will get the first 2000 etc
        autoLoad: false, // don't call load method immediately after creation
	proxy: 
        {
            type: 'ajax',
            url: Config.entityAdvancedSearch,    
            timeout: 60000,
            reader: {
                type: 'json',
                root: 'entity',
                totalProperty: 'count'                
            }, // totalProperty is the total number of results matching the search, not the number last downloaded
            sortParam:'sortColumn',
	    encodeSorters: function(sorters) {
	      	var sortstr=sorters[0].property;
	       	if (sorters[0].direction == 'DESC')
	    		sortstr = sortstr + '$';
               	return sortstr;
            }


        }, // proxy
        
        listeners: {
            datachanged: function(store,b) {
                var self = this; // the store.
                var count= store.data.length;
                Ext.getCmp("entityListStatus").setStatus(" FOUND " + count + " MATCHES");

                // MFM update title with the total record count  
                var s = store.proxy.reader.jsonData;
                var totalRecs = (s.count) ? s.count : count;
                store.grid.appendTitle("(" + totalRecs.toString() + ")");

                // If results not very large use local sorting else use server-side sorting
                // The default is local sorting but this may change based on the resultCount of the retreived data.
                if (totalRecs > self.pageSize) {
                    self.remoteSort = true;
                }
                else {
                    self.remoteSort = false;
                }

                return true;
            }
        } // listeners
     });
     
     store.grid = grid; // MFM added: kind of ugly to add custom settings - really should extend.
     
     return store;

}
