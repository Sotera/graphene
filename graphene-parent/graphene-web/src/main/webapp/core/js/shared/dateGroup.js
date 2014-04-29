// a custom control for showing a date field with a popup date picker.

  Ext.define('DARPA.dategroup', {
    	extend:'Ext.Container',
    	label:'',
    	layout: {
       		type:'hbox'
	},
	height:'auto',
	margin: 2,
    	
	constructor:function()
	{
		var name = {
			xtype:'displayfield',
			value:arguments[0].label,
			width:60
		};

		this.items= 
		 [
			name, 
			{
				xtype: 'datepicker',
				hidden:true,
				handler: function(picker,date) {                                    
					picker.ownerCt.items.items[2].setValue(utils.formatDate(date));
					picker.hide();
				}

			},
			{
				xtype:'textfield',
				width: 140
			},
			{
				xtype:'button',
				icon: 'extjs/resources/themes/images/default/shared/calendar.gif',
				listeners: {
					click: function(b)
					{
						var owner = b.ownerCt;
						owner.items.items[1].show();
					}  
				}

			}
		]
		this.callParent(arguments);
	}, // constructor

	setLabel:function(lab)
	{
		this.items.items[0].setValue(lab);
	},
	
	getDt:function()
	{
		var val = this.items.items[2].getValue();
		if (val == '')
			return 0;
		var dt = Date.parse(val);
		return dt;
	},
	clear:function()
	{
		this.items.items[1].setValue(new Date());	// the date picker
		this.items.items[2].setValue('');	// the date string
	}
	

    });
    