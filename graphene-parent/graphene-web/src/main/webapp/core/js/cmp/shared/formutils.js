Ext.define("DARPA.Statusbar", {
	extend:Ext.Container,
      	height:24,
	style:'background-color:lightblue',
	layout:{
		type:'hbox',
 		align:'stretch'
	},
	
	items:[
		{
			xtype:'displayfield',
			value:'Status: ',
			margins:'0,5,0,5'
		},
		{
			xtype:'displayfield',
			value:'OK',
			margins:'0,5,0,5'					
		}
     			
	],
	setStatus:function(stat)
	{
		var self=this;
		self.items.items[1].setValue(stat);
	}

});


Ext.define("DARPA.TextField",
{
	extend:'Ext.form.field.Text',
	enterCallback:function(){},
	listeners: {
		specialkey:function(f,o) {
			if (o.getKey() == 13)
				this.enterCallback();
		}
	},
	clear:function()
	{
		this.reset();
	}
});


// MFM LongNumberField May not be used.
Ext.define("DARPA.LongNumberField",
{
	extend:'Ext.Container',
	xtype:'fieldcontainer',
	height:'auto',
	margin:3,   	
	width:'auto',
	layout:{
		type:'hbox',
		align:'stretch'
	},
	enterCallback:function(){},
	items: [ 
	
		{
			xtype:'displayfield',
			value:'SEARCH FOR',
			width: 50,    		 				
			margin: 2
		},
		
		{
			xtype:'textfield',
			margin:3,    
			width: 350,
			listeners: {
				specialkey:function(f,o) {
					if (o.getKey() == 13)
						up().enterCallback();
				}
			}
		}
	],
	constructor:function()
	{
		
		this.callParent(arguments);
	},
	getNumber:function()
	{
		var self = this;
		return self.items.items[1].getValue();
	}
});

Ext.define('DARPA.amountRange',
{
	extend:'Ext.Container',
    	border:true,
	layout:{
		type:'hbox',
		align:'stretch'
	},
    	items: [
    		{
    			xtype:'displayfield',
    		 	value:'AMOUNT',
    		 	margin: 5
  		},
	        
    		{
    		   	xtype: 'fieldcontainer',
    		   	border:true,
    		   	flex: 1,
	                items: [ 
				{
					xtype:'textfield',
					labelWidth:20,
					fieldLabel: 'Min',
					margin:5,
					width: 120
				},
				{
					xtype:'textfield',
					fieldLabel: 'Max',
					labelWidth:20,			        
					margin:5,    
					width: 120
				}
	                ] // vertically stacked amount entry
	        }
    	
    	],
    	clear:function()
    	{
	    	var self=this;    	
	    	self.items.items[1].items.items[0].setValue('');	    	
	    	self.items.items[1].items.items[1].setValue('');	    		    	
    	},
    	getMinAmount:function()
    	{
	    	var self=this;
	    	return self.items.items[1].items.items[0].getValue();
    	},
     	getMaxAmount:function()
     	{
 	    	var self=this;
 	    	return self.items.items[1].items.items[1].getValue();
    	}
    
    });
    
    Ext.define("DARPA.dateRange", 
    {
       	extend: 'Ext.Container',
    	layout:{
    		type:'hbox',
    		align:'stretch'
    	},
    	border:true,
    	items:[],
    	
    	constructor:function()
    	{
    		this.items = [
			{
				xtype:'displayfield',
				value:'DATE',
				width: 35,
                                height: 24, // MFM
				margin:5
			},

			Ext.create("Ext.Container",
			{
				height:'auto',
				width:'auto',
				layout:{
					type:'vbox',
					align:'stretch'
				},
				items: [    // MFM added ids srchFromDate and srchToDate
					Ext.create('DARPA.dategroup', {	id: 'srchFromDate', margins: '2,0,0,1', label:'FROM:', align:'left'}), // bottom left right top   // from date
					Ext.create('DARPA.dategroup', {	id: 'srchToDate', margins: '2,0,0,1', label:'TO:' , align:'left'})  // to date
				] // vertically stacked date entry
			})
		];
		
		this.callParent(arguments);
        		 
    	
    	},
    	clear:function()
    	{
    		//OLD var self = this;    	
    		//OLD self.items.items[1].items.items[0].clear();    		
    		//OLD self.items.items[1].items.items[1].clear();
                var srchFromDate = Ext.getCmp('srchFromDate');
                if (srchFromDate) {
                    srchFromDate.clear();
                }
    	},
    	getFromDate:function()
    	{
    		//OLD var self = this;
    		//OLD return self.items.items[1].items.items[0].getDt();
                var srchFromDate = Ext.getCmp('srchFromDate');
                if (srchFromDate) {
                    return srchFromDate.getDt();
                }
                return null;
    	
    	},
    	getToDate:function()
    	{
    		//OLD var self = this;
    		//OLD return self.items.items[1].items.items[1].getDt();
                var srchToDate = Ext.getCmp('srchToDate');
                if (srchToDate) {
                    return srchToDate.getDt();
                }
                return null;                
    	}
        
   });
    

 
    