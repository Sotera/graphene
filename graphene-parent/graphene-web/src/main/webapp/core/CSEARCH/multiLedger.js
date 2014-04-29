		function getLedger(acno)
		{
				return getTransactionFrame().getLedger();
					// acno is present to support multiple current ledgers in other version.
		}

		// global function so it can be called from custom html inside the customer grid.
		// The odd code is due to the fact that the account list is passed in as a comma-separated string,
		// having been generated as in-line html within the button in the grid control

		ledgerQueue=[];
		function showLedger()
		{
			closeLedgers(); // TODO: make this a user option? Or leave it to them?

			ledgerQueue=[];
			for (var i = 0; i < arguments.length; ++i) {
				var account = arguments[i];
				ledgerQueue.push(account);
			}
			nextLedger();
		};

		// called after each ledger has been read
		function nextLedger()
		{
			if (ledgerQueue.length == 0)
				return;

			var account = ledgerQueue.shift();
			var ledger = getLedger(account); // will make a new one if not already there
			if (ledger != null) { // already exists
				nextLedger();
				return;
			}

			ledger = addLedger(account);
			ledger.setStatus("LOADING - PLEASE WAIT");
			ledger.header.loadHeader(account); 		// will show trans when header received

		};

		function closeLedgers()
		{
				var tabframe = Ext.getCmp('tabFrame');
				var alltabs = tabframe.items.items;
				var kill_list = [];
				for (var i = 0; i < alltabs.length; ++i) {
					var tab = alltabs[i];
					if (tab.cstype == 'ledger')
						kill_list.push(tab);
				}
				for (var t = 0; t < kill_list.length; ++t)
					tabframe.remove(kill_list[t], true);
		};

		function getLedger(acno)
		{
			return getTransactionFrame().getLedger();

//			return Ext.getCmp(acno);
/*
				var tabframe = Ext.getCmp('tabFrame');
				var tabs = tabframe.items.items;
				for (var i = 0; i < tabs.length; ++i) {
					if (tabs[i].title == acno)
						return tabs[i].items.items[0];
				}
				return null;
*/
		};

		function addLedger(acno)
		{
				// so not found. Create a new one.
			var tabframe = Ext.getCmp('tabFrame');
    		var ledger = Ext.create("DARPA.Ledger", {title:acno, id:acno, closable:true, cstype:'ledger'});
			tabframe.add(ledger);
			return ledger;
		};

	   function getAllTransactions(ac)
	   {
			var ledger=getLedger(ac);

			var g = ledger.grid;

			g.setTitle("Reconstructed Statement of Account " + ac + ". May not be Complete");

			getLedger(ac).getTransactions(
				ac,
				0,
				0,
				"","","","");
	   };




