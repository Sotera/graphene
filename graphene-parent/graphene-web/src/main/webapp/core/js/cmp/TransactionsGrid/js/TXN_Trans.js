

    function getTransactions(accountNumber, fromdt, todt, minAmount, maxAmount, comments, unit)
    {
    	var hasComments = (comments != null && comments != '');
    
        	var g = ledger.getGrid();
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
    	
    		if (!hasComments) {
    			var tc = ledger.getTimeline(); 
    			tc.setVisible(false);
    		
    		}
    
		setStatus("Loading");

		g.viewConfig.emptyText ='Loading';
		s.removeAll();
		s.totalCount=undefined; // kludge for ExtJS bug in guaranteeRange that doesn't realize that this
					// is a new query.

		s.guaranteeRange(0,999); // will load the store with this range which must be pagesize or lower

		// NB we can't get the statistics yet, because the load triggered by guaranteeRange is asynch and the 
		// server doesn't have the stastics yet.

 
    }
