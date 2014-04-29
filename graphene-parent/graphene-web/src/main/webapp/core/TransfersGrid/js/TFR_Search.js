// SEARCH.JS
// PWG for DARPA

Ext.define("DARPA.TransfersSearch", 
{
	extend:'Ext.panel.Panel',
    	border:true,
    	width: 800,
        layout:{
    	 	align:'middle',
    	 	type:'hbox'
        },
        
	items:[],
	constructor:function()
	{
		this.items=makeEventSearchItems();	
		this.callParent(arguments);
	},
	getAccount:function()
	{
		var self = this;
		var left = self.items.items[0];		
		var toprow = left.items.items[0];
		var acfield = toprow.items.items[0];
		return acfield.getValue();
	},
	getComments:function()
	{
		var self = this;
		var left = self.items.items[0];		
		var toprow = left.items.items[0];
		var field = toprow.items.items[1];
		return field.getValue();
	},
	getUnit:function()
	{
		var self = this;
		var left = self.items.items[0];		
		var toprow = left.items.items[0];
		var field = toprow.items.items[2];
		return field.getValue();
	},
	getMinAmount:function()
	{
		var self = this;
		var left = self.items.items[0];		
		var botrow = left.items.items[1];
		var field = botrow.items.items[1];
		return field.getMinAmount();
	},
	getMaxAmount:function()
	{
		var self = this;
		var left = self.items.items[0];		
		var botrow = left.items.items[1];
		var field = botrow.items.items[1];
		return field.getMaxAmount();
	},
	getFromDate:function()
	{
		var self = this;
		var left = self.items.items[0];		
		var botrow = left.items.items[1];
		var field = botrow.items.items[0];
		return field.getFromDate();
	},
	getToDate:function()
	{
		var self = this;
		var left = self.items.items[0];		
		var botrow = left.items.items[1];
		var field = botrow.items.items[0];
		return field.getToDate();
	},
	setAccount:function(ac)
	{
		var self = this;
		var left = self.items.items[0];		
		var toprow = left.items.items[0];
		var acfield = toprow.items.items[0];
		acfield.setValue(ac);
	},
	clear:function()
	{
		var self = this;
		this.setAccount("");
		// TODO: dates etc
	
	}
	
});
//FIXME: This is duplicated elsewhere, fix that.
function makeEventSearchItems()
{

	var accountEntry = Ext.create("DARPA.TextField", {
		margin:2,
		width:265,
		labelWidth:100,
		fieldLabel:'Id',		
		enterCallback:function() {	
			doTransfersSearch('display', this)
	    	}
	});

	var commentSearch = Ext.create("DARPA.TextField", {
		height:'auto',
		margin:2,
		width:365,
		labelWidth:130,
		fieldLabel:'Comments Contain'
	});

	var unit =  Ext.create("DARPA.TextField", {
		height:'auto',
		margin:2,
		width:220,
		labelWidth:120,
		fieldLabel:'Transaction Unit'
	});

	var topRow = Ext.create("Ext.form.FieldContainer", {
		layout:{
			type:'hbox',
			align:'stretch'
		},
		items: [ 
			accountEntry,
			commentSearch,
			unit
		   ]

	});

	var bottomRow = Ext.create("Ext.form.FieldContainer", {
		width:800,
		layout:{
			type:'hbox',
			align:'middle'
		},
		items: [ 
			Ext.create("DARPA.dateRange",{margin:'2'}),
			Ext.create("DARPA.amountRange",{height:'auto'})
		]
	});

	var leftFrame = Ext.create("Ext.form.FieldContainer", {
		border:true,
		layout:{
			align:'middle',
			type:'vbox'
		},
		items:[
			topRow,
			bottomRow
		 ]

	}); 

	var loadbutton = Ext.create("Ext.button.Button", {
       	  	text:'SEARCH',
       		margin:2,   
               	listeners: {
               		click: function()
              		{
              			doTransfersSearch('display');
       			}
           	} // listeners
               	
       });// load button


	var resetbutton = Ext.create("Ext.button.Button", {
		xtype: 'button',
		text:'Reset',
		margin:5,
		handler:function() {
			this.up('form').getForm().reset();
		}
	}); // reset button

	var buttons = Ext.create("Ext.form.FieldContainer", {
		margin:5,
		layout:{
			type:'vbox',
			align:'stretch',
			width:100
		},
		items: [ 
			loadbutton
	//		,
	//		resetbutton
			]
	});
	
	return [leftFrame, buttons];


}

    
    function needBalanceCols()
    {
    
    	var needBalance = true;
    	var srch=getTransfersFrame().getSearch();
	var ac = srch.getAccount();
    	if (ac == "")
    	   needBalance = false;
       	var partics = srch.getComments();        
       	if (partics.length > 0)
       	   needBalance = false;
       	if (srch.getMinAmount().length > 0)
       	   needBalance = false;
       	if (srch.getMaxAmount().length > 0)
    	   needBalance = false;
    	   
    	return needBalance;
    }
    
    function updateTransfersBalanceCols()
    {	
    
	var needBalance = needBalanceCols();
	var ac = getTransfersFrame().getSearch().getAccount();
	var grid=getTransfersFrame().getLedger(ac).getGrid();
    
    	if (grid.columns[4].isVisible())
    		grid.columns[6].setVisible(needBalance);
    	if (grid.columns[7].isVisible())
    		grid.columns[9].setVisible(needBalance);
    	    
    }
    
    function makeTransfersParams()
    {
    // used for csv and xls export, not for account display

	var params="";
	var srch=getTransfersFrame().getSearch();
	var fromdt = srch.getFromDate();
	var todt   = srch.getToDate();

	if (fromdt != 0) {
		params += '&fromdtSecs=' + fromdt;
	}

	if (todt != 0) {
		params += '&todtSecs=' + todt;		
	}

	var minAmount = srch.getMinAmount();
	if (minAmount != null)
		params += '&minAmount=' + minAmount;
		
	var maxAmount = srch.getMaxAmount();
	if (maxAmount != null)
		params += '&maxAmount=' + maxAmount;

	var comments = srch.getComments();
	if (comments != null) 
		params += '&comments=' + comments;
		
	return params;
    
    }

 
    

