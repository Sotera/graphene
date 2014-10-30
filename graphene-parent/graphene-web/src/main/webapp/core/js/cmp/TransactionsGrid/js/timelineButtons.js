Ext.define('DARPA.timelineSelectors', {

	extend:Ext.panel.Panel,

	layout:{
		type: 'hbox',
		align:'stretch'
	},


	constructor:function(config) {

		this.callParent(arguments);
		
		var yearButtons=[];
		this.yearPicker = Ext.create("Ext.form.RadioGroup" ,{
				xtype:'radiogroup',
				height:'auto',
				flex:1,
				columns:6,
				vertical:false,
				cls:'x-check-group-alt',
				items: []

			  }
		  );

		for (var  year = 2006; year <= 2012; ++year) {
			var button = Ext.create("Ext.form.field.Radio", {
				boxLabel: year,
				name: 'year',
				scope:this,
				handler:function(cb,checked) {
					if (checked) 
						this.tlPanel.timelinecontrolsChanged('year', cb.boxLabel);					
//						cb.up('radiogroup').up('panel').tlpanel.timelinecontrolsChanged('year', cb.boxLabel);


				} // handler args
			   } // button args
			); // create button
			yearButtons.push(button);
		}; // loop

		this.yearPicker.add(yearButtons);
		this.add(this.yearPicker);

		var monthButtons=[];
		this.monthPicker = Ext.create("Ext.form.RadioGroup" ,{
				xtype:'radiogroup',
				height:'auto',
				flex:2,
				columns:13,
				vertical:false,
				cls:'x-check-group-alt',
				items: []

			  }
		);

		var monthNames=['Jan','Feb','Mar','Apr','May','Jun','Jul','Aug','Sep','Oct','Nov','Dec', 'All'];          
		for (var month = 0; month < 13; ++month) {
			var button = Ext.create("Ext.form.field.Radio", {
				boxLabel: monthNames[month],
				name: 'month',
				scope:this,
				inputValue:month,
				handler:function(cb,checked) {
					if (checked) 
						this.tlPanel.timelinecontrolsChanged('month', cb.inputValue);					
//						cb.up('radiogroup').up('panel').tlpanel.timelinecontrolsChanged('month', cb.inputValue);

				} // handler args
			   } // button args
			); // create button
			monthButtons.push(button);
		}; // loop


		this.monthPicker.add(monthButtons);
		this.add(this.monthPicker);

		return this;

	}, // constructor

	setMonth:function(month)
	{
		var self=this;
		var monthButtons = self.monthPicker.items.items;
		for (var i = 0; i < monthButtons.length; ++ i) {
			monthButtons[i].setValue(i == month);
		}
	},
	enableAllButton:function(enabled)
	{
		var self = this;
	// when we are showing the whole year, we disable the All button at the end of the months
		var monthButtons = self.monthPicker.items.items;
		if (enabled)
			monthButtons[12].enable();
		else
		
			monthButtons[12].disable();
	},
	
	getSelectedYear:  function()
	{
	var self = this;
	
		var status = this.yearPicker.getChecked();
		if (status.length == 0)
			return "";
		return status[0].boxLabel;
	},

	
	// enables the year radio button in the array passed in, and checks the first one	
	enableYears: function(years)
	{
	var self = this;
	
		var anyChecked = false;
		var yGroup = this.yearPicker;
		var buttons = yGroup.items.items;
		for (var i = 0; i < buttons.length; ++i )
		{
			var rb = buttons[i];
			var exists = false; {
			for (var j = 0; j < years.length; ++j)
				if (years[j] == rb.boxLabel) 
					exists = true;
			} // j
			rb.setVisible(exists);
			if (exists) {
				if (anyChecked)
					rb.setValue(false);
				else {
					rb.setValue(true);
					anyChecked = true;
				}
			} // exists

		} // i

	} // enableYears
	
}
); // define
	


