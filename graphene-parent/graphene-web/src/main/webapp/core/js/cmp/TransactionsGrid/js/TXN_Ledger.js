// var timeline;

// a Ledger object contains a menu, timeline, ledger, and status. It does not contain a search
// panel because it will be created from the central search application as well as the current standalone.


Ext.define("DARPA.Ledger", {
	extend:'Ext.panel.Panel', // has to be a panel because it's a tab and we use setIcon
//	title:false,
	layout:{
		type:'vbox', align:'stretch'
	},
	items:[],

	constructor:function(config) {

		var id = (undefined==config.id) ? this.getId() : config.id; 
                this.institution = config.institution;
             
		var timelinePanel = Ext.create("DARPA.TimelinePanel",
			{
				flex:1,
				width:'auto',					
				id: config.id + "-TimelinePanel",
				hidden:false
		});
		
		this.items= [
			Ext.create("DARPA.Transactionmenu", {
				height:24, 
				width:'auto',
				ledger:this,
				id:config.id + "-Menu"
				
				}),		// Menu:0
			
		
       			Ext.create("DARPA.TransactionGrid", {
	       				flex:1,
					width:'auto',	       				
					id:config.id + "-Grid",
	       				owner:this
       				}),			

       			timelinePanel,

			Ext.create("DARPA.Statusbar", {id:config.id + "-Status", width:'auto'}) // Status: 3
		];

		this.callParent(arguments);
		
		//var p = this.items.items[2]; // see if it's the same object as timelinePanel

//		timelinePanel.add(timelinePanel.makeItems(id));
		timelinePanel.setView('year');
                // DEBUG
		//console.log("Done ledger constructor");
		
	},
	getMenu:function()
	{
		var self=this;
		return self.items.items[0];
	},
	getTimeline:function()
	{
		var self=this;
		return self.items.items[2];
	},
	getGrid:function()
	{
		var self=this;
		return self.items.items[1];
	},
	getStatus:function()
	{
		var self=this;
		return self.items.items[3];
	},
	setBlink:function(blink)
	{
		utils.setBlink(this, blink);
	},
	setTransUnit:function(setit)
	{
		var self = this;
		var g = self.getGrid();
		g.columns[4].setVisible(setit);
		g.columns[5].setVisible(setit);
		g.columns[6].setVisible(setit);
	},
	setLocalUnit:function(setit)
	{
		var self = this;	
		var g = self.getGrid();
		g.columns[7].setVisible(setit);
		g.columns[8].setVisible(setit);
		g.columns[9].setVisible(setit);
	},
	
        getAndShowTransactions:function(ac, entity_id)
	{
            var fromdt=null, todt=null;
            var srch = getTransactionFrame().getSearch();
	
		var self = this;

		var fromdate =srch.getFromDate();
		if (fromdate != "") {
//			fromdt = fromdate.getTime(); // changed PWG 5/13/2013
			fromdt=fromdate;
		}

		var tod = srch.getToDate();
		if (tod != "") {
//			todt = tod.getTime();
			todt = tod;
		}

		var minAmount = srch.getMinAmount();
		var maxAmount = srch.getMaxAmount();	

		var comments = srch.getComments();
		var unit = srch.getUnit();
		
		if (fromdt == null && comments=='') {
			self.getAllTransactions(ac); // TODO: is this necessary?
			return;
		}
		
		var multiAccount = (ac == "");
		var g = self.getGrid();
//		g.columns[3].setVisible(multiAccount); // unit
		g.columns[3].setVisible(true); 	  // unit. Always show now

		if (multiAccount) {
			g.setTitle("SEARCH RESULTS");
	//		timeline.clearStatistics() // TODO: need this function
		}
		else {
			g.setTitle("Reconstructed Statement of Account " + ac + ". May not be Complete");
	//		loadStatistics();
		}

		self.getTransactions(
			ac, 
			entity_id,
			fromdt, 
			todt, 
			minAmount,
			maxAmount,
			comments,
			unit
		);

    	},
    	
	getTransactions:function(accountNumber, entity_id, fromdt, todt, minAmount, maxAmount, comments, unit)
	{
            var self=this;
            var hasComments = (comments != null && comments != '');
    
        	var g = self.getGrid();
        	var s = g.getStore();
        	var p = s.getProxy();
    
        	p.extraParams.accountNumber = accountNumber;
        	p.extraParams.dataSource="Walker";
        	p.extraParams.dataSet="Transactions";
        	p.extraParams.entity_id=entity_id; // optional - either accountNumber or entityId
        	p.extraParams.fromdt 	  = fromdt;    	
        	p.extraParams.todt 	  = todt;    	    	
        	p.extraParams.minAmount = minAmount;    	    	
        	p.extraParams.maxAmount = maxAmount;
        	p.extraParams.unit  = unit;
    		p.extraParams.comments  = comments;
    		
    		p.url=hasComments ? Config.textUrl : Config.detailUrl;
    	
    		if (!hasComments) {
    			var tc = self.getTimeline();
    			tc.setVisible(false);
    		
    		}
    
		this.setStatus("Loading");
		g.setTitle("INSTITUTION: " + self.institution + " | Transactions for account " + accountNumber);
		g.viewConfig.emptyText ='Loading';
		s.removeAll();
		s.totalCount=undefined; // kludge for ExtJS bug in guaranteeRange that doesn't realize that this
					// is a new query.
		self.setBlink(true);
		s.guaranteeRange(0,999); // will load the store with this range which must be pagesize or lower

		// NB we can't get the statistics yet, because the load triggered by guaranteeRange is asynch and the 
		// server doesn't have the stastics yet.

 
    },
	getAllTransactions:function(accountNumber)
	{
	
		var self=this;
        	var g = self.getGrid();
        	var s = g.getStore();
        	var p = s.getProxy();
    
        	p.extraParams.accountNumber = accountNumber;
	       	p.extraParams.fromdt 	  = "";
        	p.extraParams.todt 	  = "";
        	p.extraParams.minAmount = "";
        	p.extraParams.maxAmount = "";
        	p.extraParams.unit  = "";
    		p.extraParams.comments  = "";    	
    		
    		p.url=Config.detailUrl;
    	
//		var tc = self.getTimeline();
//		tc.setVisible(false);
    
		this.setStatus("Loading");
		
		g.setTitle("INSTITUTION: " + self.institution + " | Transactions for account " + accountNumber);
		
		g.viewConfig.emptyText ='Loading';
		s.removeAll();
		s.totalCount=undefined; // kludge for ExtJS bug in guaranteeRange that doesn't realize that this
					// is a new query.
		self.setBlink(true);
		s.guaranteeRange(0,999); // will load the store with this range which must be pagesize or lower

		// NB we can't get the statistics yet, because the load triggered by guaranteeRange is asynch and the 
		// server doesn't have the stastics yet.

    },
    
	setStatus:function(status)
	{
		var self=this;
		self.getStatus().setStatus(status);
	}

});



