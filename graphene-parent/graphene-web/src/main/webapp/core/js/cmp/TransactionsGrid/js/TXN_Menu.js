// menu bar to appear above each ledger grid
// A "ledger" is an object containing a menu, a ledger grid, timeline panel and a status bar


 
   Ext.define("DARPA.Transactionmenu", {
   	extend:"Ext.toolbar.Toolbar",
	constructor:function(config)
	{
		this.items= [
			{
				text: 'Export',
				menu:makeExportMenu(config.ledger)
			},
			{
				text: 'Unit',
				menu: makeUnitMenu(config.ledger)
			},

			{
				text: 'View',
				menu: makeViewMenu()
			}
		]; // items for toolbar
		this.callParent(arguments);
	} // constructor   
   
   }); // define

   function makeExportMenu(ledger)
   {
   	this.ledger = ledger;
   	var menu = Ext.create("Ext.menu.Menu", {
		items: [
			{
				text:'CSV', 
				handler: function(item) {
					item.ledger.doTransactionSearch('csv');
				}
			},
			{
				text:'Excel', 
				handler: function(item) {
					ledger.ledger.doTransactionSearch('xls');
				}
			}
		]
	   });
	   
	   return menu;
   };
   
   function makeUnitMenu(ledger) 
   {
	var menu = Ext.create("Ext.menu.Menu", {
//	menu.ledger=ledger;

		items: [
			{
				text:'Transaction Unit',
				checked:true,
				ledger:ledger,
				checkHandler: function(item, checked) {
//					var m = item.up("menu");
//					var tb = m.up("toolbar");
					ledger.setTransUnit(checked);
				}

			},
			{
				text:'Local Unit',
				checked:false,
				ledger:ledger,				
				checkHandler: function(item, checked) {
//					var m = item.up("menu");
//					var tb = m.up("toolbar");
//					var ledger=tb.ledger;
					ledger.setLocalUnit(checked);
				}
			}


		] // items for main menu
	}); // create
	
	return menu;
   
   }
   
   function makeViewMenu(ledger) 
   {
   	var menu = Ext.create("Ext.menu.Menu", {
   		ledger:ledger,
      
		items: [
			{
				text:'Timeline', 
				checked:true,
				disabled:!Config.canUseD3,
				checkHandler:function(item,checked ) {
				var p = item.ledger;
					var m = item.up("menu");
					m = m.up("panel");
					var tcpanel = m.getTimeline();
					tcpanel.setVisible(checked);
				}
			},
			{
				text:'Transactions', 
				checked:true, 
				checkHandler:function(item,checked ) {
					var m = item.up("menu");
					m = m.up("panel");
					m.getGrid().setVisible(checked);
				}
			}
   
		] // items for view menu
   
	   }); // create view menu
	   
	   return menu
  
   }
   
   
  