// CS_SEARCH.JS
// Search form for the Customer Lists
// PWG for DARPA

// SEARCH.JS
// PWG for DARPA

Ext.define("DARPA.SearchTextField",
{
	extend:'Ext.form.field.Text',
	height:'auto',
	margin:10,
	labelWidth:160,
	minWidth:400,
	maxWidth:400
	
});

Ext.define("DARPA.CSSearch",
{
   	extend: "Ext.panel.Panel",   
   	width:'auto',
    	height:'auto',
    	align:'center',
    	title: 'GRAPHENE WEB GLOBAL SEARCH',
    	titleAlign:'center',
    	bodyStyle:'background-color:lightyellow',
    	border:1,
        margin:4,  // MFM
        layout:{
    	 	type:'vbox',
    	 	align:'stretch'
        },
        items:[],
  	constructor:function() 
	{
		var loadbutton = Ext.create("Ext.Container", {
			layout:	{
				type:'hbox',
				align:'stretch'
			},
			margin:10,
			items:[
				{xtype:'component',flex:1},
				{
					xtype:'button',
					text:'SEARCH',
					height:30,
					width:90,
					listeners: {
						click: function()
						{
							var srch = getCSFrame().getSearch();
							doGlobalSearch(srch.getName(), srch.getIdType(), srch.getType(), srch.getCaseSensitive());	
						}
					} // listeners
				},
				{xtype:'component',flex:1}
			]

		});// load button

		var idTypes = Ext.create("Ext.form.RadioGroup", {
			fieldLabel:'Search For',
			labelAlign:'center',
			height:'auto',
			width:'auto',
			minWidth:800,
			margin:10,   
			defaults: {
				labelWidth:200,
				margin:10
			},
			vertical:false,
//				cls:'x-check-group-alt',
			items: [
				{
					name:'idtype',
					boxLabel:'Name',
					inputvalue:1,
					checked:true
				},
				{
					name:'idtype',
					boxLabel:'Address',

					inputvalue:2
				},
				{
					name:'idtype',
					boxLabel:'ID 1',
					inputvalue:3
				},
				{
					name:'idtype',
					boxLabel:'ID 2',
					inputvalue:4
				},
				{
					name:'idtype',
					boxLabel:'Any Field',
					inputvalue:5
				}
			]
		});
	
		this.items = [
			idTypes, // 0
			
			Ext.create("DARPA.SearchTextField", { // 1
				fieldLabel:'All of these words'
			}),
			Ext.create("DARPA.SearchTextField", { // 2
 				fieldLabel:'The exact word or phrase'
			}),
			Ext.create("DARPA.SearchTextField", { // 3
				fieldLabel:'Any of these words'
			}),
			Ext.create("DARPA.SearchTextField", { // 4
				fieldLabel:'None of these words'
			}),
			
			Ext.create("DARPA.SearchTextField", { // 5
				fieldLabel:'Sounds Like'
			}),
			Ext.create("DARPA.SearchTextField", { // 6
				fieldLabel:'Regular Expression'
			}),
			{
				xtype:'checkbox', // 7
				margin:10,
				boxLabelAlign:'before',
				labelPad:65,
				fieldLabel:'Case Sensitive'				
			},

                        // MFM Added Date Range and Help Button     // 8
                        Ext.create("Ext.Container",
                         {
                                 height:200,
                                 width:850,
                                 minWidth: 850,
                                 border:true,
                                 margins: '2,2,2,2',
                                 layout:{
                                         type:'hbox',
                                         align:'stretch'
                                 },
                                 items: [    // MFM added ids srchFromDate and srchToDate
                                         Ext.create('DARPA.dategroup', {id: 'CSsrchFromDate', margins: '2,0,0,10', label:'FROM:', align:'left'}), // bottom left right top   // from date
                                         Ext.create('DARPA.dategroup', {id: 'CSsrchToDate', margins: '2,0,0,10', label:'TO:' , align:'left'}),  // to date
                                         {   // MFM help button
                                                xtype:'button',
                                                icon: Config.helpIcon,
                                                maxHeight: 30,
                                                scale: 'medium',
                                                style: {marginTop: '2px', marginLeft: '20px'}, 
                                                listeners: {
                                                        click: function() {
                                                            Ext.Msg.alert(
                                                               'Help',
                                                               'Enter or choose an optional FROM date. This will limit the search to records on or after this date.<br>' +
                                                               'Enter or choose an optional TO date. This will limit the search to records before or on this date.<br>' 
                                                            );
                                                        }
                                                } // listeners
                                        }
                                 ] // vertically stacked date entry
                         }),
                         
			loadbutton

		 ]; // items for main search box
		 this.callParent(arguments);
	 },
         
         getName:function() 
         {
         	var self=this;
         	
         	if (self.getIdType() == 'account')
         		return self.items.items[1].getValue().trim();
         		
         		
		var result = "";
		
		var all   = self.items.items[1].getValue().replace(",", " ").replace("  ","");
         	var exact = self.items.items[2].getValue().replace(",", " ").replace("  ","");
         	var any   = self.items.items[3].getValue().replace(",", " ").replace("  ","");
		var none  = self.items.items[4].getValue().replace(",", " ").replace("  ","");
		var soundslike = self.items.items[5].getValue();
		var regex = self.items.items[6].getValue();
		if (exact != '')
			result= '"' + exact + '"';
		else if (soundslike != '')
			result = soundslike;
		else if (regex != '')
			result = regex;

		else {
//			var x = new RegExp("\\W","g")	// any non-word character. Did not support asterisk
//			var x = new RegExp("\\W","g")	// any non-word character				
			var x = " ";
			if (all != '') {
				all = all.split(x);
				for (var i = 0; i < all.length; ++i) {
					if (all[i] != '')
						result += "+" + all[i] + " ";
				}
			}
			if (none != '') {
				none=none.split(x);
				for (var i = 0; i < none.length; ++i)
					if (all[i] != '')				
						result += "-" + none[i] + " ";
			}
			if (any != '') {
				any=any.split(x);
				for (var i = 0; i < any.length; ++i)
					if (all[i] != '')				
						result += any[i] + " ";
			}
			
		}
		return result.trim();
			
         },
         getType:function() 
         {
         	var self=this;
		var soundslike = self.items.items[5].getValue();
		if (soundslike!='')
			return "soundslike";
		var regex = self.items.items[6].getValue();
		if (regex != '')
			return "regex";
		return "simple";	

         },
         getIdType:function() 
         {
         	var self=this;
         	var type = '';
	        var cats = self.items.items[0];
      		if (cats.items.items[0].checked)
      			type='name';
      		else if (cats.items.items[1].checked)
      			type='address';
      		else if (cats.items.items[2].checked)
      			type='event';
      		else if (cats.items.items[3].checked)
      			type='account';
      		else if (cats.items.items[4].checked)
      			type='all';
      		return type;
         },
         getCaseSensitive:function()
         {
         	var self=this;
         	return self.items.items[7].getValue();        
         },
         
         // MFM
        getFromDate:function()
	{
                var srchFromDate = Ext.getCmp('CSsrchFromDate');
                if (srchFromDate) {
                    return srchFromDate.getDt();
                }
                return null;
	
	},
	getToDate:function()
	{
                var srchToDate = Ext.getCmp('CSsrchToDate');
                if (srchToDate) {
                    return srchToDate.getDt();
                }
                return null; 
	},
        updateCSSearchDates: function(fromDate, toDate) {
            var fdate = Ext.getCmp('CSsrchFromDate');
            var tdate = Ext.getCmp('CSsrchToDate');
            if (fdate) {
                var fromDateDate = new Date(fromDate);
                fdate.items.items[2].setValue(utils.formatDate(fromDateDate));
            }
            if (tdate) {
                var toDateDate = new Date(toDate);
                tdate.items.items[2].setValue(utils.formatDate(toDateDate));
            }
        } 
         
});


  
   
    

