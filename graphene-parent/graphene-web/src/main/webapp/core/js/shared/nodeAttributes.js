Ext.define("DARPA.NodeAttributes",
{
	extend:"Ext.form.FieldSet",
	items:[
		{
			xtype:'textfield',
                        id: 'nafldSelCommunicationId',
			fieldLabel:'Communication Id',
			labelLength:220,
			//value:'500',
			width: 200
		},
                {
			xtype:'textfield',
                        id: 'nafldConnections',
			fieldLabel:'Connections',
			labelLength:220,
			//value:'500',
			width: 200
		},
                {
                    xtype: 'label',
                    html: "<hr>",
                    width: 250
                },
                {
			xtype:'textfield',
                        id: 'nafldFromNum1',
			fieldLabel:'From Number 1',
			labelLength:220,
			//value:'500',
			width: 200
		},
                {
			xtype:'textfield',
                        id: 'nafldToNum1',
			fieldLabel:'To Number 1',
			labelLength:220,
			//value:'500',
			width: 200
		},
                {
			xtype:'textfield',
                        id: 'nafldNumEvents1',
			fieldLabel:'# of Events 1',
			labelLength:220,
			//value:'500',
			width: 200
		},
                {
                    xtype: 'label',
                    html: "<hr>",
                    width: 250
                },
                //-------
                {
			xtype:'textfield',
                        id: 'nafldFromNum2',
			fieldLabel:'From Number 2',
			labelLength:220,
			//value:'500',
			width: 200
		},
                {
			xtype:'textfield',
                        id: 'nafldToNum2',
			fieldLabel:'To Number 2',
			labelLength:220,
			//value:'500',
			width: 200
		},
                {
			xtype:'textfield',
                        id: 'nafldNumEvents2',
			fieldLabel:'# of Events 2',
			labelLength:220,
			//value:'500',
			width: 200
		},
                {
                    xtype: 'label',
                    html: "<hr>",
                    width: 250
                },
                //-------
                {
			xtype:'textfield',
                        id: 'nafldFromNum3',
			fieldLabel:'From Number 3',
			labelLength:220,
			//value:'500',
			width: 200
		},
                {
			xtype:'textfield',
                        id: 'nafldToNum3',
			fieldLabel:'To Number 3',
			labelLength:220,
			//value:'500',
			width: 200
		},
                {
			xtype:'textfield',
                        id: 'nafldNumEvents3',
			fieldLabel:'# of Events 3',
			labelLength:220,
			//value:'500',
			width: 200
		},
                {
                    xtype: 'label',
                    html: "<hr>",
                    width: 250
                },
                //-------
                {
			xtype:'textfield',
                        id: 'nafldFromNum4',
			fieldLabel:'From Number 4',
			labelLength:220,
			//value:'500',
			width: 200
		},
                {
			xtype:'textfield',
                        id: 'nafldToNum4',
			fieldLabel:'To Number 4',
			labelLength:220,
			//value:'500',
			width: 200
		},
                {
			xtype:'textfield',
                        id: 'nafldNumEvents4',
			fieldLabel:'# of Events 4',
			labelLength:220,
			//value:'500',
			width: 200
		}
             
	],
	getCommunicationId:function()
	{
		var nitem = Ext.getCmp('nafldSelCommunicationId');
                if (nitem) {
                    return nitem.getValue();
                }
                else return null;
	},
	setCommunicationId:function(inString)
	{
		var nitem = Ext.getCmp('nafldSelCommunicationId');
                if (nitem) {
                    nitem.setValue(inString);
                }
	},
        
        setFromNumber:function(inString, idnum)
	{
		var nitem = Ext.getCmp('nafldFromNum' + idnum);
                if (nitem) {
                    nitem.setValue(inString);
                }
	},
        getFromNumber:function(idnum)
	{
		var nitem = Ext.getCmp('nafldFromNum' + idnum);
                if (nitem) {
                    return nitem.getValue();
                }
                return null;
	},
        
        setToNumber:function(inString, idnum)
	{
		var nitem = Ext.getCmp('nafldToNum' + idnum);
                if (nitem) {
                    nitem.setValue(inString);
                }
	},
        getToNumber:function(idnum)
	{
		var nitem = Ext.getCmp('nafldToNum' + idnum);
                if (nitem) {
                    return nitem.getValue();
                }
                return null;
	},
        
        getConnections:function()
	{
		var nitem = Ext.getCmp('nafldConnections');
                if (nitem) {
                    return nitem.getValue();
                }
                else return null;
	},
        setConnections:function(inString)
	{
		var nitem = Ext.getCmp('nafldConnections');
                if (nitem) {
                    nitem.setValue(inString);
                }
	},
        //nafldNumEvents
        
        setNumEvents:function(inString, idnum)
	{
		var nitem = Ext.getCmp('nafldNumEvents' + idnum);
                if (nitem) {
                    nitem.setValue(inString);
                }
	}
});