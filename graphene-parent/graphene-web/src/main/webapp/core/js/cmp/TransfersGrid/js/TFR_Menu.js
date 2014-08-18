// Transfers menu bar to appear above each ledger

 
   Ext.define("DARPA.Transfersmenu", {
   	extend:"Ext.toolbar.Toolbar", 
   	onRender:function(config) {
       		this.callParent(arguments);
   	},
   	ledger:null,
   	
   	constructor:function(config) {
   	
   	this.items = [
   		{
   			text: 'Export',
	   		 menu: Ext.create("Ext.menu.Menu", {
   
				items: [
					{
						text:'CSV', 
						handler: function() {
							doTransfersSearch('csv');
						}
					},
					{
						text:'Excel', 
						handler: function() {
							doTransfersSearch('xls');
						}
					}
				]

			   })
		},
/*		
   		{
   			text: 'Unit',
	   		 menu: Ext.create("Ext.menu.Menu", {
   
				items: [
					{
						text:'Transaction Unit',
						checked:true,
						checkHandler: function(item, checked) {
//							var m = item.up("menu");
//							var tb = m.up("toolbar");
							var ledger=getTransfersFrame().getLedger();
							var grid = ledger.getGrid();
							var needbalance = needBalanceCols();
							// TODO: following only if there is an acno							
							grid.columns[4].setVisible(checked);
							grid.columns[5].setVisible(checked);		                				
							grid.columns[6].setVisible(checked && needbalance);
						}

					},
					{
						text:'Local Unit',
						checked:false,
						checkHandler: function(item, checked) {
//							var m = item.up("menu");
//							var tb = m.up("toolbar");
//							var ledger=tb.ledger;
							var ledger=getTransfersFrame().getLedger();							
							var grid = ledger.getGrid();
							var needbalance = needBalanceCols();
							// TODO: following only if there is an acno
							grid.columns[7].setVisible(checked);
							grid.columns[8].setVisible(checked);
							grid.columns[9].setVisible(checked && needbalance);
						}
					}


				]

			   }
			   )
		},
*/		
   		{
   			text: 'View',
	   		 menu: Ext.create("Ext.menu.Menu", {
   
				items: [
					{
						text:'Charts', 
						checked:true,
						id:'timelineMenu',
						ledger:config.ledger,
						disabled:!Config.canUseD3,
						checkHandler:function(item,checked ) {
							item.ledger.getCharts().setVisible(checked);
						}
					},
					
					{
						text:'Transactions', 
						checked:true, 
						ledger:config.ledger,
						
						checkHandler:function(item,checked ) {
							item.ledger.getGrid().setVisible(checked);
						}
					} ,
                                        // MFM JIRA-68 Local Filtering
                                        {
						text:'Filter', 
						checked:true, 
						ledger:config.ledger,
						
						checkHandler:function(item,checked ) {
                                                    item.ledger.getGrid().showFilter(checked);
						}
					}

				] // items for view menu

			   }) // create view menu
		}
   	
   	] // items for toolbar
	this.callParent(arguments);
   	
   } // constructor
   
   } // config for extend
   ); // extend
   
  