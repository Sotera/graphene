
// a Ledger object contains a menu, customer header, timeline, ledger, and status. It does not contain a search
// panel because it will be created in an Entity panel


Ext.define("DARPA.TransfersLedger", {
	extend:'Ext.panel.Panel',
	layout:{
		type:'vbox', align:'stretch'
	},

	constructor:function(config) {

                this.institution = config.institution;        
/*	
		var timelinePanel = Ext.create("DARPA.TimelinePanel",
			{
				flex:1,
				width:'auto',					
				id: config.id + "-TimelinePanel",
				hidden:false
			}
		);
*/	
	
		var charts = Ext.create("DARPA.TransfersChartFrame", {
			id:config.id + "-TransfersChartsFrame"}
		);
                
		this.items= [
			Ext.create("DARPA.Transfersmenu", {
				height:24, 
				id:config.id + '-Menu',
				ledger:this}),		// Menu:0

       			Ext.create("DARPA.TransfersGrid", {
	       				flex:1,
					width:'auto',	       				
					id:config.id + "-Grid",
	       				owner:this
       				}),						// Grid: 1
       			charts,                                                 // Chart 2
//			timelinePanel,
			
			Ext.create("DARPA.Statusbar", {id:config.id + "-Status", width:'auto'}) // Status: 3
		];

//       		this.menu.ledger=this;		
		this.callParent(arguments);      
//		timelinePanel.setView('year');		
	},
	getMenu:function()
	{
		var self=this;
		return Ext.getCmp(self.id + "-Menu");
	},
	
	getCharts:function()
	{
		var self=this;
		return Ext.getCmp(self.id + "-TransfersChartsFrame");
	},
	getGrid:function()
	{
		var self=this;
		return Ext.getCmp(self.id + "-Grid");
	},
	getStatus:function()
	{
		var self=this;
		return Ext.getCmp(self.id + "-Status");
	},
	getTimeline:function()
	{
		var self=this;
		return Ext.getCmp(self.id + "-TimelinePanel");
	},
	
	setBlink:function(blink)
	{
		utils.setBlink(this, blink);
	},
	
	
        getAndShowTransactions:function(ac)
	{
            var fromdt=null, todt=null;
            var srch = getTransfersFrame().getSearch();
		
		var self = this;

		var fromdate =srch.getFromDate();
		if (fromdate != "") {
			fromdt = fromdate.getTime();
		}

		var tod = srch.getToDate();
		if (tod != "") {
			todt = tod.getTime();
		}

		var minAmount = srch.getMinAmount();
		var maxAmount = srch.getMaxAmount();	

		var comments = srch.getComments();
		var unit = srch.getUnit();
		
		if (fromdt == null && comments=='') {
			self.getAllTransactions(ac); 
			return;
		}
		
		// TODO: Following does not work yet
		
		var multiAccount = (ac == "");
		var g = self.getGrid();
//		g.columns[3].setVisible(multiAccount); // unit
		g.columns[3].setVisible(true); 	  // unit. Always show now

//		if (multiAccount) {
//			g.setTitle("SEARCH RESULTS");
	//		timeline.clearStatistics() // TODO: need this function
//		}
//		else {
			g.setTitle("Reconstructed Statement of Account " + ac + ". May not be Complete");
			loadStatistics();
//		}

		self.getTransactions(
			ac, 
			fromdt, 
			todt, 
			minAmount,
			maxAmount,
			comments,
			unit
		);

    	},
    	
	getTransactions:function(accountNumber, fromdt, todt, minAmount, maxAmount, comments, unit)
	{
            var self=this;
            var hasComments = (comments != null && comments != '');
    
        	var g = self.getGrid();
        	var s = g.getStore();
        	var p = s.getProxy();
    
        	p.extraParams.accountNumber = accountNumber;
        	p.extraParams.fromdt 	  = fromdt;    	
        	p.extraParams.todt 	  = todt;    	    	
        	p.extraParams.minAmount = minAmount;    	    	
        	p.extraParams.maxAmount = maxAmount;
        	p.extraParams.unit  = unit;
    		p.extraParams.comments  = comments;
    		
    		p.url=hasComments ? Config.textUrl : Config.detailUrl;
    	
    
		this.setStatus("Loading");
		
		g.setTitle("INSTITUTION: " + self.institution + " | Email List");

		g.viewConfig.emptyText ='Loading';
		s.removeAll();
		s.totalCount=undefined; // kludge for ExtJS bug in guaranteeRange that doesn't realize that this
					// is a new query.

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
    		
    		p.url=Config.transfers_transUrl;
    	
		this.setStatus("Loading");

		g.setTitle("EMAIL LIST for id ["+accountNumber+"]");
		
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



